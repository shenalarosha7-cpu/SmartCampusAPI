/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampusapi.resource;

import com.mycompany.smartcampusapi.ErrorMessage;
import com.mycompany.smartcampusapi.exception.ResourceNotFoundException;
import com.mycompany.smartcampusapi.exception.RoomNotEmptyException;
import com.mycompany.smartcampusapi.dao.RoomDAO;
import com.mycompany.smartcampusapi.model.Room;
import java.util.List;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    private RoomDAO roomDAO = new RoomDAO();

    // GET /api/v1/rooms - Get all rooms
    @GET
    public List<Room> getAllRooms() {
        return roomDAO.getAllRooms();
    }

    // GET /api/v1/rooms/{roomId} - Get one room
    @GET
    @Path("/{roomId}")
    public Response getRoom(@PathParam("roomId") String roomId) {
        Room room = roomDAO.getRoomById(roomId);
        if (room == null) {
            throw new ResourceNotFoundException(
                "Room with ID '" + roomId + "' not found."
            );
        }
        return Response.ok(room)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }

    // POST /api/v1/rooms - Create a new room
    @POST
    public Response addRoom(Room room) {
        if (roomDAO.roomExists(room.getId())) {
            return Response.status(Response.Status.CONFLICT)
                           .entity(new ErrorMessage("Room ID already exists.", 409,
                            "http://localhost:8080/SmartCampusAPI/api/v1"))
                           .type(MediaType.APPLICATION_JSON)
                           .build();
        }
        roomDAO.addRoom(room);
        return Response.status(Response.Status.CREATED)
                       .entity(room)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }

    // DELETE /api/v1/rooms/{roomId} - Delete a room
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = roomDAO.getRoomById(roomId);
        if (room == null) {
            throw new ResourceNotFoundException(
                "Room with ID '" + roomId + "' not found."
            );
        }
        if (roomDAO.roomHasSensors(roomId)) {
            throw new RoomNotEmptyException(
                "Room " + roomId + " cannot be deleted because "
                + "it still has sensors assigned to it."
            );
        }
        roomDAO.deleteRoom(roomId);
        return Response.ok()
                       .entity(new ErrorMessage("Room deleted successfully.", 200,
                        "http://localhost:8080/SmartCampusAPI/api/v1"))
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}


