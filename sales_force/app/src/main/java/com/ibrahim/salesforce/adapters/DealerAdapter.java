package com.app.salesforce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.salesforce.R;
import com.app.salesforce.response.CustomersRelatedtoSO;

import java.util.List;

public class DealerAdapter extends RecyclerView.Adapter<DealerAdapter.DealerViewHolder> {
    private List<CustomersRelatedtoSO> mArrDealers;
    private Context mContext;
    private AddRemoveItem.AddRemoveDealer mAddRemoveDealer;

    public DealerAdapter(List<CustomersRelatedtoSO> mArrDealers, AddRemoveItem.AddRemoveDealer addRemoveDealer) {
        this.mArrDealers = mArrDealers;
        this.mAddRemoveDealer = addRemoveDealer;
    }


    @NonNull
    @Override
    public DealerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();

        View view = LayoutInflater.from(mContext).inflate(R.layout.row_dealer_layout, viewGroup, false);
        DealerViewHolder holder = new DealerViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull DealerViewHolder dealerViewHolder, final int i) {
        dealerViewHolder.mTvItemName.setText(mArrDealers.get(i).getShopName());
        dealerViewHolder.mCbItem.setChecked(mArrDealers.get(i).getISActive());
        dealerViewHolder.mCbItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mAddRemoveDealer.addRemoveItem(mArrDealers.get(i), true);
                } else {
                    mAddRemoveDealer.addRemoveItem(mArrDealers.get(i), false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mArrDealers.size();
    }

    public class DealerViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvItemName;
        private CheckBox mCbItem;

        public DealerViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvItemName = itemView.findViewById(R.id.tv_item_name_dealer);
            mCbItem = itemView.findViewById(R.id.cb_item_dealer);
        }
    }
}
