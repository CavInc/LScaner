package cav.lscaner.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
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
            holder.mBarcode = (TextView) row.findViewById(R.id.sc_item_barcode);
            holder.mPosId = (TextView) row.findViewById(R.id.sc_item_position);
            row.setTag(holder);
        }else{
            holder = (ViewHolder)row.getTag();
        }
        ScannedDataModel rec = getItem(position);
        if (rec.getName() == null) {
            holder.mName.setText("Новый (не опознан)");
        } else {
            holder.mName.setText(rec.getName());
        }
        holder.mQuantity.setText(String.valueOf(rec.getQuantity()));
        holder.mBarcode.setText(rec.getBarCode());
        holder.mPosId.setText(rec.getPosId());
        return row;
    }

    public void setData(ArrayList<ScannedDataModel> data){
        this.clear();
        this.addAll(data);
    }

    private class ViewHolder {
        private TextView mName;
        private TextView mQuantity;
        private TextView mBarcode;
        private TextView mPosId;
    }
}