package com.app.salesforce.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.salesforce.R;
import com.app.salesforce.model.Publisher;
import com.app.salesforce.model.SampleSearchModel;
import com.app.salesforce.model.SelectedClassItem;
import com.app.salesforce.model.SelectedItemsModel;
import com.app.salesforce.network.ApiService;
import com.app.salesforce.network.AppWebServices;
import com.app.salesforce.network.RequestCode;
import com.app.salesforce.network.RestCallbackObject;
import com.app.salesforce.network.RestClient;
import com.app.salesforce.network.ServerConnectListenerObject;
import com.app.salesforce.response.ActivityPurpose;
import com.app.salesforce.response.GetServerResponse;
import com.app.salesforce.response.Sery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import retrofit2.Call;

public class SelectedClassAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ServerConnectListenerObject {



    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;


    private List<SelectedClassItem> mSelectedClassItemArrayList;

    private List<SelectedClassItem> mArrItems;
    private Context mContext;
    private AddRemoveItem.AddRemoveSchoolItems mAddRemoveItem;
    private int mPosition;
    private SelectedItemsModel dataItem;
    private GetServerResponse mLoginResponse;
    GetServerResponse seriesResponse;
    RecyclerView.ViewHolder mViewHolder;
    List<Sery> lstSeries = new ArrayList<>();
    List<Sery> lstTobePupulated = new ArrayList<>();
    Sery selectedSeries;
    private ProgressDialog mDialog;
    private ListAdapter adapter;
    RequestQueue requestQueue, queue;
    String url;
    private ApiService mService;
    ArrayList<SampleSearchModel> sampleSearchModels;
    ActivityPurpose activityPurpose;



