package cav.lscaner.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import cav.lscaner.R;

public class QueryQuantityDialog extends DialogFragment{

    private TextView mName;
    private EditText mQuantity;

    private QuantityChangeListener mQuantityChangeListener;

    public interface QuantityChangeListener {
        public void changeQuantity (Float quantity);
    }

    public static QueryQuantityDialog newInstans(){
        Bundle args = new Bundle();

        QueryQuantityDialog dialog = new QueryQuantityDialog();
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.queryquantity_dialog, null);

        mName = (TextView) v.findViewById(R.id.qq_title);
        mQuantity = (EditText) v.findViewById(R.id.qq_quantity);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Количество товара").setView(v);

        return builder.create();
    }
}