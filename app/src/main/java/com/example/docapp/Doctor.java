package com.example.docapp;

public class Doctor {
    private int id;
    private String name;
    private String position;

    public Doctor(int id, String name, String position) {
        this.id = id;
        this.name = name;
        this.position = position;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getPosition() { return position; }
}