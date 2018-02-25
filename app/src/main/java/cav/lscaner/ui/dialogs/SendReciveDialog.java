package cav.lscaner.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import cav.lscaner.R;
import cav.lscaner.utils.ConstantManager;


public class SendReciveDialog extends DialogFragment implements View.OnClickListener{

    private OnSendReciveListener mSendReciveListener;

    public interface OnSendReciveListener {
        public void selectedItem(int item);
    }

    @Override
    public void onClick(View view) {
        int id = 0;
        switch (view.getId()){
            case R.id.ssr_gd:
               id = ConstantManager.GD;
                break;
            case R.id.ssr_ls:
                id = ConstantManager.LS;
                break;
        }
        if (mSendReciveListener != null){
            mSendReciveListener.selectedItem(id);
        }
        dismiss();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.select_sendrecive_dialog, null);

        v.findViewById(R.id.ssr_gd).setOnClickListener(this);
        v.findViewById(R.id.ssr_ls).setOnClickListener(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Выбор провайдера").setView(v);
        return builder.create();
    }

    public void setSendReciveListener (OnSendReciveListener listener){
        mSendReciveListener = listener;
    }
}