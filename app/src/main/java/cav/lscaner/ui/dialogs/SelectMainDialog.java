package cav.lscaner.ui.dialogs;

import android.app.DialogFragment;

public class SelectMainDialog extends DialogFragment{


    public interface SelectMainDialogListener {
        public void selectedItem(int mode);
    }

    public static SelectMainDialog newInstance(){
        SelectMainDialog dialog = new SelectMainDialog();
        return dialog;
    }


}