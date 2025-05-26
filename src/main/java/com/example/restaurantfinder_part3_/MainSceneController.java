package com.example.restaurantfinder_part3_;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.net.URL;

public class MainSceneController {
    @FXML
    private ListView<Place> listView;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> filterComboBox;

    @FXML
    private Button refreshButton;

    @FXML
    private Button sortButton;
    // MainSceneController'a ekleyin
    @FXML
    private void testAddPlaceManually() {
        Platform.runLater(() -> {
            Place testPlace = new Place(
                    "manual_test_id",
                    "Manual Test Place",
                    "cafe",
                    41.0370,
                    28.9857,
                    "Test Address",
                    4.5,
                    100,
                    "",
                    true,
                    250.0,
                    "5 mins"
            );

            placesList.addLast(testPlace);
            updateListView();

            System.out.println("MANUEL TEST: ListView boyutu: " +
                    listView.getItems().size());
        });
    }

    // Ana WebView - hem veri toplama hem de harita gösterimi için
    @FXML
    private WebView webView;

    // WebEngine değişkeni - EKSIK OLAN BU!
    private WebEngine webEngine; // DÜZELTME: Sınıf seviyesinde tanımlandı
    private PlaceLinkedList placesList;
    private PlaceLinkedList currentFilteredList;
    private boolean dataCollectionComplete = false;

    @FXML
    public void initialize() {
        System.out.println("Controller initialize başladı");

        // WebEngine ilk kullanım öncesi başlatma
        webEngine = webView.getEngine(); // KRİTİK DÜZELTME

        placesList = new PlaceLinkedList();
        currentFilteredList = new PlaceLinkedList();

        filterComboBox.getItems().addAll("Tümü", "Kafe", "Restoran");
        filterComboBox.setValue("Tümü");

        setupEventHandlers();
        setupWebView();
        loadDataCollector();
    }

