//package com.example.restaurantfinder;
//
//import com.example.restaurantfinder.Model.Node;
//import com.example.restaurantfinder.Model.Mekan;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//@SpringBootApplication
//@RestController
//public class MainApp {
//
//    private final BagliListeManager liste = new BagliListeManager();
//
//    public static void main(String[] args) {
//        SpringApplication.run(MainApp.class, args);
//    }
//
//    @PostMapping("/mekanlar")
//    public String mekanEkle(@RequestBody List<Mekan> mekanlar) {
//        mekanlar.forEach(liste::mekanEkle);
//        return "Toplam " + mekanlar.size() + " mekan eklendi!";
//    }
//
//    @GetMapping("/listele")
//    public String listele() {
//        liste.listeyiYazdir();
//        return "Liste konsola yazdırıldı";
//    }
//}