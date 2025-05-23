package com.example.restaurantfinder.Model;

public class Mekan {
    private String ad;
    private String adres;
    private double enlem;
    private double boylam;
    private double mesafe;

    public Mekan(String ad, String adres, double enlem, double boylam, double mesafe) {
        this.ad = ad;
        this.adres = adres;
        this.enlem = enlem;
        this.boylam = boylam;
        this.mesafe = mesafe;
    }

    // Getter ve Setter metodları
    public String getAd() { return ad; }
    public String getAdres() { return adres; }
    public double getEnlem() { return enlem; }
    public double getBoylam() { return boylam; }
    public double getMesafe() { return mesafe; }
}