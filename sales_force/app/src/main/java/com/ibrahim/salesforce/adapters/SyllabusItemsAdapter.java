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
import com.app.salesforce.response.CurrentSyllabus;

import java.util.List;

public class SyllabusItemsAdapter extends RecyclerView.Adapter<SyllabusItemsAdapter.SyllabusItemsHolder> {
    private List<CurrentSyllabus> mArrSyllItems;
    private Context mContext;
    private AddRemoveItem.AddRemoveSyllabus mAddRemoveSyll;

    public SyllabusItemsAdapter(List<CurrentSyllabus> items, AddRemoveItem.AddRemoveSyllabus addRemoveSyllabus) {
        this.mArrSyllItems = items;
        this.mAddRemoveSyll = addRemoveSyllabus;
    }

    @NonNull
    @Override
    public SyllabusItemsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_syllabus_item, viewGroup, false);
        SyllabusItemsHolder holder = new SyllabusItemsHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final SyllabusItemsHolder itemsHolder, final int i) {
        itemsHolder.mTvItemName.setText(mArrSyllItems.get(i).getSylabusName());
        itemsHolder.mCbItem.setChecked(mArrSyllItems.get(i).getISChecked());
        itemsHolder.mCbItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mArrSyllItems.get(i).setISChecked(true);
                    mAddRemoveSyll.addRemoveItem(mArrSyllItems.get(i), true);
                } else {
                    mArrSyllItems.get(i).setISChecked(false);
                    mAddRemoveSyll.addRemoveItem(mArrSyllItems.get(i), false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mArrSyllItems.size();
    }

    public class SyllabusItemsHolder extends RecyclerView.ViewHolder {
        private TextView mTvItemName;
        private CheckBox mCbItem;

        public SyllabusItemsHolder(@NonNull View itemView) {
            super(itemView);
            mTvItemName = itemView.findViewById(R.id.tv_item_name);
            mCbItem = itemView.findViewById(R.id.cb_item);
    }
    }
}
