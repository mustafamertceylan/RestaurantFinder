package com.example.restaurantfinder_part3_;

import javafx.application.Platform;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

import java.net.InetSocketAddress;

public class MyWebSocketServer extends WebSocketServer {
    private MainSceneController controller;

    public MyWebSocketServer(MainSceneController controller) {
        super(new InetSocketAddress(8081));
        this.controller = controller;
        System.out.println("🔧 WebSocket sunucusu oluşturuldu (Port: 8081)");
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("✅ Yeni WebSocket bağlantısı: " + conn.getRemoteSocketAddress());
        System.out.println("📋 Bağlantı detayları:");
        System.out.println("  - Origin: " + handshake.getFieldValue("Origin"));
        System.out.println("  - User-Agent: " + handshake.getFieldValue("User-Agent"));

        // Test mesajı gönder
        conn.send("WebSocket bağlantısı başarılı!");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("📨 WebSocket mesajı alındı:");
        System.out.println("  - Uzunluk: " + message.length() + " karakter");
        System.out.println("  - İçerik: " + message.substring(0, Math.min(100, message.length())) + "...");

        try {
            JSONObject json = new JSONObject(message);
            System.out.println("✅ JSON parse başarılı");
            System.out.println("  - ID: " + json.optString("id", "N/A"));
            System.out.println("  - Name: " + json.optString("name", "N/A"));
            System.out.println("  - Type: " + json.optString("type", "N/A"));

            // Platform.runLater ile UI thread'e gönder
            Platform.runLater(() -> {
                try {
                    System.out.println("🔄 Controller'a gönderiliyor: " + json.optString("name"));

                    controller.addPlaceFromJS(
                            json.optString("id", "unknown"),
                            json.optString("name", "Bilinmeyen Mekan"),
                            json.optString("type", "unknown"),
                            json.optDouble("lat", 0.0),
                            json.optDouble("lng", 0.0),
                            json.optString("vicinity", "Bilinmiyor"),
                            json.optDouble("rating", 0.0),
                            json.optInt("totalRatings", 0),
                            json.optString("photoUrl", ""),
                            json.optBoolean("openNow", false),
                            json.optDouble("distance", 0.0),
                            json.optString("duration", "Bilinmiyor")
                    );

                    System.out.println("✅ Controller'a başarıyla gönderildi");

                    // Başarı mesajını geri gönder
                    conn.send("Mekan alındı: " + json.optString("name"));

                } catch (Exception e) {
                    System.err.println("❌ Controller çağrısı hatası: " + e.getMessage());
                    e.printStackTrace();
                    conn.send("Hata: " + e.getMessage());
                }
            });

        } catch (Exception e) {
            System.err.println("❌ JSON parse hatası: " + e.getMessage());
            System.err.println("❌ Problematik mesaj: " + message);
            e.printStackTrace();

            // Hata mesajını geri gönder
            conn.send("JSON parse hatası: " + e.getMessage());
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("❌ WebSocket bağlantısı kapandı:");
        System.out.println("  - Code: " + code);
        System.out.println("  - Reason: " + reason);
        System.out.println("  - Remote: " + remote);
        System.out.println("  - Address: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("❌ WebSocket hatası:");
        if (conn != null) {
            System.err.println("  - Bağlantı: " + conn.getRemoteSocketAddress());
        }
        System.err.println("  - Hata: " + ex.getMessage());
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("🚀 WebSocket sunucusu başlatıldı!");
        System.out.println("  - Port: 8081");
        System.out.println("  - Adres: ws://localhost:8081");
        System.out.println("  - Controller: " + (controller != null ? "✅ Hazır" : "❌ Null"));
    }

    // Sunucu durumunu kontrol etme metodu
    public void printStatus() {
        System.out.println("📊 WebSocket Sunucu Durumu:");
        //System.out.println("  - Çalışıyor: " + (isRunning() ? "✅" : "❌"));
        System.out.println("  - Port: " + getPort());
        System.out.println("  - Bağlantı sayısı: " + getConnections().size());

        if (!getConnections().isEmpty()) {
            System.out.println("  - Aktif bağlantılar:");
            getConnections().forEach(conn -> {
                System.out.println("    * " + conn.getRemoteSocketAddress());
            });
        }
    }
}