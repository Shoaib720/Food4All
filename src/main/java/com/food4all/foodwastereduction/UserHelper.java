package com.food4all.foodwastereduction;

public class UserHelper {

    private String name, contact, email, about, dob, gender, district, userType;

    public UserHelper() {}

    public UserHelper(String name, String contact, String email, String about, String dob, String gender, String district, String userType) {
        this.name = name;
        this.contact = contact;
        this.email = email;
        this.about = about;
        this.dob = dob;
        this.gender = gender;
        this.district = district;
        this.userType = userType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
