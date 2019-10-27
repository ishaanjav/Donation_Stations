package com.example.anany.restaurantleftoverfood.Storage;

public class ShelterInfo {
    private String name;
    private String managerName;
    private String contactPhone;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String address;
    private String website;
    private String openingHours;
    private String distance;
    private String units;
    boolean same;
    String type;
    double latitude, longitude;
    double dis;
    boolean inMap;
    String status;
    String amount;

    public double getLat() {
        return latitude;
    }
    public double getLon() {
        return longitude;
    }
    public boolean isSame() {
        return same;
    }

    public double getDis() {
        return dis;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSame(boolean same) {
        this.same = same;
    }

    public String getWebsite() {
        return website;
    }

    public String getType() {
        return type;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public boolean isInMap() {
        return inMap;
    }

    public void setInMap(boolean inMap) {
        this.inMap = inMap;
    }

    public ShelterInfo(String shelterName, String managerName, String contactPhone, String address, String website,
                       String openingHours, String distance, String units, boolean same, String t, double d, double lat, double lon, boolean map, String s, String a) {
        name = shelterName;
        this.managerName = managerName;
        this.contactPhone = contactPhone;
        this.address = address;
        this.website = website;
        this.openingHours = openingHours;
        this.distance = distance;
        this.units = units;
        this.same = same;
        this.type = t;
        dis = d;
        latitude = lat;
        longitude = lon;
        inMap = map;
        status = s;
        amount = a;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
