# Smart Campus Sensor & Room Management API

**Module:** 5COSC022W — Client-Server Architectures  
**Title:** Smart Campus RESTful API  
**Technology:** JAX-RS (Jersey) · Java 8 · Maven · Apache NetBeans  
**Base URL:** `http://localhost:8080/SmartCampusAPI/api/v1`

---

## Table of Contents

1. [API Overview](#api-overview)
2. [Project Structure](#project-structure)
3. [Build & Run Instructions](#build--run-instructions)
4. [Sample curl Commands](#sample-curl-commands)
5. [Report — Question Answers](#report--question-answers)

---

## API Overview

This RESTful API forms the backend infrastructure for a university Smart Campus initiative. It manages two core domain resources — **Rooms** and **Sensors** — along with a historical log of **Sensor Readings** stored per sensor. All data is persisted in-memory using `ConcurrentHashMap` and `ArrayList` data structures; no database is used.

### Resource Hierarchy

```
/api/v1                              → Discovery endpoint (metadata + links)
/api/v1/rooms                        → Room collection
/api/v1/rooms/{roomId}               → Single room
/api/v1/sensors                      → Sensor collection (supports ?type= filter)
/api/v1/sensors/{sensorId}           → Single sensor
/api/v1/sensors/{sensorId}/readings  → Sub-resource: reading history for a sensor
```

### Endpoint Summary

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1` | Discovery — API metadata and resource links |
| GET | `/api/v1/rooms` | List all rooms |
| POST | `/api/v1/rooms` | Create a new room |
| GET | `/api/v1/rooms/{roomId}` | Get a specific room |
| DELETE | `/api/v1/rooms/{roomId}` | Delete a room (blocked if sensors are assigned) |
| GET | `/api/v1/sensors` | List all sensors (optional `?type=` filter) |
| POST | `/api/v1/sensors` | Register a new sensor |
| GET | `/api/v1/sensors/{sensorId}` | Get a specific sensor |
| DELETE | `/api/v1/sensors/{sensorId}` | Remove a sensor |
| GET | `/api/v1/sensors/{sensorId}/readings` | Get all readings for a sensor |
| POST | `/api/v1/sensors/{sensorId}/readings` | Append a new reading for a sensor |

### Data Models

**Room**
```json
{
  "id": "LIB-301",
  "name": "Library Quiet Study",
  "capacity": 50,
  "sensorIds": ["TEMP-001", "CO2-002"]
}
```

**Sensor**
```json
{
  "id": "TEMP-001",
  "type": "Temperature",
  "status": "ACTIVE",
  "currentValue": 22.5,
  "roomId": "LIB-301"
}
```

**SensorReading**
```json
{
  "id": "READ-001",
  "timestamp": 1713700000000,
  "value": 23.1
}
```

### Error Response Format

All errors return a consistent JSON body:
```json
{
  "errorMessage": "Room LIB-301 cannot be deleted because it still has sensors assigned.",
  "errorCode": 409,
  "documentation": "http://localhost:8080/SmartCampusAPI/api/v1"
}
```

### HTTP Status Codes Used

| Code | Scenario |
|------|----------|
| 200 OK | Successful GET |
| 201 Created | Successful POST (resource created) |
| 404 Not Found | Resource ID does not exist |
| 403 Forbidden | POST reading to a sensor in MAINTENANCE status |
| 409 Conflict | DELETE room that still has sensors assigned |
| 422 Unprocessable Entity | POST sensor with a `roomId` that does not exist |
| 500 Internal Server Error | Unexpected runtime error (global safety net) |

---

## Project Structure

```
smartcampusapi/
├── pom.xml
└── src/
    └── main/
        └── java/
            └── com/mycompany/smartcampusapi/
                │
                ├── model/                          # Data Objects (POJOs)
                │   ├── Room.java
                │   ├── Sensor.java
                │   └── SensorReading.java
                │
                ├── dao/                            # Data Access Layer
                │   ├── DatabaseClass.java
                │   ├── RoomDAO.java
                │   ├── SensorDAO.java
                │   └── SensorReadingDAO.java
                │
                ├── resource/                       # API Endpoints (JAX-RS)
                │   ├── DiscoveryResource.java
                │   ├── RoomResource.java
                │   ├── SensorResource.java
                │   └── SensorReadingResource.java
                │
                ├── exception/                      # Error Handling
                │   ├── RoomNotEmptyException.java
                │   ├── RoomNotEmptyExceptionMapper.java
                │   ├── LinkedResourceNotFoundException.java
                │   ├── LinkedResourceNotFoundMapper.java
                │   ├── SensorUnavailableException.java
                │   ├── SensorUnavailableMapper.java
                │   ├── ResourceNotFoundException.java
                │   ├── ResourceNotFoundMapper.java
                │   └── GlobalExceptionMapper.java
                │
                ├── SmartCampusApplication.java     # Config & Bootstrap
                ├── AppStartupListener.java        
                ├── ErrorMessage.java               
                └── LoggingFilter.java
```

---

## Build & Run Instructions

### Prerequisites

- **Java 8 or higher** — verify with `java -version`
- **Apache NetBeans IDE 28** with Maven and Tomcat support built in
- No database or external service required

### Step 1 — Clone the Repository

git clone https://github.com/shenalarosha7-cpu/smartcampusapi.git

Or download and open the project folder directly in Apache NetBeans.

### Step 2 — Open Project in NetBeans

1. Open **Apache NetBeans**
2. Click **File → Open Project**
3. Navigate to the cloned or downloaded project folder
4. Click **Open Project**

### Step 3 — Build the Project

1. Right-click the project name in the **Projects panel**
2. Click **Clean and Build**
3. Wait for **BUILD SUCCESS** in the Output panel at the bottom

### Step 4 — Run the Server

1. Right-click the project name
2. Click **Run** (or press **F6**)
3. NetBeans will automatically deploy the project to the
   built-in Apache Tomcat server
4. A browser window may open automatically — you can close it
   and use Postman for testing instead

### Step 5 — Verify the Server is Running

Open your browser or Postman and go to:

http://localhost:8080/SmartCampusAPI/api/v1

Expected response:

{
  "admin_contact": "admin@westminster.ac.uk",
  "resources": {
    "rooms": "/api/v1/rooms",
    "sensors": "/api/v1/sensors"
  },
  "version": "v1"
}

### Stopping the Server

In the NetBeans Output panel at the bottom, click the
red **Stop** button to shut down the Tomcat server.

---

## Sample curl Commands

The following ten commands demonstrate successful interactions across all parts of the API. Run them in sequence for a complete end-to-end walkthrough.

---

### 1. Discovery Endpoint — GET `/api/v1`

Returns API metadata including version, contact, and navigable resource links.

```bash
curl -s -X GET http://localhost:8080/SmartCampusAPI/api/v1 \
  -H "Accept: application/json"
```

**Expected response (200 OK):**
```json
{
  "admin_contact": "admin@westminster.ac.uk",
  "resources": {
    "rooms": "/api/v1/rooms",
    "sensors": "/api/v1/sensors"
  },
  "version": "v1"
}
```

---

### 2. Create a Room — POST `/api/v1/rooms`

Registers a new room in the system.

```bash
curl -s -X POST http://localhost:8080/SmartCampusAPI/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id":"LIB-301","name":"Library Quiet Study","capacity":50}'
```

**Expected response (201 Created):**
```json
{
  "id": "LIB-301",
  "name": "Library Quiet Study",
  "capacity": 50,
  "sensorIds": []
}
```

---

### 3. Register a Sensor — POST `/api/v1/sensors`

Creates a new sensor linked to the room created above. The API validates that the `roomId` exists.

```bash
curl -s -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"TEMP-001","type":"Temperature","status":"ACTIVE","currentValue":20.0,"roomId":"LIB-301"}'
```

**Expected response (201 Created):**
```json
{
  "id": "TEMP-001",
  "type": "Temperature",
  "status": "ACTIVE",
  "currentValue": 20.0,
  "roomId": "LIB-301"
}
```

---

### 4. Add a Second Sensor (CO2 Type)

```bash
curl -s -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"CO2-001","type":"CO2","status":"ACTIVE","currentValue":400.0,"roomId":"LIB-301"}'
```

**Expected response (201 Created):**
```json
{
  "id": "CO2-001",
  "type": "CO2",
  "status": "ACTIVE",
  "currentValue": 400.0,
  "roomId": "LIB-301"
}
```

---

### 5. Get All Sensors Filtered by Type — GET `/api/v1/sensors?type=CO2`

Demonstrates `@QueryParam` filtering. Only sensors of type `CO2` are returned.

```bash
curl -s -X GET "http://localhost:8080/SmartCampusAPI/api/v1/sensors?type=CO2" \
  -H "Accept: application/json"
```

**Expected response (200 OK):**
```json
[
  {
    "id": "CO2-001",
    "type": "CO2",
    "status": "ACTIVE",
    "currentValue": 400.0,
    "roomId": "LIB-301"
  }
]
```

---

### 6. Post a Sensor Reading — POST `/api/v1/sensors/TEMP-001/readings`

Appends a new reading to the sensor's history and updates `currentValue` on the parent sensor.

```bash
curl -s -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d '{"id":"READ-001","timestamp":1713700000000,"value":23.5}'
```

**Expected response (201 Created):**
```json
{
  "id": "READ-001",
  "timestamp": 1713700000000,
  "value": 23.5
}
```

After this call, `GET /api/v1/sensors/TEMP-001` will show `"currentValue": 23.5`.

---

### 7. Get All Readings for a Sensor — GET `/api/v1/sensors/TEMP-001/readings`

Fetches the historical reading log for sensor `TEMP-001`.

```bash
curl -s -X GET http://localhost:8080/SmartCampusAPI/api/v1/sensors/TEMP-001/readings \
  -H "Accept: application/json"
```

**Expected response (200 OK):**
```json
[
  {
    "id": "READ-001",
    "timestamp": 1713700000000,
    "value": 23.5
  }
]
```

---

### 8. Attempt to Delete a Room with Sensors — DELETE `/api/v1/rooms/LIB-301` (Blocked)

Demonstrates the `RoomNotEmptyException` → HTTP 409 safety constraint.

```bash
curl -s -X DELETE http://localhost:8080/SmartCampusAPI/api/v1/rooms/LIB-301 \
  -H "Accept: application/json"
```

**Expected response (409 Conflict):**
```json
{
  "errorMessage": "Room LIB-301 cannot be deleted because it still has sensors assigned to it.",
  "errorCode": 409,
  "documentation": "http://localhost:8080/SmartCampusAPI/api/v1"
}
```

---

### 9. Attempt to Register a Sensor with a Non-Existent Room — HTTP 422

Demonstrates `LinkedResourceNotFoundException` → HTTP 422.

```bash
curl -s -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"HUM-001","type":"Humidity","status":"ACTIVE","currentValue":55.0,"roomId":"GHOST-999"}'
```

**Expected response (422 Unprocessable Entity):**
```json
{
  "errorMessage": "Room with ID 'GHOST-999' does not exist.",
  "errorCode": 422,
  "documentation": "http://localhost:8080/SmartCampusAPI/api/v1"
}
```

---

### 10. Attempt to Post a Reading to a MAINTENANCE Sensor — HTTP 403

First, create a sensor in MAINTENANCE status, then attempt to post a reading to it.

```bash
curl -s -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"OCC-001","type":"Occupancy","status":"MAINTENANCE","currentValue":0.0,"roomId":"LIB-301"}'
```

```bash
curl -s -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors/OCC-001/readings \
  -H "Content-Type: application/json" \
  -d '{"id":"READ-002","timestamp":1713700001000,"value":12.0}'
```

**Expected response (403 Forbidden):**
```json
{
  "errorMessage": "Sensor OCC-001 is currently under MAINTENANCE and cannot accept new readings.",
  "errorCode": 403,
  "documentation": "http://localhost:8080/SmartCampusAPI/api/v1"
}
```

---

## Report — Question Answers

### Part 1.1 — JAX-RS Resource Lifecycle

**Question:** Explain the default lifecycle of a JAX-RS resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? How does this impact managing in-memory data structures?

**Answer:**

By default, the JAX-RS specification mandates a **per-request lifecycle** for resource classes. This means the runtime — whether Jersey or RESTEasy — creates a brand-new instance of every resource class for each individual HTTP request that matches its path, and discards that instance once the response has been sent. This is the specification-default behaviour and applies unless the class is explicitly annotated with `@Singleton` or registered as a singleton via the `Application` subclass.

This design decision has a direct and critical impact on how in-memory data must be managed. Because each request produces a fresh resource instance, any instance-level fields (e.g., a `HashMap` declared as a field inside `RoomResource`) would be re-initialised on every request, meaning all data would be lost between calls. To prevent this, shared state must be stored outside the resource instance — in a **static, class-level data store**. In this project, the `DatabaseClass` holds all three maps (`rooms`, `sensors`, and `sensorReadings`) as `static` fields, making them effectively global singletons that persist across the lifetime of the JVM process regardless of how many resource instances are created.

Furthermore, because multiple requests can arrive concurrently, each spawning its own resource instance but all reading from and writing to the same static maps simultaneously, race conditions become a real concern. To address this, `ConcurrentHashMap` is used instead of a plain `HashMap`. `ConcurrentHashMap` provides thread-safe read and write operations without requiring explicit `synchronized` blocks, ensuring that concurrent requests do not corrupt the data store or produce inconsistent reads.

---

### Part 1.2 — HATEOAS and Hypermedia-Driven Design

**Question:** Why is the provision of "Hypermedia" (HATEOAS) considered a hallmark of advanced RESTful design? How does this benefit client developers compared to static documentation?

**Answer:**

HATEOAS — Hypermedia As The Engine Of Application State — is Constraint 6 of Roy Fielding's original REST architectural definition, making it one of the defining characteristics of a truly RESTful system rather than simply an HTTP-based API.

The core idea is that API responses should include **links to related resources and valid next actions**, rather than expecting clients to construct URLs themselves. For example, a response to `GET /api/v1/rooms/LIB-301` in a fully HATEOAS-compliant API might include a `_links` object pointing to `self`, `sensors`, and `delete` endpoints for that specific room.

The benefit over static documentation is significant. With static documentation, the API contract is external to the data itself — a client developer reads a PDF or a Swagger page, then hard-codes URL structures into their application. If the API is versioned, restructured, or any endpoint path changes, every client breaks silently until developers manually read the updated documentation and redeploy their code.

With HATEOAS, the API becomes **self-describing at runtime**. A client only needs to know the single root entry point (`/api/v1`) and can discover all other resources by following links embedded in the responses. This reduces coupling between server and client, allows the server to evolve its URL structure without breaking compliant clients, and enables automated clients to navigate the API without any human-readable documentation. It also makes the API explorable — a developer can navigate the entire API surface by following links, much like browsing a website rather than memorising a sitemap.

---

### Part 2.1 — Returning IDs vs Full Objects in List Responses

**Question:** When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client-side processing.

**Answer:**

Returning **only IDs** in a list response results in smaller payloads and lower network bandwidth consumption, but it forces every client to make N additional requests — one per ID — to retrieve the actual data it needs. This is the classic N+1 request problem. For a list of 200 rooms, a client would make 201 HTTP requests in total: one to fetch the list, and 200 individual `GET /rooms/{id}` calls. This dramatically increases latency, network overhead, and server load, particularly problematic for remote clients or mobile devices on slow connections.

Returning **full objects** in the list response delivers all data in a single round trip, eliminating the N+1 problem and reducing client-side complexity significantly. The trade-off is a larger response payload per request. However, for the typical size of a campus room object — a handful of string and integer fields — the payload difference is negligible compared to the overhead of hundreds of additional HTTP requests.

In practice, the preferred approach is to return full objects in list responses, with the option to add **sparse fieldsets** (`?fields=id,name`) if payload size ever becomes a genuine concern at scale. This provides the best developer experience and the best runtime performance in the common case while preserving the ability to optimise later. This project returns full `Room` objects from `GET /api/v1/rooms`.

---

### Part 2.2 — DELETE Idempotency

**Question:** Is the DELETE operation idempotent in your implementation? Provide a detailed justification by describing what happens if a client mistakenly sends the exact same DELETE request for a room multiple times.

**Answer:**

**Yes**, the DELETE operation is idempotent in this implementation, consistent with the HTTP specification (RFC 9110), which states that a method is idempotent if the intended effect on the server is the same whether it is applied once or multiple times.

Consider the following sequence: a client sends `DELETE /api/v1/rooms/LIB-301`. The first request checks the room exists, verifies no sensors are assigned, removes the room from the map, and returns `200 OK`. The **second** identical request finds that `LIB-301` no longer exists in the map, and the implementation throws a `ResourceNotFoundException`, returning `404 Not Found`.

Idempotency does not require the **response code** to be identical on each call — it requires the **server state** to be the same after each repeated call. After the first DELETE, the room is absent from the system. After the second DELETE, the room is still absent from the system. The server state has not changed as a result of the second request. Therefore, the operation is idempotent by definition.

This is the standard and expected behaviour for DELETE in RESTful APIs. A well-behaved client can safely retry a DELETE request after a network timeout without fear of causing unintended side effects, since the end state — the resource being gone — is identical whether the request succeeds once or is repeated multiple times.

---

### Part 3.1 — @Consumes(MediaType.APPLICATION_JSON) Consequences

**Question:** We explicitly use `@Consumes(MediaType.APPLICATION_JSON)` on the POST method. Explain the technical consequences if a client attempts to send data in a different format such as `text/plain` or `application/xml`. How does JAX-RS handle this mismatch?

**Answer:**

The `@Consumes` annotation declares the media types that a resource method is capable of accepting and processing. When JAX-RS receives an incoming request, it performs **content negotiation** by comparing the `Content-Type` header of the request against the media types declared in `@Consumes`.

If a client sends a POST request to `/api/v1/sensors` with `Content-Type: text/plain` or `Content-Type: application/xml`, JAX-RS will be unable to match the request to any resource method annotated with `@Consumes(MediaType.APPLICATION_JSON)`. The runtime does not attempt to parse the body or invoke the method. Instead, it immediately rejects the request and returns **HTTP 415 Unsupported Media Type** to the caller, before any application code executes.

This is the correct and safe behaviour: the rejection happens at the framework layer, meaning the resource method body is never reached with malformed or unexpected input. From a robustness standpoint, this eliminates an entire class of input handling bugs. The developer does not need to write defensive checks inside the method to detect whether the body was sent as JSON or XML — the framework contract enforces this at the routing level. It also communicates a clear, machine-readable contract to API consumers: only `application/json` is accepted, and any other content type will be explicitly rejected before any processing occurs.

---

### Part 3.2 — @QueryParam vs Path Parameter for Filtering

**Question:** You implemented filtering using `@QueryParam`. Contrast this with an alternative design where the type is part of the URL path (e.g., `/api/v1/sensors/type/CO2`). Why is the query parameter approach generally considered superior for filtering and searching collections?

**Answer:**

The fundamental distinction is one of **resource identity versus resource filtering**. In REST, a URL path uniquely identifies a resource or a collection. Path parameters are appropriate when each segment narrows the identity of the resource being addressed. For example, `/api/v1/sensors/TEMP-001` unambiguously identifies a single, specific sensor. Each path segment defines what the resource *is*.

In contrast, `?type=CO2` is a **view constraint** on an existing collection — it describes a subset of `/api/v1/sensors` without implying that `CO2` is itself a resource. The full collection still exists at `/api/v1/sensors`; the query parameter simply filters the response.

Using a path segment for filtering (`/api/v1/sensors/type/CO2`) creates several problems. First, it treats `type` as a nested resource layer, implying that `type` is a resource identifier, which is semantically incorrect. Second, it does not compose — combining multiple filters requires either deeply nested paths (`/sensors/type/CO2/status/ACTIVE`) or a completely separate URL scheme, both of which are brittle and hard to extend. Third, it conflicts with sub-resource patterns: `/sensors/{sensorId}/readings` and `/sensors/type/CO2` use the same positional path segment, creating ambiguity that the router must resolve with explicit ordering or type checking.

Query parameters, by contrast, compose naturally (`?type=CO2&status=ACTIVE`), are optional by design (the collection works without them), are universally understood as filters in REST conventions, and leave the path free to represent true resource identity and hierarchy. For these reasons, `@QueryParam` is the correct and idiomatic tool for filtering, searching, and sorting collections in JAX-RS.

---

### Part 4.1 — Sub-Resource Locator Pattern

**Question:** Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path in one massive controller class?

**Answer:**

The Sub-Resource Locator pattern allows a resource method to return an object instance rather than a `Response`, delegating further request routing to that returned object. In this project, `SensorResource` contains a method annotated with `@Path("{sensorId}/readings")` that returns a new `SensorReadingResource` instance, and JAX-RS continues matching the remaining path segments against the methods of that returned object.

The architectural benefits are substantial. The most immediate is **separation of concerns**: `SensorResource` is responsible for sensor CRUD operations, and `SensorReadingResource` is solely responsible for reading history management. Each class has a single, well-defined responsibility, making it independently testable, readable, and maintainable.

In a large API without sub-resource locators, every path would be defined in monolithic resource classes. A single `SensorResource` might handle sensors, readings, alerts, calibration schedules, and maintenance logs, growing to hundreds of methods. Sub-resource delegation keeps classes small and focused.

There is also a **contextual injection benefit**: the `SensorReadingResource` constructor receives the `sensorId` as a constructor argument at instantiation time. This means every method inside `SensorReadingResource` already has the correct sensor context without needing to repeat path parameter injection. If the sensor resolution logic needs to change, it changes in one place.

Finally, this pattern supports **lazy instantiation**: the sub-resource object is only constructed when the request path actually reaches that branch, and different sub-resource classes can be returned based on runtime conditions, enabling dynamic dispatch patterns that a flat, annotation-driven class hierarchy cannot express.

---

### Part 5.2 — HTTP 422 vs HTTP 404 for Missing Referenced Resources

**Question:** Why is HTTP 422 often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?

**Answer:**

HTTP 404 Not Found means that the **requested resource** — identified by the URL of the request — could not be located on the server. In the case of `POST /api/v1/sensors`, the URL is valid and the endpoint exists. The issue is not that `/api/v1/sensors` is missing; it is entirely present and functioning. Returning 404 would be misleading because it implies the client navigated to a URL that does not exist, when in fact the problem is inside the request body.

HTTP 422 Unprocessable Entity (defined in RFC 4918 and carried into HTTP semantics) means that the server understood the request, the content type is correct, the JSON is syntactically valid — but the **semantic content** of the payload is invalid and cannot be processed. A `roomId` field that references a room which does not exist is precisely this kind of semantic error: the JSON is well-formed, the field name is recognised, but the value points to a resource that the server cannot resolve as a valid dependency.

This distinction matters for client developers. A 404 response tells a client "the thing you were looking for doesn't exist", which for an automated client might trigger retry logic or cache invalidation. A 422 response tells a client "your request data is semantically incorrect — fix the payload before retrying." These are fundamentally different signals that should produce different client behaviours. Using 422 instead of 404 therefore provides a more precise, machine-readable error contract that guides clients toward the correct corrective action.

---

### Part 5.4 — Stack Trace Exposure Security Risks

**Question:** From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?

**Answer:**

Exposing raw Java stack traces in API responses is a serious information disclosure vulnerability. A stack trace provides an attacker with detailed internal intelligence about the application that can be used to plan and execute targeted attacks.

**Technology fingerprinting:** A stack trace immediately reveals the exact software stack — the Java version, the JAX-RS implementation (e.g., Jersey 2.x), the servlet container, and any third-party libraries involved. An attacker can cross-reference these versions against public vulnerability databases (CVE, NVD) and identify known exploits that apply to this exact version combination.

**Application structure disclosure:** Class names, package names, and method signatures in the trace reveal the internal architecture of the application — which packages exist, how they are named, and how methods call one another. This significantly reduces the effort required for reverse engineering.

**Attack vector identification:** Specific exception types and the exact line in the source where the exception was thrown indicate precisely what kind of unexpected input caused the failure. A `NullPointerException` at a specific method tells an attacker which input field the application failed to validate, making it straightforward to probe that field further or craft malformed inputs that trigger deeper failures.

**Business logic revelation:** Stack traces through business logic code reveal internal processing steps, branching conditions, and data flow, which can be exploited to bypass security controls or manipulate application behaviour.

The global `ExceptionMapper<Throwable>` in this project addresses this risk entirely by intercepting all unexpected exceptions before they reach the response serialiser and replacing the raw trace with a generic `{"errorMessage": "Unexpected Error Occurred", "errorCode": 500, "documentation": "http://localhost:8080/SmartCampusAPI/api/v1"}` message. The actual exception details are logged server-side where only authorised personnel can access them, satisfying both the security requirement and the operational need for error observability.

---

### Part 5.5 — JAX-RS Filters vs Manual Logging

**Question:** Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting `Logger.info()` statements inside every single resource method?

**Answer:**

Cross-cutting concerns are behaviours that apply uniformly across many or all components of an application, regardless of the specific business logic of each component. Logging every HTTP request and response is a textbook example. Implementing such concerns through manual `Logger.info()` calls inside individual resource methods violates the **Don't Repeat Yourself** principle and produces a fragile, maintainable-hostile codebase.

With manual logging, every developer must remember to add the correct logging statements when writing a new resource method. This is error-prone: methods get forgotten, log formats drift and become inconsistent across methods, and logging statements become tangled with business logic, making the code harder to read and test. If the log format ever needs to change — for example, to add a correlation ID or switch from plain text to structured JSON — every method across the entire codebase must be updated individually.

JAX-RS filters (`ContainerRequestFilter` and `ContainerResponseFilter`) solve this by implementing the concern **once** and applying it **automatically** to every request and response that passes through the runtime. The `LoggingFilter` in this project logs the HTTP method and URI from every incoming request and the status code from every outgoing response without any of the resource classes being aware of it. Adding a new resource class requires zero changes to the logging infrastructure.

This approach also supports **separation of concerns** at the architectural level — resource classes contain only business logic, and infrastructure concerns like logging, authentication, and CORS handling live in filters. The result is code that is easier to read, easier to test in isolation, and easier to maintain as the API grows.
