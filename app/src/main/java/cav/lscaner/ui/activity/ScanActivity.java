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
import android.util.Log;
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
import java.util.logging.Filter;

import cav.lscaner.R;
import cav.lscaner.data.managers.DataManager;
import cav.lscaner.data.models.ScannedDataModel;
import cav.lscaner.data.models.StoreProductModel;
import cav.lscaner.ui.adapter.ScannedListAdapter;
import cav.lscaner.ui.dialogs.DemoDialog;
import cav.lscaner.ui.dialogs.InfoNoValidDialog;
import cav.lscaner.ui.dialogs.QueryQuantityDialog;
import cav.lscaner.ui.dialogs.SelectItemsDialog;
import cav.lscaner.ui.dialogs.SelectScanDialog;
import cav.lscaner.utils.ConstantManager;
import cav.lscaner.utils.Func;

public class ScanActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener{
    private final int MAX_REC = 10;  // количество записей в демо версии

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        mDataManager = DataManager.getInstance();

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
            mAdapter = new ScannedListAdapter(this,R.layout.scanned_item,mDataModels);
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

    private String mBar;
    private String mArticul;
    private Float qq;
    private int posID;
    private boolean scaleFlg = false;

    TextView.OnEditorActionListener mEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                    || actionId == EditorInfo.IME_ACTION_DONE){
                //Log.d("SA KEY", "EVENT KEY ");

                if (demo && countRecord >=10 ) {
                    new DemoDialog().show(getSupportFragmentManager(),"DEMO");
                    return false;
                }

               // Log.d("SA",textView.getText().toString());
                mBar = textView.getText().toString();
                if (mBar.length() == 0) return true;
                qq = 1f;
                posID = -1;
                scaleFlg = false;
                editRecord = false;

                if (fileType == ConstantManager.FILE_TYPE_EGAIS){
                    if (mBar.startsWith("1") || mBar.length() < 14) {
                        // марка ФСМ
                        mBarCode.setText("");
                        return false;
                    }
                    mBar = Func.toEGAISAlcoCode(mBar);
                } else {
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
                        return false;
                    }
                    // получили UPC-A сконвертированный в EAN
                    if (mBar.startsWith("0") && mBar.length() == 13) {
                        if (! mUPCtoEAN) {
                            mBar = mBar.substring(1, 13);
                        }
                    }
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
                } else {
                    if (productArray.size() ==1 ) {
                        product = new StoreProductModel(mBar,productArray.get(0).getName(),productArray.get(0).getArticul(),
                                productArray.get(0).getPrice(),productArray.get(0).getOstatok());
                    } else {
                        SelectItemsDialog dialog = SelectItemsDialog.newInstance(productArray);
                        dialog.setOnSelectItemsChangeListener(mOnSelectItemsChangeListener);
                        dialog.show(getSupportFragmentManager(),"SI");
                        mBarCode.setText("");
                        return false;
                    }
                }

                int l = mDataModels.indexOf(new ScannedDataModel(mBar,product.getArticul()));
                if (l == -1) {
                    showQuantityQuery(product);
                } else {
                    showExistsQQ(product,l);
                }

                mBarCode.setText("");
            }
            return false;
        }
    };

    private void showExistsQQ(StoreProductModel product,int l) {
        if (!scaleFlg) {
            Float qq = mDataModels.get(l).getQuantity();
            mArticul = mDataModels.get(l).getArticul();
            posID = mDataModels.get(l).getPosId();
            QueryQuantityDialog dialod = QueryQuantityDialog.newInstans(mDataModels.get(l).getName(), 0f, qq, editRecord,
                    mDataModels.get(l).getOstatok(),mDataModels.get(l).getPrice());
            dialod.setQuantityChangeListener(mQuantityChangeListener);
            dialod.show(getSupportFragmentManager(), "QQ");
        } else {
            Float oldqq = mDataModels.get(l).getQuantity();
            mArticul = mDataModels.get(l).getArticul();
            posID = mDataModels.get(l).getPosId();
            qq = qq+oldqq;
            qq = Func.round(qq,3);
            mDataManager.getDB().addScannedPositon(idFile, mBar, qq,posID,mArticul);
            countRecord +=1;
            updateUI();
        }
    }

    // показываем окно или же добавляем новую запсиь если код весовой
    private void showQuantityQuery(StoreProductModel product){
        if (!scaleFlg) {
            mArticul = product.getArticul();
            QueryQuantityDialog dialod = QueryQuantityDialog.newInstans(product.getName(), 0f, 0f,
                    editRecord,product.getOstatok(),product.getPrice());
            dialod.setQuantityChangeListener(mQuantityChangeListener);
            dialod.show(getSupportFragmentManager(), "QQ");
        } else {
            mDataManager.getDB().addScannedPositon(idFile, mBar, qq,-1,product.getArticul());
            countRecord +=1;
            updateUI(); // TODO передалать заполнение через добавление в адаптер
        }
    }

    QueryQuantityDialog.QuantityChangeListener mQuantityChangeListener = new QueryQuantityDialog.QuantityChangeListener(){
        @Override
        public void changeQuantity(Float quantity) {
            if (quantity!=0){
                mDataManager.getDB().addScannedPositon(idFile,mBar,quantity,posID,mArticul);
                if (!editRecord) countRecord += 1;
                updateUI(); // TODO передалать заполнение через добавление в адаптер
                if (filterLock) {
                    mAdapter.getFilter().filter(filterString);
                    mAdapter.notifyDataSetChanged();
                }
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
                QueryQuantityDialog dialog = QueryQuantityDialog.newInstans(selModel.getName(),
                        selModel.getQuantity(),
                        selModel.getQuantity(),editRecord,
                        selModel.getOstatok(),selModel.getPrice());
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
