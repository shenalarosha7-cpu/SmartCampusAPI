/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampusapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoomDAO {

    private Map<String, Room> rooms = DatabaseClass.getRooms();

    // Get all rooms
    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }

    // Get one room by ID
    public Room getRoomById(String id) {
        return rooms.get(id);
    }

    // Save a new room
    public void addRoom(Room room) {
        rooms.put(room.getId(), room);
    }

    // Delete a room
    public void deleteRoom(String id) {
        rooms.remove(id);
    }

    // Check if room exists
    public boolean roomExists(String id) {
        return rooms.containsKey(id);
    }

    // Check if room has sensors
    public boolean roomHasSensors(String id) {
        Room room = rooms.get(id);
        return room != null && !room.getSensorIds().isEmpty();
    }
}
