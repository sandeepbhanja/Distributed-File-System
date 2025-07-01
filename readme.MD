# Distributed File System - Backend

This repository contains the backend services for a Distributed File System (DFS) project. The backend is organized as a monorepo with the following microservices:

## Project Structure

```
backend/
  auth-service/
  chunk/
  gateway-service/
  metadata/
```

### Services

- **auth-service**  
  Handles user authentication and authorization for the DFS.

- **chunk**  
  Responsible for storing and managing file chunks.

- **gateway-service**  
  Acts as the main entry point for client requests, routing them to the appropriate backend services.

- **metadata**  
  Manages metadata related to files and chunks in the DFS.

## Prerequisites

- Java 21+
- Maven 3.9+
- Docker (optional, for containerization)
- PostgreSQL (for services that require a database)

## Building and Running

Each service is a Spring Boot application and can be built and run independently.

### Build All Services

From the `backend` directory, run:

```sh
cd auth-service && ./mvnw clean install
cd ../chunk && ./mvnw clean install
cd ../gateway-service && ./mvnw clean install
cd ../metadata && ./mvnw clean install
```

### Run a Service

For example, to run the `auth-service`:

```sh
cd auth-service
./mvnw spring-boot:run
```

Repeat for other services as needed.

## Configuration

Each service contains its own configuration files under `src/main/resources/application.properties`.  
Database and other environment-specific settings should be configured there.

## Docker

Each service includes a `Dockerfile` for containerized deployment.

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/YourFeature`)
3. Commit your changes
4. Push to the branch
5. Open a pull request

## License

This project is licensed under the Apache License 2.0.

---

**Note:**  
For more details on each service, refer to their respective directories.