    public SelectedClassAdapter(List<SelectedClassItem> items, AddRemoveItem.AddRemoveSchoolItems addRemoveItem, ActivityPurpose purpose,List<Sery> lstSeries) {
        this.mArrItems = items;
        this.mAddRemoveItem = addRemoveItem;
        this.activityPurpose = purpose;
        this.lstSeries = lstSeries;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        mDialog = new ProgressDialog(mContext);
        mDialog.setCancelable(false);
        queue = Volley.newRequestQueue(mContext);
        if (lstSeries!=null && lstSeries.size() == 1){
            selectedSeries = lstSeries.get(0);
        }
        url = AppWebServices.BASE_URL + "GetSeriesRelatedtoSubject?SubjectID=";
        sampleSearchModels = new ArrayList<>();
        if (i == TYPE_ITEM) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.row_rcv_selected_item, viewGroup, false);
            ItemsHolder holder = new ItemsHolder(view);
            return holder;
        } else if (i == TYPE_HEADER) {
            View viewHeader = LayoutInflater.from(mContext).inflate(R.layout.row_rcv_selected_header, viewGroup, false);
            VHHeader header = new VHHeader(viewHeader);
            return header;
        }
        throw new RuntimeException("there is no type that matches the type " + i + " + make sure your using types correctly");
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof ItemsHolder) {
            ((ItemsHolder) holder).ibDeleteClass.setOnClickListener(null);
            ((ItemsHolder) holder).cbxReturn.setOnCheckedChangeListener(null);
            ((ItemsHolder) holder).cbxDelievered.setOnCheckedChangeListener(null);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, @SuppressLint("RecyclerView") final int k) {

        if (viewHolder instanceof ItemsHolder) {

            mViewHolder = viewHolder;
            //cast holder to VHItem and set data
            ((ItemsHolder) viewHolder).mTvSubjectName.setText(getItem(k).getSubjectName());
            ((ItemsHolder) viewHolder).mTvClassName.setText(getItem(k).getClassName());

            /*Log.e("mArrSize",mArrItems.size() +"");
            for (int i=0; i< mArrItems.size();i++){
                Log.e("mArrSize",mArrItems.get(i).getClassName());
            }*/

            if (getItem(k).isDelievered() && !getItem(k).isTempAdded()) {
                // Visit Report Activity par yahan sy enable hui hay checkbox.
                ((ItemsHolder) viewHolder).cbxDelievered.setEnabled(false);
                ((ItemsHolder) viewHolder).ibDeleteClass.setEnabled(false);
                ((ItemsHolder) viewHolder).ibDeleteClass.setColorFilter(Color.rgb(128, 128, 128));
            } else if (getItem(k).isReturned() == true) {
                ((ItemsHolder) viewHolder).cbxReturn.setEnabled(false);
                ((ItemsHolder) viewHolder).ibDeleteClass.setEnabled(false);
                ((ItemsHolder) viewHolder).ibDeleteClass.setColorFilter(Color.rgb(128, 128, 128));
            }
            if (activityPurpose.getID() != 4&&activityPurpose.getID() != 6) {
                ((ItemsHolder) viewHolder).cbxReturn.setVisibility(View.INVISIBLE);
                //((ItemsHolder) viewHolder).cbxReturn.setEnabled(false);
            } else {
                ((ItemsHolder) viewHolder).cbxReturn.setVisibility(View.VISIBLE);
                //((ItemsHolder) viewHolder).cbxReturn.setEnabled(true);
            }

            if (activityPurpose.getID() == 3) {

                ((ItemsHolder) viewHolder).cbxDelievered.setEnabled(true);
                if (getItem(k).isDelievered() && !getItem(k).isTempAdded()) {
                    ((ItemsHolder) viewHolder).cbxDelievered.setEnabled(false);
                }
                //((ItemsHolder) viewHolder).cbxReturn.setEnabled(false);
                Toast.makeText(mContext, ""+ activityPurpose.getName(), Toast.LENGTH_SHORT).show();
            }

            if(activityPurpose.getID()==6&& !getItem(k).isTempAdded()){// final visit id is 6
                if(getItem(k).isDelievered()){
                    ((ItemsHolder) viewHolder).cbxDelievered.setEnabled(true);
                }
                ((ItemsHolder) viewHolder).cbxReturn.setEnabled(true);

                ((ItemsHolder) viewHolder).cbxDelievered.setChecked(getItem(k).isDelievered());
                ((ItemsHolder) viewHolder).cbxReturn.setChecked(getItem(k).isbFinal());
            }else {
                ((ItemsHolder) viewHolder).cbxDelievered.setChecked(getItem(k).isDelievered());
                ((ItemsHolder) viewHolder).cbxReturn.setChecked(getItem(k).isReturned());
            }
            if (getItem(k).getSeries() == null) {
                ((ItemsHolder) viewHolder).mTvSeries.setText("Ser");
                if (lstSeries!=null &&lstSeries.size() == 1){
                    selectedSeries = lstSeries.get(0);
                    ((ItemsHolder) viewHolder).mTvSeries.setText(selectedSeries.getSeriesName());
                    mAddRemoveItem.updateSeries(k - 1, selectedSeries);
                }
            } else {
                ((ItemsHolder) viewHolder).mTvSeries.setText(getItem(k).getSeries().getSeriesName());
            }
            ((ItemsHolder) viewHolder).ibDeleteClass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*mAddRemoveItem.uncheckSchoolItemupdate(getItem(i), false);*/
                    Log.e("Del", "Deleted");
                    mAddRemoveItem.addRemoveSchoolItem(getItem(viewHolder.getLayoutPosition()), false, viewHolder.getLayoutPosition() - 1);
                }
            });
            ((ItemsHolder) viewHolder).cbxDelievered.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if (((ItemsHolder) viewHolder).mTvSeries.getText().equals("Ser")) {
                            Toast.makeText(mContext, "Please Select Series First", Toast.LENGTH_SHORT).show();
                            ((ItemsHolder) viewHolder).cbxDelievered.setChecked(false);
                        } else {
                            mAddRemoveItem.updateDeliveredStatus(k - 1, isChecked);
                        }
                    }else {
                        mAddRemoveItem.updateDeliveredStatus(k - 1, isChecked);
                    }
                }
            });
            ((ItemsHolder) viewHolder).cbxReturn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mAddRemoveItem.updateReturnStatus(k - 1, isChecked);
                }
            });
            ((ItemsHolder) viewHolder).mTvPublishers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<Publisher> lstPublishers = null;
                    lstPublishers = getItem(k).lstPublisher;
                    if (lstPublishers.size() > 0) {
                        final List<Publisher> LstPublishers = lstPublishers;
                        adapter = new ArrayAdapter<Publisher>(
                                mContext, R.layout.series_layout, LstPublishers) {
                            ViewHolder holder;

                            class ViewHolder {
                                TextView title;
                            }

                            public View getView(int position, View convertView,
                                                ViewGroup parent) {
                                final LayoutInflater inflater = (LayoutInflater) mContext
                                        .getSystemService(
                                                Context.LAYOUT_INFLATER_SERVICE);
                                if (convertView == null) {
                                    convertView = inflater.inflate(
                                            R.layout.series_layout, null);
                                    holder = new ViewHolder();
                                    holder.title = (TextView) convertView
                                            .findViewById(R.id.series_Title);
                                    convertView.setTag(holder);
                                } else {
                                    // view already defined, retrieve view holder
                                    holder = (ViewHolder) convertView.getTag();
                                }
                                //this is an image from the drawables folder
                                holder.title.setText(LstPublishers.get(position).getPublisher());
                                if(LstPublishers.get(position).isSelected()){
                                    holder.title.setBackgroundColor(mContext.getResources().getColor(R.color.publisher_background));
                                }else{
                                    holder.title.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                                }

                                holder.title.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        for(Publisher p:LstPublishers){
                                            p.setSelected(false);
                                        }
                                        LstPublishers.get(position).setSelected(true);

                                        notifyDataSetChanged();
                                    }
                                });

                                return convertView;
                            }


                        };
                        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("Publishers");
                        builder.setAdapter(adapter, null);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }

                }
            });
            ((ItemsHolder) viewHolder).mTvSeries.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lstSeries.clear();
                    mDialog.setMessage("Getting series List");
                    mDialog.show();
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url + getItem(k).getSelectedSubjectID() + "&ClassID=" + getItem(k).getSelectedClassID(), null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            mDialog.dismiss();
                            try {
                                JSONArray jsonArray = response.getJSONArray("Series");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    int id = obj.getInt("ID");
                                    String name = obj.getString("SeriesName");
                                    lstSeries.add(new Sery(id, name));
                                    Log.e("one item", name);
                                }
                                Log.e("TotalItems", lstSeries.toString());
                                if (lstSeries.size() > 0) {
                                    sampleSearchModels.clear();
                                    for (int j = 0; j < lstSeries.size(); j++) {
                                        Sery sery = lstSeries.get(j);
                                        String myName = sery.getSeriesName();
                                        int myId = sery.getID();
                                        sampleSearchModels.add(new SampleSearchModel(myName, myId));

                                    }
                                    new SimpleSearchDialogCompat(mContext, "Search...",
                                            "What are you looking for...?", null, sampleSearchModels,
                                            new SearchResultListener<SampleSearchModel>() {
                                                @Override
                                                public void onSelected(BaseSearchDialogCompat dialog,
                                                                       SampleSearchModel item, int position) {
                                                    Sery series = new Sery(item.getID(), item.getTitle());
                                                    ((ItemsHolder) viewHolder).mTvSeries.setText(series.getSeriesName());
                                                    selectedSeries = series;
                                                    mAddRemoveItem.updateSeries(k - 1, selectedSeries);
                                                    dialog.dismiss();
                                                }
                                            }).show();
                                } else {
                                    Toast.makeText(mContext, "There are no Series associated", Toast.LENGTH_SHORT).show();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mDialog.dismiss();
                        }
                    });
                    queue.add(jsonObjectRequest);
                    Log.e("All Items", lstSeries.toString());

                }
            });

