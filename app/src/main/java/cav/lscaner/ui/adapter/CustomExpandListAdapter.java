package cav.lscaner.ui.adapter;

// Адаптер для раскрывающегося списка

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cav.lscaner.R;

public class CustomExpandListAdapter  extends BaseExpandableListAdapter {

    private ArrayList<ArrayList<String>> mGroups;

    private Context mContext;
    private List<? extends Map> mGroupData;
    private int mGroupLayout;
    private String[] mGroupFrom;
    private int[] mGroupTo;

    private List<? extends List> mChildData;
    private int mChildLayout;
    private String[] mChildFrom;
    private int[] mChildTo;

    private LayoutInflater mInflater;

    private GroupCallBackListener mGroupCallBackListener;

    public interface GroupCallBackListener {
        public void ClickSettingButton(int groupPosition);
    }

    public CustomExpandListAdapter(Context context, List<? extends Map> groupData, int groupLayout,
                                   String[] groupFrom, int[] groupTo, List<? extends List> childData,
                                   int childLayout, String[] childFrom, int[] childTo) {
        mContext = context;
        mGroupData = groupData;
        mGroupLayout = groupLayout;
        mGroupFrom = groupFrom;
        mGroupTo = groupTo;
        mChildData = childData;
        mChildLayout = childLayout;
        mChildFrom = childFrom;
        mChildTo = childTo;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {
        return mGroupData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mChildData.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroupData.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mChildData.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View v;
        if (convertView == null) {
            v = mInflater.inflate(mGroupLayout,null);
        } else {
            v = convertView;
        }
        ImageView settingButton = (ImageView) v.findViewById(R.id.elg_setting);


        switch (groupPosition) {
            case 0:
                v.setBackgroundResource(R.drawable.ligth);
                settingButton.setImageResource(R.drawable.settings_2_gray);
                break;
            case 1 :
                v.setBackgroundResource(R.drawable.gren);
                settingButton.setImageResource(R.drawable.settings_2_green);
                break;
            case 2:
                v.setBackgroundResource(R.drawable.blue);
                settingButton.setImageResource(R.drawable.settings_2_blue);
                break;
            case 3:
                v.setBackgroundResource(R.drawable.orange);
                settingButton.setImageResource(R.drawable.settings_2_orange);
                break;
            case 4:
                v.setBackgroundResource(R.drawable.dark);
                settingButton.setImageResource(R.drawable.settings_2_dark);
                break;
        }



        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mGroupCallBackListener != null) {
                    mGroupCallBackListener.ClickSettingButton(groupPosition);
                }

            }
        });

        bindView(v, mGroupData.get(groupPosition), mGroupFrom, mGroupTo);
        return v;
    }

    private void bindView(View view, Map<String, ?> data, String[] from, int[] to) {
        int len = to.length;

        for (int i = 0; i < len; i++) {
            TextView v = (TextView)view.findViewById(to[i]);
            if (v != null) {
                v.setText((String)data.get(from[i]));
            }
        }
    }

    private boolean lockEdit = false;

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View v;
        if (convertView == null){
            v = mInflater.inflate(mChildLayout,null);
        } else {
            v = convertView;
        }

        HashMap l = (HashMap) mChildData.get(groupPosition).get(childPosition);
        System.out.println(l);
        TextView tv = (TextView) v.findViewById(mChildTo[0]);
        String s = (String) l.get(mChildFrom[0]);
        tv.setText(s);

        lockEdit = false;

        EditText ed = (EditText) v.findViewById(mChildTo[1]);
        String vl = (String) l.get(mChildFrom[1]);
        if (!vl.equals("-1")) {
            ed.setText(vl);
        } else {
            ed.setText("");
        }

        // TODO нужно как то правильно передавать текущие индексы группы и позиции

        ed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (lockEdit ) {
                    if (editable.length() !=0) {
                        ((HashMap) mChildData.get(groupPosition).get(childPosition)).put("itemValue", editable.toString());
                    } else {
                        ((HashMap) mChildData.get(groupPosition).get(childPosition)).put("itemValue", "-1");
                    }
                }
            }
        });

        lockEdit = true;

        return v;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void setGroupCallBackListener (GroupCallBackListener listener){
        mGroupCallBackListener = listener;
    }


}