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
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    // The ID passed down from the parent SensorResource
    private String sensorId;
    
    private Map<String, Sensor> sensors = DatabaseClass.getSensors();
    private Map<String, List<SensorReading>> readingsMap = DatabaseClass.getSensorReadings();

    // Constructor to receive the sensor ID from the parent
    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    // GET /api/v1/sensors/{sensorId}/readings
    @GET
    public List<SensorReading> getReadings() {
        // Return the list of readings, or an empty list if none exist
        return readingsMap.getOrDefault(sensorId, new ArrayList<>());
    }

    // POST /api/v1/sensors/{sensorId}/readings
    @POST
    public Response addReading(SensorReading reading) {
        Sensor parentSensor = sensors.get(sensorId);

        if (parentSensor == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Sensor not found").build();
        }

        // 1. Save reading to history
        readingsMap.putIfAbsent(sensorId, new ArrayList<>());
        readingsMap.get(sensorId).add(reading);

        // 2. UPDATE PARENT (This is the "Side Effect" requirement)
        parentSensor.setCurrentValue(reading.getValue()); 

        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}
