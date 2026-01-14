# Match API

A simple Spring Boot + PostgreSQL API to manage matches and odds.

---

## Quickstart: Run Locally via Docker

### Prerequisites

- [Docker](https://docs.docker.com/get-docker/) installed

---

### 1. Clone and Build

```bash
git clone https://github.com/VasilisDev/MatchApi.git
cd <project-path>/match-api
docker-compose up --build
```
#### This will:
- Start a PostgreSQL container (matchapi-db) with the database and data persisted in a docker volume.
- Build and run the Spring Boot API container (matchapi) and auto-connect to the database.
- Run the SQL migrations automatically on app startup via flyway.

### 2. API Access
- API base URL: http://localhost:8081
- Swagger UI: http://localhost:8081/swagger-ui.html. Use this for documentation and to play with the API endpoints.

### 3. Stopping & Resetting
   To stop and remove all containers and the data volume (fresh DB next time):
```
docker-compose down -v
```