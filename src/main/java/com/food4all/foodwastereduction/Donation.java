package com.food4all.foodwastereduction;

import android.net.Uri;

import java.util.Map;

public class Donation {
    private String itemName, donorEmail, receiverEmail, donorCity, description, expiryDate, itemID;
    private String imageFirebaseURL;
    private int price, status;
    public static int AVAILABLE = 0, REQUESTED = 1, RECEIVED = 2, INVALID = -1, FREE = 0;

    public Donation() {
    }

    public Donation(String itemName, String donorEmail, String receiverEmail, String donorCity, String imageFirebaseURL, String description, int status, String expiryDate, String itemID, int price) {
        this.itemName = itemName;
        this.donorEmail = donorEmail;
        this.receiverEmail = receiverEmail;
        this.donorCity = donorCity;
        this.imageFirebaseURL = imageFirebaseURL;
        this.description = description;
        this.status = status;
        this.expiryDate = expiryDate;
        this.price = price;
        this.itemID = itemID;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDonorEmail() {
        return donorEmail;
    }

    public void setDonorEmail(String donorEmail) {
        this.donorEmail = donorEmail;
    }

    public String getDonorCity() {
        return donorCity;
    }

    public void setDonorCity(String donorCity) {
        this.donorCity = donorCity;
    }

    public String getImageFirebaseURL() {
        return imageFirebaseURL;
    }

    public void setImageFirebaseURL(String imageFirebaseURL) {
        this.imageFirebaseURL = imageFirebaseURL;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
