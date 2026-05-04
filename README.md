# Rideshare Microservices (location, matching, ride)

## Overview
This repository contains a small rideshare microservices example with three services:
- `location-service` — manages location data and geospatial lookups
- `matching-service` — matches riders with drivers and handles matching logic
- `ride-service` — manages ride lifecycle, events, and persistence

Each service is a standalone Spring Boot / Maven service with its own `pom.xml` and `application.yaml` configuration under each service folder.

## Project structure
- `location-service/` — Location microservice
- `matching-service/` — Matching microservice
- `ride-service/` — Ride microservice
- `docker-compose.yml` — Compose file to run services together

Main application classes are under `src/main/java/...` in each service (e.g., `LocationServiceApplication.java`, `MatchingServiceApplication.java`, `RideServiceApplication.java`). Configuration is in `src/main/resources/application.yaml` for each service.

## Prerequisites
- JDK 11 or newer installed
- Docker & Docker Compose (optional, for running the composed system)
- Git (for source control)

## Build & run
Build a single service (from its folder):

```
# Unix/macOS
./mvnw clean package

# Windows (PowerShell / CMD)
.\mvnw.cmd clean package
```

Run tests for a service:

```
./mvnw test
```

Run everything using Docker Compose (from repository root):

```
docker-compose up --build
```

Or run an individual service from your IDE by running the main class (e.g., `LocationServiceApplication`).

## Notes
- Service-specific configs live in `src/main/resources/application.yaml` in each service directory.
- The `target/` directories are build outputs and are ignored by the included `.gitignore`.

## Next steps / Useful commands
- Build all services individually or create a CI job that builds each module.
- Run `docker-compose up` to run the system locally for integration testing.

If you'd like, I can:
- Add badges (build / test) to this `README.md`
- Expand run instructions with sample curl requests or OpenAPI endpoints
- Create a root-level Maven aggregator `pom.xml` to build all modules together

