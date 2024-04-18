package com.ibrahim.salesforce.dialoge;


import static com.ibrahim.salesforce.network.RequestCode.GET_ZONES_REQUEST_CODE;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.ibrahim.salesforce.R;
import com.ibrahim.salesforce.activity.AddTargetedProduct;
import com.ibrahim.salesforce.activity.DashboardActivity;
import com.ibrahim.salesforce.activity.ReportsActivity;
import com.ibrahim.salesforce.model.SampleSearchModel;
import com.ibrahim.salesforce.network.ApiService;
import com.ibrahim.salesforce.network.RequestCode;
import com.ibrahim.salesforce.network.RestCallbackObject;
import com.ibrahim.salesforce.network.RestClient;
import com.ibrahim.salesforce.network.ServerCodes;
import com.ibrahim.salesforce.network.ServerConnectListenerObject;
import com.ibrahim.salesforce.offline.AppDataBase;
import com.ibrahim.salesforce.offline.Cities;
import com.ibrahim.salesforce.response.CustomersRelatedtoSO;
import com.ibrahim.salesforce.response.GetAreasResponse;
import com.ibrahim.salesforce.response.GetCitiesResponse;
import com.ibrahim.salesforce.response.GetServerResponse;
import com.ibrahim.salesforce.response.GetSubAreasResponse;
import com.ibrahim.salesforce.response.GetZonesResponse;
import com.ibrahim.salesforce.response.Region;
import com.ibrahim.salesforce.response.ServerResponse;
import com.ibrahim.salesforce.utilities.AppBundles;
import com.ibrahim.salesforce.utilities.AppKeys;
import com.ibrahim.salesforce.utilities.Utility;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsDialogFragment extends DialogFragment implements ServerConnectListenerObject {

    private Spinner mSpnRegion, mSpnCustomersRelatedToSo, mSpnCity, mSpnArea;
    private Button mBtnCheckin;
    private ArrayAdapter<CustomersRelatedtoSO> adapter;
    private GetServerResponse mLoginResponse;
    private GetAreasResponse mNewAreaResponse;
    private Context mContext;
    private List<GetAreasResponse.Area> mArrAreas;
    private TextView tvAreaaWise;
    private ProgressDialog mDialog;
    private ApiService mService;
    private int regionPosition=-1, customerPosition = -1, cityPosition = -1, areaPosition = -1;
    private RelativeLayout mRlRegionCustomers;
    //DetailsDialogFragment dialogFragment;
    List<Region> mArrRegion;
    List<CustomersRelatedtoSO> mArrCustomers;
    List<GetCitiesResponse.City> arrCitiesRelatedToRegion;
    List<Cities> citiesList;
    ArrayList<SampleSearchModel> sampleSearchModels;
    ArrayList<SampleSearchModel> newsampleSearchModels;
    private ArrayAdapter<GetCitiesResponse.City> cityAdapter;
    private int callType = 0;

    private RelativeLayout rlVendor;
    private Spinner spVendor;

    private boolean isZoneFormat=false;
    //private RelativeLayout viewZone,viewSubArea;

    private List<GetZonesResponse.Zone> mArrZones;
    private GetZonesResponse mZonesResponse;
    private int zoneID = -1;
    private int subAreaID = -1;
    ArrayAdapter<GetSubAreasResponse.SubArea> subAreaAdapter;
    ArrayAdapter<GetZonesResponse.Zone> zoneAdapter;
    private List<GetSubAreasResponse.SubArea> mSubAreas;
    private GetSubAreasResponse mSubAreasResponse;

    //private Spinner spSubArea,spZone;
    public DetailsDialogFragment() {
    }

    public static DetailsDialogFragment newInstance(String title, Context mContext, int type) {
        Paper.init(mContext);
        DetailsDialogFragment frag = new DetailsDialogFragment();
        frag.mContext = mContext;
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("showType", type);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.dialog_details_dialog, container, false);
        callType = getArguments().getInt("showType");
        Log.d("callType", "callType: " + callType);
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.bg_round_corners);
        initComponents(view);
        Log.d("class_name", this.getClass().getSimpleName());
        return view;
    }

    private void initComponents(View view) {
        rlVendor=view.findViewById(R.id.rl_vendor_type);
        spVendor=view.findViewById(R.id.spVendorType);
        mSpnRegion = view.findViewById(R.id.spn_region);
        mSpnCustomersRelatedToSo = view.findViewById(R.id.spn_customers);
        mBtnCheckin = view.findViewById(R.id.btnCheckIn);
        mRlRegionCustomers = view.findViewById(R.id.rl_region_customers);
        mSpnArea = view.findViewById(R.id.spn_pop_area);
        mLoginResponse = Paper.book().read(AppKeys.KEY_LOGIN_RESPONSE);
        mDialog = new ProgressDialog(mContext);
        tvAreaaWise=view.findViewById(R.id.tv_title_areawise);
        mArrCustomers = new ArrayList<CustomersRelatedtoSO>();
        mDialog.setCancelable(false);
        mSpnCity = view.findViewById(R.id.spn_city);
        setRegionSpinner();
        setVendorSpinner();
        sampleSearchModels = new ArrayList<>();
        newsampleSearchModels = new ArrayList<>();
//        viewZone=view.findViewById(R.id.view_zone);
//        viewSubArea=view.findViewById(R.id.view_sub_area);
        mArrAreas = new ArrayList<>();
        if (callType == 10) {
            mBtnCheckin.setText("Delete");
        }

//        isZoneFormat=mLoginResponse.getData().isZoneFormat();
//        if(isZoneFormat){
//            viewZone.setVisibility(View.VISIBLE);
//            viewSubArea.setVisibility(View.VISIBLE);
//        }



        mBtnCheckin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.isNetworkAvailable(mContext)) {
                    if (validateForm())
                        callService(callType);
                    if (callType == 8) {

                        if(mArrCustomers.size() > 0) {
                            Intent intent = new Intent(mContext, ReportsActivity.class);
                            intent.putExtra(AppBundles.BUNDLE_CUSTOMER_OBJ, mArrCustomers.get(customerPosition));
                            intent.putExtra(AppBundles.BUNDLE_CUSTOMER_OBJ, mLoginResponse.getData().getCustomersRelatedtoSO().get(customerPosition));
                            intent.putExtra(AppBundles.BUNDLE_ORDER, "Offline");
                            intent.putExtra(AppBundles.BUNDLE_VENDOR_TYPE,spVendor.getSelectedItem().toString());
                            startActivity(intent);
                            dismiss();
                        }else{
                            Toast.makeText(getContext(),"No School Present",Toast.LENGTH_SHORT).show();
                        }

                    }else if(callType==18){

                            Intent intent = new Intent(mContext, AddTargetedProduct.class);
                            intent.putExtra(AppBundles.BUNDLE_ORDER, "addTargetedSchool");
                            intent.putExtra("region",mSpnRegion.getSelectedItemPosition());
                            intent.putExtra("city",mSpnCity.getSelectedItemPosition());
                            intent.putExtra("area",mSpnArea.getSelectedItemPosition());
                            intent.putExtra("areaId", mArrAreas.get(mSpnArea.getSelectedItemPosition()).getID());
                            startActivity(intent);
                            dismiss();

                    }
                } else {
                    Toast.makeText(mContext, getString(R.string.str_no_internet), Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    private void setVendorSpinner() {

        ArrayList<String> vendors=new ArrayList<String>();
        vendors.add("School");
        vendors.add("Shop");
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                requireContext(),android.R.layout.simple_spinner_dropdown_item,vendors
        );


        // Set the ArrayAdapter to the Spinner
        spVendor.setAdapter(adapter);

        // Set the default selected item to "School"
        spVendor.setSelection(0);
        spVendor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView)view).setTextColor(getResources().getColorStateList(R.color.black));
                if(callType != 18&&mArrCustomers.size()>0&&cityPosition!=-1&&areaPosition!=-1) {
                    callService(1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void setRegionSpinner() {
        mArrRegion = mLoginResponse.getData().getRegion();
//        if (mArrRegion.size() > 1) {
//            Region region = new Region(0, "Select Region");
//            mArrRegion.add(0, region);
//        } else


//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    callService(3);
//                } catch (Exception ex) {
//                    Toast.makeText(mContext, "Please Wait for data to populate", Toast.LENGTH_LONG).show();
//                }
//            }
//        }, 600);

        ArrayAdapter<Region> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, mArrRegion);
        mSpnRegion.setAdapter(adapter);
        regionPosition=-1;
        mSpnRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView)view).setTextColor(getResources().getColorStateList(R.color.black));
                loadCities(position);
                if (Utility.isNetworkAvailable(mContext)) {
                    regionPosition = position;
//                    loadCities(position);
                    callService(3);
                } else
                    Toast.makeText(mContext, getString(R.string.str_no_internet), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void setCitySpinner() {
        cityAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, arrCitiesRelatedToRegion);
        mSpnCity.setAdapter(cityAdapter);
        newsampleSearchModels.clear();
        for (int i = 0; i < arrCitiesRelatedToRegion.size(); i++) {
            GetCitiesResponse.City city = arrCitiesRelatedToRegion.get(i);
            String name = city.getName();
            int id = city.getID();
            newsampleSearchModels.add(new SampleSearchModel(name, id));

        }

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
//                    if(isZoneFormat){
//                        callService(55);//fetch zones
//                    }else{
                    cityPosition=0;
                        callService(5);//fetch area
                    //}
                } catch (Exception ex) {
                    Toast.makeText(mContext, "Please Wait for data to populate", Toast.LENGTH_LONG).show();
                }
            }
        }, 600);


        mSpnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView)view).setTextColor(getResources().getColorStateList(R.color.black));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mDialog.dismiss();



        mSpnCity.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    arrCitiesRelatedToRegion.clear();
                    SimpleSearchDialogCompat temp = new SimpleSearchDialogCompat(getActivity(), "Search...",
                            "What are you looking for...?", null, newsampleSearchModels,
                            new SearchResultListener<SampleSearchModel>() {
                                @Override
                                public void onSelected(BaseSearchDialogCompat dialog,
                                                       SampleSearchModel item, int position) {
                                    arrCitiesRelatedToRegion.add(new GetCitiesResponse().new City(item.getID(), item.getTitle()));
                                    cityAdapter = new ArrayAdapter<GetCitiesResponse.City>(mContext, android.R.layout.simple_spinner_dropdown_item, arrCitiesRelatedToRegion);
                                    mSpnCity.setAdapter(cityAdapter);
                                    mSpnCity.setVisibility(View.VISIBLE);
                                    mSpnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            ((TextView)view).setTextColor(getResources().getColorStateList(R.color.black));
                                            TextView selectedText = (TextView) parent.getChildAt(0);
                                            if (selectedText != null) {
                                                selectedText.setTextColor(Color.BLACK);
                                            }
                                            else{
                                                selectedText.setTextColor(Color.BLACK);

                                            }
                                            cityPosition = position;
                                            callService(5);
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {
                                        }
                                    });
                                    dialog.dismiss();
                                }
                            });
                    temp.show();
                    temp.getTitleTextView().setTextColor(Color.BLACK);
                    temp.getSearchBox().setTextColor(getResources().getColorStateList(R.color.innercolor));
                }
                return true;
            }
        });
    }

    private void setAreaSpinner(GetAreasResponse mAreaResponse) {
        mArrAreas = mAreaResponse.getAreas();
        ArrayAdapter<GetAreasResponse.Area> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, mArrAreas);
        mSpnArea.setAdapter(adapter);
        mSpnArea.setVisibility(View.VISIBLE);
        mSpnArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView)view).setTextColor(
                        getResources().getColorStateList(R.color.black)
                );
                areaPosition = position;

