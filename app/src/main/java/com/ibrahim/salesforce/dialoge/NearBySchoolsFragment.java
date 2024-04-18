package com.ibrahim.salesforce.dialoge;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.ibrahim.salesforce.R;
import com.ibrahim.salesforce.activity.DashboardActivity;
import com.ibrahim.salesforce.activity.NearBySchoolsActivity;
import com.ibrahim.salesforce.model.SampleSearchModel;
import com.ibrahim.salesforce.network.ApiService;
import com.ibrahim.salesforce.network.RequestCode;
import com.ibrahim.salesforce.network.RestCallbackObject;
import com.ibrahim.salesforce.network.RestClient;
import com.ibrahim.salesforce.network.ServerCodes;
import com.ibrahim.salesforce.network.ServerConnectListenerObject;
import com.ibrahim.salesforce.offline.AppDataBase;
import com.ibrahim.salesforce.offline.Cities;
import com.ibrahim.salesforce.offline.SchLocations;
import com.ibrahim.salesforce.response.CustomersRelatedtoSO;
import com.ibrahim.salesforce.response.GetAreasResponse;
import com.ibrahim.salesforce.response.GetCitiesResponse;
import com.ibrahim.salesforce.response.GetServerResponse;
import com.ibrahim.salesforce.response.Region;
import com.ibrahim.salesforce.response.SLocations;
import com.ibrahim.salesforce.response.ServerResponse;
import com.ibrahim.salesforce.utilities.AppKeys;
import com.ibrahim.salesforce.utilities.Utility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import retrofit2.Call;

public class NearBySchoolsFragment extends DialogFragment implements ServerConnectListenerObject {
    private Spinner mSpnRegion, mSpnCustomersRelatedToSo, mSpnCity, mSpnArea;
    private Button mBtnCheckin;
    private ArrayAdapter<CustomersRelatedtoSO> adapter;
    private GetServerResponse mLoginResponse;
    private GetAreasResponse mNewAreaResponse;
    private Context mContext;
    private List<GetAreasResponse.Area> mArrAreas;
    private ProgressDialog mDialog;
    private ApiService mService;
    private int regionPosition, customerPosition = 0, cityPosition = 0, areaPosition = 0, schoolPosition = 0;
    private RelativeLayout mRlRegionCustomers;
    //NearBySchoolsFragment dialogFragment;
    List<Region> mArrRegion;
    List<CustomersRelatedtoSO> mArrCustomers;
    List<GetCitiesResponse.City> arrCitiesRelatedToRegion;
    List<SLocations> arrSchoolsReleatedToArea;

    List<Cities> citiesList;
    ArrayList<SampleSearchModel> sampleSearchModels;
    ArrayList<SampleSearchModel> newsampleSearchModels;
    private ArrayAdapter<GetCitiesResponse.City> cityAdapter;
    private int callType = 0;
    private Spinner spVendorType;
    private RelativeLayout rlVendorType;

    public NearBySchoolsFragment() {
    }

    public static NearBySchoolsFragment newInstance(String title, Context mContext, int type) {
        Paper.init(mContext);
        NearBySchoolsFragment frag = new NearBySchoolsFragment();
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
        View view = inflater.inflate(R.layout.dialog_near_by_schools, container, false);
        callType = getArguments().getInt("showType");
        Log.d("callType", "callType: " + callType);
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.bg_round_corners);
        initComponents(view);
        Log.d("class_name", this.getClass().getSimpleName());

        return view;
    }


    class SaveTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            final List<SchLocations> slc = new ArrayList<>();

            for (int i=0; i<=arrSchoolsReleatedToArea.size()-1; i++) {

//                slc.set(i, arrSchoolsReleatedToArea.get(i).getSchoolID());

                slc.add(new SchLocations(arrSchoolsReleatedToArea.get(i).getSchoolID(),
                        arrSchoolsReleatedToArea.get(i).getSchoolName(),
                        arrSchoolsReleatedToArea.get(i).getLatitude(),
                        arrSchoolsReleatedToArea.get(i).getLongitude()));
            }
