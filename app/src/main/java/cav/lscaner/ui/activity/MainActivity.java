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
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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


import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
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
import cav.lscaner.ui.dialogs.SendFileDialog;
import cav.lscaner.ui.dialogs.SendReciveDialog;
import cav.lscaner.ui.dialogs.WarningDialog;
import cav.lscaner.utils.ConstantManager;
import cav.lscaner.utils.Func;
import cav.lscaner.utils.SwipeDetector;
import cav.lscaner.utils.WorkInFile;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener,
        EasyPermissions.PermissionCallbacks{

    private static final String TAG = "MAIN";
    private static final int WRITE_FILE = 1012;
    private static final int READ_FILE = 1010;

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
            Toast.makeText(MainActivity.this,
                    "А тут будет диалог спрашивающий откуда взять файл (имя файла в настройка)",
                    Toast.LENGTH_LONG).show();*/
            if (!mDataManager.isOnline()){
                // показываем что нет сети
                showNoNetwork();
                return false;
            }
            directionGD = READ_FILE;
            //
            if (mDataManager.getPreferensManager().getLocalServer() != null) {
                SendReciveDialog dialog = new SendReciveDialog();
                dialog.setSendReciveListener(mSendReciveListener);
                dialog.show(getSupportFragmentManager(),"SRD");
            } else {
                //requestData();
            }
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
                //pushGD();
            }
        }
        multiSelectChange();
        updateUI();
    }

    private void updateUI(){
        ArrayList<ScannedFileModel> model = mDataManager.getScannedFile();
        if (mFileAdapter == null){
            mFileAdapter = new ScannedFileAdapter(this,R.layout.scanned_file_item,model);
            mFileAdapter.setScannedSendListener(mSendListener);
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

    // позиция на которй нажали кнопку поделится
    private int selectPosition;

    ScannedFileAdapter.ScannedSendListener mSendListener = new ScannedFileAdapter.ScannedSendListener() {
        @Override
        public void onSend(int position) {
            selectPosition = position;
            SendFileDialog dialog = new SendFileDialog();
            dialog.setListener(mSendFileDialogListener);
            dialog.show(getFragmentManager(),"SL");
        }
    };

    private SendFileDialog.SendFileDialogListener mSendFileDialogListener = new SendFileDialog.SendFileDialogListener() {
        @Override
        public void onSelectItem(int item) {
            // отправляем наружу
            if (!mDataManager.isOnline()){
                // показываем что нет сети
                showNoNetwork();
                return;
            }

            switch (item){
                case R.id.dialog_cloud_item:
                    selModel = mFileAdapter.getItem(selectPosition);

                    WorkInFile workInFile = new WorkInFile(mDataManager.getPreferensManager().getCodeFile());
                    workInFile.saveFile(selModel.getId(),selModel.getName(),mDataManager,selModel.getType());
                    Log.d(TAG,workInFile.getSavedFile());
                    storeFileFullName = workInFile.getSavedFile();
                    fileType =  selModel.getType();

                    new NetLocalTask(mDataManager.getPreferensManager().getLocalServer(),
                            storeFileFullName,fileType).execute();
                    break;
                case R.id.dialog_send_item:
                    sendFileShare();
                    break;
            }
        }
    };

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
        // а здесь выделяем или снимаем выделение
        mFileAdapter.getItem(position).setSelected(!mFileAdapter.getItem(position).isSelected());
        mFileAdapter.notifyDataSetChanged();

        /*
        SelectMainDialog dialog = new SelectMainDialog();
        dialog.setSelectMainDialogListener(mSelectMainDialogListener);
        dialog.show(getFragmentManager(),"SELECT_DIALOG");
        */
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
                if (!mDataManager.isOnline()){
                    // показываем что нет сети
                    showNoNetwork();
                    return;
                }
                directionGD = WRITE_FILE;
                // сохраняем файл
                WorkInFile workInFile = new WorkInFile(mDataManager.getPreferensManager().getCodeFile());
                workInFile.saveFile(selModel.getId(),selModel.getName(),mDataManager,selModel.getType());
                Log.d(TAG,workInFile.getSavedFile());
                storeFileFullName = workInFile.getSavedFile();
                fileType =  selModel.getType();

                // TODO
                // показываем окно с выбором... если нет сохраненного локал сервере
                // то нваерно не показываем..

                SendReciveDialog dialog = new SendReciveDialog();
                dialog.setSendReciveListener(mSendReciveListener);
                dialog.show(getSupportFragmentManager(),"SRD");
                return;
/*

                // показываем окно с выбором куда отправлять
                // Toast.makeText(MainActivity.this,"А тут будет диалог спрашивающий куда отправить",Toast.LENGTH_LONG).show();
                // вызов отправки
                pushGD();
                */

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
                if (item == ConstantManager.FL) {
                    //TODO Добавить работу с файлами
                }
                if (item == ConstantManager.LS) {
                    new NetLocalTask(mDataManager.getPreferensManager().getLocalServer(),
                            storeFileFullName,fileType).execute();
                }
            }
            if (directionGD == READ_FILE ){
                if (item == ConstantManager.FL) {
                    // TODO Добавить работу с файлами
                }
                if (item == ConstantManager.LS) {
                    new GetLocalTask(mDataManager.getPreferensManager().getLocalServer(),
                            mDataManager.getPreferensManager().getStoreFileName()).execute();
                }
            }
        }
    };


    // оправить данные через поделится
    private void sendFileShare(){
        selModel = mFileAdapter.getItem(selectPosition);

        String fname = "_"+selModel.getName()+".txt";
        int filetype = selModel.getType();

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

        WorkInFile workInFile = new WorkInFile(mDataManager.getPreferensManager().getCodeFile());
        workInFile.saveFile(selModel.getId(),fname,mDataManager,selModel.getType());
        Log.d(TAG,workInFile.getSavedFile());
        storeFileFullName = workInFile.getSavedFile();
        fileType =  selModel.getType();

        Intent sendIntend = new Intent(Intent.ACTION_SEND);
        sendIntend.putExtra(Intent.EXTRA_TEXT, "Отправить файл");
        sendIntend.setType("text/plain");
        sendIntend.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+storeFileFullName));

        sendIntend.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (sendIntend.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(sendIntend, "Share File"));
        }

    }



    static final int REQUEST_PERMISSINO_WRITE_STORAGE = 1004;
    static final int REQUEST_PERMISSION_CAMERA = 1005;


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
