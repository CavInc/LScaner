package cav.lscaner.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.baoyz.swipemenulistview.BaseSwipeListAdapter;
import com.baoyz.swipemenulistview.ContentViewWrapper;

import java.util.ArrayList;
import java.util.List;

import cav.lscaner.R;
import cav.lscaner.data.managers.DataManager;
import cav.lscaner.data.models.FileFieldModel;
import cav.lscaner.data.models.ScannedDataModel;
import cav.lscaner.utils.ConstantManager;
import cav.lscaner.utils.Func;

public class ScannedSwipeListAdapter extends BaseSwipeListAdapter implements Filterable {
    private ArrayFilter mFilter;
    private final Object mLock = new Object();

    //private ArrayList<T> mOriginalValues;
    //private List<T> mObjects;

    private ArrayList<ScannedDataModel> mOriginalValues;

    private List<ScannedDataModel> data;
    private int resLayout;
    private Context mContext;

    private int priceFlg;
    private int ostatokFlg;


    public ScannedSwipeListAdapter (Context context, int resource,List<ScannedDataModel> data){
        this.data = data;
        resLayout = resource;
        mContext = context;
        DataManager manager = DataManager.getInstance();
        FileFieldModel fieldModel = manager.getPreferensManager().getFieldFileModel();
        priceFlg = fieldModel.getPrice();
        ostatokFlg = fieldModel.getOstatok();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public ContentViewWrapper getViewAndReusable(int position, View convertView, ViewGroup parent) {
        boolean reUsable = true;
        if (convertView == null) {
            convertView = View.inflate(mContext, resLayout, null);
            convertView.setTag(new ViewHolder(convertView));
            reUsable = false;
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        ScannedDataModel rec = data.get(position);

        if (rec.getName() == null) {
            holder.mName.setText("Новый (не опознан)");
        } else {
            holder.mName.setText(rec.getName());
        }
        if (holder.mQuantity !=null ) {
            holder.mQuantity.setText(Func.viewOstatok(Double.valueOf(rec.getQuantity())));
        }
        holder.mBarcode.setText(rec.getBarCode());

        if (holder.mPosId != null) {
            if (ostatokFlg == -1) {
                holder.mPosId.setVisibility(View.INVISIBLE);
            } else {
                holder.mPosId.setVisibility(View.VISIBLE);
                holder.mPosId.setText("Остаток: " + Func.viewOstatok(rec.getOstatok()));
            }
            if (rec.getFileType() == ConstantManager.FILE_TYPE_PRIHOD) {
                holder.mPosId.setVisibility(View.VISIBLE);
                holder.mPosId.setText("№: "+rec.getPosId());
            }
            if (rec.getFileType() == ConstantManager.FILE_TYPE_ALCOMARK) {
                holder.mPosId.setVisibility(View.VISIBLE);
                holder.mPosId.setText("№: "+rec.getPosId());
            }
        }

        if (holder.mArticul !=null) {
            if (rec.getArticul() == null || rec.getArticul().length() == 0) {
                holder.mArticul.setVisibility(View.INVISIBLE);
            } else {
                holder.mArticul.setVisibility(View.VISIBLE);
                holder.mArticul.setText("Код : " + rec.getArticul());
            }
        }

        if (holder.mPrice !=null) {
            if (priceFlg == -1) {
                holder.mPrice.setVisibility(View.INVISIBLE);
            } else {
                holder.mPrice.setVisibility(View.VISIBLE);
                holder.mPrice.setText("Цена : " + rec.getPrice());
            }
        }

        if (holder.mSumma !=null) {
            holder.mSumma.setText("Сумма : " + Func.roundUp(rec.getSumm(), 2));
        }

        if (holder.mOldPrice != null) {
            holder.mOldPrice.setText("Старая: "+rec.getOldPrice());
        }

        return new ContentViewWrapper(convertView, reUsable);
    }

    public void setData(ArrayList<ScannedDataModel> data) {
        this.data.clear();
        this.data.addAll(data);
    }

    public void remove(ScannedDataModel selModel) {
        data.remove(selModel);
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    private class ArrayFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                synchronized (mLock) {
                    mOriginalValues = new ArrayList<ScannedDataModel>(data);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                ArrayList<ScannedDataModel> list;
                synchronized (mLock) {
                    list = new ArrayList<ScannedDataModel>(mOriginalValues);
                }
                results.values = list;
                results.count = list.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();

                ArrayList<ScannedDataModel> values;
                synchronized (mLock) {
                    values = new ArrayList<ScannedDataModel>(mOriginalValues);
                }

                final int count = values.size();
                final ArrayList<ScannedDataModel> newValues = new ArrayList<ScannedDataModel>();

                for (int i = 0; i < count; i++) {
                    final ScannedDataModel value = values.get(i);
                    final String valueText = value.toString().toLowerCase();

                    // First match against the whole, non-splitted value
                    if (valueText.startsWith(prefixString)) {
                        newValues.add(value);
                    } else {
                        final String[] words = valueText.split(" ");
                        final int wordCount = words.length;

                        // Start at index 0, in case valueText starts with space(s)
                        for (int k = 0; k < wordCount; k++) {
                            if (words[k].startsWith(prefixString)) {
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //noinspection unchecked
            data = (List<ScannedDataModel>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

    private class ViewHolder {
        private TextView mName;
        private TextView mQuantity;
        private TextView mBarcode;
        private TextView mPosId;
        private TextView mArticul;
        private TextView mSumma;
        private TextView mPrice;
        private TextView mOldPrice;

        public ViewHolder(View view){
            mName = (TextView) view.findViewById(R.id.sc_item_name);
            mQuantity = (TextView) view.findViewById(R.id.sc_item_qa);
            mBarcode = (TextView) view.findViewById(R.id.sc_item_barcode);
            mPosId = (TextView) view.findViewById(R.id.sc_item_position);
            mArticul = (TextView) view.findViewById(R.id.sc_item_articul);
            mSumma = (TextView) view.findViewById(R.id.sc_item_summa);
            mPrice = (TextView) view.findViewById(R.id.sc_item_price);
            mOldPrice = (TextView) view.findViewById(R.id.sc_item_oldprice);
            view.setTag(this);
        }
    }
}