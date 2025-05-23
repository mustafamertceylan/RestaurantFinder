package com.example.restaurantfinder;

import com.example.restaurantfinder.Model.Restaurant;
import com.example.restaurantfinder.Model.RestaurantList;
import javafx.scene.web.WebView;
import javafx.concurrent.Worker;
import netscape.javascript.JSObject;

import java.net.URL;

public class MapBrowser {
    private WebView webView;
    private RestaurantList restaurantList;

    public MapBrowser(WebView webView, RestaurantList restaurantList) {
        this.webView = webView;
        this.restaurantList = restaurantList;
        initializeWebView();
    }

    private void initializeWebView() {
        URL url = getClass().getResource("/com/example/restaurantfinder/harita.html");
        if (url == null) {
            System.err.println("HATA: harita.html bulunamadı!");
            return;
        }
        webView.getEngine().load(url.toExternalForm());

        // JavaScript ile iletişim kurmak için
        webView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webView.getEngine().executeScript("window");
                window.setMember("javaApp", this);
            }
        });
    }

    // JavaScript'ten çağrılacak metod
    public void addRestaurant(String name, String address, String type,
                              double rating, int reviewCount,
                              double distance, double lat, double lng,
                              String photoUrl) {
        Restaurant restaurant = new Restaurant(name, address, type, rating,
                reviewCount, distance, lat, lng, photoUrl);
        restaurantList.addRestaurant(restaurant);
    }

    // Konum bilgisini JavaScript'e gönder
    public void sendLocation(double lat, double lng) {
        webView.getEngine().executeScript(
                String.format("updateUserLocation(%f, %f)", lat, lng)
        );
    }
}