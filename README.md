# Smart Campus Sensor & Room Management API

## Project Overview
This project is a RESTful API developed for the "Smart Campus" initiative at the University of Westminster. It is built using Java and the JAX-RS (Jersey) framework. The system manages a campus-wide infrastructure involving thousands of Rooms and various Sensors (such as CO2 monitors and occupancy trackers). 

## How to Build and Launch the Server
Follow these steps to get the API running locally:
1. **Prerequisites:** Ensure you have JDK 8 (or higher), Apache NetBeans, and Apache Tomcat 9.0+ installed.
2. **Clone the Repo:** Clone this repository to your local machine using `git clone`.
3. **Open Project:** Open the project in NetBeans.
4. **Build:** Right-click the project name `SmartCampusAPI` and select **Clean and Build**.
5. **Run:** Right-click the project and select **Run**. This will deploy the WAR file to your Tomcat server.
6. **Access:** Once the browser opens, navigate to: `http://localhost:8080/SmartCampusAPI/api/v1`

---

## Part 1: Service Architecture Report

### Question 1: Default Lifecycle of a JAX-RS Resource
**Explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this impacts managing in-memory data structures.**

**Answer:** By default, JAX-RS resources are **request-scoped**. This means the JAX-RS runtime creates a new instance of the resource class for every incoming HTTP request and discards it once the response is sent. 

Because the instance is destroyed after each request, standard instance variables cannot be used to store data, as they would be reset constantly. To prevent data loss, we must use **static** data structures (like `static HashMap`) or a separate Singleton provider. This ensures that data persists in memory across different requests. Furthermore, since multiple requests can occur simultaneously, we must use thread-safe collections (like `ConcurrentHashMap`) or synchronization blocks to prevent race conditions during data access.

### Question 2: The Importance of Hypermedia (HATEOAS)
**Why is the provision of "Hypermedia" (links and navigation within responses) considered a hallmark of advanced RESTful design (HATEOAS)? How does this approach benefit client developers?**

**Answer:** Hypermedia as the Engine of Application State (HATEOAS) is a hallmark of advanced REST because it makes the API self-descriptive. Instead of requiring the client to have hard-coded knowledge of every URL, the server provides links within the JSON response that guide the client on what actions are currently possible.

This benefits client developers by reducing coupling between the client and the server. If the server’s URL structure changes, the client doesn't "break" because it follows dynamic links provided in the response rather than relying on static, external documentation. It allows the API to evolve more easily without forcing immediate updates to all client-side code.

---

### Part 2: Question 1 (IDs vs. Full Objects)
**When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client side processing.**

**Answer:** Returning only IDs significantly reduces the payload size, which saves network bandwidth and speeds up the initial API response. However, it introduces the "N+1 problem" for the client—if the client needs to display the room names or capacities, it must make subsequent HTTP requests for every single ID. Returning the full room objects consumes more initial bandwidth, but it vastly improves client-side performance by providing all necessary data in a single request, eliminating the latency of multiple round trips.

### Part 2: Question 2 (DELETE Idempotency)
**Is the DELETE operation idempotent in your implementation? Provide a detailed justification by describing what happens if a client mistakenly sends the exact same DELETE request for a room multiple times.**

**Answer:** Yes, the DELETE operation is idempotent. In REST, an operation is idempotent if making multiple identical requests has the same effect on the server's state as making a single request. In our implementation, the first DELETE request successfully removes the room and returns a `204 No Content`. If the client sends the exact same request again, the room is already gone, so our code returns a `404 Not Found`. While the HTTP status code changes, the *state of the server* remains exactly the same (the room remains deleted), fulfilling the definition of idempotency.

---

### Part 3: Question 1 (@Consumes Mismatch)
**Explain the technical consequences if a client attempts to send data in a different format, such as text/plain or application/xml. How does JAX-RS handle this mismatch?**

**Answer:** By using the `@Consumes(MediaType.APPLICATION_JSON)` annotation, we are explicitly telling the server to only accept JSON payloads. If a client attempts to send `text/plain` or `application/xml`, the JAX-RS runtime will intercept the request before it even reaches our Java method. It checks the `Content-Type` header of the incoming request, notices the mismatch, and automatically rejects the request, returning a standard `HTTP 415 Unsupported Media Type` error to the client.

### Part 3: Question 2 (QueryParam vs PathParam)
**Contrast filtering using @QueryParam with an alternative design where the type is part of the URL path (e.g., /api/v1/sensors/type/CO2). Why is the query parameter approach generally considered superior for filtering?**

**Answer:** In REST API design, path parameters are meant to identify specific, unique resources or hierarchical structures (e.g., a specific room or sensor). Query parameters are meant to apply optional filters, sorting, or pagination to a collection. Using the path parameter approach (`/type/CO2`) creates rigid, inflexible routing. If we later wanted to filter by type *and* status, the URL structure would become highly convoluted. Query parameters allow clients to dynamically combine multiple optional filters (e.g., `?type=CO2&status=ACTIVE`) cleanly and intuitively.

## Part 4: Deep Nesting with Sub-Resources

### Question 1: Sub-Resource Locator Pattern
**Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path (e.g., sensors/{id}/readings/{rid}) in one massive controller class?**

**Answer:** The Sub-Resource Locator pattern is essential for maintaining the Single Responsibility Principle within an API. By delegating the logic for `/readings` to a dedicated `SensorReadingResource` class, we prevent the parent `SensorResource` class from becoming a massive, unmanageable "god class." 

This separation of concerns makes the codebase significantly easier to read, test, and maintain. If the API grows to include further nesting (e.g., `/sensors/{id}/readings/{rid}/calibrations`), the locator pattern allows us to cleanly chain these distinct resources together. It also encapsulates the business logic specific to readings entirely within its own context, rather than cluttering the sensor management logic.