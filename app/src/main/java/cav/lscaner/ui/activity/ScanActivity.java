package cav.lscaner.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import cav.lscaner.R;
import cav.lscaner.data.managers.DataManager;
import cav.lscaner.data.models.ScannedDataModel;
import cav.lscaner.data.models.StoreProductModel;
import cav.lscaner.ui.adapter.ScannedListAdapter;
import cav.lscaner.ui.dialogs.QueryQuantityDialog;
import cav.lscaner.ui.dialogs.SelectScanDialog;
import cav.lscaner.utils.ConstantManager;

public class ScanActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener{
    private EditText mBarCode;
    private ListView mListView;

    private DataManager mDataManager;

    private int idFile = -1;

    private ScannedListAdapter mAdapter;
    private ArrayList<ScannedDataModel> mDataModels;

    private ArrayList<String> prefixScale;
    private int sizeScale = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        mDataManager = DataManager.getInstance();

        prefixScale = mDataManager.getPreferensManager().getScalePrefix();
        sizeScale = mDataManager.getPreferensManager().getSizeScale();

        idFile = getIntent().getIntExtra(ConstantManager.SELECTED_FILE,-1);

        mBarCode = (EditText) findViewById(R.id.barcode_et);

        mListView = (ListView) findViewById(R.id.san_lv);

        mBarCode.setOnEditorActionListener(mEditorActionListener);

        mListView.setOnItemLongClickListener(this);

        setupToolBar();
        updateUI();

    }

    public void setupToolBar(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
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
        mDataModels = mDataManager.getScannedData(idFile);
        if (mAdapter == null) {
            mAdapter = new ScannedListAdapter(this,R.layout.scanned_item,mDataModels);
            mListView.setAdapter(mAdapter);
        }else {
            mAdapter.setData(mDataModels);
            mAdapter.notifyDataSetChanged();
        }
    }

    private String mBar;
    private Float qq;

    TextView.OnEditorActionListener mEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            Log.d("SA",textView.getText().toString());
            mBar = textView.getText().toString();
            qq = 1f;
            // выкидываем EAN 8 так как его весовым у нас быть не может
            if (prefixScale.contains(mBar.substring(0,2)) && mBar.length() == 13){
                Log.d("SA","SCALE KODE");
                String lq = mBar.substring(sizeScale,mBar.length()-1);
                lq = lq.substring(0,2)+"."+lq.substring(3);
                mBar = mBar.substring(0,sizeScale);
                qq = Float.parseFloat(lq);
            }

            int l = mDataModels.indexOf(new ScannedDataModel(-1,-1,mBar,"", 0.0f));
            if (l == -1) {
                // нифига не нашли в уже добавленых смотрим в базе
                StoreProductModel product = mDataManager.getDB().searchStore(mBar);
                if (product == null) {
                    product = new StoreProductModel(mBar,"Новый");
                }
                QueryQuantityDialog dialod = QueryQuantityDialog.newInstans(product.getName(),qq,0f);
                dialod.setQuantityChangeListener(mQuantityChangeListener);
                dialod.show(getSupportFragmentManager(),"QQ");
            } else {
                Float qq = mDataModels.get(l).getQuantity();
                QueryQuantityDialog dialod = QueryQuantityDialog.newInstans(mDataModels.get(l).getName(),qq,qq);
                dialod.setQuantityChangeListener(mQuantityChangeListener);
                dialod.show(getSupportFragmentManager(),"QQ");
            }
            mBarCode.setText("");
            return false;
        }
    };

    QueryQuantityDialog.QuantityChangeListener mQuantityChangeListener = new QueryQuantityDialog.QuantityChangeListener(){
        @Override
        public void changeQuantity(Float quantity) {
            if (quantity!=0){
                mDataManager.getDB().addScannedPositon(idFile,mBar,quantity);
                updateUI(); // TODO передалать заполнение через добавление в адаптер
            }

        }
    };

    private int selectId;

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        ScannedDataModel model = (ScannedDataModel) adapterView.getItemAtPosition(position);
        selectId = model.getPosId();
        SelectScanDialog dialog = new SelectScanDialog();
        dialog.setOnSelectScanDialogListener(mScanDialogListener);
        dialog.show(getSupportFragmentManager(),"SD");
        return true;
    }

    SelectScanDialog.SelectScanDialogListener mScanDialogListener = new SelectScanDialog.SelectScanDialogListener() {
        @Override
        public void selectedItem(int item) {
            if (item == R.id.ss_dialog_del_item){
                deleteRecord(idFile,selectId);
            }

            if (item == R.id.ss_dialog_edit_item){

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
                        //TODO добавить удаление файла выгрузки с SD
                        mDataManager.getDB().delScannedPosition(selIdFile,position);
                        updateUI();
                    }
                })
                .setNegativeButton(R.string.button_cancel,null)
                .create();
        builder.show();
    }

}
