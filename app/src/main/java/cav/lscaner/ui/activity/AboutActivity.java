package cav.lscaner.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import cav.lscaner.R;
import cav.lscaner.data.managers.DataManager;
import cav.lscaner.utils.Func;

public class AboutActivity extends AppCompatActivity {

    private DataManager mDataManager;
    private TextView mDeviceId;
    private EditText mSerialCode;

    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mDataManager = DataManager.getInstance();

        mDeviceId = (TextView) findViewById(R.id.ab_device_code);
        mSerialCode = (EditText) findViewById(R.id.ab_code_et);

        deviceId = mDataManager.getAndroidID();
        deviceId = deviceId.substring(deviceId.length()-8);
        mDeviceId.setText(deviceId);
        //mDeviceId.setText(deviceID);

        mSerialCode.setOnEditorActionListener(mEditorActionListener);

        if (mDataManager.getPreferensManager().getRegistrationNumber() != null){
            mSerialCode.setText(mDataManager.getPreferensManager().getRegistrationNumber());
        }

        setupToolbar();
    }

    private void setupToolbar() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return true;
    }

    TextView.OnEditorActionListener mEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            // проверяем тот ли номер
            String x = textView.getText().toString();
            if (Func.checkSerialNumber(x,deviceId) ) {
                // сохраняем серийник и флаг что не недемо
                mDataManager.getPreferensManager().setRegistrationNumber(x);
                mDataManager.getPreferensManager().setDemo(false);
            } else {
                mDataManager.getPreferensManager().setDemo(true);
            }
            return false;
        }
    };
}
