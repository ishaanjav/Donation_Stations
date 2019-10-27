package com.example.anany.restaurantleftoverfood.Storage;

public class Leaderboard {

    String name, donationPoints;

    public Leaderboard(String name, String points){
        this.name = name;
        donationPoints = points;
    }

    public String getDonationPoints() {
        return donationPoints;
    }

    public String getName() {return name;}
}
