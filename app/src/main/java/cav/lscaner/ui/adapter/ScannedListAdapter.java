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
import cav.lscaner.utils.Func;

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
            holder.mArticul = (TextView) row.findViewById(R.id.sc_item_articul);
            holder.mSumma = (TextView) row.findViewById(R.id.sc_item_summa);
            holder.mPrice = (TextView) row.findViewById(R.id.sc_item_price);
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
        //holder.mPosId.setText(String.valueOf(rec.getPosId())); // крзиция
        holder.mPosId.setText("Остаток: "+rec.getOstatok());
        if (rec.getArticul() == null || rec.getArticul().length() == 0 ){
            holder.mArticul.setVisibility(View.INVISIBLE);
        } else {
            holder.mArticul.setVisibility(View.VISIBLE);
            holder.mArticul.setText("Код : "+rec.getArticul());
        }
        holder.mPrice.setText("Цена : "+rec.getPrice());
        holder.mSumma.setText("Сумма : "+ Func.roundUp(rec.getSumm(),2));

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
        private TextView mArticul;
        private TextView mSumma;
        private TextView mPrice;
    }
}