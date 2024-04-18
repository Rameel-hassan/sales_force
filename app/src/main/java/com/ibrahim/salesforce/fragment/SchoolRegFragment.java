package com.ibrahim.salesforce.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ibrahim.salesforce.R;
import com.ibrahim.salesforce.activity.DashboardActivity;
import com.ibrahim.salesforce.activity.FragmentShownActivity;
import com.ibrahim.salesforce.adapters.AddRemoveItem;
import com.ibrahim.salesforce.adapters.BookSellerItemsAdapter;
import com.ibrahim.salesforce.adapters.SyllabusItemsAdapter;
import com.ibrahim.salesforce.databinding.FragmentSchoolRegistrationBinding;
import com.ibrahim.salesforce.dialoge.AreaRegistrationDialogFrag;
import com.ibrahim.salesforce.dialoge.SubAreaRegistrationDialog;
import com.ibrahim.salesforce.model.SampleSearchModel;
import com.ibrahim.salesforce.network.ApiService;
import com.ibrahim.salesforce.network.RequestCode;
import com.ibrahim.salesforce.network.RestCallbackObject;
import com.ibrahim.salesforce.network.RestClient;
import com.ibrahim.salesforce.network.ServerCodes;
import com.ibrahim.salesforce.network.ServerConnectListenerObject;
import com.ibrahim.salesforce.permissions.RuntimePermissionHandler;
import com.ibrahim.salesforce.permissions.RuntimePermissionUtils;
import com.ibrahim.salesforce.request.SchoolRegRequest;
import com.ibrahim.salesforce.response.CurrentSyllabus;
import com.ibrahim.salesforce.response.Fee;
import com.ibrahim.salesforce.response.GetAreasResponse;
import com.ibrahim.salesforce.response.GetBookSellerResponse;
import com.ibrahim.salesforce.response.GetCitiesResponse;
import com.ibrahim.salesforce.response.GetServerResponse;
import com.ibrahim.salesforce.response.GetSubAreasResponse;
import com.ibrahim.salesforce.response.GetZonesResponse;
import com.ibrahim.salesforce.response.Region;
import com.ibrahim.salesforce.response.ServerResponse;
import com.ibrahim.salesforce.utilities.AppKeys;
import com.ibrahim.salesforce.utilities.GPSTrackerService;
import com.ibrahim.salesforce.utilities.PermissionResultCallBack;
import com.ibrahim.salesforce.utilities.Utility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import retrofit2.Call;

public class SchoolRegFragment extends Fragment implements AreaDialog, View.OnClickListener, ServerConnectListenerObject, PermissionResultCallBack, AddRemoveItem.AddRemoveSyllabus, AddRemoveItem.AddRemoveBookSellers {

    // region

    private Spinner mSpnCity;
    private RelativeLayout rlSpCity;
    private Spinner mSpnArea;
    private RelativeLayout mViewArea;
    private Spinner mSpnFeeStruct;
    private Spinner mSpnEduSys;

    private String customerType = "School";
    private Spinner mWorkingPriority;
    private Spinner mSpnSessionDate;
    private Spinner mSpnBookShop;
    private Spinner mSpnRegion;
    private Spinner mSpnCountry;
    private Spinner mSpnSyllSelectionDate;
    private EditText mEtEmail;
    private EditText mEtSchoolName;
    private EditText mEtOwnerPrincipalName;
    private EditText mEtContactNumber;
    private EditText mEtCellNumber;
    private EditText mEtWhatsAppNo;
    private EditText mEtAddress;
    private EditText mEtContactPerson;
    private EditText mEtOtherPersonNumber;
    private EditText mEtBranches;
    private EditText mEtStudentStrength;
    private EditText mEtRemarks;
    private ImageView mIvAddArea;
    private Button mBtnSubmit;
    private ImageView mIvCapture;
    private TextView mTvLong;
    private TextView mTvLat, mTvSyllabus;
    private ImageView mIvLocation;
    private ImageView mIvImage;
    private ScrollView scrlView;
    private CheckBox cbxWhatsAppNo;
    private List<GetCitiesResponse.City> mArrCities;
    private List<Region> mArrRegions;
    private List<GetAreasResponse.Area> mArrAreas;
    private List<GetSubAreasResponse.SubArea> mSubAreas;
    private List<GetZonesResponse.Zone> mArrZones;
    private List<GetBookSellerResponse.BookSeller> mArrBookSellers;
    private List<GetBookSellerResponse.BookSeller> mArrSelectedBookSellers;
    private List<String> mArrEduSys;
    private List<Fee> mArrFeeStruct;
    private String selectedExamBoard = "";
    private List<String> mArrSessionDate;
    private List<String> mArrBookShop;
    private List<String> mArrSyllSelectionDate;
    private List<String> mArrSampleMonth;
    private String mRegionId;
    int cityId = -1;
    private int districtID = 0, tehsilID = 0;
    private ArrayAdapter<String> adapter;
    private EditText mEtTeacherStrength;

    private static final int CAMERA_INTENT = 30;
    private static final int GALLERY_INTENT = 40;
    private final int REQ_CODE_CAMERA_PERMISSION = 1001;
    private final int REQ_CODE_LOCATION_PERMISSION = 1002;

    private ProgressDialog mDialog;
    private Bitmap bitmap;

    private SchoolRegRequest mSchoolRegReq;
    private GetServerResponse mLoginResponse;
    private GetCitiesResponse mCitiesResponse;
    private GetAreasResponse mAreaResponse;
    private GetSubAreasResponse mSubAreasResponse;
    private GetZonesResponse mZonesResponse;
    private GetBookSellerResponse mBookSellersResponse;
    private boolean isLocationVisible;
    private ApiService mService;
    private Context mContext;


    private List<CurrentSyllabus> mArrSyllabusItems;
    private List<CurrentSyllabus> mArrSyllabusItemsSelected;
    private ImageButton ibShowItems, ibshowSellers;
    private RecyclerView mRvItems, mRvSellers;
    private RecyclerView.LayoutManager mLayoutManager, sellerLayoutManager;
    private RecyclerView.Adapter mAdapter, sellerAdapter;
    private RelativeLayout rlShowSellers;
    public GPSTrackerService mLocationService = new GPSTrackerService();
    public EditText etWebsite;
    public Spinner spnExamBoard;

    // endregion
    public String selectedCountry = "";
    private boolean isZoneFormat = false;

    public static SchoolRegFragment newInstance() {
        SchoolRegFragment result = new SchoolRegFragment();
//        Bundle args = new Bundle();
//        args.putInt(ARG_SECTION_POSITION, sectionPos);
//        result.setArguments(args);
        return (result);
    }


    private FragmentSchoolRegistrationBinding binding;