//                if(isZoneFormat){
//                    //Load sub areas
//                    callService(60);
//                }else
                    if(callType != 18) {
                        callService(1);
                    }
                //}

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setCustomerSpinner() {
        //mArrCustomers.add(0, new CustomersRelatedtoSO(0, false, "Select Customer"));
        adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, mArrCustomers);
        mSpnCustomersRelatedToSo.setAdapter(adapter);
        mSpnCustomersRelatedToSo.setVisibility(View.VISIBLE);
        tvAreaaWise.setVisibility(View.VISIBLE);
        sampleSearchModels.clear();
        for (int i = 0; i < mArrCustomers.size(); i++) {
            CustomersRelatedtoSO customer = mArrCustomers.get(i);
            String name = customer.getShopName();
            int id = customer.getID();
            sampleSearchModels.add(new SampleSearchModel(name, id));

        }
        customerPosition=0;

        mSpnCustomersRelatedToSo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView)view).setTextColor(getResources().getColorStateList(R.color.black));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mDialog.dismiss();
        mSpnCustomersRelatedToSo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mArrCustomers.clear();

                    SimpleSearchDialogCompat temp = new SimpleSearchDialogCompat(getActivity(), "Search...",
                            "What are you looking for...?", null, sampleSearchModels,
                            new SearchResultListener<SampleSearchModel>() {
                                @Override
                                public void onSelected(BaseSearchDialogCompat dialog,
                                                       SampleSearchModel item, int position) {
                                    mArrCustomers.add(new CustomersRelatedtoSO(item.getID(), false, item.getTitle()));
                                    adapter = new ArrayAdapter<CustomersRelatedtoSO>(mContext, android.R.layout.simple_spinner_dropdown_item, mArrCustomers);
                                    mSpnCustomersRelatedToSo.setAdapter(adapter);
                                    mSpnCustomersRelatedToSo.setVisibility(View.VISIBLE);
                                    tvAreaaWise.setVisibility(View.VISIBLE);
                                    mSpnCustomersRelatedToSo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            ((CheckedTextView)view).setTextColor(
                                                    getResources().getColorStateList(R.color.black)
                                            );
                                            customerPosition = position;

                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {
                                        }

                                    });
                                    dialog.dismiss();
                                }
                            });
                    temp.show();
                    temp.getTitleTextView().setTextColor(Color.BLACK);
                    temp.getSearchBox().setTextColor(getResources().getColorStateList(R.color.innercolor));

                }
                return true;
            }
        });

    }

    private boolean validateForm() {
//        if (position == 0) {
//            Toast.makeText(mContext, "Select any customer", Toast.LENGTH_SHORT).show();
//            return false;
//        } else
        if (DashboardActivity.mLocationService.getLongitude() == 0.0) {
            DashboardActivity.mLocationService.getLocation();
            Toast.makeText(mContext, "Can't get your location", Toast.LENGTH_SHORT).show();
            return false;
        }else
            return true;
    }

    private void callService(int type) {
        mService = RestClient.getInstance(mContext);
        if (type == 1) {
            mDialog.show();
            mDialog.setMessage("Loading your customers,please wait...");
            Call<GetServerResponse> userObject = mService.getCustomerRelatedToSo(arrCitiesRelatedToRegion.get(cityPosition).getID(), mArrAreas.get(areaPosition).getID(),spVendor.getSelectedItem().toString());
            RestCallbackObject callbackObject = new RestCallbackObject((Activity) mContext, this, RequestCode.GET_CUSTOMERS_RELATED_TO_AREA).showProgress(true, 0).dontHideProgress(false);
            userObject.enqueue(callbackObject);

        } else if (type == 3) {
            mDialog.show();
            mDialog.setMessage("Loading Cities,please wait...");
            Call<GetCitiesResponse> userObject = mService.getCities(mArrRegion.get(regionPosition).getID(), mLoginResponse.getData().getSOID());
            RestCallbackObject callbackObject = new RestCallbackObject((Activity) mContext, this, RequestCode.GET_CITIES_REQUEST_CODE).showProgress(true, 0).dontHideProgress(false);
            userObject.enqueue(callbackObject);
        } else if (type == 10) {
            mDialog.show();
            mDialog.setMessage("Deleting School,please wait...");
            Call<ServerResponse> userObject = mService.deleteRetailer(mArrCustomers.get(customerPosition).getID());
            RestCallbackObject callbackObject = new RestCallbackObject((Activity) mContext, this, RequestCode.DELETE_SCHOOLS).showProgress(true, 0).dontHideProgress(false);
            userObject.enqueue(callbackObject);

        } else if (type == 5) {
            mDialog.show();
            mDialog.setMessage("Fetching areas please wait...");
            mService = RestClient.getInstance(mContext);
            Call<GetAreasResponse> userObject = mService.getAreas(String.valueOf(arrCitiesRelatedToRegion.get(cityPosition).getID()), mLoginResponse.getData().getSOID());
            RestCallbackObject callbackObject = new RestCallbackObject((Activity) mContext, this, RequestCode.GET_AREAS_FOR_CITY).showProgress(true, 0).dontHideProgress(false);
            userObject.enqueue(callbackObject);
        }else if (type == 55) {
            mDialog.show();
            mDialog.setMessage("Fetching Zones please wait...");
            mService = RestClient.getInstance(mContext);
            Call<GetZonesResponse> userObject = mService.getZones(String.valueOf(arrCitiesRelatedToRegion.get(cityPosition).getID()),mLoginResponse.getData().getSOID());
            RestCallbackObject callbackObject = new RestCallbackObject(getActivity(), this, GET_ZONES_REQUEST_CODE).showProgress(true, 0).dontHideProgress(false);
            userObject.enqueue(callbackObject);
        }else if(type==50){
            mDialog.show();
            mDialog.setMessage("Fetching areas please wait...");
            mService = RestClient.getInstance(mContext);
            Call<GetAreasResponse> userObject = mService.getAreas(String.valueOf(arrCitiesRelatedToRegion.get(cityPosition).getID()), mLoginResponse.getData().getSOID(),zoneID);
            RestCallbackObject callbackObject = new RestCallbackObject(getActivity(), this, RequestCode.GET_AREA_REQUEST_CODE).showProgress(true, 0).dontHideProgress(false);
            userObject.enqueue(callbackObject);
        }else if(type==60){
            mDialog.show();
            mDialog.setMessage("Fetching Sub-Areas please wait...");
            mService = RestClient.getInstance(mContext);
            Call<GetSubAreasResponse> userObject = mService.geSubAreas(mArrAreas.get(areaPosition).getID());
            RestCallbackObject callbackObject = new RestCallbackObject(getActivity(), this, RequestCode.GET_SUB_AREAS_REQUEST_CODE).showProgress(true, 0).dontHideProgress(false);
            userObject.enqueue(callbackObject);
        } else if (type == 15) {

            Log.e("Fifteen", "15");

            mBtnCheckin.setEnabled(false);
            Call<ServerResponse> userObject = mService.validateLatLong(mArrCustomers.get(customerPosition).getID(), DashboardActivity.mLocationService.getLatitude(), DashboardActivity.mLocationService.getLongitude());
            RestCallbackObject callbackObject = new RestCallbackObject((Activity) mContext, this, RequestCode.VALIDATE_LAT_LONG).showProgress(true, 0).dontHideProgress(false);
            userObject.enqueue(callbackObject);

        } else {
            if (type == 7) {
                if (mArrCustomers.isEmpty()) {

                    Toast.makeText(mContext, "Please select school first", Toast.LENGTH_SHORT).show();
                } else {
                    mBtnCheckin.setEnabled(false);
                    mDialog.show();
                    mDialog.setMessage("please wait...");
                    Call<ServerResponse> userObject = mService.validateLatLong(mArrCustomers.get(customerPosition).getID(), DashboardActivity.mLocationService.getLatitude(), DashboardActivity.mLocationService.getLongitude());
                    RestCallbackObject callbackObject = new RestCallbackObject((Activity) mContext, this, RequestCode.VALIDATE_LAT_LONG).showProgress(true, 0).dontHideProgress(false);
                    userObject.enqueue(callbackObject);
                }
            }
        }
    }

    @Override
    public void onFailure(String error, RequestCode requestCode) {
        if (requestCode == RequestCode.DELETE_SCHOOLS) {
            Toast.makeText(mContext, "Cannot Delete School", Toast.LENGTH_SHORT).show();
        } else if (requestCode == RequestCode.VALIDATE_LAT_LONG) {
            Toast.makeText(mContext, "Cannot validate latlong", Toast.LENGTH_SHORT).show();
        } else if (requestCode == RequestCode.GET_AREAS_FOR_CITY) {
            Toast.makeText(mContext, "No area Associated", Toast.LENGTH_SHORT).show();
            mSpnArea.setVisibility(View.GONE);
            areaPosition=-1;
            customerPosition=-1;
            mSpnCustomersRelatedToSo.setVisibility(View.GONE);
            tvAreaaWise.setVisibility(View.GONE);
        } else if (requestCode == RequestCode.GET_CUSTOMERS_RELATED_TO_AREA) {
            mSpnCustomersRelatedToSo.setVisibility(View.GONE);
            tvAreaaWise.setVisibility(View.GONE);
            customerPosition=-1;
            Toast.makeText(mContext, "No Customers Associated", Toast.LENGTH_SHORT).show();
        }else if (requestCode == RequestCode.GET_SUB_AREAS_REQUEST_CODE) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
            Log.e("Result", "Failed");
            Toast.makeText(mContext, "Failed-- " + error, Toast.LENGTH_SHORT).show();
        }  else if (requestCode == RequestCode.GET_ZONES_REQUEST_CODE) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
