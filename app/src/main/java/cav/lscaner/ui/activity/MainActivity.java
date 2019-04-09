package cav.lscaner.ui.activity;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v4.provider.DocumentFile;
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
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAuthIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.firebase.crash.FirebaseCrash;


import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cav.lscaner.R;
import cav.lscaner.data.managers.DataManager;
import cav.lscaner.data.models.ScannedFileModel;
import cav.lscaner.ui.adapter.ScannedFileAdapter;
import cav.lscaner.ui.dialogs.AddEditNameFileDialog;
import cav.lscaner.ui.dialogs.DemoDialog;
import cav.lscaner.ui.dialogs.SelectMainDialog;
import cav.lscaner.ui.dialogs.SendReciveDialog;
import cav.lscaner.ui.dialogs.WarningDialog;
import cav.lscaner.utils.ConstantManager;
import cav.lscaner.utils.Func;
import cav.lscaner.utils.SwipeDetector;
import cav.lscaner.utils.WorkInFile;
import lib.folderpicker.FolderPicker;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener,
        EasyPermissions.PermissionCallbacks{

    private static final String TAG = "MAIN";
    private static final int WRITE_FILE = 1012;
    private static final int READ_FILE = 1010;
    private static final int REQUEST_OPEN_DOCUMENT = 875;
    private static final int REQUEST_STORE_FILE = 876;
    private static final int FOLDERPICKER_CODE = 877;

    private final int MAX_DEMO_REC = 4;

    private FloatingActionButton mFAB;
    private ListView mListView;

    private DataManager mDataManager;

    private ScannedFileAdapter mFileAdapter;

    private boolean newRecord = true;

    private String storeFileFullName;

    private int directionGD = WRITE_FILE ;// что делаем с файлом

    private boolean demo = true;
    private SwipeDetector swipeDetector;

    private boolean multiSelectFlg = false;

    private ActionBar mActionBar;


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


        swipeDetector = new SwipeDetector();

        mListView.setOnTouchListener(swipeDetector);

        mActionBar = getActionBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        demo = mDataManager.getPreferensManager().getDemo();
        getPermisionStorage(); // запрос разрешения на SD
        getPermissionCamera(); // разрешения на камеру
        updateUI();
    }

    private Menu menu;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
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
            /*
            if (!mDataManager.isOnline()){
                // показываем что нет сети
                showNoNetwork();
                return false;
            }
            */
            directionGD = READ_FILE;

            SendReciveDialog dialog = new SendReciveDialog();
            dialog.setSendReciveListener(mSendReciveListener);
            dialog.show(getSupportFragmentManager(),"SRD");

            //
            /*
            if (mDataManager.getPreferensManager().getLocalServer() != null) {
                SendReciveDialog dialog = new SendReciveDialog();
                dialog.setSendReciveListener(mSendReciveListener);
                dialog.show(getSupportFragmentManager(),"SRD");
            } else {
                requestData();
            }
            */
        }
        if (item.getItemId() == R.id.menu_about) {
            Intent intent = new Intent(this,AboutActivity.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.menu_filefield_setting){
            Intent intent = new Intent(this,SettingFieldNewActivity.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.menu_store_product){
            Intent intent = new Intent(this,StoreProductActivity.class);
            startActivity(intent);
        }
        // множествееный выбор
        if (item.getItemId() == R.id.menu_multi_select) {
            multiSelectChange();
        }

        // групповое удаление
        if (item.getItemId() == R.id.menu_delete_select) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Удаление")
                    .setMessage("Удаляем ? Вы уверены ?")
                    .setPositiveButton(R.string.button_ok,new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface, int witch) {
                            for (int i = 0;i < mFileAdapter.getCount();i++){
                                if (mFileAdapter.getItem(i).isSelected()) {
                                    mDataManager.getDB().deleteFile(mFileAdapter.getItem(i).getId());
                                }
                            }
                            multiSelectChange();
                            updateUI();
                        }
                    })
                    .setNegativeButton(R.string.button_cancel,null)
                    .create();
            builder.show();
        }
        // групповая отправка файлов
        if (item.getItemId() == R.id.menu_send_select) {
            multiSelectSend();
        }
        return true;
    }

    private void multiSelectChange(){
        if (!multiSelectFlg) {
            multiSelectFlg = true;
            mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            menu.clear();
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.main_menu2, menu);

        } else {
            multiSelectFlg = false;
            // убираем отметки
            clearSelectModel();
            menu.clear();
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.main_menu, menu);
        }
    }

    // отправка выделенных файлов на сервер
    private void multiSelectSend(){
        // отправляем наружу
        if (!mDataManager.isOnline()){
            // показываем что нет сети
            showNoNetwork();
            return;
        }

        directionGD = WRITE_FILE;
        // сохраняем файл
        WorkInFile workInFile = new WorkInFile(mDataManager.getPreferensManager().getCodeFile());

        for (int i = 0;i < mFileAdapter.getCount();i++){
            if (mFileAdapter.getItem(i).isSelected()) {
               // mDataManager.getDB().deleteFile(mFileAdapter.getItem(i).getId());
                selModel = mFileAdapter.getItem(i);
                workInFile.saveFile(selModel.getId(),selModel.getName(),mDataManager,selModel.getType());
                Log.d(TAG,workInFile.getSavedFile());

                storeFileFullName = workInFile.getSavedFile();
                fileType =  selModel.getType();
                pushGD();
            }
        }
        multiSelectChange();
        updateUI();
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
            int rec_count = mDataManager.getDB().getCountFile();
            if (demo && (rec_count+1)>MAX_DEMO_REC) {
                Log.d(TAG,"DEMO");
                new DemoDialog().show(getSupportFragmentManager(),"DEMO");
            } else {
                Log.d(TAG,"NO DEMO");
                AddEditNameFileDialog dialog = AddEditNameFileDialog.newInstance("",0);
                dialog.setAddEditNameFileListener(mAddEditNameFileListener);
                dialog.show(getSupportFragmentManager(), "AddFile");
            }
        }

    }

    private AddEditNameFileDialog.AddEditNameFileListener mAddEditNameFileListener = new AddEditNameFileDialog.AddEditNameFileListener() {
        @Override
        public void changeName(String value, int type_file) {
            String cdate = Func.getNowDate("yyyy-MM-dd");
            String ctime = Func.getNowTime();
            int pos = -1;
            if (!newRecord) pos = selModel.getId();
            mDataManager.getDB().addFileName(value,cdate,ctime,pos,type_file);
            updateUI();
        }
    };

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (swipeDetector.swipeDetected()) {
            if (swipeDetector.getAction() == SwipeDetector.Action.LR) {
                Log.d(TAG,"LEFT");
                // удаляем
                deleteRecord(mFileAdapter.getItem(position).getId());
            }
            if (swipeDetector.getAction() == SwipeDetector.Action.RL) {
                Log.d(TAG,"RIGTH");
                selModel = (ScannedFileModel) adapterView.getItemAtPosition(position);
                editRecord();
            }
            return;
        }
        if (!multiSelectFlg) {
            Intent intent = new Intent(this, ScanActivity.class);
            intent.putExtra(ConstantManager.SELECTED_FILE, mFileAdapter.getItem(position).getId());
            intent.putExtra(ConstantManager.SELECTED_FILE_NAME, mFileAdapter.getItem(position).getName());
            intent.putExtra(ConstantManager.SELECTED_FILE_TYPE, mFileAdapter.getItem(position).getType());
            startActivity(intent);
        } else {
            //TODO а здесь выделяем или снимаем выделение
            mFileAdapter.getItem(position).setSelected(!mFileAdapter.getItem(position).isSelected());
            mFileAdapter.notifyDataSetChanged();
        }
    }

    // сбрасываем выделенные отметки
    private void clearSelectModel() {
        for (int i=0;i < mFileAdapter.getCount(); i++){
         mFileAdapter.getItem(i).setSelected(false);
        }
        mFileAdapter.notifyDataSetChanged();
    }

    private ScannedFileModel selModel = null;
    private int fileType;

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
                editRecord();
            }
            if (index == R.id.dialog_send_item) {
                // отправляем наружу
                /*
                if (!mDataManager.isOnline()){
                    // показываем что нет сети
                    showNoNetwork();
                    return;
                }
                */
                directionGD = WRITE_FILE;
                // сохраняем файл
                WorkInFile workInFile = new WorkInFile(mDataManager.getPreferensManager().getCodeFile());
                workInFile.saveFile(selModel.getId(),selModel.getName(),mDataManager,selModel.getType());
                Log.d(TAG,workInFile.getSavedFile());
                storeFileFullName = workInFile.getSavedFile();
                fileType =  selModel.getType();

                // показываем окно с выбором... если нет сохраненного локал сервере
                // то нваерно не показываем..

                SendReciveDialog dialog = new SendReciveDialog();
                dialog.setSendReciveListener(mSendReciveListener);
                dialog.show(getSupportFragmentManager(),"SRD");
                return;


            }
        }
    };

    // редактируем запись (заголовок)
    private void editRecord(){
        newRecord = false;
        AddEditNameFileDialog dialog = AddEditNameFileDialog.newInstance(selModel.getName(),selModel.getType());
        dialog.setAddEditNameFileListener(mAddEditNameFileListener);
        dialog.show(getSupportFragmentManager(),"UpdateFile");
    }

    private void pushGD() {
        // создаем аккаунт и запрашиваем всякое. через OAuth2
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        // работаем с API
        getResultsFromApi();
    }

    private void requestData() {
        // создаем аккаунт и запрашиваем всякое. через OAuth2
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        // работаем с API
        getResultsFromApi();
    }

    // показываем что нет сети.
    private void showNoNetwork() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name)
                .setMessage(R.string.no_network)
                .setCancelable(false)
                .setNegativeButton(R.string.button_close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // Удаляем запись
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

    SendReciveDialog.OnSendReciveListener mSendReciveListener = new SendReciveDialog.OnSendReciveListener() {
        @Override
        public void selectedItem(int item) {
            if (directionGD == WRITE_FILE) {
                if (item == ConstantManager.GD) {
                    if (!mDataManager.isOnline()){
                        // показываем что нет сети
                        showNoNetwork();
                        return;
                    }
                    pushGD();
                }
                if (item == ConstantManager.LS) {
                    if (!mDataManager.isOnline()){
                        // показываем что нет сети
                        showNoNetwork();
                        return;
                    }
                    if (mDataManager.getPreferensManager().getLocalServer() != null) {
                        new NetLocalTask(mDataManager.getPreferensManager().getLocalServer(),
                                storeFileFullName, fileType).execute();
                    } else {
                        //Показываем что ой нету у нас настроенного сервера
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Внимание! ")
                                .setMessage("Не настроено подключение к локальному серверу")
                                .setNegativeButton(R.string.button_cancel,null)
                                .show();
                    }
                }
                if (item == ConstantManager.LDEV) {
                    storeLocalFile(storeFileFullName);
                }
            }
            if (directionGD == READ_FILE ){

                if (item == ConstantManager.GD) {
                    if (!mDataManager.isOnline()){
                        // показываем что нет сети
                        showNoNetwork();
                        return;
                    }
                    requestData();
                }
                if (item == ConstantManager.LS) {
                    if (!mDataManager.isOnline()){
                        // показываем что нет сети
                        showNoNetwork();
                        return ;
                    }
                    if (mDataManager.getPreferensManager().getLocalServer() != null) {
                        new GetLocalTask(mDataManager.getPreferensManager().getLocalServer(),
                                mDataManager.getPreferensManager().getStoreFileName()).execute();
                    } else {
                        //Показываем что ой нету у нас настроенного сервера
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Внимание! ")
                                .setMessage("Не настроено подключение к локальному серверу")
                                .setNegativeButton(R.string.button_cancel,null)
                                .show();
                    }
                }
                if (item == ConstantManager.LDEV) {
                    openLocalFile();
                }
            }
        }
    };

    // запись на локальное устройство с выбором места
    private void storeLocalFile(String storeFileFullName) {
        Intent storeData = null;
        if (Build.VERSION.SDK_INT >= 21 ) {
            storeData = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            startActivityForResult(Intent.createChooser(storeData,
                    getString(R.string.store_intent)), REQUEST_STORE_FILE);
        } else {
            //storeData = new Intent(Intent.ACTION_GET_CONTENT);
            //storeData.addCategory(Intent.CATEGORY_OPENABLE);
            //startActivityForResult(storeData,REQUEST_STORE_FILE);


            //Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            //Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()
            //        +   java.io.File.separator + "/" + java.io.File.separator);
            //intent.setDataAndType(uri, "*/*");
            //intent.addCategory(Intent.CATEGORY_OPENABLE);
            //intent.addCategory(Intent.CATEGORY_DEFAULT);
            //startActivityForResult(Intent.createChooser(intent,
           //         getString(R.string.store_intent)), REQUEST_STORE_FILE);


            /*
            Uri selectedUri = Uri.parse(Environment.getExternalStorageDirectory() + "/myFolder/");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(selectedUri, "resource/folder");

            if (intent.resolveActivityInfo(getPackageManager(), 0) != null) {
                startActivity(intent);
            } else {
                // if you reach this place, it means there is no any file
                // explorer app installed on your device
                Toast.makeText(this,"NO Explorer",Toast.LENGTH_SHORT).show();

            }
            */

            Intent intent = new Intent(this, FolderPicker.class);
            startActivityForResult(intent, FOLDERPICKER_CODE);

        }

    }

    // читаем файл с локального устройства
    private void openLocalFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        startActivityForResult(Intent.createChooser(intent,"chooser"),REQUEST_OPEN_DOCUMENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(R.string.app_name)
                            .setMessage("Для этого приложения требуются сервисы Google Play. Пожалуйста, установите " +
                                    "Службы Google Play на вашем устройстве и перезапустите это приложение.")
                            .setCancelable(false)
                            .setNegativeButton(R.string.button_close,new DialogInterface.OnClickListener(){
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
            case REQUEST_OPEN_DOCUMENT:
                if (resultCode == RESULT_OK && data != null) {
                    Uri uri = data.getData();
                    loadLocalFile(uri);
                }
                break;
            case REQUEST_STORE_FILE:
                if (resultCode == RESULT_OK &&
                        mDataManager.takePermission(getApplicationContext(), data.getData())) {
                    Uri uri = data.getData();
                    localStoreFile(uri);
                }
                break;
            case FOLDERPICKER_CODE:
                if (resultCode == RESULT_OK && data.hasExtra("data")) {
                    String folderLocation = data.getExtras().getString("data");
                    Log.i("folderLocation", folderLocation);
                    //content://com.android.externalstorage.documents/tree/primary%3ADownload
                    //Uri uri = Uri.parse("file://"+folderLocation);
                    //Uri uri = Uri.parse("content://com.android.externalstorage.documents/tree/primary");
                   // localStoreFile(uri);
                    localStoreFileOld(folderLocation);
                    /*
                    if (mDataManager.takePermission(getApplicationContext(), uri)) {
                        localStoreFile(uri);
                    }
                   */

                }
                break;
        }
    }

    private void loadLocalFile(Uri uri) {
        final java.io.File fname = copyUriToLocal(uri);

        WorkInFile workInFile = new WorkInFile(mDataManager.getPreferensManager().getCodeFile());
        int ret_flg =  workInFile.loadProductFile(fname.getName(),mDataManager);

        if (ret_flg == ConstantManager.RET_NO_FIELD_MANY) {
            WarningDialog dialog = WarningDialog.newInstance("Количество полей в файле меньше чем указано в настройках");
            dialog.show(getFragmentManager(),"WD");
            return;
        }
        if (ret_flg == ConstantManager.RET_ERROR) {
            WarningDialog dialog = WarningDialog.newInstance("Ошибка при загрузке файла данных\n"+mDataManager.getLastError());
            dialog.show(getFragmentManager(),"WD");
            return;
        }
        // сказать что все ок
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.app_name)
                .setMessage("Обработан файл \n"+fname.getName())
                .setCancelable(false)
                .setNegativeButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        fname.delete();
                    }
                })
                .create();
        builder.show();
    }

    private void localStoreFile(Uri treeUri) {
        if (treeUri.toString().length() == 0) return;
        DocumentFile fout = DocumentFile.fromTreeUri(this,treeUri);
        //File fout = new File(storeFileFullName);

        java.io.File fSrc = new java.io.File(storeFileFullName);
        String f = Func.createFileName(fSrc.getName(),fileType);


        DocumentFile infile = fout.createFile("text/plain", f);
        try {
            FileOutputStream output = (FileOutputStream) getContentResolver().openOutputStream(infile.getUri());
            FileInputStream input = new FileInputStream(fSrc);

            FileChannel fileChannelIn = input.getChannel();
            FileChannel fileChannelOut = output.getChannel();
            fileChannelIn.transferTo(0, fileChannelIn.size(), fileChannelOut);

            output.flush();
            output.close();
            input.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        // удаляем скопрированный файл
        fSrc.delete();

    }

    private void localStoreFileOld(String fdir) {
        java.io.File fSrc = new java.io.File(storeFileFullName);
        String f = Func.createFileName(fSrc.getName(),fileType);

        java.io.File fDesc = new java.io.File(fdir,f);


        try {
            FileOutputStream output = new FileOutputStream(fDesc);
            FileInputStream input = new FileInputStream(fSrc);

            FileChannel fileChannelIn = input.getChannel();
            FileChannel fileChannelOut = output.getChannel();
            fileChannelIn.transferTo(0, fileChannelIn.size(), fileChannelOut);

            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private java.io.File copyUriToLocal(Uri uri){
        DocumentFile file = DocumentFile.fromSingleUri(this,uri);

        try {
            java.io.File fOut = new java.io.File(mDataManager.getStorageAppPath(),file.getName());

            FileInputStream input = (FileInputStream) getContentResolver().openInputStream(file.getUri());
            FileOutputStream output = new FileOutputStream(fOut);

            FileChannel fileChannelIn = input.getChannel();
            FileChannel fileChannelOut = output.getChannel();
            fileChannelIn.transferTo(0, fileChannelIn.size(), fileChannelOut);

            output.flush();
            output.close();
            input.close();

            return fOut;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    // все что связано с гуглодрайвом
    GoogleAccountCredential mCredential;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    static final int REQUEST_PERMISSINO_WRITE_STORAGE = 1004;
    static final int REQUEST_PERMISSION_CAMERA = 1005;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { DriveScopes.DRIVE_METADATA_READONLY,
            DriveScopes.DRIVE_FILE,DriveScopes.DRIVE_APPDATA,DriveScopes.DRIVE};

    // обрабатываем весь цикл для GD

    private void getResultsFromApi(){
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else {
            if (directionGD == WRITE_FILE) {
                new SendRequestTask(mCredential, storeFileFullName,fileType).execute();
            } else {
                new RequestDataTask(mCredential,mDataManager.getPreferensManager().getStoreFileName()).execute();
            }
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

    // запрос разрешения на работу с акаунтами
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
                    "Это приложение должно получить доступ к вашей учетной записи Google (через контакты).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    // запрос разрешения на SD
    @AfterPermissionGranted(REQUEST_PERMISSINO_WRITE_STORAGE)
    private void getPermisionStorage(){
        if (!EasyPermissions.hasPermissions(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            EasyPermissions.requestPermissions(this,"Это приложение должно получить доступ к вашему SD",
                    REQUEST_PERMISSINO_WRITE_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    // запрос разрешения на камеру
    @AfterPermissionGranted(REQUEST_PERMISSION_CAMERA)
    private void getPermissionCamera(){
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)){
            EasyPermissions.requestPermissions(this,"Это приложение должно получить доступ к вашешей камере",
                    REQUEST_PERMISSION_CAMERA,Manifest.permission.CAMERA);
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

        private int filetype;

        public SendRequestTask(GoogleAccountCredential credential, String fn,int filetype){
            this.fn = fn;
            this.filetype = filetype;

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
            //fileMetadata.setMimeType("application/vnd.google-apps.spreadsheet");
            fileMetadata.setMimeType("text/plain");
            fileMetadata.setDescription("Scanned file");
            String fname = filePath.getName();
            if (fname.toUpperCase().indexOf(".TXT") == -1){ fname = fname+".txt";}

            fname="_"+fname;

            if (filetype == ConstantManager.FILE_TYPE_PRODUCT) {
                fname = ConstantManager.PREFIX_FILE_TOVAR+fname;
            }
            if (filetype == ConstantManager.FILE_TYPE_EGAIS) {
                fname = ConstantManager.PREFIX_FILE_EGAIS+fname;
            }
            if (filetype == ConstantManager.FILE_TYPE_CHANGE_PRICE) {
                fname = ConstantManager.PREFIX_FILE_CHANGEPRICE+fname;
            }
            if (filetype == ConstantManager.FILE_TYPE_PRIHOD) {
                fname = ConstantManager.PREFIX_FILE_PRIHOD+fname;
            }
            if (filetype == ConstantManager.FILE_TYPE_ALCOMARK) {
                fname = ConstantManager.PREFIX_FILE_ALCOMARK+fname;
            }

            fileMetadata.setName(fname);

            FileContent mediaContent = new FileContent("text/csv", filePath);

            File file = null;

            try {
                file = mService.files().create(fileMetadata, mediaContent)
                        .setFields("id,name")
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
                FirebaseCrash.report(e);
                mLastError = e;
                cancel(true);
                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.app_name)
                    .setMessage("Выгружен файл ....")
                    .setCancelable(false)
                    .setNegativeButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            filePath.delete();
                        }
                    })
                    .create();
            builder.show();
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
                    ErrorDialog(mLastError,null);
                }
            } else {
                Log.d(TAG,"Request cancelled.");
            }

        }
    }

    // класс для чтения данных
    class RequestDataTask extends AsyncTask<Void, Void, Void> {
        private Exception mLastError = null;
        private com.google.api.services.drive.Drive mService = null;

        private String fn = null;
        private java.io.File filePath;

        private DateTime createDate;
        private DateTime modifidDate;
        private boolean resultSearchFlag;


        public RequestDataTask(GoogleAccountCredential credential, String fn){
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
            String path = mDataManager.getStorageAppPath();
            filePath = new java.io.File(path,fn);

            //System.out.println(new Date());

            // получили список файлов
            List fileList = null;
            try {
                fileList = getList();
            } catch (IOException e) {
                e.printStackTrace();
                mLastError = e;
                cancel(true);
                return null;
            }

            //http://javaprogrammernotes.blogspot.ru/2013/01/drive-api-2.html
            String fileId = null;
            String fileName = null;
            resultSearchFlag = false;

            for (Object l:fileList){
                File lx = (File) l;
                //System.out.println(lx.getId()+" "+lx.getName()+" "+lx.getDescription());
                //Log.d(TAG,lx.getName().toUpperCase()+" "+fn.toUpperCase()+" "+lx.getName().toUpperCase().equals(fn.toUpperCase()));
                if (lx.getName().toUpperCase().equals(fn.toUpperCase())){
                    fileId = lx.getId();
                    //fileName = lx.getName();
                    try {
                        File fxm = mService.files().get(fileId).setFields("createdTime,modifiedTime").execute();
                        createDate = fxm.getCreatedTime();
                        modifidDate = fxm.getModifiedTime();
                    } catch (IOException e) {
                        e.printStackTrace();
                        mLastError = e;
                        cancel(true);
                        return null;
                    }
                    break;
                }
            }
            if (fileId != null) {
                //mService.files().
                resultSearchFlag = true;

                Log.d(TAG,"НАШЛИ :"+fileId);
               // System.out.println(new Date());
                try {
                    final FileOutputStream outputStream = new FileOutputStream(filePath);
                    mService.files().get(fileId).executeMediaAndDownloadTo(outputStream);
                    //System.out.println(new Date());
                    WorkInFile workInFile = new WorkInFile(mDataManager.getPreferensManager().getCodeFile());
                    int ret_flg =  workInFile.loadProductFile(fn,mDataManager);

                    if (ret_flg == ConstantManager.RET_NO_FIELD_MANY) {
                        WarningDialog dialog = WarningDialog.newInstance("Количество полей в файле меньше чем указано в настройках");
                        dialog.show(getFragmentManager(),"WD");
                    }
                    if (ret_flg == ConstantManager.RET_ERROR) {
                        WarningDialog dialog = WarningDialog.newInstance("Ошибка при загрузке файла данных\n"+mDataManager.getLastError());
                        dialog.show(getFragmentManager(),"WD");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    FirebaseCrash.report(e);
                    mLastError = e;
                    cancel(true);
                    return null;
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            hideProgress();
           // System.out.println(new Date());

            String msg;
            if (resultSearchFlag) {
                msg = "Скачан файл с товаром:"+fn+"\nсозданный : "+Func.getDateTimeToStr(createDate,"dd.MM.yyyy HH:mm")
                        +"\nи измененный : "+Func.getDateTimeToStr(modifidDate,"dd.MM.yyyy HH:mm");
            } else {
                msg = "Файл "+fn+" отсутствует на GD";
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.app_name)
                    .setMessage(msg)
                    .setCancelable(false)
                    .setNegativeButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .create();
            builder.show();
        }

        @Override
        protected void onCancelled() {
            if (mLastError != null) {
                hideProgress();
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                }else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MainActivity.REQUEST_AUTHORIZATION);

                } else if (mLastError instanceof GoogleAuthIOException) {
                    ErrorDialog(mLastError,"Ошибка аудентификации приложения");
                } else {
                    Log.d(TAG,"Error "+mLastError);
                    ErrorDialog(mLastError,null);
                }
            } else {
                Log.d(TAG,"Request cancelled.");
            }
        }

        private List getList() throws IOException {
            List rec = new ArrayList();
            Drive.Files.List listRequest = mService.files().list();
            do {
                FileList fList = listRequest.execute();
                rec.addAll(fList.getFiles());
                listRequest.setPageToken(fList.getNextPageToken());
            }while (listRequest.getPageToken() != null && listRequest.getPageToken().length() > 0);
            return rec;
        }

    }

    public void ErrorDialog(Exception e,String msg){
        String er = e.getLocalizedMessage();
        if (msg != null ){
            er = msg;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ошибка")
                .setMessage(er)
                .setNegativeButton(R.string.button_close,null).create();
        builder.show();
    }

    // отправляем локально
    class NetLocalTask extends AsyncTask<Void,Void,Void> {
        private String urlServer;
        private String fname;

        private boolean result;
        private String resultMessage;

        private Exception mLastError = null;
        private java.io.File filePath = null;

        private int filetype;


        public NetLocalTask(String url,String fname,int filetype){
            this.urlServer = url;
            this.fname = fname;
            this.filetype = filetype;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "-------------" + System.currentTimeMillis();
            filePath = new java.io.File(fname);
            fname = filePath.getName();

            if (fname.toUpperCase().indexOf(".TXT") == -1){ fname = fname+".txt";}

            fname="_"+fname;


            if (filetype == ConstantManager.FILE_TYPE_PRODUCT) {
                fname = ConstantManager.PREFIX_FILE_TOVAR+fname;
            }
            if (filetype == ConstantManager.FILE_TYPE_EGAIS) {
                fname = ConstantManager.PREFIX_FILE_EGAIS+fname;
            }
            if (filetype == ConstantManager.FILE_TYPE_CHANGE_PRICE) {
                fname = ConstantManager.PREFIX_FILE_CHANGEPRICE+fname;
            }
            if (filetype == ConstantManager.FILE_TYPE_PRIHOD) {
                fname = ConstantManager.PREFIX_FILE_PRIHOD+fname;
            }
            if (filetype == ConstantManager.FILE_TYPE_ALCOMARK) {
                fname = ConstantManager.PREFIX_FILE_ALCOMARK+fname;
            }


            try {
                URL url = new URL(urlServer+"/upload");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");

                /* setRequestProperty */
                conn.setRequestProperty("Accept-Charset", "UTF-8");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Charset", "UTF-8");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+ boundary);

                /*
                fileName = request.getHeader ( "user-agent" ).contains ( "MSIE" ) ? URLEncoder.encode ( fileName, "utf-8") : MimeUtility.encodeWord ( fileName );
                response.setHeader ( "Content-disposition", "attachment; filename=\"" + fileName + "\"");
                */
                String fileName = URLEncoder.encode(fname, "UTF-8");
               // Log.d(TAG," Encoder :"+fileName);

                //Log.d(TAG,"URU : "+Uri.encode(fname));
                //Log.d(TAG," URU DECODE : "+Uri.decode(fileName));

                DataOutputStream ds = new DataOutputStream(conn.getOutputStream());
                ds.writeBytes(twoHyphens + boundary + lineEnd);
                ds.writeBytes("Content-Disposition: form-data; name=\"uploadedFile\";filename*=\"" +
                        fileName +"\"" + lineEnd);
                ds.writeBytes(lineEnd);

                FileInputStream fStream = new FileInputStream(filePath);

                int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];
                int length = -1;

                while((length = fStream.read(buffer)) != -1) {
                    ds.write(buffer, 0, length);
                }
                ds.writeBytes(lineEnd);
                ds.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                /* close streams */
                fStream.close();
                ds.flush();
                ds.close();

                if(conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    result = true;
                    resultMessage = conn.getResponseMessage();
                } else {
                    result = false;
                    resultMessage = conn.getResponseMessage();
                }

            } catch (Exception e) {
                e.printStackTrace();
                FirebaseCrash.report(e);
                mLastError = e;
                cancel(true);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d(TAG,urlServer+"/upload");
            Log.d(TAG,resultMessage);

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.app_name)
                    .setMessage("Выгружен файл ....")
                    .setCancelable(false)
                    .setNegativeButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            filePath.delete();
                        }
                    })
                    .create();
            builder.show();

        }

        @Override
        protected void onCancelled(Void aVoid) {
            if (mLastError != null ){
                if (mLastError.getMessage().indexOf("Connection refused") !=-1){
                    String msg = "В соединении отказано\n не доступен сервер";
                    ErrorDialog(mLastError, msg);
                } else {
                    ErrorDialog(mLastError, null);
                }
            }
        }
    }

    // получаем локально
    class GetLocalTask extends AsyncTask<Void,Void,Void>  {
        private String urlServer;
        private String fname;
        private java.io.File filePath;
        private Exception mLastError;

        private static final int BUFFER_SIZE = 4096;
        private boolean resultSearchFlag;

        public GetLocalTask(String url,String fname){
            this.urlServer = url;
            this.fname = fname;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String charset = "UTF-8";
            String path = mDataManager.getStorageAppPath();
            filePath = new java.io.File(path,fname);

            try {
                URL url = new URL(urlServer + "/download/" + fname);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Accept-Charset", charset);
                conn.setRequestMethod("GET");
                //conn.setDoOutput(true);
                conn.connect();
                Log.d(TAG,"METHOD :"+conn.getRequestMethod());

                int responseCode = conn.getResponseCode();
                Log.d(TAG,"GET Response Code :: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {

                    InputStream inputStream = conn.getInputStream();

                    FileOutputStream fos = new FileOutputStream(filePath);

                    int bytesRead = -1;
                    byte[] buffer = new byte[BUFFER_SIZE];
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }

                    fos.close();
                    inputStream.close();


                    resultSearchFlag = true;
                    WorkInFile workInFile = new WorkInFile(mDataManager.getPreferensManager().getCodeFile());
                    int ret_flg =  workInFile.loadProductFile(fname,mDataManager);

                    if (ret_flg == ConstantManager.RET_NO_FIELD_MANY) {
                        WarningDialog dialog = WarningDialog.newInstance("Количество полей в файле меньше чем указано в настройках");
                        dialog.show(getFragmentManager(),"WD");
                    }
                    if (ret_flg == ConstantManager.RET_ERROR) {
                        WarningDialog dialog = WarningDialog.newInstance("Ошибка при загрузке файла данных\n"+mDataManager.getLastError());
                        dialog.show(getFragmentManager(),"WD");
                    }

                }
                conn.disconnect();
            }catch(IOException e){
                e.printStackTrace();
                FirebaseCrash.report(e);
                mLastError = e;
                cancel(true);
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            hideProgress();
            // System.out.println(new Date());

            String msg;
            if (resultSearchFlag) {
                msg = "Скачан файл с товаром:"+fname;
            } else {
                msg = "Файл "+fname+" отсутствует на локальном сервере";
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.app_name)
                    .setMessage(msg)
                    .setCancelable(false)
                    .setNegativeButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .create();
            builder.show();
        }

        @Override
        protected void onCancelled() {
            if (mLastError != null ){
                hideProgress();
                Log.d(TAG,mLastError.getMessage());
                if (mLastError.getMessage().indexOf("Connection refused") !=-1){
                    String msg = "В соединении отказано\n не доступен сервер";
                    ErrorDialog(mLastError, msg);
                } else {
                    ErrorDialog(mLastError, null);
                }
            }
        }
    }

}
