package com.example.restaurantfinder_part3_;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // FXML dosyasını yükle
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScene.fxml"));
        Parent root = loader.load();

        // Sahne oluştur
        Scene scene = new Scene(root);

        // Stage ayarları
        primaryStage.setTitle("Yakındaki Kafe ve Restoranlar");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        // JavaFX WebView için geolocation izinlerini otomatik ver
        System.setProperty("javafx.allowGeolocation", "true");
        System.setProperty("sun.net.useExclusiveBind", "false");

        // Chrome benzeri otomatik konum izni ayarları
        System.setProperty("javafx.webkit.geolocation.enabled", "true");
        System.setProperty("javafx.webkit.geolocation.permission", "granted");

        // WebKit ayarları
        System.setProperty("prism.allowhidpi", "false");
        System.setProperty("glass.accessible.force", "false");
        launch(args);
    }
}