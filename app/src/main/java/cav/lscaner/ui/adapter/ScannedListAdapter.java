package cav.lscaner.ui.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

import cav.lscaner.data.models.ScannedDataModel;

public class ScannedListAdapter extends ArrayAdapter<ScannedDataModel>{

    public ScannedListAdapter(Context context, int resource, List<ScannedDataModel> objects) {
        super(context, resource, objects);
    }
}