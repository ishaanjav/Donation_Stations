package com.example.anany.restaurantleftoverfood.Storage;

public  class Singleton {
    private static boolean showInMap = false;
    static String name, phone, hours, address, type, website;

    public static String getWebsite() {
        return website;
    }

    public static void setWebsite(String website) {
        Singleton.website = website;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String nae) {
        name = nae;
    }

    public static String getPhone() {
        return phone;
    }

    public static void setPhone(String phne) {
        phone = phne;
    }

    public static String getHours() {
        return hours;
    }

    public static void setHours(String hors) {
        hours = hors;
    }

    public static String getAddress() {
        return address;
    }

    public static void setAddress(String addess) {
        address = addess;
    }

    public static String getType() {
        return type;
    }

    public static void setType(String tpe) {
        type = tpe;
    }

    public static double getLat() {
        return lat;
    }

    public static void setLat(double lat) {
        Singleton.lat = lat;
    }

    public static double getLon() {
        return lon;
    }

    public static void setLon(double lon) {
        Singleton.lon = lon;
    }

    private static double lat = 0;
    private static double lon = 0;
    protected Singleton(boolean t){
        showInMap = t;
    }

    public static boolean isShowInMap() {
        return showInMap;
    }

    public static void setShowInMap(boolean showInMap) {
        Singleton.showInMap = showInMap;
    }
}
