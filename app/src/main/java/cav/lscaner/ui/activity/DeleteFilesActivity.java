package cav.lscaner.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;

import cav.lscaner.R;
import cav.lscaner.data.managers.DataManager;
import cav.lscaner.data.models.ScannedFileModel;
import cav.lscaner.ui.adapter.ScannedSwipeFileAdapter;

public class DeleteFilesActivity extends AppCompatActivity {
    private DataManager mDataManager;

    private SwipeMenuListView mListView;
    private ScannedSwipeFileAdapter mFileAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activate_deletefiles);
        mDataManager = DataManager.getInstance();

        mListView = findViewById(R.id.delete_lv);

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem retrunItem = new SwipeMenuItem(getApplicationContext());
                // set item background
                retrunItem.setBackground(R.drawable.swipe_button_bg_edit);
                // set item width
                retrunItem.setWidth(dp2px(90));
                retrunItem.setIcon(R.drawable.ic_redo_black_24dp);
                // set item title fontsize
                retrunItem.setTitleSize(18);
                // set item title font color
                retrunItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(retrunItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                // set item background
                deleteItem.setBackground(R.drawable.swipe_button_bg_edit);
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete_red_24dp);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        mListView.setMenuCreator(creator);
        mListView.setOnMenuItemClickListener(mMenuSwipeListener);

        setupToolBar();
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }


    public void setupToolBar(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
           actionBar.setTitle("Удаленные файлы");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        if (item.getItemId() == R.id.delete_file_all) {
            deleteAll();
        }
        return true;
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.deletefile_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI(){
        ArrayList<ScannedFileModel> model = mDataManager.getScannedFile(true);
        if (mFileAdapter == null){
            //mFileAdapter = new ScannedFileAdapter(this,R.layout.scanned_file_item,model);
            mFileAdapter = new ScannedSwipeFileAdapter(this,R.layout.scanned_file_item,model);
            //mFileAdapter.setScannedSendListener(mSendListener);
            //mFileAdapter.setScannedSendListener(mSendSwipeListener);
            mListView.setAdapter(mFileAdapter);
            mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        }else {
            mFileAdapter.setDate(model);
            mFileAdapter.notifyDataSetChanged();
        }
    }

    private ScannedFileModel selModel;

    SwipeMenuListView.OnMenuItemClickListener mMenuSwipeListener = new SwipeMenuListView.OnMenuItemClickListener(){

        @Override
        public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
            selModel = mFileAdapter.getItem(position);
            switch (index){
                case 0:
                    refreshRecord();
                    break;
                case 1:
                    deleteRecord();
                    break;
            }
            return false;
        }
    };

    private void deleteRecord() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Удаляем?")
                .setMessage("Вы уверены?")
                .setPositiveButton(R.string.button_yes,new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int witch) {
                        mDataManager.getDB().deleteFile(selModel.getId());
                        updateUI();
                    }
                })
                .setNegativeButton(R.string.button_cancel,null)
                .create();
        builder.show();
    }

    private void refreshRecord() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Восстанавливаем ?")
                .setMessage("Вы уверены ?")
                .setNegativeButton(R.string.button_cancel,null)
                .setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDataManager.getDB().returnDeleteFile(selModel.getId());
                        updateUI();
                    }
                })
                .show();
    }

    // удаляем все
    private void deleteAll() {
        mDataManager.getDB().deleteAll();
        updateUI();
    }


}