//            viewZone.setVisibility(View.GONE);
//            viewSubArea.setVisibility(View.GONE);
            mSpnArea.setVisibility(View.GONE);
            Log.e("Result", "Failed");
            Toast.makeText(mContext, "Failed-- " + error, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "API Failed--" + requestCode + error, Toast.LENGTH_SHORT).show();
        }
        mDialog.dismiss();
        // this.dismiss();
    }

    @Override
    public void onSuccess(Object object, RequestCode requestCode) {
        if (requestCode == RequestCode.VALIDATE_LAT_LONG) {
            // TODO remove this acitivty call from here to make it work properly
            //startReportsActivity();
            ServerResponse serverResponse = (ServerResponse) object;
            if (serverResponse.getResultType() == ServerCodes.ServerResultType.SERVER_RESULT_SUCCESS) {
                /*if (mArrCustomers.isEmpty()){
                    Toast.makeText(mContext, "ABCDEFG", Toast.LENGTH_SHORT).show();
                } else {*/
                    startReportsActivity();
                /*}*/
            } else {
                Toast.makeText(mContext, serverResponse.getMessage(), Toast.LENGTH_LONG).show();
                if (mDialog.isShowing()) {
                    mDialog.hide();
                }
//                this.dismiss();
            }
            mBtnCheckin.setEnabled(true);
        } else if (requestCode == RequestCode.GET_AREAS_FOR_CITY) {
            if (mDialog.isShowing()) {
                mDialog.hide();
            }
            mNewAreaResponse = (GetAreasResponse) object;
            if (mNewAreaResponse.getAreas() == null || mNewAreaResponse.getAreas().isEmpty()) {
                mSpnCustomersRelatedToSo.setVisibility(View.GONE);
                tvAreaaWise.setVisibility(View.GONE);
                areaPosition=-1;
                customerPosition=-1;
                Toast.makeText(mContext, "There are no areas associated", Toast.LENGTH_SHORT).show();
            } else {
                mSpnArea.setVisibility(View.VISIBLE);
                setAreaSpinner(mNewAreaResponse);
            }
        }else if (requestCode == RequestCode.GET_SUB_AREAS_REQUEST_CODE) {
            mSubAreasResponse = (GetSubAreasResponse) object;
            if (mDialog.isShowing()) {
                mDialog.hide();
            }
            //testing
            if (mSubAreasResponse.getSubAreas().size() <= 0) {
                Toast.makeText(mContext, "There are no sub areas associated", Toast.LENGTH_SHORT).show();
            } else {
                //setSubAreasSpinner(mSubAreasResponse);
            }
        } else if (requestCode == RequestCode.GET_ZONES_REQUEST_CODE) {
            mZonesResponse = (GetZonesResponse) object;
            if (mDialog.isShowing()) {
                mDialog.hide();
            }
            //testing
            if (mZonesResponse.getZones().size() <= 0) {
                Toast.makeText(mContext, "There are no Zones associated", Toast.LENGTH_SHORT).show();
                //viewZone.setVisibility(View.GONE);
                mSpnArea.setVisibility(View.GONE);
                //viewSubArea.setVisibility(View.GONE);
            } else {
               // setZoneSpinner();
            }
        }  else if (requestCode == RequestCode.DELETE_SCHOOLS) {
            ServerResponse serverResponse = (ServerResponse) object;
            Toast.makeText(mContext, serverResponse.getMessage(), Toast.LENGTH_LONG).show();
            callService(1);
        } else if (requestCode == RequestCode.GET_CITIES_REQUEST_CODE) {
            if (((GetCitiesResponse) object).getCities() == null || ((GetCitiesResponse) object).getCities().isEmpty()) {
                mSpnArea.setVisibility(View.GONE);
                mSpnCustomersRelatedToSo.setVisibility(View.GONE);
                tvAreaaWise.setVisibility(View.GONE);
                mSpnCity.setVisibility(View.GONE);
                cityPosition=-1;
                areaPosition=-1;
                customerPosition=-1;
                Toast.makeText(mContext, "There are no cities in this ", Toast.LENGTH_SHORT).show();
                mDialog.dismiss();
            } else {
                mSpnCity.setVisibility(View.VISIBLE);
                arrCitiesRelatedToRegion = ((GetCitiesResponse) object).getCities();
                setCitySpinner();
            }
        } else if (requestCode == RequestCode.GET_CUSTOMERS_RELATED_TO_AREA) {
            GetServerResponse serverResponse = (GetServerResponse) object;
            if (serverResponse.getCustomersRelatedtoSO() != null && !serverResponse.getCustomersRelatedtoSO().isEmpty()) {
                mSpnCustomersRelatedToSo.setVisibility(View.VISIBLE);
                tvAreaaWise.setVisibility(View.VISIBLE);
                mArrCustomers.clear();
                mArrCustomers = ((GetServerResponse) object).getCustomersRelatedtoSO();
                if(callType!=18){
                    setCustomerSpinner();
                }
                Log.e("ServerResponse", String.valueOf(mLoginResponse.getData().getSOID()));
                Log.e("ServerResponse", String.valueOf(arrCitiesRelatedToRegion.get(cityPosition).getID()));
                mBtnCheckin.setEnabled(true);

                mLoginResponse.getData().setCustomersRelatedtoSO(serverResponse.getCustomersRelatedtoSO());
                Paper.book().write(AppKeys.KEY_LOGIN_RESPONSE, mLoginResponse);
                //hassan Usman Test 2
            } else {
//                mSpnCustomersRelatedToSo.setVisibility(View.GONE);
                mSpnCustomersRelatedToSo.setVisibility(View.GONE);
                tvAreaaWise.setVisibility(View.GONE);
                mArrCustomers.clear();
                areaPosition=-1;
                customerPosition=-1;
                Toast.makeText(mContext, "There are no Customers in this arae", Toast.LENGTH_SHORT).show();
                mDialog.dismiss();
            }

        }
        mDialog.hide();
    }


