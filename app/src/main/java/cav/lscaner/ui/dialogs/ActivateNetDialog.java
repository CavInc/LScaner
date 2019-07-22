package cav.lscaner.ui.dialogs;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cav.lscaner.R;
import cav.lscaner.data.managers.DataManager;
import cav.lscaner.data.network.Request;

public class ActivateNetDialog extends DialogFragment implements View.OnClickListener{
    private DataManager mDataManager;

    private EditText mPhone;
    private EditText mName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataManager = DataManager.getInstance();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.activate_net_dialog, null);

        mPhone = v.findViewById(R.id.active_phone);
        mName = v.findViewById(R.id.active_name);

        ((TextView) v.findViewById(R.id.active_device_id)).setText(mDataManager.getAndroidID());

        ((Button) v.findViewById(R.id.activate_dlg_ok)).setOnClickListener(this);
        ((Button) v.findViewById(R.id.activate_dlg_cancel)).setOnClickListener(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Ативация приложения")
                .setView(v);

        return builder.create();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.activate_dlg_cancel){
            dismiss();
        }
        if (v.getId() == R.id.activate_dlg_ok) {
            licenseRequest();
        }
    }

    private void licenseRequest(){
        final Request request = new Request();
        new Thread(new Runnable() {
            @Override
            public void run() {
                request.registryLicense(mPhone.getText().toString(),mName.getText().toString(),
                        mDataManager.getAndroidID());
            }
        }).start();
    }
}