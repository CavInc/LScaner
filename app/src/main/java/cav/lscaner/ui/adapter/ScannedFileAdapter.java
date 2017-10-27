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
import cav.lscaner.data.models.ScannedFileModel;
import cav.lscaner.utils.Func;

public class ScannedFileAdapter extends ArrayAdapter<ScannedFileModel> {

    private LayoutInflater mInflater;
    private int resLayout;

    public ScannedFileAdapter(Context context, int resource, List<ScannedFileModel> objects) {
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
            holder.mName = (TextView) row.findViewById(R.id.sd_file_name);
            holder.mDate = (TextView) row.findViewById(R.id.sd_file_date);
            holder.mTime = (TextView) row.findViewById(R.id.sd_file_time);
            row.setTag(holder);
        }else{
            holder = (ViewHolder)row.getTag();
        }

        ScannedFileModel record = getItem(position);
        holder.mName.setText(record.getName());
        holder.mDate.setText(Func.getDateToStr(record.getCreateDate(),"dd.MM.yyyy"));
        holder.mTime.setText(record.getTime());
        return row;
    }

    public void setDate(ArrayList<ScannedFileModel> data){
        this.clear();
        this.addAll(data);
    }

    private class ViewHolder {
        public TextView mName;
        public TextView mDate;
        public TextView mTime;
    }
}