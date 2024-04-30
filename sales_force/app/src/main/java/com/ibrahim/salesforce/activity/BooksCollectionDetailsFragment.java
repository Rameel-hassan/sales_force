package com.app.salesforce.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.salesforce.R;
import com.app.salesforce.adapters.BooksCollectionDetailsAdapter;
import com.app.salesforce.network.ApiService;
import com.app.salesforce.network.RequestCode;
import com.app.salesforce.network.RestCallbackObject;
import com.app.salesforce.network.RestClient;
import com.app.salesforce.network.ServerConnectListenerObject;
import com.app.salesforce.response.BooksSampleInfo;
import com.app.salesforce.response.GetBooksCollectionResponse;
import com.app.salesforce.response.GetServerResponse;
import com.app.salesforce.utilities.AppKeys;

import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;

/**
 * @author Rameel Hassan
 * Created 24/09/2022 at 7:31 pm
 */
public class BooksCollectionDetailsFragment extends Fragment implements ServerConnectListenerObject {

    RecyclerView recyclerView;
    ProgressBar mProgressBar;
    TextView mTextViewEmptyMsg,mTextViewSubjectName;



    private ApiService mService;

    List<BooksSampleInfo> bookDetailList;
    BooksCollectionDetailsAdapter adapter;

    int SubjectID;
    String SubjectName;
    public BooksCollectionDetailsFragment(int SubjectID,String subjectName) {
        this.SubjectID = SubjectID;
        this.SubjectName = subjectName;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_books_collection_details, container, false);
        setUi(view);

        return view;

    }




    private void setUi(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_books_collection);
        mProgressBar = view.findViewById(R.id.progress_circular);
        mTextViewEmptyMsg = view.findViewById(R.id.text_view_response_msg);
        mTextViewSubjectName = view.findViewById(R.id.textview_subject);
    }
    private void callGetUserAssignedSample(int UserId) {
        mProgressBar.setVisibility(View.VISIBLE);
        mService = RestClient.getInstance(getActivity());
        Call<GetBooksCollectionResponse> userObject = mService.getUserAssignedSampleDetail(UserId,SubjectID);
        RestCallbackObject callbackObject = new RestCallbackObject(getActivity(), this, RequestCode.API_GET_USER_ASSIGNED_SAMPLE_DETAILS);
        userObject.enqueue(callbackObject);
    }
    @Override
    public void onResume() {
        super.onResume();
        mTextViewSubjectName.setText(SubjectName);

        GetServerResponse mLoginResponse = Paper.book().read(AppKeys.KEY_LOGIN_RESPONSE);
        callGetUserAssignedSample(mLoginResponse.getData().getSOID());
//        callGetUserAssignedSample(7);

    }

    @Override
    public void onFailure(String error, RequestCode requestCode) {
        mProgressBar.setVisibility(View.INVISIBLE);
        mTextViewEmptyMsg.setText(error);


    }
    @Override
    public void onSuccess(Object object, RequestCode requestCode) {
        mProgressBar.setVisibility(View.INVISIBLE);


        if (requestCode == RequestCode.API_GET_USER_ASSIGNED_SAMPLE_DETAILS) {
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
        adapter = new BooksCollectionDetailsAdapter(bookDetailList);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }
}
