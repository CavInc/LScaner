package cav.lscaner.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import cav.lscaner.R;
import cav.lscaner.utils.ConstantManager;

public class AddEditNameFileDialog extends DialogFragment implements View.OnClickListener{
    private static final String EDIT_NAME = "EDIT_NAME";
    private static final String EDIT_TYPE = "EDIT_TYPE";
    private static final String TAG = "AEND";
    private AddEditNameFileDialog INSTANSE = null;

    private AddEditNameFileListener mListener;

    private Button mCancelBt;
    private Button mOkBt;

    private EditText mName;

    private String nameFile;

    private RadioButton mTovar;
    private RadioButton mEGAIS;
    private RadioButton mPrihod;
    private RadioButton mChangePrice;

    private int type;

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.dialog_file_bt_ok){
            resultReturn();
            dismiss();
        }
        if (view.getId() == R.id.dialog_file_bt_cancel) {
            dismiss();
        }

    }

    TextView.OnEditorActionListener mNameActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            resultReturn();
            dismiss();
            return false;
        }
    };

    private void resultReturn(){
        if (mName.getText().length()!=0) {
            if (mListener != null) {
                int type = 0;
                if (mTovar.isChecked()) {
                    type = 0;
                }else if (mEGAIS.isChecked()){
                    type = 1;
                } else if (mPrihod.isChecked()) {
                    type = 2;
                } else {
                    type = 3;
                }
                mListener.changeName(mName.getText().toString(),type);
            }
        }
    }


    public interface AddEditNameFileListener {
        public void changeName(String value,int type_file);
    }

    public static AddEditNameFileDialog newInstance(String name,int type){
        Bundle args = new Bundle();
        args.putString(EDIT_NAME,name);
        args.putInt(EDIT_TYPE,type);
        AddEditNameFileDialog dialog = new AddEditNameFileDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!= null) {
            nameFile = getArguments().getString(EDIT_NAME);
            type = getArguments().getInt(EDIT_TYPE);
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.name_file_dialog, null);

        mName = (EditText) v.findViewById(R.id.dialog_file_name);
        mCancelBt = (Button) v.findViewById(R.id.dialog_file_bt_cancel);
        mOkBt = (Button) v.findViewById(R.id.dialog_file_bt_ok);

        mTovar = (RadioButton) v.findViewById(R.id.dialog_tovar);
        mEGAIS = (RadioButton) v.findViewById(R.id.dialog_egais);
        mPrihod = (RadioButton) v.findViewById(R.id.dialog_prixod);
        mChangePrice = (RadioButton) v.findViewById(R.id.dialog_changeprise);

        if (type == ConstantManager.FILE_TYPE_PRODUCT) {
            clearCheck();
            mTovar.setChecked(true);
        } else  if (type == ConstantManager.FILE_TYPE_EGAIS){
            clearCheck();
            mEGAIS.setChecked(true);
        } else if (type == ConstantManager.FILE_TYPE_PRIHOD) {
            clearCheck();
            mPrihod.setChecked(true);
        } else  {
            clearCheck();
            mChangePrice.setChecked(true);
        }

        mCancelBt.setOnClickListener(this);
        mOkBt.setOnClickListener(this);

        mTovar.setOnClickListener(mRbListener);
        mEGAIS.setOnClickListener(mRbListener);
        mPrihod.setOnClickListener(mRbListener);
        mChangePrice.setOnClickListener(mRbListener);

        mName.setText(nameFile);

        mName.setOnEditorActionListener(mNameActionListener);
        String title;
        if (nameFile == null || nameFile.length() == 0){
            title = "Новый документ";
        } else {
            title = "Редактировать документ";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title).setView(v);
        return builder.create();
    }

    private void clearCheck() {
        mTovar.setChecked(false);
        mEGAIS.setChecked(false);
        mChangePrice.setChecked(false);
        mPrihod.setChecked(false);
    }

    public void setAddEditNameFileListener(AddEditNameFileListener listener){
        mListener = listener;
    }

    View.OnClickListener mRbListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mTovar.setChecked(false);
            mEGAIS.setChecked(false);
            mChangePrice.setChecked(false);
            mPrihod.setChecked(false);
            switch (view.getId()){
                case R.id.dialog_tovar:
                    mTovar.setChecked(true);
                    break;
                case R.id.dialog_egais:
                    mEGAIS.setChecked(true);
                    break;
                case R.id.dialog_changeprise:
                    mChangePrice.setChecked(true);
                    break;
                case R.id.dialog_prixod:
                    mPrihod.setChecked(true);
                    break;
            }

        }
    };

}