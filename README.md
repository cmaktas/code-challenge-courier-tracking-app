# Courier Tracking App

---
## Getting Started
Clone the repository:
```bash
git clone https://github.com/cmaktas/code-challenge-courier-tracking-app.git
cd code-challenge-courier-tracking-app
```
Build and run with Docker:
```bash
docker build -t courier-geolocation-tracker . && docker run --rm -d -p 8080:8080 --name courier-app courier-geolocation-tracker
```
Stop and remove project related Docker components:
```bash
docker build -t courier-geolocation-tracker . && docker run --rm -d -p 8080:8080 --name courier-app courier-geolocation-tracker
```

## Configuring Application
All custom logic for this project is grouped under the courier-app in application.yml:

**- courier-app.max-number-of-courier-entities**
>Defines the maximum number of different couriers the application simulates and caches.
The Producer uses this to generate Courier IDs from 1 up to the configured number. 
Total number of cached entities will be defined by this value.

**- courier-app.producer.rate-ms**
>Controls how often the Producer sends new geolocation updates (in milliseconds).

**- courier-app.data-flush.rate-ms**
>Defines how frequently (in milliseconds) the DataFlushService will flush cached distances to the H2 database.
Default is 60,000 ms (1 minute). Lower it if you need more frequent persistence, or increase for less overhead.

**- courier-app.cache.expire-duration-minutes**
>Specifies how long an entry remains in the cache (in minutes) before it expires.
If you prefer short-lived data (e.g., 15 minutes) or no expiration at all, customize accordingly.

## Endpoints
### Swagger UI
Visit http://localhost:8080/swagger-ui/index.html to explore and test the REST endpoints interactively.  

>**Testable Endpoints:**  
**GET** /api/couriers/{courierId}/distance?unit=km — Retrieve total travel distance in kilometers.  
**GET** /api/couriers/{courierId}/distance?unit=mi — Retrieve total travel distance in miles.

### Javadocs 
Automatically generated during the Gradle build and served at http://localhost:8080/docs/index.html.

## Architecture
The Courier Tracking App uses an in-memory H2 database, embedded ActiveMQ, and embedded Caffeine cache. These components are all bundled within the JAR file, so when you build and run the project, the queue, database, and cache are initialized automatically.

Below is a high-level architecture diagram:

![Courier System Architecture](src/main/resources/static/diagram/courier_app_arch.png)


## Technologies Used
- Java 21
- Spring Boot (3.4)
- Gradle
- Spring Data JPA (with in memory H2 Database)
- Spring JMS + ActiveMQ (embedded broker)
- Caffeine Cache
- Docker
- Swagger / OpenAPI (for API docs)
- Javadocs (for Java code documentation, served via /docs)

