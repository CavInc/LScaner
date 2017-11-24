package cav.lscaner.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import cav.lscaner.R;
import cav.lscaner.data.models.StoreProductModel;
import cav.lscaner.ui.adapter.SelectItemsAdapter;

public class SelectItemsDialog extends DialogFragment implements AdapterView.OnItemClickListener{

    private static final String PRODUCT_DATA = "PRODUCT_DATA";
    private OnSelectItemsChangeListener mListener;
    private ArrayList<StoreProductModel> product;

    private ListView mListView;

    private SelectItemsAdapter mAdapter;

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (mListener != null ){
            mListener.onSelectItem(product.get(position));
        }
        dismiss();
    }

    public interface OnSelectItemsChangeListener {
        public void onSelectItem(StoreProductModel product);
    }

    public static SelectItemsDialog newInstance(ArrayList<StoreProductModel> data){
        Bundle args = new Bundle();
        args.putParcelableArrayList(PRODUCT_DATA,data);
        SelectItemsDialog dialog = new SelectItemsDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        product = getArguments().getParcelableArrayList(PRODUCT_DATA);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.selectitems_dialog, null);
        mListView = (ListView) v.findViewById(R.id.si_lv);
        mAdapter = new SelectItemsAdapter(getContext(),R.layout.select_items_item,product);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Выберите товар").setView(v);
        return builder.create();
    }

    public void setOnSelectItemsChangeListener(OnSelectItemsChangeListener listener){
        mListener = listener;
    }
}