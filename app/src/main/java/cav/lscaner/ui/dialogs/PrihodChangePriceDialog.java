package cav.lscaner.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import cav.lscaner.R;

public class PrihodChangePriceDialog extends DialogFragment implements View.OnClickListener{

    public static PrihodChangePriceDialog newInstance(){
        PrihodChangePriceDialog dialog = new PrihodChangePriceDialog();
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.change_price_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);

        return builder.create();
    }

    @Override
    public void onClick(View view) {

    }
}