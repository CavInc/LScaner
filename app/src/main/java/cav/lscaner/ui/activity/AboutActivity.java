package cav.lscaner.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cav.lscaner.BuildConfig;
import cav.lscaner.R;
import cav.lscaner.data.managers.DataManager;
import cav.lscaner.ui.dialogs.ActivateDialog;
import cav.lscaner.ui.dialogs.ActivateNetDialog;

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
        ActivateNetDialog dialog = new ActivateNetDialog();
        dialog.setDialogListener(mDialogListener);
        dialog.show(getSupportFragmentManager(),"AD");
    }

    ActivateDialog.ActivateDialogListener mDialogListener = new ActivateDialog.ActivateDialogListener() {
        @Override
        public void activateState(boolean state) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!mDataManager.getPreferensManager().getDemo()){
                        mActivateTv.setText("(активированна)");
                    }
                }
            });

        }
    };
}
