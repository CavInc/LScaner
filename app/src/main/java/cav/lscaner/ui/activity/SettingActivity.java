package cav.lscaner.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import cav.lscaner.R;
import cav.lscaner.data.managers.DataManager;

public class SettingActivity extends PreferenceActivity implements View.OnClickListener{
    private DataManager mDataManager;

    private EditTextPreference mScaleSize;
    private EditTextPreference mStoreFile;

    private EditTextPreference mFileDelimeter;
    private EditTextPreference mScalePrefix;

    private ListPreference mCodeFile;

    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);

        mDataManager = DataManager.getInstance();

        mScalePrefix = (EditTextPreference) findPreference("prefix_scale");
        mScalePrefix.setOnPreferenceChangeListener(mChangeListener);

        mScaleSize = (EditTextPreference) findPreference("scale_size");
        mScaleSize.setOnPreferenceChangeListener(mChangeListener);

        mStoreFile = (EditTextPreference) findPreference("file_store");
        mStoreFile.setOnPreferenceChangeListener(mChangeListener);

        mFileDelimeter = (EditTextPreference) findPreference("file_delimiter");
        mFileDelimeter.setOnPreferenceChangeListener(mChangeListener);

        mCodeFile = (ListPreference) findPreference("file_code");
        mCodeFile.setOnPreferenceChangeListener(mChangeListener);

        int code = mDataManager.getPreferensManager().getCodeFile();
        String[] hL = getResources().getStringArray(R.array.code_entries);
        mCodeFile.setSummary(hL[code-1]);


        mScaleSize.setSummary(String.valueOf(mDataManager.getPreferensManager().getSizeScale()));
        mFileDelimeter.setSummary(mDataManager.getPreferensManager().getDelimiterStoreFile());
        mStoreFile.setSummary(mDataManager.getPreferensManager().getStoreFileName());

        mScalePrefix.setSummary(mDataManager.getPreferensManager().getScalePrefixStr());
        mScalePrefix.setDefaultValue(mDataManager.getPreferensManager().getScalePrefixStr());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
        bar.getNavigationIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    Preference.OnPreferenceChangeListener mChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            if (preference.getKey().equals("scale_size")){
                String l = (String) o;
                mScaleSize.setSummary(l);
                mDataManager.getPreferensManager().setSizeScale(Integer.parseInt(l));
            }
            if (preference.getKey().equals("file_store")){
                String l = (String) o;
                mStoreFile.setSummary(l);
                mDataManager.getPreferensManager().setStoreFileName(l);
            }
            if (preference.getKey().equals("file_delimiter")){
                String l = (String) o;
                mFileDelimeter.setSummary(l);
                mDataManager.getPreferensManager().setDelimiterStoreFile(l);
            }
            if (preference.getKey().equals("prefix_scale")){
                String l = (String) o;
                mScalePrefix.setSummary(l);
                mDataManager.getPreferensManager().setScalePrefix(l);
            }
            if (preference.getKey().equals("file_code")){
                String l = (String) o;
                mDataManager.getPreferensManager().setCodeFile(Integer.valueOf(l));
                String[] hL = getResources().getStringArray(R.array.code_entries);
                mCodeFile.setSummary(hL[Integer.valueOf(l)-1]);
            }
            return true;
        }
    };


}