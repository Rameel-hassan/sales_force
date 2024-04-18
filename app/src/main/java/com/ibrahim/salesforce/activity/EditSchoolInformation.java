package com.ibrahim.salesforce.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ibrahim.salesforce.R;
import com.ibrahim.salesforce.adapters.AddRemoveItem;
import com.ibrahim.salesforce.adapters.BookSellerItemsAdapter;
import com.ibrahim.salesforce.adapters.SelectedBookSellerItemsAdapter;
import com.ibrahim.salesforce.adapters.SelectedPublisherItemsAdapter;
import com.ibrahim.salesforce.databinding.SchoolEditInformationActivityBinding;
import com.ibrahim.salesforce.model.SampleSearchModel;
import com.ibrahim.salesforce.network.ApiService;
import com.ibrahim.salesforce.network.RequestCode;
import com.ibrahim.salesforce.network.RestCallbackObject;
import com.ibrahim.salesforce.network.RestClient;
import com.ibrahim.salesforce.network.ServerCodes;
import com.ibrahim.salesforce.network.ServerConnectListenerObject;
import com.ibrahim.salesforce.request.SchoolRegRequest;
import com.ibrahim.salesforce.response.BookPublisherID;
import com.ibrahim.salesforce.response.BookSellerID;
import com.ibrahim.salesforce.response.CurrentSyllabus;
import com.ibrahim.salesforce.response.CustomersRelatedtoSO;
import com.ibrahim.salesforce.response.Fee;
import com.ibrahim.salesforce.response.FormData;
import com.ibrahim.salesforce.response.GetAreasResponse;
import com.ibrahim.salesforce.response.GetBookSellerResponse;
import com.ibrahim.salesforce.response.GetCitiesResponse;
import com.ibrahim.salesforce.response.GetServerResponse;
import com.ibrahim.salesforce.response.GetSubAreasResponse;
import com.ibrahim.salesforce.response.GetZonesResponse;
import com.ibrahim.salesforce.response.IdName;
import com.ibrahim.salesforce.response.Region;
import com.ibrahim.salesforce.response.ServerResponse;
import com.ibrahim.salesforce.utilities.AppKeys;
import com.ibrahim.salesforce.utilities.Utility;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import retrofit2.Call;

public class EditSchoolInformation extends AppCompatActivity implements ServerConnectListenerObject, View.OnClickListener, AddRemoveItem.AddRemoveBookSellers, AddRemoveItem.AddRemoveSyllabus {
    Spinner SnpName, mSpnRegion, mSpnCity, mSpnArea, mSpnNewArea, mSpnNewDistrict, mSpnNewTehsil, mSpnEduSys, mSpnFeeStruct, mSpnSyllSelectionDate, mSpnSessionDate, spZone, spSubArea;
    RecyclerView mrvItems;
    ImageButton ibShowPublishers;
    private GetServerResponse mLoginResponse;
    private GetCitiesResponse mCitiesResponse;
    private GetCitiesResponse mNewCitiesResponse;
    private GetAreasResponse mAreaResponse;
    private GetAreasResponse mNewAreaResponse;

    private GetSubAreasResponse newSubAreaResponse;
    private GetZonesResponse newZonesResponse;


    private GetBookSellerResponse mBookSellersResponse;


    private int newRegionId = -1, newCityId = -1, newZoneId = -1, newAreaId = -1, newSubAreaId = -1;
    private int newRegionPosition = -1, newCityPosition = -1, newZonePosition = -1, newAreaPosition = -1, newSubAreaPosition = -1;
    private ApiService mService;
    int soId;
    private String cityId;
    String Token;
    int studentStrength = 0;
    int cityIDtOSend = 0, areaIDtoSend = 0;
    RecyclerView mRvSellers;
    RecyclerView.LayoutManager sellerLayoutManager, mLayoutManager;
    RecyclerView.Adapter sellerAdapter, mAdapter;
    RelativeLayout rlShowSellers, viewZone, viewSubArea;
    private ArrayAdapter<CustomersRelatedtoSO> customerAdapter;
    private RelativeLayout rlShowPublishers;
    List<CustomersRelatedtoSO> mArrCustomers;
    List<GetCitiesResponse.City> arrCitiesRelatedToRegion;
    List<GetCitiesResponse.City> arrNewCities;
    ArrayList<SampleSearchModel> sampleSearchModels;
    ArrayList<SampleSearchModel> newSampleSearchModels;
    ArrayList<SampleSearchModel> schoolSampleSearchModels;
    List<FormData> editSchoolInformations;
    private List<GetAreasResponse.Area> mArrAreas;
    private List<GetAreasResponse.Area> mArrNewAreas;
    private List<GetBookSellerResponse.BookSeller> mArrBookSellers;
    private List<GetBookSellerResponse.BookSeller> mArrSelectedBookSellers;
    private List<String> mArrEduSys;
    private List<Fee> mArrFeeStructure;
    private List<String> mArrSessionDate;
    private List<String> mArrBookShop;
    private List<String> mArrSyllSelectionDate;
    private List<CurrentSyllabus> mArrSyllabusItems;
    private List<CurrentSyllabus> mArrSyllabusItemsSelected;
    EditSchoolInformation editSchoolInformation;
    private ProgressDialog mDialog;
    List<Region> mArrRegion;
    List<Region> newArrRegion;
    LinearLayout llMainLayout;
    String oldDistrict = "";
    int oldDistrID = 0;
    String oldTehsil = "";
    int oldTehsID = 0;
    FormData formData;
    SchoolRegRequest schoolRegRequest;
    private int regionPosition, customerPosition = 0, cityPosition = -1, areaPosition = -1, districtPosition = 0, tehsilPosition = 0, subAreaPosition = -1, zonePosition = -1;
    private ArrayAdapter<GetCitiesResponse.City> cityAdapter;
    private ArrayAdapter<GetCitiesResponse.City> newCityAdapter;
    EditText edtShopname, etdOwerName, edtcellno, edtemail, edtaddress, edtcontactperson, edtcontactcellno, edtstudentstr, edtNoOfBraches;
    Button btnsbm;
    private List<IdName> mArrDistricts;
    private List<IdName> mArrTehsils;
    private ImageButton ibshowSellers;
    private ScrollView scrlEdit;
    private ArrayAdapter<String> adapter;
    private int districtID = 0, tehsilID = 0;
    private List<BookSellerID> BookSellerIDs;
    private List<BookPublisherID> PublisherIDs;
    private String vendorType = "School";
    private boolean isZoneFormat = false;
    private GetZonesResponse mZoneResponse;
    private int zoneID = -1;
    private GetSubAreasResponse mSubAreasResponse;
    private int subAreaID = -1;
    private List<GetSubAreasResponse.SubArea> mSubAreas;
    private List<GetSubAreasResponse.SubArea> newSubAreas;
    private ArrayAdapter<GetSubAreasResponse.SubArea> subAreaAdapter;
    private ArrayAdapter<GetSubAreasResponse.SubArea> newSubAreaAdapter;
    private TextView tvCity, tvArea;
    private SchoolEditInformationActivityBinding binding;
    private String selectedExamBoard = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Paper.init(EditSchoolInformation.this);
        binding = SchoolEditInformationActivityBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);

        setContentView(binding.getRoot());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Vendor Information");
        mArrCustomers = new ArrayList<>();
        mArrAreas = new ArrayList<>();
        mArrNewAreas = new ArrayList<>();
        arrCitiesRelatedToRegion = new ArrayList<>();
        sampleSearchModels = new ArrayList<>();
        newSampleSearchModels = new ArrayList<>();
        editSchoolInformations = new ArrayList<>();
        schoolSampleSearchModels = new ArrayList<>();
        mArrBookSellers = new ArrayList<>();
        mArrSelectedBookSellers = new ArrayList<>();
        mArrSyllabusItems = new ArrayList<>();
        mArrSyllabusItemsSelected = new ArrayList<>();
        mArrDistricts = new ArrayList<>();
        mArrTehsils = new ArrayList<>();
        init();
        Log.d("class_name", this.getClass().getSimpleName());
    }

    private void init() {
        SnpName = findViewById(R.id.spnShopName);
        mSpnRegion = findViewById(R.id.spn_edit_region);
        mSpnCity = findViewById(R.id.spn_edit_city);
        mSpnArea = findViewById(R.id.spn_edit_area);

        mSpnNewArea = findViewById(R.id.spn_new_area);
        mSpnNewDistrict = findViewById(R.id.spn_new_District);
        mSpnNewTehsil = findViewById(R.id.spn_new_Tehsil);
        mSpnEduSys = findViewById(R.id.spn_edit_edu_sys);
        mSpnFeeStruct = findViewById(R.id.spn_edit_fee_struct);
        mSpnSyllSelectionDate = findViewById(R.id.spn_edit_syll_sel_month);
        mSpnSessionDate = findViewById(R.id.spn_edit_sess_start_month);
        rlShowPublishers = findViewById(R.id.rl_select_Publishers);
        mrvItems = findViewById(R.id.rv_publishers);
        ibShowPublishers = findViewById(R.id.ib_show_publisher);
        edtShopname = findViewById(R.id.etShopName);
        etdOwerName = findViewById(R.id.etOwerName);
        edtcellno = findViewById(R.id.etCellNo1);

        edtemail = findViewById(R.id.etEmail);
        tvCity = findViewById(R.id.tvt_city);
        tvArea = findViewById(R.id.tvt_area);
        mDialog = new ProgressDialog(EditSchoolInformation.this);
        mDialog.setCancelable(false);
        edtaddress = findViewById(R.id.etAddress);
        edtcontactperson = findViewById(R.id.etContactPerson);
        edtcontactcellno = findViewById(R.id.etContactPersonCellNo);
        edtstudentstr = findViewById(R.id.etStudentStrength);
        edtNoOfBraches = findViewById(R.id.etNoOfBranches);
        mRvSellers = findViewById(R.id.rv_sellers);

        mLoginResponse = Paper.book().read(AppKeys.KEY_LOGIN_RESPONSE);
        btnsbm = findViewById(R.id.btnSubmit);
        ibshowSellers = findViewById(R.id.ib_show_seller);
        rlShowSellers = findViewById(R.id.rl_select_Sellers);
        llMainLayout = findViewById(R.id.llMain);
        scrlEdit = findViewById(R.id.scrlEdit);
        btnsbm.setOnClickListener(this);
        rlShowPublishers.setOnClickListener(this);
        ibShowPublishers.setOnClickListener(this);
        ibshowSellers.setOnClickListener(this);
        rlShowSellers.setOnClickListener(this);
        soId = mLoginResponse.getData().getSOID();
        Token = mLoginResponse.getData().getToken();
        mArrSyllabusItems = mLoginResponse.getData().getCurrentSyllabus();
        viewZone = findViewById(R.id.view_zone);
        viewSubArea = findViewById(R.id.view_sub_area);
        spZone = findViewById(R.id.sp_zone);
        spSubArea = findViewById(R.id.sp_sub_area);

        isZoneFormat = mLoginResponse.getData().isZoneFormat();
        setCountrySpinner();
        setRegionSpinner();
        // setNewRegionSpinner();
        //callGetDistrictService();
    }

    private void setCountrySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.country_list, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spnCountry.setAdapter(adapter);
        binding.spnCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                parent.getItemAtPosition(position).toString();
                // Save the selected country to a variable or perform any desired action
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case where no country is selected, if needed
            }
        });
    }

    private void setNewCountrySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.country_list, android.R.layout.simple_spinner_dropdown_item);
        binding.spnNewCountry.setAdapter(adapter);
