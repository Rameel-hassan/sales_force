package com.app.salesforce.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.salesforce.R;
import com.app.salesforce.adapters.AddRemoveItem;
import com.app.salesforce.adapters.ClassRangeAdapter;
import com.app.salesforce.adapters.InterestedBooksAdapter;
import com.app.salesforce.adapters.ItemsAdapter;
import com.app.salesforce.adapters.SelectedClassAdapter;
import com.app.salesforce.adapters.SelectedItemsAdapter;
import com.app.salesforce.adapters.SubjectsAndClassesAdapter;
import com.app.salesforce.base.SFApplication;
import com.app.salesforce.model.CompetitorDetail;
import com.app.salesforce.model.JobItemsDetails;
import com.app.salesforce.model.Publisher;
import com.app.salesforce.model.SampleSearchModel;
import com.app.salesforce.model.SelectedClassItem;
import com.app.salesforce.model.SelectedItemsModel;
import com.app.salesforce.model.SelectedVisitItems;
import com.app.salesforce.model.SubjectAndClassesModel;
import com.app.salesforce.network.ApiService;
import com.app.salesforce.network.RequestCode;
import com.app.salesforce.network.RestCallbackObject;
import com.app.salesforce.network.RestClient;
import com.app.salesforce.network.RestService;
import com.app.salesforce.network.ServerCodes;
import com.app.salesforce.network.ServerConnectListenerObject;
import com.app.salesforce.permissions.RuntimePermissionHandler;
import com.app.salesforce.permissions.RuntimePermissionUtils;
import com.app.salesforce.request.VisitReportRequest;
import com.app.salesforce.response.ActivityPurpose;
import com.app.salesforce.response.Clas;
import com.app.salesforce.response.Competitor;
import com.app.salesforce.response.CustomersRelatedtoSO;
import com.app.salesforce.response.GetItemsResponse;
import com.app.salesforce.response.GetRetailerSampling;
import com.app.salesforce.response.GetSchoolInfo;
import com.app.salesforce.response.GetServerResponse;
import com.app.salesforce.response.GetSubCatResponse;
import com.app.salesforce.response.GetSubCategoryAResponse;
import com.app.salesforce.response.MainCatg;
import com.app.salesforce.response.Region;
import com.app.salesforce.response.SalesOfficer;
import com.app.salesforce.response.ServerResponse;
import com.app.salesforce.response.Sery;
import com.app.salesforce.response.Subjects;
import com.app.salesforce.utilities.AppBundles;
import com.app.salesforce.utilities.AppKeys;
import com.app.salesforce.utilities.Utility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import retrofit2.Call;

public class ReportsActivity extends AppCompatActivity implements ServerConnectListenerObject, View.OnClickListener, AddRemoveItem, AddRemoveItem.AddRemoveSchoolItems, AddRemoveItem.AddRemovePublishers {

    //region
    private final int REQ_CODE_CAMERA_PERMISSION = 1001;
    private static final int CAMERA_INTENT = 30;
    private static final int GALLERY_INTENT = 40;
    //    private Spinner mSpnActivityType;
    private RadioButton rbIndivisual, rbCombined;
    private Spinner mSpnPurpose;
    private RelativeLayout rlSalesOfficer;
    private Spinner mSpnSalesOfficer;
    private Spinner selectSeries;
    private Spinner mSpnMainCat;
    private Spinner getmSpnSubCatA;
    private Spinner mSpnRegion;
    private Spinner mSpnRegionCustomers;
    private Spinner mSpnSampleMonth;
    private Spinner mSpnSessionMonth;
    private Spinner mSpnPosOrNeg;
    private LinearLayout ll_header;
    private LinearLayout ll_sub_class;

    private CheckBox selectAllOptionsCheckBox;
    private Boolean currentInterestedBooksOpen = false;
    private Boolean currentPublisherOpen = false;
    private Boolean currentClassRangeOpen = false;
    private String seriesNameString = "";
    private ArrayList<String> mArrActType;
    private ArrayAdapter adapter;
    ArrayList<SampleSearchModel> seriesSampleSearchModels;
    ArrayList<Subjects> subjectsRelatedToSeries;
    private ArrayAdapter<SalesOfficer> salesOfiicerAdapter;
    private List<GetRetailerSampling.SampleItems> mArrSamplingItems;
    private List<Subjects> subjectSelectArray;
    private ArrayList<String> mArrpriority;
    private ArrayList<String> mArrClosingDate;
    private ArrayList<String> mArrSubject;
    private List<Sery> arrInterestedBooksList;
    private List<Clas> arrClassList;
    private List<Sery> arrSeriesList;
    private List<Competitor> arrPublishers;
    private ArrayList<String> arrPublishersToSend = new ArrayList<>();
    //    private List<Competitor> arrSelectedPublishers;
    private List<Sery> arrSelectedInterestedBooks;
    private List<Integer> CompetitorIDToSend;
    private List<Integer> SeriesIDToSend;
    ArrayList<SampleSearchModel> sampleSearchModels;
    List<ActivityPurpose> lstActivityPurposes;
    private List<SalesOfficer> mArrEmployee;
    private List<String> mArrSessionDate;
    private List<String> mArrSyllSelectionDate;
    TextView mEtShopName, mTvSchoolName, mTvSelectItems;
    TextView mTvSelectNextVisitDate, mTvSelectedItems;
    Button btnSubmit, btnAddPublishers;
    EditText etActivityDetail, etCntctPersonName, etPhoneNo, etPersonDesignation, etPersonIsMember, etExpectedQuantity, etPublisher;
    ImageView btnCamera, mIvSampleReceipt;
    ImageButton btnAddPublisher;
    LinearLayout mLlSampling, mLlOrderDetail;
    //private RelativeLayout  classRangeItem, interestedBooksItem;
    private Sery selectedSeries;
    private RelativeLayout rlSelectItems;
    private ImageButton ibShowItems;
    private RecyclerView rvSubjClas;
    private RecyclerView mRvItems, listOfInterestedBooks,/*listOfPublishers,*//* , *//*//listOfClasses*/
            mRvSelectedClasses;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.Adapter mSelectedItemsAdapter;
    private RecyclerView.Adapter mSelectedClassAdapter;
    //    private PublishersListAdapter mPublisherItemAdapter;
    private InterestedBooksAdapter mInterestedBooksAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRvSelectedItems;
    private ClassRangeAdapter classRangeAdapter;
    private String shopName, schoolShopName;
    private VisitReportRequest visitReportRequest;
    private ProgressDialog mDialog;
    private ApiService mService;
    private Context mContext;
    private GetServerResponse mLoginResponse;
    private GetSubCatResponse mGetSubCatResponse;
    private GetSubCategoryAResponse mGetSubCatAResponse;
    private GetItemsResponse mGetItemsResponse;
    //private Sery selectedSeries = null;
    Calendar myCalendar = Calendar.getInstance();
    Calendar my_prev_Calendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener date, previousDate;
    private Bitmap bitmap;
    private List<MainCatg> mArrMainCat;
    private List<Region> mArrRegion;
    private int JobID = 0;
    private List<CustomersRelatedtoSO> mArrRegionCustomers;
    private List<GetSchoolInfo> mSchoolInfos;
    private List<GetSubCatResponse.SubCategory> mArrSubCat;
    private List<GetSubCategoryAResponse.SubCategoryA> mArrSubCatA;
    private List<GetItemsResponse.Item> mArrItems;
    private List<SelectedClassItem> mArrSelectedClassItem;
    private List<SelectedClassItem> mArrSelectedClassItemToSend = new ArrayList<>();
    private List<SelectedItemsModel> mArrSelectedItems;
    String personName = "", personPhone = "", personDesignation = "", activityDetail = "", nextVisitDate = "";
    private List<SelectedVisitItems> mArrSelectedJobItems;
    private List<JobItemsDetails> mArrSelectedJobDetials;
    private List<CompetitorDetail> mArrCompetitorDetail;
    private CustomersRelatedtoSO mCustomerObj;
    //RelativeLayout rlShowClassRange;
    private ArrayAdapter<Sery> seriesAdapter;
    private Spinner mSpnSubCat;
    private int Purposeposition = 0;
    private boolean _doubleBackToExitPressedOnce = false;
    private LinearLayout llMonths;
    private int otherSOID = 0;
    //private RelativeLayout currentPubItem;
    //endregion
    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;
    private String activityType;

    private RestService restService;
    private String vendorType;

    private LinearLayout llDesignation, llMemberSchool, llExpectedQuantity;
    private TextView tvTitleShopName;

