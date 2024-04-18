package com.ibrahim.salesforce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ibrahim.salesforce.R;
import com.ibrahim.salesforce.model.SelectedItemsModel;

import java.util.List;

public class SelectedItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private List<SelectedItemsModel> mArrItems;
    private Context mContext;
    private AddRemoveItem mAddRemoveItem;

    private int mPosition;
    private SelectedItemsModel dataItem;

    RecyclerView.ViewHolder mViewHolder;

    public SelectedItemsAdapter(List<SelectedItemsModel> items, AddRemoveItem addRemoveItem) {
        this.mArrItems = items;
        mAddRemoveItem = addRemoveItem;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        if (i == TYPE_ITEM) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.row_selected_items, viewGroup, false);
            ItemsHolder holder = new ItemsHolder(view);
            return holder;
        } else if (i == TYPE_HEADER) {
            View viewHeader = LayoutInflater.from(mContext).inflate(R.layout.row_selected_items_header, viewGroup, false);
            VHHeader header = new VHHeader(viewHeader);
            return header;
        }

        throw new RuntimeException("there is no type that matches the type " + i + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        if (viewHolder instanceof ItemsHolder) {
            mViewHolder = viewHolder;
            //cast holder to VHItem and set data
            ((ItemsHolder) viewHolder).mTvItemName.setText(getItem(i).getItemName());
            ((ItemsHolder) viewHolder).mTvItemCode.setText(Integer.toString(getItem(i).getItemId()));
            ((ItemsHolder) viewHolder).mTvItemQty.setText(Integer.toString(getItem(i).getQty()));
            ((ItemsHolder) viewHolder).mTvItemPrice.setText(getItem(i).getItemPrice()+"");
            ((ItemsHolder) viewHolder).mIbRemoveItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAddRemoveItem.uncheckupdate(getItem(i), false);
                    mAddRemoveItem.addRemoveItem(getItem(i), false,i);
                }
            });
        } else if (viewHolder instanceof VHHeader) {
            //cast holder to VHHeader and set data for header.
        }
    }

    @Override
    public int getItemCount() {
        return mArrItems.size() + 1;
//        return mArrItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private SelectedItemsModel getItem(int position) {
        return mArrItems.get(position - 1);
    }

    public class ItemsHolder extends RecyclerView.ViewHolder {
        private TextView mTvItemName;
        private TextView mTvItemCode;
        private TextView mTvItemQty;
        private TextView mTvItemPrice;
        private ImageButton mIbRemoveItem;

        public ItemsHolder(@NonNull View itemView) {
            super(itemView);
            mTvItemName = itemView.findViewById(R.id.tv_item_name);
            mTvItemCode = itemView.findViewById(R.id.tv_item_code);
            mTvItemQty = itemView.findViewById(R.id.tv_item_qty);
            mTvItemPrice = itemView.findViewById(R.id.tv_item_price);
            mIbRemoveItem = itemView.findViewById(R.id.ib_remove_item);
        }
    }

    class VHHeader extends RecyclerView.ViewHolder {
        public VHHeader(View itemView) {
            super(itemView);
        }
    }
}
