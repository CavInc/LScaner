package cav.lscaner.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;

import cav.lscaner.R;
import cav.lscaner.data.managers.DataManager;
import cav.lscaner.ui.adapter.ScannedListAdapter;
import cav.lscaner.utils.ConstantManager;

public class ScanActivity extends AppCompatActivity {
    private EditText mBarCode;
    private ListView mListView;

    private DataManager mDataManager;

    private int idFile = -1;

    private ScannedListAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        mDataManager = DataManager.getInstance();

        idFile = getIntent().getIntExtra(ConstantManager.SELECTED_FILE,-1);

        mBarCode = (EditText) findViewById(R.id.barcode_et);

        mListView = (ListView) findViewById(R.id.san_lv);

    }

    private void updateUI(){

    }
}
