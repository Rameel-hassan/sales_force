package com.app.salesforce.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.salesforce.R;
import com.app.salesforce.response.MyVisits;
import com.app.salesforce.utilities.Utility;

import java.util.List;

public class VisitDetailsAdapter extends RecyclerView.Adapter<VisitDetailsAdapter.VisitDetailsHolder> {

    private List<MyVisits> mArrVisits;

    public VisitDetailsAdapter(List<MyVisits> mArrVisits) {
        this.mArrVisits = mArrVisits;
    }

    @Override
    public VisitDetailsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_visit_details, viewGroup, false);
        return new VisitDetailsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VisitDetailsHolder visitDetailsHolder, int i) {
        visitDetailsHolder.mTvSerialNo.setText(String.valueOf(i + 1));
        visitDetailsHolder.updateView(mArrVisits.get(i));
    }

    @Override
    public int getItemCount() {
        return mArrVisits.size();
    }

    class VisitDetailsHolder extends RecyclerView.ViewHolder {
        private TextView mTvSerialNo;
        private TextView mTvCustomerName;
        private TextView mTvVisitTime;
        private TextView mTvVisitType;
        public VisitDetailsHolder(@NonNull View itemView) {
            super(itemView);
            mTvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            mTvVisitTime = itemView.findViewById(R.id.tv_visit_time);
            mTvVisitType=itemView.findViewById(R.id.tv_visit_type);
            mTvSerialNo = itemView.findViewById(R.id.tv_serialNum);
        }

        void updateView(MyVisits visit) {
            mTvCustomerName.setText(visit.getCustomerName());
            mTvVisitTime.setText(Utility.setDateTime(visit.getVisitDate()));
            mTvVisitType.setText(visit.getVisitType());
        }

    }
}
