package cav.lscaner.ui.dialogs;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;

import cav.lscaner.R;
import cav.lscaner.data.managers.DataManager;
import cav.lscaner.data.models.GetLicenseModel;
import cav.lscaner.data.models.LicenseModel;
import cav.lscaner.data.network.Request;
import cav.lscaner.utils.ConstantManager;
import cav.lscaner.utils.Func;
import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.slots.PredefinedSlots;
import ru.tinkoff.decoro.watchers.FormatWatcher;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;

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

        MaskImpl mask = MaskImpl.createNonTerminated(PredefinedSlots.RUS_PHONE_NUMBER);
        mask.setHideHardcodedHead(true);
        FormatWatcher formatWatcher = new MaskFormatWatcher(mask);
        formatWatcher.installOn(mPhone);

        mName.setText(mDataManager.getPreferensManager().getLicenseRegistryName());
        mPhone.setText(mDataManager.getPreferensManager().getLicenseRegistryPhone());

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
            mDataManager.getPreferensManager().setLicenseRegistryName(mName.getText().toString());
            mDataManager.getPreferensManager().setLicenseRegistryPhone(PhoneNumberUtils.stripSeparators(mPhone.getText().toString()));
            licenseRequest();
        }
    }

    private void licenseRequest(){
        final Request request = new Request(mDataManager.getPreferensManager());
        new Thread(new Runnable() {
            @Override
            public void run() {
                String phone = PhoneNumberUtils.stripSeparators(mPhone.getText().toString());
                GetLicenseModel ret = request.registryLicense(phone, mName.getText().toString(),
                        mDataManager.getAndroidID());
                // если EXISTS_DEVICE_AND_CLIENT то запросим лицензию
                if (ret.getRequestServer().equals("EXISTS_DEVICE_AND_CLIENT")) {
                    LicenseModel license = request.getLicense(mDataManager.getAndroidID());
                    if (license.isStatus()) {
                        Func.storeLicense(mDataManager,license);
                    }
                }
                // если новое устройство то тоже запрос лицензии
                if (ret.getRequestServer().equals("NOT_DEVICE")) {
                    LicenseModel license = request.getLicense(mDataManager.getAndroidID());
                    if (license.isStatus()) {
                        Func.storeLicense(mDataManager,license);
                    }
                }
                // новый клиент и новое устройство
                if (ret.getRequestServer().equals("NEW_DEVICE_AND_CLIENT")){

                }
                dismiss();
            }
        }).start();
    }

    // сораняем лицензию
    private void storeLicense(LicenseModel licenseModel){
        mDataManager.getPreferensManager().setLicenseType(licenseModel.getLicenseType());
        mDataManager.getPreferensManager().setLicenseWorkDay(licenseModel.getLicenseDay());
        mDataManager.getPreferensManager().setLicenseActivate(licenseModel.getActionLicense());
        // так как мы получили лицензию то активуруем даже если она временная
        mDataManager.getPreferensManager().setDemo(false);
        mDataManager.getPreferensManager().setLicenseLastDayRefresh(Func.getDateToStr(new Date(),"yyyy-MM-dd"));
        if (licenseModel.getLicenseType() == ConstantManager.LICENSE_PERMANENT) {
            // постоянная
        }
    }

}