package cav.lscaner.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.nio.CharBuffer;

import cav.lscaner.BuildConfig;
import cav.lscaner.R;
import cav.lscaner.data.managers.DataManager;
import cav.lscaner.data.network.Request;
import cav.lscaner.ui.dialogs.ActivateDialog;
import cav.lscaner.ui.dialogs.ActivateNetDialog;
import cav.lscaner.utils.SwipeDetector;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    private DataManager mDataManager;

    private TextView mVersion;
    private TextView mActivateTv;
    private Button mActiveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mDataManager = DataManager.getInstance();


        mVersion = (TextView) findViewById(R.id.ab_version);
        mActiveButton = (Button) findViewById(R.id.ab_bt_activate);
        mActivateTv = (TextView) findViewById(R.id.ab_flg_activate);

        if (!mDataManager.getPreferensManager().getDemo()){
            mActivateTv.setText("(активирована)");
            mActiveButton.setText("Деактивировать");
        }

        mVersion.setText("v"+BuildConfig.VERSION_NAME);

        mActiveButton.setOnClickListener(this);


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

    @Override
    public void onClick(View view) {
        // показываем диалог активации
        /*
        ActivateDialog dialog = new ActivateDialog();
        dialog.setDialogListener(mDialogListener);
        dialog.show(getSupportFragmentManager(),"AD");
        */
        if (mDataManager.getPreferensManager().getDemo()) {
            ActivateNetDialog dialog = new ActivateNetDialog();
            dialog.setDialogListener(mDialogListener);
            dialog.show(getSupportFragmentManager(), "AD");
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Внимание")
                    .setMessage("Деактивируем лицензию на устройство ?")
                    .setNegativeButton(R.string.dialog_no,null)
                    .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int with) {
                            new DeleteDeviceAndLicenseAS(AboutActivity.this,mDataManager.getAndroidID()).execute();
                        }
                    })
                    .show();
        }
    }

    ActivateNetDialog.ActivateDialogListener mDialogListener = new ActivateNetDialog.ActivateDialogListener() {
        @Override
        public void activateState(boolean state) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!mDataManager.getPreferensManager().getDemo()){
                        mActivateTv.setText("(активированна)");
                        mActiveButton.setText("Деактивировать");
                    } else {
                        mActivateTv.setText("(не активирована)");
                    }
                    if (mDataManager.getPreferensManager().getLicenseNewClient()) {
                        // новый клиент
                        AlertDialog.Builder builder = new AlertDialog.Builder(AboutActivity.this);
                        builder.setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Внимание")
                                .setMessage("Клиет новый на нем нет лицензий")
                                .setPositiveButton(R.string.button_ok,null)
                                .show();
                    }
                }
            });


        }

        @Override
        public void noLicense() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AboutActivity.this);
                    builder.setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Внимание")
                            .setMessage("На клиенте нет свободных или активынх лицензий")
                            .setPositiveButton(R.string.button_ok,null)
                            .show();
                }
            });
        }
    };


    private class DeleteDeviceAndLicenseAS  extends AsyncTask<Void,Void,String>{

        private final Activity parent;
        private String deviceid;

        private ProgressDialog dialog;

        public DeleteDeviceAndLicenseAS(Activity activity,String deviceID) {
            parent = activity;
            deviceid = deviceID;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(parent, "Удаление", "Please wait...", true);
        }

        @Override
        protected String doInBackground(Void... voids) {
            Request request = new Request(mDataManager.getPreferensManager());
            boolean res = request.deleteDevice(deviceid);
            if (res) {
                mDataManager.getPreferensManager().setDemo(true);
            }
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            if (!mDataManager.getPreferensManager().getDemo()){
                mActivateTv.setText("(активированна)");
            } else {
                mActivateTv.setText("(не активирована)");
                mActiveButton.setText("Активировать");
            }
        }
    }
}
