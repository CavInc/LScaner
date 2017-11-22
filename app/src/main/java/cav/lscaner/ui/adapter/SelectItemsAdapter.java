package cav.lscaner.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;



import java.util.List;

import cav.lscaner.R;
import cav.lscaner.data.models.StoreProductModel;

public class SelectItemsAdapter extends ArrayAdapter<StoreProductModel> {
    private LayoutInflater mInflater;
    private int resLayout;

    public SelectItemsAdapter(Context context, int resource, List<StoreProductModel> objects) {
        super(context, resource, objects);
        resLayout = resource;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View row = convertView;
        if (row == null) {
            row = mInflater.inflate(resLayout, parent, false);
            holder = new ViewHolder();
            holder.mArticul = (TextView) row.findViewById(R.id.ssii_articul);
            holder.mName = (TextView) row.findViewById(R.id.ssii_name);
            row.setTag(holder);
        }else{
            holder = (ViewHolder)row.getTag();
        }

        StoreProductModel rec = getItem(position);
        holder.mArticul.setText(rec.getArticul());
        holder.mName.setText(rec.getName());

        return row;
    }

    public class ViewHolder {
        public TextView mName;
        public TextView mArticul;
    }
}