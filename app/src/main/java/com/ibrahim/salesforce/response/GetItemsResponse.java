package com.ibrahim.salesforce.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetItemsResponse {
    @SerializedName("Items")
    @Expose
    private List<Item> items = null;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public class Item {

        @SerializedName("ItemId")
        @Expose
        private int itemId;
        @SerializedName("ItemName")
        @Expose
        private String itemName;
        @SerializedName("ItemPrice")
        @Expose
        private double price;
        private boolean isChecked;

        public Item(int itemId, String itemName) {
            this.itemId = itemId;
            this.itemName = itemName;
        }

        public int getItemId() {
            return itemId;
        }

        public void setItemId(int itemId) {
            this.itemId = itemId;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }
    }
}
