package cav.lscaner.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cav.lscaner.R;
import cav.lscaner.data.managers.DataManager;
import cav.lscaner.utils.Func;

public class ActivateDialog extends DialogFragment implements View.OnClickListener {

    private DataManager mDataManager;
    private EditText mAcivateCode;

    private ActivateDialogListener mDialogListener;

    private String deviceId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataManager = DataManager.getInstance();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.activate_dialog, null);
        mAcivateCode = (EditText) v.findViewById(R.id.activate_key_code);


        deviceId = mDataManager.getAndroidID();
        deviceId = deviceId.substring(deviceId.length()-8);

        ((TextView) v.findViewById(R.id.activate_code)).setText(deviceId);

        TextView activate_msg = ((TextView) v.findViewById(R.id.activate_link_msg));
        //activate_msg.setText(Html.fromHtml(getResources().getString(R.string.activate_msg)));
        activate_msg.setMovementMethod(LinkMovementMethod.getInstance());

        ((Button) v.findViewById(R.id.activate_dlg_ok)).setOnClickListener(this);
        ((Button) v.findViewById(R.id.activate_dlg_cancel)).setOnClickListener(this);

        if (!mDataManager.getPreferensManager().getDemo()) {
            mAcivateCode.setText(mDataManager.getPreferensManager().getRegistrationNumber());
        }

        mAcivateCode.setOnEditorActionListener(mEditorActionListener);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Активация")
                .setView(v);

        return builder.create();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.activate_dlg_cancel){
            dismiss();
        }
        if (view.getId() == R.id.activate_dlg_ok) {
            // здесь проверяем код и пишем если все ок
            // проверяем тот ли номер
            okClick();
        }
    }

    private void okClick(){
        String x = mAcivateCode.getText().toString();
        boolean flg;
        if (Func.checkSerialNumber(x,deviceId) ) {
            // сохраняем серийник и флаг что не недемо
            mDataManager.getPreferensManager().setRegistrationNumber(x);
            mDataManager.getPreferensManager().setDemo(false);
            flg = true;
        } else {
            mDataManager.getPreferensManager().setDemo(true);
            flg = false;
        }

        if (mDialogListener != null) {
            mDialogListener.activateState(flg);
        }

        dismiss();
    }

    public void setDialogListener (ActivateDialogListener listener){
        mDialogListener = listener;
    }

    TextView.OnEditorActionListener mEditorActionListener = new TextView.OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            if (actionId == EditorInfo.IME_ACTION_DONE  || (keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                    keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER
                    && keyEvent.getRepeatCount() == 0)){
                okClick();
            }
            return false;
        };
    };

    public interface ActivateDialogListener {
        public void activateState(boolean state);
    }
}