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
import com.ibrahim.salesforce.response.BookPublisherID;
import com.ibrahim.salesforce.response.CurrentSyllabus;

import java.util.List;

public class SelectedPublisherItemsAdapter extends RecyclerView.Adapter<SelectedPublisherItemsAdapter.SelectedSyllabusItemsHolder> {
    private List<CurrentSyllabus> mArrSyllItems;
    private Context mContext;
    private AddRemoveItem.AddRemoveSyllabus mAddRemoveSyll;
    private List<BookPublisherID> syllabusIDs;
//    private List<SyllabusIDs> syllabusID;

    public SelectedPublisherItemsAdapter(List<CurrentSyllabus> items, AddRemoveItem.AddRemoveSyllabus addRemoveSyllabus, List<BookPublisherID> syllabusIDs) {
        this.mArrSyllItems = items;
        this.mAddRemoveSyll = addRemoveSyllabus;
        this.syllabusIDs = syllabusIDs;
    }

    @NonNull
    @Override
    public SelectedSyllabusItemsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_syllabus_item, viewGroup, false);
        SelectedSyllabusItemsHolder holder = new SelectedSyllabusItemsHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final SelectedSyllabusItemsHolder itemsHolder, final int i) {
//        for(int count = 0; count<syllabusIDs.size(); count++){
//            syllabusID.set(count, new SyllabusIDs(0));
//        }
        for (int l = 0; l < syllabusIDs.size(); l++) {
            if (mArrSyllItems.get(i).getSylabusID() == syllabusIDs.get(l).getID()) {
                mArrSyllItems.get(i).setISChecked(true);
                itemsHolder.mCbItem.setChecked(mArrSyllItems.get(i).getISChecked());
                mAddRemoveSyll.addRemoveItem(mArrSyllItems.get(i), true);
                break;
            }
        }
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
                    for (int l = 0; l < syllabusIDs.size(); l++) {
                        if (mArrSyllItems.get(i).getSylabusID() == syllabusIDs.get(l).getID()) {
                            syllabusIDs.set(l, new BookPublisherID(0));
                            break;
                        }
                    }
                    mAddRemoveSyll.addRemoveItem(mArrSyllItems.get(i), false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mArrSyllItems.size();
    }

    public class SelectedSyllabusItemsHolder extends RecyclerView.ViewHolder {
        private TextView mTvItemName;
        private CheckBox mCbItem;

        public SelectedSyllabusItemsHolder(@NonNull View itemView) {
            super(itemView);
            mTvItemName = itemView.findViewById(R.id.tv_item_name);
            mCbItem = itemView.findViewById(R.id.cb_item);
        }
    }
}
