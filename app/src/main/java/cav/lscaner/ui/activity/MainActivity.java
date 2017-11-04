package cav.lscaner.ui.activity;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.LocaleDisplayNames;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cav.lscaner.R;
import cav.lscaner.data.managers.DataManager;
import cav.lscaner.data.models.ScannedFileModel;
import cav.lscaner.ui.adapter.ScannedFileAdapter;
import cav.lscaner.ui.dialogs.AddEditNameFileDialog;
import cav.lscaner.ui.dialogs.SelectMainDialog;
import cav.lscaner.utils.ConstantManager;
import cav.lscaner.utils.Func;
import cav.lscaner.utils.WorkInFile;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener,
        EasyPermissions.PermissionCallbacks{

    private static final String TAG = "MAIN";
    private FloatingActionButton mFAB;
    private ListView mListView;

    private DataManager mDataManager;

    private ScannedFileAdapter mFileAdapter;

    private boolean newRecord = true;

    private String storeFileFullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDataManager = DataManager.getInstance();

        mListView = (ListView) findViewById(R.id.main_lv);

        mFAB = (FloatingActionButton) findViewById(R.id.main_fab);

        mFAB.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_setting){
            Intent intent = new Intent(this,SettingActivity.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.menu_refresh){
            Toast.makeText(MainActivity.this,
                    "А тут будет диалог спрашивающий откуда взять файл (имя файла в настройка)",
                    Toast.LENGTH_LONG).show();
        }
        if (item.getItemId() == R.id.menu_about) {
            Intent intent = new Intent(this,AboutActivity.class);
            startActivity(intent);
        }

        return true;
    }

    private void updateUI(){
        ArrayList<ScannedFileModel> model = mDataManager.getScannedFile();
        if (mFileAdapter == null){
            mFileAdapter = new ScannedFileAdapter(this,R.layout.scanned_file_item,model);
            mListView.setAdapter(mFileAdapter);
        }else {
            mFileAdapter.setDate(model);
            mFileAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.main_fab) {
            newRecord = true;
            AddEditNameFileDialog dialog = AddEditNameFileDialog.newInstance("");
            dialog.setAddEditNameFileListener(mAddEditNameFileListener);
            dialog.show(getSupportFragmentManager(),"AddFile");
        }

    }

    private AddEditNameFileDialog.AddEditNameFileListener mAddEditNameFileListener = new AddEditNameFileDialog.AddEditNameFileListener() {
        @Override
        public void changeName(String value) {
            String cdate = Func.getNowDate("yyyy-MM-dd");
            String ctime = Func.getNowTime();
            int pos = -1;
            if (!newRecord) pos = selModel.getId();
            mDataManager.getDB().addFileName(value,cdate,ctime,pos);
            updateUI();
        }
    };

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent(this,ScanActivity.class);
        intent.putExtra(ConstantManager.SELECTED_FILE,mFileAdapter.getItem(position).getId());
        intent.putExtra(ConstantManager.SELECTED_FILE_NAME,mFileAdapter.getItem(position).getName());
        startActivity(intent);
    }

    private ScannedFileModel selModel = null;

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        selModel = (ScannedFileModel) adapterView.getItemAtPosition(position);

        SelectMainDialog dialog = new SelectMainDialog();
        dialog.setSelectMainDialogListener(mSelectMainDialogListener);
        dialog.show(getFragmentManager(),"SELECT_DIALOG");
        return true;
    }

    SelectMainDialog.SelectMainDialogListener mSelectMainDialogListener = new SelectMainDialog.SelectMainDialogListener() {
        @Override
        public void selectedItem(int index) {
            if (index == R.id.dialog_del_item) {
                // удаляем
                deleteRecord(selModel.getId());
            }
            if (index == R.id.dialog_edit_item) {
                // редактируем заголовок
                //Toast.makeText(MainActivity.this,"А тут будет диалог редактирования заголовка файла",Toast.LENGTH_LONG).show();
                newRecord = false;
                AddEditNameFileDialog dialog = AddEditNameFileDialog.newInstance(selModel.getName());
                dialog.setAddEditNameFileListener(mAddEditNameFileListener);
                dialog.show(getSupportFragmentManager(),"UpdateFile");
            }
            if (index == R.id.dialog_send_item) {
                // отправляем наружу
                if (!mDataManager.isOnline()){
                    // показываем что нет сети
                    showNoNetwork();
                    return;
                }
                // сохраняем файл
                WorkInFile workInFile = new WorkInFile();
                workInFile.saveFile(selModel.getId(),selModel.getName(),mDataManager);
                Log.d(TAG,workInFile.getSavedFile());
                storeFileFullName = workInFile.getSavedFile();

                // показываем окно с выбором куда отправлять
                Toast.makeText(MainActivity.this,"А тут будет диалог спрашивающий куда отправить",Toast.LENGTH_LONG).show();
                // вызов отправки
                pushGD();


            }
        }
    };

    private void pushGD() {
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        getResultsFromApi();
    }

    // показываем что нет сети.
    private void showNoNetwork() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.app_name)
                .setMessage("No network connection available")
                .setCancelable(false)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void deleteRecord(final int selIdFile) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Удаление")
                .setMessage("Удаляем ? Вы уверены ?")
                .setPositiveButton(R.string.button_ok,new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int witch) {
                        //TODO добавить удаление файла выгрузки с SD
                        mDataManager.getDB().deleteFile(selIdFile);
                        updateUI();
                    }
                })
                .setNegativeButton(R.string.button_cancel,null)
                .create();
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(R.string.app_name)
                            .setMessage("This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.")
                            .setCancelable(false)
                            .setNegativeButton("Close",new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    // все что связано с гуглодрайвом
    GoogleAccountCredential mCredential;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { DriveScopes.DRIVE_METADATA_READONLY,DriveScopes.DRIVE_FILE };

    // обрабатываем весь цикл для GD

    private void getResultsFromApi(){
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else {
            new SendRequestTask(mCredential,storeFileFullName).execute();
        }
    }


    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    private void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                MainActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();

            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }


    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    // Класс для передачи данных
    public class SendRequestTask extends AsyncTask<Void, Void, Void> {
        private Exception mLastError = null;
        private com.google.api.services.drive.Drive mService = null;

        private String fn = null;
        private java.io.File filePath = null;

        public SendRequestTask(GoogleAccountCredential credential, String fn){
            this.fn = fn;

            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            mService = new com.google.api.services.drive.Drive.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("LScanner")
                    .build();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            filePath = new java.io.File(fn);

            File fileMetadata = new File();
            fileMetadata.setMimeType("application/vnd.google-apps.spreadsheet");
            fileMetadata.setDescription("Scanned file");
            fileMetadata.setName(filePath.getName());

            FileContent mediaContent = new FileContent("text/csv", filePath);

            File file = null;

            try {
                file = mService.files().create(fileMetadata, mediaContent)
                        .setFields("id,name")
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();

                mLastError = e;
                cancel(true);
                return null;
            }



            /*
            			String extend_file = defAppSettings.getString("extend_file","csv");

            fileMetadata.setName("KarmaReport"+viewDate+"."+extend_file);
            if (extend_file.equals("csv")) {
                fileMetadata.setMimeType("application/vnd.google-apps.spreadsheet");
            } else {
                // закоментировать нахрен если завтра утром останется таблицей
               // fileMetadata.setMimeType("application/vnd.google-apps.unknown");
            }


            filePath = new java.io.File(fn);
            FileContent mediaContent = new FileContent("text/csv", filePath);

            File file = null;
            try {

                Log.d(TAG,"DATE: " + defAppSettings.getString("GD_DATE","")+" file :"+defAppSettings.getString("GD_FILE",""));

                if (defAppSettings.getString("GD_DATE","").equals(viewDate) &
                        defAppSettings.getString("GD_FILE","").length()!=0 &
                        defAppSettings.getString("GD_OLD_EXT","csv").equals(extend_file)){
                    Log.d(TAG,"YES CONT DATE");
                    fileMetadata.setModifiedTime(new DateTime(System.currentTimeMillis()));
                    file = mService.files().update(defAppSettings.getString("GD_FILE",""),fileMetadata,mediaContent)
                            .setFields("id,modifiedTime")
                            .execute();
                    //.setFileId("id, modifiedTime")

                } else {

                    file = mService.files().create(fileMetadata, mediaContent)
                            .setFields("id,name")
                            .execute();

                    Editor editor = defAppSettings.edit();
                    editor.putString("GD_FILE",file.getId());
                    editor.putString("GD_DATE",viewDate);
                    editor.putString("GD_NAME",file.getName());
                    editor.putString("GD_OLD_EXT",defAppSettings.getString("extend_file","csv"));

                    editor.apply();
                }

            } catch (Exception e) {
                e.printStackTrace();
                mLastError = e;
				// удаление файла здесь не лутшая идея наверно если мы потом перезапускаем отправку
				//filePath.delete();
                cancel(true);
                return null;
            }

            Log.d(TAG,"File ID: " + file.getId()+" file :"+file.getName());
			filePath.delete();

             */

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //resend = false;
            //count_fail_resend = 0;
        }

        @Override
        protected void onCancelled() {
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                }else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MainActivity.REQUEST_AUTHORIZATION);

                } else {
                    Log.d(TAG,"Error "+mLastError);

                }
            } else {
                Log.d(TAG,"Request cancelled.");
            }
            /*
            if (mLastError != null) {

                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                }else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MainActivity.REQUEST_AUTHORIZATION);

                } else {
                    //Log.d(TAG,mLastError.getMessage());
                    // вот так должно работать
                    int pref_count_fail =  Integer.parseInt(defAppSettings.getString("count_send", "3"));

                    if (!defAppSettings.getBoolean("resend_on_error",false) | pref_count_fail<count_fail_resend) {
                        // удаляем файл для пересылки
                        filePath.delete();

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle(R.string.app_name)
                                .setMessage("Error: " + mLastError.toString())
                                .setCancelable(false)
                                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                    @Override
                                   public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        resend = true;
                        count_fail_resend +=1;
                        getResultsFromApi();
                    }
                }

            }
            */
        }
    }


}
