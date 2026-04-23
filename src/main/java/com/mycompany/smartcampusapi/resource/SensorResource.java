/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampusapi.resource;

import com.mycompany.smartcampusapi.ErrorMessage;
import com.mycompany.smartcampusapi.exception.LinkedResourceNotFoundException;
import com.mycompany.smartcampusapi.exception.ResourceNotFoundException;
import com.mycompany.smartcampusapi.dao.SensorDAO;
import com.mycompany.smartcampusapi.dao.RoomDAO;
import com.mycompany.smartcampusapi.model.Sensor;
import com.mycompany.smartcampusapi.model.Room;
import java.util.List;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private SensorDAO sensorDAO = new SensorDAO();
    private RoomDAO roomDAO = new RoomDAO();

    // GET /api/v1/sensors - Get all sensors (optional ?type= filter)
    @GET
    public List<Sensor> getAllSensors(@QueryParam("type") String type) {
        if (type != null && !type.isEmpty()) {
            return sensorDAO.getSensorsByType(type);
        }
        return sensorDAO.getAllSensors();
    }

    // GET /api/v1/sensors/{sensorId} - Get one sensor
    @GET
    @Path("/{sensorId}")
    public Response getSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = sensorDAO.getSensorById(sensorId);
        if (sensor == null) {
            throw new ResourceNotFoundException(
                "Sensor with ID '" + sensorId + "' not found."
            );
        }
        return Response.ok(sensor)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }

    // POST /api/v1/sensors - Register a new sensor
    @POST
    public Response addSensor(Sensor sensor) {
        if (sensorDAO.sensorExists(sensor.getId())) {
            return Response.status(Response.Status.CONFLICT)
                           .entity(new ErrorMessage("Sensor ID already exists.", 409,
                            "http://localhost:8080/SmartCampusAPI/api/v1"))
                           .type(MediaType.APPLICATION_JSON)
                           .build();
        }
        if (!roomDAO.roomExists(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException(
                "Room with ID '" + sensor.getRoomId() + "' does not exist."
            );
        }
        sensorDAO.addSensor(sensor);
        // Link sensor ID to the room's sensorIds list
        roomDAO.getRoomById(sensor.getRoomId())
               .getSensorIds().add(sensor.getId());
        return Response.status(Response.Status.CREATED)
                       .entity(sensor)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }

    // DELETE /api/v1/sensors/{sensorId} - Remove a sensor
    @DELETE
    @Path("/{sensorId}")
    public Response deleteSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = sensorDAO.getSensorById(sensorId);
        if (sensor == null) {
            throw new ResourceNotFoundException(
                "Sensor with ID '" + sensorId + "' not found."
            );
        }
        // Unlink sensor from its room
        Room room = roomDAO.getRoomById(sensor.getRoomId());
        if (room != null) {
            room.getSensorIds().remove(sensorId);
        }
        sensorDAO.deleteSensor(sensorId);
        return Response.noContent().build();
    }

    // SUB-RESOURCE LOCATOR - Delegates to SensorReadingResource
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingsResource(
            @PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}
