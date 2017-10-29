package cav.lscaner.ui.activity;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import cav.lscaner.R;
import cav.lscaner.data.managers.DataManager;

public class SettingActivity extends PreferenceActivity {
    private DataManager mDataManager;

    private EditTextPreference mScalePref;
    private EditTextPreference mStoreFile;

    private EditTextPreference mFileDelimeter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);

        mDataManager = DataManager.getInstance();

        mScalePref = (EditTextPreference) findPreference("scale_prefix");

        mScalePref.setOnPreferenceChangeListener(mChangeListener);

        mStoreFile = (EditTextPreference) findPreference("file_store");
        mStoreFile.setOnPreferenceChangeListener(mChangeListener);

        mFileDelimeter = (EditTextPreference) findPreference("file_delimiter");
        mFileDelimeter.setOnPreferenceChangeListener(mChangeListener);

        mScalePref.setSummary(String.valueOf(mDataManager.getPreferensManager().getSizeScale()));
        mFileDelimeter.setSummary(mDataManager.getPreferensManager().getDelimiterStoreFile());
        mStoreFile.setSummary(mDataManager.getPreferensManager().getStoreFileName());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
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
            if (preference.getKey().equals("scale_prefix")){
                String l = (String) o;
                mScalePref.setSummary(l);
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
            return true;
        }
    };


}