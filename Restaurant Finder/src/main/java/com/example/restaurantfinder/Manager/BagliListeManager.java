package com.example.restaurantfinder.Manager;

import com.example.restaurantfinder.Model.Mekan;
import com.example.restaurantfinder.Model.Node;

public class BagliListeManager {
    private Node head;
    private int size;

    public BagliListeManager() {
        head = null;
        size = 0;
    }

    public void mekanEkle(Mekan yeniMekan) {
        Node newNode = new Node(yeniMekan);

        // Boş liste veya yeni eleman en başa eklenmeli
        if (head == null || yeniMekan.getMesafe() < head.getMekan().getMesafe()) {
            newNode.setNext(head);
            head = newNode;
        } else {
            Node current = head;
            while (current.getNext() != null &&
                    current.getNext().getMekan().getMesafe() < yeniMekan.getMesafe()) {
                current = current.getNext();
            }
            newNode.setNext(current.getNext());
            current.setNext(newNode);
        }
        size++;

        // Maksimum 20 düğüm
        if(size > 20) {
            sonDugumuSil();
        }
    }

    private void sonDugumuSil() {
        if(head == null) return;

        Node current = head;
        while(current.getNext() != null && current.getNext().getNext() != null) {
            current = current.getNext();
        }
        current.setNext(null);
        size--;
    }

    public void listeyiYazdir() {
        Node current = head;
        while(current != null) {
            System.out.printf("%s - %.2f km%n",
                    current.getMekan().getAd(),
                    current.getMekan().getMesafe());
            current = current.getNext();
        }
    }
}