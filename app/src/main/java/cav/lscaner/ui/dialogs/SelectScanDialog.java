package cav.lscaner.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import cav.lscaner.R;

public class SelectScanDialog extends DialogFragment implements View.OnClickListener{

    private SelectScanDialogListener mScanDialogListener;

    @Override
    public void onClick(View view) {
        int id= view.getId();
        if (mScanDialogListener != null) {
            mScanDialogListener.selectedItem(id);
        }
        dismiss();
    }

    public interface SelectScanDialogListener {
        public void selectedItem(int item);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.select_scan_dialog, null);

        v.findViewById(R.id.ss_dialog_edit_item).setOnClickListener(this);
        v.findViewById(R.id.ss_dialog_del_item).setOnClickListener(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Выбор действия")
                .setView(v);
        return builder.create();
    }

    public void setOnSelectScanDialogListener (SelectScanDialogListener listener){
        mScanDialogListener = listener;
    }

}