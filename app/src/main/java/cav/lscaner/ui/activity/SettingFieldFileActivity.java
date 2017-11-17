package cav.lscaner.ui.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;

import cav.lscaner.R;
import cav.lscaner.data.managers.DataManager;
import cav.lscaner.data.models.FieldOutFile;
import cav.lscaner.data.models.FileFieldModel;

public class SettingFieldFileActivity extends AppCompatActivity {
    private EditText mBar;
    private EditText mName;
    private EditText mArticul;
    private EditText mPrice;
    private EditText mEgais;

    private EditText mBasePrice;
    private EditText mOstatok;

    private EditText mOutBar;
    private EditText mOutQuantity;
    private EditText mOutPrice;

    private DataManager mDataManager;

    private FileFieldModel mFileFieldModel;
    private FieldOutFile mFieldOutFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_field_file);

        mDataManager = DataManager.getInstance();

        mBar = (EditText) findViewById(R.id.sff_barcode);
        mName = (EditText) findViewById(R.id.sff_name);
        mArticul = (EditText) findViewById(R.id.sff_code);
        mPrice = (EditText) findViewById(R.id.sff_price);
        mEgais = (EditText) findViewById(R.id.sff_egais);

        //mBasePrice;
        //mOstatok;

        mOutBar = (EditText) findViewById(R.id.sf_out_bar);
        mOutQuantity = (EditText) findViewById(R.id.sf_out_quantity);

        mFileFieldModel = mDataManager.getPreferensManager().getFieldFileModel();
        mFieldOutFile = mDataManager.getPreferensManager().getFieldOutFile();

        if (mFileFieldModel.getBar() != -1) {
            mBar.setText(String.valueOf(mFileFieldModel.getBar()));
        }
        if (mFileFieldModel.getName() != -1){
            mName.setText(String.valueOf(mFileFieldModel.getName()));
        }
        if (mFileFieldModel.getArticul() !=-1){
            mArticul.setText(String.valueOf(mFileFieldModel.getArticul()));
        }
        if (mFileFieldModel.getPrice() != -1) {
            mPrice.setText(String.valueOf(mFileFieldModel.getPrice()));
        }
        if (mFileFieldModel.getEGAIS() != -1) {
            mEgais.setText(String.valueOf(mFileFieldModel.getEGAIS()));
        }

        // выходной файл
        if (mFieldOutFile.getBarcode() != -1){
            mOutBar.setText(String.valueOf(mFieldOutFile.getBarcode()));
        }
        if (mFieldOutFile.getQuantity() != -1){
            mOutQuantity.setText(String.valueOf(mFieldOutFile.getQuantity()));
        }
        if (mFieldOutFile.getPrice() != -1){
            mOutPrice.setText(String.valueOf(mFieldOutFile.getPrice()));
        }

        setupToolBar();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // сохраняем данные
        if (mBar.getText().length() == 0) {
            mFileFieldModel.setBar(-1);
        } else {
            mFileFieldModel.setBar(Integer.parseInt(mBar.getText().toString()));
        }
        if (mName.getText().length() == 0) {
            mFileFieldModel.setName(-1);
        } else {
            mFileFieldModel.setName(Integer.parseInt(mName.getText().toString()));
        }
        if (mArticul.getText().length() == 0) {
            mFileFieldModel.setArticul(-1);
        } else {
            mFileFieldModel.setArticul(Integer.parseInt(mArticul.getText().toString()));
        }
        if (mPrice.getText().length() == 0) {
            mFileFieldModel.setPrice(-1);
        } else {
            mFileFieldModel.setPrice(Integer.parseInt(mPrice.getText().toString()));
        }
        if (mEgais.getText().length() == 0) {
            mFileFieldModel.setEGAIS(-1);
        } else {
            mFileFieldModel.setEGAIS(Integer.parseInt(mEgais.getText().toString()));

        }
        mDataManager.getPreferensManager().setFieldFileModel(mFileFieldModel);
    }
}
