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
    
    private Map<String, Room> rooms = DatabaseClass.getRooms();

    @GET
    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }

    @GET
    @Path("/{roomId}")
    public Response getRoom(@PathParam("roomId") String roomId) {
        Room room = rooms.get(roomId);
        if (room == null) {
            // Uses ResourceNotFoundMapper (404)
            throw new LinkedResourceNotFoundException("Room with ID " + roomId + " not found.");
        }
        return Response.ok(room).build();
    }

    @POST
    public Response addRoom(Room room) {
        if (rooms.containsKey(room.getId())) {
            // NEW: Use the RoomNotEmptyException for "Conflict" (409)
            throw new RoomNotEmptyException("Room ID " + room.getId() + " already exists.");
        }
        rooms.put(room.getId(), room);
        return Response.status(Response.Status.CREATED).entity(room).build();
    }
    
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = rooms.get(roomId);
        
        if (room == null) {
            throw new LinkedResourceNotFoundException("Room with ID " + roomId + " not found.");
        }
        
        if (!room.getSensorIds().isEmpty()) {
            // NEW: Use RoomNotEmptyException (409) instead of BAD_REQUEST
            // The spec specifically mentions "Conflict" for this rule in many JAX-RS labs
            throw new RoomNotEmptyException("Cannot delete room " + roomId + " because it has attached sensors.");
        }
        
        rooms.remove(roomId);
        return Response.noContent().build(); 
    }
}


