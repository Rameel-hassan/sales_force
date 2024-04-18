package com.ibrahim.salesforce.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ibrahim.salesforce.R;
import com.ibrahim.salesforce.response.School;

import java.util.List;

public class SessionWiseSchoolsAdapter extends RecyclerView.Adapter<SessionWiseSchoolsAdapter.SchoolItemsHolder>{
    List<School> lstSchool;
    public SessionWiseSchoolsAdapter(List<School> lstSchools) {
        this.lstSchool=lstSchools;
    }

    @NonNull
    @Override
    public SchoolItemsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_schools_session_wise, viewGroup, false);
        return new SchoolItemsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SchoolItemsHolder schoolItemsHolder, int i) {
schoolItemsHolder.updateView(lstSchool.get(i));
    }

    @Override
    public int getItemCount() {
        return lstSchool.size();
    }

    public class SchoolItemsHolder extends RecyclerView.ViewHolder {
        private TextView mTvSchoolName;
        private TextView mTvCityName;
        private TextView mTvAreaName;
        private ImageButton ibFirstVisit;
        private ImageButton ibSamplel;
        private ImageButton ibAdditionalSample;
        private ImageButton ibFollowUp;
        private ImageButton ibFinalVisit;

        public SchoolItemsHolder(@NonNull View itemView) {
            super(itemView);
            mTvSchoolName=itemView.findViewById(R.id.tvSchoolName);
            mTvCityName=itemView.findViewById(R.id.tvCityName);
            mTvAreaName=itemView.findViewById(R.id.tvAreaName);
            ibFirstVisit=itemView.findViewById(R.id.ibFV);
            ibSamplel=itemView.findViewById(R.id.ibSAM);
            ibAdditionalSample=itemView.findViewById(R.id.ibAS);
            ibFollowUp=itemView.findViewById(R.id.ibFU);
            ibFinalVisit=itemView.findViewById(R.id.ibFinV);
    }
        void updateView(School school) {
            mTvSchoolName.setText(school.getSchoolName());
            mTvCityName.setText(school.getCityName());
            mTvAreaName.setText(school.getAreaName());
            if(school.getFirstVisit()==0){
           ibFirstVisit.setImageResource(R.drawable.ic_no);
            }
            else{
                ibFirstVisit.setImageResource(R.drawable.ic_yes);
            }
            if(school.getSample()==0){
                ibSamplel.setImageResource(R.drawable.ic_no);
            }
            else{
                ibSamplel.setImageResource(R.drawable.ic_yes);
            }
            if(school.getAdditionalSample()==0){
                ibAdditionalSample.setImageResource(R.drawable.ic_no);
            }
            else{
                ibAdditionalSample.setImageResource(R.drawable.ic_yes);
            }
            if(school.getFollowUp()==0){
                ibFollowUp.setImageResource(R.drawable.ic_no);
            }
            else{
                ibFollowUp.setImageResource(R.drawable.ic_yes);
            }
            if(school.getFinalVisit()==0){
                ibFinalVisit.setImageResource(R.drawable.ic_no);
            }
            else{
                ibFinalVisit.setImageResource(R.drawable.ic_yes);
            }
        }
    }
}
