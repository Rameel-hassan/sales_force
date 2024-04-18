package com.ibrahim.salesforce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ibrahim.salesforce.R;
import com.ibrahim.salesforce.model.SelectedItemsModel;
import com.ibrahim.salesforce.response.GetItemsResponse;

import java.util.ArrayList;
import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemsHolder> {
    private List<GetItemsResponse.Item> mArrItems;
    private Context mContext;
    private List<GetItemsResponse.Item> mArrSelectedItems;
    private AddRemoveItem mAddRemoveItem;

    public ItemsAdapter(List<GetItemsResponse.Item> items, AddRemoveItem addRemoveItem) {
        this.mArrItems = items;
        mArrSelectedItems = new ArrayList<>();
        mAddRemoveItem = addRemoveItem;
    }

    @NonNull
    @Override
    public ItemsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_spn_items, viewGroup, false);
        ItemsHolder holder = new ItemsHolder(view);
        return holder;
    }
//another comment
    @Override
    public void onBindViewHolder(@NonNull final ItemsHolder itemsHolder, final int i) {
        itemsHolder.mTvItemName.setText(mArrItems.get(i).getItemName());
        itemsHolder.mCbItem.setChecked(mArrItems.get(i).isChecked());
        itemsHolder.mCbItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                double totalPrice = 0;
                if (isChecked) {
                    if (!itemsHolder.mNpQty.getText().toString().trim().isEmpty() && Integer.valueOf(itemsHolder.mNpQty.getText().toString()) > 0) {
                        mArrItems.get(i).setChecked(isChecked);
                        if(mArrItems.get(i).getPrice() > 0 )
                            totalPrice =  mArrItems.get(i).getPrice() * Integer.valueOf(itemsHolder.mNpQty.getText().toString());
                        mAddRemoveItem.addRemoveItem(new SelectedItemsModel(mArrItems.get(i).getItemId(), mArrItems.get(i).getItemName(), totalPrice,
                                itemsHolder.mNpQty.getText().toString()), true,i);
                    } else {
                        Toast.makeText(mContext, " Item quantity is required", Toast.LENGTH_SHORT).show();
                        itemsHolder.mCbItem.setChecked(false);
                    }
                } else {
                    mArrItems.get(i).setChecked(isChecked);
                    if(mArrItems.get(i).getPrice() > 0 )
                        totalPrice =  mArrItems.get(i).getPrice() * Integer.valueOf(itemsHolder.mNpQty.getText().toString());
                    mAddRemoveItem.addRemoveItem(new SelectedItemsModel(mArrItems.get(i).getItemId(), mArrItems.get(i).getItemName(), totalPrice, itemsHolder.mNpQty.getText().toString()), false,i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mArrItems.size();
    }

    public class ItemsHolder extends RecyclerView.ViewHolder {
        private TextView mTvItemName;
        private EditText mNpQty;
        private CheckBox mCbItem;

        public ItemsHolder(@NonNull View itemView) {
            super(itemView);
            mTvItemName = itemView.findViewById(R.id.tv_item_name);
            mNpQty = itemView.findViewById(R.id.np_qty);
            mCbItem = itemView.findViewById(R.id.cb_item);
        }
    }
}
