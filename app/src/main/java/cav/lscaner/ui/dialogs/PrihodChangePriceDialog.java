package cav.lscaner.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cav.lscaner.R;
import cav.lscaner.data.models.StoreProductModel;
import cav.lscaner.utils.ConstantManager;

public class PrihodChangePriceDialog extends DialogFragment implements View.OnClickListener{

    private static final String POSITION_FILE_TYPE = "PFT";
    private static final String EDIT_FLG = "EDIT_FLG";
    private static final String POSITION_NAME = "POSITION_NAME";
    private static final String POSITION_PRICE = "POSITION_PRICE";
    private static final String POSITION_OSTATOK = "POSITION_OSTATOK";
    private static final String POSITION_BARCODE = "POSITION_BARCODE";
    private static final String POSITON_ARTICUL = "POSITION_ARTICUL";
    private static final String POSITION_QUANTITY = "POSITION_QUANTITY";
    private StoreProductModel mProductModel;
    private int mFileType;
    private boolean mEditFlg;

    private String mBarcode;
    private String mArticul;
    private String mGetName;
    private Double mGetOstatok;
    private Double mGetPrice;
    private Float mGetQuantity;


    private EditText mPrice; // поле ввода цены
    private EditText mQuantity; // количество
    private EditText mSumma; // сумма

    private PrihodChangePriceListener mListener;


    public static PrihodChangePriceDialog newInstance(StoreProductModel productModel,int fileType,boolean editFlg){
        Bundle args = new Bundle();
        args.putInt(POSITION_FILE_TYPE,fileType);
        args.putBoolean(EDIT_FLG,editFlg);
        args.putString(POSITION_NAME,productModel.getName());
        args.putDouble(POSITION_PRICE,productModel.getPrice());
        args.putDouble(POSITION_OSTATOK,productModel.getOstatok());
        args.putString(POSITION_BARCODE,productModel.getBarcode());
        args.putString(POSITON_ARTICUL,productModel.getArticul());
        args.putFloat(POSITION_QUANTITY,productModel.getQuantity());

        PrihodChangePriceDialog dialog = new PrihodChangePriceDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().size() != 0){
            mEditFlg = getArguments().getBoolean(EDIT_FLG);
            mFileType = getArguments().getInt(POSITION_FILE_TYPE);

            mGetName = getArguments().getString(POSITION_NAME,"");
            mEditFlg = getArguments().getBoolean(EDIT_FLG);
            mGetOstatok = getArguments().getDouble(POSITION_OSTATOK);
            mGetPrice = getArguments().getDouble(POSITION_PRICE);
            mBarcode = getArguments().getString(POSITION_BARCODE);
            mArticul = getArguments().getString(POSITON_ARTICUL);
            mGetQuantity = getArguments().getFloat(POSITION_QUANTITY);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v;
        String buildTitle;
        if (mFileType == ConstantManager.FILE_TYPE_CHANGE_PRICE) {
            buildTitle = "Переоценка";
            v = LayoutInflater.from(getActivity()).inflate(R.layout.change_price_dialog, null);
            ((TextView) v.findViewById(R.id.chpr_price)).setText("Текущая: "+mGetPrice);
            ((TextView) v.findViewById(R.id.chpr_ostatok)).setText("Остаток: "+mGetOstatok);
            mPrice = (EditText) v.findViewById(R.id.chpr_newprice);
            if (mEditFlg) {
                //mPrice.setText(String.valueOf(mGetPrice));
                mPrice.setHint(String.valueOf(mGetPrice));
            }
            mPrice.setOnEditorActionListener(mEditorActionListener);
        } else {
            buildTitle = "Поступление";
            v = LayoutInflater.from(getActivity()).inflate(R.layout.prihod_dialog, null);
            mPrice = (EditText) v.findViewById(R.id.qq_price);
            mQuantity = (EditText) v.findViewById(R.id.qq_quantity);
            mSumma = (EditText) v.findViewById(R.id.qq_summ);
            ((TextView) v.findViewById(R.id.qq_articul)).setText(mArticul);
            if (mEditFlg) {
                mPrice.setHint(String.valueOf(mGetPrice));
                mQuantity.setHint(String.valueOf(mGetQuantity));
                mSumma.setHint(String.valueOf(mGetQuantity*mGetPrice));
            }
        }

        TextView mName = (TextView) v.findViewById(R.id.qq_title);

        if (mGetName != null || mGetName.length() != 0) {
            mName.setText(mGetName);
        } else {
            mName.setText("Новый");
        }

        ((Button) v.findViewById(R.id.qq_bt_cancel)).setOnClickListener(this);
        ((Button) v.findViewById(R.id.qq_bt_ok)).setOnClickListener(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(buildTitle).setView(v);

        return builder.create();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.qq_bt_cancel) {
            if (mListener != null) {
                mListener.cancelButton();
            }
            dismiss();
        }
        if (view.getId() == R.id.qq_bt_ok) {
            storeData();
            dismiss();
        }
    }

    private void storeData() {
        if (mListener != null ) {
            Double price = 0.0;
            if (mPrice.getText().length() !=  0){
                price = Double.parseDouble(mPrice.getText().toString());
            }

            if (mFileType == ConstantManager.FILE_TYPE_CHANGE_PRICE) {
                StoreProductModel productModel = new StoreProductModel(mBarcode, mGetName, mArticul,price);
                mListener.changeQuantity(productModel);
            } else {
                Float qq;
                if (mQuantity.getText().length()!=0) {
                    qq = Float.valueOf(mQuantity.getText().toString());
                } else {
                    qq = 1f;
                }
                StoreProductModel productModel = new StoreProductModel(mBarcode, mGetName, mArticul,price,qq,0.0);
                mListener.changeQuantity(productModel);
            }

        }
    }

    public void setPrihodChangePriceListener (PrihodChangePriceListener listener){
        mListener = listener;
    }

    TextView.OnEditorActionListener mEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            if (actionId == EditorInfo.IME_ACTION_DONE || (keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                    keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER
                    && keyEvent.getRepeatCount() == 0)){
                storeData();
                dismiss();
                return true;
            }
            return false;
        }
    };

    public interface PrihodChangePriceListener {
        public void cancelButton();
        public void changeQuantity(StoreProductModel productModel);
    }

}