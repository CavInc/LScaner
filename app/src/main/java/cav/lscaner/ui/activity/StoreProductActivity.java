package cav.lscaner.ui.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_product);
        mDataManager  = DataManager.getInstance();

        mListView = (ListView) findViewById(R.id.store_p_lv);

        ArrayList<StoreProductModel> models = mDataManager.getStoreProdect();
        StoreProductAdapter adapter = new StoreProductAdapter(this,R.layout.store_product_item,models);
        mListView.setAdapter(adapter);

        setupToolBar();
    }

    public void setupToolBar(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

}
