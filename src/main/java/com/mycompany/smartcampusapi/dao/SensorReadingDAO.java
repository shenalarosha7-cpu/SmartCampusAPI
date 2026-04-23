/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampusapi.dao;

import com.mycompany.smartcampusapi.dao.DatabaseClass;
import com.mycompany.smartcampusapi.model.SensorReading;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SensorReadingDAO {

    private Map<String, List<SensorReading>> readingsMap 
        = DatabaseClass.getSensorReadings();

    // Get all readings for a sensor
    public List<SensorReading> getReadings(String sensorId) {
        return readingsMap.getOrDefault(sensorId, new ArrayList<>());
    }

    // Add a reading for a sensor
    public void addReading(String sensorId, SensorReading reading) {
        readingsMap.putIfAbsent(sensorId, new ArrayList<>());
        readingsMap.get(sensorId).add(reading);
    }
}
