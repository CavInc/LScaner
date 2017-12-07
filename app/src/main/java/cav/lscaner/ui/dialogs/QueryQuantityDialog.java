package cav.lscaner.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cav.lscaner.R;
import cav.lscaner.data.managers.DataManager;
import cav.lscaner.data.models.StoreProductModel;
import cav.lscaner.utils.Func;

public class QueryQuantityDialog extends DialogFragment implements View.OnClickListener{

    private static final String POSITION_NAME = "POSITION_NAME";
    private static final String POSITION_QUANTITY = "POSITION_QUANTITY";
    private static final String POSITION_OLD_QUANTITY = "POSUTUIN_OLD_QUANTITY";
    private static final String EDIT_FLG = "EDIT_FLG";
    private static final String POSITION_PRICE = "POSITION_PRICE";
    private static final String POSITION_OSTATOK = "POSITION_OSTATOK";
    private static final String POSITION_BARCODE = "POSITION_BARCODE";
    private static final String POSITON_ARTICUL = "POSITION_ARTICUL";

    private TextView mName;
    private EditText mQuantity;
    private TextView mOldQuantityTV;
    private Button mCancelBt;
    private Button mOkBt;

    private String mGetName;
    private Float mGetQuantity;
    private Float mOldQuantity;
    private Boolean mEditFlg;

    private String mBarcode;
    private String mArticul;

    private Double mOstatok;
    private Double mPrice;

    private TextView mOstatokTV;
    private TextView mPriceTV;

    private QuantityChangeListener mQuantityChangeListener;
    private DataManager mDataManager;
    private String debugOutFile;

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.qq_bt_ok) {
            storeQuantiy();
            dismiss();
        }
        if (view.getId() == R.id.qq_bt_cancel){
            if (mQuantityChangeListener != null) {
                mQuantityChangeListener.cancelButton();
            }
            dismiss();
        }
    }

    public interface QuantityChangeListener {
       // public void changeQuantity (Float quantity);
        public void changeQuantity(Float quantity,StoreProductModel productModel);
        public void cancelButton();
    }


    public static QueryQuantityDialog newInstans(StoreProductModel productModel,Float qunatity,Float oldQuantity,boolean editFlg) {
        Bundle args = new Bundle();
        args.putString(POSITION_NAME,productModel.getName());
        args.putFloat(POSITION_QUANTITY,qunatity);
        args.putFloat(POSITION_OLD_QUANTITY,oldQuantity);
        args.putDouble(POSITION_PRICE,productModel.getPrice());
        args.putDouble(POSITION_OSTATOK,productModel.getOstatok());
        args.putBoolean(EDIT_FLG,editFlg);
        args.putString(POSITION_BARCODE,productModel.getBarcode());
        args.putString(POSITON_ARTICUL,productModel.getArticul());

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
            mOstatok = getArguments().getDouble(POSITION_OSTATOK);
            mPrice = getArguments().getDouble(POSITION_PRICE);
            mBarcode = getArguments().getString(POSITION_BARCODE);
            mArticul = getArguments().getString(POSITON_ARTICUL);
        }

        mDataManager = DataManager.getInstance(); // debug

        //debugOutFile = mDataManager.getStorageAppPath() + "/log_file.log"; // debug
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.queryquantity_dialog, null);

        mName = (TextView) v.findViewById(R.id.qq_title);
        mQuantity = (EditText) v.findViewById(R.id.qq_quantity);
        mOldQuantityTV = (TextView) v.findViewById(R.id.qq_old_quantity);

        mOstatokTV = (TextView) v.findViewById(R.id.qq_ostatok);
        mPriceTV = (TextView) v.findViewById(R.id.qq_price);

        mQuantity.setOnEditorActionListener(mEditorActionListener);


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
            mQuantity.setHint(String.valueOf(mGetQuantity));
        }

        if (mOldQuantity == 0) {
            mOldQuantityTV.setVisibility(View.GONE);
        } else {
            mOldQuantityTV.setText(mOldQuantity+" - уже добавлено");
        }

        mOstatokTV.setText("Остаток: "+ Func.viewOstatok(mOstatok));
        mPriceTV.setText("Цена: "+Func.roundUp(mPrice,2));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Количество товара")
                .setView(v);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        mQuantity.requestFocus();
    }

    public void setQuantityChangeListener (QuantityChangeListener listener){
        mQuantityChangeListener = listener;
    }

    TextView.OnEditorActionListener mEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
          //  Func.addLog(debugOutFile,"QQ KEY EVENT  ac: "+actionId+" kv :"+keyEvent); // debug
            if (actionId == EditorInfo.IME_ACTION_DONE  || (keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                    keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER
                    && keyEvent.getRepeatCount() == 0)){
                storeQuantiy();
                dismiss();
                return true;
            }
            return false;
        }
    };


    private void storeQuantiy (){
        if (mQuantityChangeListener != null){
            Float qq;
            if (mQuantity.getText().length()!=0) {
                qq = Float.valueOf(mQuantity.getText().toString());
            } else {
                qq = 1f;
            }
            if (! mEditFlg) {
                qq = mOldQuantity + qq;
            }
            StoreProductModel productModel = new StoreProductModel(mBarcode,mGetName,mArticul);
            mQuantityChangeListener.changeQuantity(qq,productModel);

           // Func.addLog(debugOutFile,"STORE QUANTITY : "+qq); // debug
        }
    }

}