//        binding.spnCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                parent.getItemAtPosition(position).toString();
//                // Save the selected country to a variable or perform any desired action
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                // Handle the case where no country is selected, if needed
//            }
//        });
    }

    private void setRegionSpinner() {
        mArrRegion = mLoginResponse.getData().getRegion();
        ArrayAdapter<Region> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, mArrRegion);
        mSpnRegion.setAdapter(adapter);
        mSpnRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                viewZone.setVisibility(View.GONE);
                viewSubArea.setVisibility(View.GONE);
                mSpnArea.setVisibility(View.GONE);
                tvArea.setVisibility(View.GONE);
                tvCity.setVisibility(View.GONE);
                mSpnCity.setVisibility(View.GONE);

                if (Utility.isNetworkAvailable(getApplicationContext())) {
                    regionPosition = position;
                    callCityService();
                } else
                    Toast.makeText(getApplicationContext(), getString(R.string.str_no_internet), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        RadioButton rbShop = findViewById(R.id.rb_shop);
        rbShop.setOnCheckedChangeListener(new RadioButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    vendorType = "Shop";
                    hideViews();
                    if (isZoneFormat&&subAreaID != 0 && subAreaID != -1) {
                        GetCustomerRelatedToSubArea(subAreaID);
                        callGetBookSellerService(String.valueOf(arrCitiesRelatedToRegion.get(cityPosition).getID()), String.valueOf(mArrAreas.get(areaPosition).getID()));
                        rlShowSellers.setVisibility(View.GONE);
                        mRvSellers.setVisibility(View.GONE);
                    }else if(!isZoneFormat&&mArrAreas.get(areaPosition).getID()!=0&&mArrAreas.get(areaPosition).getID()!=-1){
                        GetCustomerRelatedToArea(mArrAreas.get(areaPosition).getID(),arrCitiesRelatedToRegion.get(cityPosition).getID());
                        callGetBookSellerService(String.valueOf(arrCitiesRelatedToRegion.get(cityPosition).getID()), String.valueOf(mArrAreas.get(areaPosition).getID()));
                        rlShowSellers.setVisibility(View.GONE);
                        mRvSellers.setVisibility(View.GONE);
                    }
                }
            }
        });
        RadioButton rbSchool = findViewById(R.id.rb_school);
        rbSchool.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    vendorType = "School";
                    showViews();
                    if (isZoneFormat&&subAreaID != 0 && subAreaID != -1) {
                        GetCustomerRelatedToSubArea(subAreaID);
                        callGetBookSellerService(String.valueOf(arrCitiesRelatedToRegion.get(cityPosition).getID()), String.valueOf(mArrAreas.get(areaPosition).getID()));
                        rlShowSellers.setVisibility(View.GONE);
                        mRvSellers.setVisibility(View.GONE);
                    }else if(!isZoneFormat&&mArrAreas.get(areaPosition).getID()!=0&&mArrAreas.get(areaPosition).getID()!=-1){
                        GetCustomerRelatedToArea(mArrAreas.get(areaPosition).getID(),arrCitiesRelatedToRegion.get(cityPosition).getID());
                        callGetBookSellerService(String.valueOf(arrCitiesRelatedToRegion.get(cityPosition).getID()), String.valueOf(mArrAreas.get(areaPosition).getID()));
                        rlShowSellers.setVisibility(View.GONE);
                        mRvSellers.setVisibility(View.GONE);
                    }
                }
            }
        });


    }
    private void setNewRegionSpinner() {
        newArrRegion = mLoginResponse.getData().getRegion();
        ArrayAdapter<Region> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, newArrRegion);
        binding.spnNewRegion.setAdapter(adapter);

        binding.spnNewRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                binding.viewNewZone.setVisibility(View.GONE);
                binding.viewNewSubArea.setVisibility(View.GONE);
                binding.spnNewArea.setVisibility(View.GONE);
                binding.tvtNewArea.setVisibility(View.GONE);
                binding.tvtNewCity.setVisibility(View.GONE);
                binding.spnNewCity.setVisibility(View.GONE);

                if (Utility.isNetworkAvailable(getApplicationContext())) {
                    newRegionId = newArrRegion.get(position).getID();
                    callNewCityService();

                }else
                    Toast.makeText(getApplicationContext(), getString(R.string.str_no_internet), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void showViews() {
        edtNoOfBraches.setVisibility(View.VISIBLE);
        binding.rlWorkingPriority.setVisibility(View.VISIBLE);
        binding.rlExamBoard.setVisibility(View.VISIBLE);
        edtstudentstr.setVisibility(View.VISIBLE);
        mSpnSyllSelectionDate.setVisibility(View.VISIBLE);
        mSpnEduSys.setVisibility(View.VISIBLE);
        mSpnFeeStruct.setVisibility(View.VISIBLE);
        mSpnSessionDate.setVisibility(View.VISIBLE);
    }

    private void hideViews() {
        edtNoOfBraches.setVisibility(View.GONE);
        binding.rlWorkingPriority.setVisibility(View.GONE);
        binding.rlExamBoard.setVisibility(View.GONE);
        edtstudentstr.setVisibility(View.GONE);
        mSpnSyllSelectionDate.setVisibility(View.GONE);
        mSpnEduSys.setVisibility(View.GONE);
        mSpnFeeStruct.setVisibility(View.GONE);
        mSpnSessionDate.setVisibility(View.GONE);
    }

    private void setCitySpinner() {
        cityAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, arrCitiesRelatedToRegion);
        mSpnCity.setAdapter(cityAdapter);
        sampleSearchModels.clear();
        for (int i = 0; i < arrCitiesRelatedToRegion.size(); i++) {
            GetCitiesResponse.City city = arrCitiesRelatedToRegion.get(i);
            String name = city.getName();
            int id = city.getID();
            sampleSearchModels.add(new SampleSearchModel(name, id));

        }

        mSpnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) view).setTextColor(getResources().getColorStateList(R.color.black));
                cityPosition = 0;
                cityId = String.valueOf(arrCitiesRelatedToRegion.get(cityPosition).getID());
                if (isZoneFormat) {
                    loadZones();
                } else {
                    callGetAreaService(String.valueOf(arrCitiesRelatedToRegion.get(cityPosition).getID()));
                }
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
                    new SimpleSearchDialogCompat(EditSchoolInformation.this, "Search...",
                            "What are you looking for...?", null, sampleSearchModels,
                            new SearchResultListener<SampleSearchModel>() {
                                @Override
                                public void onSelected(BaseSearchDialogCompat dialog,
                                                       SampleSearchModel item, int position) {
                                    arrCitiesRelatedToRegion.add(new GetCitiesResponse().new City(item.getID(), item.getTitle()));
                                    cityAdapter = new ArrayAdapter<GetCitiesResponse.City>(EditSchoolInformation.this, android.R.layout.simple_spinner_dropdown_item, arrCitiesRelatedToRegion);
                                    mSpnCity.setAdapter(cityAdapter);
                                    mSpnCity.setVisibility(View.VISIBLE);
                                    mSpnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            cityPosition = position;
                                            if (isZoneFormat) {
                                                loadZones();
                                            } else {
                                                callGetAreaService(String.valueOf(arrCitiesRelatedToRegion.get(cityPosition).getID()));
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

    private void loadZones() {
        mDialog.show();
        mDialog.setMessage("Fetching Zones please wait...");
        mService = RestClient.getInstance(this);
        Call<GetZonesResponse> userObject = mService.getZones(String.valueOf(arrCitiesRelatedToRegion.get(cityPosition).getID()), mLoginResponse.getData().getSOID());
        RestCallbackObject callbackObject =
                new RestCallbackObject(this, this, RequestCode.GET_ZONES_REQUEST_CODE).showProgress(
                        true,
                        0
                ).dontHideProgress(false);
        userObject.enqueue(callbackObject);
    }

    private void loadNewZones() {
        mDialog.show();
        mDialog.setMessage("Fetching New Zones please wait...");
        mService = RestClient.getInstance(this);
        Call<GetZonesResponse> userObject = mService.getZones(String.valueOf(arrCitiesRelatedToRegion.get(cityPosition).getID()), mLoginResponse.getData().getSOID());
        RestCallbackObject callbackObject =
                new RestCallbackObject(this, this, RequestCode.GET_NEW_ZONES_REQUEST_CODE).showProgress(
                        true,
                        0
                ).dontHideProgress(false);
        userObject.enqueue(callbackObject);
    }

    private void setNewCitySpinner() {
        newCityAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, arrNewCities);
        binding.spnNewCity.setAdapter(newCityAdapter);
        newSampleSearchModels.clear();
        for (int i = 0; i < arrNewCities.size(); i++) {
            GetCitiesResponse.City city = arrNewCities.get(i);
            String name = city.getName();
            int id = city.getID();
            newSampleSearchModels.add(new SampleSearchModel(name, id));
        }
        newCityPosition=0;
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    newCityId = arrNewCities.get(0).getID();
                    if (isZoneFormat) {
                        loadNewZones();
                    } else {
                        callGetNewAreaService(String.valueOf(arrNewCities.get(newCityPosition).getID()));
                    }
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), "City Not Available.", Toast.LENGTH_LONG).show();
                }
            }
        }, 600);

        binding.spnNewCity.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    arrNewCities.clear();
                    new SimpleSearchDialogCompat(EditSchoolInformation.this, "Search...",
                            "What are you looking for...?", null, newSampleSearchModels,
                            new SearchResultListener<SampleSearchModel>() {
                                @Override
                                public void onSelected(BaseSearchDialogCompat dialog,
                                                       SampleSearchModel item, int position) {
                                    arrNewCities.add(new GetCitiesResponse().new City(item.getID(), item.getTitle()));
                                    newCityAdapter = new ArrayAdapter<GetCitiesResponse.City>(EditSchoolInformation.this, android.R.layout.simple_spinner_dropdown_item, arrNewCities);
                                    binding.spnNewCity.setAdapter(newCityAdapter);
                                    binding.spnNewCity.setVisibility(View.VISIBLE);
                                    binding.spnNewCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            newCityPosition = position;
                                            newCityId = arrNewCities.get(position).getID();
                                            if (isZoneFormat) {
                                                loadNewZones();
                                            } else {
                                                callGetNewAreaService(String.valueOf(arrNewCities.get(newCityPosition).getID()));
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
        ArrayAdapter<GetAreasResponse.Area> adapter = new ArrayAdapter<>(EditSchoolInformation.this, android.R.layout.simple_spinner_dropdown_item, mArrAreas);
        mSpnArea.setAdapter(adapter);
        mSpnArea.setVisibility(View.VISIBLE);
        mSpnArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //if (position ! 0) {
                areaPosition = position;
                if (isZoneFormat) {
                    loadSubAreas();
                } else {
                    GetCustomerRelatedToArea(mArrAreas.get(areaPosition).getID(), arrCitiesRelatedToRegion.get(cityPosition).getID());
                    callGetBookSellerService(String.valueOf(arrCitiesRelatedToRegion.get(cityPosition).getID()), String.valueOf(mArrAreas.get(areaPosition).getID()));

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadSubAreas() {
        mDialog.setMessage("Fetching Sub-Areas please wait...");
        mDialog.show();
        mService = RestClient.getInstance(this);
        Call<GetSubAreasResponse> userObject = mService.geSubAreas(mArrAreas.get(areaPosition).getID());
        RestCallbackObject callbackObject = new RestCallbackObject(this, this, RequestCode.GET_SUB_AREAS_REQUEST_CODE).showProgress(true, 0).dontHideProgress(false);
        userObject.enqueue(callbackObject);
    }

    private void loadNewSubAreas() {
        mDialog.setMessage("Fetching New Sub-Areas please wait...");
        mDialog.show();
        mService = RestClient.getInstance(this);
        Call<GetSubAreasResponse> userObject = mService.geSubAreas(mArrAreas.get(areaPosition).getID());
        RestCallbackObject callbackObject = new RestCallbackObject(this, this, RequestCode.GET_NEW_SUB_AREAS_REQUEST_CODE).showProgress(true, 0).dontHideProgress(false);
        userObject.enqueue(callbackObject);
    }

    private void setNewAreaSpinner() {
        mArrNewAreas = mNewAreaResponse.getAreas();
        /*GetAreasResponse.Area emptyCity = new GetAreasResponse().new Area(0, "Select Area");
        mArrNewAreas.add(0, emptyCity);*/
        ArrayAdapter<GetAreasResponse.Area> adapter = new ArrayAdapter<>(EditSchoolInformation.this, android.R.layout.simple_spinner_dropdown_item, mArrNewAreas);
        mSpnNewArea.setAdapter(adapter);
        mSpnNewArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // if (position != 0) {
                newAreaPosition = position;
                newAreaId = mArrNewAreas.get(position).getID();
                if (isZoneFormat) {
                    loadNewSubAreas();
                }
                //}
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setNewDistrictSpinner() {
        IdName emptyDis = new IdName(0, "Select District");
        mArrDistricts.add(0, emptyDis);
        ArrayAdapter<IdName> adapter = new ArrayAdapter<>(EditSchoolInformation.this, android.R.layout.simple_spinner_dropdown_item, mArrDistricts);
        mSpnNewDistrict.setAdapter(adapter);
        mSpnNewDistrict.setVisibility(View.VISIBLE);
        mSpnNewDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    districtPosition = position;
                    districtID = mArrDistricts.get(position).getID();
                    callGetTehsilService(mArrDistricts.get(position).getID());

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setNewTehsilSpinner() {
        mSpnNewTehsil.setVisibility(View.VISIBLE);
        IdName emptyTehsil = new IdName(0, "Select Tehsil");
        mArrTehsils.add(0, emptyTehsil);
        ArrayAdapter<IdName> adapter = new ArrayAdapter<>(EditSchoolInformation.this, android.R.layout.simple_spinner_dropdown_item, mArrTehsils);
        mSpnNewTehsil.setAdapter(adapter);
        mSpnNewTehsil.setVisibility(View.VISIBLE);
        mSpnNewTehsil.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    tehsilPosition = position;
                    tehsilID = mArrTehsils.get(position).getID();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setCustomerSpinner() {
        SnpName.setVisibility(View.VISIBLE);
        customerAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, mArrCustomers);
        SnpName.setAdapter(customerAdapter);
        schoolSampleSearchModels.clear();
        for (int i = 0; i < mArrCustomers.size(); i++) {
            CustomersRelatedtoSO customer = mArrCustomers.get(i);
            String name = customer.getShopName();
            int id = customer.getID();
            schoolSampleSearchModels.add(new SampleSearchModel(name, id));
        }


        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    callService_(mArrCustomers.get(customerPosition).getID());
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), "Vendor not Available.", Toast.LENGTH_LONG).show();
                }

            }
        }, 100);

        SnpName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mArrCustomers.clear();
                    new SimpleSearchDialogCompat(EditSchoolInformation.this, "Search...",
                            "What are you looking for...?", null, schoolSampleSearchModels,
                            new SearchResultListener<SampleSearchModel>() {
                                @Override
                                public void onSelected(BaseSearchDialogCompat dialog,
                                                       SampleSearchModel item, int position) {
                                    mArrCustomers.add(new CustomersRelatedtoSO(item.getID(), false, item.getTitle()));
                                    customerAdapter = new ArrayAdapter<CustomersRelatedtoSO>(EditSchoolInformation.this, android.R.layout.simple_spinner_dropdown_item, mArrCustomers);
                                    SnpName.setAdapter(customerAdapter);
                                    SnpName.setVisibility(View.VISIBLE);
                                    SnpName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            customerPosition = position;
                                            callService_(mArrCustomers.get(customerPosition).getID());
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


    private void callCityService() {
       try {
            mDialog.show();
            mDialog.setMessage("Loading Cities,please wait...");
            mService = RestClient.getInstance(EditSchoolInformation.this);
            Call<GetCitiesResponse> userObject = mService.getCities(mArrRegion.get(regionPosition).getID(), mLoginResponse.getData().getSOID());
            RestCallbackObject callbackObject = new RestCallbackObject(EditSchoolInformation.this, this, RequestCode.GET_CITIES_REQUEST_CODE).showProgress(true, 0).dontHideProgress(false);
            userObject.enqueue(callbackObject);
        }catch (Exception e){

       }
    }

    private void callNewCityService() {
        mDialog.show();
        mDialog.setMessage("Loading New Cities,please wait...");
        mService = RestClient.getInstance(EditSchoolInformation.this);
        Call<GetCitiesResponse> userObject = mService.getCities(mArrRegion.get(regionPosition).getID(), mLoginResponse.getData().getSOID());
        RestCallbackObject callbackObject = new RestCallbackObject(EditSchoolInformation.this, this, RequestCode.GET_NEW_CITIES_REQUEST_CODE).showProgress(true, 0).dontHideProgress(false);
        userObject.enqueue(callbackObject);
    }

    private void callGetAreaService(String cityId) {
        mDialog.show();
        mDialog.setMessage("Fetching areas please wait...");
        mService = RestClient.getInstance(getApplicationContext());
        Call<GetAreasResponse> userObject = mService.getAreas(cityId, mLoginResponse.getData().getSOID());
        RestCallbackObject callbackObject = new RestCallbackObject(EditSchoolInformation.this, this, RequestCode.GET_AREA_REQUEST_CODE).showProgress(true, 0).dontHideProgress(false);
        userObject.enqueue(callbackObject);
    }

    private void callGetNewAreaService(String cityId) {
        mDialog.show();
        mDialog.setMessage("Fetching areas please wait...");
        mService = RestClient.getInstance(getApplicationContext());
        Call<GetAreasResponse> userObject = mService.getAreas(cityId, mLoginResponse.getData().getSOID());
        RestCallbackObject callbackObject = new RestCallbackObject(EditSchoolInformation.this, this, RequestCode.GET_AREAS_FOR_CITY).showProgress(true, 0).dontHideProgress(false);
        userObject.enqueue(callbackObject);
    }

    private void callGetBookSellerService(String cityId, String areaId) {
        this.cityId = cityId;
        mDialog.show();
        mDialog.setMessage("Fetching Selllers please wait...");
        mService = RestClient.getInstance(getApplicationContext());
        Call<GetBookSellerResponse> userObject = mService.getSellers(cityId, areaId);
        RestCallbackObject callbackObject = new RestCallbackObject(EditSchoolInformation.this, this, RequestCode.GET_AREA_SELLERS).showProgress(true, 0).dontHideProgress(false);
        userObject.enqueue(callbackObject);

    }

    private void callGetDistrictService() {
        mDialog.setMessage("Fetching Districts please wait...");
        mDialog.show();
        mService = RestClient.getInstance(this);
        Call<GetServerResponse> userObject = mService.getDistricts(mLoginResponse.getData().getProvinceID());
        RestCallbackObject callbackObject = new RestCallbackObject(this, this, RequestCode.GET_DISTRCIST).showProgress(true, 0).dontHideProgress(false);
        userObject.enqueue(callbackObject);
    }

    private void callGetTehsilService(int DitrictID) {
        mDialog.setMessage("Fetching Tehsils please wait...");
        mDialog.show();
        mService = RestClient.getInstance(this);
        mDialog.show();
        Call<GetServerResponse> userObject = mService.getTehsils(DitrictID);
        RestCallbackObject callbackObject = new RestCallbackObject(this, this, RequestCode.GET_TEHSILS).showProgress(true, 0).dontHideProgress(false);
        userObject.enqueue(callbackObject);
    }

    private void GetCustomerRelatedToArea(int areaID, int cityID) {
        mDialog.show();
        mDialog.setMessage("Fetching customers please wait...");
        mService = RestClient.getInstance(this);
        Call<GetServerResponse> userObject = mService.getCustomerRelatedToSo(cityID, areaID, vendorType);
        RestCallbackObject callbackObject = new RestCallbackObject(this, this, RequestCode.GET_CUSTOMER_RELATED_TO_SO).showProgress(true, 0).dontHideProgress(false);
        userObject.enqueue(callbackObject);
    }

    private void GetCustomerRelatedToSubArea(int subAreaID) {
        mDialog.show();
        mDialog.setMessage("Fetching customers please wait...");
        mService = RestClient.getInstance(this);
        Call<GetServerResponse> userObject = mService.getCustomerSubAreaWise(subAreaID, vendorType);
        RestCallbackObject callbackObject = new RestCallbackObject(this, this, RequestCode.GET_CUSTOMER_RELATED_TO_SO).showProgress(true, 0).dontHideProgress(false);
        userObject.enqueue(callbackObject);
    }

    private void callgetDeeletSchool(int retailerID) {
        mDialog.show();
        mDialog.setMessage("Deleting School,please wait...");
        Call<ServerResponse> userObject = mService.deleteRetailer(retailerID);
        RestCallbackObject callbackObject = new RestCallbackObject(this, this, RequestCode.DELETE_SCHOOLS).showProgress(true, 0).dontHideProgress(false);
        userObject.enqueue(callbackObject);
    }

    public void getEditSchoolInfo(FormData req) {

        binding.etStudentStrength.setText(String.valueOf(req.getStudentStrength()));
        binding.etWebsite.setText(req.getWebsite());

        mSpnEduSys.setAdapter(null);
        mSpnSessionDate.setAdapter(null);
        mSpnFeeStruct.setAdapter(null);
        mSpnSyllSelectionDate.setAdapter(null);
        edtShopname.setText(req.getShopName());
        etdOwerName.setText(req.getName());
        edtcellno.setText(req.getPhone1());

        edtemail.setText(req.getEmail());
        edtaddress.setText(req.getAddress());
        edtcontactperson.setText(req.getContactPerson());
        edtcontactcellno.setText(req.getContactPersonCellNo());
        edtstudentstr.setText(String.valueOf(req.getStudentStrength()));
        edtNoOfBraches.setText(String.valueOf(req.getNoOfBranches()));

        setFeeStructureSpinner(req.getFeeStructID());
        setEducationSystemSpinner(req.getEduSystem());
        setSessionStartMonth(req.getSessionStartMonth());
        setSyllabusSelectionMonth(req.getSyllSectionMonth());
        mRvSellers.setVisibility(View.GONE);
        mRvSellers.setAdapter(null);
        mrvItems.setVisibility(View.GONE);
        mrvItems.setAdapter(null);
        ibshowSellers.setBackgroundResource(R.drawable.ic_expand_more_black_24dp);
        ibShowPublishers.setBackgroundResource(R.drawable.ic_expand_more_black_24dp);

        /*mSpnNewCity.setSelection(cityPosition);

         mSpnNewArea.setSelection(areaPosition);*/

        /*for(int j=0;j<=mArrNewAreas.size();j++) {
            if(mArrNewAreas.get(j).getID()==req.getAreaID())
            {            mSpnNewArea.setSelection(j);
                break;
            }

        }*/
        //add the relavent city at top
       /* GetCitiesResponse.City city=new GetCitiesResponse().new City(req.getCityID(),req.getCityName());
        arrNewCities.add(0,city);
        newCityAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, arrNewCities);
        mSpnNewCity.setAdapter(newCityAdapter);
        //ADD THE RELAVENT AREA AT TOP
        mArrNewAreas = mAreaResponse.getAreas();
        GetAreasResponse.Area emptyCity = new GetAreasResponse().new Area(req.getAreaID(), req.getAreaName());
        mArrNewAreas.add(0, emptyCity);
        ArrayAdapter<GetAreasResponse.Area> adapter = new ArrayAdapter<>(EditSchoolInformation.this, android.R.layout.simple_spinner_dropdown_item, mArrNewAreas);
        mSpnNewArea.setAdapter(adapter);*/
        setWorkingPrioritySpinner();
        binding.spnWorkingPriority.setSelection(getPriority(req.getStudentStrength()));
        binding.etStudentStrength.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int studentStrength = Integer.parseInt(binding.etStudentStrength.getText().toString());
                    binding.spnWorkingPriority.setSelection(getPriority(studentStrength));

                } catch (NumberFormatException e) {
                    //mEtStudentStrength.setText("");
                    binding.etStudentStrength.setError("Please enter valid student strength");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        setExamBoardSpinner();
        String[] arrays = getResources().getStringArray(R.array.exam_boards);
        int index = -1;
        for (String str : arrays) {
            if (str.equals(req.getExamBoard())) {
                binding.spExamBoard.setSelection(++index);
                break;
            } else {
                ++index;
            }
        }
        setNewCountrySpinner();
        setNewRegionSpinner();

        //binding.etWebsite.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        //binding.etStudentStrength.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        binding.etAddress.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        //binding.etEmail.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        binding.etContactPerson.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        //binding.etCellNo1.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        //binding.etContactPersonCellNo.setFilters(new InputFilter[]{getPhoneFilter(), new InputFilter.LengthFilter(12)});
        binding.etOwerName.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        binding.etShopName.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        btnsbm.setEnabled(true);

        binding.viewNewZone.setVisibility(View.GONE);
        binding.viewNewSubArea.setVisibility(View.GONE);
        binding.tvtNewArea.setVisibility(View.GONE);
        binding.tvtNewCity.setVisibility(View.GONE);
        binding.spnNewCity.setVisibility(View.GONE);
        binding.spnNewArea.setVisibility(View.GONE);
    }

    private int getPriority(int studentStrength) {
        if (studentStrength >= 1 && studentStrength <= 1999) {
            return 0;
        } else if (studentStrength >= 2000 && studentStrength <= 3000) {
            return 1;
        } else if (studentStrength > 3000) {
            return 2;
        } else {
            return 0;
        }
    }

    private void setEducationSystemSpinner(String Current) {
        mArrEduSys = new ArrayList<>();
        mArrEduSys.add("Select Education System");
        mArrEduSys.add("Primary");
        mArrEduSys.add("Middle");
        mArrEduSys.add("Matric");
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, mArrEduSys);
        mSpnEduSys.setAdapter(adapter);
        if (Current != null) {
            int spinnerPosition = adapter.getPosition(Current);
            mSpnEduSys.setSelection(spinnerPosition);
        }
    }

    private void setFeeStructureSpinner(int Current) {
        mArrFeeStructure = mLoginResponse.getData().getFee();
        mArrFeeStructure.add(0, new Fee(0, "Select Fee Structure*"));
        // To remove duplicate "Select Fee Structure"
        if (mArrFeeStructure.get(0).getFeeStructID() == mArrFeeStructure.get(1).getFeeStructID()) {
            mArrFeeStructure.remove(0);
        }
        ArrayAdapter<Fee> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, mArrFeeStructure);
        mSpnFeeStruct.setAdapter(adapter);
        if (Current != 0) {
            mSpnFeeStruct.setSelection(getIndex(Current));
        }
    }

    private int getIndex(int fee) {
        for (int i = 0; i < mArrFeeStructure.size(); i++) {
            if (mArrFeeStructure.get(i).getFeeStructID() == fee) {
                return i;
            }
        }
        return 0;
    }

    private void setSyllabusSelectionMonth(String Current) {
        mArrSyllSelectionDate = new ArrayList<>();
        mArrSyllSelectionDate.add("Select Syllabus Selection Month*");
        mArrSyllSelectionDate.add("January");
        mArrSyllSelectionDate.add("February");
        mArrSyllSelectionDate.add("march");
        mArrSyllSelectionDate.add("april");
        mArrSyllSelectionDate.add("May");
        mArrSyllSelectionDate.add("June");
        mArrSyllSelectionDate.add("July");
        mArrSyllSelectionDate.add("August");
        mArrSyllSelectionDate.add("September");
        mArrSyllSelectionDate.add("October");
        mArrSyllSelectionDate.add("November");
        mArrSyllSelectionDate.add("December");
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, mArrSyllSelectionDate);
        mSpnSyllSelectionDate.setAdapter(adapter);
        if (Current != null) {
            mSpnSyllSelectionDate.setSelection(mArrSyllSelectionDate.indexOf(Current));
        }
    }

    private void setSessionStartMonth(String Current) {
        mArrSessionDate = new ArrayList<>();
        mArrSessionDate.add("Select Session Start Month*");
        mArrSessionDate.add("January");
        mArrSessionDate.add("February");
        mArrSessionDate.add("march");
        mArrSessionDate.add("april");
        mArrSessionDate.add("May");
        mArrSessionDate.add("June");
        mArrSessionDate.add("July");
        mArrSessionDate.add("August");
        mArrSessionDate.add("September");
        mArrSessionDate.add("October");
        mArrSessionDate.add("November");
        mArrSessionDate.add("December");
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, mArrSessionDate);
        mSpnSessionDate.setAdapter(adapter);
        if (Current != null) {
            mSpnSessionDate.setSelection(mArrSessionDate.indexOf(Current));
        }
    }

    private void setShopReq(SchoolRegRequest obj) {

        obj.setID(mArrCustomers.get(SnpName.getSelectedItemPosition()).getID());
        obj.setShopOrSchoolName(edtShopname.getText().toString());
        obj.setPrincipleName(etdOwerName.getText().toString());
        obj.setPhone1(edtcellno.getText().toString());
        obj.setCountryName(binding.spnNewCountry.getSelectedItem().toString());
        if (!selectedExamBoard.isEmpty()) {
            obj.setExamBoard(selectedExamBoard);
        }
        obj.setSalesOfficerID(mLoginResponse.getData().getSOID());
        obj.setEmail(edtemail.getText().toString());
        obj.setAddress(edtaddress.getText().toString());
        obj.setContactPerson(edtcontactperson.getText().toString());
        obj.setAreaID(mArrNewAreas.get(mSpnNewArea.getSelectedItemPosition()).getID());
        obj.setCityID(arrNewCities.get(binding.spnNewCity.getSelectedItemPosition()).getID());
        obj.setSubAreaID(newSubAreaId);
        obj.setZoneID(newZoneId);
        obj.setRegionId(newRegionId);
        obj.setContactPersonCellNo(edtcontactcellno.getText().toString());
        obj.setShopsRelatedToRetailer(mArrSelectedBookSellers);
        obj.setTehsilID(tehsilID);
        obj.setDistrictID(districtID);
        obj.setEducationSystem(mArrEduSys.get(mSpnEduSys.getSelectedItemPosition()));
        obj.setFeeStructure(mArrFeeStructure.get(mSpnFeeStruct.getSelectedItemPosition()).getFeeStructID());
        obj.setSessionStart(mArrSessionDate.get(mSpnSessionDate.getSelectedItemPosition()));
        obj.setSampleMonth(mArrSyllSelectionDate.get(mSpnSyllSelectionDate.getSelectedItemPosition()));
        obj.setCompititorInformation(mArrSyllabusItemsSelected);
        Log.e("Shops Size", "" + mArrSelectedBookSellers.size());
        if (edtNoOfBraches.getText().toString().isEmpty())
            obj.setNoOfBranches(0);
        else obj.setNoOfBranches(Integer.parseInt(edtNoOfBraches.getText().toString()));
        /*if (edtNoOfTeacher.getText().toString().isEmpty())
            obj.setNoOfTeachers(0);
        else obj.setNoOfTeachers(Integer.parseInt(edtNoOfTeacher.getText().toString()));*/
        if (edtstudentstr.getText().toString().length() > 0 || edtstudentstr.getText().toString() != null) {
            studentStrength = Integer.valueOf(edtstudentstr.getText().toString());
        }
        obj.setStudentStrength(studentStrength);
        obj.setToken(Token);
//        obj.setVerified();
    }

    private void callService_(int id) {
        btnsbm.setEnabled(false);
        mDialog.show();
        mDialog.setMessage("Loading Customer's Data please wait...");
        mService = RestClient.getInstance(this);
        Call<GetServerResponse> userObject = mService.GetSchoolinfoForEdit(String.valueOf(id));
        RestCallbackObject callbackObject = new RestCallbackObject(this, this, RequestCode.API_GET_SCHOOL_INFO_FOR_EDIT_REQUEST_CODE).showProgress(true, 0).dontHideProgress(false);
        userObject.enqueue(callbackObject);
    }

    private void callRegService() {
        btnsbm.setEnabled(false);
        mDialog.show();
        mDialog.setMessage("Submitting Details please wait...");
        mService = RestClient.getInstance(this);
        Call<ServerResponse> userObject = mService.GetSchoolSchoolEditInfo(schoolRegRequest);
        RestCallbackObject callbackObject = new RestCallbackObject(this, this, RequestCode.EDIT_SCHOOL_REQUEST_INFO).showProgress(true, 0).dontHideProgress(false);
        userObject.enqueue(callbackObject);
    }

    @Override
    public void onSuccess(Object object, RequestCode requestCode) {
        if (requestCode == RequestCode.GET_CITIES_REQUEST_CODE) {
            if (mDialog.isShowing()) {
                mDialog.hide();
            }
            mCitiesResponse = (GetCitiesResponse) object;
            if (mCitiesResponse.getCities() != null && mCitiesResponse.getCities().size() > 0) {
                arrCitiesRelatedToRegion = mCitiesResponse.getCities();
                setCitySpinner();
                mSpnCity.setVisibility(View.VISIBLE);
                tvCity.setVisibility(View.VISIBLE);
            } else {
                mSpnCity.setVisibility(View.GONE);
                tvCity.setVisibility(View.GONE);
                mSpnArea.setVisibility(View.GONE);
                tvArea.setVisibility(View.GONE);
                SnpName.setVisibility(View.GONE);
                llMainLayout.setVisibility(View.GONE);
                viewZone.setVisibility(View.GONE);
                viewSubArea.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "There are no cities associated", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == RequestCode.GET_NEW_CITIES_REQUEST_CODE) {
            if (mDialog.isShowing()) {
                mDialog.hide();
            }
            mNewCitiesResponse = (GetCitiesResponse) object;
            if (mNewCitiesResponse.getCities() != null && mNewCitiesResponse.getCities().size() > 0) {
                arrNewCities = mCitiesResponse.getCities();
                setNewCitySpinner();
                binding.spnNewCity.setVisibility(View.VISIBLE);
                binding.tvtNewCity.setVisibility(View.VISIBLE);
            } else {
                mSpnCity.setVisibility(View.GONE);
                tvCity.setVisibility(View.GONE);
                mSpnArea.setVisibility(View.GONE);
                tvArea.setVisibility(View.GONE);
                SnpName.setVisibility(View.GONE);
                llMainLayout.setVisibility(View.GONE);
                viewZone.setVisibility(View.GONE);
                viewSubArea.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "There are no cities associated", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == RequestCode.GET_AREA_REQUEST_CODE) {
            if (mDialog.isShowing()) {
                mDialog.hide();
            }
            mAreaResponse = (GetAreasResponse) object;
            if (mAreaResponse.getAreas() == null) {
                mSpnArea.setVisibility(View.GONE);
                tvArea.setVisibility(View.GONE);
                SnpName.setVisibility(View.GONE);
                viewSubArea.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "There are no areas associated", Toast.LENGTH_SHORT).show();
            } else {
                mSpnArea.setVisibility(View.VISIBLE);
                tvArea.setVisibility(View.VISIBLE);
                setAreaSpinner(mAreaResponse);
            }
        } else if (requestCode == RequestCode.GET_AREAS_FOR_CITY) {
            if (mDialog.isShowing()) {
                mDialog.hide();
            }
            mNewAreaResponse = (GetAreasResponse) object;
            if (mNewAreaResponse.getAreas() == null) {
                binding.spnNewArea.setVisibility(View.GONE);
                binding.tvtNewArea.setVisibility(View.GONE);
                binding.viewNewSubArea.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "There are no areas associated", Toast.LENGTH_SHORT).show();
            } else {
                binding.spnNewArea.setVisibility(View.VISIBLE);
                binding.tvtNewArea.setVisibility(View.VISIBLE);
                setNewAreaSpinner();
            }
        } else if (requestCode == RequestCode.GET_SUB_AREAS_REQUEST_CODE) {
            mSubAreasResponse = (GetSubAreasResponse) object;
            if (mDialog.isShowing()) {
                mDialog.hide();
            }
            //testing
            if (mSubAreasResponse.getSubAreas().size() <= 0) {
                viewSubArea.setVisibility(View.GONE);
                Toast.makeText(this, "There are no sub areas associated", Toast.LENGTH_SHORT).show();
            } else {
                viewSubArea.setVisibility(View.VISIBLE);
                setSubAreasSpinner(mSubAreasResponse);
            }
        } else if (requestCode == RequestCode.GET_NEW_SUB_AREAS_REQUEST_CODE) {
            newSubAreaResponse = (GetSubAreasResponse) object;
            if (mDialog.isShowing()) {
                mDialog.hide();
            }
            //testing
            if (newSubAreaResponse.getSubAreas().size() <= 0) {
                binding.viewNewSubArea.setVisibility(View.GONE);
                Toast.makeText(this, "There are no sub areas associated", Toast.LENGTH_SHORT).show();
            } else {
                binding.viewNewSubArea.setVisibility(View.VISIBLE);
                setNewSubAreasSpinner();
            }
        } else if (requestCode == RequestCode.GET_CUSTOMER_RELATED_TO_SO) {
            GetServerResponse serverResponse = (GetServerResponse) object;
            if (mDialog.isShowing()) {
                mDialog.hide();
            }
            if (serverResponse.getCustomersRelatedtoSO() != null && serverResponse.getCustomersRelatedtoSO().size() > 0) {
                llMainLayout.setVisibility(View.VISIBLE);
                SnpName.setVisibility(View.VISIBLE);
                mArrCustomers = serverResponse.getCustomersRelatedtoSO();
                setCustomerSpinner();//hassan Usman Test 2
            } else {
                llMainLayout.setVisibility(View.GONE);
                SnpName.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "There are no customers associated", Toast.LENGTH_LONG).show();
            }

        } else if (requestCode == RequestCode.GET_ZONES_REQUEST_CODE) {
            mZoneResponse = (GetZonesResponse) object;
            if (mDialog.isShowing()) {
                mDialog.hide();
            }
            //testing
            //testing
            if (mZoneResponse.getZones().size() <= 0) {
                Toast.makeText(this, "There are no Zones associated", Toast.LENGTH_SHORT).show();
                viewZone.setVisibility(View.GONE);
                viewSubArea.setVisibility(View.GONE);
                mSpnArea.setVisibility(View.GONE);
                tvArea.setVisibility(View.GONE);
            } else {
                viewZone.setVisibility(View.VISIBLE);
                setZoneSpinner(mZoneResponse);
            }
        } else if (requestCode == RequestCode.GET_NEW_ZONES_REQUEST_CODE) {
            newZonesResponse = (GetZonesResponse) object;
            if (mDialog.isShowing()) {
                mDialog.hide();
            }
            //testing
            //testing
            if (newZonesResponse.getZones().size() <= 0) {
                Toast.makeText(this, "There are no Zones associated", Toast.LENGTH_SHORT).show();
                binding.viewNewZone.setVisibility(View.GONE);
                binding.viewNewSubArea.setVisibility(View.GONE);
                binding.spnNewArea.setVisibility(View.GONE);
                binding.tvtNewArea.setVisibility(View.GONE);
            } else {
                binding.viewNewZone.setVisibility(View.VISIBLE);
                setNewZoneSpinner();
            }
        } else if (requestCode == RequestCode.API_GET_SCHOOL_INFO_FOR_EDIT_REQUEST_CODE) {
            if (mDialog.isShowing()) {
                mDialog.hide();
            }
            GetServerResponse serverResponse = (GetServerResponse) object;
            if (serverResponse.getSchoolsForEdit() != null) {
                for (int i = 0; i < serverResponse.getSchoolsForEdit().size(); i++) {
                    formData = serverResponse.getSchoolsForEdit().get(i);
                    BookSellerIDs = serverResponse.getSylabusID();
                    PublisherIDs = serverResponse.getPublisherID();
                    getEditSchoolInfo(formData);
                }
                //assan Usman Test 2
            }
        } else if (requestCode == RequestCode.EDIT_SCHOOL_REQUEST_INFO) {
            ServerResponse serverResponse = (ServerResponse) object;
            if (serverResponse.getResultType() == ServerCodes.ServerResultType.SERVER_RESULT_SUCCESS) {
                Toast.makeText(this, serverResponse.getMessage(), Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        } else if (requestCode == RequestCode.DELETE_SCHOOLS) {
            ServerResponse serverResponse = (ServerResponse) object;
            Toast.makeText(getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_LONG).show();
            onBackPressed();
        } else if (requestCode == RequestCode.GET_AREA_SELLERS) {
            mBookSellersResponse = (GetBookSellerResponse) object;
            if (mDialog.isShowing()) {
                mDialog.hide();
            }
            if (mBookSellersResponse.getBookSellers().size() <= 0) {
                Toast.makeText(getApplicationContext(), "There are No Sellers Associated", Toast.LENGTH_SHORT).show();
            } else {
                mArrBookSellers = mBookSellersResponse.getBookSellers();
                rlShowSellers.setVisibility(View.VISIBLE);
                mRvSellers.setVisibility(View.VISIBLE);
            }
        } else if (requestCode == RequestCode.GET_DISTRCIST) {
            if (mDialog.isShowing()) {
                mDialog.hide();
            }
            GetServerResponse response = (GetServerResponse) object;
            if (response.getDistricts().size() > 0) {
                mArrDistricts = response.getDistricts();
                setNewDistrictSpinner();
                Log.e("ALL CITIES", mArrDistricts.toString());
            } else {
                Toast.makeText(getApplicationContext(), "There are No Districts in this province", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == RequestCode.GET_TEHSILS) {
            if (mDialog.isShowing()) {
                mDialog.hide();
            }
            GetServerResponse response = (GetServerResponse) object;
            if (response.getTehsils().size() > 0) {
                mArrTehsils = response.getTehsils();
                setNewTehsilSpinner();
                Log.e("ALL Tehsil", mArrTehsils.toString());
            } else {
                Toast.makeText(getApplicationContext(), "There are No Tehsils in this District", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onFailure(String error, RequestCode requestCode) {
        mDialog.hide();
        if (requestCode == RequestCode.GET_CUSTOMER_RELATED_TO_SO) {
            Toast.makeText(this, "Customer not Found", Toast.LENGTH_SHORT).show();

        } else if (requestCode == RequestCode.API_GET_SCHOOL_INFO_FOR_EDIT_REQUEST_CODE) {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();

        } else if (requestCode == RequestCode.EDIT_SCHOOL_REQUEST_INFO) {
            btnsbm.setEnabled(true);
        } else if (requestCode == RequestCode.DELETE_SCHOOLS) {
            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
            onBackPressed();
        } else if (requestCode == RequestCode.GET_SUB_AREAS_REQUEST_CODE) {
            viewSubArea.setVisibility(View.GONE);
            if (mDialog.isShowing()) {
                mDialog.hide();
            }
            Log.e("Result", "Failed");
            Toast.makeText(this, "Failed-- " + error, Toast.LENGTH_SHORT).show();
        } else if (requestCode == RequestCode.GET_CITIES_REQUEST_CODE) {
            viewZone.setVisibility(View.GONE);
            viewSubArea.setVisibility(View.GONE);
            mSpnArea.setVisibility(View.GONE);
            tvArea.setVisibility(View.GONE);
            tvCity.setVisibility(View.GONE);
            mSpnCity.setVisibility(View.GONE);
            if (mDialog.isShowing()) {
                mDialog.hide();
            }
            Log.e("Result", "Failed");
            Toast.makeText(this, "Failed-- " + error, Toast.LENGTH_SHORT).show();

        } else if (requestCode == RequestCode.GET_NEW_ZONES_REQUEST_CODE) {
            binding.viewNewSubArea.setVisibility(View.GONE);
            binding.spnNewArea.setVisibility(View.GONE);
            binding.tvtNewArea.setVisibility(View.GONE);
            if (mDialog.isShowing()) {
                mDialog.hide();
            }
            Log.e("Result", "Failed");
            Toast.makeText(this, "Failed-- " + error, Toast.LENGTH_SHORT).show();

        } else if (requestCode == RequestCode.GET_AREAS_FOR_CITY) {
            binding.viewNewSubArea.setVisibility(View.GONE);

            if (mDialog.isShowing()) {
                mDialog.hide();
            }
            Log.e("Result", "Failed");
            Toast.makeText(this, "Failed-- " + error, Toast.LENGTH_SHORT).show();

        } else if (requestCode == RequestCode.GET_NEW_SUB_AREAS_REQUEST_CODE) {
            binding.viewNewSubArea.setVisibility(View.GONE);
            if (mDialog.isShowing()) {
                mDialog.hide();
            }
            Log.e("Result", "Failed");
            Toast.makeText(this, "Failed-- " + error, Toast.LENGTH_SHORT).show();

        } else if (requestCode == RequestCode.GET_NEW_CITIES_REQUEST_CODE) {
            binding.viewNewZone.setVisibility(View.GONE);
            binding.viewNewSubArea.setVisibility(View.GONE);
            binding.spnNewArea.setVisibility(View.GONE);
            binding.tvtNewArea.setVisibility(View.GONE);
            binding.tvtNewCity.setVisibility(View.GONE);
            binding.spnNewCity.setVisibility(View.GONE);
            if (mDialog.isShowing()) {
                mDialog.hide();
            }
            Log.e("Result", "Failed");
            Toast.makeText(this, "Failed-- " + error, Toast.LENGTH_SHORT).show();

        } else if (requestCode == RequestCode.GET_ZONES_REQUEST_CODE) {
            viewZone.setVisibility(View.GONE);
            viewSubArea.setVisibility(View.GONE);
            mSpnArea.setVisibility(View.GONE);
            tvArea.setVisibility(View.GONE);
            if (mDialog.isShowing()) {
                mDialog.hide();
            }
            Log.e("Result", "Failed");
            Toast.makeText(this, "Failed-- " + error, Toast.LENGTH_SHORT).show();

        } else if (requestCode == RequestCode.GET_AREA_REQUEST_CODE) {
            viewSubArea.setVisibility(View.GONE);
            mSpnArea.setVisibility(View.GONE);
            tvArea.setVisibility(View.GONE);
            if (mDialog.isShowing()) {
                mDialog.hide();
            }
            Log.e("Result", "Failed");
            Toast.makeText(this, "Failed-- " + error, Toast.LENGTH_SHORT).show();

        } else if (requestCode == RequestCode.GET_TEHSILS) {
            if (mDialog.isShowing()) {
                mDialog.hide();
            }
            Log.e("Result", "Failed");
        }
    }

    private final void focusOnView(final View view) {
        scrlEdit.post(new Runnable() {
            @Override
            public void run() {
                scrlEdit.scrollTo(0, view.getBottom());
            }
        });
    }

    private boolean validateForm() {
       if (newCityId < 0) {
            showToast("New City");
            return false;
        }else if (isZoneFormat && newZoneId < 0) {
           showToast("New Zone");
           return false;
       }  else if (newAreaId < 0) {
            showToast("New Area");
            return false;
        } else if (isZoneFormat && newSubAreaId < 0) {
            showToast("New SubArea");
            return false;
        } else if (mSpnNewDistrict.getSelectedItemPosition() == 0) {
            ((TextView) mSpnNewDistrict.getSelectedView()).setError("District is Empty");
            showToast("District");
            return false;
        } else if (mSpnNewTehsil.getSelectedItemPosition() == 0) {
            ((TextView) mSpnNewTehsil.getSelectedView()).setError("Tehsil is Empty");
            showToast("Tehsil");
            return false;
        } else {
            return true;
        }
    }

    private void setExamBoardSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.exam_boards, android.R.layout.simple_spinner_dropdown_item);
        binding.spExamBoard.setAdapter(adapter);
        binding.spExamBoard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedExamBoard = "";
                } else {
                    selectedExamBoard = parent.getItemAtPosition(position).toString();
                }
                // Save the selected country to a variable or perform any desired action

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case where no country is selected, if needed
            }
        });

    }

    private void setWorkingPrioritySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.working_priority, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spnWorkingPriority.setAdapter(adapter);
        binding.spnWorkingPriority.setEnabled(false);
    }

    private ArrayList<GetZonesResponse.Zone> mArrZones = null;
    private ArrayList<GetZonesResponse.Zone> newArrZones = null;
    private ArrayAdapter<GetZonesResponse.Zone> zoneAdapter = null;
    private ArrayAdapter<GetZonesResponse.Zone> newZoneAdapter = null;

    private void setZoneSpinner(GetZonesResponse mZonesResponse) {
        zoneID = -1;
        mArrZones = mZonesResponse != null ? (ArrayList<GetZonesResponse.Zone>) mZonesResponse.getZones() : null;

        zoneAdapter = new ArrayAdapter<GetZonesResponse.Zone>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                mArrZones
        );
        spZone.setAdapter(zoneAdapter);

        spZone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                zoneID = mArrZones != null ? mArrZones.get(position).getID() : -1;
                loadAreas();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mDialog.isShowing()){
            mDialog.hide();
        }
    }

    private void setNewZoneSpinner() {
        newArrZones = newZonesResponse != null ? (ArrayList<GetZonesResponse.Zone>) newZonesResponse.getZones() : null;

        newZoneAdapter = new ArrayAdapter<GetZonesResponse.Zone>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                newArrZones
        );
        binding.spNewZone.setAdapter(zoneAdapter);
        binding.spNewZone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newZoneId = newArrZones != null ? newArrZones.get(position).getID() : -1;
                loadNewAreas();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void loadAreas() {
        if (mDialog != null) {
            mDialog.show();
            mDialog.setMessage("Fetching areas please wait...");
        }

        mService = RestClient.getInstance(this);
        Call<GetAreasResponse> userObject = mService.getAreas(
                String.valueOf(cityId),
                mLoginResponse.getData().getSOID(),
                zoneID
        );

        RestCallbackObject callbackObject = new RestCallbackObject(this, this, RequestCode.GET_AREA_REQUEST_CODE)
                .showProgress(true, 0)
                .dontHideProgress(false);

        userObject.enqueue(callbackObject);
    }

    private void loadNewAreas() {
        if (mDialog != null) {
            mDialog.show();
            mDialog.setMessage("Fetching New areas please wait...");
        }

        mService = RestClient.getInstance(this);
        Call<GetAreasResponse> userObject = mService.getAreas(
                String.valueOf(cityId),
                mLoginResponse.getData().getSOID(),
                zoneID
        );

        RestCallbackObject callbackObject = new RestCallbackObject(this, this, RequestCode.GET_AREAS_FOR_CITY)
                .showProgress(true, 0)
                .dontHideProgress(false);

        userObject.enqueue(callbackObject);
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg + " is required", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void addRemoveItem(CurrentSyllabus item, boolean isAdd) {
        if (isAdd) {
            for (int i = 0; i < mArrSyllabusItems.size(); i++) {
                if (mArrSyllabusItems.get(i).equals(item)) {
                    mArrSyllabusItems.get(i).setISChecked(true);
                }
            }
            mArrSyllabusItemsSelected.add(item);
        } else {
            for (int i = 0; i < mArrSyllabusItems.size(); i++) {
                if (mArrSyllabusItems.get(i).equals(item)) {
                    mArrSyllabusItems.get(i).setISChecked(false);
                }
            }
            mArrSyllabusItemsSelected.remove(item);
        }
    }

    @Override
    public void addRemoveItem(GetBookSellerResponse.BookSeller item, boolean isAdd) {
        if (isAdd) {
            for (int i = 0; i < mArrBookSellers.size(); i++) {
                if (mArrBookSellers.get(i).equals(item)) {
                    mArrBookSellers.get(i).setChecked(true);
                }
            }
            mArrSelectedBookSellers.add(item);
        } else {
            for (int i = 0; i < mArrBookSellers.size(); i++) {
                if (mArrBookSellers.get(i).equals(item)) {
                    mArrBookSellers.get(i).setChecked(false);
                }
            }
            mArrSelectedBookSellers.remove(item);
        }

    }

    private void setSubAreasSpinner(GetSubAreasResponse mAreaResponse) {
        subAreaID = -1;
        mSubAreas = mAreaResponse.getSubAreas();
        subAreaAdapter = new ArrayAdapter<GetSubAreasResponse.SubArea>(this, android.R.layout.simple_spinner_dropdown_item, mSubAreas);
        spSubArea.setAdapter(subAreaAdapter);

        ArrayList<SampleSearchModel> subAreaSearchList = new ArrayList<SampleSearchModel>();
        for (int i = 0; i < mSubAreas.size(); i++) {
            GetSubAreasResponse.SubArea area = mSubAreas.get(i);
            String name = area.getName();
            int id = area.getID();
            subAreaSearchList.add(new SampleSearchModel(name, id));
        }

        viewSubArea.setVisibility(View.VISIBLE);
        spSubArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                ((TextView) view).setTextColor(getResources().getColorStateList(R.color.black));
                TextView selectedText = (TextView) parent.getChildAt(0);
                if (selectedText != null) {
                    selectedText.setTextColor(Color.BLACK);
                } else {
                    selectedText.setTextColor(Color.BLACK);

                }
                if (position != 0) {
                    subAreaID = mSubAreas.get(position).getID();
                    GetCustomerRelatedToSubArea(subAreaID);
                    callGetBookSellerService(String.valueOf(arrCitiesRelatedToRegion.get(cityPosition).getID()), String.valueOf(mArrAreas.get(areaPosition).getID()));
                    rlShowSellers.setVisibility(View.GONE);
                    mRvSellers.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /*spSubArea.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mSubAreas.clear();
                    SimpleSearchDialogCompat temp = new SimpleSearchDialogCompat(EditSchoolInformation.this, "Search...", "What are you looking for...?", null, subAreaSearchList, new SearchResultListener<SampleSearchModel>() {
                        @Override
                        public void onSelected(BaseSearchDialogCompat dialog, SampleSearchModel item, int position) {


                            mSubAreas.add(new GetSubAreasResponse().new SubArea(item.getID(), item.getTitle()));
                            subAreaAdapter = new ArrayAdapter<GetSubAreasResponse.SubArea>(EditSchoolInformation.this, android.R.layout.simple_spinner_dropdown_item, mSubAreas);
                            spSubArea.setAdapter(subAreaAdapter);

                            dialog.dismiss();
                        }
                    });
                    temp.show();
                    temp.getTitleTextView().setTextColor(Color.BLACK);
                    temp.getSearchBox().setTextColor(getResources().getColorStateList(R.color.innercolor));
                }
                return true;
            }
        });*/


   /*     mSpnArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });*/
    }

    private void setNewSubAreasSpinner() {
        newSubAreas = newSubAreaResponse.getSubAreas();
        newSubAreaAdapter = new ArrayAdapter<GetSubAreasResponse.SubArea>(this, android.R.layout.simple_spinner_dropdown_item, newSubAreas);
        binding.spNewSubArea.setAdapter(newSubAreaAdapter);

        ArrayList<SampleSearchModel> subAreaSearchList = new ArrayList<SampleSearchModel>();
        for (int i = 0; i < newSubAreas.size(); i++) {
            GetSubAreasResponse.SubArea area = newSubAreas.get(i);
            String name = area.getName();
            int id = area.getID();
            subAreaSearchList.add(new SampleSearchModel(name, id));
        }

        binding.viewNewSubArea.setVisibility(View.VISIBLE);
        binding.spNewSubArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                ((TextView) view).setTextColor(getResources().getColorStateList(R.color.black));
                TextView selectedText = (TextView) parent.getChildAt(0);
                if (selectedText != null) {
                    selectedText.setTextColor(Color.BLACK);
                } else {
                    selectedText.setTextColor(Color.BLACK);

                }

                newSubAreaPosition = position;
                newSubAreaId = newSubAreas.get(position).getID();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /*spSubArea.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mSubAreas.clear();
                    SimpleSearchDialogCompat temp = new SimpleSearchDialogCompat(EditSchoolInformation.this, "Search...", "What are you looking for...?", null, subAreaSearchList, new SearchResultListener<SampleSearchModel>() {
                        @Override
                        public void onSelected(BaseSearchDialogCompat dialog, SampleSearchModel item, int position) {


                            mSubAreas.add(new GetSubAreasResponse().new SubArea(item.getID(), item.getTitle()));
                            subAreaAdapter = new ArrayAdapter<GetSubAreasResponse.SubArea>(EditSchoolInformation.this, android.R.layout.simple_spinner_dropdown_item, mSubAreas);
                            spSubArea.setAdapter(subAreaAdapter);

                            dialog.dismiss();
                        }
                    });
                    temp.show();
                    temp.getTitleTextView().setTextColor(Color.BLACK);
                    temp.getSearchBox().setTextColor(getResources().getColorStateList(R.color.innercolor));
                }
                return true;
            }
        });*/


   /*     mSpnArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            callgetDeeletSchool(mArrCustomers.get(customerPosition).getID());
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.dismiss();
                            break;
                    }
                }
            };
            if (SnpName.getSelectedItem() == null || mArrCustomers.size() <= 0) {
                Toast.makeText(getApplicationContext(), "There are no schools loaded yet...", Toast.LENGTH_LONG).show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditSchoolInformation.this);
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSubmit:
                schoolRegRequest = new SchoolRegRequest();
                if (validateForm()) {
                    setShopReq(schoolRegRequest);
                    mDialog.setMessage("Submitting Details...");
                    mDialog.show();
                    callRegService();
                }
                break;
            case R.id.ib_show_seller_items:
                if (mArrBookSellers.size() > 0) {
                    if (mRvSellers.getVisibility() == View.GONE) {
                        mRvSellers.setVisibility(View.VISIBLE);
                        ibshowSellers.setBackgroundResource(R.drawable.ic_expand_less_black_24dp);
                        sellerLayoutManager = new LinearLayoutManager(getApplicationContext());
                        mRvSellers.setLayoutManager(sellerLayoutManager);
                        sellerAdapter = new BookSellerItemsAdapter(mArrBookSellers, this);
                        mRvSellers.setAdapter(sellerAdapter);
                    } else {
                        mRvSellers.setVisibility(View.GONE);
                        ibshowSellers.setBackgroundResource(R.drawable.ic_expand_more_black_24dp);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No item found", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.rl_select_Publishers:
                if (mArrSyllabusItems.size() > 0) {
                    if (mrvItems.getVisibility() == View.GONE) {
                        mrvItems.setVisibility(View.VISIBLE);
                        ibShowPublishers.setBackgroundResource(R.drawable.ic_expand_less_black_24dp);
                        mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        mrvItems.setLayoutManager(mLayoutManager);
                        mAdapter = new SelectedPublisherItemsAdapter(mArrSyllabusItems, this, PublisherIDs);
                        mrvItems.setAdapter(mAdapter);
                    } else {
                        mrvItems.setVisibility(View.GONE);
                        ibShowPublishers.setBackgroundResource(R.drawable.ic_expand_more_black_24dp);
                    }
                } else
                    Toast.makeText(getApplicationContext(), "No item found", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rl_select_Sellers:
                if (mArrBookSellers.size() > 0) {
                    if (mRvSellers.getVisibility() == View.GONE) {
                        mRvSellers.setVisibility(View.VISIBLE);
                        ibshowSellers.setBackgroundResource(R.drawable.ic_expand_less_black_24dp);
                        sellerLayoutManager = new LinearLayoutManager(getApplicationContext());
                        mRvSellers.setLayoutManager(sellerLayoutManager);
                        sellerAdapter = new SelectedBookSellerItemsAdapter(mArrBookSellers, this, BookSellerIDs);
                        mRvSellers.setAdapter(sellerAdapter);
                    } else {
                        mRvSellers.setVisibility(View.GONE);
                        ibshowSellers.setBackgroundResource(R.drawable.ic_expand_more_black_24dp);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No item found", Toast.LENGTH_SHORT).show();
                }
                break;

        }


    }
}
