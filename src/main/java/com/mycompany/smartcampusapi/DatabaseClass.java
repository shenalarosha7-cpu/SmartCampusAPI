/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampusapi;

/**
 *
 * @author Admin
 */

import java.util.HashMap;
import java.util.Map;

public class DatabaseClass {
    // This static map will hold our rooms in memory
    private static Map<String, Room> rooms = new HashMap<>();

    // Pre-load dummy data when the server starts
    static {
        rooms.put("LIB-301", new Room("LIB-301", "Library Quiet Study", 50));
        rooms.put("CS-101", new Room("CS-101", "Computer Lab 1", 30));
    }

    public static Map<String, Room> getRooms() {
        return rooms;
    }
}
