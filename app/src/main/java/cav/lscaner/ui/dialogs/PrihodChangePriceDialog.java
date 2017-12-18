package cav.lscaner.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import cav.lscaner.R;
import cav.lscaner.data.models.StoreProductModel;
import cav.lscaner.utils.ConstantManager;

public class PrihodChangePriceDialog extends DialogFragment implements View.OnClickListener{

    private static final String POSITION_FILE_TYPE = "PFT";
    private static final String EDIT_FLG = "EDIT_FLG";
    private StoreProductModel mProductModel;
    private int mFileType;
    private boolean mEditFlg;

    public static PrihodChangePriceDialog newInstance(StoreProductModel productModel,int fileType,boolean editFlg){
        Bundle args = new Bundle();
        args.putInt(POSITION_FILE_TYPE,fileType);
        args.putBoolean(EDIT_FLG,editFlg);

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
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v;
        if (mFileType == ConstantManager.FILE_TYPE_CHANGE_PRICE) {
            v = LayoutInflater.from(getActivity()).inflate(R.layout.change_price_dialog, null);
        } else {
            v = LayoutInflater.from(getActivity()).inflate(R.layout.prihod_dialog, null);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);

        return builder.create();
    }

    @Override
    public void onClick(View view) {

    }
}