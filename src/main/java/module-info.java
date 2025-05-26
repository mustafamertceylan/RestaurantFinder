module com.example.restaurantfinder_part3_ {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires jdk.jsobject;
    requires java.desktop;
    requires jdk.httpserver;
    requires Java.WebSocket;
    requires org.json;

    opens com.example.restaurantfinder_part3_ to javafx.fxml;
    exports com.example.restaurantfinder_part3_;
}