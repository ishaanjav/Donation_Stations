package com.example.anany.restaurantleftoverfood;

public class RestaurantInfo {
    private String name;
    private String managerName;
    private String contactPhone;
    private String address;
    private String closingTime;

    public RestaurantInfo(String shelterName, String managerName, String contactPhone, String address, String closingTime) {
        name = shelterName;
        this.managerName = managerName;
        this.contactPhone = contactPhone;
        this.address = address;
        this.closingTime = closingTime;
    }

    public String getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(String closingTime) {
        this.closingTime = closingTime;
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