//            ((ItemsHolder) viewHolder).setIsRecyclable(false);

            if (getItem(k).isTempAdded()){
//                ((ItemsHolder) viewHolder).ibDeleteClass.setVisibility(View.VISIBLE);
                ((ItemsHolder) viewHolder).ibDeleteClass.setClickable(true);

            }else {
//                ((ItemsHolder) viewHolder).ibDeleteClass.setVisibility(View.GONE);
                ((ItemsHolder) viewHolder).ibDeleteClass.setClickable(false);
                ((ItemsHolder) viewHolder).ibDeleteClass.setImageResource(R.drawable.ic_delete_gray_24);
            }

        }
    }


    private SelectedClassItem getItem(int position) {
        return mArrItems.get(position - 1);
    }

    @Override
    public int getItemCount() {
        return mArrItems.size() + 1;
//        return mArrItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }


    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private void callGetSeriesForSubject(int SubjectID) {
        mDialog.show();
        mDialog.setMessage("Fetching Series please wait...");
        mService = RestClient.getInstance(mContext);
        Call<GetServerResponse> userObject = mService.getSeriesForSubject(SubjectID);
        RestCallbackObject callbackObject = new RestCallbackObject((Activity) mContext, this, RequestCode.GET_SUBJECTS_FOR_SERIES).showProgress(true, 0).dontHideProgress(false);
        userObject.enqueue(callbackObject);
    }


    @Override
    public void onFailure(String error, RequestCode requestCode) {
        Toast.makeText(mContext, "API Failed--" + error, Toast.LENGTH_LONG).show();
        mDialog.dismiss();
    }

    @Override
    public void onSuccess(Object object, RequestCode requestCode) {
        if (requestCode == RequestCode.GET_SUBJECTS_FOR_SERIES) {
            seriesResponse = (GetServerResponse) object;
            //lstSeries.clear();
            lstSeries = seriesResponse.getSeries();
            Log.e("SeriesList", lstSeries.toString());
            lstTobePupulated = lstSeries;
            //((ArrayAdapter<Sery>) adapter).notifyDataSetChanged();
        } else {
            Toast.makeText(mContext, "There is No series related to this Subject", Toast.LENGTH_LONG).show();
        }
        mDialog.dismiss();
    }

    public class ItemsHolder extends RecyclerView.ViewHolder {
        private TextView mTvSubjectName;
        private TextView mTvClassName;
        private TextView mTvPublishers;
        private TextView mTvSeries;
        private ImageButton ibDeleteClass;
        private CheckBox cbxDelievered;
        private CheckBox cbxReturn;

        public ItemsHolder(@NonNull View itemView) {
            super(itemView);
            mTvClassName = itemView.findViewById(R.id.txtClassName);
            mTvSubjectName = itemView.findViewById(R.id.txtSubjectName);
            mTvPublishers = itemView.findViewById(R.id.tvPublishers);
            mTvSeries = itemView.findViewById(R.id.txtSeries);
            ibDeleteClass = itemView.findViewById(R.id.ib_delete_Class);
            cbxDelievered = itemView.findViewById(R.id.cb_Delieverd);
            cbxReturn = itemView.findViewById(R.id.cb_ReturnCollection);
        }
    }

    class VHHeader extends RecyclerView.ViewHolder {
        public VHHeader(View itemView) {
            super(itemView);
        }
    }

}
