package com.app.salesforce.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.app.salesforce.R;
import com.app.salesforce.activity.DashboardActivity;
import com.app.salesforce.activity.FragmentShownActivity;
import com.app.salesforce.network.ApiService;
import com.app.salesforce.network.RequestCode;
import com.app.salesforce.network.RestCallbackObject;
import com.app.salesforce.network.RestClient;
import com.app.salesforce.network.ServerCodes;
import com.app.salesforce.network.ServerConnectListenerObject;
import com.app.salesforce.request.ShopRegRequest;
import com.app.salesforce.response.GetAreasResponse;
import com.app.salesforce.response.GetCitiesResponse;
import com.app.salesforce.response.GetServerResponse;
import com.app.salesforce.response.Region;
import com.app.salesforce.response.ServerResponse;
import com.app.salesforce.utilities.AppKeys;
import com.app.salesforce.utilities.Utility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;

public class ShopRegFragment extends Fragment implements View.OnClickListener, ServerConnectListenerObject {

    private Spinner mSpShopType;
    private Spinner mSpnCity;
    private Spinner mSpnArea;
    private Spinner mSpnRegion;


    private EditText mEtShopName;
    private EditText mEtOwnerName;
    private EditText mEtContNum;
    private EditText mEtCellNum;
    private EditText mEtEmail;
    private EditText mEtAddress;
    private EditText mEtContPerson;
    private EditText mEtOtherContNum;
    private EditText mEtRemarks;
    private TextView mTvLong;
    private TextView mTvLat;
    private Button mBtnSubmit;
    private ImageView mIvCapture, mIvImage;
    private ImageView mIvLocation;
    private Bitmap bitmap;

    private boolean isLocationVisible;

    private List<GetCitiesResponse.City> mArrCities;
    private List<Region> mArrRegions;
    private List<GetAreasResponse.Area> mArrAreas;
    private String mRegionId;

    private ArrayList<String> mArrShopType;
    private ArrayAdapter adapter;
    private ArrayList<String> mArrShopCat;

    private ProgressDialog mDialog;
    private ShopRegRequest mShopRegReq;
    private ApiService mService;
    private GetServerResponse mLoginResponse;
    private Context mContext;
    private GetCitiesResponse mCitiesResponse;
    private GetAreasResponse mAreaResponse;
    private View view;
    private static final int CAMERA_INTENT = 10;
    private static final int GALLERY_INTENT = 40;
    public static ShopRegFragment newInstance( ) {
        return (new ShopRegFragment());
    }


