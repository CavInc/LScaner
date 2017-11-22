package cav.lscaner.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import cav.lscaner.data.models.StoreProductModel;

public class SelectItemsAdapter extends ArrayAdapter<StoreProductModel> {
    private LayoutInflater mInflater;
    private int resLayout;

    public SelectItemsAdapter(Context context, int resource, List<StoreProductModel> objects) {
        super(context, resource, objects);
        resLayout = resource;
        mInflater = LayoutInflater.from(context);
    }



    public class ViewHolder {
        public TextView mName;
        public TextView mArticul;
    }
}