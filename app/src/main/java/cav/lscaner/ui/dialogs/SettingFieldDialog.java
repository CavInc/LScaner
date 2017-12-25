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

    private static final String MODE = "SFD_MODE";
    private CheckBox[] mCheckBoxes;

    private int mode;

    public static SettingFieldDialog newInstance(int mode){
        Bundle args = new Bundle();
        args.putInt(MODE,mode);
        SettingFieldDialog dialog = new SettingFieldDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mode = getArguments().getInt(MODE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.settin_field_dialog, null);

        ((Button) v.findViewById(R.id.sfd_bt_ok)).setOnClickListener(this);
        ((Button) v.findViewById(R.id.sfd_bt_cancel)).setOnClickListener(this);
        String title = null;

        switch (mode){
            case 0:
                title = "Поля базы данных";
                break;
            case 1:
                title = "Поля файла Товар";
                break;
            case 2:
                title = "Поля файла ЕГАИС";
                break;
            case 3:
                title = "Поля файла Переоценка";
                break;
            case 4:
                title = "Поля файла Поступление";
                break;
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title).setView(v);
        return builder.create();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sfd_bt_ok:
                dismiss();
                break;
            case R.id.sfd_bt_cancel:
                dismiss();
                break;
        }

    }
}