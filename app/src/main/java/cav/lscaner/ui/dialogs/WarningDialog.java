package cav.lscaner.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import cav.lscaner.R;

public class WarningDialog extends DialogFragment{

    private static final String MESSAGE = "MESSAGE";

    private String mMessage;
    private WarinindDialogListener mListener;

    private interface WarinindDialogListener {
        public void OnPositiveButton();
    }

    public static WarningDialog newInstance(String msg){
        Bundle args = new Bundle();
        args.putString(MESSAGE,msg);
        WarningDialog dialog = new WarningDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null){
            mMessage = getArguments().getString(MESSAGE);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Внимание !")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(mMessage)
                .setPositiveButton(R.string.button_close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mListener != null){
                            mListener.OnPositiveButton();
                        }
                        dismiss();
                    }
                });
        return builder.create();
    }

    public void setOnWarningDialogListener(WarinindDialogListener listener){
        mListener = listener;
    }


}