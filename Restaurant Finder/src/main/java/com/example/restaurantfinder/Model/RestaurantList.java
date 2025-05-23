package com.example.restaurantfinder.Model;

import com.example.restaurantfinder.Model.Restaurant;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Comparator;

public class RestaurantList {
    private ObservableList<Restaurant> restaurants;

    public RestaurantList() {
        restaurants = FXCollections.observableArrayList();
    }

    public void addRestaurant(Restaurant restaurant) {
        restaurants.add(restaurant);
    }

    public ObservableList<Restaurant> getRestaurants() {
        return restaurants;
    }

    public void sortByDistance() {
        FXCollections.sort(restaurants, Comparator.comparingDouble(Restaurant::getDistance));
    }

    public void sortByRating() {
        FXCollections.sort(restaurants, Comparator.comparingDouble(Restaurant::getRating).reversed());
    }
}
