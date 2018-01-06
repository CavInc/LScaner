package cav.lscaner.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cav.lscaner.R;
import cav.lscaner.data.managers.DataManager;
import cav.lscaner.data.models.FieldOutFile;

public class SettingFieldDialog extends DialogFragment implements View.OnClickListener{

    private static final String MODE = "SFD_MODE";
    private CheckBox[] mCheckBoxes;

    private DataManager mDataManager;
    private int mode;

    private SettingFieldDialogListener mFieldDialogListener;

    private int[] activeField;

    public static SettingFieldDialog newInstance(int mode){
        Bundle args = new Bundle();
        args.putInt(MODE,mode);
        SettingFieldDialog dialog = new SettingFieldDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mode = getArguments().getInt(MODE);
        mDataManager = DataManager.getInstance();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.settin_field_dialog, null);

        mCheckBoxes = new CheckBox[9];
        mCheckBoxes[0] = (CheckBox) v.findViewById(R.id.sfd_barcode);
        mCheckBoxes[1] = (CheckBox) v.findViewById(R.id.sfd_articul);
        mCheckBoxes[2] = (CheckBox) v.findViewById(R.id.sfd_name);
        mCheckBoxes[3] = (CheckBox) v.findViewById(R.id.sfd_ostatok);
        mCheckBoxes[4] = (CheckBox) v.findViewById(R.id.sfd_price);
        mCheckBoxes[5] = (CheckBox) v.findViewById(R.id.sfd_base_price);
        mCheckBoxes[6] = (CheckBox) v.findViewById(R.id.sfd_egais);
        mCheckBoxes[7] = (CheckBox) v.findViewById(R.id.sfd_codetv);
        mCheckBoxes[8] = (CheckBox) v.findViewById(R.id.sfd_quantity);

        ((Button) v.findViewById(R.id.sfd_bt_ok)).setOnClickListener(this);
        ((Button) v.findViewById(R.id.sfd_bt_cancel)).setOnClickListener(this);
        String title = null;

        if (mode!=0) {
            mCheckBoxes[3].setVisibility(View.GONE);
            mCheckBoxes[2].setVisibility(View.GONE);
            mCheckBoxes[8].setVisibility(View.VISIBLE);
        }

        switch (mode){
            case 0:
                title = "Поля базы данных";
                activeField = mDataManager.getPreferensManager().getFieldFileActive();
                break;
            case 1:
                title = "Поля файла Товар";
                activeField = mDataManager.getPreferensManager().getFieldOutFile().getArrayIndex();
                break;
            case 2:
                title = "Поля файла ЕГАИС";
                activeField = mDataManager.getPreferensManager().getFieldOutEgaisFile().getArrayIndex();
                break;
            case 3:
                title = "Поля файла Переоценка";
                activeField = mDataManager.getPreferensManager().getFieldOutChangePriceFile().getArrayIndex();
                break;
            case 4:
                title = "Поля файла Поступление";
                activeField = mDataManager.getPreferensManager().getFieldOutPrixodFile().getArrayIndex();
                break;
        }

        setCheckItems(mode,activeField);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title).setView(v);
        return builder.create();
    }

    private void setCheckItems(int mode, int[] activeField) {
        Arrays.sort(activeField);
        for (int i = 0;i<activeField.length;i++){
            if (activeField[i] < 2) {
                mCheckBoxes[activeField[i]].setChecked(true);
            }else if(mode == 0) {
                mCheckBoxes[activeField[i]].setChecked(true);
            } else {
                if (activeField[i] == 2) mCheckBoxes[8].setChecked(true);
                else mCheckBoxes[activeField[i]+1].setChecked(true);
            }
        }
    }

    // собираемы выбранные элементы
    private int[] getSelect(){
        List<Integer> l = new ArrayList<>();
        if (mode == 0) {
            for (int i = 0; i<mCheckBoxes.length-1;i++){
                if (mCheckBoxes[i].isChecked()){
                    l.add(i);
                }
            }
        } else {
            for (int i = 0; i<mCheckBoxes.length-2;i++){
                if (i == 2 && mCheckBoxes[8].isChecked()){
                    l.add(i);
                }
                if (i<2){
                    if (mCheckBoxes[i].isChecked()) l.add(i);
                }else if(i>2){
                    if (mCheckBoxes[i+1].isChecked()) l.add(i);
                }
            }
        }
        int[] x = new int[l.size()];
        for (int i = 0;i<l.size();i++){
            x[i] = l.get(i);
        }
        return x;
    }

    private void storeData(){
        int [] x = getSelect();
        FieldOutFile l;

        switch (mode){
            case 0:
                mDataManager.getPreferensManager().setFieldFileActive(x);
                break;
            case 1:
                l = mDataManager.getPreferensManager().getFieldOutFile();
                l.setPositionItem(x);
                mDataManager.getPreferensManager().setFieldOutFile(l);
                //mDataManager.getPreferensManager().setFieldOutActive(x);
                break;
            case 2:
                l = mDataManager.getPreferensManager().getFieldOutEgaisFile();
                l.setPositionItem(x);
                mDataManager.getPreferensManager().setFieldOutEgaisFile(l);
                //mDataManager.getPreferensManager().setFieldEGAISActive(x);
                break;
            case 3:
                l = mDataManager.getPreferensManager().getFieldOutChangePriceFile();
                l.setPositionItem(x);
                mDataManager.getPreferensManager().setFieldOutChangePriceFile(l);
                //mDataManager.getPreferensManager().setFieldChangePriceActive(x);
                break;
            case 4:
                l = mDataManager.getPreferensManager().getFieldOutPrixodFile();
                l.setPositionItem(x);
                mDataManager.getPreferensManager().setFieldOutPrixodFile(l);
                //mDataManager.getPreferensManager().setFieldPrihoxPriceActive(x);
                break;
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sfd_bt_ok:
                storeData();
                if (mFieldDialogListener != null) mFieldDialogListener.onPostitiveButton();
                dismiss();
                break;
            case R.id.sfd_bt_cancel:
                if (mFieldDialogListener != null) mFieldDialogListener.onNegativeButton();
                dismiss();
                break;
        }
    }

    public void setSettingFieldDialogListener (SettingFieldDialogListener listener){
        mFieldDialogListener = listener;
    }

    public interface SettingFieldDialogListener {
        public void onNegativeButton();
        public void onPostitiveButton();
    }
}