package cav.lscaner.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import cav.lscaner.R;
import cav.lscaner.data.models.ScannedDataModel;

public class ScannedListAdapter extends ArrayAdapter<ScannedDataModel>{

    private LayoutInflater mInflater;
    private int resLayout;

    public ScannedListAdapter(Context context, int resource, List<ScannedDataModel> objects) {
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
            holder.mName = (TextView) row.findViewById(R.id.sc_item_name);
            holder.mQuantity = (TextView) row.findViewById(R.id.sc_item_qa);
            row.setTag(holder);
        }else{
            holder = (ViewHolder)row.getTag();
        }
        ScannedDataModel rec = getItem(position);
        holder.mName.setText(rec.getName());

        return row;
    }

    private class ViewHolder {
        private TextView mName;
        private TextView mQuantity;

    }
}