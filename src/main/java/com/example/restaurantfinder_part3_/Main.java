package com.example.restaurantfinder_part3_;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScene.fxml"));
        Parent root = loader.load();
        MainSceneController controller = loader.getController();

        // WebSocket sunucusunu başlat
        MyWebSocketServer server = new MyWebSocketServer(controller);
        server.start(); // start() metodunu kullan

        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        // HTTP sunucusunu başlat
        try {
            SimpleHttpServer.start();
        } catch (IOException e) {
            System.err.println("HTTP sunucusu başlatılamadı: " + e.getMessage());
        }

        // Chrome'u başlat (HTTP sunucusu başladıktan sonra kısa bir bekleme)
        new Thread(() -> {
            try {
                Thread.sleep(2000); // 2 saniye bekle
                openChromeWithMap();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void openChromeWithMap() {
        try {
            String chromePath = "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe";
            ProcessBuilder pb = new ProcessBuilder(chromePath, "http://localhost:8080");
            pb.start();
            System.out.println("Chrome başlatıldı: http://localhost:8080");
        } catch (IOException e) {
            System.err.println("Chrome başlatma hatası: " + e.getMessage());
            // Alternatif olarak varsayılan tarayıcıyı dene
            try {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler http://localhost:8080");
            } catch (IOException ex) {
                System.err.println("Varsayılan tarayıcı başlatma hatası: " + ex.getMessage());
            }
        }
    }
}