package com.app.salesforce.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.salesforce.R;
import com.app.salesforce.model.MainOption;

import java.util.List;

public class MainOptionsAdapter extends RecyclerView.Adapter<MainOptionsAdapter.OptionsViewHolder> {
List<MainOption> optionsList;
Context context;

    public MainOptionsAdapter(List<MainOption> optionsList,Context context) {
        this.context=context;
        this.optionsList=optionsList;
    }

    @NonNull
    @Override
    public MainOptionsAdapter.OptionsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_main_options,viewGroup, false);
        return new OptionsViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull MainOptionsAdapter.OptionsViewHolder myViewHolder, int i) {
        MainOption obj=optionsList.get(i);
        myViewHolder.tvOption_name.setText(obj.getOption_name());
        myViewHolder.option_icon.setImageResource(obj.getIcon_id());
    }

    @Override
    public int getItemCount() {
        return optionsList.size();
    }
    public class OptionsViewHolder  extends RecyclerView.ViewHolder {
        public ImageButton option_icon;
        public TextView tvOption_name;

        public OptionsViewHolder(View view) {
            super(view);
            option_icon=(ImageButton) view.findViewById(R.id.ib_option_icon);
            tvOption_name=(TextView) view.findViewById(R.id.tv_option_name);

        }
    }


}
