module com.example.restaurantfinder {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires fxgl;
    requires jdk.jsobject;

    opens com.example.restaurantfinder to javafx.fxml, javafx.web;
    exports com.example.restaurantfinder;
    exports com.example.restaurantfinder.controller;
    opens com.example.restaurantfinder.controller to javafx.fxml, javafx.web;
}