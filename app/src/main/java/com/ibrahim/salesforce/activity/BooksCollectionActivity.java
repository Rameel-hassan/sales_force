package com.ibrahim.salesforce.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ibrahim.salesforce.R;
import com.ibrahim.salesforce.adapters.BooksCollectionAdapter;
import com.ibrahim.salesforce.network.ApiService;
import com.ibrahim.salesforce.network.RequestCode;
import com.ibrahim.salesforce.network.RestCallbackObject;
import com.ibrahim.salesforce.network.RestClient;
import com.ibrahim.salesforce.network.ServerConnectListenerObject;
import com.ibrahim.salesforce.response.BooksSampleInfo;
import com.ibrahim.salesforce.response.GetBooksCollectionResponse;
import com.ibrahim.salesforce.response.GetServerResponse;
import com.ibrahim.salesforce.utilities.AppKeys;

import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;

/**
 * @author Rameel Hassan
 * Created 24/09/2022 at 7:31 pm
 */
public class BooksCollectionActivity extends AppCompatActivity implements ServerConnectListenerObject {

    RecyclerView recyclerView;
    ProgressBar mProgressBar;
    TextView mTextViewEmptyMsg;



    private ApiService mService;

    List<BooksSampleInfo> bookDetailList;
    BooksCollectionAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_collection);
        setUi();
    }


    private void setUi() {
        recyclerView = findViewById(R.id.recycler_view_books_collection);
        mProgressBar = findViewById(R.id.progress_circular);
        mTextViewEmptyMsg = findViewById(R.id.text_view_response_msg);
    }
    private void callGetUserAssignedSample(int UserId) {
        mProgressBar.setVisibility(View.VISIBLE);
        mService = RestClient.getInstance(this);
        Call<GetBooksCollectionResponse> userObject = mService.getUserAssignedSample(UserId);
        RestCallbackObject callbackObject = new RestCallbackObject(this, this, RequestCode.API_GET_USER_ASSIGNED_SAMPLE);
        userObject.enqueue(callbackObject);
    }
    @Override
    protected void onResume() {
        super.onResume();
        GetServerResponse mLoginResponse = Paper.book().read(AppKeys.KEY_LOGIN_RESPONSE);
        callGetUserAssignedSample(mLoginResponse.getData().getSOID());
//                callGetUserAssignedSample(7);

    }

    @Override
    public void onFailure(String error, RequestCode requestCode) {
        mProgressBar.setVisibility(View.INVISIBLE);
        mTextViewEmptyMsg.setText(error);


    }
    @Override
    public void onSuccess(Object object, RequestCode requestCode) {
        mProgressBar.setVisibility(View.INVISIBLE);


        if (requestCode == RequestCode.API_GET_USER_ASSIGNED_SAMPLE) {
            GetBooksCollectionResponse response = (GetBooksCollectionResponse) object;
            if (response.getAssignedSampleInfo().size() > 0) {
                bookDetailList = response.getAssignedSampleInfo();
                showDataInRecylerView(bookDetailList);
            }else {
                mTextViewEmptyMsg.setText("No Data Found");
            }
        }

    }

    private void showDataInRecylerView(List<BooksSampleInfo> bookDetailList) {
        adapter = new BooksCollectionAdapter(bookDetailList, new BooksCollectionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(getApplicationContext(),"clicked "+position,Toast.LENGTH_SHORT).show();
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                BooksCollectionDetailsFragment booksCollectionDetailsFragment = new BooksCollectionDetailsFragment(bookDetailList.get(position).getSubjectID(),bookDetailList.get(position).getSubjectName());
                transaction.add(R.id.frameLayout, booksCollectionDetailsFragment,null).addToBackStack("true").commit();

            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}
