package com.ibrahim.salesforce.adapters;

import android.view.View;
import android.widget.TextView;

import com.ibrahim.salesforce.R;
import com.intrusoft.scatter.PieChart;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import java.util.List;

public class ArtistViewHolder extends ChildViewHolder {

    public TextView m_vister_name, m_visiter, m_area;
    PieChart simpleChart;

    public ArtistViewHolder(View itemView) {
        super(itemView);
        m_vister_name = (TextView) itemView.findViewById(R.id.tv_visiter_name);
        m_visiter  = (TextView)itemView.findViewById(R.id.tv_visit_);
        m_area = (TextView) itemView.findViewById(R.id.tv_area_name);

//ARGS-> (displayText, percentage, textColor, backgroundColor)
           //ARGS-> (display text, percentage)


    }

    public void setM_areaName(String area) {
        m_area.setText(area);
    }
    public void setArtistName(String name) {m_vister_name.setText(name);
    }

    public void setM_visiter(String vister) {m_visiter.setText(vister);}


    public  void set_chart_name(List<Character> chart_name){



    }



}