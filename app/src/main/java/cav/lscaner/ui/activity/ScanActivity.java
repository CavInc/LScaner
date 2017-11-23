package cav.lscaner.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        mDataManager = DataManager.getInstance();

        prefixScale = mDataManager.getPreferensManager().getScalePrefix();
        sizeScale = mDataManager.getPreferensManager().getSizeScale();

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
                    if (prefixScale.contains(mBar.substring(0,2)) && mBar.length() == 13){
                        // Log.d("SA","SCALE KODE");
                        String lq = mBar.substring(sizeScale,mBar.length()-1);
                        lq = lq.substring(0,2)+"."+lq.substring(2);
                        mBar = mBar.substring(0,sizeScale);
                        qq = Float.parseFloat(lq);
                        scaleFlg = true;
                    }
                }

                int l = mDataModels.indexOf(new ScannedDataModel(-1,-1,mBar,"", 0.0f));
                if (l == -1) {
                    // нифига не нашли в уже добавленых смотрим в базе
                    StoreProductModel product = null;
                    ArrayList<StoreProductModel> productArray;
                    if (fileType == ConstantManager.FILE_TYPE_EGAIS) {
                        //product = mDataManager.getDB().searchStoreEgais(mBar);
                        productArray = mDataManager.getDB().searchStoreEgaisArray(mBar);
                    } else {
                        //product = mDataManager.getDB().searchStore(mBar);
                        productArray = mDataManager.getDB().searchStoreArray(mBar);
                    }

                    /*
                    if (product == null) {
                        product = new StoreProductModel(mBar,"Новый");
                    }
                    */
                    if (productArray == null || productArray.size() == 0){
                        product = new StoreProductModel(mBar,"Новый");
                    } else {
                        if (productArray.size() ==1 ) {
                            product = new StoreProductModel(mBar,productArray.get(0).getName(),productArray.get(0).getArticul());
                        } else {
                            SelectItemsDialog dialog = SelectItemsDialog.newInstance(productArray);
                            dialog.setOnSelectItemsChangeListener(mOnSelectItemsChangeListener);
                            dialog.show(getSupportFragmentManager(),"SI");
                            mBarCode.setText("");
                            return false;
                        }
                    }

                    // если здесь не одна запись то а) показать а еще одно окно или же выбор в количестве.

                    showQuantityQuery(product);

                    /*
                    if (!scaleFlg) {
                        QueryQuantityDialog dialod = QueryQuantityDialog.newInstans(product.getName(), 0f, 0f,editRecord);
                        dialod.setQuantityChangeListener(mQuantityChangeListener);
                        dialod.show(getSupportFragmentManager(), "QQ");
                    } else {
                        mDataManager.getDB().addScannedPositon(idFile, mBar, qq,-1);
                        countRecord +=1;
                        updateUI(); // TODO передалать заполнение через добавление в адаптер
                    }
                    */

                } else {
                    if (!scaleFlg) {
                        Float qq = mDataModels.get(l).getQuantity();
                        mArticul = mDataModels.get(l).getArticul();
                        posID = mDataModels.get(l).getPosId();
                        QueryQuantityDialog dialod = QueryQuantityDialog.newInstans(mDataModels.get(l).getName(), 0f, qq, editRecord);
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
                        updateUI(); // TODO передалать заполнение через добавление в адаптер
                    }
                }
                mBarCode.setText("");
            }
            return false;
        }
    };

    // показываем окно или же добавляем новую запсиь если код весовой
    private void showQuantityQuery(StoreProductModel product){
        if (!scaleFlg) {
            mArticul = product.getArticul();
            QueryQuantityDialog dialod = QueryQuantityDialog.newInstans(product.getName(), 0f, 0f,editRecord);
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
            showQuantityQuery(product);
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
            }

            if (item == R.id.ss_dialog_edit_item){
                editRecord = true;
                posID = selModel.getPosId();
                mBar = selModel.getBarCode();
                QueryQuantityDialog dialog = QueryQuantityDialog.newInstans(selModel.getName(),
                        selModel.getQuantity(),
                        selModel.getQuantity(),editRecord);
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