    /**
     * here layout for fragment is set.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSchoolRegistrationBinding.inflate(inflater, container, false);
        mContext = getActivity();
        Paper.init(mContext);
        parseStoredData();
        initGui();
        Log.d("class_name", this.getClass().getSimpleName());
        return binding.getRoot();
    }

    /**
     * all GUI components are initialized here
     * image is set
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initGui() {
        // region
        mViewArea = binding.viewArea;
        mSpnCity = binding.spCity;
        mSpnArea = binding.spArea;
        spnExamBoard = binding.spExamBoard;
        etWebsite = binding.etWebsite;
        mSpnEduSys = binding.spEducationSystem;
        mSpnFeeStruct = binding.spFeeStructure;
        mSpnSessionDate = binding.spSessionDate;
        mSpnBookShop = binding.spBookShop;
        mWorkingPriority = binding.spnWorkingPriority;
        mSpnRegion = binding.spnRegion;
        mSpnCountry = binding.spnCountry;
        mSpnSyllSelectionDate = binding.spnSyllSelectionDate;
        mEtSchoolName = binding.etSchoolName;
        mEtOwnerPrincipalName = binding.etOwnerPrincipalName;
        mEtContactNumber = binding.etContactNumber;
        mEtCellNumber = binding.etCellNumber;
        mEtWhatsAppNo = binding.etWhatsAppNo;
        mEtEmail = binding.etEmail;
        mEtAddress = binding.etAddress;
        mEtContactPerson = binding.etContactPerson;
        mEtOtherPersonNumber = binding.etOtherPersonPhNumber;
        mEtBranches = binding.etNoOfBranches;
        mEtStudentStrength = binding.etStudentStrength;
        mEtTeacherStrength = binding.etTeacherStrength;
        mEtRemarks = binding.etRemarks;
        mTvSyllabus = binding.tvSelectSyllabus;
        mTvLong = binding.tvLong;
        mTvLat = binding.tvLat;
        mIvImage = binding.ivImage;
        ibShowItems = binding.ibShowItems;
        ibshowSellers = binding.ibShowSellerItems;
        mRvSellers = binding.rvSellerItems;
        mRvItems = binding.rvItems;
        rlShowSellers = binding.rlSelectSeller;
        mBtnSubmit = binding.btnSubmit;
        mIvCapture = binding.ivCapture;
        mIvLocation = binding.ivLocation;
        cbxWhatsAppNo = binding.cbxWhatsAppNo;
        scrlView = binding.scrlView;
        mSpnCity.setVisibility(View.GONE);
        rlSpCity = binding.rlCitySpinner;

        rlSpCity.setVisibility(View.GONE);

        mSpnArea.setVisibility(View.GONE);
        mViewArea.setVisibility(View.GONE);
        mBtnSubmit.setOnClickListener(this);
        mTvSyllabus.setOnClickListener(this);
        mIvCapture.setOnClickListener(this);
        mIvLocation.setOnClickListener(this);
        ibShowItems.setOnClickListener(this);
        ibShowItems.setOnClickListener(this);
        ibshowSellers.setOnClickListener(this);
        rlShowSellers.setOnClickListener(this);
        mIvAddArea = binding.ivAddArea;
        mIvAddArea.setOnClickListener(this);
        binding.ivAddSubArea.setOnClickListener(this);
        mDialog = new ProgressDialog(mContext);
        mDialog.setCancelable(false);
        mArrSyllabusItemsSelected = new ArrayList<>();
        mArrSelectedBookSellers = new ArrayList<>();
        mArrBookSellers = new ArrayList<>();
        setAllSpinners();
        mTvLat.setText(Double.toString(DashboardActivity.mLocationService.getLatitude()));
        mTvLong.setText(Double.toString(DashboardActivity.mLocationService.getLongitude()));
        setWhatsAppNo();
        mEtContactNumber.setFilters(new InputFilter[]{getPhoneFilter(), new InputFilter.LengthFilter(12)});

//all caps
        //mEtEmail.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        mEtSchoolName.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        mEtOwnerPrincipalName.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        mEtContactNumber.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        mEtCellNumber.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        mEtWhatsAppNo.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        mEtAddress.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        mEtContactPerson.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        mEtOtherPersonNumber.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        mEtBranches.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        mEtStudentStrength.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        mEtRemarks.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        mEtContactNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (mEtContactNumber.getText().toString().isEmpty()) {
                        mEtContactNumber.setText("92");
                        Selection.setSelection(mEtContactNumber.getText(), mEtContactNumber.getText().length());
                    }
                } else {
                    if (mEtContactNumber.getText().toString().equalsIgnoreCase("92")) {
                        mEtContactNumber.setFilters(new InputFilter[]{});
                        mEtContactNumber.setText("");
                        mEtContactNumber.setFilters(new InputFilter[]{getPhoneFilter(), new InputFilter.LengthFilter(12)});

                    }
                }
            }
        });

        mEtStudentStrength.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int studentStrength = Integer.parseInt(mEtStudentStrength.getText().toString());
                    if (studentStrength >= 1 && studentStrength <= 1999) {
                        mWorkingPriority.setSelection(0);
                    } else if (studentStrength >= 2000 && studentStrength <= 3000) {
                        mWorkingPriority.setSelection(1);
                    } else if (studentStrength > 3000) {
                        mWorkingPriority.setSelection(2);
                    } else {
                        mWorkingPriority.setSelection(0);
                    }
                } catch (NumberFormatException e) {
                    //mEtStudentStrength.setText("");
                    mEtStudentStrength.setError("Please enter valid student strength");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEtCellNumber.setFilters(new InputFilter[]{getCellFilter(), new InputFilter.LengthFilter(12)});
        mEtCellNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (mEtCellNumber.getText().toString().isEmpty()) {
                        mEtCellNumber.setText("92");
                        Selection.setSelection(mEtCellNumber.getText(), mEtCellNumber.getText().length());
                    }
                } else {
                    if (mEtCellNumber.getText().toString().equalsIgnoreCase("92")) {
                        mEtCellNumber.setFilters(new InputFilter[]{});
                        mEtCellNumber.setText("");
                        mEtCellNumber.setFilters(new InputFilter[]{getCellFilter(), new InputFilter.LengthFilter(12)});

                    }
                }
            }
        });

        isZoneFormat = mLoginResponse.getData().isZoneFormat();
        binding.viewZone.setVisibility(View.GONE);
        binding.viewSubArea.setVisibility(View.GONE);

        binding.rbShop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    binding.etNoOfBranches.setVisibility(View.GONE);
                    binding.tvExamBoard.setVisibility(View.GONE);
                    binding.rlExamBoard.setVisibility(View.GONE);
                    binding.etStudentStrength.setVisibility(View.GONE);
                    binding.rlWorkingPriority.setVisibility(View.GONE);
                    binding.rlReviewMonth.setVisibility(View.GONE);
                    binding.rlSessionMonth.setVisibility(View.GONE);
                    binding.rlSelectSyllabus.setVisibility(View.GONE);

                    if (cityId != -1 && cityId != 0&&zoneID==-1) {
                        loadZones();
                    }
                }
            }
        });
        binding.rbSchool.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    binding.etNoOfBranches.setVisibility(View.VISIBLE);
                    binding.tvExamBoard.setVisibility(View.VISIBLE);
                    binding.rlExamBoard.setVisibility(View.VISIBLE);
                    binding.etStudentStrength.setVisibility(View.VISIBLE);
                    binding.rlWorkingPriority.setVisibility(View.VISIBLE);
                    binding.rlReviewMonth.setVisibility(View.VISIBLE);
                    binding.rlSessionMonth.setVisibility(View.VISIBLE);
                    binding.rlSelectSyllabus.setVisibility(View.VISIBLE);
                    if (cityId != -1 && cityId != 0 && zoneID != -1 && zoneID != 0) {
                        callGetAreaService(cityId, zoneID);
                    }
                }
            }
        });

        // endregion
    }

    private void loadZones() {
        mDialog.show();
        
        mDialog.setMessage("Fetching Zones please wait...");
        mService = RestClient.getInstance(mContext);
        Call<GetZonesResponse> userObject = mService.getZones(String.valueOf(cityId),mLoginResponse.getData().getSOID());
        RestCallbackObject callbackObject = new RestCallbackObject(getActivity(), this, RequestCode.GET_ZONES_REQUEST_CODE).showProgress(true, 0).dontHideProgress(false);
        userObject.enqueue(callbackObject);
    }

    private void parseStoredData() {
        mLoginResponse = Paper.book().read(AppKeys.KEY_LOGIN_RESPONSE);
        mArrSyllabusItems = mLoginResponse.getData().getCurrentSyllabus();
    }

    private void setWhatsAppNo() {
        cbxWhatsAppNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mEtWhatsAppNo.setText(mEtContactNumber.getText().toString());
                } else if (!isChecked) {
                    mEtWhatsAppNo.setText("");
                }
            }
        });
    }

    public void setAllSpinners() {
        setRegionSpinner();
        setCountrySpinner();
        setEduSysSpinner();
        setFeeStructSpinner();
        setSessionDateSpinner();
        setBookShopSpinner();
        setSyllSelectionDateSpinner();
        setWorkingPrioritySpinner();
        setExamBoardSpinner();
        setCustomerTypeSpinner();
    }

    public void setCustomerTypeSpinner(){
        // Define the list of items
        final String[] items = {"School", "Academy", "College"};

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, items);

        // Apply the adapter to the spinner
        binding.spInstituteType.setAdapter(adapter);

        // Set an item selected listener to capture the selected item
        binding.spInstituteType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Store the selected item in the customerType variable
                customerType = items[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle the case where nothing is selected if needed
            }
        });
    }

    private void setExamBoardSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext, R.array.exam_boards, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnExamBoard.setAdapter(adapter);
        spnExamBoard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext, R.array.working_priority, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mWorkingPriority.setAdapter(adapter);
        mWorkingPriority.setEnabled(false);
    }

    private void setCountrySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext, R.array.country_list, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnCountry.setAdapter(adapter);
        mSpnCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCountry = parent.getItemAtPosition(position).toString();
                // Save the selected country to a variable or perform any desired action

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case where no country is selected, if needed
            }
        });
    }

    private InputFilter getPhoneFilter() {
        Selection.setSelection(mEtContactNumber.getText(), mEtContactNumber.getText().length());
        mEtContactNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().startsWith("92")) {
                    if (mEtContactNumber.getFilters() != null && mEtContactNumber.getFilters().length > 0) {
                        mEtContactNumber.setText("92");
                        Selection.setSelection(mEtContactNumber.getText(), mEtContactNumber.getText().length());
                    }
                }
            }
        });
        // Input filter to restrict user to enter only digits..
        InputFilter filter = new InputFilter() {

            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!String.valueOf(getString(R.string.digits_number)).contains(String.valueOf(source.charAt(i)))) {
                        return "";
                    }
                }
                return null;
            }
        };
        return filter;
    }

    private InputFilter getCellFilter() {
        Selection.setSelection(mEtCellNumber.getText(), mEtCellNumber.getText().length());
        mEtCellNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().startsWith("92")) {
                    if (mEtCellNumber.getFilters() != null && mEtCellNumber.getFilters().length > 0) {
                        mEtCellNumber.setText("92");
                        Selection.setSelection(mEtCellNumber.getText(), mEtCellNumber.getText().length());
                    }
                }
            }
        });
        // Input filter to restrict user to enter only digits..
        InputFilter filter = new InputFilter() {

            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!String.valueOf(getString(R.string.digits_number)).contains(String.valueOf(source.charAt(i)))) {
                        return "";
                    }
                }
                return null;
            }
        };
        return filter;
    }

    private void setRegionSpinner() {
        mArrRegions = mLoginResponse.getData().getRegion();
        Region emptyRegion = new Region(0, "Select Region*");
        mArrRegions.add(0, emptyRegion);
        ArrayAdapter<Region> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, mArrRegions);
        mSpnRegion.setAdapter(adapter);
        mSpnRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    callGetCityService(mArrRegions.get(position).getID(), mLoginResponse.getData().getSOID());
                    mSpnCity.setVisibility(View.GONE);
                    rlSpCity.setVisibility(View.GONE);
                    binding.viewZone.setVisibility(View.GONE);
                    binding.viewSubArea.setVisibility(View.GONE);
                    mSpnArea.setVisibility(View.GONE);
                    mViewArea.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    ArrayAdapter<GetCitiesResponse.City> cityAdapter;

    private void setCitySpinner(GetCitiesResponse citiesResponse) {
        cityId = -1;
        mArrCities = citiesResponse.getCities();
        GetCitiesResponse.City emptyCity = new GetCitiesResponse().new City(0, "Select City*");
        mArrCities.add(0, emptyCity);
        cityAdapter = new ArrayAdapter<GetCitiesResponse.City>(mContext, android.R.layout.simple_spinner_dropdown_item, mArrCities);
        mSpnCity.setAdapter(cityAdapter);
        mSpnCity.setVisibility(View.VISIBLE);
        rlSpCity.setVisibility(View.VISIBLE);
        ArrayList<SampleSearchModel> citiesSearchList = new ArrayList<SampleSearchModel>();
        for (int i = 0; i < mArrCities.size(); i++) {
            GetCitiesResponse.City city = mArrCities.get(i);
            String name = city.getName();
            int id = city.getID();
            citiesSearchList.add(new SampleSearchModel(name, id));

        }
        mSpnCity.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mArrCities.clear();
                    SimpleSearchDialogCompat temp = new SimpleSearchDialogCompat(getActivity(), "Search...", "What are you looking for...?", null, citiesSearchList, new SearchResultListener<SampleSearchModel>() {
                        @Override
                        public void onSelected(BaseSearchDialogCompat dialog, SampleSearchModel item, int position) {

                            mArrCities.add(new GetCitiesResponse().new City(item.getID(), item.getTitle()));
                            cityAdapter = new ArrayAdapter<GetCitiesResponse.City>(mContext, android.R.layout.simple_spinner_dropdown_item, mArrCities);
                            mSpnCity.setAdapter(cityAdapter);
                            mSpnCity.setVisibility(View.VISIBLE);
                            rlSpCity.setVisibility(View.VISIBLE);
                            mSpnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                    ((TextView) view).setTextColor(getResources().getColorStateList(R.color.black));
                                    TextView selectedText = (TextView) parent.getChildAt(0);
                                    if (selectedText != null) {
                                        selectedText.setTextColor(Color.BLACK);
                                    } else {
                                        selectedText.setTextColor(Color.BLACK);

                                    }
                                    cityId = mArrCities.get(position).getID();
                                    if (mArrCities.get(position).getID() != 0) {
                                        if (isZoneFormat) {
                                            loadZones();
                                        } else {
                                            //TODO Auto-generated
                                            callGetAreaService(mArrCities.get(position).getID());
                                        }
                                    }
                                    binding.viewZone.setVisibility(View.GONE);
                                    binding.viewSubArea.setVisibility(View.GONE);
                                    mSpnArea.setVisibility(View.GONE);
                                    mViewArea.setVisibility(View.GONE);

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
   /*     mSpnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });*/
    }

    private int areaID = -1;
    private int subAreaID = -1;
    private int zoneID = -1;
    ArrayAdapter<GetAreasResponse.Area> areaAdapter;
    ArrayAdapter<GetSubAreasResponse.SubArea> subAreaAdapter;
    ArrayAdapter<GetZonesResponse.Zone> zoneAdapter;

    private void setAreaSpinner(GetAreasResponse mAreaResponse) {
        areaID = -1;
        mArrAreas = mAreaResponse.getAreas();
        GetAreasResponse.Area emptyCity = new GetAreasResponse().new Area(0, "Select Area*");
        mArrAreas.add(0, emptyCity);
        areaAdapter = new ArrayAdapter<GetAreasResponse.Area>(mContext, android.R.layout.simple_spinner_dropdown_item, mArrAreas);
        mSpnArea.setAdapter(areaAdapter);
        mSpnArea.setVisibility(View.VISIBLE);
        mViewArea.setVisibility(View.VISIBLE);

        ArrayList<SampleSearchModel> areaSearchList = new ArrayList<SampleSearchModel>();
        for (int i = 0; i < mArrAreas.size(); i++) {
            GetAreasResponse.Area area = mArrAreas.get(i);
            String name = area.getName();
            int id = area.getID();
            areaSearchList.add(new SampleSearchModel(name, id));

        }
        mSpnArea.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mArrAreas.clear();
                    SimpleSearchDialogCompat temp = new SimpleSearchDialogCompat(getActivity(), "Search...", "What are you looking for...?", null, areaSearchList, new SearchResultListener<SampleSearchModel>() {
                        @Override
                        public void onSelected(BaseSearchDialogCompat dialog, SampleSearchModel item, int position) {


                            mArrAreas.add(new GetAreasResponse().new Area(item.getID(), item.getTitle()));
                            areaAdapter = new ArrayAdapter<GetAreasResponse.Area>(mContext, android.R.layout.simple_spinner_dropdown_item, mArrAreas);
                            mSpnArea.setAdapter(areaAdapter);
                            mSpnArea.setVisibility(View.VISIBLE);
                            mViewArea.setVisibility(View.VISIBLE);
                            mSpnArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                                    ((TextView) view).setTextColor(getResources().getColorStateList(R.color.black));
                                    TextView selectedText = (TextView) parent.getChildAt(0);
                                    if (selectedText != null) {
                                        selectedText.setTextColor(Color.BLACK);
                                    } else {
                                        selectedText.setTextColor(Color.BLACK);

                                    }

                                    areaID = mArrAreas.get(position).getID();
                                    if (mArrAreas.get(position).getID() != 0) {
                                        if (isZoneFormat) {
                                            callGetSubAreas();
                                        } else {
                                            //TODO: check
                                            callGetBookSellerService(cityId, String.valueOf(mArrAreas.get(position).getID()));
                                        }
                                    }
                                    binding.viewSubArea.setVisibility(View.GONE);
                                    rlShowSellers.setVisibility(View.GONE);
                                    mRvSellers.setVisibility(View.GONE);
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


   /*     mSpnArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });*/
    }

    private void setSubAreasSpinner(GetSubAreasResponse mAreaResponse) {
        subAreaID = -1;
        mSubAreas = mAreaResponse.getSubAreas();
        GetSubAreasResponse.SubArea emptyCity = new GetSubAreasResponse().new SubArea(0, "Select Sub-Area*");
        mSubAreas.add(0, emptyCity);
        subAreaAdapter = new ArrayAdapter<GetSubAreasResponse.SubArea>(mContext, android.R.layout.simple_spinner_dropdown_item, mSubAreas);
        binding.spSubArea.setAdapter(subAreaAdapter);

        ArrayList<SampleSearchModel> subAreaSearchList = new ArrayList<SampleSearchModel>();
        for (int i = 0; i < mSubAreas.size(); i++) {
            GetSubAreasResponse.SubArea area = mSubAreas.get(i);
            String name = area.getName();
            int id = area.getID();
            subAreaSearchList.add(new SampleSearchModel(name, id));

        }

        binding.viewSubArea.setVisibility(View.VISIBLE);

        binding.spSubArea.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mSubAreas.clear();
                    SimpleSearchDialogCompat temp = new SimpleSearchDialogCompat(getActivity(), "Search...", "What are you looking for...?", null, subAreaSearchList, new SearchResultListener<SampleSearchModel>() {
                        @Override
                        public void onSelected(BaseSearchDialogCompat dialog, SampleSearchModel item, int position) {


                            mSubAreas.add(new GetSubAreasResponse().new SubArea(item.getID(), item.getTitle()));
                            subAreaAdapter = new ArrayAdapter<GetSubAreasResponse.SubArea>(mContext, android.R.layout.simple_spinner_dropdown_item, mSubAreas);
                            binding.spSubArea.setAdapter(subAreaAdapter);
                            binding.spSubArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                                    ((TextView) view).setTextColor(getResources().getColorStateList(R.color.black));
                                    TextView selectedText = (TextView) parent.getChildAt(0);
                                    if (selectedText != null) {
                                        selectedText.setTextColor(Color.BLACK);
                                    } else {
                                        selectedText.setTextColor(Color.BLACK);

                                    }
                                    subAreaID = mSubAreas.get(position).getID();
                                    if (subAreaID != 0) {
                                        callGetBookSellerService(cityId, String.valueOf(mArrAreas.get(position).getID()));
                                    }
                                    rlShowSellers.setVisibility(View.GONE);
                                    mRvSellers.setVisibility(View.GONE);
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


   /*     mSpnArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });*/
    }

    private void callGetSubAreas() {
        mDialog.show();
        mDialog.setMessage("Fetching Sub-Areas please wait...");
        mService = RestClient.getInstance(mContext);
        Call<GetSubAreasResponse> userObject = mService.geSubAreas(areaID);
        RestCallbackObject callbackObject = new RestCallbackObject(getActivity(), this, RequestCode.GET_SUB_AREAS_REQUEST_CODE).showProgress(true, 0).dontHideProgress(false);
        userObject.enqueue(callbackObject);
    }

    private void callGetCityService(int regionId, int soID) {
        try{
            mDialog.show();
            mDialog.setMessage("Fetching cities please wait...");
            mService = RestClient.getInstance(mContext);
            Call<GetCitiesResponse> userObject = mService.getCities(regionId, soID);
            RestCallbackObject callbackObject = new RestCallbackObject(getActivity(), this, RequestCode.GET_CITIES_REQUEST_CODE).showProgress(true, 0).dontHideProgress(false);
            userObject.enqueue(callbackObject);
        }catch (Exception e){
            Toast.makeText(mContext,"Failed to load Cities.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showEditDialog(String title) {
        FragmentManager fm = getChildFragmentManager();
        AreaRegistrationDialogFrag editNameDialogFragment = AreaRegistrationDialogFrag.Companion.newInstance(title, mContext, this);
        editNameDialogFragment.show(fm, "fragment_edit_name");
    }
    private void showSubAreaEditDialog(String title) {
        FragmentManager fm = getChildFragmentManager();
        SubAreaRegistrationDialog editNameDialogFragment = SubAreaRegistrationDialog.Companion.newInstance(title, mContext, this);
        editNameDialogFragment.show(fm, "fragment_edit_name");
    }
    private void callGetAreaService(int cityId) {
        this.cityId = cityId;
        mDialog.show();
        mDialog.setMessage("Fetching areas please wait...");
        mService = RestClient.getInstance(mContext);
        Call<GetAreasResponse> userObject = mService.getAreas(String.valueOf(cityId), mLoginResponse.getData().getSOID());
        RestCallbackObject callbackObject = new RestCallbackObject(getActivity(), this, RequestCode.GET_AREA_REQUEST_CODE).showProgress(true, 0).dontHideProgress(false);
        userObject.enqueue(callbackObject);
    }


    private void callGetAreaService(int cityId, int zoneID) {
        this.cityId = cityId;
        mDialog.show();
        mDialog.setMessage("Fetching areas please wait...");
        mService = RestClient.getInstance(mContext);
        Call<GetAreasResponse> userObject = mService.getAreas(String.valueOf(cityId), mLoginResponse.getData().getSOID(), zoneID);
        RestCallbackObject callbackObject = new RestCallbackObject(getActivity(), this, RequestCode.GET_AREA_REQUEST_CODE).showProgress(true, 0).dontHideProgress(false);
        userObject.enqueue(callbackObject);
    }

    private void callGetBookSellerService(int cityId, String areaId) {
        this.cityId = cityId;
        mDialog.show();
        mDialog.setMessage("Fetching Selllers please wait...");
        mService = RestClient.getInstance(mContext);
        Call<GetBookSellerResponse> userObject = mService.getSellers(String.valueOf(cityId), areaId);
        RestCallbackObject callbackObject = new RestCallbackObject(getActivity(), this, RequestCode.GET_AREA_SELLERS).showProgress(true, 0).dontHideProgress(false);
        userObject.enqueue(callbackObject);

    }

    private void setEduSysSpinner() {
        mArrEduSys = new ArrayList<>();
        mArrEduSys.add("Select Education System");
        mArrEduSys.add("Primary");
        mArrEduSys.add("Middle");
        mArrEduSys.add("Matric");
        adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, mArrEduSys);
        mSpnEduSys.setAdapter(adapter);
    }

    private void setFeeStructSpinner() {
        mArrFeeStruct = mLoginResponse.getData().getFee();
        Fee fee = new Fee(0, "Select Fee Structure*");
        mArrFeeStruct.add(0, fee);
        ArrayAdapter<Fee> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, mArrFeeStruct);
        mSpnFeeStruct.setAdapter(adapter);
    }

    private void setSyllSelectionDateSpinner() {
        mArrSyllSelectionDate = new ArrayList<>();
        mArrSyllSelectionDate.add("Select Review Month*");
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
        adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, mArrSyllSelectionDate);
        mSpnSyllSelectionDate.setAdapter(adapter);
    }

    private void setSessionDateSpinner() {
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
        adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, mArrSessionDate);
        mSpnSessionDate.setAdapter(adapter);
    }

    private void setBookShopSpinner() {
        mArrBookShop = new ArrayList<>();
        mArrBookShop.add("Select Book Shop");
        mArrBookShop.add("Yes");
        mArrBookShop.add("No");
        adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, mArrBookShop);
        mSpnBookShop.setAdapter(adapter);
    }

    public void openCameraStart() {
        RuntimePermissionHandler.requestPermission(REQ_CODE_CAMERA_PERMISSION, getActivity(), mPermissionListener, RuntimePermissionUtils.CameraPermission);
    }

    public void openCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_INTENT);
    }

    public void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_INTENT);
    }

    public void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(mContext);
        pictureDialog.setTitle("Select Action");
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        pictureDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    openCamera();
                } else if (items[item].equals("Choose from Library")) {
                    openGallery();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        pictureDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAMERA_INTENT) {
            if (data != null && data.getExtras() != null && data.getExtras().get("data") != null) {
                bitmap = Bitmap.createScaledBitmap((Bitmap) data.getExtras().get("data"), 512, 512, true);
                mIvImage.setImageBitmap(bitmap);
                mIvImage.setVisibility(View.VISIBLE);
            }
        } else if (requestCode == GALLERY_INTENT) {
            if (data != null) {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mIvImage.setImageBitmap(bitmap);
            mIvImage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void isPermissionGranted(boolean result, int resultFor, String message) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        RuntimePermissionHandler.onRequestPermissionsResult(getActivity(), requestCode, permissions, grantResults);
    }

    private RuntimePermissionHandler.PermissionListener mPermissionListener = new RuntimePermissionHandler.PermissionListener() {
        @Override
        public void onRationale(final @NonNull RuntimePermissionHandler.PermissionRequest permissionRequest, final Activity target, final int requestCode, @NonNull final String[] permissions) {
            switch (requestCode) {
                case REQ_CODE_CAMERA_PERMISSION:
                    new AlertDialog.Builder(target).setPositiveButton(R.string.allow, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(@NonNull DialogInterface dialog, int which) {
                            permissionRequest.proceed(target, requestCode, permissions);
                            dialog.dismiss();
                        }
                    }).setNegativeButton(R.string.deny, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(@NonNull DialogInterface dialog, int which) {
                            permissionRequest.cancel(target, requestCode, permissions);
                            dialog.dismiss();
                        }
                    }).setCancelable(false).setMessage(R.string.camera_permission_rational).show();
                    break;
                case REQ_CODE_LOCATION_PERMISSION:
                    new AlertDialog.Builder(target).setPositiveButton(R.string.allow, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(@NonNull DialogInterface dialog, int which) {
                            permissionRequest.proceed(target, requestCode, permissions);
                            dialog.dismiss();
                        }
                    }).setNegativeButton(R.string.deny, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(@NonNull DialogInterface dialog, int which) {
                            permissionRequest.cancel(target, requestCode, permissions);
                            dialog.dismiss();
                        }
                    }).setCancelable(false).setMessage(R.string.location_permission_rational).show();
                    break;
            }
        }

        @Override
        public void onAllowed(int requestCode, @NonNull String[] permissions) {
            switch (requestCode) {
                case REQ_CODE_CAMERA_PERMISSION:
                    openCamera();
                    break;
                case REQ_CODE_LOCATION_PERMISSION:
                    mLocationService.getLocation();
                    //getLocation();
                    break;
            }
        }

        @Override
        public void onDenied(final @NonNull RuntimePermissionHandler.PermissionRequest permissionRequest, Activity target, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, RuntimePermissionHandler.DENIED_REASON deniedReason) {
            if (deniedReason == RuntimePermissionHandler.DENIED_REASON.USER) {
                switch (requestCode) {
                    case REQ_CODE_CAMERA_PERMISSION:
                        Toast.makeText(target, R.string.camera_permission_denied, Toast.LENGTH_SHORT).show();
                        break;
                    case REQ_CODE_LOCATION_PERMISSION:
                        Toast.makeText(target, R.string.location_permission_denied, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }

        @Override
        public void onNeverAsk(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            switch (requestCode) {
                case REQ_CODE_CAMERA_PERMISSION:
                    new AlertDialog.Builder(mContext).setPositiveButton(R.string.allow, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(@NonNull DialogInterface dialog, int which) {
                            RuntimePermissionUtils.openAppSettings(mContext);
                        }
                    }).setNegativeButton(R.string.deny, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(@NonNull DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setCancelable(false).setMessage(R.string.camera_pemission_never_ask).show();
                    break;
                case REQ_CODE_LOCATION_PERMISSION:
                    new AlertDialog.Builder(mContext).setPositiveButton(R.string.allow, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(@NonNull DialogInterface dialog, int which) {
                            RuntimePermissionUtils.openAppSettings(mContext);
                        }
                    }).setNegativeButton(R.string.deny, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(@NonNull DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setCancelable(false).setMessage(R.string.location_permission_denied).show();
                    break;
            }
        }
    };

    private void setSchoolReq(SchoolRegRequest obj) {
//        obj.setRetailerID();
        obj.setShopOrSchoolName(mEtSchoolName.getText().toString());
        obj.setPrincipleName(mEtOwnerPrincipalName.getText().toString());
        obj.setContactNo(mEtContactNumber.getText().toString());
        obj.setPhone2(mEtCellNumber.getText().toString());
        obj.setEmail(mEtEmail.getText().toString());
        obj.setWhatsAppNo(mEtWhatsAppNo.getText().toString());
        obj.setCustomerType(customerType);
        if (mSpnRegion.getSelectedItemPosition() > 0)
            obj.setRegionId(mArrRegions.get(mSpnRegion.getSelectedItemPosition()).getID());
        if (mSpnCity.getSelectedItemPosition() >= 0)
            obj.setCityID(mArrCities.get(mSpnCity.getSelectedItemPosition()).getID());
        if (mSpnArea.getSelectedItemPosition() >= 0)
            obj.setAreaID(mArrAreas.get(mSpnArea.getSelectedItemPosition()).getID());
        else obj.setAreaID(1);

        if (binding.rbShop.isChecked()) {
            obj.setVendorType("Shop");
        } else {
            obj.setVendorType("School");
        }


        obj.setZoneID(zoneID);
        obj.setSubAreaID(subAreaID);
        obj.setCountryName(selectedCountry);
        obj.setExamBoard(selectedExamBoard);
        obj.setWebsite(etWebsite.getText().toString());
        obj.setWorkingPriority(mWorkingPriority.getSelectedItem().toString());
        obj.setFeeStructure(mArrFeeStruct.get(mSpnFeeStruct.getSelectedItemPosition()).getFeeStructID());
        obj.setCompititorInformation(mArrSyllabusItemsSelected);
        obj.setShopsRelatedToRetailer(mArrSelectedBookSellers);
        obj.setLattitude(DashboardActivity.mLocationService.getLatitude());
        obj.setLongitude(DashboardActivity.mLocationService.getLongitude());
        obj.setAddress(mEtAddress.getText().toString());
        obj.setToken(mLoginResponse.getData().getToken());
        obj.setSalesOfficerID(mLoginResponse.getData().getSOID());
        obj.setContactPerson(mEtContactPerson.getText().toString());
        obj.setContactPersonCellNo(mEtOtherPersonNumber.getText().toString());
        obj.setEducationSystem(mSpnEduSys.getSelectedItem().toString());
        obj.setNoOfBranches(mEtBranches.getText().toString().length() > 0 ? Integer.parseInt(mEtBranches.getText().toString()) : 0);
        obj.setStudentStrength(mEtStudentStrength.getText().toString().length() > 0 ? Integer.parseInt(mEtStudentStrength.getText().toString()) : 0);
        obj.setNoOfTeachers(mEtTeacherStrength.getText().toString().length() > 0 ? Integer.parseInt(mEtTeacherStrength.getText().toString()) : 0);
        obj.setSessionStart(mSpnSessionDate.getSelectedItem().toString());
        obj.setCurrentSyllabus(1);
        obj.setSampleMonth(mSpnSyllSelectionDate.getSelectedItem().toString());
        obj.setBookShop((mSpnBookShop.getSelectedItem().toString().equalsIgnoreCase("Yes")) ? true : false);
        obj.setRemarks(mEtRemarks.getText().toString());
        obj.setDistrictID(districtID);
        obj.setTehsilID(tehsilID);
        obj.setPicture1(getStringImage(bitmap));

    }

    public String getStringImage(Bitmap bmp) {
        if (bmp == null) {
            return "";
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private boolean validateForm() {
        if (mSchoolRegReq.getShopOrSchoolName().trim().length() <= 0) {
            showToast("School name");
            mEtSchoolName.setError("This Field Cannot be Blank");
            return false;
        } else if (mSchoolRegReq.getPrincipleName().trim().length() <= 0) {
            showToast("Principal name");
            focusOnView(mEtOwnerPrincipalName);
            mEtOwnerPrincipalName.setError("This Field Cannot be Blank");
            return false;
        } else if (mSpnRegion.getSelectedItemPosition() <= 0) {
            showToast("Region");
            TextView errorTextForRegion = (TextView) mSpnRegion.getSelectedView();
            errorTextForRegion.setError("Region Cannot Be Empty");
            errorTextForRegion.setTextColor(Color.RED);
            errorTextForRegion.setText("Region Cannot Be Empty");
            focusOnView(mSpnRegion);
            return false;
        } else if (cityId == -1 || cityId == 0) {
            showToast("City");
//            TextView errorTextForArea = (TextView) mSpnCity.getSelectedView();
//            errorTextForArea.setError("City Cannot Be Empty");
//            errorTextForArea.setTextColor(Color.RED);
//            errorTextForArea.setText("City Cannot be Empty");
            return false;
        } else if (isZoneFormat&&(zoneID == -1 || zoneID == 0)) {
            showToast("Zone");
//            TextView errorTextForArea = (TextView) binding.spZone.getSelectedView();
//            errorTextForArea.setError("Zone Cannot Be Empty");
//            errorTextForArea.setTextColor(Color.RED);
//            errorTextForArea.setText("Zone Cannot be Empty");
            return false;
        } else if (isZoneFormat&&(subAreaID == -1 || subAreaID == 0)) {
            showToast("Sub Area");
//            TextView errorTextForArea = (TextView) binding.spSubArea.getSelectedView();
//            errorTextForArea.setError("Sub Area Cannot Be Empty");
//            errorTextForArea.setTextColor(Color.RED);
//            errorTextForArea.setText("Sub Area Cannot be Empty");
            return false;
        } else if (areaID <= 0) {
            showToast("Area");

//            TextView errorTextForArea = (TextView) mSpnArea.getSelectedView();
//            errorTextForArea.setError("Area Cannot Be Empty");
//            errorTextForArea.setTextColor(Color.RED);
//            errorTextForArea.setText("Area Cannot be Empty");
            focusOnView(mSpnArea);

            return false;
//        } else if (mSchoolRegReq.getContactNo().length() <= 0) {
//            mEtContactNumber.setError("This Field Cannot be Blank");
//            showToast("Contact no.");
//            focusOnView(mEtContactNumber);
//            return false;
//        } else if (mSpnFeeStruct.getSelectedItemPosition() <= 0) {
//            ((TextView) mSpnFeeStruct.getSelectedView()).setError("Fee Structure Empty");
//            showToast("Fee Structure");
//            focusOnView(mSpnFeeStruct);
//            return false;
//        } else if (mSpnSyllSelectionDate.getSelectedItemPosition() <= 0) {
//            ((TextView) mSpnSyllSelectionDate.getSelectedView()).setError("Syllabus Selection Empty");
//            showToast("Syllabus Selection Date");
//            focusOnView(mSpnSyllSelectionDate);
//            return false;
//        } else if (mSpnSessionDate.getSelectedItemPosition() <= 0) {
//            ((TextView) mSpnSessionDate.getSelectedView()).setError("Session Date Cannot be Empty");
//            showToast("Session Start Date");
//            focusOnView(mSpnSessionDate);
//            return false;
        } else if (DashboardActivity.mLocationService.getLatitude() == 0.0) {
            DashboardActivity.mLocationService.getLocation();
            showToast("Can't get your Location.");
            return false;
        }
        return true;
    }

    private void showConfirmationDialouge() {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.confirmation_popup);

        // set the custom dialog components - text and button
        TextView title = (TextView) dialog.findViewById(R.id.txtDiaTitle);
        TextView message = (TextView) dialog.findViewById(R.id.txtDiaMsg);
        title.setText("Registration");
        message.setText("Successfuly Registered");
        Button dialogButton = (Button) dialog.findViewById(R.id.btnOk);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getActivity().finish();
            }
        });
        dialog.show();
    }

    private final void focusOnView(final View view) {
        scrlView.post(new Runnable() {
            @Override
            public void run() {
                scrlView.scrollTo(0, view.getBottom());
            }
        });
    }


    private void showToast(String msg) {
        Toast.makeText(mContext, msg + " is required", Toast.LENGTH_SHORT).show();
    }

    private void callRegService() {
        mDialog.show();
        mDialog.setMessage("Submitting form, please wait...");
        mService = RestClient.getInstance(mContext);
        Call<ServerResponse> userObject = mService.regSchool(mSchoolRegReq);
        RestCallbackObject callbackObject = new RestCallbackObject(getActivity(), this, RequestCode.SCHOOL_REG_REQUEST_CODE).showProgress(true, 0).dontHideProgress(false);
        userObject.enqueue(callbackObject);
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

    @Override
    public void onFailure(String error, RequestCode requestCode) {
        if (requestCode == RequestCode.SCHOOL_REG_REQUEST_CODE) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
            Log.e("Result", "Failed");
            Toast.makeText(mContext, "Failed-- " + error, Toast.LENGTH_SHORT).show();
        } else if (requestCode == RequestCode.GET_CITIES_REQUEST_CODE) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
            Log.e("Result", "Failed");
            Toast.makeText(mContext, "Failed-- " + error, Toast.LENGTH_SHORT).show();
        } else if (requestCode == RequestCode.GET_AREA_REQUEST_CODE) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }

            Log.e("Result", "Failed");
            Toast.makeText(mContext, "Failed-- " + error, Toast.LENGTH_SHORT).show();
        } else if (requestCode == RequestCode.GET_SUB_AREAS_REQUEST_CODE) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
            Log.e("Result", "Failed");
            Toast.makeText(mContext, "Failed-- " + error, Toast.LENGTH_SHORT).show();
        } else if (requestCode == RequestCode.GET_ZONES_REQUEST_CODE) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
            binding.viewZone.setVisibility(View.GONE);
            binding.viewArea.setVisibility(View.GONE);
            binding.viewSubArea.setVisibility(View.GONE);

            Log.e("Result", "Failed");
            Toast.makeText(mContext, "Failed-- " + error, Toast.LENGTH_SHORT).show();
        } else if (requestCode == RequestCode.GET_AREA_SELLERS) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
            Log.e("Result", "Failed");
            Toast.makeText(mContext, "No Sellers Associated... ", Toast.LENGTH_SHORT).show();
        } else if (requestCode == RequestCode.GET_DISTRCIST) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
            Log.e("Result", "Failed");
        } else if (requestCode == RequestCode.GET_TEHSILS) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
            Log.e("Result", "Failed");
        }
    }

    @Override
    public void onSuccess(Object object, RequestCode requestCode) {
        if (requestCode == RequestCode.SCHOOL_REG_REQUEST_CODE) {
            ServerResponse serverResponse = (ServerResponse) object;
            if (mDialog.isShowing()) mDialog.dismiss();
            Log.e("Result", "Success");
            if (serverResponse.getResultType() == ServerCodes.ServerResultType.SERVER_RESULT_SUCCESS) {
                Toast.makeText(mContext, "Form Successfully submitted.", Toast.LENGTH_LONG).show();
                showConfirmationDialouge();
            } else {
                Toast.makeText(mContext, serverResponse.getMessage(), Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
        } else if (requestCode == RequestCode.GET_CITIES_REQUEST_CODE) {
            if (mDialog.isShowing()) {
                mDialog.hide();
            }

            mCitiesResponse = (GetCitiesResponse) object;
            if (mCitiesResponse.getCities() != null && !mCitiesResponse.getCities().isEmpty()) {
                setCitySpinner(mCitiesResponse);
            } else {
                Toast.makeText(mContext, "There are no cities associated", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == RequestCode.GET_AREA_REQUEST_CODE) {
            mAreaResponse = (GetAreasResponse) object;
            if (mDialog.isShowing()) {
                mDialog.hide();
            }
            //testing
            if (mAreaResponse.getAreas().size() <= 0) {
                binding.viewArea.setVisibility(View.GONE);
                binding.viewSubArea.setVisibility(View.GONE);
                Toast.makeText(mContext, "There are no areas associated", Toast.LENGTH_SHORT).show();
            } else {
                binding.viewArea.setVisibility(View.VISIBLE);
                binding.spArea.setVisibility(View.VISIBLE);
                setAreaSpinner(mAreaResponse);
            }
        } else if (requestCode == RequestCode.GET_SUB_AREAS_REQUEST_CODE) {
            mSubAreasResponse = (GetSubAreasResponse) object;
            if (mDialog.isShowing()) {
                mDialog.hide();
            }
            //testing
            if (mSubAreasResponse.getSubAreas().size() <= 0) {
                binding.viewSubArea.setVisibility(View.GONE);
                Toast.makeText(mContext, "There are no sub areas associated", Toast.LENGTH_SHORT).show();
            } else {
                binding.viewSubArea.setVisibility(View.VISIBLE);
                setSubAreasSpinner(mSubAreasResponse);
            }
        } else if (requestCode == RequestCode.GET_ZONES_REQUEST_CODE) {
            mZonesResponse = (GetZonesResponse) object;
            if (mDialog.isShowing()) {
                mDialog.hide();
            }
            //testing
            if (mZonesResponse.getZones().size() <= 0) {
                Toast.makeText(mContext, "There are no Zones associated", Toast.LENGTH_SHORT).show();
                binding.viewZone.setVisibility(View.GONE);
                binding.viewArea.setVisibility(View.GONE);
                binding.viewSubArea.setVisibility(View.GONE);
            } else {
                binding.viewZone.setVisibility(View.VISIBLE);
                setZoneSpinner(mZonesResponse);
            }
        } else if (requestCode == RequestCode.GET_AREA_SELLERS) {
            mBookSellersResponse = (GetBookSellerResponse) object;
            if (mDialog.isShowing()) {
                mDialog.hide();
            }
            if (mBookSellersResponse.getBookSellers().size() <= 0) {
                Toast.makeText(mContext, "There are No Sellers Associated", Toast.LENGTH_SHORT).show();
            } else {
                mArrBookSellers = mBookSellersResponse.getBookSellers();

                rlShowSellers.setVisibility(View.VISIBLE);
                mRvSellers.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setZoneSpinner(GetZonesResponse mZonesResponse) {
        zoneID = -1;
        mArrZones =  mZonesResponse.getZones();
        GetZonesResponse.Zone emptyCity =  new GetZonesResponse().new Zone(0, "Select Zone*");
        mArrZones.add(0, emptyCity);

        ArrayList<SampleSearchModel> zoneSearchList = new ArrayList<SampleSearchModel>();
        for (int i = 0; i < mArrZones.size(); i++) {
            GetZonesResponse.Zone zone = mArrZones.get(i);
            String name = zone.getName();
            int id = zone.getID();
            zoneSearchList.add(new SampleSearchModel(name, id));
        }
        zoneAdapter = new ArrayAdapter<GetZonesResponse.Zone>(mContext, android.R.layout.simple_spinner_dropdown_item, mArrZones);
        binding.spZone.setAdapter(zoneAdapter);

        binding.spZone.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mArrZones.clear();
                    SimpleSearchDialogCompat temp = new SimpleSearchDialogCompat(getActivity(), "Search...", "What are you looking for...?", null, zoneSearchList, new SearchResultListener<SampleSearchModel>() {
                        @Override
                        public void onSelected(BaseSearchDialogCompat dialog, SampleSearchModel item, int position) {


                            mArrZones.add(new GetZonesResponse().new Zone(item.getID(), item.getTitle()));
                            zoneAdapter = new ArrayAdapter<GetZonesResponse.Zone>(mContext, android.R.layout.simple_spinner_dropdown_item, mArrZones);
                            binding.spZone.setAdapter(zoneAdapter);

                            binding.spZone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                                    ((TextView) view).setTextColor(getResources().getColorStateList(R.color.black));
                                    TextView selectedText = (TextView) parent.getChildAt(0);
                                    if (selectedText != null) {
                                        selectedText.setTextColor(Color.BLACK);
                                    } else {
                                        selectedText.setTextColor(Color.BLACK);

                                    }

                                    zoneID = mArrZones.get(position).getID();
                                    if (zoneID != 0) {
                                        callGetAreaService(mArrCities.get(position).getID(), zoneID);
                                    }
                                    binding.viewArea.setVisibility(View.GONE);
                                    binding.viewSubArea.setVisibility(View.GONE);

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


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                mSchoolRegReq = new SchoolRegRequest();
                setSchoolReq(mSchoolRegReq);
                if (Utility.isNetworkAvailable(mContext)) {
                    mTvLat.setText(Double.toString(DashboardActivity.mLocationService.getLatitude()));
                    mTvLong.setText(Double.toString(DashboardActivity.mLocationService.getLongitude()));
                    if (validateForm()) callRegService();
                } else
                    Toast.makeText(mContext, getString(R.string.str_no_internet), Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_capture:
                //openCameraStart();
                ((FragmentShownActivity) getActivity()).openCamera(this);
                break;
            case R.id.iv_location:
                mTvLat.setText(Double.toString(DashboardActivity.mLocationService.getLatitude()));
                mTvLong.setText(Double.toString(DashboardActivity.mLocationService.getLongitude()));
                break;
            case R.id.rl_select_seller:
            case R.id.ib_show_items:
                if (mArrSyllabusItems.size() > 0) {
                    if (mRvItems.getVisibility() == View.GONE) {
                        mRvItems.setVisibility(View.VISIBLE);
                        ibShowItems.setBackgroundResource(R.drawable.ic_expand_less_black_24dp);
                        mLayoutManager = new LinearLayoutManager(mContext);
                        mRvItems.setLayoutManager(mLayoutManager);
                        mAdapter = new SyllabusItemsAdapter(mArrSyllabusItems, this);
                        mRvItems.setAdapter(mAdapter);
                    } else {
                        mRvItems.setVisibility(View.GONE);
                        ibShowItems.setBackgroundResource(R.drawable.ic_expand_more_black_24dp);
                    }
                } else Toast.makeText(mContext, "No item found", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ib_show_seller_items:
                if (mArrBookSellers.size() > 0) {
                    if (mRvSellers.getVisibility() == View.GONE) {
                        mRvSellers.setVisibility(View.VISIBLE);
                        ibshowSellers.setBackgroundResource(R.drawable.ic_expand_less_black_24dp);
                        sellerLayoutManager = new LinearLayoutManager(mContext);
                        mRvSellers.setLayoutManager(sellerLayoutManager);
                        sellerAdapter = new BookSellerItemsAdapter(mArrBookSellers, this);
                        mRvSellers.setAdapter(sellerAdapter);
                    } else {
                        mRvSellers.setVisibility(View.GONE);
                        ibshowSellers.setBackgroundResource(R.drawable.ic_expand_more_black_24dp);
                    }
                } else {
                    Toast.makeText(mContext, "No item found", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.iv_add_area:
                showEditDialog("Add New Area");
                break;
            case R.id.iv_add_sub_area:
                showSubAreaEditDialog("Add New Sub Area");
                break;


        }
    }

    @Override
    public void areaAdded() {

        binding.viewSubArea.setVisibility(View.GONE);
        binding.viewArea.setVisibility(View.GONE);
        callGetAreaService(cityId);
    }

    @Override
    public void subAreaAdded() {
        binding.viewSubArea.setVisibility(View.GONE);
        callGetSubAreas();
    }


}
