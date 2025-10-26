# Task Manager API

## Overview
The Task Manager API is a RESTful API service that allows users to create, read, update, and delete tasks. It supports CRUD operations on tasks stored in a MySQL database and provides endpoints for querying task details.

## Prerequisites
- Java 21
- Gradle 8+
- MySQL 8+
- Docker

## Quick Start
```bash
# Single command to run the entire application
./run.sh
```

## API Documentation
- **OpenAPI Specification**: Available at `/swagger-ui.html` when running
- **Postman Collection**: Import `postman_collection.json` for testing

## Testing
Describe your testing strategy and how to run tests.

### Unit Tests
```bash
./gradlew test
```

### Integration Tests
```bash
./gradlew integrationTest
```

## Database Schema
Task table:
- `id` BIGINT AUTO_INCREMENT PRIMARY KEY
    - Auto increment to allows for unque `id` to be generate without manually specifying.
- `title` VARCHAR(255)
- `description` VARCHAR(1000)
- `completed` BOOLEAN DEFAULT FALSE NOT NULL
    - `NOT NULL` and defaulting to false will ensure the data is always valid.
- `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL
- `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL
    - `ON UPDATE` ensures that it will be updated even if the user does not specify.

## Architecture
- **Layering**: Controller layer directly interacts with JPA repositories (no service layer in this implementation).
- **Unit testing**: Unit tests mock repository (no interaction with database) dependencies using Mockito
- **Integration testing**: Integration tests run against an in-memory H2 database to validate the controller-to-database flow.

## CI/CD Pipeline
The pipeline will first attempt to build the application. If code successfully compiles, then the unit tests and integration tests will run. Generated test reports will be saved as artifacts with a 7-day retention. 

## Assumptions Made
- No authentication and authorization is required, anyone can use the API endpoints
- Task titles and descriptions are allowed to be `null` instead of enforcing an empty string.
- Tasks title are assumed to be non-unique. Multiple task can have the same title or no title at all.
- Future database operations are assumed to remain simple (single-task CRUD). Complex operations (e.g. batch updates or multi-step changes) may require transactions or concurrency handling to ensure data consistency.


## Known Limitations
- `.env` file is upload for convenience.
- While the application can be run with a single command with `run.sh`, it is not optimized for cross-platform development.
- No service layer between controller and repository.
- Running the application requires a new build each time. No option to run the built binary executable.
- Tests does not test beyond the controller (limited coverage).
- No pagination or filtering for GET /tasks endpoints, which may be inefficient for large datasets.
- No logging.

## Technology Stack
- **Framework**: Spring Boot 3.5+
- **Language**: Java 21
- **Database**: MySQL 8+ (production), H2 (test)
- **Build tool**: Gradle
- **Containerization**: Docker & Docker Compose
- **Testing**: JUnit 5, Mockito

## Author
Ho Khee Wei - kheewei.h@gmail.com