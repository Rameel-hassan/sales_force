package com.app.salesforce.adapters;

import com.app.salesforce.model.SelectedClassItem;
import com.app.salesforce.model.SelectedItemsModel;
import com.app.salesforce.response.Clas;
import com.app.salesforce.response.Competitor;
import com.app.salesforce.response.CurrentSyllabus;
import com.app.salesforce.response.CustomersRelatedtoSO;
import com.app.salesforce.response.GetBookSellerResponse;
import com.app.salesforce.response.Sery;
import com.app.salesforce.response.Subjects;

public interface AddRemoveItem {
    void addRemoveItem(SelectedItemsModel item, boolean isAdd, int position);

    void uncheckupdate(SelectedItemsModel item, boolean isChecked);

    interface AddRemoveSyllabus {
        void addRemoveItem(CurrentSyllabus item, boolean isAdd);
    }

    interface AddRemoveDealer {
        void addRemoveItem(CustomersRelatedtoSO item, boolean isAdd);
    }
    interface AddRemoveBookSellers{
        void addRemoveItem(GetBookSellerResponse.BookSeller item, boolean isAdd);
    }

    interface AddRemoveClass{
        void addRemoveItem(Clas clas,boolean isChecked,int position);
    }

    interface AddSubjClass{
       void addRemoveSelectedItem(Subjects subject, Clas clas, boolean isChecked,int absoluteAdapterPosition);
    }


    interface AddRemoveSchoolItems{

        void addRemoveSchoolItem(SelectedClassItem item, boolean isAdd,int position);
        void uncheckSchoolItemupdate(SelectedClassItem item,boolean isChecked);
        void updateReturnStatus(int position,boolean status);
        void updateDeliveredStatus(int position, boolean status);
        void updateSeries(int position,Sery series);
    }
    /*interface AddRemoveInterestedBook{
        void AddRemoveInterestedBook(Sery item, boolean isAdd);
        void uncheckInterestedBookUpdate(Sery item, boolean isChecked);
    }*/
    interface AddRemovePublishers{
        void AddRemovePublishers(Competitor item, boolean isAdd);
        void uncheckPublisher(Competitor item, boolean isChecked);
    }

}
