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
import android.widget.Toast;


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
    private EditText mEmail;

    private ActivateDialogListener mDialogListener;

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
        mEmail = v.findViewById(R.id.active_email);

        ((TextView) v.findViewById(R.id.active_device_id)).setText(mDataManager.getAndroidID());

        ((Button) v.findViewById(R.id.activate_dlg_ok)).setOnClickListener(this);
        ((Button) v.findViewById(R.id.activate_dlg_cancel)).setOnClickListener(this);

        MaskImpl mask = MaskImpl.createNonTerminated(PredefinedSlots.RUS_PHONE_NUMBER);
        mask.setHideHardcodedHead(true);
        FormatWatcher formatWatcher = new MaskFormatWatcher(mask);
        formatWatcher.installOn(mPhone);

        mName.setText(mDataManager.getPreferensManager().getLicenseRegistryName());
        mPhone.setText(mDataManager.getPreferensManager().getLicenseRegistryPhone());
        mEmail.setText(mDataManager.getPreferensManager().getLicenseEmail());

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
            if (mDataManager.getPreferensManager().getDemo()) {
                mDataManager.getPreferensManager().setLicenseRegistryName(mName.getText().toString());
                mDataManager.getPreferensManager().setLicenseRegistryPhone(PhoneNumberUtils.stripSeparators(mPhone.getText().toString()));
                mDataManager.getPreferensManager().setLicenseEmail(mEmail.toString());
                licenseRequest();
            } else {
                // есть лицензия отвязываемся
                deleteDeviceAndLicense(mDataManager.getAndroidID());
            }
        }
    }

    private void licenseRequest(){
        final Request request = new Request(mDataManager.getPreferensManager());
        new Thread(new Runnable() {
            @Override
            public void run() {
                String phone = PhoneNumberUtils.stripSeparators(mPhone.getText().toString());
                GetLicenseModel ret = request.registryLicense(phone, mName.getText().toString(),
                        mDataManager.getAndroidID(),mEmail.getText().toString());
                // если EXISTS_DEVICE_AND_CLIENT то запросим лицензию
                if (ret.getRequestServer().equals("EXISTS_DEVICE_AND_CLIENT")) {
                    LicenseModel license = request.getLicense(mDataManager.getAndroidID());
                    if (license.isStatus()) {
                        Func.storeLicense(mDataManager,license);
                        setActivateStatus(true);
                    }
                }
                // если новое устройство то тоже запрос лицензии
                if (ret.getRequestServer().equals("NOT_DEVICE")) {
                    if (ret.isLicense()) {
                        LicenseModel license = request.getLicense(mDataManager.getAndroidID());
                        if (license.isStatus()) {
                            Func.storeLicense(mDataManager, license);
                            mDataManager.getPreferensManager().setLicenseNewClient(false);
                            setActivateStatus(true);
                        }
                    } else {
                        // показываем что на устройстве и клиенте  нет лицензии
                        if (mDialogListener != null) {
                            mDialogListener.noLicense();
                        }
                    }
                }
                // новый клиент и новое устройство
                if (ret.getRequestServer().equals("NEW_DEVICE_AND_CLIENT")){
                    mDataManager.getPreferensManager().setLicenseNewClient(true);
                    mDataManager.getPreferensManager().setDemo(true);
                    mDataManager.getPreferensManager().setLicenseType(ConstantManager.LICENSE_NEW_CLIENT);
                    setActivateStatus(false);
                }

                dismiss();
            }
        }).start();
    }

    private void deleteDeviceAndLicense(final String deviceID){
        final Request request = new Request(mDataManager.getPreferensManager());
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean res = request.deleteDevice(deviceID);
                if (res) {
                    mDataManager.getPreferensManager().setDemo(true);
                    setActivateStatus(false);
                }
                dismiss();
            }
        }).start();

    }

    public void setDialogListener(ActivateDialogListener dialogListener) {
        mDialogListener = dialogListener;
    }

    private void setActivateStatus(boolean state){
        if (mDialogListener != null) {
            mDialogListener.activateState(state);
        }
    }

    public interface ActivateDialogListener {
        void activateState(boolean state);
        void noLicense();
    }
}