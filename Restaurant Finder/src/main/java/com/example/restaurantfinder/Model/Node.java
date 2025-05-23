package com.example.restaurantfinder.Model;
public class Node {
    private Mekan mekan;
    private Node next;

    public Node(Mekan mekan) {
        this.mekan = mekan;
        this.next = null;
    }

    // Getter ve Setter metodları
    public Mekan getMekan() { return mekan; }
    public Node getNext() { return next; }
    public void setNext(Node next) { this.next = next; }
}