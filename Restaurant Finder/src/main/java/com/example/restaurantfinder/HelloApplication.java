package com.example.restaurantfinder;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("MainScene.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
        // Kodunuzda bu satırı ekleyin:
        System.out.println("Dosya Yolu: " + getClass().getResource("/com/example/restaurantfinder/harita.html"));
    }

    public static void main(String[] args) {
        launch();
    }
}