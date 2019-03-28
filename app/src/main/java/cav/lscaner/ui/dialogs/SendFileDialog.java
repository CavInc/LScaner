package cav.lscaner.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import cav.lscaner.R;

public class SendFileDialog extends DialogFragment implements View.OnClickListener{

    private SendFileDialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.select_send_dialog, null);
        v.findViewById(R.id.dialog_cloud_item).setOnClickListener(this);
        v.findViewById(R.id.dialog_send_item).setOnClickListener(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder.setView(v).setTitle("Выбор действия").create();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (mListener != null) {
            mListener.onSelectItem(id);
        }
        dismiss();
    }

    public void setListener(SendFileDialogListener listener) {
        mListener = listener;
    }

    public interface SendFileDialogListener {
        void onSelectItem(int item);
    }
}