    List<String> vendorTypes = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = this;
        vendorTypes.add("School");
        vendorTypes.add("Shop");
        parseBundle();


        initGui(SFApplication.getAppResources().getString(R.string.visit_report));
        Log.d("class_name", this.getClass().getSimpleName());
        arrPublishers = mLoginResponse.getData().getCompetitor();
        for (Competitor comp : arrPublishers) {
            arrPublishersToSend.add(comp.getCompetitorName());
        }
    }

    private void makeChangesAccordingToSelectedVendorType() {
        lstActivityPurposes = mLoginResponse.getData().getActivityPurpose();
        if (vendorTypes.get(0).equalsIgnoreCase(vendorType)) {//shool
            //don't need to do anything
            showTables();
            return;
        } else {//shop

            ArrayList<ActivityPurpose> temp = new ArrayList<ActivityPurpose>();
            for (ActivityPurpose purpose : lstActivityPurposes) {
                if (purpose.getName().equalsIgnoreCase("FollowUp")) {
                    temp.add(purpose);
                    break;
                }
            }
            hideTables();
            lstActivityPurposes = temp;

            etPersonDesignation.setVisibility(View.GONE);
            llMemberSchool.setVisibility(View.GONE);
            llExpectedQuantity.setVisibility(View.GONE);
            llDesignation.setVisibility(View.GONE);
            tvTitleShopName.setVisibility(View.GONE);
            mEtShopName.setVisibility(View.GONE);
        }

    }

    private void initGui(String title) {
        setTitle(title);
//        mSpnActivityType = findViewById(R.id.spn_activity_type);

        rbIndivisual = findViewById(R.id.rb_individual);
        rbCombined = findViewById(R.id.rb_combined);
        mSpnSalesOfficer = findViewById(R.id.sp_salesofficer);
        rlSalesOfficer = findViewById(R.id.rl_salesofficer);
        llDesignation = findViewById(R.id.ll_designation);
        llExpectedQuantity = findViewById(R.id.ll_expected_quantity);
        llMemberSchool = findViewById(R.id.ll_member_school);
        tvTitleShopName = findViewById(R.id.tv_shop_name_title);


        mSpnPurpose = findViewById(R.id.spn_region);
        selectSeries = findViewById(R.id.subject);
        mSpnPosOrNeg = findViewById(R.id.spinner_pos_or_neg);
        mSpnMainCat = findViewById(R.id.spn_main_cat);
        mSpnSubCat = findViewById(R.id.spn_sub_cat);
        getmSpnSubCatA = findViewById(R.id.spn_sub_cat_a);
        mSpnRegion = findViewById(R.id.spn_region);
        mSpnRegionCustomers = findViewById(R.id.spn_region_customers);
        rlSelectItems = findViewById(R.id.rl_select_items);
        ibShowItems = findViewById(R.id.ib_show_items);
        mRvItems = findViewById(R.id.rv_items);
        mRvSelectedClasses = findViewById(R.id.rcv_Selected_Class_Item);
        ll_header = findViewById(R.id.ll_table_head);
        ll_sub_class = findViewById(R.id.ll_table);

        //hideTables();


        mRvSelectedItems = findViewById(R.id.rv_selected_items);
        etCntctPersonName = findViewById(R.id.etContactPerson_reports_act);
        etExpectedQuantity = findViewById(R.id.etExpectedQuantity);
        etPersonDesignation = findViewById(R.id.etCntctPersonDesignation);
        etPersonIsMember = findViewById(R.id.etCntctIsMemberSchool);

        etPhoneNo = findViewById(R.id.etCntctPersonPhNo_reports_act);
        btnAddPublisher = findViewById(R.id.btnAddPublishers);
        etPublisher = findViewById(R.id.etPublishers);
        //mSpnItems = findViewById(R.id.spn_items);
        mTvSchoolName = findViewById(R.id.tv_school_name);
        mEtShopName = findViewById(R.id.et_shop_name);
        seriesSampleSearchModels = new ArrayList<>();
        mTvSelectNextVisitDate = findViewById(R.id.tv_next_visit_date);
        btnSubmit = findViewById(R.id.btn_submit);
        etActivityDetail = findViewById(R.id.et_activity_detail);
        btnCamera = findViewById(R.id.iv_capture);
        mIvSampleReceipt = findViewById(R.id.iv_image);
        mTvSelectItems = findViewById(R.id.tv_select_items);
        mLlSampling = findViewById(R.id.ll_sampling);
        mLlOrderDetail = findViewById(R.id.llOrderDetail);
        mTvSelectedItems = findViewById(R.id.tv_selected_items);
        //currentPubItem = findViewById(R.id.publisher_layout);
        //  classRangeItem = findViewById(R.id.range_layout);
        mSpnSampleMonth = findViewById(R.id.spn_sample_month);
        mSpnSessionMonth = findViewById(R.id.spn_session_month);

        selectAllOptionsCheckBox = findViewById(R.id.select_all_options);

//        listOfPublishers = findViewById(R.id.list_of_publishers);
        ////listOfClasses = findViewById(R.id.list_of_classes);
        arrSelectedInterestedBooks = new ArrayList<Sery>();
        arrClassList = new ArrayList<Clas>();
//        listOfInterestedBooks = findViewById(R.id.list_of_interested_books);
        mTvSchoolName.setText(shopName);
        llMonths = findViewById(R.id.llMonths);
        mDialog = new ProgressDialog(this);
        mDialog.setCancelable(false);
        btnCamera.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        mTvSelectNextVisitDate.setOnClickListener(this);
        ibShowItems.setOnClickListener(this);
        mTvSelectItems.setOnClickListener(this);
        mArrSelectedClassItem = new ArrayList<>();
        mArrItems = new ArrayList<>();
        //rlShowClassRange = findViewById(R.id.range_layout);
        mArrSelectedItems = new ArrayList<>();

        // arrSelectedPublishers = new ArrayList<Competitor>();
        mArrSelectedJobItems = new ArrayList<SelectedVisitItems>();
        CompetitorIDToSend = new ArrayList<Integer>();
        SeriesIDToSend = new ArrayList<Integer>();
        mArrSelectedJobDetials = new ArrayList<>();
        mArrCompetitorDetail = new ArrayList<>();
        arrPublishersToSend = new ArrayList<>();
        lstActivityPurposes = new ArrayList<>();


        rvSubjClas = findViewById(R.id.rv_subj_class);
        makeChangesAccordingToSelectedVendorType();

//        setActTypeSpinner();
        setPrioritySpinner();
        setCloseDateSpinner();
        setSyllSelectionDateSpinner();
        setSessionDateSpinner();
        setMainCatSpn();
        startDatePicker();//setRegionSpinner();
//        selectSubject();
        setSeriesListSpinner();
        arrClassList = mLoginResponse.getData().getClasss();
        //setSeriesSpinner();
        setPurposeSpinner();
        if (mCustomerObj != null) {
            callGetRetailerSampleItemsService(mCustomerObj.getID());
            //callGetPurpose(mCustomerObj.getID());
        }
        /*********************************************/
        if (mCustomerObj != null) {
            getSchoolsInfoAndShow(mCustomerObj.getID());
        }
        mArrEmployee = mLoginResponse.getData().getSalesOfficer();
        btnAddPublisher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedSeries == null) {
                    Toast.makeText(getApplicationContext(), "Select any subject first", Toast.LENGTH_SHORT).show();
                } else {
                    if (etPublisher.getText().toString().isEmpty() || etPublisher.getText().toString() == null) {
                        Toast.makeText(getApplicationContext(), "Add any Publisher First", Toast.LENGTH_SHORT).show();
                    } else {
                        btnAddPublisher.setEnabled(false);
                        arrPublishersToSend.add(etPublisher.getText().toString());
                        arrPublishers.add(new Competitor(etPublisher.getText().toString(), arrPublishers.size(), selectedSeries));
                        String competitor = etPublisher.getText().toString();
                        for (SelectedClassItem in : mArrSelectedClassItem) {
                            in.lstPublisher.add(new Publisher(competitor, false));
                        }

                        mSelectedClassAdapter.notifyDataSetChanged();

                        //List<String> items = Arrays.asList(etPublisher.getText().toString().split("\\s*,\\s*"));
//                        for (String i : items) {
//                            AddRemovePublishers(new Competitor(i, 0, selectedSeries), true);
//                        }
                        Toast.makeText(getApplicationContext(), "Publishers Added", Toast.LENGTH_SHORT).show();
                        etPublisher.setText("");
                        btnAddPublisher.setEnabled(true);
                    }
                }
            }
        });
       /* rlShowClassRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrClassList.size() > 0) {
                   // if (//listOfClasses.getVisibility() == View.GONE) {
                    //    //listOfClasses.setVisibility(View.VISIBLE);
                    //} else {
                      //  //listOfClasses.setVisibility(View.GONE);
                   // }
                    mLayoutManager = new LinearLayoutManager(v.getContext());
                    ////listOfClasses.setLayoutManager(mLayoutManager);
                    //classRangeAdapter = new ClassRangeAdapter(arrClassList, v.getContext(), ReportsActivity.this, selectedSeries, arrSelectedPublishers);
                    ////listOfClasses.setAdapter(classRangeAdapter);
                }
            }
        });*/


        rbIndivisual.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(

        ) {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //hide spinner
                if (isChecked) {
                    rlSalesOfficer.setVisibility(View.GONE);
                }

            }
        });

        rbCombined.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //show spinner woth sales officers names
                if (isChecked) {
                    rlSalesOfficer.setVisibility(View.VISIBLE);
                    setSalesOfficerSpinner();
                }
            }
        });

    }

    private void hideTables() {

        ll_header.setVisibility(View.GONE);
        ll_sub_class.setVisibility(View.GONE);
        mRvSelectedClasses.setVisibility(View.GONE);
    }

    private void showTables() {
        // ll_header.setVisibility(View.VISIBLE);
        //ll_sub_class.setVisibility(View.VISIBLE);
        //mRvSelectedClasses.setVisibility(View.VISIBLE);
    }

    private void setSalesOfficerSpinner() {
        salesOfiicerAdapter = new ArrayAdapter<SalesOfficer>(this, android.R.layout.simple_spinner_item, mArrEmployee);
        mSpnSalesOfficer.setAdapter(salesOfiicerAdapter);
//        if (mArrEmployee.size() == 1) {
        // mSpnSalesOfficer.setEnabled(false);
        otherSOID = mArrEmployee.get(0).getID();
        //   }
        mSpnSalesOfficer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                otherSOID = mArrEmployee.get(position).getID();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void startDatePicker() {
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };
        previousDate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                my_prev_Calendar.set(Calendar.YEAR, year);
                my_prev_Calendar.set(Calendar.MONTH, monthOfYear);
                my_prev_Calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        mTvSelectNextVisitDate.setText(sdf.format(myCalendar.getTime()));
        nextVisitDate = mTvSelectNextVisitDate.getText().toString();
    }

    private boolean validateSeries() {
        boolean check = true;
        if (mArrSelectedClassItem.size() > 0) {
            for (SelectedClassItem item : mArrSelectedClassItem) {
                if (item.getSeries() == null) {
                    check = false;
                    break;
                }
            }

        }
        if (!check) {
            Toast.makeText(mContext, "Select All the series First", Toast.LENGTH_SHORT).show();
        }
        return check;
    }

    private boolean validateForm() {
//        if (mSpnActivityType.getSelectedItemPosition() == 0) {
//            showToast("Activity type");
//            return false;
//        } else
        /*if (mSpnPurpose.getSelectedItemPosition() == 0) {
            showToast("Purpose of visit");
            return false;
        }*/
        schoolShopName = mEtShopName.getText().toString().trim();
//        if (schoolShopName.trim().length() <= 0 && mLoginResponse.getData().getIsShopNameInVisitForm()) {
//            showToast("Shop name");
//            return false;
//        } else
        if (etActivityDetail.getText().toString().trim().length() <= 0) {
            showToast("Comment");
            return false;
        } else if (Purposeposition == 0) {
            showToast("Activity Purpose");
            return false;
        }

        if (nextVisitDate.contains("Select") || nextVisitDate == "" || nextVisitDate.length() <= 0) {
            showToast("Next visit date");
            return false;
        } else if (mArrSelectedClassItem.size() <= 0) {
            if (lstActivityPurposes.get(Purposeposition).getID() == 2 || lstActivityPurposes.get(Purposeposition).getID() == 5) {
                return true;
            } else {
                Toast.makeText(mContext, "Cart Cannot be Empty", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            return true;
        }
    }

    private void parseBundle() {
        Paper.init(this);
        mLoginResponse = Paper.book().read(AppKeys.KEY_LOGIN_RESPONSE);
        if (getIntent() != null) {
            mCustomerObj = (CustomersRelatedtoSO) getIntent().getSerializableExtra(AppBundles.BUNDLE_CUSTOMER_OBJ);
            shopName = mCustomerObj.getShopName();
            activityType = getIntent().getStringExtra(AppBundles.BUNDLE_ORDER);
            vendorType = getIntent().getStringExtra(AppBundles.BUNDLE_VENDOR_TYPE);
        }
    }

    private void showToast(String msg) {
        Toast.makeText(mContext, msg + " is required", Toast.LENGTH_SHORT).show();
    }

    private void callRegService() {
        mDialog.show();
        mDialog.setMessage("Submitting your Activity, please wait...");
        btnSubmit.setEnabled(false);
        mService = RestClient.getInstance(this);
        Call<ServerResponse> userObject = mService.regActivity(visitReportRequest);
        RestCallbackObject callbackObject = new RestCallbackObject(this, this, RequestCode.VISIT_REPORT_ACTIVITY)
                .showProgress(true, 0).dontHideProgress(false);
        userObject.enqueue(callbackObject);
    }
//    private void setActTypeSpinner() {
//        mArrActType = new ArrayList<>();
//        mArrActType.add("Select Activity Type");
//        mArrActType.add("School Visit");
//        mArrActType.add("Call");
//        mArrActType.add("Other");
//        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mArrActType);
//        mSpnActivityType.setAdapter(adapter);
//        mSpnActivityType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (position == 2) {
//                    mArrpurpose.clear();
//                    mArrpurpose.add("Select Purpose of visit");
//                } else {
//                    mArrpurpose.clear();
//                    mArrpurpose.add("Select Purpose of visit");
//                    mArrpurpose.add("New Visit");
//                    mArrpurpose.add("FollowUp");
//                    mArrpurpose.add("Sampling");
//                    mArrpurpose.add("Ordering");
//                    mArrpurpose.add("Return Collection");
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//    }

    private void setSeriesListSpinner() {
        arrSeriesList = mLoginResponse.getData().getSeries();
        arrSeriesList.add(0, new Sery(0, "Select Series"));
        seriesAdapter = new ArrayAdapter<Sery>(this, android.R.layout.simple_spinner_dropdown_item, arrSeriesList);
        selectSeries.setAdapter(seriesAdapter);
        seriesSampleSearchModels.clear();
        for (int i = 1 ; i < arrSeriesList.size() ; i++) {
            Sery customer = arrSeriesList.get(i);
            String name = customer.getSeriesName();
            int id = customer.getID();
            seriesSampleSearchModels.add(new SampleSearchModel(name, id));
        }
        mDialog.dismiss();


        selectSeries.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    arrSeriesList.clear();
                    new SimpleSearchDialogCompat(ReportsActivity.this, "Search...",
                            "What are you looking for...?", null, seriesSampleSearchModels,
                            new SearchResultListener<SampleSearchModel>() {
                                @Override
                                public void onSelected(BaseSearchDialogCompat dialog,
                                                       SampleSearchModel item, int position) {
                                    arrSeriesList.add(new Sery(item.getID(), item.getTitle()));
                                    seriesAdapter = new ArrayAdapter<Sery>(ReportsActivity.this, android.R.layout.simple_spinner_dropdown_item, arrSeriesList);
                                    selectSeries.setAdapter(seriesAdapter);
                                    selectSeries.setVisibility(View.VISIBLE);
                                    selectSeries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            if (arrSeriesList.get(0).getID() != 0) {
                                                //currentPubItem.setVisibility(View.VISIBLE);
                                                //classRangeItem.setVisibility(View.VISIBLE);
                                                selectedSeries = arrSeriesList.get(position);
                                                btnAddPublisher.setEnabled(true);
                                                //interestedBooksItem.setVisibility(View.VISIBLE);
                                                // arrSelectedPublishers.clear();
//                                               listOfPublishers.setVisibility(View.GONE);
//                                                mLayoutManager = new LinearLayoutManager(view.getContext());
//                                                 listOfPublishers.setLayoutManager(mLayoutManager);
//                                                 mPublisherItemAdapter = new PublishersListAdapter(arrPublishers, view.getContext(), ReportsActivity.this, selectedSeries);
//                                                  listOfPublishers.setAdapter(mPublisherItemAdapter);
//                                                mPublisherItemAdapter.notifyDataSetChanged();
                                                //listOfClasses.setVisibility(View.GONE);
                                                mLayoutManager = new LinearLayoutManager(view.getContext());
                                                //listOfClasses.setLayoutManager(mLayoutManager);
                                                //  classRangeAdapter = new ClassRangeAdapter(arrClassList, view.getContext(), ReportsActivity.this, selectedSeries, arrSelectedPublishers);
                                                //listOfClasses.setAdapter(classRangeAdapter);

                                                loadBooks(selectedSeries.getID());
                                                //classRangeAdapter.notifyDataSetChanged();
                                            } else {
                                                // currentPubItem.setVisibility(View.GONE);
                                                //classRangeItem.setVisibility(View.GONE);
                                                //interestedBooksItem.setVisibility(View.GONE);
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

    private void loadBooks(int id) {
        mDialog.show();
        mDialog.setMessage("Getting Books, please wait...");
        mService = RestClient.getInstance(this);
        Call<GetServerResponse> userObject = mService.getSubjectsSeriesWise(id);
        RestCallbackObject callbackObject = new RestCallbackObject((Activity)
                mContext, this, RequestCode.GET_SUBJECTS_SERIES_WISE)
                .showProgress(true, 0)
                .dontHideProgress(false);
        userObject.enqueue(callbackObject);
    }

    private void showError(String message) {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Log.e("msg", message.toString());
    }

    private void showResult(GetServerResponse response) {


        Log.e("Response", response.toString());
        Log.e("ALL DATA", response.getData().toString());
        Log.d("ehtesham", response.getData().getClasss().toString());

        // Remove this
//        for (i in response.Data.Class) {
//            Log.d("ehtesham", "" + i.ClassName)
//        }
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        if (response.getResultType() == ServerCodes.ServerResultType.SERVER_RESULT_SUCCESS) {

            Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("TAG", "showResult: " + response.getMessage());
        }
    }

    /*private void setSeriesSpinner() {
        arrInterestedBooksList = mLoginResponse.getData().getSeries();
        adapter = new ArrayAdapter<Sery>(this, android.R.layout.simple_spinner_item, arrInterestedBooksList);
        mSpnInterestedBooks.setAdapter(adapter);
        arrInterestedBooksList.add(0, new Sery(0, "Select Series"));
        mSpnInterestedBooks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    selectedSeries = arrInterestedBooksList.get(position);
                    callGetAllClassesRelatedToSubject(selectedSubject.getID(), selectedSeries.getID());
                    //listOfClasses.setVisibility(View.VISIBLE);
                    mLayoutManager = new LinearLayoutManager(view.getContext());
                    //listOfClasses.setLayoutManager(mLayoutManager);
                    classRangeAdapter = new ClassRangeAdapter(arrClassList, view.getContext(), ReportsActivity.this, arrInterestedBooksList, selectedSubject, arrSelectedPublishers);
                    //listOfClasses.setAdapter(classRangeAdapter);
                    // classRangeAdapter.notifyDataSetChanged();
                } else {
                    currentPubItem.setVisibility(View.GONE);
                    classRangeItem.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }*/

    private void setPurposeSpinner() {

        lstActivityPurposes.add(0, new ActivityPurpose(0, "Select Purpose of visit"));
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, lstActivityPurposes);
        mSpnPurpose.setAdapter(adapter);
        mSpnPurpose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Purposeposition = position;
                if (view != null) {
                    mLayoutManager = new LinearLayoutManager(view.getContext());
                } else {
                    mLayoutManager = new LinearLayoutManager(getApplicationContext());

                }
                mRvSelectedClasses.setLayoutManager(mLayoutManager);
                mSelectedClassAdapter = new SelectedClassAdapter(mArrSelectedClassItem, ReportsActivity.this, lstActivityPurposes.get(Purposeposition), mLoginResponse.getData().getSeries());

                Log.e("AAA", mSelectedClassAdapter.getItemCount() + "");


//                Log.d("SA", mArrSelectedClassItem.size()+"");
//                Log.d("SB", mArrSamplingItems.size()+"");

                selectAllOptionsCheckBox.setVisibility(View.GONE);
                mRvSelectedClasses.setAdapter(mSelectedClassAdapter);
                mSelectedClassAdapter.notifyDataSetChanged();
                if (lstActivityPurposes.get(Purposeposition).getID() == 5) {
                    mLlSampling.setVisibility(View.GONE);
                    mLlOrderDetail.setVisibility(View.GONE);
                } else if (lstActivityPurposes.get(Purposeposition).getID() == 3) {  //Sampling
                    selectAllOptionsCheckBox.setVisibility(View.GONE);            //show checkbox if sampling
                    mLlSampling.setVisibility(View.GONE);
                    mLlOrderDetail.setVisibility(View.GONE);
                    if (mArrSamplingItems == null)
                        Toast.makeText(getApplicationContext(), "Please create sample demand from firstVisit", Toast.LENGTH_SHORT).show();
//                    Log.d("SA", mArrSelectedClassItem.size()+"");
//                    Log.d("SB", mArrSamplingItems.size()+"");


                } else {
                    mLlSampling.setVisibility(View.GONE);
                    mLlOrderDetail.setVisibility(View.VISIBLE);
                }
                llMonths.setVisibility(View.GONE);
                if (lstActivityPurposes.get(Purposeposition).getID() == 2) {
                    llMonths.setVisibility(View.VISIBLE);
                }

//                if (mSpnPurpose.getSelectedItem().toString().equalsIgnoreCase("Sampling")) {
//                    selectAllOptionsCheckBox.setVisibility(View.VISIBLE);
//                } else {
//                    selectAllOptionsCheckBox.setVisibility(View.GONE);
//                }
                if (mSpnPurpose.getSelectedItem().toString().equalsIgnoreCase("FollowUp") && !vendorType.equalsIgnoreCase(vendorTypes.get(0))) {
                    hideTables();
                } else {
                    showTables();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mLlSampling.setVisibility(View.GONE);

            }
        });
    }

    private void setSyllSelectionDateSpinner() {
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
        adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, mArrSyllSelectionDate);
        mSpnSampleMonth.setAdapter(adapter);
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
        mSpnSessionMonth.setAdapter(adapter);
    }

    private void setPrioritySpinner() {
        mArrpriority = new ArrayList<>();
        mArrpriority.add("Select Priority");
        mArrpriority.add("Low");
        mArrpriority.add("Medium");
        mArrpriority.add("High");
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mArrpriority);
    }

    private void setCloseDateSpinner() {
        mArrClosingDate = new ArrayList<>();
        mArrClosingDate.add("Select Closing Date");
        mArrClosingDate.add("30");
        mArrClosingDate.add("60");
        mArrClosingDate.add("90");
        mArrClosingDate.add("120");
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mArrClosingDate);
    }

    private void setMainCatSpn() {
        mArrMainCat = mLoginResponse.getData().getMainCatg();
        MainCatg mainCat = new MainCatg("Select Main Category", 0);
        mArrMainCat.add(0, mainCat);
        ArrayAdapter<MainCatg> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mArrMainCat);
        mSpnMainCat.setAdapter(adapter);
        mSpnMainCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (Utility.isNetworkAvailable(mContext)) {
                    if (position != 0) {
                        callGetItemsService(Integer.toString(mArrMainCat.get(mSpnMainCat.getSelectedItemPosition()).getMainCategID()));
                        //setSubCatSpn2();
                    }
                    resetSpinners();
                } else {
                    Toast.makeText(ReportsActivity.this, getString(R.string.global_network_timeout), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setRegionSpinner() {
        mArrRegion = mLoginResponse.getData().getRegion();
        if (mArrRegion.size() > 1) {
            Region region = new Region(0, "Select Region");
            mArrRegion.add(0, region);
        } else
            callRegionCustomersService(Integer.toString(mArrRegion.get(0).getID()));
        ArrayAdapter<Region> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mArrRegion);
        mSpnRegion.setAdapter(adapter);
        mSpnRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (Utility.isNetworkAvailable(mContext)) {
                    if (position != 0) {
                        callRegionCustomersService(Integer.toString(mArrRegion.get(position).getID()));
                    }
                } else
                    Toast.makeText(ReportsActivity.this, getString(R.string.global_network_timeout), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void setRegionCustomersSpn() {
        ArrayAdapter<CustomersRelatedtoSO> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mArrRegionCustomers);
        mSpnRegionCustomers.setAdapter(adapter);
        mSpnRegionCustomers.setVisibility(View.VISIBLE);
    }

    private void setSubCatSpn(GetSubCatResponse response) {
        mArrSubCat = response.getSubCategory();
        mSpnSubCat.setVisibility(View.GONE);
        GetSubCatResponse.SubCategory subCat = new GetSubCatResponse().new SubCategory(0, "Select sub category");
        mArrSubCat.add(0, subCat);
        ArrayAdapter<GetSubCatResponse.SubCategory> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mArrSubCat);
        mSpnSubCat.setAdapter(adapter);
        mSpnSubCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (Utility.isNetworkAvailable(mContext)) {
                    if (position != 0) {
//                        callGetItemsService(Integer.toString(mArrMainCat.get(mSpnMainCat.getSelectedItemPosition()).getMainCategID()), Integer.toString(mArrSubCat.get(position).getID()));
                    }
//                    resetSpinners();
                } else {
                    Toast.makeText(ReportsActivity.this, getString(R.string.global_network_timeout), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setSubCatASpn(GetSubCategoryAResponse response) {
        mArrSubCatA = response.getSubCategoryA();
        getmSpnSubCatA.setVisibility(View.GONE);
        GetSubCategoryAResponse.SubCategoryA subCat = new GetSubCategoryAResponse().new SubCategoryA(0, "Select sub category A");
        mArrSubCatA.add(0, subCat);
        ArrayAdapter<GetSubCategoryAResponse.SubCategoryA> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mArrSubCatA);
        getmSpnSubCatA.setAdapter(adapter);
        getmSpnSubCatA.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (Utility.isNetworkAvailable(mContext)) {
                    if (position != 0) {
                    }
                    resetSpinners();
                } else {
                    Toast.makeText(ReportsActivity.this, getString(R.string.global_network_timeout), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
//    private void setItemsSpn(GetItemsResponse response) {
////        mArrItems = response.getItems();
////        mSpnItems.setVisibility(View.VISIBLE);
////        GetItemsResponse.Item subItem = new GetItemsResponse().new Item(0, "Select Item");
////        mArrItems.add(0, subItem);
////        ArrayAdapter<GetItemsResponse.Item> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mArrItems);
////        //CustomArrayAdapter adapter = new CustomArrayAdapter(mContext, R.layout.row_spn_items, mArrItems);
////        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
////        CustomAdapterTwo adapter = new CustomAdapterTwo(this, android.R.layout.simple_spinner_item, mArrItems);
////        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
////        mSpnItems.setAdapter(adapter);
//    }

    private void callGetItemsService(String mainCatId) {
        mDialog.show();
        mDialog.setMessage("please wait...");
        mService = RestClient.getInstance(this);
        Call<GetItemsResponse> userObject = mService.getItems(mainCatId);
        RestCallbackObject callbackObject = new RestCallbackObject(this, this, RequestCode.GET_LIST_REQUEST_CODE)
                .showProgress(true, 0).dontHideProgress(false);
        userObject.enqueue(callbackObject);
    }

    private void callGetRetailerSampleItemsService(int RetailerID) {
        mDialog.show();
        mDialog.setMessage("Previous Detials Loading....");
        mService = RestClient.getInstance(this);
        Call<GetRetailerSampling> userObject = mService.GetSamplingItems(RetailerID);
        RestCallbackObject callbackObject = new RestCallbackObject(this, this, RequestCode.GET_LIST_SAMPLING_ITEMS)
                .showProgress(true, 0).dontHideProgress(false);
        userObject.enqueue(callbackObject);
    }


    private void callGetPurpose(int RetailerID) {
        mDialog.show();
        mDialog.setMessage("Getting ActivityPurposes....");
        mService = RestClient.getInstance(this);
        Call<GetServerResponse> userObject = mService.getActivityPurpose(RetailerID);
        RestCallbackObject callbackObject = new RestCallbackObject(this, this, RequestCode.GET_ACTIVITY_PURPOSE_RETAILER_WISE)
                .showProgress(true, 0).dontHideProgress(false);
        userObject.enqueue(callbackObject);
    }

   /* private void callSubCatService(String id) {
        mDialog.show();
        mDialog.setMessage("please wait...");
        mService = RestClient.getInstance(this);
        Call<GetSubCatResponse> userObject = mService.getSubCat(id);
        RestCallbackObject callbackObject = new RestCallbackObject(this, this, RequestCode.GET_SUBCATEGORY_REQUEST_CODE)
                .showProgress(true, 0).dontHideProgress(false);
        userObject.enqueue(callbackObject);
    }

    private void callSubCatAService(String mainCatId, String subCatId) {
        mDialog.show();
        mDialog.setMessage("please wait...");
        mService = RestClient.getInstance(this);
        Call<GetSubCategoryAResponse> userObject = mService.getSubCatA(mainCatId, subCatId);
        RestCallbackObject callbackObject = new RestCallbackObject(this, this, RequestCode.GET_SUBCATEGORY_A_REQUEST_CODE)
                .showProgress(true, 0).dontHideProgress(false);
        userObject.enqueue(callbackObject);
    }*/

    private void callRegionCustomersService(String id) {
      /*  mDialog.show();
        mDialog.setMessage("please wait...");
        mService = RestClient.getInstance(this);
        Call<GetServerResponse> userObject = mService.getCustomerRelatedToRegion(id);
        RestCallbackObject callbackObject = new RestCallbackObject(this, this, RequestCode.GET_CUSTOMER_RELATED_TO_REGION)
                .showProgress(true, 0).dontHideProgress(false);
        userObject.enqueue(callbackObject);*/
    }

    private void setActivityReq(VisitReportRequest obj) {
        if (mCustomerObj != null)
            obj.setRetailerID(mCustomerObj.getID());
        obj.setActivityType(activityType);
        obj.setPurposeofVisit(mSpnPurpose.getSelectedItem().toString());
        activityDetail = etActivityDetail.getText().toString();
        personName = etCntctPersonName.getText().toString();
        personPhone = etPhoneNo.getText().toString();
        personDesignation = etPersonDesignation.getText().toString();
        obj.setOtherSOID(otherSOID);
        obj.setCombined(rbCombined.isChecked());
        obj.setSchoolShopName(schoolShopName);

        obj.setActivityDetails(activityDetail);
        obj.setNextVisitDate(nextVisitDate);
        obj.setSaleOfficerId(mLoginResponse.getData().getSOID());
        obj.setSampleRecipt(getStringImage(bitmap));
        obj.setJobID(JobID);
        if (lstActivityPurposes.get(Purposeposition).getID() == 2) {
            obj.setSampleMonth(mArrSyllSelectionDate.get(mSpnSampleMonth.getSelectedItemPosition()));
            obj.setSessionMonth(mArrSessionDate.get(mSpnSessionMonth.getSelectedItemPosition()));
        }
        int expectedValue = 0;
        String expectedQuantityString = etExpectedQuantity.getText().toString();
        try {
            expectedValue = Integer.parseInt(expectedQuantityString);
        } catch (NumberFormatException e) {

        }

        if (mSpnPosOrNeg.getSelectedItemPosition() == 0) {
            expectedValue = 1 * expectedValue;
        } else {
            expectedValue = -1 * expectedValue;
        }

        obj.seteXValue(expectedValue);

        obj.setContactPersonName(personName);
        obj.setContcatPersonPhNo(personPhone);
        obj.setContactPersonDesignation(personDesignation);
        for (SelectedClassItem classItem : mArrSelectedClassItem) {
            String seriesName = null, competitorString = "";
            boolean Delievered = false, Returned = false, bFinal = false;
            int subjectID = 0, ClassID = 0, seriesID = 0, JobItemID = 0;
            try {
                if (classItem != null) {
                    subjectID = classItem.getSelectedSubjectID();
                    ClassID = classItem.getSelectedClassID();
                    if (classItem.getSeries() != null) {
                        seriesID = classItem.getSeries().getID();
                    }
                    JobItemID = classItem.getJobItemID();
                    Delievered = classItem.isDelievered();
                    Returned = classItem.isReturned();
                    if (lstActivityPurposes.get(Purposeposition).getID() == 6) {
                        bFinal = true;
                    } else {
                        bFinal = false;
                    }
                    for (Publisher p : classItem.lstPublisher) {
                        if (p.isSelected()) {
                            competitorString = p.getPublisher();
                            break;
                        }
                    }
                }
            } catch (Exception msg) {
                showToast(msg.toString());
            }
            SelectedVisitItems myObj = new SelectedVisitItems(subjectID, seriesID, ClassID, competitorString, mLoginResponse.getData().getSOID(), JobItemID, Delievered, Returned, bFinal);
            mArrSelectedJobItems.add(myObj);
//            if(lstActivityPurposes.get(Purposeposition).getID() == 6&&myObj.isReturned()){
//                mArrSelectedJobItems.add(myObj);
//            }else if(lstActivityPurposes.get(Purposeposition).getID() != 6){
//                mArrSelectedJobItems.add(myObj);
//            }
        }
        obj.setJobItems(mArrSelectedJobItems);
    }

    private void resetSpinners() {
        mRvItems.setVisibility(View.VISIBLE);
        rlSelectItems.setVisibility(View.VISIBLE);
        mRvSelectedItems.setVisibility(View.VISIBLE);
        mTvSelectedItems.setVisibility(View.VISIBLE);
        ibShowItems.setBackgroundResource(R.drawable.ic_expand_more_black_24dp);
        if (mArrItems.size() > 0) {
            mArrItems.clear();
            mAdapter.notifyDataSetChanged();
        }
    }

    public void openCameraStart() {
        RuntimePermissionHandler.requestPermission(REQ_CODE_CAMERA_PERMISSION, this, mPermissionListener, RuntimePermissionUtils.CameraPermission);
    }

    public void openCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_INTENT);
    }

    public void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_INTENT);
    }

    public void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(mContext);
        pictureDialog.setTitle("Select Action");
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
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
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_INTENT) {
            if (data != null && data.getExtras() != null && data.getExtras().get("data") != null) {
                bitmap = Bitmap.createScaledBitmap((Bitmap) data.getExtras().get("data"), 512, 512, true);
                mIvSampleReceipt.setImageBitmap(bitmap);
                mIvSampleReceipt.setVisibility(View.VISIBLE);
            }
        } else if (requestCode == GALLERY_INTENT) {
            if (data != null) {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mIvSampleReceipt.setImageBitmap(bitmap);
            mIvSampleReceipt.setVisibility(View.VISIBLE);
        }
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        RuntimePermissionHandler.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    private RuntimePermissionHandler.PermissionListener mPermissionListener = new RuntimePermissionHandler.PermissionListener() {
        @Override
        public void onRationale(final @NonNull RuntimePermissionHandler.PermissionRequest permissionRequest, final Activity target, final int requestCode, @NonNull final String[] permissions) {
            switch (requestCode) {
                case REQ_CODE_CAMERA_PERMISSION:
                    new AlertDialog.Builder(target)
                            .setPositiveButton(R.string.allow, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(@NonNull DialogInterface dialog, int which) {
                                    permissionRequest.proceed(target, requestCode, permissions);
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton(R.string.deny, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(@NonNull DialogInterface dialog, int which) {
                                    permissionRequest.cancel(target, requestCode, permissions);
                                    dialog.dismiss();
                                }
                            })
                            .setCancelable(false)
                            .setMessage(R.string.camera_permission_rational)
                            .show();
                    break;
            }
        }

        @Override
        public void onAllowed(int requestCode, @NonNull String[] permissions) {
            switch (requestCode) {
                case REQ_CODE_CAMERA_PERMISSION:
                    showPictureDialog();
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
                }
            }
        }

        @Override
        public void onNeverAsk(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            switch (requestCode) {
                case REQ_CODE_CAMERA_PERMISSION:
                    new AlertDialog.Builder(ReportsActivity.this)
                            .setPositiveButton(R.string.allow, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(@NonNull DialogInterface dialog, int which) {
                                    RuntimePermissionUtils.openAppSettings(ReportsActivity.this);
                                }
                            })
                            .setNegativeButton(R.string.deny, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(@NonNull DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setCancelable(false)
                            .setMessage(R.string.camera_pemission_never_ask)
                            .show();
                    break;
            }
        }
    };


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

    @Override
    public void onFailure(String error, RequestCode requestCode) {
        if (requestCode == RequestCode.VISIT_REPORT_ACTIVITY) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
            btnSubmit.setEnabled(true);
            Log.d("Response: ", error);
            Toast.makeText(this, "Failed-- " + error, Toast.LENGTH_SHORT).show();
        } else if (requestCode == RequestCode.GET_SUBCATEGORY_REQUEST_CODE) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
            Log.d("Response: ", error);
        } else if (requestCode == RequestCode.GET_SUBCATEGORY_A_REQUEST_CODE) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
            Log.d("Response: ", error);
        } else if (requestCode == RequestCode.GET_LIST_SAMPLING_ITEMS) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
            Log.d("Response: ", error);
        } else if (requestCode == RequestCode.GET_LIST_REQUEST_CODE) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
            // TODO remove this visibility code once services are fine
            rlSelectItems.setVisibility(View.VISIBLE);
            Log.d("Response: ", error);
        } else if (requestCode == RequestCode.API_GET_SCHOOL_INFO) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }

            Log.d("Response: ", error);
        } else if (requestCode == RequestCode.GET_SUBJECTS_SERIES_WISE) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
            ll_header.setVisibility(View.GONE);
            ll_sub_class.setVisibility(View.GONE);
            Log.d("Response: ", error);
        }

        if (requestCode == RequestCode.GET_ACTIVITY_PURPOSE_RETAILER_WISE) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
            Log.d("Response: ", error);
        }
    }

    @Override
    public void onSuccess(Object object, RequestCode requestCode) {
        if (requestCode == RequestCode.GET_LIST_SAMPLING_ITEMS) {
            GetRetailerSampling response = (GetRetailerSampling) object;
            mArrSamplingItems = response.getSampleItemsList();
            mArrSelectedClassItem = new ArrayList<>();
            if (response.getSampleItemsList().size() <= 0) {
                JobID = 0;
            } else {
                for (GetRetailerSampling.SampleItems item : mArrSamplingItems) {
                    Log.e("Series ID", String.valueOf(item.getSeries()));
                    ArrayList<String> competitors = new ArrayList<String>(Arrays.asList(item.getCompetitors().split("\\s*,\\s*")));
                    if (competitors.size() <= 1) {
                        if (competitors.get(0).equals("") || competitors.get(0).equals("null")) {
                            competitors.remove(0);
                            List<Competitor> pub = mLoginResponse.getData().getCompetitor();
                            competitors.addAll(getStringsList(pub, ""));
                        } else {
                            List<Competitor> pub = mLoginResponse.getData().getCompetitor();
                            competitors.addAll(getStringsList(pub, competitors.get(0)));

                        }
                    }
                    SelectedClassItem clsItem = new SelectedClassItem(item.getClassID(), item.getClassName(), item.getSubjects(), item.getSubjectName(), new Sery(item.getSeries(), item.getSeriesName()), item.isbFinal(), competitors, item.getJobItemID(), item.isDelivered(), item.isReturned());
                    mArrSelectedClassItem.add(clsItem);
                }
                mLayoutManager = new LinearLayoutManager(this);
                mRvSelectedClasses.setLayoutManager(mLayoutManager);
                mSelectedClassAdapter = new SelectedClassAdapter(mArrSelectedClassItem, this, lstActivityPurposes.get(Purposeposition), mLoginResponse.getData().getSeries());
                mRvSelectedClasses.setAdapter(mSelectedClassAdapter);
                mRvSelectedClasses.setVisibility(View.VISIBLE);
                JobID = mArrSamplingItems.get(0).getJobID();
            }
        } else if (requestCode == RequestCode.VISIT_REPORT_ACTIVITY) {
            ServerResponse serverResponse = (ServerResponse) object;
            if (serverResponse.getResultType() == ServerCodes.ServerResultType.SERVER_RESULT_SUCCESS)
                Toast.makeText(this, serverResponse.getMessage(), Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this, serverResponse.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        } else if (requestCode == RequestCode.GET_SUBCATEGORY_REQUEST_CODE) {
            mGetSubCatResponse = (GetSubCatResponse) object;
            setSubCatSpn(mGetSubCatResponse);
        } else if (requestCode == RequestCode.GET_SUBCATEGORY_A_REQUEST_CODE) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
            mGetSubCatAResponse = (GetSubCategoryAResponse) object;
//            setSubCatSpn(mGetSubCatResponse);
            setSubCatASpn(mGetSubCatAResponse);
        } else if (requestCode == RequestCode.GET_LIST_REQUEST_CODE) {
            mGetItemsResponse = (GetItemsResponse) object;
            rlSelectItems.setVisibility(View.VISIBLE);
            mArrItems = mGetItemsResponse.getItems();
            //setItemsSpn(mGetItemsResponse);
        } else if (requestCode == RequestCode.GET_CUSTOMER_RELATED_TO_REGION) {
            GetServerResponse response = (GetServerResponse) object;
            mArrRegionCustomers = new ArrayList<>();
            mArrRegionCustomers = response.getCustomersRelatedtoSO();
            mArrRegionCustomers.add(0, new CustomersRelatedtoSO(0, false, "Select Customer"));
            setRegionCustomersSpn();
        } else if (requestCode == RequestCode.GET_ACTIVITY_PURPOSE_RETAILER_WISE) {
            GetServerResponse response = (GetServerResponse) object;
            //lstActivityPurposes = response.getPurposeOfActivity();
            setPurposeSpinner();
        } else if (requestCode == RequestCode.API_GET_SCHOOL_INFO) {
            GetSchoolInfo response = (GetSchoolInfo) object;
            mSchoolInfos = new ArrayList<>();
//            mSchoolInfos = response.getGetSchoolInfo();

//            Log.d("taggg", "setValues: " + response );
//            Log.d("taggg", "setValues: " + response.getPrincipalName() );

            setValues(response.getPrincipalName(), response.getPhone1(), response.getSchoolShopName(), response.getExpectedQuantity(), response.isMemberSchool());

        } else if (requestCode == RequestCode.GET_SUBJECTS_SERIES_WISE) {
            GetServerResponse serverResponse = (GetServerResponse) object;

            if (serverResponse.getSubjectsRelatedToSeries() != null) {
                //make table visible
                subjectSelectArray = serverResponse.getSubjectsRelatedToSeries();
                ll_header.setVisibility(View.VISIBLE);
                ll_sub_class.setVisibility(View.VISIBLE);
                populateTable(subjectSelectArray);
            } else if (serverResponse.getSubjectsRelatedToSeries() == null) {
                //hide table
                ll_header.setVisibility(View.GONE);
                ll_sub_class.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "There are no Books related with this Series.", Toast.LENGTH_LONG).show();
            }
            if (mDialog.isShowing()) {
                mDialog.hide();
            }
        }
        if (mDialog.isShowing())
            mDialog.dismiss();
    }

    private List<String> getStringsList(List<Competitor> pub, String s) {
        List<String> list = new ArrayList<String>();
        for (Competitor c : pub) {
            if (!c.getCompetitorName().equalsIgnoreCase(s)) {
                list.add(c.getCompetitorName());
            }
        }
        return list;
    }

    private void populateTable(List<Subjects> subjectSelectArray) {

        rvSubjClas.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        ArrayList<SubjectAndClassesModel> userObject = new ArrayList<SubjectAndClassesModel>();
        for (Subjects subject : subjectSelectArray) {
            userObject.add(new SubjectAndClassesModel(subject));
        }
        SubjectsAndClassesAdapter adapter = new SubjectsAndClassesAdapter(userObject, this, new AddSubjClass() {

            @Override
            public void addRemoveSelectedItem(Subjects subject, Clas clas, boolean isChecked, int absoluteAdapterPosition) {
                addRemoveSchoolItem(new SelectedClassItem(clas.getClassID(), clas.getClassName(), subject.getID(),
                        subject.getSubjectName(), selectedSeries, (ArrayList<String>) arrPublishersToSend, 0, true, false, isChecked), isChecked, absoluteAdapterPosition);
            }
        });

        rvSubjClas.setAdapter(adapter);
    }

    private ArrayList<Clas> getDuplicateList() {
        ArrayList<Clas> duplicates = new ArrayList<>();
        for (Clas cls : arrClassList) {
            Clas _cls = new Clas();
            _cls.setSelected(false);
            _cls.setClassName(cls.getClassName());
            _cls.setID(cls.getID());
            duplicates.add(_cls);
        }
        return duplicates;
    }

    private void getSchoolsInfoAndShow(int RetailerID) {
        Log.e("taggg", "getSchoolsInfoAndShow: CALLED");
        mService = RestClient.getInstance(this);
        Call<GetSchoolInfo> userObject = mService.getSchoolInfo(RetailerID);
        RestCallbackObject callbackObject = new RestCallbackObject(this, this, RequestCode.API_GET_SCHOOL_INFO)
                .showProgress(true, 0).dontHideProgress(false);
        userObject.enqueue(callbackObject);
    }

    private void setValues(String strName, String strPhone, String shopName, int expectedQuantity, boolean isMemberSchool) {
        etCntctPersonName.setText(strName);
        etPhoneNo.setText(strPhone);
        String isMember = isMemberSchool ? "YES" : "NO";
        etPersonIsMember.setText(isMember);
        if (shopName != null)
            mEtShopName.setText(shopName);
        if (expectedQuantity < 0) {
            expectedQuantity = expectedQuantity * -1;
            mSpnPosOrNeg.setSelection(1);
        } else {
            mSpnPosOrNeg.setSelection(0);
        }
        etExpectedQuantity.setText("" + expectedQuantity);
        Log.e("setValues", "name :" + strName);
        Log.e("setValues", "PHN :" + strPhone);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //case R.id.show_publishers:
            //getListOfPublisher(v);
            case R.id.tv_next_visit_date:
                new DatePickerDialog(ReportsActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.iv_capture:
                openCameraStart();
                break;
            case R.id.btn_submit:
                visitReportRequest = new VisitReportRequest();
                if (Utility.isNetworkAvailable(ReportsActivity.this)) {
                    if (validateForm()) {
                        setActivityReq(visitReportRequest);
                        callRegService();
                    }
                } else {
                    Toast.makeText(ReportsActivity.this, getString(R.string.str_no_internet), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_select_items:
            case R.id.ib_show_items:
                if (mArrItems.size() > 0) {
                    if (mRvItems.getVisibility() == View.GONE) {
                        mRvItems.setVisibility(View.VISIBLE);
                        ibShowItems.setBackgroundResource(R.drawable.ic_expand_less_black_24dp);
                        mLayoutManager = new LinearLayoutManager(this);
                        mRvItems.setLayoutManager(mLayoutManager);
                        mAdapter = new ItemsAdapter(mArrItems, this);
                        mRvItems.setAdapter(mAdapter);
                    } else {
                        mRvItems.setVisibility(View.GONE);
                        ibShowItems.setBackgroundResource(R.drawable.ic_expand_more_black_24dp);
                    }
                } else
                    Toast.makeText(ReportsActivity.this, "No item found", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void uncheckupdate(SelectedItemsModel item, boolean isChecked) {
        if (mArrItems.size() > 0) {
            for (int i = 0 ; i < mArrItems.size() ; i++) {
                if (mArrItems.get(i).getItemId() == item.getItemId()) {
                    mArrItems.get(i).setChecked(isChecked);
                    mAdapter.notifyDataSetChanged();
                    return;
                }
            }
        }
    }

    public void addRemoveItem(SelectedItemsModel item, boolean isAdd, int position) {
        if (mSelectedItemsAdapter == null) {
            mLayoutManager = new LinearLayoutManager(this);
            mRvSelectedItems.setLayoutManager(mLayoutManager);
            mSelectedItemsAdapter = new SelectedItemsAdapter(mArrSelectedItems, this);
            mRvSelectedItems.setAdapter(mSelectedItemsAdapter);
        }
        if (isAdd) {
            mArrSelectedItems.add(item);
        } else {
            int i = 0;
            if (mArrSelectedItems.size() > 0) {
                for (Iterator<SelectedItemsModel> iterator = mArrSelectedItems.iterator() ; iterator.hasNext() ; i++) {
                    if (i == position)
                        iterator.remove();
                }
            }
        }
        if (mArrSelectedItems.size() > 0) {
            if (mRvSelectedItems.getVisibility() == View.GONE) {
                mRvSelectedItems.setVisibility(View.VISIBLE);
                mTvSelectedItems.setVisibility(View.VISIBLE);
            }
            mSelectedItemsAdapter.notifyDataSetChanged();
        } else {
            mRvSelectedItems.setVisibility(View.GONE);
            mTvSelectedItems.setVisibility(View.GONE);
        }
        //mSelectedItemsAdapter.notifyDataSetChanged();
    }

    @Override
    public void uncheckSchoolItemupdate(SelectedClassItem item, boolean isChecked) {
        if (mArrSelectedClassItem.size() > 0) {
            for (int i = 0 ; i < mArrSelectedClassItem.size() ; i++) {
                if (mArrSelectedClassItem.get(i).getSelectedClassID() == item.getSelectedClassID()) {
                    // TODO: Change the items to mArrSelectediTEMS
                    //mArrItems.get(i).setChecked(isChecked);
                    //mAdapter.notifyDataSetChanged();
                    //return;
                }
            }
        }
    }

    @Override
    public void updateReturnStatus(final int position, boolean status) {
        for (int i = 0 ; i < mArrSelectedClassItem.size() ; i++) {
            if (i == position) {
                mArrSelectedClassItem.get(i).setReturned(status);
            }
        }
        /*mRvSelectedClasses.post(new Runnable() {
            @Override
            public void run() {
                //mSelectedClassAdapter.notifyItemChanged(position+1);
                mSelectedClassAdapter.notifyDataSetChanged();
            }
        });*/
    }

    @Override
    public void updateDeliveredStatus(final int position, boolean status) {
        for (int i = 0 ; i < mArrSelectedClassItem.size() ; i++) {
            if (i == position) {
                mArrSelectedClassItem.get(i).setDelievered(status);
            }
        }
        /*mRvSelectedClasses.post(new Runnable() {
            @Override
            public void run() {
                //mSelectedClassAdapter.notifyItemChanged(position+1);
                mSelectedClassAdapter.notifyDataSetChanged();
            }
        });
*/
    }

    @Override
    public void updateSeries(final int position, Sery series) {
        for (int i = 0 ; i < mArrSelectedClassItem.size() ; i++) {
            if (i == position) {
                mArrSelectedClassItem.get(i).setSeries(series);

            }
        }
        /*mRvSelectedClasses.post(new Runnable() {
            @Override
            public void run() {
                mSelectedClassAdapter.notifyItemChanged(position+1);
                mSelectedClassAdapter.notifyDataSetChanged();
            }
        });*/
    }

    @Override
    public void addRemoveSchoolItem(SelectedClassItem item, boolean isAdd, int position) {
        if (mSelectedClassAdapter == null) {
            mLayoutManager = new LinearLayoutManager(this);
            mRvSelectedClasses.setLayoutManager(mLayoutManager);
            mSelectedClassAdapter = new SelectedClassAdapter(mArrSelectedClassItem, this, lstActivityPurposes.get(Purposeposition), mLoginResponse.getData().getSeries());
            mRvSelectedClasses.setAdapter(mSelectedClassAdapter);
        }
        if (isAdd) {
            if (mArrSelectedClassItem.size() > 0) {
                boolean isPresent = true;
                for (int i = 0 ; i < mArrSelectedClassItem.size() ; i++) {
                    if (mArrSelectedClassItem.get(i).getSeries().getSeriesName().equals(item.getSeries().getSeriesName()) &&
                            mArrSelectedClassItem.get(i).getSelectedSubjectID() == item.getSelectedSubjectID() &&
                            mArrSelectedClassItem.get(i).getSelectedClassID() == item.getSelectedClassID()) {
                        Toast.makeText(getApplicationContext(), "Class, Subject and Series Already Exist", Toast.LENGTH_LONG).show();
                        isPresent = false;
                        break;
                    }
                }
                if (isPresent) {
                    mArrSelectedClassItem.add(new SelectedClassItem(item.getSelectedClassID(), item.getClassName(), item.getSelectedSubjectID(), item.getSubjectName(), item.getSeries(), (ArrayList<String>) item.getLstPublisher(), item.getJobItemID(), true, false, isAdd));
                    mArrSelectedClassItemToSend.add(mArrSelectedClassItem.get(mArrSelectedClassItem.size() - 1));
                    mSelectedClassAdapter.notifyItemInserted(mArrSelectedClassItem.size());
                }
            } else {
                mArrSelectedClassItem.add(new SelectedClassItem(item.getSelectedClassID(), item.getClassName(), item.getSelectedSubjectID(), item.getSubjectName(), item.getSeries(), (ArrayList<String>) item.getLstPublisher(), item.getJobItemID(), true, false, isAdd));
                mArrSelectedClassItemToSend.add(mArrSelectedClassItem.get(mArrSelectedClassItem.size() - 1));
                mSelectedClassAdapter.notifyItemInserted(mArrSelectedClassItem.size());
            }
        } else {
            if (mArrSelectedClassItem.size() > 0) {
                int index = -1;
                for (int i = 0 ; i < mArrSelectedClassItem.size() ; i++) {
                    if (mArrSelectedClassItem.get(i).getClassName().equals(item.getClassName()) &&
                            item.getSubjectName().equals(mArrSelectedClassItem.get(i).getSubjectName()) &&
                            item.getSeries().getSeriesName().equals(mArrSelectedClassItem.get(i).getSeries().getSeriesName())) {
                        index = i;
                        // mArrSelectedClassItem.get(i).setFlag(0);
                        // mSelectedClassAdapter.notifyItemRemoved(position);//to work

//                        mSelectedClassAdapter.notifyItemRangeChanged(position, mSelectedClassAdapter.getItemCount());
//                        mSelectedClassAdapter.notifyDataSetChanged();
//                        mArrSelectedClassItem.size()-1
                        break;
                    }
                }
                if (index != -1 && mArrSelectedClassItem.get(index).isDelievered()) {
                    mArrSelectedClassItemToSend.remove(mArrSelectedClassItem.remove(index));
                    mSelectedClassAdapter.notifyItemRemoved(index + 1);
                }
            }
        }
        if (mArrSelectedClassItem.size() > 0) {
            if (mRvSelectedClasses.getVisibility() == View.GONE) {
                mRvSelectedClasses.setVisibility(View.VISIBLE);
                // mTvSelectedItems.setVisibility(View.VISIBLE);
            }
        } else {
            mRvSelectedClasses.setVisibility(View.GONE);
        }
    }

    @Override
    public void AddRemovePublishers(Competitor item, boolean isAdd) {
//        if (isAdd) {
//            //arrSelectedPublishers.add(item);
//            mLayoutManager = new LinearLayoutManager(getApplicationContext());
//            //listOfClasses.setLayoutManager(mLayoutManager);
//            //classRangeAdapter = new ClassRangeAdapter(arrClassList, getApplicationContext(), ReportsActivity.this, selectedSeries, arrSelectedPublishers);
//            //listOfClasses.setAdapter(classRangeAdapter);
//            classRangeAdapter.notifyDataSetChanged();
//        } else {
//            if (arrSelectedPublishers.size() > 0) {
//                int i = 0;
//                for (Iterator<Competitor> iterator = arrSelectedPublishers.iterator(); iterator.hasNext(); i++) {
//                    if ((iterator.next().getID() == item.getID())) {
//                        iterator.remove();
//                    }
//                }
//            }
//        }
    }

    @Override
    public void uncheckPublisher(Competitor item, boolean isChecked) {
    }

    /*@Override
    public void AddRemoveInterestedBook(Sery item, boolean isAdd) {
        if (isAdd) {
            arrSelectedInterestedBooks.add(item);
        } else {
            if (arrSelectedInterestedBooks.size() > 0) {
                for (Iterator<Sery> iterator = arrSelectedInterestedBooks.iterator(); iterator.hasNext(); ) {
                    if (iterator.next().getID() == item.getID() && selectedSubject.getSubjectName() == item.getSubjectName()) {
                        iterator.remove();
                    }
                }

            }
        }
    }

    @Override
    public void uncheckInterestedBookUpdate(Sery item, boolean isChecked) {
    }*/


//    public void getListOfPublisher(View view) {
//        if (arrPublishers.size() > 0 && !currentPublisherOpen) {
//            currentPublisherOpen = true;
//            listOfPublishers.setVisibility(View.VISIBLE);
//            mLayoutManager = new LinearLayoutManager(this);
//            listOfPublishers.setLayoutManager(mLayoutManager);
//            mPublisherItemAdapter = new PublishersListAdapter(arrPublishers, this, this, selectedSeries);
//            listOfPublishers.setAdapter(mPublisherItemAdapter);
//        } else if (currentPublisherOpen) {
//            currentPublisherOpen = false;
//            listOfPublishers.setVisibility(View.GONE);
//        }
//    }

    @Override
    public void onBackPressed() {
        Log.i("ONBACKPRESSED", "onBackPressed--");
        if (_doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this._doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to quit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                _doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public void getClassRange(View view) {
        if (arrClassList.size() > 0) {
            /*if (//listOfClasses.getVisibility() == View.GONE) {
                //listOfClasses.setVisibility(View.VISIBLE);
            } else {
                //listOfClasses.setVisibility(View.GONE);
            }*/
            mLayoutManager = new LinearLayoutManager(view.getContext());
            ////listOfClasses.setLayoutManager(mLayoutManager);
            // classRangeAdapter = new ClassRangeAdapter(arrClassList, view.getContext(), ReportsActivity.this, selectedSeries, arrSelectedPublishers);
            ////listOfClasses.setAdapter(classRangeAdapter);
        }
    }

}