    private void setupWebView() {
        try {

            // WebEngine zaten initialize'da atanmıştı, tekrar atama yapmıyoruz
            webEngine.setJavaScriptEnabled(true);

            // Load worker listener
            webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    setupJavaScriptBridge();
                }
            });

            // İlk yükleme
            loadDataCollector();
        } catch (Exception e) {
            System.err.println("WebView ayarlanırken hata: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupEventHandlers() {
        // ComboBox değişiklik listener
        filterComboBox.setOnAction(e -> filterPlaces());

        // TextField değişiklik listener
        searchField.textProperty().addListener((obs, oldText, newText) -> searchPlaces(newText));

        // Button click handler'ları
        refreshButton.setOnAction(e -> refreshPlaces());
        sortButton.setOnAction(e -> sortPlaces());

        // ListView seçim listener'ı - haritada vurgulamak için
        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null && dataCollectionComplete) {
                highlightPlaceOnMap(newSelection);
            }
        });

        System.out.println("Event handler'lar ayarlandı");
    }

    private void loadDataCollector() {
        // Önce resources klasöründen deneyin
        URL url = getClass().getResource("com.example.restaurantfinder_part3_/data_collector.html");

        // Hala bulamazsa, farklı yolları deneyin
        if (url == null) {
            url = getClass().getResource("data_collector.html");
        }

        if (url != null) {
            System.out.println("data_collector.html bulundu: " + url.toString());
            webEngine.load(url.toExternalForm());

            webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    System.out.println("WebView yüklendi, JavaScript bridge kuruluyor...");
                    setupJavaScriptBridge();
                } else if (newState == Worker.State.FAILED) {
                    System.err.println("WebView yüklenemedi!");
                }
            });
        } else {
            System.err.println("HATA: data_collector.html hiçbir yolda bulunamadı!");
            System.err.println("Aşağıdaki konumları kontrol edin:");
            System.err.println("1. src/main/resources/data_collector.html");
            System.err.println("2. src/data_collector.html");
            System.err.println("3. resources/data_collector.html");

            // Alternatif: doğrudan HTML içeriğini string olarak yükle
            loadDataCollectorFromString();
        }
    }

    // Eğer dosya bulunamazsa, HTML içeriğini doğrudan string olarak yükle
    private void loadDataCollectorFromString() {
        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Veri Toplayıcı</title>
                <meta charset="utf-8">
                <style>
                    body {
                        margin: 0;
                        padding: 20px;
                        font-family: Arial, sans-serif;
                        background: #f5f5f5;
                    }
                    #status {
                        background: white;
                        padding: 20px;
                        border-radius: 8px;
                        box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                        max-width: 400px;
                        margin: 0 auto;
                    }
                    .loading {
                        text-align: center;
                        color: #666;
                    }
                    .progress {
                        background: #e0e0e0;
                        height: 10px;
                        border-radius: 5px;
                        margin: 10px 0;
                        overflow: hidden;
                    }
                    .progress-bar {
                        background: #4CAF50;
                        height: 100%;
                        width: 0%;
                        transition: width 0.3s ease;
                    }
                    #log {
                        margin-top: 20px;
                        padding: 10px;
                        background: #f9f9f9;
                        border-radius: 4px;
                        max-height: 200px;
                        overflow-y: auto;
                        font-size: 12px;
                    }
                </style>
            </head>
            <body>
            <div id="status">
                <div class="loading">
                    <h3>📍 Yakındaki Mekanlar Aranıyor...</h3>
                    <div class="progress">
                        <div class="progress-bar" id="progressBar"></div>
                    </div>
                    <p id="statusText">Konum bilgisi alınıyor...</p>
                </div>
                <div id="log"></div>
            </div>

            <!-- Gizli harita div'i -->
            <div id="map" style="display: none; width: 1px; height: 1px;"></div>

            <script>
                let map;
                let userLocation;
                let placesFound = 0;
                let placesProcessed = 0;
                let totalExpected = 0;
                const serviceTypes = ['cafe', 'restaurant'];

                function updateStatus(message) {
                    document.getElementById('statusText').textContent = message;
                    addLog(message);
                }

                function addLog(message) {
                    const log = document.getElementById('log');
                    const time = new Date().toLocaleTimeString();
                    log.innerHTML += `<div>[${time}] ${message}</div>`;
                    log.scrollTop = log.scrollHeight;
                }

                function updateProgress() {
                    if (totalExpected > 0) {
                        const progress = (placesProcessed / totalExpected) * 100;
                        document.getElementById('progressBar').style.width = progress + '%';
                    }
                }

                function startDataCollection() {
                    updateStatus("Veri toplama başlatılıyor...");
                    placesFound = 0;
                    placesProcessed = 0;
                    totalExpected = 0;

                    if (navigator.geolocation) {
                        navigator.geolocation.getCurrentPosition(
                            position => {
                                userLocation = {
                                    lat: position.coords.latitude,
                                    lng: position.coords.longitude
                                };

                                updateStatus(`Konum tespit edildi: ${userLocation.lat.toFixed(6)}, ${userLocation.lng.toFixed(6)}`);

                                // Minimal harita oluştur
                                map = new google.maps.Map(document.getElementById("map"), {
                                    zoom: 15,
                                    center: userLocation
                                });

                                // Mekanları ara
                                searchPlaces();
                            },
                            error => {
                                updateStatus("Konum erişimi reddedildi veya alınamadı!");
                                addLog("Hata: " + error.message);
                            }
                        );
                    } else {
                        updateStatus("Tarayıcı konum desteği sunmuyor!");
                    }
                }

                function searchPlaces() {
                    updateStatus("Yakındaki mekanlar aranıyor...");
                    let completedSearches = 0;

                    serviceTypes.forEach(type => {
                        const service = new google.maps.places.PlacesService(map);
                        service.nearbySearch(
                            {
                                location: userLocation,
                                radius: 1500,
                                type: type
                            },
                            (results, status) => {
                                completedSearches++;

                                if (status === "OK" && results) {
                                    const validPlaces = results.filter(place =>
                                        place.business_status === "OPERATIONAL" && place.name
                                    );

                                    totalExpected += validPlaces.length;
                                    addLog(`${type.toUpperCase()}: ${validPlaces.length} mekan bulundu`);

                                    validPlaces.forEach(place => {
                                        processPlace(place, type);
                                    });
                                } else {
                                    addLog(`${type.toUpperCase()}: Arama başarısız (${status})`);
                                }

                                if (completedSearches === serviceTypes.length) {
                                    updateStatus(`Toplam ${totalExpected} mekan bulundu, işleniyor...`);
                                    if (totalExpected === 0) {
                                        updateStatus("Hiç mekan bulunamadı!");
                                        if (window.notifyDataLoadComplete) {
                                            window.notifyDataLoadComplete();
                                        }
                                    }
                                }
                            }
                        );
                    });
                }

                function processPlace(place, placeType) {
                    const service = new google.maps.places.PlacesService(map);
                    service.getDetails(
                        {
                            placeId: place.place_id,
                            fields: ['place_id', 'name', 'types', 'geometry', 'vicinity',
                                    'rating', 'user_ratings_total', 'photos', 'opening_hours']
                        },
                        (placeDetails, status) => {
                            placesProcessed++;
                            updateProgress();

                            if (status === "OK" && placeDetails) {
                                sendPlaceToJava(placeDetails, placeType);
                                placesFound++;
                                calculateDistance(placeDetails);
                            } else {
                                addLog(`Detay alınamadı: ${place.name} (${status})`);
                            }

                            if (placesProcessed >= totalExpected) {
                                updateStatus(`✅ Tamamlandı! ${placesFound} mekan başarıyla yüklendi.`);
                                if (window.notifyDataLoadComplete) {
                                    window.notifyDataLoadComplete();
                                }
                            }
                        }
                    );
                }

                function sendPlaceToJava(place, placeType) {
                    if (window.javaController && window.sendPlaceToJava) {
                        const placeData = {
                            id: place.place_id,
                            name: place.name || "İsimsiz Mekan",
                            type: placeType,
                            lat: place.geometry.location.lat(),
                            lng: place.geometry.location.lng(),
                            vicinity: place.vicinity || place.formatted_address || "Adres bilgisi yok",
                            rating: place.rating || 0,
                            totalRatings: place.user_ratings_total || 0,
                            photoUrl: place.photos && place.photos[0] ?
                                     place.photos[0].getUrl({ maxWidth: 300, maxHeight: 200 }) : "",
                            openNow: place.opening_hours ? place.opening_hours.open_now : false
                        };

                        window.sendPlaceToJava(placeData);
                    }
                }

                function calculateDistance(place) {
                    const directionsService = new google.maps.DirectionsService();

                    directionsService.route(
                        {
                            origin: userLocation,
                            destination: place.geometry.location,
                            travelMode: google.maps.TravelMode.DRIVING,
                            unitSystem: google.maps.UnitSystem.METRIC
                        },
                        (response, status) => {
                            if (status === "OK" && window.updateDistanceInJava) {
                                const distance = response.routes[0].legs[0].distance.text;
                                const duration = response.routes[0].legs[0].duration.text;
                                window.updateDistanceInJava(place.place_id, distance, duration);
                            }
                        }
                    );
                }

                window.addEventListener('load', () => {
                    setTimeout(startDataCollection, 1000);
                });

                console.log("Veri toplayıcı hazır");
            </script>

            <script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyA6WUm_MlnT4_M4tnfVnqTDd9f021ZInSo&libraries=places" async defer></script>
            </body>
            </html>
            """;

        webEngine.loadContent(htmlContent);
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                System.out.println("HTML içerik yüklendi, JavaScript bridge kuruluyor...");
                setupJavaScriptBridge();
            }
        });
    }

    private void setupJavaScriptBridge() {
        try {
            System.out.println("=== JavaScript Bridge Kurulumu Başlıyor ===");

            JSObject window = (JSObject) webEngine.executeScript("window");
            window.setMember("javaController", this);

            System.out.println("✅ Java controller window'a eklendi");

            // JavaScript fonksiyonlarını tanımla
            String jsCode = """
        console.log('JavaScript bridge fonksiyonları tanımlanıyor...');
        
        window.sendPlaceToJava = function(placeData) {
            try {
                console.log('sendPlaceToJava çağrıldı:', placeData.name);
                javaController.addPlaceFromJS(
                    placeData.id, 
                    placeData.name, 
                    placeData.type,
                    placeData.lat, 
                    placeData.lng, 
                    placeData.vicinity, 
                    placeData.rating,
                    placeData.totalRatings, 
                    placeData.photoUrl, 
                    placeData.openNow,
                    placeData.distance || null,  // EKLENDİ
                    placeData.duration || null    // EKLENDİ
                );
                console.log('✅ Java metoduna başarıyla gönderildi');
            } catch(e) { 
                console.error('❌ sendPlaceToJava error:', e); 
            }
        };
        
        window.updateDistanceInJava = function(placeId, distance, duration) {
            try {
                console.log('updateDistanceInJava çağrıldı:', placeId);
                javaController.updatePlaceDistance(placeId, distance, duration);
            } catch(e) { 
                console.error('❌ updateDistanceInJava error:', e); 
            }
        };
        
        window.notifyDataLoadComplete = function() {
            try {
                console.log('notifyDataLoadComplete çağrıldı');
                javaController.onDataLoadComplete();
            } catch(e) { 
                console.error('❌ notifyDataLoadComplete error:', e); 
            }
        };
        
        console.log('✅ Tüm bridge fonksiyonları tanımlandı');
        """;

            webEngine.executeScript(jsCode);
            System.out.println("✅ JavaScript bridge başarıyla kuruldu");

            // Test bridge
            String testResult = (String) webEngine.executeScript("typeof window.sendPlaceToJava");
            System.out.println("Bridge test sonucu: " + testResult);

        } catch (Exception e) {
            System.err.println("❌ JavaScript bridge kurulurken hata: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // addPlaceFromJS metodunu da güncelleyin:
    public void addPlaceFromJS(String id, String name, String type, double lat, double lng,
                               String vicinity, double rating, int totalRatings,
                               String photoUrl, boolean openNow, Double distance, String duration) {
        Platform.runLater(() -> {
            try {
                System.out.println("=== MEKAN EKLENİYOR ===");
                System.out.println("Distance: " + distance + "m");
                System.out.println("Duration: " + duration);

                Place existingPlace = placesList.findById(id);
                if (existingPlace == null) {
                    Place newPlace = new Place(id, name, type, lat, lng, vicinity,
                            rating, totalRatings, photoUrl, openNow,
                            distance, duration);

                    placesList.addLast(newPlace);
                    System.out.println("✅ Yeni mekan eklendi: " + name);
                    updateListView(); // TEST: Her eklemede güncelle
                }
            } catch (Exception e) {
                System.err.println("❌ Hata: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    public void updatePlaceDistance(String placeName, double distance, String duration) {
        Platform.runLater(() -> {
            try {
                // Tüm mekanlarda isme göre arama
                for (Place place : placesList) {
                    if (place.getName().equalsIgnoreCase(placeName)) {
                        place.updateDistanceInfo(distance, duration);

                        // ListView'ı güncelle (performans için sadece ilgili satır)
                        int index = placesList.indexOf(place);
                        if (index >= 0) {
                            listView.getItems().set(index, place);
                        }

                        System.out.println("📍 Güncellendi: " + placeName +
                                " - " + distance + "m (" + duration + ")");
                        return; // Bulunca döngüden çık
                    }
                }
                System.err.println("⚠️ Uyarı: " + placeName + " isimli mekan bulunamadı!");
            } catch (Exception e) {
                System.err.println("❌ Hata: " + e.getMessage());
            }
        });
    }

    public void onDataLoadComplete() {
        Platform.runLater(() -> {
            dataCollectionComplete = true;
            System.out.println("✅ Veri yükleme tamamlandı! Toplam: " + placesList.size() + " mekan");
            updateListView();
            loadMapView();
        });
    }

    private void loadMapView() {
        URL mapUrl = getClass().getResource("/harita.html");

        if (mapUrl == null) {
            mapUrl = getClass().getClassLoader().getResource("harita.html");
        }

        if (mapUrl == null) {
            mapUrl = getClass().getResource("harita.html");
        }

        if (mapUrl != null) {
            System.out.println("Harita görünümüne geçiliyor...");
            webEngine.load(mapUrl.toExternalForm());

            webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    setupMapJavaScriptBridge();
                }
            });
        } else {
            System.err.println("HATA: harita.html bulunamadı!");
        }
    }

    private void setupMapJavaScriptBridge() {
        try {
            JSObject window = (JSObject) webEngine.executeScript("window");
            window.setMember("javaController", this);
            System.out.println("Harita JavaScript bridge kuruldu");
        } catch (Exception e) {
            System.err.println("Harita JavaScript bridge hatası: " + e.getMessage());
        }
    }

    private void highlightPlaceOnMap(Place place) {
        try {
            if (dataCollectionComplete) {
                String script = String.format(
                        "if (typeof highlightPlace === 'function') { highlightPlace('%s', %f, %f); }",
                        place.getId(), place.getLatitude(), place.getLongitude()
                );
                webEngine.executeScript(script);
                System.out.println("Haritada vurgulandı: " + place.getName());
            }
        } catch (Exception e) {
            System.err.println("Haritada vurgulama hatası: " + e.getMessage());
        }
    }

    private void updateListView() {
        try {
            System.out.println("=== UPDATE LIST VIEW ===");
            ObservableList<Place> displayList = currentFilteredList.isEmpty()
                    ? placesList.toObservableList()
                    : currentFilteredList.toObservableList();

            listView.setCellFactory(lv -> new ListCell<Place>() {
                private final Label nameLabel = new Label();
                private final Label distanceLabel = new Label();
                private final VBox textBox = new VBox(4);
                private final ImageView icon = new ImageView();
                private final HBox content = new HBox(15);
                private final StackPane card = new StackPane();

                {
                    nameLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #222;");
                    distanceLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");
                    textBox.getChildren().addAll(nameLabel, distanceLabel);

                    // Konum simgesi
                    icon.setImage(new Image("https://img.icons8.com/ios-filled/50/2ecc71/marker.png", 24, 24, true, true));
                    icon.setFitWidth(24);
                    icon.setFitHeight(24);
                    icon.setPreserveRatio(true);
                    icon.setSmooth(true);

                    // HBox içeriği
                    content.setAlignment(Pos.CENTER_LEFT);
                    content.getChildren().addAll(icon, textBox);

                    // Kart stili (arkaplan, gölge, köşeler)
                    card.setPadding(new Insets(10));
                    card.getChildren().add(content);
                    card.setStyle(
                            "-fx-background-color: linear-gradient(to right, #ffffff, #f9f9f9);" +
                                    "-fx-background-radius: 12;" +
                                    "-fx-border-radius: 12;" +
                                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6, 0.3, 0, 3);"
                    );

                    setPadding(new Insets(5));
                }

                @Override
                protected void updateItem(Place place, boolean empty) {
                    super.updateItem(place, empty);
                    if (empty || place == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        nameLabel.setText(place.getName());
                        distanceLabel.setText("Uzaklık: " + (place.getDistance() != null ?
                                String.format("%.1f m", place.getDistance()) : "Bilinmiyor"));
                        setGraphic(card);
                    }
                }
            });

            listView.setItems(displayList);
            System.out.println("✅ ListView güncellendi. " + displayList.size() + " mekan gösteriliyor");
        } catch (Exception e) {
            System.err.println("❌ ListView güncellenirken hata: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Yardımcı metodlar
    private String formatDistance(String meter) {
        // 1. Null kontrolü
        if (meter == null || meter.trim().isEmpty()) {
            return "Bilinmiyor";
        }

        try {
            // 2. String'i double'a çevir
            double distanceInMeters = Double.parseDouble(meter.trim());

            // 3. Negatif değer kontrolü
            if (distanceInMeters < 0) {
                return "Bilinmiyor";
            }

            // 4. Formatlama
            if (distanceInMeters < 1000) {
                return String.format("%d metre", (int)distanceInMeters);
            } else {
                return String.format("%.1f km", distanceInMeters / 1000.0);
            }
        } catch (NumberFormatException e) {
            return "Bilinmiyor"; // Geçersiz sayı formatı
        }
    }
    private String formatDuration(String second) {
        double secondss = Double.parseDouble(second);
        int seconds = (int) secondss;
        if (seconds < 60) {
            return String.format("%d sn", seconds);
        } else {
            int minutes = seconds / 60;
            return String.format("%d dk", minutes);
        }
    }
    @FXML
    private void filterPlaces() {
        try {
            String selectedFilter = filterComboBox.getValue();
            System.out.println("Filtre uygulanıyor: " + selectedFilter);

            if ("Tümü".equals(selectedFilter)) {
                currentFilteredList.clear();
            } else {
                String filterType = selectedFilter.equals("Kafe") ? "cafe" : "restaurant";
                currentFilteredList = placesList.filterByType(filterType);
            }

            updateListView();
        } catch (Exception e) {
            System.err.println("Filtreleme hatası: " + e.getMessage());
        }
    }

    private void searchPlaces(String searchText) {
        try {
            if (searchText == null || searchText.trim().isEmpty()) {
                filterPlaces();
                return;
            }

            PlaceLinkedList searchResults = new PlaceLinkedList();
            String lowerSearchText = searchText.toLowerCase();

            for (Place place : placesList) {
                if (place.getName().toLowerCase().contains(lowerSearchText) ||
                        place.getVicinity().toLowerCase().contains(lowerSearchText)) {
                    searchResults.addLast(place);
                }
            }

            currentFilteredList = searchResults;
            updateListView();
            System.out.println("Arama yapıldı: '" + searchText + "' - " + searchResults.size() + " sonuç");
        } catch (Exception e) {
            System.err.println("Arama hatası: " + e.getMessage());
        }
    }

    @FXML
    private void refreshPlaces() {
        try {
            System.out.println("Veriler yenileniyor...");
            placesList.clear();
            currentFilteredList.clear();
            updateListView();
            dataCollectionComplete = false;
            loadDataCollector();
        } catch (Exception e) {
            System.err.println("Yenileme hatası: " + e.getMessage());
        }
    }

    @FXML
    private void sortPlaces() {
        try {
            System.out.println("Sıralama yapılıyor...");

            if (currentFilteredList.isEmpty()) {
                placesList.sortByRating();
            } else {
                currentFilteredList.sortByRating();
            }
            updateListView();

            System.out.println("Sıralama tamamlandı");
        } catch (Exception e) {
            System.err.println("Sıralama hatası: " + e.getMessage());
        }
    }


}