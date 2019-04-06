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
import cav.lscaner.data.models.StoreProductModel;
import cav.lscaner.utils.Func;

public class StoreProductAdapter extends ArrayAdapter<StoreProductModel> {
    private LayoutInflater mInflater;
    private int resLayout;

    public StoreProductAdapter(Context context, int resource, List<StoreProductModel> objects) {
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
            holder.mName = (TextView) row.findViewById(R.id.spi_name);
            holder.mArticul = (TextView) row.findViewById(R.id.spi_articul);
            holder.mBarcode = (TextView) row.findViewById(R.id.spi_barcode);
            holder.mPrice = (TextView) row.findViewById(R.id.spi_price);
            holder.mOstatok = (TextView) row.findViewById(R.id.spi_ostatok);
            row.setTag(holder);
        }else{
            holder = (ViewHolder)row.getTag();
        }

        StoreProductModel rec = getItem(position);
        holder.mName.setText(rec.getName());
        holder.mArticul.setText(rec.getArticul());
        holder.mBarcode.setText(rec.getBarcode());
        holder.mPrice.setText("Цена: " + Func.roundUp(rec.getPrice(),2));
        holder.mOstatok.setText("Остаток: " + Func.roundUp(rec.getOstatok(),3));
        return row;
    }

    public void setData(ArrayList<StoreProductModel> data){
        this.clear();
        this.addAll(data);
    }

    private class ViewHolder {
        private TextView mName;
        private TextView mArticul;
        private TextView mBarcode;
        private TextView mPrice;
        private TextView mOstatok;
    }
}