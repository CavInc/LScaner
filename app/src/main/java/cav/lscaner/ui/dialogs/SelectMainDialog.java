package cav.lscaner.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import cav.lscaner.R;

public class SelectMainDialog extends DialogFragment implements View.OnClickListener{

    private SelectMainDialogListener mDialogListener;



    @Override
    public void onClick(View view) {
        int id= view.getId();
        if (mDialogListener != null) {
            mDialogListener.selectedItem(id);
        }
        dismiss();
    }

    public interface SelectMainDialogListener {
        public void selectedItem(int index);
    }

    public static SelectMainDialog newInstance(){
        SelectMainDialog dialog = new SelectMainDialog();
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.select_main_dialog, null);

        v.findViewById(R.id.dialog_send_item).setOnClickListener(this);
        v.findViewById(R.id.dialog_edit_item).setOnClickListener(this);
        v.findViewById(R.id.dialog_del_item).setOnClickListener(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder.setView(v).setTitle("Выбор действия").create();
    }

    public void setSelectMainDialogListener(SelectMainDialogListener listener){
        mDialogListener = listener;
    }
}