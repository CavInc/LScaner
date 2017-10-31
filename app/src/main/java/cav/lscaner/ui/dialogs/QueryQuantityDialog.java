package cav.lscaner.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cav.lscaner.R;

public class QueryQuantityDialog extends DialogFragment implements View.OnClickListener{

    private static final String POSITION_NAME = "POSITION_NAME";
    private static final String POSITION_QUANTITY = "POSITION_QUANTITY";
    private static final String POSITION_OLD_QUANTITY = "POSUTUIN_OLD_QUANTITY";
    private static final String EDIT_FLG = "EDIT_FLG";

    private TextView mName;
    private EditText mQuantity;
    private TextView mOldQuantityTV;
    private Button mCancelBt;
    private Button mOkBt;

    private String mGetName;
    private Float mGetQuantity;
    private Float mOldQuantity;
    private Boolean mEditFlg;

    private QuantityChangeListener mQuantityChangeListener;

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.qq_bt_ok) {
            if (mQuantityChangeListener != null){
                Float qq;
                if (mQuantity.getText().length()!=0) {
                    qq = Float.valueOf(mQuantity.getText().toString());
                } else {
                    qq = 1f;
                }
                if (mEditFlg) {
                    mQuantityChangeListener.changeQuantity(qq);
                } else {
                    mQuantityChangeListener.changeQuantity(mOldQuantity + qq);
                }

            }

            dismiss();
        }
        if (view.getId() == R.id.qq_bt_cancel){
            dismiss();
        }
    }

    public interface QuantityChangeListener {
        public void changeQuantity (Float quantity);
    }

    public static QueryQuantityDialog newInstans(String name,Float qunatity,Float oldQuantity,boolean editFlg){
        Bundle args = new Bundle();
        args.putString(POSITION_NAME,name);
        args.putFloat(POSITION_QUANTITY,qunatity);
        args.putFloat(POSITION_OLD_QUANTITY,oldQuantity);
        args.putBoolean(EDIT_FLG,editFlg);
        QueryQuantityDialog dialog = new QueryQuantityDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGetName = getArguments().getString(POSITION_NAME,"");
            mGetQuantity = getArguments().getFloat(POSITION_QUANTITY);
            mOldQuantity = getArguments().getFloat(POSITION_OLD_QUANTITY);
            mEditFlg = getArguments().getBoolean(EDIT_FLG);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.queryquantity_dialog, null);

        mName = (TextView) v.findViewById(R.id.qq_title);
        mQuantity = (EditText) v.findViewById(R.id.qq_quantity);
        mOldQuantityTV = (TextView) v.findViewById(R.id.qq_old_quantity);

        mCancelBt = (Button) v.findViewById(R.id.qq_bt_cancel);
        mOkBt = (Button) v.findViewById(R.id.qq_bt_ok);

        mCancelBt.setOnClickListener(this);
        mOkBt.setOnClickListener(this);

        if (mGetName != null || mGetName.length() != 0) {
            mName.setText(mGetName);
        } else {
            mName.setText("Новый");
        }
        if (mGetQuantity != null && mGetQuantity !=0) {
            mQuantity.setText(String.valueOf(mGetQuantity));
        }

        if (mOldQuantity == 0) {
            mOldQuantityTV.setVisibility(View.GONE);
        } else {
            mOldQuantityTV.setText(mOldQuantity+"- уже добавлено");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Количество товара")
                .setView(v);
        /*
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int witch) {
                        if (mQuantityChangeListener != null){
                            Float qq = Float.valueOf(mQuantity.getText().toString());
                            mQuantityChangeListener.changeQuantity(mOldQuantity+qq);
                        }

                    }
                })
                .setNegativeButton(R.string.button_cancel,null);
                */

        mQuantity.requestFocus();

        return builder.create();
    }

    public void setQuantityChangeListener (QuantityChangeListener listener){
        mQuantityChangeListener = listener;
    }
}