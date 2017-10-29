package cav.lscaner.ui.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
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
import cav.lscaner.utils.ConstantManager;

public class ScanActivity extends AppCompatActivity {
    private EditText mBarCode;
    private ListView mListView;

    private DataManager mDataManager;

    private int idFile = -1;

    private ScannedListAdapter mAdapter;
    private ArrayList<ScannedDataModel> mDataModels;

    private ArrayList<String> prefixScale;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        mDataManager = DataManager.getInstance();

        prefixScale = mDataManager.getPreferensManager().getScalePrefix();

        idFile = getIntent().getIntExtra(ConstantManager.SELECTED_FILE,-1);

        mBarCode = (EditText) findViewById(R.id.barcode_et);

        mListView = (ListView) findViewById(R.id.san_lv);

        mBarCode.setOnEditorActionListener(mEditorActionListener);

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

    TextView.OnEditorActionListener mEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            Log.d("SA",textView.getText().toString());
            mBar = textView.getText().toString();

            int l = mDataModels.indexOf(new ScannedDataModel(-1,mBar,"", 0.0f));
            if (l == -1) {
                // нифига не нашли в уже добавленых смотрим в базе
                StoreProductModel product = mDataManager.getDB().searchStore(mBar);
                if (product!= null) {
                    Log.d("SA", product.getName());
                } else {
                    product = new StoreProductModel(mBar,"Новый");
                }
                QueryQuantityDialog dialod = QueryQuantityDialog.newInstans(product.getName(),1f);
                dialod.setQuantityChangeListener(mQuantityChangeListener);
                dialod.show(getSupportFragmentManager(),"QQ");
            } else {
                Float qq = mDataModels.get(l).getQuantity();
                QueryQuantityDialog dialod = QueryQuantityDialog.newInstans(mDataModels.get(l).getName(),qq);
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
            }

        }
    };

}
