package com.example.restaurantfinder.controller;

import com.example.restaurantfinder.MapBrowser;
import com.example.restaurantfinder.Model.Restaurant;
import com.example.restaurantfinder.Model.RestaurantList;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.web.WebView;

public class MainSceneController {
    @FXML
    private WebView mapWebView;
    @FXML
    private ListView<String> restaurantListView;

    private RestaurantList restaurantList;
    private MapBrowser mapBrowser;

    public void initialize() {
        restaurantList = new RestaurantList();
        mapBrowser = new MapBrowser(mapWebView, restaurantList);

        // ObservableList'ten gelen değişiklikleri dinlemeye gerek yok
        // çünkü listView zaten binding ile güncellenecek
        restaurantList.getRestaurants().addListener((ListChangeListener<Restaurant>) change -> {
            updateListView(); // Eğer ekstra güncelleme gerekiyorsa
        });
    }

    @FXML
    private void handleSortByDistance() {
        restaurantList.sortByDistance();
    }

    @FXML
    private void handleSortByRating() {
        restaurantList.sortByRating();
    }

    private void updateListView() {
        restaurantListView.getItems().setAll(
                restaurantList.getRestaurants().stream()
                        .map(Restaurant::toString)
                        .toList()
        );
    }
}
