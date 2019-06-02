package cav.lscaner.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.BaseSwipeListAdapter;
import com.baoyz.swipemenulistview.ContentViewWrapper;

import java.util.ArrayList;
import java.util.List;

import cav.lscaner.R;
import cav.lscaner.data.models.ScannedFileModel;
import cav.lscaner.utils.ConstantManager;
import cav.lscaner.utils.Func;

public class ScannedSwipeFileAdapter extends BaseSwipeListAdapter {

    private int resLayout;
    private Context mContext;

    private List<ScannedFileModel> data;
    private ScannedSendListener mScannedSendListener;

    public ScannedSwipeFileAdapter(Context context, int resource,List<ScannedFileModel> data){
        this.data = data;
        resLayout = resource;
        mContext = context;
    }

    @Override
    public ContentViewWrapper getViewAndReusable(final int position, View convertView, ViewGroup parent) {
        boolean reUsable = true;
        if (convertView == null) {
            convertView = View.inflate(mContext, resLayout, null);
            convertView.setTag(new ViewHolder(convertView));
            reUsable = false;
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        ScannedFileModel record = data.get(position);

        holder.mName.setText(record.getName());
        holder.mDate.setText(Func.getDateToStr(record.getCreateDate(),"dd.MM.yyyy"));
        holder.mTime.setText(record.getTime());
        if (record.getType() == 0 ){
            holder.mType.setText("Товар");
            holder.mType.setTextColor(ContextCompat.getColor(mContext,R.color.app_green));
            holder.mIndicator.setBackgroundColor(ContextCompat.getColor(mContext,R.color.app_green));

        } else if (record.getType() == 1){
            holder.mType.setText("ЕГАИС");
            holder.mType.setTextColor(ContextCompat.getColor(mContext,R.color.app_blue));
            holder.mIndicator.setBackgroundColor(ContextCompat.getColor(mContext,R.color.app_blue));
        } else if (record.getType() == 2) {
            holder.mType.setText("Поступление");
            holder.mType.setTextColor(ContextCompat.getColor(mContext,R.color.app_gray_bt_dark));
            holder.mIndicator.setBackgroundColor(ContextCompat.getColor(mContext,R.color.app_gray_bt_dark));
        } else if (record.getType() == ConstantManager.FILE_TYPE_CHANGE_PRICE) {
            holder.mType.setText("Переоценка");
            holder.mType.setTextColor(ContextCompat.getColor(mContext,R.color.app_orange_normal));
            holder.mIndicator.setBackgroundColor(ContextCompat.getColor(mContext,R.color.app_orange_normal));
        } else {
            holder.mType.setText("Алкомарки");
            holder.mType.setTextColor(ContextCompat.getColor(mContext,R.color.app_yellow));
            holder.mIndicator.setBackgroundColor(ContextCompat.getColor(mContext,R.color.app_yellow));
        }


        if (record.isSelected()) {
            holder.mSelected.setVisibility(View.VISIBLE);
        } else {
            holder.mSelected.setVisibility(View.GONE);
        }

        holder.mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mScannedSendListener != null) {
                    mScannedSendListener.onSend(position);
                }
            }
        });

        return new ContentViewWrapper(convertView, reUsable);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public ScannedFileModel getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setDate(ArrayList<ScannedFileModel> data){
        data.clear();
        data.addAll(data);
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

        public ViewHolder(View view){
            mName = view.findViewById(R.id.sd_file_name);
            mDate = view.findViewById(R.id.sd_file_date);
            mTime = view.findViewById(R.id.sd_file_time);
            mType = view.findViewById(R.id.sd_file_type);
            mIndicator = view.findViewById(R.id.sd_file_indicator);
            mSelected = (ImageView) view.findViewById(R.id.sd_file_check);
            mSend = view.findViewById(R.id.sd_file_send);
        }
    }
}