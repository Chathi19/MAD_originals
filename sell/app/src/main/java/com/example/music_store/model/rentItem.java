package com.example.music_store.model;

public class rentItem {

    private String rentItemId;
    private String itemName;
    private String avalability;
    private String price_pre_day;
    private String description;
    private String image_data;


    public rentItem() {
    }

    public String getRentItemId() {
        return rentItemId;
    }

    public void setRentItemId(String rentItemId) {
        this.rentItemId = rentItemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getAvalability() {
        return avalability;
    }

    public void setAvalability(String avalability) {
        this.avalability = avalability;
    }

    public String getPrice_pre_day() {
        return price_pre_day;
    }

    public void setPrice_pre_day(String price_pre_day) {
        this.price_pre_day = price_pre_day;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage_data() {
        return image_data;
    }

    public void setImage_data(String image_data) {
        this.image_data = image_data;
    }
}
