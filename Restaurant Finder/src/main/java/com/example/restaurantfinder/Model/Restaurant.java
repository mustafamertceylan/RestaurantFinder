package com.example.restaurantfinder.Model;

import java.util.Objects;

public class Restaurant {
    private String name;
    private String address;
    private String type; // "cafe" veya "restaurant"
    private double rating; // 0.0 - 5.0 arası
    private int reviewCount;
    private double distance; // km cinsinden
    private double latitude;
    private double longitude;
    private String photoUrl;

    // Constructor
    public Restaurant(String name, String address, String type,
                      double rating, int reviewCount,
                      double distance, double latitude,
                      double longitude, String photoUrl) {
        this.name = name;
        this.address = address;
        this.type = type;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.distance = distance;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photoUrl = photoUrl;
    }

    // Getter ve Setter metodları
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }

    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    @Override
    public String toString() {
        return String.format("%s (%s) - %.1f km", name, type, distance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Restaurant)) return false;
        Restaurant that = (Restaurant) o;
        return Double.compare(that.latitude, latitude) == 0 &&
                Double.compare(that.longitude, longitude) == 0 &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, latitude, longitude);
    }
}
