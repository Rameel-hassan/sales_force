package com.ibrahim.salesforce.model;

public class SelectedItemsModel {
    private int ItemID;
    private String itemName;
    private double Price;
    private int Quantity;

    public SelectedItemsModel(int ItemID, String itemName, double Price, String Quantity) {
        this.ItemID = ItemID;
        this.itemName = itemName;
        this.Price = Price;
        this.Quantity = Integer.valueOf(Quantity);
    }


    public int getItemId() {
        return ItemID;
    }

    public void setItemId(int ItemID) {
        this.ItemID = ItemID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getItemPrice() {
        return Price;
    }

    public void setItemPrice(double Price) {
        this.Price = Price;
    }

    public int getQty() {
        return Quantity;
    }

    public void setQty(int Quantity) {
        this.Quantity = Quantity;
    }
}
//    private List<Item> items = null;

//    public List<Item> getItems() {
//        return items;
//    }

//    public void setItems(List<Item> items) {
//        this.items = items;
//    }

//    public class Item {
//
//        private int ItemID;
//        private String itemName;
//        private double Price;
//        private int Quantity;
//
//        public Item(int ItemID, String itemName, double Price, int Quantity) {
//            this.ItemID = ItemID;
//            this.itemName = itemName;
//            this.Price = Price;
//            this.Quantity = Quantity;
//        }
//
//        public int getItemId() {
//            return ItemID;
//        }
//
//        public void setItemId(int ItemID) {
//            this.ItemID = ItemID;
//        }
//
//        public String getItemName() {
//            return itemName;
//        }
//
//        public void setItemName(String itemName) {
//            this.itemName = itemName;
//        }
//
//        public double getItemPrice() {
//            return Price;
//        }
//
//        public void setItemPrice(double Price) {
//            this.Price = Price;
//        }
//
//        public int getQty() {
//            return Quantity;
//        }
//
//        public void setQty(int Quantity) {
//            this.Quantity = Quantity;
//        }
//    }
//}