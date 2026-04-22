/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampusapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private String sensorId;
    private Map<String, Sensor> sensors = DatabaseClass.getSensors();
    private Map<String, List<SensorReading>> readingsMap = DatabaseClass.getSensorReadings();

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    // GET /api/v1/sensors/{sensorId}/readings
    @GET
    public List<SensorReading> getReadings() {
        // First check if the sensor exists at all
        Sensor parentSensor = sensors.get(sensorId);
        if (parentSensor == null) {
            throw new ResourceNotFoundException(
                "Sensor with ID '" + sensorId + "' not found."
            );
        }
        return readingsMap.getOrDefault(sensorId, new ArrayList<>());
    }

    // POST /api/v1/sensors/{sensorId}/readings
    @POST
    public Response addReading(SensorReading reading) {
        // 1. Check sensor exists
        Sensor parentSensor = sensors.get(sensorId);
        if (parentSensor == null) {
            throw new ResourceNotFoundException(
                "Sensor with ID '" + sensorId + "' not found."
            );
        }

        // 2. PART 5 - State Constraint (403): Block if sensor is MAINTENANCE
        if ("MAINTENANCE".equals(parentSensor.getStatus())) {
            throw new SensorUnavailableException(
                "Sensor " + sensorId + " is currently under MAINTENANCE and cannot accept new readings."
            );
        }

        // 3. Save reading to history
        readingsMap.putIfAbsent(sensorId, new ArrayList<>());
        readingsMap.get(sensorId).add(reading);

        // 4. UPDATE PARENT currentValue (Side Effect requirement from Part 4)
        parentSensor.setCurrentValue(reading.getValue());

        return Response.status(Response.Status.CREATED)
                       .entity(reading)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}