//                slc.add(new SchLocations(1, "ABC", 1.0, 1.2));
            //adding to database
            AppDataBase.getInstance(getContext()).schLocationsDao()
                    .insertSchLocation(slc);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getContext(), "Saved", Toast.LENGTH_LONG).show();
        }
    }


    private void initComponents(View view) {
        spVendorType=view.findViewById(R.id.spVendorType);
        rlVendorType=view.findViewById(R.id.rl_vendor_type);
        mSpnRegion = view.findViewById(R.id.spn_region);
        mSpnCustomersRelatedToSo = view.findViewById(R.id.spn_customers);
        mBtnCheckin = view.findViewById(R.id.btnCheckIn);
        mRlRegionCustomers = view.findViewById(R.id.rl_region_customers);
        mSpnArea = view.findViewById(R.id.spn_pop_area);
        mLoginResponse = Paper.book().read(AppKeys.KEY_LOGIN_RESPONSE);
        mDialog = new ProgressDialog(mContext);
        mArrCustomers = new ArrayList<CustomersRelatedtoSO>();
        mDialog.setCancelable(false);
        mSpnCity = view.findViewById(R.id.spn_city);
        setRegionSpinner();
        sampleSearchModels = new ArrayList<>();
        newsampleSearchModels = new ArrayList<>();
        arrSchoolsReleatedToArea = new ArrayList<>();
        mArrAreas = new ArrayList<>();
        if (callType == 10) {
            mBtnCheckin.setText("Delete");
        }

        mBtnCheckin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TAG", "Size: " + mNewAreaResponse.getAreas().size());
                for (int i = 0; i < mNewAreaResponse.getAreas().size(); i++) {
                    Log.e("TAG", "id: " + mNewAreaResponse.getAreas().get(i).getID());
                    Log.e("TAG", "Name: " + mNewAreaResponse.getAreas().get(i).getName());
                }
                startReportsActivity();
                dismiss();
            }
        });

        if (callType == 15) {
            mBtnCheckin.setText("Search");
        }

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
        mSpnRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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
                    callService(5);
                } catch (Exception ex) {
                    Toast.makeText(mContext, "Please Wait for data to populate", Toast.LENGTH_LONG).show();
                }
            }
        }, 600);
        mDialog.dismiss();
        mSpnCity.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    arrCitiesRelatedToRegion.clear();
                    new SimpleSearchDialogCompat(getActivity(), "Search...",
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
                                            cityPosition = position;
                                            callService(5);
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {
                                        }
                                    });
                                    dialog.dismiss();
                                }
                            }).show();
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
        rlVendorType.setVisibility(View.VISIBLE);
        mSpnArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                areaPosition = position;
                setVendorSpinner();



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setVendorSpinner() {

        ArrayList<String> vendors=new ArrayList<String>();
        vendors.add("School");
        vendors.add("Shop");
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                requireContext(),android.R.layout.simple_spinner_item,vendors
        );

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the ArrayAdapter to the Spinner
        spVendorType.setAdapter(adapter);

        // Set the default selected item to "School"
        spVendorType.setSelection(0);


        spVendorType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                callService(1);
