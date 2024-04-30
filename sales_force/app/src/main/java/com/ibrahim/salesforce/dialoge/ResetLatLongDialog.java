package com.app.salesforce.dialoge;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.app.salesforce.R;
import com.app.salesforce.activity.DashboardActivity;
import com.app.salesforce.model.SampleSearchModel;
import com.app.salesforce.network.ApiService;
import com.app.salesforce.network.RequestCode;
import com.app.salesforce.network.RestCallbackObject;
import com.app.salesforce.network.RestClient;
import com.app.salesforce.network.ServerCodes;
import com.app.salesforce.network.ServerConnectListenerObject;
import com.app.salesforce.response.CustomersRelatedtoSO;
import com.app.salesforce.response.GetAreasResponse;
import com.app.salesforce.response.GetCitiesResponse;
import com.app.salesforce.response.GetServerResponse;
import com.app.salesforce.response.Region;
import com.app.salesforce.response.ServerResponse;
import com.app.salesforce.utilities.AppKeys;
import com.app.salesforce.utilities.Utility;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import retrofit2.Call;


public class ResetLatLongDialog extends DialogFragment implements ServerConnectListenerObject {

    private Spinner mSpnCustomersRelatedToSo, mSpnRegion, mSpnCity, mSpnArea;
    private Button mBtnResetLocation;
    private ArrayAdapter<CustomersRelatedtoSO> adapter;
    private GetServerResponse mLoginResponse;
    private GetAreasResponse mNewAreaResponse;
    private Context mContext;
    private ProgressDialog mDialog;
    private List<GetAreasResponse.Area> mArrAreas;
    private ApiService mService;
    private int regionPosition = 0, customerPOsition = 0, cityPosition = 0, AreaPosition;
    private RelativeLayout mRlRegionCustomers;
    List<Region> mArrRegion;
    List<GetCitiesResponse.City> arrCitiesRelatedToRegion;
    List<CustomersRelatedtoSO> mArrCustomers;
    ArrayList<SampleSearchModel> sampleSearchModels;
    ArrayList<SampleSearchModel> newSampleSearchModels;
    ArrayAdapter<GetCitiesResponse.City> cityAdapter;
    private String vendorType="School";

    public ResetLatLongDialog() {
    }

