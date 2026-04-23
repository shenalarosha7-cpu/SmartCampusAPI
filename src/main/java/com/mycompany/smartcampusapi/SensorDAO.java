/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampusapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SensorDAO {

    private Map<String, Sensor> sensors = DatabaseClass.getSensors();

    // Get all sensors
    public List<Sensor> getAllSensors() {
        return new ArrayList<>(sensors.values());
    }

    // Get sensors filtered by type
    public List<Sensor> getSensorsByType(String type) {
        List<Sensor> filtered = new ArrayList<>();
        for (Sensor s : sensors.values()) {
            if (s.getType().equalsIgnoreCase(type)) {
                filtered.add(s);
            }
        }
        return filtered;
    }

    // Get one sensor by ID
    public Sensor getSensorById(String id) {
        return sensors.get(id);
    }

    // Save a new sensor
    public void addSensor(Sensor sensor) {
        sensors.put(sensor.getId(), sensor);
    }

    // Delete a sensor
    public void deleteSensor(String id) {
        sensors.remove(id);
    }

    // Check if sensor exists
    public boolean sensorExists(String id) {
        return sensors.containsKey(id);
    }
}
