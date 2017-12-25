package cav.lscaner.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import cav.lscaner.R;

public class SettingFieldDialog extends DialogFragment implements View.OnClickListener{

    private CheckBox[] mCheckBoxes;

    public static SettingFieldDialog newInstance(){
        SettingFieldDialog dialog = new SettingFieldDialog();
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.settin_field_dialog, null);

        ((Button) v.findViewById(R.id.sfd_bt_ok)).setOnClickListener(this);
        ((Button) v.findViewById(R.id.sfd_bt_cancel)).setOnClickListener(this);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Поля базы данных").setView(v);
        return builder.create();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sfd_bt_ok:
                break;
            case R.id.sfd_bt_cancel:
                break;
        }

    }
}