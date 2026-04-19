# Smart Campus Sensor & Room Management API

## Project Overview
[cite_start]This project is a RESTful API developed for the "Smart Campus" initiative at the University of Westminster[cite: 22, 24]. [cite_start]It is built using Java and the JAX-RS (Jersey) framework[cite: 35, 103]. [cite_start]The system manages a campus-wide infrastructure involving thousands of Rooms and various Sensors (such as CO2 monitors and occupancy trackers)[cite: 26, 34]. 

## How to Build and Launch the Server
Follow these steps to get the API running locally:
1. **Prerequisites:** Ensure you have JDK 8 (or higher), Apache NetBeans, and Apache Tomcat 9.0+ installed.
2. **Clone the Repo:** Clone this repository to your local machine using `git clone`.
3. **Open Project:** Open the project in NetBeans.
4. **Build:** Right-click the project name `SmartCampusAPI` and select **Clean and Build**.
5. **Run:** Right-click the project and select **Run**. This will deploy the WAR file to your Tomcat server.
6. **Access:** Once the browser opens, navigate to: `http://localhost:8080/SmartCampusAPI/api/v1`

## Sample API Commands
You can test the API using the following `curl` commands:

1. **Discovery Endpoint:**
   `curl -X GET http://localhost:8080/SmartCampusAPI/api/v1`

2. **List All Rooms:**
   `curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/rooms`

3. **Get Specific Room:**
   `curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/rooms/LIB-301`

4. **List All Sensors:**
   `curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/sensors`

5. **Filter Sensors by Type:**
   `curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/sensors?type=CO2`

---

## Part 1: Service Architecture Report

### Question 1: Default Lifecycle of a JAX-RS Resource
**Explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this impacts managing in-memory data structures.**

[cite_start]**Answer:** By default, JAX-RS resources are **request-scoped**[cite: 105, 106]. [cite_start]This means the JAX-RS runtime creates a new instance of the resource class for every incoming HTTP request and discards it once the response is sent[cite: 106]. 

Because the instance is destroyed after each request, standard instance variables cannot be used to store data, as they would be reset constantly. [cite_start]To prevent data loss, we must use **static** data structures (like `static HashMap`) or a separate Singleton provider[cite: 107]. This ensures that data persists in memory across different requests. [cite_start]Furthermore, since multiple requests can occur simultaneously, we must use thread-safe collections (like `ConcurrentHashMap`) or synchronization blocks to prevent race conditions during data access[cite: 107].

### Question 2: The Importance of Hypermedia (HATEOAS)
**Why is the provision of "Hypermedia" (links and navigation within responses) considered a hallmark of advanced RESTful design (HATEOAS)? How does this approach benefit client developers?**

[cite_start]**Answer:** Hypermedia as the Engine of Application State (HATEOAS) is a hallmark of advanced REST because it makes the API self-descriptive[cite: 110]. [cite_start]Instead of requiring the client to have hard-coded knowledge of every URL, the server provides links within the JSON response that guide the client on what actions are currently possible[cite: 110].

[cite_start]This benefits client developers by reducing coupling between the client and the server[cite: 111]. [cite_start]If the server’s URL structure changes, the client doesn't "break" because it follows dynamic links provided in the response rather than relying on static, external documentation[cite: 111]. It allows the API to evolve more easily without forcing immediate updates to all client-side code.
