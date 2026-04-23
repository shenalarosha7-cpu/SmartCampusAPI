/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampusapi;

import java.util.List;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private String sensorId;
    private SensorDAO sensorDAO = new SensorDAO();
    private SensorReadingDAO readingDAO = new SensorReadingDAO();

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    // GET /api/v1/sensors/{sensorId}/readings - Get all readings
    @GET
    public List<SensorReading> getReadings() {
        Sensor sensor = sensorDAO.getSensorById(sensorId);
        if (sensor == null) {
            throw new ResourceNotFoundException(
                "Sensor with ID '" + sensorId + "' not found."
            );
        }
        return readingDAO.getReadings(sensorId);
    }

    // POST /api/v1/sensors/{sensorId}/readings - Add a new reading
    @POST
    public Response addReading(SensorReading reading) {
        // Check sensor exists
        Sensor sensor = sensorDAO.getSensorById(sensorId);
        if (sensor == null) {
            throw new ResourceNotFoundException(
                "Sensor with ID '" + sensorId + "' not found."
            );
        }
        // Part 5 - Block if sensor is under MAINTENANCE
        if ("MAINTENANCE".equals(sensor.getStatus())) {
            throw new SensorUnavailableException(
                "Sensor " + sensorId + " is currently under MAINTENANCE "
                + "and cannot accept new readings."
            );
        }
        // Save reading
        readingDAO.addReading(sensorId, reading);
        // Update parent sensor currentValue (Part 4 side effect)
        sensor.setCurrentValue(reading.getValue());
        return Response.status(Response.Status.CREATED)
                       .entity(reading)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}
