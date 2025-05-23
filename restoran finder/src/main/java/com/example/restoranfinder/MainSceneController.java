package com.example.restoranfinder;

import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.w3c.dom.ls.LSOutput;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URL;

public class MainSceneController {
//
//    @FXML
//    private WebView webView;
//
//    public void initialize() {
//        // JavaScript etkinleştir
//        webView.getEngine().setJavaScriptEnabled(true);
//        webView.getEngine().setUserAgent("Mozilla/5.0");
//
//        // HTML dosyasını localhost sunucusundan yükle
//        webView.getEngine().load("http://localhost:8000/yeni_harita.html");
//        //webView.getEngine().load("http://google.com");//test amaçlı kullandın
//        //webView.getEngine().load("http://google.com/localhost:8000/harita.html");//test amaçlı kullandın
//    }


    @FXML
    private AnchorPane cefContainer;

    public void initialize() {
        try {
            // JCEF başlatma
            CefApp cefApp = CefApp.getInstance();
            CefClient client = cefApp.createClient();

            // HTML dosyasının yolunu al
            URL url = getClass().getResource("/com/example/restoranfinder/harita.html");
            if (url == null) {
                throw new FileNotFoundException("HTML dosyası bulunamadı!");
            }
            File htmlFile = new File(url.toURI());
            String htmlPath = htmlFile.toURI().toString(); // file:///C:/... şeklinde olur

            // Browser oluştur
            CefBrowser browser = client.createBrowser(htmlPath, false, false);

            // JavaFX içinde Swing bileşeni göstermek
            SwingNode swingNode = new SwingNode();
            SwingUtilities.invokeLater(() -> swingNode.setContent((JComponent) browser.getUIComponent()));

            // AnchorPane içine yerleştirmek (tam ekran olacak şekilde)
            Platform.runLater(() -> {
                cefContainer.getChildren().add(swingNode);
                AnchorPane.setTopAnchor(swingNode, 0.0);
                AnchorPane.setBottomAnchor(swingNode, 0.0);
                AnchorPane.setLeftAnchor(swingNode, 0.0);
                AnchorPane.setRightAnchor(swingNode, 0.0);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @FXML
//    private WebView webView;
//
//    public void initialize() {
//        WebEngine engine = webView.getEngine();
//
//        try {
//            // Daha güvenli URL alma yöntemi
//            URL url = getClass().getResource("/com/example/restoranfinder/yeni_harita.html");
//            //URL url = getClass().getResource("/com/example/restoranfinder/haritaDeneme.html");
//
//            if (url != null) {
//                System.out.println("HTML dosyası bulundu: " + url.toString());
//                engine.load(url.toExternalForm());
//                //Desktop.getDesktop().browse(new URI("http://localhost:8000/harita.html"));//deneme amaçlı
//            } else {
//                // Alternatif yol deneme
//                url = getClass().getClassLoader().getResource("com/example/restoranfinder/yeni_harita.html");
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
}