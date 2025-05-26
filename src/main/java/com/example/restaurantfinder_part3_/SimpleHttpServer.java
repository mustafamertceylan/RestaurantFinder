package com.example.restaurantfinder_part3_;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SimpleHttpServer {
    public static void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                try {
                    // Önce resources klasöründen okumayı dene
                    InputStream htmlStream = SimpleHttpServer.class.getResourceAsStream("/com/example/restaurantfinder_part3_/harita.html");

                    byte[] htmlBytes;
                    if (htmlStream != null) {
                        // Resources'dan oku
                        htmlBytes = htmlStream.readAllBytes();
                        htmlStream.close();
                        System.out.println("HTML dosyası resources'dan yüklendi");
                    } else {
                        // Proje klasöründen okumayı dene
                        try {
                            htmlBytes = Files.readAllBytes(Paths.get("harita.html"));
                            System.out.println("HTML dosyası proje klasöründen yüklendi");
                        } catch (IOException e) {
                            // Varsayılan HTML içeriği
                            String defaultHtml = """
                            <!DOCTYPE html>
                            <html>
                            <head>
                                <title>HTML Dosyası Bulunamadı</title>
                                <meta charset="utf-8">
                            </head>
                            <body>
                                <h1>HTML Dosyası Bulunamadı</h1>
                                <p>harita.html dosyası resources klasörüne veya proje ana dizinine yerleştirilmelidir.</p>
                                <p>Dosya yolu kontrol edin:</p>
                                <ul>
                                    <li>src/main/resources/com/example/restaurantfinder_part3_/harita.html</li>
                                    <li>veya proje ana dizini/harita.html</li>
                                </ul>
                            </body>
                            </html>
                            """;
                            htmlBytes = defaultHtml.getBytes("UTF-8");
                            System.err.println("HTML dosyası bulunamadı, varsayılan sayfa gösteriliyor");
                        }
                    }

                    // Content-Type header'ı ekle
                    exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                    exchange.sendResponseHeaders(200, htmlBytes.length);
                    exchange.getResponseBody().write(htmlBytes);

                } catch (Exception e) {
                    String errorHtml = "<html><body><h1>Sunucu Hatası</h1><p>" + e.getMessage() + "</p></body></html>";
                    byte[] errorBytes = errorHtml.getBytes("UTF-8");
                    exchange.sendResponseHeaders(500, errorBytes.length);
                    exchange.getResponseBody().write(errorBytes);
                    System.err.println("HTTP handler hatası: " + e.getMessage());
                } finally {
                    exchange.close();
                }
            }
        });

        server.start();
        System.out.println("HTTP Sunucusu http://localhost:8080 adresinde başlatıldı");
    }
}