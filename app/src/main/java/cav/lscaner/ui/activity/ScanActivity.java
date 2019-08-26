package cav.lscaner.ui.activity;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.HybridBinarizer;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cav.lscaner.R;
import cav.lscaner.data.managers.DataManager;
import cav.lscaner.data.models.ScannedDataModel;
import cav.lscaner.data.models.StoreProductModel;
import cav.lscaner.ui.adapter.ScannedListAdapter;
import cav.lscaner.ui.adapter.ScannedSwipeListAdapter;
import cav.lscaner.ui.dialogs.DemoDialog;
import cav.lscaner.ui.dialogs.InfoNoValidDialog;
import cav.lscaner.ui.dialogs.PrihodChangePriceDialog;
import cav.lscaner.ui.dialogs.QueryQuantityDialog;
import cav.lscaner.ui.dialogs.SelectItemsDialog;
import cav.lscaner.ui.dialogs.SelectScanDialog;
import cav.lscaner.utils.CustomCameraPreview;
import cav.lscaner.utils.CameraUtils;
import cav.lscaner.utils.ConstantManager;
import cav.lscaner.utils.Func;
import cav.lscaner.utils.SwipeDetector;

public class ScanActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener,AdapterView.OnItemClickListener {

    private static final int CAMERA_D = 0;
    private static final String TAG = "SA";
    private final int MAX_REC = 30;  // количество записей в демо версии

    private EditText mBarCode;
    private SwipeMenuListView mListView;
    private TextView mSumma;


    private CompoundBarcodeView mBarcodeView;


    private DataManager mDataManager;

    private int idFile = -1;

    //private ScannedListAdapter mAdapter;
    private ScannedSwipeListAdapter mAdapter;

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
    private boolean preview = false;


    private Button mStartScan;


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

        mBarcodeView = (CompoundBarcodeView) findViewById(R.id.barcode_scan_v);
        mBarcodeView.setStatusText("");

        mStartScan = (Button) findViewById(R.id.barcode_scanner_bt);
        mStartScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!preview) {
                    startCamera();
                }
            }
        });
        mStartScan.setVisibility(View.INVISIBLE);



        mBarCode = (EditText) findViewById(R.id.barcode_et);
        mSumma = (TextView) findViewById(R.id.scan_summ);

        mListView = findViewById(R.id.san_lv);

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
                    new InputFilter.LengthFilter(155)});
        }

        if (fileType == ConstantManager.FILE_TYPE_PRIHOD) {
            mSumma.setVisibility(View.VISIBLE);
        }

        // хз
        mListView.setOnFocusChangeListener(mOnFocusChangeListener);


        swipeDetector = new SwipeDetector();
        //mListView.setOnTouchListener(swipeDetector); // свайп


        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                openItem.setBackground(R.drawable.swipe_button_bg_edit);
                // set item width
                openItem.setWidth(dp2px(90));
                // set item title
                //openItem.setTitle("Open");
                openItem.setIcon(R.drawable.ic_mode_edit_blue_24dp);
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                // set item background
                //deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                //deleteItem.setBackground(R.drawable.button_orange_border);
                deleteItem.setBackground(R.drawable.swipe_button_bg_edit);
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete_red_24dp);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        mListView.setMenuCreator(creator);
        mListView.setOnMenuItemClickListener(mMenuItemClickListener);


        setupToolBar();
        updateUI();

    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
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
                Log.d(TAG,query);
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
                releaceCamera();
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
        releaceCamera();
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
            mAdapter = new ScannedSwipeListAdapter(this,linkLayout,mDataModels);
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


    @SuppressWarnings("MissingPermission")
    private void iniCamera() throws IOException {
        mBarcodeView.decodeContinuous(callback);
        mBarcodeView.resume();
        preview = true;
    }

    private void releaceCamera(){
        mBarcodeView.pause();
        preview = false;
    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaceCamera();
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
                return workingBarcode(textView);
            }
            return false;
        }
    };

    private int selectPosition;

    SwipeMenuListView.OnMenuItemClickListener mMenuItemClickListener = new SwipeMenuListView.OnMenuItemClickListener(){

        @Override
        public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
            selectPosition = position;
            selModel = (ScannedDataModel) mAdapter.getItem(position);
            switch (index){
                case 0:
                    editRec();
                    break;
                case 1:
                    deleteRec();
                    break;

            }
            return false;
        }
    };

    private void startCamera(){
        try {
            iniCamera();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mStartScan.setVisibility(View.INVISIBLE);
    }


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
            if (/*mBar.startsWith("1") || */ mBar.length() < 14) {
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

        int l = mDataModels.indexOf(new ScannedDataModel(mBar,product.getArticul(),product.getName()));
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
            startCamera();
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
                //setDetector();
                startCamera();
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
                //setDetector();
                startCamera();
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

                if (filterLock) {
                    Log.d(TAG,"SELECT POS :"+selectPosition);
                    ScannedDataModel ll = (ScannedDataModel) mAdapter.getItem(selectPosition);
                    ll.setQuantity(quantity);
                    mAdapter.setOneItem(selectPosition,ll);
                    mAdapter.getFilter().filter(filterString);
                    mAdapter.notifyDataSetChanged();
                }
                updateUI();

                mBarCode.setText("");                // если камера отрыта то запускаем детектор по новой
                if (frameScanVisible) {
                    startCamera();
                }
                mBarCode.requestFocus();
            }
        }

        @Override
        public void cancelButton() {
            mBarCode.requestFocus();
            // если камера отрыта то запускаем детектор по новой
            if (frameScanVisible) {
                startCamera();
            }
        }
    };

    SelectItemsDialog.OnSelectItemsChangeListener mOnSelectItemsChangeListener = new SelectItemsDialog.OnSelectItemsChangeListener() {
        @Override
        public void onSelectItem(StoreProductModel product) {
            //showQuantityQuery(product);
            int l = mDataModels.indexOf(new ScannedDataModel(mBar,product.getArticul(),product.getName()));
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
            QueryQuantityDialog dialog = QueryQuantityDialog.newInstans(new StoreProductModel(selModel.getBarCode(),
                            selModel.getName(), selModel.getArticul()),
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
    }

    private void deleteRecord(final int selIdFile, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Удаляем?")
                .setMessage("Вы уверены?")
                .setPositiveButton(R.string.button_yes,new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int witch) {
                        if (filterLock) {
                                    mAdapter.remove(selModel);
                            mAdapter.getFilter().filter(filterString);
                            mAdapter.notifyDataSetChanged();
                        }

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
                deleteRec();
            }
            if (swipeDetector.getAction() == SwipeDetector.Action.RL) {
                Log.d("SA","RIGTH");
                editRec();
            }
            return;
        }
    }

    private BarcodeCallback callback = new BarcodeCallback() {

        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                Log.d("M2A",result.getText());
                mBarCode.setText(result.getText());
                mStartScan.setVisibility(View.VISIBLE);
                Func.playMessage(ScanActivity.this);
                releaceCamera();
                workingBarcode(mBarCode);
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };

}
