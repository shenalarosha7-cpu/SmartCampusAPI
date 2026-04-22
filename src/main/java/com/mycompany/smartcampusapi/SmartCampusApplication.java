/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smartcampusapi;

/**
 *
 * @author Admin
 */
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

// This sets the base URL for your entire API to /api/v1 as requested
@ApplicationPath("/api/v1")
public class SmartCampusApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        
        // Resources
        classes.add(DiscoveryResource.class);
        classes.add(RoomResource.class);
        classes.add(SensorResource.class);

        // Exception Mappers
        classes.add(ResourceNotFoundMapper.class);       
        classes.add(RoomNotEmptyExceptionMapper.class);  
        classes.add(LinkedResourceNotFoundMapper.class); 
        classes.add(SensorUnavailableMapper.class);      
        classes.add(GlobalExceptionMapper.class);   

        // Filter
        classes.add(LoggingFilter.class);
        
        return classes;
    }
}
