package com.app.salesforce.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.app.salesforce.R;
import com.app.salesforce.utilities.AppKeys;
import com.app.salesforce.utilities.AppPreference;

import java.util.ArrayList;
import java.util.Arrays;

public class UtilityActivity extends AppCompatActivity implements View.OnClickListener {
    private Context mContext;
    ArrayList<String> mArrBaseURL = new ArrayList<>();
    private AutoCompleteTextView mMultiAutoCompleteTextView;
    private Button mBtnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utility);
        mContext = this;
        mArrBaseURL = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.arr_url)));
        initGuiComponents();
        setBaseURLAdapter();
        Log.d("class_name", this.getClass().getSimpleName());
    }

    private void initGuiComponents() {
        mMultiAutoCompleteTextView = findViewById(R.id.multiAutoCompleteTextView);
        mBtnUpdate = findViewById(R.id.btn_update);
        mBtnUpdate.setOnClickListener(this);
    }

    private void setBaseURLAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.autocomplete_textview, mArrBaseURL);
        mMultiAutoCompleteTextView.setAdapter(adapter);
        mMultiAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMultiAutoCompleteTextView.setText(mMultiAutoCompleteTextView.getText().toString().trim() + "");
                mMultiAutoCompleteTextView.setSelection(mMultiAutoCompleteTextView.getText().toString().length());
            }
        });
    }
    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update:
                AppPreference.saveValue(mContext, mMultiAutoCompleteTextView.getText().toString(), AppKeys.KEY_BASE_URL);
                int id = android.os.Process.myPid();
                android.os.Process.killProcess(id);
                break;
        }
    }
}
