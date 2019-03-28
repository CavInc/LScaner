package cav.lscaner.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.sax.SAXSource;

import cav.lscaner.R;
import cav.lscaner.data.models.ScannedFileModel;
import cav.lscaner.utils.ConstantManager;
import cav.lscaner.utils.Func;

public class ScannedFileAdapter extends ArrayAdapter<ScannedFileModel> {

    private static final String TAG = "SFA";
    private LayoutInflater mInflater;
    private int resLayout;

    private ScannedSendListener mScannedSendListener;

    public ScannedFileAdapter(Context context, int resource, List<ScannedFileModel> objects) {
        super(context, resource, objects);
        resLayout = resource;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View row = convertView;
        if (row == null) {
            row = mInflater.inflate(resLayout, parent, false);
            holder = new ViewHolder();
            holder.mName = (TextView) row.findViewById(R.id.sd_file_name);
            holder.mDate = (TextView) row.findViewById(R.id.sd_file_date);
            holder.mTime = (TextView) row.findViewById(R.id.sd_file_time);
            holder.mType = (TextView) row.findViewById(R.id.sd_file_type);
            holder.mIndicator = row.findViewById(R.id.sd_file_indicator);
            holder.mSelected = (ImageView) row.findViewById(R.id.sd_file_check);
            holder.mSend = row.findViewById(R.id.sd_file_send);
            row.setTag(holder);
        }else{
            holder = (ViewHolder)row.getTag();
        }

        ScannedFileModel record = getItem(position);
        holder.mName.setText(record.getName());
        holder.mDate.setText(Func.getDateToStr(record.getCreateDate(),"dd.MM.yyyy"));
        holder.mTime.setText(record.getTime());
        if (record.getType() == 0 ){
            holder.mType.setText("Товар");
            holder.mType.setTextColor(ContextCompat.getColor(getContext(),R.color.app_green));
            holder.mIndicator.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.app_green));

        } else if (record.getType() == 1){
            holder.mType.setText("ЕГАИС");
            holder.mType.setTextColor(ContextCompat.getColor(getContext(),R.color.app_blue));
            holder.mIndicator.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.app_blue));
        } else if (record.getType() == 2) {
            holder.mType.setText("Поступление");
            holder.mType.setTextColor(ContextCompat.getColor(getContext(),R.color.app_gray_bt_dark));
            holder.mIndicator.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.app_gray_bt_dark));
        } else if (record.getType() == ConstantManager.FILE_TYPE_CHANGE_PRICE) {
            holder.mType.setText("Переоценка");
            holder.mType.setTextColor(ContextCompat.getColor(getContext(),R.color.app_orange_normal));
            holder.mIndicator.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.app_orange_normal));
        } else {
            holder.mType.setText("Алкомарки");
            holder.mType.setTextColor(ContextCompat.getColor(getContext(),R.color.app_yellow));
            holder.mIndicator.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.app_yellow));
        }


        if (record.isSelected()) {
          holder.mSelected.setVisibility(View.VISIBLE);
        } else {
          holder.mSelected.setVisibility(View.GONE);
        }

        holder.mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"OPPS");
                if (mScannedSendListener != null) {
                    mScannedSendListener.onSend(position);
                }
            }
        });

        return row;
    }

    public void setDate(ArrayList<ScannedFileModel> data){
        this.clear();
        this.addAll(data);
    }

    public void setScannedSendListener(ScannedSendListener listener){
        mScannedSendListener = listener;
    }

    public interface ScannedSendListener {
        void onSend(int position);
    }

    private class ViewHolder {
        public TextView mName;
        public TextView mDate;
        public TextView mTime;
        public TextView mType;
        public View mIndicator;
        public ImageView mSelected;
        public ImageView mSend;
    }
}