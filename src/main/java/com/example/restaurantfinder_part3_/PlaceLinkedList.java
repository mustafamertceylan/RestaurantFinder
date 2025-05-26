package com.example.restaurantfinder_part3_;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PlaceLinkedList {
    private Node head;
    private int size;

    // Node inner class
    private static class Node {
        Place data;
        Node next;

        Node(Place data) {
            this.data = data;
        }
    }

    // Add a place to the end of the list
    public void add(Place place) {
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

    // Add a place at the end (alias for add)
    public void addLast(Place place) {
        add(place);
    }

    // Check if list contains a place with given ID
    public boolean contains(String id) {
        Node current = head;
        while (current != null) {
            if (current.data.getId().equals(id)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    // Clear the list
    public void clear() {
        head = null;
        size = 0;
    }

    // Check if list is empty
    public boolean isEmpty() {
        return head == null;
    }

    // Get the size of the list
    public int size() {
        return size;
    }

    // Convert to ObservableList for JavaFX
    public ObservableList<Place> toObservableList() {
        ObservableList<Place> result = FXCollections.observableArrayList();
        Node current = head;
        while (current != null) {
            result.add(current.data);
            current = current.next;
        }
        return result;
    }

    // For each implementation
    public void forEach(PlaceConsumer action) {
        Node current = head;
        while (current != null) {
            action.accept(current.data);
            current = current.next;
        }
    }

    // Sort the list using a comparator
    public void sort(PlaceComparator comparator) {
        if (head == null || head.next == null) return;

        boolean swapped;
        do {
            swapped = false;
            Node previous = null;
            Node current = head;
            Node next = head.next;

            while (next != null) {
                if (comparator.compare(current.data, next.data) > 0) {
                    // Swap nodes
                    if (previous == null) {
                        head = next;
                    } else {
                        previous.next = next;
                    }
                    current.next = next.next;
                    next.next = current;

                    previous = next;
                    next = current.next;
                    swapped = true;
                } else {
                    previous = current;
                    current = next;
                    next = next.next;
                }
            }
        } while (swapped);
    }

    @FunctionalInterface
    public interface PlaceConsumer {
        void accept(Place place);
    }

    @FunctionalInterface
    public interface PlaceComparator {
        int compare(Place p1, Place p2);
    }
}