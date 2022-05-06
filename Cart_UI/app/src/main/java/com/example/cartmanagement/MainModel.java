package com.example.cartmanagement;

public class MainModel {

    String name, price, quantity,mImageUrls;

    public MainModel() {
    }

    public MainModel(String name, String price, String quantity, String mImageUrls) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.mImageUrls = mImageUrls;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getmImageUrls() {
        return mImageUrls;
    }

    public void setmImageUrls(String mImageUrls) {
        this.mImageUrls = mImageUrls;
    }
}
