package com.ibrahim.salesforce.dialoge;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ibrahim.salesforce.R;
import com.ibrahim.salesforce.activity.MultiPurposeActivity;
import com.ibrahim.salesforce.adapters.AddRemoveItem;
import com.ibrahim.salesforce.adapters.DealerAdapter;
import com.ibrahim.salesforce.model.SampleSearchModel;
import com.ibrahim.salesforce.network.ApiService;
import com.ibrahim.salesforce.network.RequestCode;
import com.ibrahim.salesforce.network.RestCallbackObject;
import com.ibrahim.salesforce.network.RestClient;
import com.ibrahim.salesforce.network.ServerConnectListenerObject;
import com.ibrahim.salesforce.response.CustomersRelatedtoSO;
import com.ibrahim.salesforce.response.GetAreasResponse;
import com.ibrahim.salesforce.response.GetCitiesResponse;
import com.ibrahim.salesforce.response.GetServerResponse;
import com.ibrahim.salesforce.response.Region;
import com.ibrahim.salesforce.utilities.AppBundles;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class MultiPurposeDialougeFragment extends DialogFragment implements ServerConnectListenerObject, View.OnClickListener, AddRemoveItem.AddRemoveDealer {

    //DetailsDialogFragment dialogFragment;
    List<Region> mArrRegion;
    List<CustomersRelatedtoSO> mArrCustomers;
    List<CustomersRelatedtoSO> mArrDealers;
    List<CustomersRelatedtoSO> mArrSelectedDealers;
    List<GetCitiesResponse.City> arrCitiesRelatedToRegion;
    ArrayList<SampleSearchModel> sampleSearchModels;
    ArrayList<SampleSearchModel> sampleSearchModelDealers;
    ArrayList<SampleSearchModel> newsampleSearchModels;
    TextView txtMulSelectDealer;
    private Spinner mSpnRegion, mSpnCustomersRelatedToSo, mSpnCity, mSpnArea, mSpnDealer;
    private Button mBtnCheckin;
    private ArrayAdapter<CustomersRelatedtoSO> adapter;
    private GetServerResponse mLoginResponse;
    private GetAreasResponse mNewAreaResponse;
    private Context mContext;
    TextView txtAreaText, txtCityText, txtCustomerText;
    private List<GetAreasResponse.Area> mArrAreas;
    private ProgressDialog mDialog;
    private ApiService mService;
    private int regionPosition, customerPosition = 0, cityPosition = -1, areaPosition = -1, newDealerPosition = 0;
    private RelativeLayout rlShowDealers;
    private ArrayAdapter<GetCitiesResponse.City> cityAdapter;
    private int callType = 0;
    private String visitPurpose;
    private RelativeLayout mRlRegionCustomers, mRlRegionDealers;
    private ImageButton imgExpand;
    private RecyclerView.LayoutManager mLayoutManager, sellerLayoutManager;
    private RecyclerView rvDealers;
    private RecyclerView.Adapter mAdapter, sellerAdapter;
    private RelativeLayout rlVendorType;
    private Spinner spVendorType;

    public MultiPurposeDialougeFragment() {
        // Required empty public constructor
    }

    public static MultiPurposeDialougeFragment newInstance(String title, Context mContext, int type) {
        Paper.init(mContext);
        MultiPurposeDialougeFragment frag = new MultiPurposeDialougeFragment();
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
        View view = inflater.inflate(R.layout.fragment_multi_purpose_dialouge, container, false);
        callType = getArguments().getInt("showType");
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.bg_round_corners);
        initComponents(view);
        return view;
    }

    private void initComponents(View view) {
        rlVendorType=view.findViewById(R.id.rl_vendor_type);
        spVendorType=view.findViewById(R.id.spVendorType);
        mSpnRegion = view.findViewById(R.id.spn_mul_region);
        mSpnCustomersRelatedToSo = view.findViewById(R.id.spn_mul_customers);
        mBtnCheckin = view.findViewById(R.id.btnCheckInMul);
        mRlRegionCustomers = view.findViewById(R.id.rl_region_mul_customers);
        mRlRegionDealers = view.findViewById(R.id.rl_region_mul_dealers);
        mSpnDealer = view.findViewById(R.id.spn_mul_dealers);
        txtMulSelectDealer = view.findViewById(R.id.txtSelectDealer);
        txtAreaText = view.findViewById(R.id.tvAreaText);
        txtCityText = view.findViewById(R.id.tvCityText);
        txtCustomerText = view.findViewById(R.id.txtCustomer);
        rlShowDealers = view.findViewById(R.id.rl_select_dealers);
        rlShowDealers.setOnClickListener(this);
        mSpnArea = view.findViewById(R.id.spn_pop_mul_area);
        imgExpand = view.findViewById(R.id.ib_show_items_rc);
        imgExpand.setOnClickListener(this);
        rvDealers = view.findViewById(R.id.rvShowDealers);
        mLoginResponse = Paper.book().read(AppKeys.KEY_LOGIN_RESPONSE);
        mDialog = new ProgressDialog(mContext);
        mArrCustomers = new ArrayList<CustomersRelatedtoSO>();
        mArrDealers = new ArrayList<CustomersRelatedtoSO>();
        mArrSelectedDealers = new ArrayList<CustomersRelatedtoSO>();
        mDialog.setCancelable(false);
        mSpnCity = view.findViewById(R.id.spn_mul_city);
        setRegionSpinner();
        sampleSearchModels = new ArrayList<>();
        sampleSearchModelDealers = new ArrayList<>();
        newsampleSearchModels = new ArrayList<>();
        mArrAreas = new ArrayList<>();


        mBtnCheckin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.isNetworkAvailable(mContext)) {
                    if (callType == 3) {
                        if (mArrSelectedDealers.size() <= 0) {
                            Toast.makeText(mContext, "Please Select any Dealer First", Toast.LENGTH_SHORT).show();
                        } else {
                            startMultiPurposeActivity();
                        }
                    } else {
                        if (mSpnCustomersRelatedToSo.getVisibility() != View.VISIBLE && !mLoginResponse.getData().getIsRegionalHead()) {
                            Toast.makeText(mContext, "Please Select any Customer First", Toast.LENGTH_SHORT).show();
                        } else {
                            startMultiPurposeActivity();
                        }
                    }
                }
            }

        });
        if (callType == 1) {
            visitPurpose = "Combined Visit";
        } else if (callType == 2) {
            visitPurpose = "Events";
        } else if (callType == 3) {
            visitPurpose = "Dealer Visits";
        }
    }

    private void setRegionSpinner() {
        mArrRegion = mLoginResponse.getData().getRegion();
//        if (mArrRegion.size() > 1) {
//            Region region = new Region(0, "Select Region");
//            mArrRegion.add(0, region);
//        } else
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {

                    if (callType != 3) {
                        if (!mLoginResponse.getData().getIsRegionalHead()) {
                            callService(3);
                        } else {
                            callService(6);
                        }
                    } else {
                        callService(4);
                    }
                } catch (Exception ex) {
                    Toast.makeText(mContext, "Please Wait for data to populate", Toast.LENGTH_LONG).show();
                }
            }
        }, 600);
        ArrayAdapter<Region> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, mArrRegion);
        mSpnRegion.setAdapter(adapter);
        mSpnRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (Utility.isNetworkAvailable(mContext)) {
                    regionPosition = position;


                    if (callType != 3) {
                        if (!mLoginResponse.getData().getIsRegionalHead()) {
                            callService(3);
                        } else {
                            callService(6);
                        }
                    } else {
                        callService(4);
                    }
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
                    if (callType != 3) {
                        if (!mLoginResponse.getData().getIsRegionalHead()) {
                            callService(5);
                        } else {
                            callService(7);
                        }
                    } else {
                        callService(2);
                    }
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
                                    txtCityText.setVisibility(View.VISIBLE);
                                    mSpnCity.setVisibility(View.VISIBLE);
                                    mSpnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            cityPosition = position;
                                            if (callType != 3) {
                                                if (!mLoginResponse.getData().getIsRegionalHead()) {
                                                    callService(5);
                                                } else {
                                                    callService(7);
                                                }
                                            } else {
                                                callService(2);
                                            }
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
        txtAreaText.setVisibility(View.VISIBLE);
        mSpnArea.setVisibility(View.VISIBLE);
        rlVendorType.setVisibility(View.VISIBLE);
        mSpnArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                areaPosition = position;
                //TODO
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
                if (!mLoginResponse.getData().getIsRegionalHead()) {

                    if(cityPosition!=-1&&areaPosition!=-1) {
                        callService(1);
                    }
                }
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
        txtCustomerText.setVisibility(View.VISIBLE);
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
                                    txtCustomerText.setVisibility(View.VISIBLE);
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

    private void setDealerSpinner() {
        //mArrCustomers.add(0, new CustomersRelatedtoSO(0, false, "Select Customer"));
        adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, mArrDealers);
        mSpnDealer.setAdapter(adapter);
        mSpnDealer.setVisibility(View.VISIBLE);
        sampleSearchModelDealers.clear();
        for (int i = 0; i < mArrDealers.size(); i++) {
            CustomersRelatedtoSO customer = mArrDealers.get(i);
            String name = customer.getShopName();
            int id = customer.getID();
            sampleSearchModelDealers.add(new SampleSearchModel(name, id));

        }
        mDialog.dismiss();
        mSpnDealer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mArrDealers.clear();
                    new SimpleSearchDialogCompat(getActivity(), "Search...",
                            "What are you looking for...?", null, sampleSearchModelDealers,
                            new SearchResultListener<SampleSearchModel>() {
                                @Override
                                public void onSelected(BaseSearchDialogCompat dialog,
                                                       SampleSearchModel item, int position) {
                                    mArrDealers.add(new CustomersRelatedtoSO(item.getID(), false, item.getTitle()));
                                    adapter = new ArrayAdapter<CustomersRelatedtoSO>(mContext, android.R.layout.simple_spinner_dropdown_item, mArrDealers);
                                    mSpnDealer.setAdapter(adapter);
                                    mSpnDealer.setVisibility(View.VISIBLE);
                                    mSpnDealer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            newDealerPosition = position;

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

    private void callService(int type) {
        mService = RestClient.getInstance(mContext);
        if (type == 1) {
            mDialog.show();
            mDialog.setMessage("Loading your customers,please wait...");
            Call<GetServerResponse> userObject = mService.getCustomerRelatedToSo(arrCitiesRelatedToRegion.get(cityPosition).getID(), mArrAreas.get(areaPosition).getID(), spVendorType.getSelectedItem().toString());
            RestCallbackObject callbackObject = new RestCallbackObject((Activity) mContext, this, RequestCode.GET_CUSTOMERS_RELATED_TO_AREA).showProgress(true, 0).dontHideProgress(false);
            userObject.enqueue(callbackObject);
        } else if (type == 3) {
            mDialog.show();
            mDialog.setMessage("Loading Cities,please wait...");
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
        } else if (type == 2) {
            mDialog.show();
            mDialog.setMessage("Fetching Dealers please wait...");
            mService = RestClient.getInstance(mContext);
            Log.d("ehtie", "cityID: " + arrCitiesRelatedToRegion.get(cityPosition).getID());
            Call<GetServerResponse> userObject = mService.getDealers(arrCitiesRelatedToRegion.get(cityPosition).getID());
            RestCallbackObject callbackObject = new RestCallbackObject((Activity) mContext, this, RequestCode.GET_DEALER_CITY_WISE).showProgress(true, 0).dontHideProgress(false);
            userObject.enqueue(callbackObject);
        } else if (type == 4) {
            mDialog.show();
            mDialog.setMessage("Loading Cities,please wait...");
            Call<GetCitiesResponse> userObject = mService.getAllCities(mArrRegion.get(regionPosition).getID());
            RestCallbackObject callbackObject = new RestCallbackObject((Activity) mContext, this, RequestCode.GET_CITIES_REQUEST_CODE).showProgress(true, 0).dontHideProgress(false);
            userObject.enqueue(callbackObject);
        } else if (type == 6) {
            mDialog.show();
            mDialog.setMessage("Loading Cities,please wait...");
            Call<GetCitiesResponse> userObject = mService.getRegionalHeadCities(mArrRegion.get(regionPosition).getID(), mLoginResponse.getData().getSOID(), mLoginResponse.getData().getIsRegionalHead());
            RestCallbackObject callbackObject = new RestCallbackObject((Activity) mContext, this, RequestCode.GET_CITIES_REQUEST_CODE).showProgress(true, 0).dontHideProgress(false);
            userObject.enqueue(callbackObject);
        } else if (type == 7) {
            mDialog.show();
            mDialog.setMessage("Fetching areas please wait...");
            mService = RestClient.getInstance(mContext);
            Call<GetAreasResponse> userObject = mService.getRegionalHeadAreas(arrCitiesRelatedToRegion.get(cityPosition).getID(), mLoginResponse.getData().getSOID(), mLoginResponse.getData().getIsRegionalHead());
            RestCallbackObject callbackObject = new RestCallbackObject((Activity) mContext, this, RequestCode.GET_AREAS_FOR_CITY).showProgress(true, 0).dontHideProgress(false);
            userObject.enqueue(callbackObject);
        }
    }

    @Override
    public void onFailure(String error, RequestCode requestCode) {
        if (requestCode == RequestCode.GET_AREAS_FOR_CITY) {
            Toast.makeText(mContext, "No area Associated", Toast.LENGTH_SHORT).show();
            txtAreaText.setVisibility(View.GONE);
            mSpnArea.setVisibility(View.GONE);
            areaPosition=-1;
            rlVendorType.setVisibility(View.GONE);
            txtCustomerText.setVisibility(View.GONE);
            mSpnCustomersRelatedToSo.setVisibility(View.GONE);
        }else if(requestCode==RequestCode.GET_CITIES_REQUEST_CODE){
            txtAreaText.setVisibility(View.GONE);
            mSpnArea.setVisibility(View.GONE);
            rlVendorType.setVisibility(View.GONE);
            mSpnCity.setVisibility(View.GONE);
            cityPosition=-1;
            areaPosition=-1;
            txtCityText.setVisibility(View.GONE);
            txtCustomerText.setVisibility(View.GONE);
            mSpnCustomersRelatedToSo.setVisibility(View.GONE);
            Toast.makeText(mContext, "There are no cities associated ", Toast.LENGTH_SHORT).show();
            mDialog.dismiss();
        } else if (requestCode == RequestCode.GET_CUSTOMERS_RELATED_TO_AREA) {
            txtCustomerText.setVisibility(View.GONE);
            mSpnCustomersRelatedToSo.setVisibility(View.GONE);
            Toast.makeText(mContext, "No Customers Associated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "API Failed--" + requestCode + error, Toast.LENGTH_SHORT).show();
        }
        mDialog.dismiss();
    }

    @Override
    public void onSuccess(Object object, RequestCode requestCode) {
        if (requestCode == RequestCode.GET_AREAS_FOR_CITY) {
            if (mDialog.isShowing()) {
                mDialog.hide();
            }
            mNewAreaResponse = (GetAreasResponse) object;
            if (mNewAreaResponse.getAreas() == null || mNewAreaResponse.getAreas().isEmpty()) {
                txtCustomerText.setVisibility(View.GONE);
                mSpnCustomersRelatedToSo.setVisibility(View.GONE);
                Toast.makeText(mContext, "There are no areas associated", Toast.LENGTH_SHORT).show();
            } else {
                if (callType != 3) {
                    txtAreaText.setVisibility(View.VISIBLE);
                    mSpnArea.setVisibility(View.VISIBLE);
                    setAreaSpinner(mNewAreaResponse);
                }
            }
        } else if (requestCode == RequestCode.GET_CITIES_REQUEST_CODE) {
            if (((GetCitiesResponse) object).getCities() == null || ((GetCitiesResponse) object).getCities().isEmpty()) {
                txtAreaText.setVisibility(View.GONE);
                mSpnArea.setVisibility(View.GONE);
                rlVendorType.setVisibility(View.GONE);
                mSpnCity.setVisibility(View.GONE);
                cityPosition=-1;
                areaPosition=-1;
                txtCityText.setVisibility(View.GONE);
                txtCustomerText.setVisibility(View.GONE);
                mSpnCustomersRelatedToSo.setVisibility(View.GONE);
                Toast.makeText(mContext, "There are no cities associated ", Toast.LENGTH_SHORT).show();
                mDialog.dismiss();
            } else {
                txtCityText.setVisibility(View.VISIBLE);
                mSpnCity.setVisibility(View.VISIBLE);
                arrCitiesRelatedToRegion = ((GetCitiesResponse) object).getCities();
                setCitySpinner();
            }
        } else if (requestCode == RequestCode.GET_CUSTOMERS_RELATED_TO_AREA) {
            GetServerResponse serverResponse = (GetServerResponse) object;
            if (serverResponse.getCustomersRelatedtoSO() != null && !serverResponse.getCustomersRelatedtoSO().isEmpty()) {
                txtCustomerText.setVisibility(View.VISIBLE);
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

        } else if (requestCode == RequestCode.GET_DEALER_CITY_WISE) {
            GetServerResponse serverResponse = (GetServerResponse) object;

            if (serverResponse.getDealers() != null && !serverResponse.getDealers().isEmpty()) {
                mArrDealers = ((GetServerResponse) object).getDealers();
                rlShowDealers.setVisibility(View.VISIBLE);
                if (mArrDealers.size() > 0) {
                    rvDealers.setVisibility(View.GONE);
                    imgExpand.setBackgroundResource(R.drawable.ic_expand_more_black_24dp);
                    mLayoutManager = new LinearLayoutManager(mContext);
                    rvDealers.setLayoutManager(mLayoutManager);
                    mAdapter = new DealerAdapter(mArrDealers, this);
                    rvDealers.setAdapter(mAdapter);

                } else
                    Toast.makeText(mContext, "No Dealers found", Toast.LENGTH_SHORT).show();

                //
         /*
                txtMulSelectDealer.setVisibility(View.VISIBLE);
                mSpnDealer.setVisibility(View.VISIBLE);
                mArrDealers = ((GetServerResponse) object).getDealers();
                setDealerSpinner();
                Log.e("ServerResponse", String.valueOf(mLoginResponse.getData().getSOID()));
                Log.e("ServerResponse", String.valueOf(arrCitiesRelatedToRegion.get(cityPosition).getID()));*/
                mBtnCheckin.setEnabled(true);
            } else {
                rlShowDealers.setVisibility(View.GONE);
                Toast.makeText(mContext, "No dealers Associated", Toast.LENGTH_SHORT).show();
            }
        }
        mDialog.hide();
    }

    private void startMultiPurposeActivity() {
        Intent intent = new Intent(mContext, MultiPurposeActivity.class);
        Bundle args = new Bundle();
        if (callType != 3 && !mLoginResponse.getData().getIsRegionalHead()) {
            args.putSerializable(AppBundles.BUNDLE_CUSTOMER_OBJ, mArrCustomers.get(customerPosition));
        } else {
            args.putSerializable("ARRAYLIST", (Serializable) mArrSelectedDealers);
        }
        args.putSerializable(AppBundles.BUNDLE_VISIT_TYPE, visitPurpose);
        args.putSerializable(AppBundles.BUNDLE_ORDER, "Online");
        intent.putExtra("BUNDLE", args);
        startActivity(intent);
        this.dismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_select_dealers:
                if (mArrDealers.size() > 0) {
                    if (rvDealers.getVisibility() == View.GONE) {
                        rvDealers.setVisibility(View.VISIBLE);
                        imgExpand.setBackgroundResource(R.drawable.ic_expand_less_black_24dp);
                        mLayoutManager = new LinearLayoutManager(mContext);
                        rvDealers.setLayoutManager(mLayoutManager);
                        mAdapter = new DealerAdapter(mArrDealers, this);
                        rvDealers.setAdapter(mAdapter);
                    } else {
                        rvDealers.setVisibility(View.GONE);
                        imgExpand.setBackgroundResource(R.drawable.ic_expand_more_black_24dp);
                    }
                } else
                    Toast.makeText(mContext, "No Dealers found", Toast.LENGTH_SHORT).show();

                break;
        }
    }

    @Override
    public void addRemoveItem(CustomersRelatedtoSO item, boolean isAdd) {
        if (isAdd) {
            mArrSelectedDealers.add(item);
        } else {
            mArrSelectedDealers.remove(item);
        }
    }
}
