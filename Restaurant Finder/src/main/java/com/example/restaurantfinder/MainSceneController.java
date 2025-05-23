package com.example.restaurantfinder;

import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.net.URL;

public class MainSceneController {
//    @FXML
//    private WebView webView;
//
//    public void initialize() {
//        WebEngine engine = webView.getEngine();
//
//        try {
//            // Daha güvenli URL alma yöntemi
//            URL url = getClass().getResource("/com/example/restaurantfinder/harita.html");
//
//            if (url != null) {
//                System.out.println("HTML dosyası bulundu: " + url.toString());
//                engine.load(url.toExternalForm());
//            } else {
//                // Alternatif yol deneme
//                url = getClass().getClassLoader().getResource("com/example/restaurantfinder/harita.html");
//                if (url != null) {
//                    engine.load(url.toExternalForm());
//                } else {
//                    throw new RuntimeException("HTML dosyası her iki yöntemle de bulunamadı!");
//                }
//            }
//        } catch (Exception e) {
//            System.err.println("Hata oluştu: " + e.getMessage());
//            engine.loadContent("<h1>Hata: " + e.getMessage() + "</h1>");
//        }
//
//    }


    @FXML
    private WebView webView;

    public void initialize() {
        // JavaScript etkinleştir
        webView.getEngine().setJavaScriptEnabled(true);
        webView.getEngine().setUserAgent("Mozilla/5.0");

        // HTML dosyasını localhost sunucusundan yükle
        webView.getEngine().load("http://localhost:8000/harita.html");
    }
}
