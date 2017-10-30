package cav.lscaner.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cav.lscaner.R;

public class AddEditNameFileDialog extends DialogFragment implements View.OnClickListener{
    private AddEditNameFileDialog INSTANSE = null;

    private AddEditNameFileListener mListener;

    private Button mCancelBt;
    private Button mOkBt;

    private EditText mName;

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.dialog_file_bt_ok){
            if (mName.getText().length()!=0) {
                if (mListener != null) {
                    mListener.changeName(mName.getText().toString());
                }
            }
            dismiss();
        }
        if (view.getId() == R.id.dialog_file_bt_ok) {
            dismiss();
        }

    }

    public interface AddEditNameFileListener {
        public void changeName(String value);
    }

    public static AddEditNameFileDialog newInstance(){
        AddEditNameFileDialog dialog = new AddEditNameFileDialog();
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.name_file_dialog, null);

        mName = (EditText) v.findViewById(R.id.dialog_file_name);
        mCancelBt = (Button) v.findViewById(R.id.dialog_file_bt_cancel);
        mOkBt = (Button) v.findViewById(R.id.dialog_file_bt_ok);

        mCancelBt.setOnClickListener(this);
        mOkBt.setOnClickListener(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Имя файла документа").setView(v);
                /*
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (mName.getText().length()!=0) {
                    if (mListener != null) {
                        mListener.changeName(mName.getText().toString());
                    }
                }
            }
        })
                .setNegativeButton(R.string.button_cancel,null);
         */
        return builder.create();
    }

    public void setAddEditNameFileListener(AddEditNameFileListener listener){
        mListener = listener;
    }
}