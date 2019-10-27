package com.example.anany.restaurantleftoverfood;

class MarkerInfo {
    private String name, type2, address2, phone, hours, website;
    private double latT, lonT;
    String status;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    String amount;

    public String getStatus(){
        return status;
    }
    public void setStatus(String s){
        this.status = s;
    }

    public MarkerInfo(String name, String type2, double latT, double lonT, String address2, String phone, String hours, String website, String s, String a) {
        this.name = name;
        this.type2 = type2;
        this.latT = latT;
        this.lonT = lonT;
        this.address2 = address2;
        this.phone = phone;
        this.hours = hours;
        this.website = website;
        this.status = s;
        this.amount = a;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType2() {
        return type2;
    }

    public void setType2(String type2) {
        this.type2 = type2;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public double getLatT() {
        return latT;
    }

    public void setLatT(double latT) {
        this.latT = latT;
    }

    public double getLonT() {
        return lonT;
    }

    public void setLonT(double lonT) {
        this.lonT = lonT;
    }


}
