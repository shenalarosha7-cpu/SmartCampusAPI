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

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {
    
    // Connect to our mock database
    private Map<String, Room> rooms = DatabaseClass.getRooms();

    // 1. GET ALL ROOMS
    @GET
    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }

    // 2. GET A SPECIFIC ROOM BY ID
    @GET
    @Path("/{roomId}")
    public Response getRoom(@PathParam("roomId") String roomId) {
        Room room = rooms.get(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("Room not found")
                           .build();
        }
        return Response.ok(room).build();
    }

    // 3. CREATE A NEW ROOM
    @POST
    public Response addRoom(Room room) {
        // Prevent overwriting an existing room
        if (rooms.containsKey(room.getId())) {
            return Response.status(Response.Status.CONFLICT)
                           .entity("Room ID already exists")
                           .build();
        }
        rooms.put(room.getId(), room);
        return Response.status(Response.Status.CREATED).entity(room).build();
    }
    
    // 4. DELETE A ROOM
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = rooms.get(roomId);
        
        // Check if the room actually exists
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("Room not found")
                           .build();
        }
        
        // CRITICAL COURSEWORK RULE: Cannot delete if sensors are attached
        if (!room.getSensorIds().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("Cannot delete room: Sensors are still attached.")
                           .build();
        }
        
        // If it exists and has no sensors, delete it
        rooms.remove(roomId);
        return Response.status(Response.Status.NO_CONTENT).build(); // 204 No Content is standard for successful deletion
    }
}


