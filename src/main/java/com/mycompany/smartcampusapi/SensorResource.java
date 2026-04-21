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

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private Map<String, Sensor> sensors = DatabaseClass.getSensors();
    private Map<String, Room> rooms = DatabaseClass.getRooms();

    // 1. GET ALL SENSORS (Includes optional QueryParam to filter by type)
    @GET
    public List<Sensor> getAllSensors(@QueryParam("type") String type) {
        if (type != null && !type.isEmpty()) {
            List<Sensor> filteredSensors = new ArrayList<>();
            for (Sensor s : sensors.values()) {
                if (s.getType().equalsIgnoreCase(type)) {
                    filteredSensors.add(s);
                }
            }
            return filteredSensors;
        }
        return new ArrayList<>(sensors.values());
    }

    // 2. GET A SPECIFIC SENSOR BY ID
    @GET
    @Path("/{sensorId}")
    public Response getSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = sensors.get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("Sensor not found")
                           .build();
        }
        return Response.ok(sensor).build();
    }

    // 3. CREATE A NEW SENSOR
    @POST
    public Response addSensor(Sensor sensor) {
        // Prevent overwriting
        if (sensors.containsKey(sensor.getId())) {
            return Response.status(Response.Status.CONFLICT)
                           .entity("Sensor ID already exists")
                           .build();
        }

        // Validate that the Room ID provided actually exists
        Room room = rooms.get(sensor.getRoomId());
        if (room == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("Invalid Room ID. The room must exist before adding a sensor.")
                           .build();
        }

        // Save the sensor
        sensors.put(sensor.getId(), sensor);
        
        // Link the sensor to the room's list
        room.getSensorIds().add(sensor.getId());

        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }

    // 4. DELETE A SENSOR
    @DELETE
    @Path("/{sensorId}")
    public Response deleteSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = sensors.get(sensorId);
        
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("Sensor not found")
                           .build();
        }

        // Find the room this sensor belongs to and remove the link
        Room room = rooms.get(sensor.getRoomId());
        if (room != null) {
            room.getSensorIds().remove(sensorId);
        }

        // Finally, delete the sensor itself
        sensors.remove(sensorId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
