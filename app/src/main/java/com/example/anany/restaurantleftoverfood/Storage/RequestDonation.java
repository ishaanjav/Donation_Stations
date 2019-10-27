package com.example.anany.restaurantleftoverfood.Storage;

public class RequestDonation {
    private String name;
    private String managerName;
    private String contactPhone;

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String donations;

    public String getNumberofDonations() {
        return donations;
    }

    private String address;
    private String website;
    private String openingHours;
    private String distance;
    private String units;
    boolean same;
    String type;
    double latitude, longitude;
    String dis;
    boolean inMap;
    String rating;
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

    public String getDis() {
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

    double toLat, toLon;

    public double getToLat() {
        return toLat;
    }

    public double getToLon() {
        return toLon;
    }

    String shelterVC, restaurantVC;

    public String getShelterVC() {
        return shelterVC;
    }

    public String getRestaurantVC() {
        return restaurantVC;
    }

    String currentAddress, currentName, currentPhone, currentWebsite, currentManager, currentHours, status;

    public void setShelterVC(String shelterVC) {
        this.shelterVC = shelterVC;
    }

    public void setRestaurantVC(String restaurantVC) {
        this.restaurantVC = restaurantVC;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    public String getCurrentName() {
        return currentName;
    }

    public void setCurrentName(String currentName) {
        this.currentName = currentName;
    }

    public String getCurrentPhone() {
        return currentPhone;
    }

    public void setCurrentPhone(String currentPhone) {
        this.currentPhone = currentPhone;
    }

    public String getCurrentWebsite() {
        return currentWebsite;
    }

    public void setCurrentWebsite(String currentWebsite) {
        this.currentWebsite = currentWebsite;
    }

    public String getCurrentManager() {
        return currentManager;
    }

    public void setCurrentManager(String currentManager) {
        this.currentManager = currentManager;
    }

    public String getCurrentHours() {
        return currentHours;
    }

    public void setCurrentHours(String currentHours) {
        this.currentHours = currentHours;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public RequestDonation(String name, String managerName, String contactPhone, String address, String website,
                           String openingHours, String distance, String units, boolean same, double lat, double lon, double toLat, double toLon,
                           String rating, String number, String shelterVC, String restaurantVC, String cAddress, String cName, String cPhone,
                           String cWebsite, String cManager, String cHours, String cStatus, String amount) {
        this.name = name;
        this.managerName = managerName;
        this.contactPhone = contactPhone;
        this.address = address;
        this.website = website;
        this.openingHours = openingHours;
        this.distance = distance;
        this.units = units;
        this.same = same;
        latitude = lat;
        dis = distance;
        this.toLat = toLat;
        this.toLon = toLon;
        longitude = lon;
        this.rating = rating;
        this.shelterVC = shelterVC;
        this.restaurantVC = restaurantVC;
        this.currentAddress = cAddress;
        this.currentName = cName;
        currentPhone = cPhone;
        currentWebsite = cWebsite;
        currentManager = cManager;
        currentHours = cHours;
        status = cStatus;
        donations = number;
        this.amount = amount;
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
