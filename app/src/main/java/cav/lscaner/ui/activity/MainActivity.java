package cav.lscaner.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.ArrayList;

import cav.lscaner.R;
import cav.lscaner.data.managers.DataManager;
import cav.lscaner.data.models.ScannedFileModel;
import cav.lscaner.ui.adapter.ScannedFileAdapter;
import cav.lscaner.ui.dialogs.AddEditNameFileDialog;
import cav.lscaner.ui.dialogs.SelectMainDialog;
import cav.lscaner.utils.ConstantManager;
import cav.lscaner.utils.Func;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener{

    private FloatingActionButton mFAB;
    private ListView mListView;

    private DataManager mDataManager;

    private ScannedFileAdapter mFileAdapter;

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
            AddEditNameFileDialog dialog = AddEditNameFileDialog.newInstance();
            dialog.setAddEditNameFileListener(mAddEditNameFileListener);
            dialog.show(getSupportFragmentManager(),"AddFile");
        }

    }

    private AddEditNameFileDialog.AddEditNameFileListener mAddEditNameFileListener = new AddEditNameFileDialog.AddEditNameFileListener() {
        @Override
        public void changeName(String value) {
            String cdate = Func.getNowDate("yyyy-MM-dd");
            String ctime = Func.getNowTime();
            mDataManager.getDB().addFileName(value,cdate,ctime);
            updateUI();
        }
    };

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

        Intent intent = new Intent(this,ScanActivity.class);
        intent.putExtra(ConstantManager.SELECTED_FILE,mFileAdapter.getItem(position).getId());
        startActivity(intent);
    }

    private int selIdFile;

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        ScannedFileModel model = (ScannedFileModel) adapterView.getItemAtPosition(position);
        selIdFile = model.getId();
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
                deleteRecord(selIdFile);
            }
            if (index == R.id.dialog_edit_item) {
                // редактируем заголовок
                Toast.makeText(MainActivity.this,"А тут будет диалог редактирования заголовка файла",Toast.LENGTH_LONG).show();
            }
            if (index == R.id.dialog_send_item) {
                // отправляем наружу
                if (!mDataManager.isOnline()){
                    // показываем что нет сети
                    return;
                }
                // показываем окно с выбором куда отправлять
                Toast.makeText(MainActivity.this,"А тут будет диалог спрашивающий куда отправить",Toast.LENGTH_LONG).show();
            }
        }
    };

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

}