    /**
     * here layout for fragment is set.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_shop_registration, container, false);
        mContext = getActivity();
        Paper.init(mContext);
        parseStoredData();
        initGui();
        Log.d("class_name", this.getClass().getSimpleName());
        return view;
    }

    /**
     * all GUI components are initialized here
     * image is set
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {super.onViewCreated(view, savedInstanceState);}

    private void initGui() {
        mSpShopType = view.findViewById(R.id.sp_shop_type);
        mEtShopName = view.findViewById(R.id.et_shop_name);
        mEtOwnerName = view.findViewById(R.id.et_owner_name);
        mEtContNum = view.findViewById(R.id.et_contact_number);
        mEtCellNum = view.findViewById(R.id.et_cell_number);
        mEtEmail = view.findViewById(R.id.et_email);
        mEtAddress = view.findViewById(R.id.et_address);
        mEtContPerson = view.findViewById(R.id.et_contact_person);
        mEtOtherContNum = view.findViewById(R.id.et_other_person_number);
        mEtRemarks = view.findViewById(R.id.et_remarks);
        mTvLong = view.findViewById(R.id.tv_long);
        mTvLat = view.findViewById(R.id.tv_lat);
        mIvImage = view.findViewById(R.id.iv_image);
        mSpnCity = view.findViewById(R.id.sp_city);
        mSpnArea = view.findViewById(R.id.sp_sub_area);
        mSpnRegion = view.findViewById(R.id.spn_region);

        mBtnSubmit = view.findViewById(R.id.btn_submit);
        mIvCapture = view.findViewById(R.id.iv_capture);
        mIvLocation = view.findViewById(R.id.iv_location);
        mSpnCity.setVisibility(View.GONE);
        mSpnArea.setVisibility(View.GONE);

        mBtnSubmit.setOnClickListener(this);
        mIvCapture.setOnClickListener(this);
        mIvLocation.setOnClickListener(this);
        setAllSpinners();

        mDialog = new ProgressDialog(getActivity());
        mDialog.setCancelable(false);

        mTvLat.setText(Double.toString(DashboardActivity.mLocationService.getLatitude()));
        mTvLong.setText(Double.toString(DashboardActivity.mLocationService.getLongitude()));
    }

    private void parseStoredData() {
        mLoginResponse = Paper.book().read(AppKeys.KEY_LOGIN_RESPONSE);
    }

    public void setAllSpinners() {
        setRegionSpinner();
       // setShopTypeSpinner();
    }


    private void setRegionSpinner() {
        mArrRegions = mLoginResponse.getData().getRegion();
        Region emptyRegion = new Region(0, "Select Region");
        mArrRegions.add(0, emptyRegion);
        ArrayAdapter<Region> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, mArrRegions);
        mSpnRegion.setAdapter(adapter);
        mSpnRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    callGetCityService(mArrRegions.get(position).getID(),mLoginResponse.getData().getSOID());
                }
                mSpnCity.setVisibility(View.GONE);
                mSpnArea.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setCitySpinner(GetCitiesResponse citiesResponse) {
        mArrCities = citiesResponse.getCities();
        GetCitiesResponse.City emptyCity = new GetCitiesResponse().new City(0, "Select City");
        mArrCities.add(0, emptyCity);
        ArrayAdapter<GetCitiesResponse.City> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, mArrCities);
        mSpnCity.setAdapter(adapter);
        mSpnCity.setVisibility(View.VISIBLE);
        mSpnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    callGetAreaService(String.valueOf(mArrCities.get(position).getID()));
                    mSpnArea.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setAreaSpinner(GetAreasResponse mAreaResponse) {
        mArrAreas = mAreaResponse.getAreas();
        GetAreasResponse.Area emptyCity = new GetAreasResponse().new Area(0, "Select Area");
        mArrAreas.add(0, emptyCity);
        ArrayAdapter<GetAreasResponse.Area> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, mArrAreas);
        mSpnArea.setAdapter(adapter);
        mSpnArea.setVisibility(View.VISIBLE);
    }

    private void callGetCityService(int regionId, int soID) {
        mDialog.show();
        mDialog.setMessage("Fetching cities please wait...");
        mService = RestClient.getInstance(getActivity());
        Call<GetCitiesResponse> userObject = mService.getCities(regionId,soID);
        RestCallbackObject callbackObject = new RestCallbackObject(getActivity(), this, RequestCode.GET_CITIES_REQUEST_CODE).showProgress(true, 0).dontHideProgress(false);
        userObject.enqueue(callbackObject);
    }

    private void callGetAreaService( String cityId) {
        mDialog.show();
        mDialog.setMessage("Fetching areas please wait...");
        mService = RestClient.getInstance(mContext);
        Call<GetAreasResponse> userObject = mService.getAreas(cityId,mLoginResponse.getData().getSOID());
        RestCallbackObject callbackObject = new RestCallbackObject(getActivity(), this, RequestCode.GET_AREA_REQUEST_CODE).showProgress(true, 0).dontHideProgress(false);
        userObject.enqueue(callbackObject);
    }

    private void setShopTypeSpinner() {
        mArrShopType = new ArrayList<>();
        mArrShopType.add("Select Shop Type");
        mArrShopType.add("Book Shop");
        mArrShopType.add("Gift Shop");
        mArrShopType.add("Both");
        adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, mArrShopType);
        mSpShopType.setAdapter(adapter);
    }


    public void openCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_INTENT);
    }
    public void openGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_INTENT);
    }
    public void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(mContext);
        pictureDialog.setTitle("Select Action");
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
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
        }
        else if(requestCode==GALLERY_INTENT){
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
    public void onFailure(String error, RequestCode requestCode) {
        if (requestCode == RequestCode.SHOP_REG_REQUEST_CODE) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
        } else if (requestCode == RequestCode.GET_CITIES_REQUEST_CODE) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
            Toast.makeText(mContext, "Failed-- " + error, Toast.LENGTH_SHORT).show();
        } else if (requestCode == RequestCode.GET_AREA_REQUEST_CODE) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
            Toast.makeText(mContext, "Failed-- " + error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSuccess(Object object, RequestCode requestCode) {
        if (requestCode == RequestCode.SHOP_REG_REQUEST_CODE) {
            ServerResponse serverResponse = (ServerResponse) object;
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
            if (serverResponse.getResultType() == ServerCodes.ServerResultType.SERVER_RESULT_SUCCESS)
                Toast.makeText(mContext, "Form Successfully submitted.", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(mContext, serverResponse.getMessage(), Toast.LENGTH_LONG).show();

            getActivity().finish();
        } else if (requestCode == RequestCode.GET_CITIES_REQUEST_CODE) {
            if (mDialog.isShowing()) {
                mDialog.hide();
            }
            mCitiesResponse = (GetCitiesResponse) object;
            setCitySpinner(mCitiesResponse);
        } else if (requestCode == RequestCode.GET_AREA_REQUEST_CODE) {
            mAreaResponse = (GetAreasResponse) object;
            if (mDialog.isShowing()) {
                mDialog.hide();
            }
            mAreaResponse = (GetAreasResponse) object;
            setAreaSpinner(mAreaResponse);
        }
    }

    private void setShopReq(ShopRegRequest obj) {
       // obj.setTypeOfShop(mSpShopType.getSelectedItem().toString());
        obj.setShopName(mEtShopName.getText().toString());
        obj.setOwnerName(mEtOwnerName.getText().toString());
        obj.setContactNo(mEtContNum.getText().toString());
        obj.setPhone2(mEtCellNum.getText().toString());
        obj.setEmail(mEtEmail.getText().toString());
        obj.setLattitude(DashboardActivity.mLocationService.getLatitude());
        obj.setLongitude(DashboardActivity.mLocationService.getLongitude());
        obj.setAddress(mEtAddress.getText().toString());
        obj.setToken(mLoginResponse.getData().getToken());
        obj.setSalesOfficerID(mLoginResponse.getData().getSOID());
        obj.setContactPerson(mEtContPerson.getText().toString());
        obj.setContactPersonCellNo(mEtOtherContNum.getText().toString());
        obj.setRegionId(mArrRegions.get(mSpnRegion.getSelectedItemPosition()).getID());
        obj.setCityID(mArrCities.get(mSpnCity.getSelectedItemPosition()).getID());
        obj.setAreaID(mArrAreas.get(mSpnArea.getSelectedItemPosition()).getID());
        obj.setZoneID(1);
        obj.setRemarks(mEtRemarks.getText().toString());
        obj.setPicture1(getStringImage(bitmap));
//        obj.setVerified();
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
       /* if (mShopRegReq.getTypeOfShop().trim().length() <= 0) {
            showToast("Type of shop");
            return false;
        } else*/
        if (mShopRegReq.getShopName().trim().length() <= 0) {
            showToast("Shop name");
            return false;
        } else if (mShopRegReq.getOwnerName().trim().length() <= 0) {
            showToast("Owner name");
            return false;
        } else if (mSpnRegion.getSelectedItemPosition() <= 0) {
            showToast("Region");
            return false;
        } else if (mSpnCity.getSelectedItemPosition() <= 0) {
            showToast("City");
            return false;
        } else if (mSpnArea.getSelectedItemPosition() <= 0) {
            showToast("Area");
            return false;
        } else if (mShopRegReq.getContactNo().length() <= 0) {
            showToast("Contact no.");
            return false;
        } else if (mShopRegReq.getAddress().trim().length() <= 0) {
            showToast("Address");
            return false;
        } else if (mShopRegReq.getContactPersonCellNo().trim().length() <= 0) {
            showToast("Contact cell no.");
            return false;
        } else if (DashboardActivity.mLocationService.getLatitude() == 0.0) {
            DashboardActivity.mLocationService.getLocation();
            showToast("Can't get your Location.");
            return false;
        }
        return true;
    }
    private void showToast(String msg) {
        Toast.makeText(mContext, msg + " is required", Toast.LENGTH_SHORT).show();
    }
    private void callRegService() {
        mDialog.show();
        mDialog.setMessage("Submitting form, please wait...");
        mService = RestClient.getInstance(mContext);
        Call<ServerResponse> userObject = mService.regShop(mShopRegReq);
        RestCallbackObject callbackObject = new RestCallbackObject(getActivity(), this, RequestCode.SHOP_REG_REQUEST_CODE).showProgress(true, 0).dontHideProgress(false);
        userObject.enqueue(callbackObject);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                mShopRegReq = new ShopRegRequest();

                setShopReq(mShopRegReq);
                if (Utility.isNetworkAvailable(mContext)) {
                    mTvLat.setText(Double.toString(DashboardActivity.mLocationService.getLatitude()));
                    mTvLong.setText(Double.toString(DashboardActivity.mLocationService.getLongitude()));
                    if (validateForm())
                        callRegService();
                } else {
                    Toast.makeText(mContext, getString(R.string.str_no_internet), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.iv_capture:
                ((FragmentShownActivity) getActivity()).openCamera(this);
                break;
        }
    }
}
