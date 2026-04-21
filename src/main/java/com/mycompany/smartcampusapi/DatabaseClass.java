/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampusapi;

/**
 *
 * @author Admin
 */

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DatabaseClass {
    // We use LinkedHashMap to preserve the order in which items are added
    private static Map<String, Room> rooms = new LinkedHashMap<>();
    private static Map<String, Sensor> sensors = new LinkedHashMap<>();
    private static Map<String, List<SensorReading>> sensorReadings = new LinkedHashMap<>();

    // Static block to initialize the "Database" when the server starts
    static {
        // 1. Create Rooms
        Room lib = new Room("LIB-301", "Library Quiet Study", 50);
        Room cs = new Room("CS-101", "Computer Lab 1", 30);
        rooms.put(lib.getId(), lib);
        rooms.put(cs.getId(), cs);

        // 2. Create Sensors (Updated to use status "ACTIVE" and currentValue)
        // Constructor: id, type, status, currentValue, roomId
        Sensor s1 = new Sensor("SENS-01", "CO2", "ACTIVE", 400.5, "LIB-301");
        Sensor s2 = new Sensor("SENS-02", "Occupancy", "ACTIVE", 15.0, "CS-101");
        
        sensors.put(s1.getId(), s1);
        sensors.put(s2.getId(), s2);

        // 3. Link Sensors to Rooms (Crucial for Part 2 & 3 logic)
        lib.getSensorIds().add(s1.getId());
        cs.getSensorIds().add(s2.getId());

        // 4. Initialize Sensor Reading History (Part 4 requirement)
        List<SensorReading> sens01Readings = new ArrayList<>();
        // Add one initial reading to SENS-01 for testing GET requests
        sens01Readings.add(new SensorReading("READ-001", System.currentTimeMillis(), 400.5));
        sensorReadings.put("SENS-01", sens01Readings);
        
        // Ensure other sensors have an empty list ready instead of null
        sensorReadings.put("SENS-02", new ArrayList<>());
    }

    // Static getters to access the maps from Resource classes
    public static Map<String, Room> getRooms() {
        return rooms;
    }

    public static Map<String, Sensor> getSensors() {
        return sensors;
    }

    public static Map<String, List<SensorReading>> getSensorReadings() {
        return sensorReadings;
    }
}
