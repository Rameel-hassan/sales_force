package com.ibrahim.salesforce.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ibrahim.salesforce.R;
import com.ibrahim.salesforce.model.GetTodayTargetedSchools;

import java.util.List;

public class GetTodayTargetedSchoolsAdapter extends RecyclerView.Adapter<GetTodayTargetedSchoolsAdapter.ViewHolder> {

    private List<GetTodayTargetedSchools.TodayTargetedSchool> schoolList;

    public GetTodayTargetedSchoolsAdapter(List<GetTodayTargetedSchools.TodayTargetedSchool> schoolList) {

        this.schoolList = schoolList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_today_targeted_schools, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GetTodayTargetedSchools.TodayTargetedSchool school = schoolList.get(position);

        holder.srTextView.setText(String.valueOf(position + 1));
        holder.schoolTextView.setText(school.getVendorName());
        holder.contactTextView.setText(school.getPhoneNumber());
        holder.areaTextView.setText(school.getAreaName());
    }

    @Override
    public int getItemCount() {
        return schoolList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView srTextView, schoolTextView, contactTextView, areaTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            srTextView = itemView.findViewById(R.id.sr);
            schoolTextView = itemView.findViewById(R.id.school);
            contactTextView = itemView.findViewById(R.id.contact);

            areaTextView = itemView.findViewById(R.id.area);
        }
    }
}
