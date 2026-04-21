/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampusapi;

/**
 *
 * @author Admin
 */

public class Sensor {
    private String id;
    private String type; // e.g., "CO2", "Occupancy", "Temperature"
    private double value;
    private String roomId; // Links the sensor to a specific room

    // Default constructor required for JSON translation
    public Sensor() {}

    // Constructor for testing
    public Sensor(String id, String type, double value, String roomId) {
        this.id = id;
        this.type = type;
        this.value = value;
        this.roomId = roomId;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
}
