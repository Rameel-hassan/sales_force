package com.ibrahim.salesforce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ibrahim.salesforce.R;

import java.util.ArrayList;
import java.util.List;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.DashboardItemsHolder> {

    private List<String> text1 = new ArrayList<>();
    private List<String> text2 = new ArrayList<>();
    private List<String> text3 = new ArrayList<>();
    private List<String> text4 = new ArrayList<>();
    Context context;

    public DashboardAdapter(List<String> text1, List<String> text2, List<String> text3, List<String> text4, Context context) {
        this.text1 = text1;
        this.text2 = text2;
        this.text3 = text3;
        this.text4 = text4;
        this.context = context;
    }

    @NonNull
    @Override
    public DashboardItemsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.dashboard_lists_content, viewGroup, false);
        return new DashboardItemsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardItemsHolder dashboardItemsHolder, int i) {
        if (text1.get(i) == "") {
            dashboardItemsHolder.text1.setVisibility(View.GONE);
        }

        if (text2.get(i) == "") {
            dashboardItemsHolder.text2.setVisibility(View.GONE);
        }

        if (text3.get(i) == "") {
            dashboardItemsHolder.text3.setVisibility(View.GONE);

        }
        if (text4.get(i) == "") {
            dashboardItemsHolder.text4.setVisibility(View.GONE);
        }
        dashboardItemsHolder.text1.setText(text1.get(i));
        dashboardItemsHolder.text2.setText(text2.get(i));
        dashboardItemsHolder.text3.setText(text3.get(i));
        dashboardItemsHolder.text4.setText(text4.get(i));

    }

    @Override
    public int getItemCount() {
        return text1.size();
    }

    public class DashboardItemsHolder extends RecyclerView.ViewHolder {
        TextView text1;
        TextView text2;
        TextView text3;
        TextView text4;

        public DashboardItemsHolder(View view) {
            super(view);
            text1 = (TextView) view.findViewById(R.id.text1);
            text2 = (TextView) view.findViewById(R.id.text2);
            text3 = (TextView) view.findViewById(R.id.text3);
            text4 = (TextView) view.findViewById(R.id.text4);
        }
    }
}
