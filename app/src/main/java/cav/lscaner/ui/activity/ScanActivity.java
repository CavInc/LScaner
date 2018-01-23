package cav.lscaner.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cav.lscaner.R;
import cav.lscaner.data.managers.DataManager;
import cav.lscaner.data.models.ScannedDataModel;
import cav.lscaner.data.models.StoreProductModel;
import cav.lscaner.ui.adapter.ScannedListAdapter;
import cav.lscaner.ui.dialogs.DemoDialog;
import cav.lscaner.ui.dialogs.InfoNoValidDialog;
import cav.lscaner.ui.dialogs.PrihodChangePriceDialog;
import cav.lscaner.ui.dialogs.QueryQuantityDialog;
import cav.lscaner.ui.dialogs.SelectItemsDialog;
import cav.lscaner.ui.dialogs.SelectScanDialog;
import cav.lscaner.utils.CameraUtils;
import cav.lscaner.utils.ConstantManager;
import cav.lscaner.utils.CustomBarcodeDetector;
import cav.lscaner.utils.Func;
import cav.lscaner.utils.SwipeDetector;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class ScanActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener,AdapterView.OnItemClickListener {

    private final int MAX_REC = 30;  // количество записей в демо версии

    private EditText mBarCode;
    private ListView mListView;
    private TextView mSumma;

    private BarcodeDetector barcodeDetector;
    private SurfaceView cameraView;
    private CameraSource cameraSource;


    private DataManager mDataManager;

    private int idFile = -1;

    private ScannedListAdapter mAdapter;
    private ArrayList<ScannedDataModel> mDataModels;
    private FrameLayout mFrameLayout;

    private ArrayList<String> prefixScale;
    private int sizeScale = -1;

    private String mFileName;

    private boolean editRecord = false;
    private boolean demo = true;
    private int countRecord = 0;
    private int fileType = 0;

    private boolean mUPCtoEAN = false;

    private boolean filterLock = false;

    private SwipeDetector swipeDetector;

    private boolean frameScanVisible = false;



    private String debugOutFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        mDataManager = DataManager.getInstance();

        //debugOutFile = mDataManager.getStorageAppPath() + "/log_file.log"; //debug

        prefixScale = mDataManager.getPreferensManager().getScalePrefix();
        sizeScale = mDataManager.getPreferensManager().getSizeScale();

        mUPCtoEAN = mDataManager.getPreferensManager().getSharedPreferences().getBoolean("upc_to_ean",true);

        idFile = getIntent().getIntExtra(ConstantManager.SELECTED_FILE,-1);
        mFileName = getIntent().getStringExtra(ConstantManager.SELECTED_FILE_NAME);
        fileType = getIntent().getIntExtra(ConstantManager.SELECTED_FILE_TYPE,ConstantManager.FILE_TYPE_PRODUCT);

         // окно для отображения
        mFrameLayout = (FrameLayout) findViewById(R.id.barcode_frame);
        cameraView = (SurfaceView) findViewById(R.id.barcode_scan_v);




        mBarCode = (EditText) findViewById(R.id.barcode_et);
        mSumma = (TextView) findViewById(R.id.scan_summ);

        mListView = (ListView) findViewById(R.id.san_lv);

        mBarCode.setOnEditorActionListener(mEditorActionListener);

        mListView.setOnItemLongClickListener(this);
        mListView.setOnItemClickListener(this);

        demo = mDataManager.getPreferensManager().getDemo();
        if (demo) {
            countRecord = mDataManager.getDB().getCountRecInFile(idFile);
        }

        if (fileType == ConstantManager.FILE_TYPE_EGAIS || fileType == ConstantManager.FILE_TYPE_ALCOMARK) {
            mBarCode.setInputType(InputType.TYPE_CLASS_TEXT);
            mBarCode.setFilters(new InputFilter[] {
                    new InputFilter.LengthFilter(68)});
        }

        if (fileType == ConstantManager.FILE_TYPE_PRIHOD) {
            mSumma.setVisibility(View.VISIBLE);
        }

        // хз
        mListView.setOnFocusChangeListener(mOnFocusChangeListener);


        swipeDetector = new SwipeDetector();
        mListView.setOnTouchListener(swipeDetector);

        setupToolBar();
        updateUI();

    }

    public void setupToolBar(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(mFileName);
        }
    }

    private MenuItem searchItem;

    private String filterString = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scan_menu, menu);
        searchItem = menu.findItem(R.id.scan_menu_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        if(null!=searchManager ) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                return  false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() == 0){
                    mAdapter = null;
                    filterLock = false;
                    updateUI();
                } else {
                    filterString = newText;
                    filterLock = true;
                    mAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem,new MenuItemCompat.OnActionExpandListener(){
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                mAdapter.notifyDataSetChanged();
                return true;
            }
        });

        CameraUtils cameraUtils = new CameraUtils();
        if (!cameraUtils.isCameraExists()) {
            MenuItem item = menu.findItem(R.id.scan_menu_photo);
            item.setEnabled(false);
        } else {
            // запрос разрешения на камеру
           // getPermissionCamera();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        if (item.getItemId() == R.id.scan_menu_photo) {
            if (frameScanVisible) {
                // гасим камеру и скрываем окно
                closeCamera();
                mFrameLayout.setVisibility(View.GONE);
                item.setIcon(R.drawable.ic_local_see_white_24dp);
            } else {
                // включаем камеру и открываем окно
                mFrameLayout.setVisibility(View.VISIBLE);
                item.setIcon(R.drawable.ic_photo_camera_green_24dp);

                try {
                    iniCamera();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            frameScanVisible = !frameScanVisible;
        }
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeCamera();
    }

    private void updateUI(){
        mDataModels = mDataManager.getScannedData(idFile,fileType);
        if (mAdapter == null) {
            int linkLayout = R.layout.scanned_item;
            if (fileType == ConstantManager.FILE_TYPE_CHANGE_PRICE) {
                linkLayout = R.layout.change_price_item;
            } else if (fileType == ConstantManager.FILE_TYPE_ALCOMARK) {
                linkLayout = R.layout.scanned_alko_item;
            }
            mAdapter = new ScannedListAdapter(this,linkLayout,mDataModels);
            mListView.setAdapter(mAdapter);
        }else {
            mAdapter.setData(mDataModels);
            mAdapter.notifyDataSetChanged();
        }
        if (!editRecord) {
            mListView.setSelection(0);
        }
        mBarCode.requestFocus();
        if (fileType == ConstantManager.FILE_TYPE_PRIHOD) {
            Double res = mDataManager.getDB().getSumInFile(idFile);
            mSumma.setText("Сумма : "+Func.roundUp(res,2));
        }
    }

    private HolderCallback mHolderCallback;

    @SuppressWarnings("MissingPermission")
    private void iniCamera() throws IOException {

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(400,280)
                .setAutoFocusEnabled(true)
                .build();
        mHolderCallback = new HolderCallback();
        cameraView.getHolder().addCallback(mHolderCallback);

        Log.d("SA"," SF "+cameraView.isShown());

        cameraSource.start(cameraView.getHolder());

        setDetector();
    }

    private void closeCamera(){
        if (cameraSource != null) {
            cameraView.getHolder().removeCallback(mHolderCallback);
            cameraSource.stop();
            cameraSource.release();
            barcodeDetector.release();
            cameraSource = null;
            barcodeDetector = null;
        }
    }

    private void setDetector(){
        CustomBarcodeDetector detector = new CustomBarcodeDetector();
        detector.setBarcodeDetectorCallback(mBarcodeDetectorCallback);
        barcodeDetector.setProcessor(detector);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private String mBar;
    private String mArticul;
    private Float qq;
    private int posID;
    private boolean scaleFlg = false;


    TextView.OnEditorActionListener mEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

            //Func.addLog(debugOutFile,"KEY EVENT  ac: "+actionId+" kv :"+keyEvent); // debug

            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (keyEvent.getAction() == KeyEvent.ACTION_DOWN
                            && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER
                            && keyEvent.getRepeatCount() == 0)) {
                // Log.d("SA KEY", "EVENT KEY ");
                //  Func.addLog(debugOutFile," обрабатываем ввод"); // debug
                /*
                InputDevice lxDev = keyEvent.getDevice();
                Log.d("SA "," KEY DES "+lxDev.getDescriptor()+" "+lxDev.getName());
                */
                return workingBarcode(textView);
            }
            return false;
        }
    };


    // обрабатываем полученный штрихкод
    private boolean workingBarcode(TextView textView){
        if (demo && countRecord >=10 ) {
            new DemoDialog().show(getSupportFragmentManager(),"DEMO");
            return false;
        }

        mBar = textView.getText().toString();
        if (mBar.length() == 0) return true;
        qq = 1f;
        posID = -1;
        scaleFlg = false;
        editRecord = false;
        //Func.addLog(debugOutFile,"---------------------------------------"); // debug
        // Func.addLog(debugOutFile,"RAW Scanned code : "+mBar); // debug

        if (fileType == ConstantManager.FILE_TYPE_EGAIS || fileType == ConstantManager.FILE_TYPE_ALCOMARK){
            if (mBar.startsWith("1") || mBar.length() < 14) {
                // марка ФСМ
                //Func.addLog(debugOutFile,"Mark FSM : "+mBar); // debug
                mBarCode.setText("");
                return true;
            }
            if (fileType != ConstantManager.FILE_TYPE_ALCOMARK) {
                mBar = Func.toEGAISAlcoCode(mBar);
            }
        } else {
            if (mBar.length()<2) return true;
            // выкидываем EAN 8 так как его весовым у нас быть не может
            if (prefixScale.contains(mBar.substring(0,2)) && (mBar.length() == 13 || mBar.length() == sizeScale)){
                // Log.d("SA","SCALE KODE");
                scaleFlg = true;
                if (mBar.length() != sizeScale) {
                    String lq = mBar.substring(sizeScale, mBar.length() - 1);
                    lq = lq.substring(0, 2) + "." + lq.substring(2);
                    qq = Float.parseFloat(lq);
                } else {
                    scaleFlg = false;
                }
                mBar = mBar.substring(0,sizeScale);
            } else if (! mBar.startsWith("0") &&  !Func.checkEAN(mBar)) {
                // покажем онко что куй а не код
                InfoNoValidDialog dialog = new InfoNoValidDialog();
                dialog.show(getSupportFragmentManager(),"INFD");
                mBarCode.setText("");
                return false;
            }
            // получили UPC-A сконвертированный в EAN
            if (mBar.startsWith("0") && mBar.length() == 13) {
                if (! mUPCtoEAN) {
                    mBar = mBar.substring(1, 13);
                }
            }
            //Func.addLog(debugOutFile,"Scanned code : "+mBar); // debug
        }

        // ищем код и артикул в базе. (в основном артикул потому что иначе куй определим двойной код
        StoreProductModel product = null;
        ArrayList<StoreProductModel> productArray;
        if (fileType == ConstantManager.FILE_TYPE_EGAIS) {
            productArray = mDataManager.getDB().searchStoreEgaisArray(mBar);
        } else {
            productArray = mDataManager.getDB().searchStoreArray(mBar);
        }

        if (productArray == null || productArray.size() == 0){
            product = new StoreProductModel(mBar,"Новый");
            // Func.addLog(debugOutFile,"New product : "+mBar); // debug
        } else {
            if (productArray.size() == 1 ) {
                product = new StoreProductModel(mBar,productArray.get(0).getName(),productArray.get(0).getArticul(),
                        productArray.get(0).getPrice(),productArray.get(0).getOstatok(),
                        "",productArray.get(0).getBasePrice());
                // Func.addLog(debugOutFile,"Product : "+product.getArticul()+" :: "+product.getName()); // debug
            } else {
                SelectItemsDialog dialog = SelectItemsDialog.newInstance(productArray);
                dialog.setOnSelectItemsChangeListener(mOnSelectItemsChangeListener);
                dialog.show(getSupportFragmentManager(),"SI");
                //Func.addLog(debugOutFile,"Selected multiple product : "); // debug
                mBarCode.setText("");
                return true;
            }
        }

        int l = mDataModels.indexOf(new ScannedDataModel(mBar,product.getArticul()));
        if (l == -1) {
            // Func.addLog(debugOutFile,"New File pos : "+product.getArticul()+" :: "+product.getName()+" :: "+mBar); // debug
            if (fileType == ConstantManager.FILE_TYPE_ALCOMARK) {
                storeAlocomarkPosition(mBar);
                return true;
            } else {
                showQuantityQuery(product);
            }
        } else {
            //Func.addLog(debugOutFile,"Exst File pos : "+product.getArticul()+" :: "+product.getName()+" :: "+mBar); // debug
            if (fileType != ConstantManager.FILE_TYPE_ALCOMARK) {
                showExistsQQ(product, l);
            }
        }
        //Func.addLog(debugOutFile," CLEAR EDIT TEXT MAIN"); // debug
        mBarCode.setText("");
        return false;
    }

    // сохраняем новую алкомарку
    private void storeAlocomarkPosition(String alcomark){
        mDataManager.getDB().addScannedPositon(idFile,alcomark ,0.0f,-1,"");
        countRecord +=1;
        mBarCode.setText("");
        updateUI();
        // если камера отрыта то запускаем детектор по новой
        // по хорошему тут должна быть задержка
        if (frameScanVisible) {
            setDetector();
        }
    }

    // позицаия есть в базе
    private void showExistsQQ(StoreProductModel product, int l) {
        if (!scaleFlg) {
            Float qq = mDataModels.get(l).getQuantity();
            mArticul = mDataModels.get(l).getArticul();
            posID = mDataModels.get(l).getPosId();
           // Func.addLog(debugOutFile,"No Scale : "+mArticul+" :: "+mDataModels.get(l).getName()+" :: "+l); // debug
            if (fileType == ConstantManager.FILE_TYPE_CHANGE_PRICE || fileType == ConstantManager.FILE_TYPE_PRIHOD) {
               // editRecord = true;
                product.setPrice(mDataModels.get(l).getPrice());
                product.setQuantity(qq);
                PrihodChangePriceDialog dialog = PrihodChangePriceDialog.newInstance(product,fileType,editRecord);
                dialog.setPrihodChangePriceListener(mChangePriceListener);
                dialog.show(getFragmentManager(),"pcd");
            }else {
                QueryQuantityDialog dialog = QueryQuantityDialog.newInstans(product, 0f, qq, editRecord);
                dialog.setQuantityChangeListener(mQuantityChangeListener);
                dialog.show(getSupportFragmentManager(), "QQ");
            }
        } else {
            Float oldqq = mDataModels.get(l).getQuantity();
            mArticul = mDataModels.get(l).getArticul();
            posID = mDataModels.get(l).getPosId();
            qq = qq+oldqq;
            qq = Func.round(qq,3);
           // Func.addLog(debugOutFile,"Scale : "+mArticul+" :: "+mDataModels.get(l).getName()+" :: "+l); // debug
            mDataManager.getDB().addScannedPositon(idFile, mBar, qq,posID,mArticul);
            countRecord +=1;
            updateUI();
        }
    }

    // показываем окно или же добавляем новую запсиь если код весовой
    private void showQuantityQuery(StoreProductModel product){
        if (!scaleFlg) {
           // Func.addLog(debugOutFile,"No Scale : "+product.getArticul()+" :: "+product.getName()+" :: "+product.getBarcode()+" :: store "+mBar); // debug
            mArticul = product.getArticul();

            if (fileType == ConstantManager.FILE_TYPE_CHANGE_PRICE) {
                PrihodChangePriceDialog dialog = PrihodChangePriceDialog.newInstance(product,fileType,editRecord);
                dialog.setPrihodChangePriceListener(mChangePriceListener);
                dialog.show(getFragmentManager(),"pcd");
            } else if (fileType == ConstantManager.FILE_TYPE_PRIHOD) {
                PrihodChangePriceDialog dialog = PrihodChangePriceDialog.newInstance(product,fileType,editRecord);
                dialog.setPrihodChangePriceListener(mChangePriceListener);
                dialog.show(getFragmentManager(),"ppd");
            }else {
                QueryQuantityDialog dialog = QueryQuantityDialog.newInstans(product, 0f, 0f, editRecord);
                dialog.setQuantityChangeListener(mQuantityChangeListener);
                dialog.show(getSupportFragmentManager(), "QQ");
            }

        } else {
           // Func.addLog(debugOutFile,"Scale : "+product.getArticul()+" :: "+product.getName()); // debug
            mDataManager.getDB().addScannedPositon(idFile, mBar, qq,-1,product.getArticul());
            countRecord +=1;
            updateUI(); // TODO передалать заполнение через добавление в адаптер
        }
    }

    // получаем обратно данные от переоценке или приходе товара
    PrihodChangePriceDialog.PrihodChangePriceListener mChangePriceListener = new PrihodChangePriceDialog.PrihodChangePriceListener() {
        @Override
        public void cancelButton() {
            mBarCode.requestFocus();
            // если камера отрыта то запускаем детектор по новой
            if (frameScanVisible) {
                setDetector();
            }
        }

        @Override
        public void changeQuantity(StoreProductModel productModel) {
            if (fileType == ConstantManager.FILE_TYPE_CHANGE_PRICE){
                mDataManager.getDB().addScannedPricePosition(idFile,productModel,posID);
            } else {
                mDataManager.getDB().addScannedPrihodPosition(idFile,productModel,posID);
            }
            updateUI();
            mBarCode.setText("");
            mBarCode.requestFocus();

            // если камера отрыта то запускаем детектор по новой
            if (frameScanVisible) {
                setDetector();
            }
        }
    };

    // получаем обратно данные о количестве
    QueryQuantityDialog.QuantityChangeListener mQuantityChangeListener = new QueryQuantityDialog.QuantityChangeListener(){

        @Override
        public void changeQuantity(Float quantity, StoreProductModel productModel) {
            if (quantity!=0){
               // Func.addLog(debugOutFile,"Change QQ : "+mArticul+" :: "+mBar+" :: "+posID); // debug
               // Func.addLog(debugOutFile,"Change QQ : "+productModel.getArticul()+" :: "+productModel.getBarcode()+" :: "+posID+" :: IDFILE :"+idFile); // debug

                mDataManager.getDB().addScannedPositon(idFile,productModel.getBarcode(),quantity,posID,productModel.getArticul());
                if (!editRecord) countRecord += 1;
                updateUI(); // TODO передалать заполнение через добавление в адаптер
                if (filterLock) {
                    mAdapter.getFilter().filter(filterString);
                    mAdapter.notifyDataSetChanged();
                }
                mBarCode.setText("");                // если камера отрыта то запускаем детектор по новой
                if (frameScanVisible) {
                    setDetector();
                }
                mBarCode.requestFocus();
                // если камера отрыта то запускаем детектор по новой
                if (frameScanVisible) {
                    setDetector();
                }
            }
        }

        @Override
        public void cancelButton() {
            mBarCode.requestFocus();
            // если камера отрыта то запускаем детектор по новой
            if (frameScanVisible) {
                setDetector();
            }
        }
    };

    SelectItemsDialog.OnSelectItemsChangeListener mOnSelectItemsChangeListener = new SelectItemsDialog.OnSelectItemsChangeListener() {
        @Override
        public void onSelectItem(StoreProductModel product) {
            //showQuantityQuery(product);
            int l = mDataModels.indexOf(new ScannedDataModel(mBar,product.getArticul()));
            if (l == -1) {
                showQuantityQuery(product);
            } else {
                showExistsQQ(product, l);
            }
        }
    };

    private ScannedDataModel selModel;

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        selModel = (ScannedDataModel) adapterView.getItemAtPosition(position);
        SelectScanDialog dialog = new SelectScanDialog();
        dialog.setOnSelectScanDialogListener(mScanDialogListener);
        dialog.show(getSupportFragmentManager(),"SD");
        return true;
    }

    SelectScanDialog.SelectScanDialogListener mScanDialogListener = new SelectScanDialog.SelectScanDialogListener() {
        @Override
        public void selectedItem(int item) {
            if (item == R.id.ss_dialog_del_item){
                deleteRec();
            }

            if (item == R.id.ss_dialog_edit_item){
                editRec();
            }
        }
    };

    private void editRec() {
        if (fileType == ConstantManager.FILE_TYPE_ALCOMARK) return;

        editRecord = true;
        posID = selModel.getPosId();
        mBar = selModel.getBarCode();
        mArticul = selModel.getArticul();
        if (fileType == ConstantManager.FILE_TYPE_EGAIS || fileType == ConstantManager.FILE_TYPE_PRODUCT) {
            QueryQuantityDialog dialog = QueryQuantityDialog.newInstans(new StoreProductModel(selModel.getBarCode(), selModel.getName(), selModel.getArticul()),
                    selModel.getQuantity(), selModel.getQuantity(), editRecord);
            dialog.setQuantityChangeListener(mQuantityChangeListener);
            dialog.show(getSupportFragmentManager(), "EDITSD");
        } else if (fileType == ConstantManager.FILE_TYPE_CHANGE_PRICE){
            PrihodChangePriceDialog dialog = PrihodChangePriceDialog.newInstance(new StoreProductModel(selModel.getBarCode(),
                            selModel.getName(), selModel.getArticul(),selModel.getPrice(),selModel.getOstatok())
                    ,fileType,editRecord);
            dialog.setPrihodChangePriceListener(mChangePriceListener);
            dialog.show(getFragmentManager(),"pcd");
        } else {
            PrihodChangePriceDialog dialog = PrihodChangePriceDialog.newInstance(new StoreProductModel(selModel.getBarCode(),
                            selModel.getName(), selModel.getArticul(),selModel.getPrice(),selModel.getQuantity(),
                    selModel.getOstatok(),"",selModel.getBasePrice())
                    ,fileType,editRecord);
            dialog.setPrihodChangePriceListener(mChangePriceListener);
            dialog.show(getFragmentManager(),"ppd");
        }
    }

    private void deleteRec() {
        deleteRecord(idFile,selModel.getPosId());
        if (filterLock) {
            //android.widget.Filter fl = mAdapter.getFilter();
            mAdapter.remove(selModel);
            mAdapter.getFilter().filter(filterString);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void deleteRecord(final int selIdFile, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Удаление")
                .setMessage("Удаляем ? Вы уверены ?")
                .setPositiveButton(R.string.button_ok,new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int witch) {
                        mDataManager.getDB().delScannedPosition(selIdFile,position);
                        countRecord -=1;
                        updateUI();
                    }
                })
                .setNegativeButton(R.string.button_cancel,null)
                .create();
        builder.show();
    }

    View.OnFocusChangeListener mOnFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean status) {
           // Log.d("SA па","Change focus - "+status);
            mBarCode.requestFocus();
        }
    };

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (swipeDetector.swipeDetected()) {
            selModel = (ScannedDataModel) adapterView.getItemAtPosition(position);
            if (swipeDetector.getAction() == SwipeDetector.Action.LR) {
                Log.d("SA","LEFT");
                // удаляем
                //deleteRecord(mFileAdapter.getItem(position).getId());
                deleteRec();
            }
            if (swipeDetector.getAction() == SwipeDetector.Action.RL) {
                Log.d("SA","RIGTH");
                editRec();
            }
            return;
        }
    }

    CustomBarcodeDetector.BarcodeDetectorCallback mBarcodeDetectorCallback = new CustomBarcodeDetector.BarcodeDetectorCallback() {
        @Override
        public void OnBarcode(final String barcode) {
            Log.d("SA",barcode);
            //mBarCode.setText(barcode);
            mBarCode.post(new Runnable() {
                @Override
                public void run() {
                    mBarCode.setText(barcode);
                    Func.playMessage(ScanActivity.this);
                    workingBarcode(mBarCode);
                }
            });
            barcodeDetector.release();
        }
    };


    class HolderCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            Log.d("SA","CreateSurface");
            cameraSource.stop();

            try {
                //noinspection MissingPermission
                cameraSource.start(cameraView.getHolder());
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }

        @SuppressWarnings("MissingPermission")
        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
            Log.d("SA","SF CHANGE");
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            Log.d("SA","STOP CAMERA");
          //  if (cameraSource != null)  cameraSource.stop();
        }
    }

    /*
    class BarcodeDetectorCallback implements Detector.Processor {

        @Override
        public void release() {

        }

        @Override
        public void receiveDetections(Detector.Detections detections) {
            final SparseArray barcodes = detections.getDetectedItems();
            if (barcodes.size() != 0) {
                Log.d("SA",((Barcode) barcodes.valueAt(0)).displayValue);
                mBarCode.post(new Runnable() {
                    @Override
                    public void run() {
                        mBarCode.setText(((Barcode) barcodes.valueAt(0)).displayValue);
                    }
                });
            }
        }
    }
    */
}
