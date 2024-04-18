package com.ibrahim.salesforce.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ibrahim.salesforce.R;
import com.ibrahim.salesforce.model.Artist;
import com.ibrahim.salesforce.model.Genre;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class GenreAdapter extends ExpandableRecyclerViewAdapter<GenreViewHolder, ArtistViewHolder> {

    public GenreAdapter(List<? extends ExpandableGroup> groups) {
        super(groups);
    }

    @Override
    public GenreViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.parent_layout, parent, false);
        return new GenreViewHolder(view);
    }

    @Override
    public ArtistViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.purchase_list_content, parent, false);


        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(ArtistViewHolder holder, int flatPosition,
                                      ExpandableGroup group, int childIndex) {
        final Artist artist = ((Genre) group).getItems().get(childIndex);
        if (artist.getVisits() == "" || artist.getVisits() == null) {
            holder.m_visiter.setVisibility(View.GONE);
        }
        if (artist.getArea() == "" || artist.getArea() == null) {
            holder.m_area.setVisibility(View.GONE);
        }
        holder.setArtistName(artist.getName());
        holder.setM_visiter(artist.getVisits());
        holder.setM_areaName(artist.getArea());
    }

    @Override
    public void onBindGroupViewHolder(GenreViewHolder holder, int flatPosition,
                                      ExpandableGroup group) {

        holder.setGenreTitle(group);
    }
}