package cav.lscaner.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import cav.lscaner.R;

public class SettingFieldDialog extends DialogFragment{

    private CheckBox[] mCheckBoxes;

    public static SettingFieldDialog newInstance(){
        SettingFieldDialog dialog = new SettingFieldDialog();
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.settin_field_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Поля базы данных").setView(v);
        return builder.create();
    }
}