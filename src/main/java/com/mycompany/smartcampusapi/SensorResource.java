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

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private Map<String, Sensor> sensors = DatabaseClass.getSensors();
    private Map<String, Room> rooms = DatabaseClass.getRooms();

    // 1. GET ALL SENSORS (with optional ?type= filter)
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
            throw new ResourceNotFoundException("Sensor with ID '" + sensorId + "' not found.");
        }
        return Response.ok(sensor).build();
    }

    // 3. CREATE A NEW SENSOR
    @POST
    public Response addSensor(Sensor sensor) {
        if (sensors.containsKey(sensor.getId())) {
            return Response.status(Response.Status.CONFLICT)
                           .entity(new ErrorMessage("Sensor ID already exists.", 409))
                           .type(MediaType.APPLICATION_JSON)
                           .build();
        }
        Room room = rooms.get(sensor.getRoomId());
        if (room == null) {
            throw new LinkedResourceNotFoundException(
                "Room with ID '" + sensor.getRoomId() + "' does not exist."
            );
        }
        sensors.put(sensor.getId(), sensor);
        room.getSensorIds().add(sensor.getId());
        return Response.status(Response.Status.CREATED)
                       .entity(sensor)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }

    // 4. DELETE A SENSOR
    @DELETE
    @Path("/{sensorId}")
    public Response deleteSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = sensors.get(sensorId);
        if (sensor == null) {
            throw new ResourceNotFoundException("Sensor with ID '" + sensorId + "' not found.");
        }
        Room room = rooms.get(sensor.getRoomId());
        if (room != null) {
            room.getSensorIds().remove(sensorId);
        }
        sensors.remove(sensorId);
        return Response.noContent().build();
    }

    // 5. SUB-RESOURCE LOCATOR for Readings
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingsResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}