    public static ResetLatLongDialog newInstance(String title, Context mContext) {
        Paper.init(mContext);
        ResetLatLongDialog frag = new ResetLatLongDialog();
        frag.mContext = mContext;
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_reset_lat_long, container, false);
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.bg_round_corners);
        initComponents(view);
        return view;
    }

    private void initComponents(View view) {
        arrCitiesRelatedToRegion = new ArrayList<GetCitiesResponse.City>();
        mSpnCity = view.findViewById(R.id.spn_City_ResetLatLong);
        mSpnRegion = view.findViewById(R.id.spn_region);
        mSpnCustomersRelatedToSo = view.findViewById(R.id.spn_customers);
        mSpnArea = view.findViewById(R.id.spn_pop_reset_area);
        mBtnResetLocation = view.findViewById(R.id.btnResetLocation);
        mLoginResponse = Paper.book().read(AppKeys.KEY_LOGIN_RESPONSE);
        mRlRegionCustomers = view.findViewById(R.id.rl_region_customers);
        mDialog = new ProgressDialog(mContext);
        mDialog.setCancelable(false);
        sampleSearchModels = new ArrayList<>();
        newSampleSearchModels = new ArrayList<>();
        mArrAreas = new ArrayList<>();
        setRegionSpinner();
        mBtnResetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.isNetworkAvailable(mContext)) {
                    if (validateForm())
                        callService(3);
                } else {
                    Toast.makeText(mContext, getString(R.string.str_no_internet), Toast.LENGTH_SHORT).show();
                }
            }
        });

        RadioButton rbShop=view.findViewById(R.id.rb_shop);

        rbShop.setOnCheckedChangeListener(new RadioButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    vendorType="Shop";
                }
            }
        });
        RadioButton rbSchool=view.findViewById(R.id.rb_school);
        rbSchool.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    vendorType="School";
                }
            }
        });

    }

    private void setRegionSpinner() {
        mArrRegion = mLoginResponse.getData().getRegion();
//        if (mArrRegion.size() > 1) {
//            mArrRegion.add(0, new Region(0, "Select Region"));
//        } else
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                callService(2);
            }
        }, 600);
        ArrayAdapter<Region> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, mArrRegion);
        mSpnRegion.setAdapter(adapter);
        mSpnRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView)view).setTextColor(getResources().getColorStateList(R.color.black));
                if (Utility.isNetworkAvailable(mContext)) {
//                    if (position != 0) {
                    regionPosition = position;
                    callService(2);
//                    }
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
        mSpnCity.setVisibility(View.VISIBLE);
        newSampleSearchModels.clear();
        for (int i = 0; i < arrCitiesRelatedToRegion.size(); i++) {
            GetCitiesResponse.City city = arrCitiesRelatedToRegion.get(i);
            String name = city.getName();
            int id = city.getID();
            newSampleSearchModels.add(new SampleSearchModel(name, id));

        }
//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    callService(5);
//                } catch (Exception ex) {
//                    Toast.makeText(mContext, "Please Wait for data to populate", Toast.LENGTH_LONG).show();
//                }
//            }
//        }, 600);
//        mDialog.dismiss();

        mSpnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView)view).setTextColor(getResources().getColorStateList(R.color.black));
                cityPosition = position;
                callService(5);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        mSpnCity.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    arrCitiesRelatedToRegion.clear();
                    new SimpleSearchDialogCompat(getActivity(), "Search...",
                            "What are you looking for...?", null, newSampleSearchModels,
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
        mSpnArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView)view).setTextColor(getResources().getColorStateList(R.color.black));

                AreaPosition = position;
                callService(1);

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

        mSpnCustomersRelatedToSo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView)view).setTextColor(getResources().getColorStateList(R.color.black));
                customerPOsition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


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
                                            ((TextView)view).setTextColor(getResources().getColorStateList(R.color.black));

                                            customerPOsition = position;

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
            Toast.makeText(mContext, "Can't get your location", Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
    }

    private void callService(int type) {
        mService = RestClient.getInstance(mContext);
        mDialog.show();
        if (type == 2) {
            mDialog.setMessage("Loading your Cities,please wait...");
            Call<GetCitiesResponse> userObject = mService.getCities(mArrRegion.get(regionPosition).getID(), mLoginResponse.getData().getSOID());
            RestCallbackObject callbackObject = new RestCallbackObject((Activity) mContext, this, RequestCode.GET_CITIES_REQUEST_CODE).showProgress(true, 0).dontHideProgress(false);
            userObject.enqueue(callbackObject);
        } else if (type == 5) {
            mDialog.show();
            mDialog.setMessage("Fetching areas please wait...");
            mService = RestClient.getInstance(mContext);
            Call<GetAreasResponse> userObject = mService.getAreas(String.valueOf(arrCitiesRelatedToRegion.get(cityPosition).getID()), mLoginResponse.getData().getSOID());
            RestCallbackObject callbackObject = new RestCallbackObject((Activity) mContext, this, RequestCode.GET_AREAS_FOR_CITY).showProgress(true, 0).dontHideProgress(false);
            userObject.enqueue(callbackObject);
        } else if (type == 1) {
            mDialog.setMessage("Loading your customers,please wait...");
            Call<GetServerResponse> userObject = mService.getCustomerRelatedToSo(arrCitiesRelatedToRegion.get(cityPosition).getID(), mArrAreas.get(AreaPosition).getID(), vendorType);
            RestCallbackObject callbackObject = new RestCallbackObject((Activity) mContext, this, RequestCode.GET_CUSTOMER_RELATED_TO_SO).showProgress(true, 0).dontHideProgress(false);
            userObject.enqueue(callbackObject);
        } else {
                mDialog.setMessage("Please wait...");
                mBtnResetLocation.setEnabled(false);
                Call<ServerResponse> userObject = mService.resetLatLong(mArrCustomers.get(customerPOsition).getID(), DashboardActivity.mLocationService.getLatitude(), DashboardActivity.mLocationService.getLongitude());
                RestCallbackObject callbackObject = new RestCallbackObject((Activity) mContext, this, RequestCode.RESET_LAT_LONG).showProgress(true, 0).dontHideProgress(false);
                userObject.enqueue(callbackObject);
            }
    }



    @Override
    public void onFailure(String error, RequestCode requestCode) {
        if (requestCode == RequestCode.RESET_LAT_LONG) {
            Toast.makeText(mContext, error, Toast.LENGTH_LONG).show();
            this.dismiss();
        } else if (requestCode == RequestCode.GET_CITIES_REQUEST_CODE) {
            Toast.makeText(mContext, error, Toast.LENGTH_LONG).show();
            mSpnCity.setVisibility(View.GONE);
            mSpnArea.setVisibility(View.GONE);
            mSpnCustomersRelatedToSo.setVisibility(View.GONE);
            mDialog.dismiss();
        } else if (requestCode == RequestCode.GET_AREAS_FOR_CITY) {
            Toast.makeText(mContext, error, Toast.LENGTH_LONG).show();
            mSpnArea.setVisibility(View.GONE);
            mSpnCustomersRelatedToSo.setVisibility(View.GONE);
            mDialog.dismiss();
        } else if (requestCode == RequestCode.GET_CUSTOMER_RELATED_TO_SO) {
            Toast.makeText(mContext, error, Toast.LENGTH_LONG).show();
            mSpnCustomersRelatedToSo.setVisibility(View.GONE);
            mDialog.dismiss();
        }
    }

    @Override
    public void onSuccess(Object object, RequestCode requestCode) {
        if (requestCode == RequestCode.RESET_LAT_LONG) {
            ServerResponse serverResponse = (ServerResponse) object;
            if (serverResponse.getResultType() == ServerCodes.ServerResultType.SERVER_RESULT_SUCCESS) {
                Toast.makeText(mContext, serverResponse.getMessage(), Toast.LENGTH_LONG).show();
                this.dismiss();
            }
        } else if (requestCode == RequestCode.GET_CITIES_REQUEST_CODE) {
            if (((GetCitiesResponse) object).getCities() == null || ((GetCitiesResponse) object).getCities().isEmpty()) {
                Toast.makeText(mContext, "There are no cities in this Region ", Toast.LENGTH_SHORT).show();
                mSpnCity.setVisibility(View.GONE);
                mSpnArea.setVisibility(View.GONE);
                mSpnCustomersRelatedToSo.setVisibility(View.GONE);
            } else {
                mSpnCity.setVisibility(View.VISIBLE);
                arrCitiesRelatedToRegion = ((GetCitiesResponse) object).getCities();
                setCitySpinner();
            }
        } else if (requestCode == RequestCode.GET_AREAS_FOR_CITY) {
            if (mDialog.isShowing()) {
                mDialog.hide();
            }
            mNewAreaResponse = (GetAreasResponse) object;
            if (mNewAreaResponse.getAreas() == null || mNewAreaResponse.getAreas().isEmpty()) {
                Toast.makeText(mContext, "There are no areas associated", Toast.LENGTH_SHORT).show();
                mSpnArea.setVisibility(View.GONE);
                mSpnCustomersRelatedToSo.setVisibility(View.GONE);
            } else {
                mSpnArea.setVisibility(View.VISIBLE);
                setAreaSpinner(mNewAreaResponse);
            }
        } else if (requestCode == RequestCode.GET_CUSTOMER_RELATED_TO_SO) {
            GetServerResponse serverResponse = (GetServerResponse) object;
            if (serverResponse.getCustomersRelatedtoSO() != null && !serverResponse.getCustomersRelatedtoSO().isEmpty()) {
                mArrCustomers = serverResponse.getCustomersRelatedtoSO();
                mSpnCustomersRelatedToSo.setVisibility(View.VISIBLE);
                setCustomerSpinner();
                mLoginResponse.getData().setCustomersRelatedtoSO(serverResponse.getCustomersRelatedtoSO());
                Paper.book().write(AppKeys.KEY_LOGIN_RESPONSE, mLoginResponse);
            } else {
                mSpnCustomersRelatedToSo.setVisibility(View.GONE);
            }
        }
        mDialog.hide();
    }

}

