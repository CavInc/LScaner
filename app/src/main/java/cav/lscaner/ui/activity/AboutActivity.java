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

public class AboutActivity extends AppCompatActivity {

    private DataManager mDataManager;
    private TextView mDeviceId;
    private EditText mSerialCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mDataManager = DataManager.getInstance();

        mDeviceId = (TextView) findViewById(R.id.ab_device_code);
        mSerialCode = (EditText) findViewById(R.id.ab_code_et);

        String deviceID = mDataManager.getAndroidID();
        mDeviceId.setText(deviceID.substring(deviceID.length()-8));
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
                Log.d("AA","Enter");

                return true;
        }
    };
}
