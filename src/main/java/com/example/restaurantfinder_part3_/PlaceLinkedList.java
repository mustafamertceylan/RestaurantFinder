package com.example.restaurantfinder_part3_;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class PlaceLinkedList implements Iterable<Place> {

    // Node sınıfı - her bir mekan için
    private class Node {
        Place data;
        Node next;

        Node(Place data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node head;
    private int size;


    public PlaceLinkedList() {
        head = null;
        size = 0;
    }

    // Liste başına ekleme
    public void addFirst(Place place) {
        Node newNode = new Node(place);
        newNode.next = head;
        head = newNode;
        size++;
    }

    // Liste sonuna ekleme
    public void addLast(Place place) {
        Node newNode = new Node(place);

        if (head == null) {
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }

    // ID'ye göre arama
    public Place findById(String name) {
        Node current = head;
        while (current != null) {
            if (current.data.getName().equals(name)) {
                return current.data;
            }
            current = current.next;
        }
        return null;
    }

    // İsme göre arama
    public Place findByName(String name) {
        Node current = head;
        while (current != null) {
            if (current.data.getName().equalsIgnoreCase(name)) {
                return current.data;
            }
            current = current.next;
        }
        return null;
    }
    public int indexOf(Place place) {
        Node current = head;
        int index = 0;
        while (current != null) {
            if (current.data.equals(place)) {
                return index;
            }
            current = current.next;
            index++;
        }
        return -1;
    }

    // Türe göre filtreleme
    public PlaceLinkedList filterByType(String type) {
        PlaceLinkedList filteredList = new PlaceLinkedList();
        Node current = head;

        while (current != null) {
            if (current.data.getType().equals(type)) {
                filteredList.addLast(current.data);
            }
            current = current.next;
        }

        return filteredList;
    }

    // Rating'e göre sıralama (basit bubble sort)
    public void sortByRating() {
        if (size <= 1) return;

        boolean swapped;
        do {
            swapped = false;
            Node current = head;

            while (current.next != null) {
                if (current.data.getRating() < current.next.data.getRating()) {
                    // Swap data
                    Place temp = current.data;
                    current.data = current.next.data;
                    current.next.data = temp;
                    swapped = true;
                }
                current = current.next;
            }
        } while (swapped);
    }

    // ID'ye göre silme
    public boolean removeById(String id) {
        if (head == null) return false;

        if (head.data.getId().equals(id)) {
            head = head.next;
            size--;
            return true;
        }

        Node current = head;
        while (current.next != null) {
            if (current.next.data.getId().equals(id)) {
                current.next = current.next.next;
                size--;
                return true;
            }
            current = current.next;
        }

        return false;
    }

    // Listeyi temizle
    public void clear() {
        head = null;
        size = 0;
    }

    // Liste boyutu
    public int size() {
        return size;
    }

    // Liste boş mu?
    public boolean isEmpty() {
        return head == null;
    }

    // JavaFX ListView için ObservableList'e dönüştürme
    public ObservableList<Place> toObservableList() {
        ObservableList<Place> list = FXCollections.observableArrayList();
        Node current = head;

        while (current != null) {
            list.add(current.data);
            current = current.next;
        }

        return list;
    }

    // Iterator implementasyonu
    @Override
    public Iterator<Place> iterator() {
        return new PlaceIterator();
    }

    private class PlaceIterator implements Iterator<Place> {
        private Node current = head;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public Place next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Place data = current.data;
            current = current.next;
            return data;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PlaceLinkedList [size=").append(size).append("]\n");

        Node current = head;
        while (current != null) {
            sb.append("- ").append(current.data.toString()).append("\n");
            current = current.next;
        }

        return sb.toString();
    }
}