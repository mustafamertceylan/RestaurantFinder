package com.example.restaurantfinder_part3_;

public class Place {
    private String id;
    private String name;
    private String type;
    private double latitude;
    private double longitude;
    private String vicinity;
    private double rating;
    private int totalRatings;
    private String photoUrl;
    private boolean openNow;
    private Double distance; // Double olarak değiştirildi (null olabilir)
    private String duration;

    // Full Constructor (HTML'den gelen tüm veriler için)
    public Place(String id, String name, String type, double latitude, double longitude,
                 String vicinity, double rating, int totalRatings, String photoUrl,
                 boolean openNow, Double distance, String duration) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.vicinity = vicinity;
        this.rating = rating;
        this.totalRatings = totalRatings;
        this.photoUrl = photoUrl;
        this.openNow = openNow;
        this.distance = distance;
        this.duration = duration;
    }

    // Minimal Constructor (Eski versiyonla uyumluluk için)
    public Place(String id, String name, String type, double latitude, double longitude,
                 String vicinity, double rating, int totalRatings, String photoUrl,
                 boolean openNow) {
        this(id, name, type, latitude, longitude, vicinity, rating,
                totalRatings, photoUrl, openNow, null, null);
    }

    // Default constructor
    public Place() {}

    // Getter & Setter'lar
    public String getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getVicinity() { return vicinity; }
    public double getRating() { return rating; }
    public int getTotalRatings() { return totalRatings; }
    public String getPhotoUrl() { return photoUrl; }
    public boolean isOpenNow() { return openNow; }
    public Double getDistance() { return distance; } // Double olarak değiştirildi
    public String getDuration() { return duration; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setVicinity(String vicinity) { this.vicinity = vicinity; }
    public void setRating(double rating) { this.rating = rating; }
    public void setTotalRatings(int totalRatings) { this.totalRatings = totalRatings; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public void setOpenNow(boolean openNow) { this.openNow = openNow; }
    public void setDistance(Double distance) { this.distance = distance; } // Double olarak değiştirildi
    public void setDuration(String duration) { this.duration = duration; }

    // Mesafe bilgilerini güncelleme metodu (HTML'den gelen veri için optimize edildi)
    public void updateDistanceInfo(double distanceMeters, String duration) {
        this.distance = distanceMeters;
        this.duration = duration;
    }

    // Formatlı mesafe bilgisi (ListView'da göstermek için)
    public String getFormattedDistance() {
        return (distance != null) ? String.format("%.1f m", distance) : "Bilinmiyor";
    }

    // Türü Türkçe olarak döndürme
    public String getTypeInTurkish() {
        if (type == null) return "Mekan";
        return type.equalsIgnoreCase("cafe") ? "Kafe" :
                type.equalsIgnoreCase("restaurant") ? "Restoran" : "Mekan";
    }

    @Override
    public String toString() {
        return String.format("%s - %s ⭐%.1f (%s - %s)",
                name,
                getTypeInTurkish(),
                rating,
                getFormattedDistance(),
                (duration != null) ? duration : "Süre bilinmiyor");
    }
}