package com.ibrahim.salesforce.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ibrahim.salesforce.R;
import com.ibrahim.salesforce.response.BooksSampleInfo;

import java.util.List;

/**
 * @author Rameel Hassan
 * Created 24/09/2022 at 8:48 pm
 */
public class BooksCollectionDetailsAdapter extends RecyclerView.Adapter<BooksCollectionDetailsAdapter.BooksCollectionDetailsViewHolder> {


    private List<BooksSampleInfo> mArrBookSellers;


    public BooksCollectionDetailsAdapter(List<BooksSampleInfo> mArrBookSellers) {
        this.mArrBookSellers = mArrBookSellers;
    }

    @NonNull
    @Override
    public BooksCollectionDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.books_sample_info,
                parent, false);

        return new BooksCollectionDetailsViewHolder(itemView);
    }

    private BooksSampleInfo getItem(int position){
        return mArrBookSellers.get(position);
    }

    @Override
    public void onBindViewHolder(@NonNull BooksCollectionDetailsViewHolder holder, int position) {
        holder.mTvItemSubjectName.setText(getItem(position).getClassName());
        holder.mTvItemSrlNo.setText(Integer.toString(position + 1));
        holder.mTvItemAssignedQty.setText(Integer.toString(getItem(position).getTotalQnt()));
        holder.mTvItemDeliveredQty.setText(Integer.toString(getItem(position).getDeliveredQnt()));
        holder.mTvItemRemainingQty.setText(Integer.toString(getItem(position).getRemainingQnt()));
    }

    @Override
    public int getItemCount() {
        if (mArrBookSellers!=null)
            return mArrBookSellers.size();
        return 0;
    }

    public class BooksCollectionDetailsViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvItemSubjectName;
        private TextView mTvItemSrlNo;
        private TextView mTvItemAssignedQty;
        private TextView mTvItemDeliveredQty;
        private TextView mTvItemRemainingQty;

        public BooksCollectionDetailsViewHolder(@NonNull View itemView) {
            super(itemView);

            mTvItemSrlNo = itemView.findViewById(R.id.tv_item_srl_no);
            mTvItemSubjectName = itemView.findViewById(R.id.tv_item_name);
            mTvItemAssignedQty = itemView.findViewById(R.id.tv_item_total_quantity);
            mTvItemDeliveredQty = itemView.findViewById(R.id.tv_item_delivered_qty);
            mTvItemRemainingQty = itemView.findViewById(R.id.tv_item_remaining_qty);
        }
    }
}
