package cav.lscaner.ui.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.io.IOException;
import java.util.List;

import cav.lscaner.R;
import cav.lscaner.ui.activity.SettingActivity;

public class EditPrefCustomDialog extends DialogFragment {

    private static final String TITLE = "title";
    private static final String HINT = "hint";
    private static final String DATA_STR = "data_str";
    private CheckBox checkBox;
    private FrameLayout mFrameLayout;
    private EditText mEditText;
    private CompoundBarcodeView mBarcodeView;

    private String title;
    private String hint;
    private String serverStr;

    private EditPrefDialogListener mPrefDialogListener;
    private boolean preview = false;

    public static EditPrefCustomDialog newInstance(String title,String hint,String dataStr){
        Bundle args = new Bundle();
        args.putString(TITLE,title);
        args.putString(HINT,hint);
        args.putString(DATA_STR,dataStr);
        EditPrefCustomDialog dialog = new EditPrefCustomDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(TITLE);
            hint = getArguments().getString(HINT);
            serverStr = getArguments().getString(DATA_STR);
        }
    }

    public void setPrefDialogListener(EditPrefDialogListener prefDialogListener) {
        mPrefDialogListener = prefDialogListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.editprefcustom_dialog, null);
        mFrameLayout = v.findViewById(R.id.edit_frame_dialog);
        mEditText = v.findViewById(R.id.edit_dialog);
        mEditText.setHint(hint);
        if (serverStr != null) {
            mEditText.setText(serverStr);
        }

        mBarcodeView = v.findViewById(R.id.barcode_scan_dialog);

        checkBox = v.findViewById(R.id.edit_cb_dialog);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    mFrameLayout.setVisibility(View.VISIBLE);
                    startCamera();
                } else {
                    releaceCamera();
                    mFrameLayout.setVisibility(View.GONE);
                }
            }
        });

        mEditText.setOnEditorActionListener(mEditorActionListener);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setView(v)
                .setNegativeButton(R.string.button_cancel,null)
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mPrefDialogListener != null) {
                            mPrefDialogListener.onChange(mEditText.getText().toString());
                        }
                    }
                });

        return builder.create();
    }

    TextView.OnEditorActionListener mEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

            //Func.addLog(debugOutFile,"KEY EVENT  ac: "+actionId+" kv :"+keyEvent); // debug

            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (keyEvent.getAction() == KeyEvent.ACTION_DOWN
                            && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER
                            && keyEvent.getRepeatCount() == 0)) {
                if (mPrefDialogListener != null) {
                    mPrefDialogListener.onChange(mEditText.getText().toString());
                }
                dismiss();
                return true;
            }
            return false;
        }
    };

    private void startCamera(){
        try {
            iniCamera();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("MissingPermission")
    private void iniCamera() throws IOException {
        mBarcodeView.decodeContinuous(callback);
        mBarcodeView.resume();
        preview = true;
    }

    private void releaceCamera(){
        mBarcodeView.pause();
        preview = false;
    }

    private BarcodeCallback callback = new BarcodeCallback() {

        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                mEditText.setText(result.getText());
                releaceCamera();
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };


    public interface EditPrefDialogListener {
        void onChange(String data);
    }

}