//    private void setSubAreasSpinner(GetSubAreasResponse mAreaResponse) {
//        subAreaID = -1;
//        mSubAreas = mAreaResponse.getSubAreas();
//        GetSubAreasResponse.SubArea emptyCity = new GetSubAreasResponse.SubArea(0, "Select Sub-Area*");
//        mSubAreas.add(0, emptyCity);
//        subAreaAdapter = new ArrayAdapter<GetSubAreasResponse.SubArea>(mContext, android.R.layout.simple_spinner_dropdown_item, mSubAreas);
//        spSubArea.setAdapter(subAreaAdapter);
//
//        ArrayList<SampleSearchModel> subAreaSearchList = new ArrayList<SampleSearchModel>();
//        for (int i = 0; i < mSubAreas.size(); i++) {
//            GetSubAreasResponse.SubArea area = mSubAreas.get(i);
//            String name = area.getName();
//            int id = area.getID();
//            subAreaSearchList.add(new SampleSearchModel(name, id));
//
//        }
//
//        viewSubArea.setVisibility(View.VISIBLE);
//
//        spSubArea.setOnTouchListener(new View.OnTouchListener() {
//
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    mArrAreas.clear();
//                    SimpleSearchDialogCompat temp = new SimpleSearchDialogCompat(getActivity(), "Search...",
//                            "What are you looking for...?", null, subAreaSearchList,
//                            new SearchResultListener<SampleSearchModel>() {
//                                @Override
//                                public void onSelected(BaseSearchDialogCompat dialog,
//                                                       SampleSearchModel item, int position) {
//
//
//                                    mSubAreas.add(new GetSubAreasResponse.SubArea(item.getID(), item.getTitle()));
//                                    subAreaAdapter = new ArrayAdapter<GetSubAreasResponse.SubArea>(mContext, android.R.layout.simple_spinner_dropdown_item, mSubAreas);
//                                    spSubArea.setAdapter(subAreaAdapter);
//                                    spSubArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                                        @Override
//                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//
//                                            ((TextView) view).setTextColor(getResources().getColorStateList(R.color.black));
//                                            TextView selectedText = (TextView) parent.getChildAt(0);
//                                            if (selectedText != null) {
//                                                selectedText.setTextColor(Color.BLACK);
//                                            } else {
//                                                selectedText.setTextColor(Color.BLACK);
//
//                                            }
//                                            subAreaID = mSubAreas.get(position).getID();
//                                            if (subAreaID != 0) {
//                                                if(callType != 18){
//                                                    callService(1);
//                                                }
//                                            }
//
//                                        }
//
//                                        @Override
//                                        public void onNothingSelected(AdapterView<?> parent) {
//                                        }
//                                    });
//                                    dialog.dismiss();
//                                }
//                            });
//                    temp.show();
//                    temp.getTitleTextView().setTextColor(Color.BLACK);
//                    temp.getSearchBox().setTextColor(getResources().getColorStateList(R.color.innercolor));
//                }
//                return true;
//            }
//        });
//
//
//   /*     mSpnArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });*/
//    }
//    private void setZoneSpinner() {
//        zoneID = -1;
//        mArrZones = mZonesResponse.getZones();
//        Zone emptyCity = new Zone(0, "Select Zone*");
//        mArrZones.add(0, emptyCity);
//        zoneAdapter = new ArrayAdapter<Zone>(mContext, android.R.layout.simple_spinner_dropdown_item, mArrZones);
//        spZone.setAdapter(zoneAdapter);
//
//        ArrayList<SampleSearchModel> zoneSearchList = new ArrayList<SampleSearchModel>();
//        for (int i = 0; i < mArrZones.size(); i++) {
//            Zone zone = mArrZones.get(i);
//            String name = zone.getName();
//            int id = zone.getID();
//            zoneSearchList.add(new SampleSearchModel(name, id));
//
//        }
//        spZone.setOnTouchListener(new View.OnTouchListener() {
//
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    mArrZones.clear();
//                    SimpleSearchDialogCompat temp = new SimpleSearchDialogCompat(getActivity(), "Search...",
//                            "What are you looking for...?", null, zoneSearchList,
//                            new SearchResultListener<SampleSearchModel>() {
//                                @Override
//                                public void onSelected(BaseSearchDialogCompat dialog,
//                                                       SampleSearchModel item, int position) {
//
//
//                                    mArrZones.add(new Zone(item.getID(), item.getTitle()));
//                                    zoneAdapter = new ArrayAdapter<Zone>(mContext, android.R.layout.simple_spinner_dropdown_item, mArrZones);
//                                    spZone.setAdapter(zoneAdapter);
//
//                                    spZone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                                        @Override
//                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//
//                                            ((TextView) view).setTextColor(getResources().getColorStateList(R.color.black));
//                                            TextView selectedText = (TextView) parent.getChildAt(0);
//                                            if (selectedText != null) {
//                                                selectedText.setTextColor(Color.BLACK);
//                                            } else {
//                                                selectedText.setTextColor(Color.BLACK);
//
//                                            }
//
//                                            zoneID = mArrZones.get(position).getID();
//                                            if (zoneID != 0) {
//                                                callService(50);//fetch Area
//                                            }
//
//                                        }
//
//                                        @Override
//                                        public void onNothingSelected(AdapterView<?> parent) {
//                                        }
//                                    });
//                                    dialog.dismiss();
//                                }
//                            });
//                    temp.show();
//                    temp.getTitleTextView().setTextColor(Color.BLACK);
//                    temp.getSearchBox().setTextColor(getResources().getColorStateList(R.color.innercolor));
//                }
//                return true;
//            }
//        });
//    }
    private void startReportsActivity() {
        Intent intent = new Intent(mContext, ReportsActivity.class);
        intent.putExtra(AppBundles.BUNDLE_CUSTOMER_OBJ, mArrCustomers.get(customerPosition));
        intent.putExtra(AppBundles.BUNDLE_ORDER, "Online");
        intent.putExtra(AppBundles.BUNDLE_VENDOR_TYPE,spVendor.getSelectedItem().toString());
        startActivity(intent);
        this.dismiss();
    }

    private void loadCities(final int position) {
        new Thread(new Runnable() {
            public void run() {
                List<Cities> something = AppDataBase.getInstance(getContext())
                        .cityDao()
                        .getCities(mArrRegion.get(position).getID());

                citiesList = something;
                for (int i = 0; i < citiesList.size(); i++) {
//                    citiesList.add(something.get(i));
                    Log.d("ehtie", "cities: " + something.get(i).getCityName() + "\t RegionID: " + something.get(i).getRegionId());
                }

            }
        }).start();
    }

}