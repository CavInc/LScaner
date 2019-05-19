package cav.lscaner.ui.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

import cav.lscaner.R;
import cav.lscaner.data.managers.DataManager;
import cav.lscaner.data.models.StoreProductModel;
import cav.lscaner.ui.adapter.StoreProductAdapter;

public class StoreProductActivity extends AppCompatActivity {

    private DataManager mDataManager;
    private ListView mListView;

    private StoreProductAdapter adapter;
    private MenuItem searchItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_product);
        mDataManager  = DataManager.getInstance();

        mListView = (ListView) findViewById(R.id.store_p_lv);

        setupToolBar();
    }

    public void setupToolBar(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scan_menu, menu);
        MenuItem item = menu.findItem(R.id.scan_menu_photo);
        item.setVisible(false);
        MenuItem delItem = menu.findItem(R.id.scan_menu_clear);
        delItem.setVisible(true);

        searchItem = menu.findItem(R.id.scan_menu_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if(null!=searchManager ) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        } else {
            return true;
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                return  false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                /*
                int pos = adapter.getPosition(new StoreProductModel("",newText));
                if (pos != 0 & newText.length() != 0) {
                    //Log.d("SP","POS : "+pos);
                    mListView.setSelection(pos);
                }
                */
                if (newText.length() == 0){
                    adapter = null;
                    updateUI();
                } else {
                    adapter.getFilter().filter(newText);
                }
                return  true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem,new MenuItemCompat.OnActionExpandListener(){
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                //mAdapter.notifyDataSetChanged();
                return true;
            }
        });


        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI(){
        ArrayList<StoreProductModel> models = mDataManager.getStoreProdect();
        if (adapter == null) {
            adapter = new StoreProductAdapter(this, R.layout.store_product_item, models);
            mListView.setAdapter(adapter);
        } else {
            adapter.setData(models);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        if (item.getItemId() == R.id.scan_menu_clear) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Удаление")
                    .setMessage("Удаляем данные ?")
                    .setNegativeButton(R.string.button_cancel,null)
                    .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mDataManager.getDB().deleteStore(); // удаляем данные в списке товаров
                            updateUI();
                        }
                    })
                    .show();
        }
        return true;
    }

}