//                Toast.makeText(getContext(), "Selected City areaID :" + areaId, Toast.LENGTH_SHORT).show();
                callService(99);
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
        sampleSearchModels.clear();
        for (int i = 0; i < mArrCustomers.size(); i++) {
            CustomersRelatedtoSO customer = mArrCustomers.get(i);
            String name = customer.getShopName();
            int id = customer.getID();
            sampleSearchModels.add(new SampleSearchModel(name, id));

        }
        mDialog.dismiss();
        mSpnCustomersRelatedToSo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mArrCustomers.clear();
                    new SimpleSearchDialogCompat(getActivity(), "Search...",
                            "What are you looking for...?", null, sampleSearchModels,
                            new SearchResultListener<SampleSearchModel>() {
                                @Override
                                public void onSelected(BaseSearchDialogCompat dialog,
                                                       SampleSearchModel item, int position) {
                                    mArrCustomers.add(new CustomersRelatedtoSO(item.getID(), false, item.getTitle()));
                                    adapter = new ArrayAdapter<CustomersRelatedtoSO>(mContext, android.R.layout.simple_spinner_dropdown_item, mArrCustomers);
                                    mSpnCustomersRelatedToSo.setAdapter(adapter);
                                    mSpnCustomersRelatedToSo.setVisibility(View.VISIBLE);
                                    mSpnCustomersRelatedToSo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            customerPosition = position;

                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {
                                        }

                                    });
                                    dialog.dismiss();
                                }
                            }).show();
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
        } else
            return true;
    }

    private void callService(int type) {
        mService = RestClient.getInstance(mContext);
        if (type == 1) {
            mDialog.show();
            mDialog.setMessage("Loading your customers,please wait...");
            Log.e("TAG", "Customers : ");
            Call<GetServerResponse> userObject = mService.getCustomerRelatedToSo(arrCitiesRelatedToRegion.get(cityPosition).getID(), mArrAreas.get(areaPosition).getID(), spVendorType.getSelectedItem().toString());

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
        } else if (type == 99) {
            mDialog.show();
            mDialog.setMessage("Fetching Schools please wait...");
            mService = RestClient.getInstance(mContext);
//            Call<GetServerResponse> userObject = mService.getRetailerSchoolsLocation(1, 1);
            Call<GetServerResponse> userObject = mService.getRetailerSchoolsLocation(mLoginResponse.getData().getSOID(), mArrAreas.get(areaPosition).getID());
            RestCallbackObject callbackObject = new RestCallbackObject((Activity) mContext, NearBySchoolsFragment.this, RequestCode.API_GET_RETAILERS_LOCATIONS).showProgress(true, 0).dontHideProgress(false);
            userObject.enqueue(callbackObject);
        } else {
            if (type == 7) {
//                try {
                mBtnCheckin.setEnabled(false);
                Call<ServerResponse> userObject = mService.validateLatLong(mArrCustomers.get(customerPosition).getID(), DashboardActivity.mLocationService.getLatitude(), DashboardActivity.mLocationService.getLongitude());
                RestCallbackObject callbackObject = new RestCallbackObject((Activity) mContext, this, RequestCode.VALIDATE_LAT_LONG).showProgress(true, 0).dontHideProgress(false);
                userObject.enqueue(callbackObject);

                /*}catch (Exception e){
                    e.printStackTrace();
                    Log.e("MyCrash", e.getMessage());
                }*/
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
            rlVendorType.setVisibility(View.GONE);
            mSpnCustomersRelatedToSo.setVisibility(View.GONE);
        } else if (requestCode == RequestCode.API_GET_RETAILERS_LOCATIONS) {
            Toast.makeText(mContext, "Cannot validate latlong :" + requestCode, Toast.LENGTH_SHORT).show();
            Log.e("TAG", "onFailure: " + requestCode);
        }

        /*else if (requestCode == RequestCode.GET_CUSTOMERS_RELATED_TO_AREA) {
            mSpnCustomersRelatedToSo.setVisibility(View.GONE);
            Toast.makeText(mContext, "No Customers Associated", Toast.LENGTH_SHORT).show();
        } */
        else {
//            Toast.makeText(mContext, "API Failed--" + requestCode + error, Toast.LENGTH_SHORT).show();
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
                startReportsActivity();
            } else {
                Toast.makeText(mContext, serverResponse.getMessage(), Toast.LENGTH_LONG).show();
                this.dismiss();
            }
        } else if (requestCode == RequestCode.GET_AREAS_FOR_CITY) {
            if (mDialog.isShowing()) {
                mDialog.hide();
            }
            mNewAreaResponse = (GetAreasResponse) object;
            if (mNewAreaResponse.getAreas() == null || mNewAreaResponse.getAreas().isEmpty()) {
                mSpnCustomersRelatedToSo.setVisibility(View.GONE);
                Toast.makeText(mContext, "There are no areas associated", Toast.LENGTH_SHORT).show();
            } else {
                mSpnArea.setVisibility(View.VISIBLE);
                setAreaSpinner(mNewAreaResponse);
            }
        } else if (requestCode == RequestCode.DELETE_SCHOOLS) {
            ServerResponse serverResponse = (ServerResponse) object;
            Toast.makeText(mContext, serverResponse.getMessage(), Toast.LENGTH_LONG).show();
            callService(1);
        } else if (requestCode == RequestCode.GET_CITIES_REQUEST_CODE) {
            if (((GetCitiesResponse) object).getCities() == null || ((GetCitiesResponse) object).getCities().isEmpty()) {
                mSpnArea.setVisibility(View.GONE);
                rlVendorType.setVisibility(View.GONE);
                mSpnCustomersRelatedToSo.setVisibility(View.GONE);
                Toast.makeText(mContext, "There are no cities in this ", Toast.LENGTH_SHORT).show();
                mDialog.dismiss();
            } else {
                mSpnCity.setVisibility(View.VISIBLE);
                arrCitiesRelatedToRegion = ((GetCitiesResponse) object).getCities();
                setCitySpinner();
            }
        } else if (requestCode == RequestCode.API_GET_RETAILERS_LOCATIONS) {
            GetServerResponse serverResponse = (GetServerResponse) object;
            arrSchoolsReleatedToArea = serverResponse.getSLocations();
        }

        /*else if (requestCode == RequestCode.GET_CUSTOMERS_RELATED_TO_AREA) {
            GetServerResponse serverResponse = (GetServerResponse) object;
            if (serverResponse.getCustomersRelatedtoSO() != null && !serverResponse.getCustomersRelatedtoSO().isEmpty()) {
                mSpnCustomersRelatedToSo.setVisibility(View.VISIBLE);
                mArrCustomers = ((GetServerResponse) object).getCustomersRelatedtoSO();
                setCustomerSpinner();
                Log.e("ServerResponse", String.valueOf(mLoginResponse.getData().getSOID()));
                Log.e("ServerResponse", String.valueOf(arrCitiesRelatedToRegion.get(cityPosition).getID()));
                mBtnCheckin.setEnabled(true);
                mLoginResponse.getData().setCustomersRelatedtoSO(serverResponse.getCustomersRelatedtoSO());
                Paper.book().write(AppKeys.KEY_LOGIN_RESPONSE, mLoginResponse);
                //hassan Usman Test 2
            }

        }*/
        mDialog.hide();
    }

    private void startReportsActivity() {
        Intent intent = new Intent(mContext, NearBySchoolsActivity.class);
//        intent.putExtra(AppBundles.BUNDLE_CUSTOMER_OBJ, String.valueOf(arrSchoolsReleatedToArea.get(schoolPosition)));
//        intent.putExtra(AppBundles.BUNDLE_ORDER, "Online");
        intent.putExtra("myloc", (Serializable) arrSchoolsReleatedToArea);

//        Bundle bundle = new Bundle();
//        bundle.putParcelable("myloc", (Parcelable) arrSchoolsReleatedToArea);
//        intent.putExtras(bundle);
        startActivity(intent);
        this.dismiss();
//        dismiss();
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

    @Override
    public void dismiss() {
        super.dismiss();
//        SaveTask saveTask = new SaveTask();
//        saveTask.execute();
//        startActivity(new Intent(getContext(), NearBySchoolsActivity.class));
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }
}