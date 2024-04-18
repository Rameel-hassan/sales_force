package com.ibrahim.salesforce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ibrahim.salesforce.R;
import com.ibrahim.salesforce.response.BookSellerID;
import com.ibrahim.salesforce.response.GetBookSellerResponse;

import java.util.List;

public class SelectedBookSellerItemsAdapter extends RecyclerView.Adapter<SelectedBookSellerItemsAdapter.BookSellersViewHolder> {
    private List<GetBookSellerResponse.BookSeller> mArrBookSellers;
    private Context context;
    private AddRemoveItem.AddRemoveBookSellers mAddRemoveBookSellers;
    private List<BookSellerID> BookSellerID;

    public SelectedBookSellerItemsAdapter(List<GetBookSellerResponse.BookSeller> items, AddRemoveItem.AddRemoveBookSellers addRemoveBookSellers, List<BookSellerID> BookSellerIDs) {
        this.mAddRemoveBookSellers = addRemoveBookSellers;
        this.mArrBookSellers = items;
        this.BookSellerID = BookSellerIDs;
    }

    @NonNull
    @Override
    public SelectedBookSellerItemsAdapter.BookSellersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.row_syllabus_item, viewGroup, false);
        SelectedBookSellerItemsAdapter.BookSellersViewHolder holder = new SelectedBookSellerItemsAdapter.BookSellersViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull final SelectedBookSellerItemsAdapter.BookSellersViewHolder itemsHolder, final int i) {
        for (int l = 0; l < BookSellerID.size(); l++) {
            if (mArrBookSellers.get(i).getSellerId() == BookSellerID.get(l).getID()) {
                mArrBookSellers.get(i).setChecked(true);
                itemsHolder.mCbItem.setChecked(mArrBookSellers.get(i).isChecked());
                mAddRemoveBookSellers.addRemoveItem(mArrBookSellers.get(i), true);
                break;
            }
        }
        itemsHolder.mTvItemName.setText(mArrBookSellers.get(i).getSellerName());
        itemsHolder.mCbItem.setChecked(mArrBookSellers.get(i).isChecked());
        itemsHolder.mCbItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mArrBookSellers.get(i).setChecked(true);
                    mAddRemoveBookSellers.addRemoveItem(mArrBookSellers.get(i), true);
                } else {
                    mArrBookSellers.get(i).setChecked(false);
                    for (int l = 0; l < BookSellerID.size(); l++) {
                        if (mArrBookSellers.get(i).getSellerId() == BookSellerID.get(l).getID()) {
                            BookSellerID.set(l, new BookSellerID(0));
                            break;
                        }
                    }
                    mAddRemoveBookSellers.addRemoveItem(mArrBookSellers.get(i), false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mArrBookSellers.size();
    }

    public class BookSellersViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvItemName;
        private CheckBox mCbItem;

        public BookSellersViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvItemName = itemView.findViewById(R.id.tv_item_name);
            mCbItem = itemView.findViewById(R.id.cb_item);
        }
    }
}
