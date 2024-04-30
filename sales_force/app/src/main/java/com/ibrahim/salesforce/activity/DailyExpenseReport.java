package com.app.salesforce.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.salesforce.R;
import com.app.salesforce.network.ApiService;
import com.app.salesforce.network.RequestCode;
import com.app.salesforce.network.RestCallbackObject;
import com.app.salesforce.network.RestClient;
import com.app.salesforce.network.ServerCodes;
import com.app.salesforce.network.ServerConnectListenerObject;
import com.app.salesforce.response.GetServerResponse;
import com.app.salesforce.response.SalesOfficer;
import com.app.salesforce.utilities.AppKeys;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;

public class DailyExpenseReport extends AppCompatActivity implements ServerConnectListenerObject {
    Button btnDownloadMail;
    ApiService mService;
    Spinner spnDisributor, spnSoDropDown;
    TextView dateFrom, dateTo;
    Calendar myCalendar;
    String select_date;
    String reportStatus;
    ProgressDialog progressDialog;
    String type;
    private DatePickerDialog.OnDateSetListener date;
    private Spinner mSpnMainCat;
    private List<String> mArrMainCat;
    int salesofficerID;
    ArrayAdapter<SalesOfficer> SalesOfficerArrayAdapter;
    private GetServerResponse mLoginResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("class_name", this.getClass().getSimpleName());
        setContentView(R.layout.activity_daily_expense_report);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading......");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        myCalendar = Calendar.getInstance();
        mLoginResponse = Paper.book().read(AppKeys.KEY_LOGIN_RESPONSE);
       // mSpnMainCat = findViewById(R.id.spn_main_cat);
        spnSoDropDown = findViewById(R.id.spnSoDropDown);
        dateFrom = findViewById(R.id.tv_from_visit_date);
        dateTo = findViewById(R.id.tv_to_visit_date);
        btnDownloadMail = findViewById(R.id.btnDownload);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Daily Expense Report");
        startDatePicker();
        String date_n = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        dateFrom.setText(date_n);
        dateTo.setText(date_n);
        dateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select_date = "date from";
                new DatePickerDialog(DailyExpenseReport.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        dateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select_date = "date To";
                new DatePickerDialog(DailyExpenseReport.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        setSoSpinner();
        btnDownloadMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onFailure(String error, RequestCode requestCode) {
        progressDialog.cancel();
        Toast.makeText(this, "Error" + error, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onSuccess(Object object, RequestCode requestCode) {
        if (requestCode == RequestCode.POST_EXCEL_SHEETS) {
            GetServerResponse getServerResponse = (GetServerResponse) object;
            if (getServerResponse.getResultType() == ServerCodes.ServerResultType.SERVER_RESULT_SUCCESS) {
                progressDialog.cancel();
                btnDownloadMail.setEnabled(true);
                if (getServerResponse.getData().getDownloadPdfUrl() != null) {
                    Uri uri = Uri.parse(getServerResponse.getData().getDownloadPdfUrl()); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } else if (getServerResponse.getData().getDownloadPdfUrl() == null)
                    Toast.makeText(this, reportStatus + " Report Not Found", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), getServerResponse.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }

    }

    List<SalesOfficer> salesOfficers;

    private void setSoSpinner() {
        salesOfficers = mLoginResponse.getData().getSalesOfficer();
        //distributorRelatedToSOS =     getDistributorResponse.g.getdistributor();
        SalesOfficerArrayAdapter = new ArrayAdapter<SalesOfficer>(this, android.R.layout.simple_spinner_dropdown_item, salesOfficers);
        spnSoDropDown.setAdapter(SalesOfficerArrayAdapter);
        spnSoDropDown.setVisibility(View.VISIBLE);
        spnSoDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                salesofficerID = salesOfficers.get(spnSoDropDown.getSelectedItemPosition()).getID();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    public void sendRequest() {
        {
            mService = RestClient.getInstance(DailyExpenseReport.this);
            //Call<GetServerResponse> userObject = mService.getOrderExcel(distributorRelatedToSOS.get(spnDisributor.getSelectedItemPosition()).getID(),etEmail.getText().toString(), type,dateFrom.getText().toString(),dateTo.getText().toString(),mLoginResponse.getData().getSOID(),reportStatus,mArrMainCat.get(mSpnMainCat.getSelectedItemPosition()).getMainCategID());
            Call<GetServerResponse> userObject = mService.getOrderExcel(dateFrom.getText().toString(), dateTo.getText().toString(), salesOfficers.get(spnSoDropDown.getSelectedItemPosition()).getID());
            RestCallbackObject callbackObject = new RestCallbackObject((Activity) DailyExpenseReport.this, this, RequestCode.POST_EXCEL_SHEETS).showProgress(true, 0).dontHideProgress(false);
            userObject.enqueue(callbackObject);
//            btnDownloadMail.setEnabled(false);


        }
    }
   /* private boolean isValidEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
    public static final Pattern EMAIL_ADDRESS
            = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
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
    }

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd "; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        if (select_date == "date from")
            dateFrom.setText(sdf.format(myCalendar.getTime()));
        else
            dateTo.setText(sdf.format(myCalendar.getTime()));

    }


}
