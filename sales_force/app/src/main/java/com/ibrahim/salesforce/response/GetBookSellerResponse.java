package com.app.salesforce.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetBookSellerResponse extends ServerResponse {
    @SerializedName("Shops")
    @Expose
    private List<BookSeller> BookSellers = null;

    public List<BookSeller> getBookSellers() {
        return BookSellers;
    }

    public void setBookSellers(List<BookSeller> bookSellers) {
        BookSellers = bookSellers;
    }

    public class BookSeller {
        @SerializedName("ShopID")
        @Expose
        private int SellerId;
        @SerializedName("ShopName")
        @Expose
        private String SellerName;
        private boolean isChecked;


        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }

        public int getSellerId() {
            return SellerId;
        }

        public void setSellerId(int sellerId) {
            SellerId = sellerId;
        }

        public String getSellerName() {
            return SellerName;
        }

        public void setSellerName(String sellerName) {
            SellerName = sellerName;
        }


    }
}
