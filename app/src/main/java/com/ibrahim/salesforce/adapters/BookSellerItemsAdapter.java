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
import com.ibrahim.salesforce.response.GetBookSellerResponse;

import java.util.List;

public class BookSellerItemsAdapter extends RecyclerView.Adapter<BookSellerItemsAdapter.BookSellersViewHolder> {

    private List<GetBookSellerResponse.BookSeller> mArrBookSellers;
    private Context context;
    private AddRemoveItem.AddRemoveBookSellers mAddRemoveBookSellers;

    public BookSellerItemsAdapter(List<GetBookSellerResponse.BookSeller> items, AddRemoveItem.AddRemoveBookSellers addRemoveBookSellers) {
        this.mAddRemoveBookSellers = addRemoveBookSellers;
        this.mArrBookSellers = items;
    }

    @NonNull
    @Override
    public BookSellersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.row_syllabus_item, viewGroup, false);
        BookSellersViewHolder holder = new BookSellersViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull final BookSellersViewHolder itemsHolder, final int i) {
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