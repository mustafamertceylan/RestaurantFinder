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
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.net.URL;

public class MainSceneController {

    // FXML Components
    @FXML private ListView<Place> listView;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterComboBox;
    @FXML private Button refreshButton;
    @FXML private Button sortButton;
    @FXML private WebView webView;

    // Data Structures
    private WebEngine webEngine;
    private final PlaceLinkedList placesList = new PlaceLinkedList();
    private PlaceLinkedList currentFilteredList = new PlaceLinkedList();
    private boolean dataCollectionComplete = false;

    @FXML
    public void initialize() {
        System.out.println("Controller initialized");

        new MyWebSocketServer(this).start();

        // ListView ayarları
        listView.setCellFactory(lv -> new PlaceListCell());

        // WebView setup
        webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);

        // ComboBox initialization
        filterComboBox.getItems().addAll("Tümü", "Kafe", "Restoran");
        filterComboBox.setValue("Tümü");

        // Event handlers
        setupEventHandlers();
        loadWebContent();
    }

    private void loadWebContent() {
        URL url = getClass().getResource("/com/example/restaurantfinder_part3_/harita.html");
        if (url != null) {
            webEngine.load(url.toExternalForm());
            webEngine.getLoadWorker().stateProperty().addListener(
                    (obs, oldState, newState) -> {
                        if (newState == Worker.State.SUCCEEDED) {
                            setupJavaScriptBridge();
                        }
                    }
            );
        } else {
            System.err.println("Error: HTML file not found!");
        }
    }

    private void setupJavaScriptBridge() {
        try {
            // Java nesnesini JavaScript'e ekle
            JSObject window = (JSObject) webEngine.executeScript("window");
            window.setMember("javaController", this);

            // JavaScript fonksiyonlarını tanımla
            webEngine.executeScript("""
            window.sendPlaceToJava = function(placeData) {
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
                    placeData.distance,
                    placeData.duration
                );
            };
            
            window.notifyDataLoadComplete = function() {
                javaController.onDataLoadComplete();
            };
            
            // Sayfa yüklendiğinde veri toplamayı başlat
            window.startDataCollection = function() {
                setTimeout(startDataCollection, 500);
            };
            
            // Sayfa yüklendiğinde otomatik başlat
            window.addEventListener('load', function() {
                setTimeout(startDataCollection, 500);
            });
        """);

            System.out.println("JavaScript bridge başarıyla kuruldu");
        } catch (Exception e) {
            System.err.println("Bridge kurulum hatası: " + e.getMessage());
        }
    }

    // JavaScript Interface Methods
    public void addPlaceFromJS(String id, String name, String type, double lat, double lng,
                               String vicinity, double rating, int totalRatings,
                               String photoUrl, boolean openNow, double distance, String duration) {
        Platform.runLater(() -> {
            try {
                System.out.println("Java'ya yeni mekan geldi: " + name); // Debug log

                Place newPlace = new Place(
                        id, name, type, lat, lng, vicinity,
                        rating, totalRatings, photoUrl, openNow,
                        distance, duration
                );

                if (!placesList.contains(id)) {
                    placesList.add(newPlace);
                    updateListView();
                    System.out.println("Listeye eklendi: " + name);
                }
            } catch (Exception e) {
                System.err.println("Mekan ekleme hatası: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public void onDataLoadComplete() {
        Platform.runLater(() -> {
            dataCollectionComplete = true;
            System.out.println("Data load complete. Total places: " + placesList.size());
            updateListView();
        });
    }

    private void setupEventHandlers() {
        filterComboBox.setOnAction(e -> filterPlaces());
        searchField.textProperty().addListener((obs, oldVal, newVal) -> searchPlaces(newVal));
        refreshButton.setOnAction(e -> refreshPlaces());
        sortButton.setOnAction(e -> sortPlaces());

        listView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null && dataCollectionComplete) {
                        highlightPlaceOnMap(newVal);
                    }
                }
        );
    }

    @FXML
    private void refreshPlaces() {
        System.out.println("Refreshing data...");
        placesList.clear();
        currentFilteredList.clear();
        dataCollectionComplete = false;
        webEngine.reload();
        updateListView();
    }

    @FXML
    private void sortPlaces() {
        PlaceLinkedList targetList = currentFilteredList.isEmpty() ? placesList : currentFilteredList;

        targetList.sort((p1, p2) -> {
            // Primary sort: Rating descending
            int ratingCompare = Double.compare(p2.getRating(), p1.getRating());
            if (ratingCompare != 0) return ratingCompare;

            // Secondary sort: Distance ascending
            return Double.compare(p1.getDistance(), p2.getDistance());
        });

        updateListView();
    }

    @FXML
    private void filterPlaces() {
        String filter = filterComboBox.getValue();
        currentFilteredList.clear();

        if (!"Tümü".equals(filter)) {
            String typeFilter = filter.equals("Kafe") ? "cafe" : "restaurant";
            placesList.forEach(place -> {
                if (place.getType().equalsIgnoreCase(typeFilter)) {
                    currentFilteredList.add(place);
                }
            });
        }

        updateListView();
    }

    private void searchPlaces(String query) {
        if (query == null || query.trim().isEmpty()) {
            currentFilteredList.clear();
            filterPlaces();
            return;
        }

        String searchTerm = query.toLowerCase().trim();
        PlaceLinkedList results = new PlaceLinkedList();

        placesList.forEach(place -> {
            if (place.getName().toLowerCase().contains(searchTerm) ||
                    place.getVicinity().toLowerCase().contains(searchTerm) ||
                    place.getTypeInTurkish().toLowerCase().contains(searchTerm)) {
                results.add(place);
            }
        });

        currentFilteredList = results;
        updateListView();
    }

    private void updateListView() {
        ObservableList<Place> displayList = currentFilteredList.isEmpty()
                ? placesList.toObservableList()
                : currentFilteredList.toObservableList();

        listView.setCellFactory(lv -> new PlaceListCell());
        listView.setItems(displayList);
    }

    private void highlightPlaceOnMap(Place place) {
        try {
            if (dataCollectionComplete) {
                String script = String.format(
                        "if(window.highlightPlace) highlightPlace('%s', %f, %f);",
                        place.getId(), place.getLatitude(), place.getLongitude()
                );
                webEngine.executeScript(script);
            }
        } catch (Exception e) {
            System.err.println("Map highlight error: " + e.getMessage());
        }
    }

    private static class PlaceListCell extends ListCell<Place> {
        private final Label nameLabel = new Label();
        private final Label detailsLabel = new Label();
        private final ImageView icon = new ImageView();
        private final HBox content = new HBox(10);

        public PlaceListCell() {
            super();
            configureUI();
        }

        private void configureUI() {
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
            detailsLabel.setStyle("-fx-text-fill: #666;");

            icon.setImage(new Image("https://img.icons8.com/ios-filled/50/2ecc71/marker.png"));
            icon.setFitWidth(24);
            icon.setFitHeight(24);

            content.setAlignment(Pos.CENTER_LEFT);
            content.getChildren().addAll(icon, new VBox(2, nameLabel, detailsLabel));
            setGraphic(content);
        }

        @Override
        protected void updateItem(Place place, boolean empty) {
            super.updateItem(place, empty);
            if (empty || place == null) {
                setGraphic(null);
            } else {
                nameLabel.setText(place.getName());
                detailsLabel.setText(String.format("%s • ⭐%.1f • %s • %s",
                        place.getTypeInTurkish(),
                        place.getRating(),
                        formatDistance(place.getDistance()),
                        formatDuration(place.getDuration())
                ));
                setGraphic(content);
            }
        }

        private String formatDistance(Double distance) {
            if (distance == null) return "Bilinmiyor";
            return distance < 1000 ?
                    String.format("%.0f m", distance) :
                    String.format("%.1f km", distance/1000);
        }

        private String formatDuration(String duration) {
            if (duration == null || duration.isEmpty()) return "Bilinmiyor";

            try {
                String[] parts = duration.split(" ");
                int totalMinutes = 0;

                for (int i=0; i<parts.length; i++) {
                    if (parts[i].equals("hour") && i>0) totalMinutes += 60 * Integer.parseInt(parts[i-1]);
                    if (parts[i].equals("mins") && i>0) totalMinutes += Integer.parseInt(parts[i-1]);
                }

                if (totalMinutes < 60) return totalMinutes + " dakika";
                return String.format("%d saat %d dakika", totalMinutes/60, totalMinutes%60);
            } catch (Exception e) {
                System.err.println("Duration format error: " + duration);
                return "Bilinmiyor";
            }
        }
    }
}