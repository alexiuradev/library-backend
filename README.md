
# Library Management System - Step B (Full Auth)

## What you get
- Spring Boot 3 (Maven)
- Postgres (docker-compose)
- Flyway migrations
- JWT auth (register/login) + roles
- `/api/v1/auth/me`
- Protected endpoint demo: `/api/v1/secure/ping`
- Admin-only demo: `/api/v1/admin/ping`
- Integration tests with Testcontainers

## Run locally
```bash
docker compose up -d
mvn spring-boot:run
```

Health:
- http://localhost:8080/actuator/health

## Postman flow (JWT)
1) Register
POST http://localhost:8080/api/v1/auth/register
Body (JSON):
```json
{ "email": "a@b.com", "password": "secret12" }
```
Response:
```json
{ "token": "..." }
```

2) Me
GET http://localhost:8080/api/v1/auth/me
Header:
Authorization: Bearer <token>

3) Protected ping
GET http://localhost:8080/api/v1/secure/ping
Header:
Authorization: Bearer <token>
