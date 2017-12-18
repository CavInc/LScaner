package cav.lscaner.ui.activity;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.InputFilter;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

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
import cav.lscaner.utils.ConstantManager;
import cav.lscaner.utils.Func;

public class ScanActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener{
    private final int MAX_REC = 30;  // количество записей в демо версии

    private EditText mBarCode;
    private ListView mListView;

    private DataManager mDataManager;

    private int idFile = -1;

    private ScannedListAdapter mAdapter;
    private ArrayList<ScannedDataModel> mDataModels;

    private ArrayList<String> prefixScale;
    private int sizeScale = -1;

    private String mFileName;

    private boolean editRecord = false;
    private boolean demo = true;
    private int countRecord = 0;
    private int fileType = 0;

    private boolean mUPCtoEAN = false;

    private boolean filterLock = false;

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

        mBarCode = (EditText) findViewById(R.id.barcode_et);

        mListView = (ListView) findViewById(R.id.san_lv);

        mBarCode.setOnEditorActionListener(mEditorActionListener);

        mListView.setOnItemLongClickListener(this);

        demo = mDataManager.getPreferensManager().getDemo();
        if (demo) {
            countRecord = mDataManager.getDB().getCountRecInFile(idFile);
        }

        if (fileType == ConstantManager.FILE_TYPE_EGAIS) {
            mBarCode.setInputType(InputType.TYPE_CLASS_TEXT);
            mBarCode.setFilters(new InputFilter[] {
                    new InputFilter.LengthFilter(68)});
        }

        // хз
        mListView.setOnFocusChangeListener(mOnFocusChangeListener);

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


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    private void updateUI(){
        mDataModels = mDataManager.getScannedData(idFile,fileType);
        if (mAdapter == null) {
            int linkLayout = R.layout.scanned_item;
            if (fileType == ConstantManager.FILE_TYPE_CHANGE_PRICE) {
                linkLayout = R.layout.change_price_item;
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
    }

    @Override
    protected void onResume() {
        super.onResume();
       // Func.addLog(debugOutFile,"FORM RESUME : "); // debug
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
                            && keyEvent.getRepeatCount() == 0)){
               // Log.d("SA KEY", "EVENT KEY ");
              //  Func.addLog(debugOutFile," обрабатываем ввод"); // debug
                /*
                InputDevice lxDev = keyEvent.getDevice();
                Log.d("SA "," KEY DES "+lxDev.getDescriptor()+" "+lxDev.getName());
                */

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

                if (fileType == ConstantManager.FILE_TYPE_EGAIS){
                    if (mBar.startsWith("1") || mBar.length() < 14) {
                        // марка ФСМ
                        //Func.addLog(debugOutFile,"Mark FSM : "+mBar); // debug
                        mBarCode.setText("");
                        return true;
                    }
                    mBar = Func.toEGAISAlcoCode(mBar);
                    //Func.addLog(debugOutFile,"EGAIS code : "+mBar); // debug
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
                    if (productArray.size() ==1 ) {
                        product = new StoreProductModel(mBar,productArray.get(0).getName(),productArray.get(0).getArticul(),
                                productArray.get(0).getPrice(),productArray.get(0).getOstatok());
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
                    showQuantityQuery(product);
                } else {
                    //Func.addLog(debugOutFile,"Exst File pos : "+product.getArticul()+" :: "+product.getName()+" :: "+mBar); // debug
                    showExistsQQ(product,l);
                }
                //Func.addLog(debugOutFile," CLEAR EDIT TEXT MAIN"); // debug
                mBarCode.setText("");
            }
            return false;
        }
    };

    private void showExistsQQ(StoreProductModel product, int l) {
        if (!scaleFlg) {
            Float qq = mDataModels.get(l).getQuantity();
            mArticul = mDataModels.get(l).getArticul();
            posID = mDataModels.get(l).getPosId();
           // Func.addLog(debugOutFile,"No Scale : "+mArticul+" :: "+mDataModels.get(l).getName()+" :: "+l); // debug
            QueryQuantityDialog dialog = QueryQuantityDialog.newInstans(product,0f,qq,editRecord);
            dialog.setQuantityChangeListener(mQuantityChangeListener);
            dialog.show(getSupportFragmentManager(), "QQ");
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
        }

        @Override
        public void changeQuantity(StoreProductModel productModel) {
            if (fileType == ConstantManager.FILE_TYPE_CHANGE_PRICE){
                mDataManager.getDB().addScannedPricePosition(idFile,productModel,posID);
            }
            updateUI();
            mBarCode.setText("");
            mBarCode.requestFocus();
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
                mBarCode.setText("");
                mBarCode.requestFocus();
            }
        }

        @Override
        public void cancelButton() {
            mBarCode.requestFocus();
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
                deleteRecord(idFile,selModel.getPosId());
                if (filterLock) {
                    //android.widget.Filter fl = mAdapter.getFilter();
                    mAdapter.remove(selModel);
                    mAdapter.getFilter().filter(filterString);
                    mAdapter.notifyDataSetChanged();
                }
            }

            if (item == R.id.ss_dialog_edit_item){
                editRecord = true;
                posID = selModel.getPosId();
                mBar = selModel.getBarCode();
                mArticul = selModel.getArticul();
                /*
                QueryQuantityDialog dialog = QueryQuantityDialog.newInstans(selModel.getName(),
                        selModel.getQuantity(),
                        selModel.getQuantity(),editRecord,
                        selModel.getOstatok(),selModel.getPrice());
                        */
                QueryQuantityDialog dialog = QueryQuantityDialog.newInstans(new StoreProductModel(selModel.getBarCode(),selModel.getName(),selModel.getArticul()),
                        selModel.getQuantity(),selModel.getQuantity(),editRecord);
                dialog.setQuantityChangeListener(mQuantityChangeListener);
                dialog.show(getSupportFragmentManager(),"EDITSD");
            }
        }
    };

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

}
