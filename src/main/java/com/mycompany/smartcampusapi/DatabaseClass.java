/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampusapi;

/**
 *
 * @author Admin
 */

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DatabaseClass {
    // Our in-memory maps
    private static Map<String, Room> rooms = new LinkedHashMap<>();
    private static Map<String, Sensor> sensors = new LinkedHashMap<>(); 

    // Pre-load dummy data when the server starts
    static {
        Room lib = new Room("LIB-301", "Library Quiet Study", 50);
        Room cs = new Room("CS-101", "Computer Lab 1", 30);
        rooms.put(lib.getId(), lib);
        rooms.put(cs.getId(), cs);

        // Pre-load dummy sensors
        Sensor s1 = new Sensor("SENS-01", "CO2", 400.5, "LIB-301");
        Sensor s2 = new Sensor("SENS-02", "Occupancy", 15.0, "CS-101");
        sensors.put(s1.getId(), s1);
        sensors.put(s2.getId(), s2);

        // Link the sensor IDs to their respective rooms
        lib.getSensorIds().add(s1.getId());
        cs.getSensorIds().add(s2.getId());
    }

    public static Map<String, Room> getRooms() { return rooms; }
    public static Map<String, Sensor> getSensors() { return sensors; }
}
