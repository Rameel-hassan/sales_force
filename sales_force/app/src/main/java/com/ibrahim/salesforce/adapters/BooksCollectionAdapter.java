package com.app.salesforce.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.salesforce.R;
import com.app.salesforce.response.BooksSampleInfo;

import java.util.List;

/**
 * @author Rameel Hassan
 * Created 24/09/2022 at 8:48 pm
 */
public class BooksCollectionAdapter extends RecyclerView.Adapter<BooksCollectionAdapter.BooksCollectionViewHolder> {


    private List<BooksSampleInfo> mArrBookSellers;
    private OnItemClickListener listener;


    public BooksCollectionAdapter(List<BooksSampleInfo> mArrBookSellers, OnItemClickListener listener) {
        this.mArrBookSellers = mArrBookSellers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BooksCollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.books_sample_info,
                parent, false);

        return new BooksCollectionViewHolder(itemView);
    }

    private BooksSampleInfo getItem(int position){
        return mArrBookSellers.get(position);
    }


    @Override
    public void onBindViewHolder(@NonNull BooksCollectionViewHolder holder, int position) {
        holder.mTvItemSubjectName.setText(getItem(position).getSubjectName());
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

    public class BooksCollectionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTvItemSubjectName;
        private TextView mTvItemSrlNo;
        private TextView mTvItemAssignedQty;
        private TextView mTvItemDeliveredQty;
        private TextView mTvItemRemainingQty;


        public BooksCollectionViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mTvItemSrlNo = itemView.findViewById(R.id.tv_item_srl_no);
            mTvItemSubjectName = itemView.findViewById(R.id.tv_item_name);
            mTvItemAssignedQty = itemView.findViewById(R.id.tv_item_total_quantity);
            mTvItemDeliveredQty = itemView.findViewById(R.id.tv_item_delivered_qty);
            mTvItemRemainingQty = itemView.findViewById(R.id.tv_item_remaining_qty);

        }

        @Override
        public void onClick(View view) {
            int position = getAbsoluteAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position);
            }
        }
    }
    public interface OnItemClickListener {
         void onItemClick(int position);
    }
}
