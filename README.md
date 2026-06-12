# Spring Base Platform

Omobio Spring Boot backend platform — hybrid model mirroring the FE stack (`@omobio/ui` + `@omobio/create-app-ui`).

## Repository layout

```
common-be/
├── spring-base-starter/     # Published: com.omobio:spring-base-starter
├── templates/
│   ├── service-blank/       # Minimal runnable service (auth + health)
│   └── service-crud/        # Employee CRUD example
├── create-app-be/           # CLI: @omobio/create-app-be
├── docker-compose.yaml      # Local PostgreSQL + Redis for development
└── pom.xml                  # Parent BOM (spring-base-parent)
```

## Quick start — scaffold a new service

```bash
npx @omobio/create-app-be@latest my-hr-service -y --template service-crud
cd my-hr-service
docker compose up -d
mvn spring-boot:run "-Dspring-boot.run.profiles=dev,seed"
```

On **PowerShell**, quote the `-D` flag:

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=dev,seed"
```

### Default admin credentials

| Field    | Value              |
|----------|--------------------|
| Email    | admin@example.com  |
| Password | admin123           |

Base URL: `http://localhost:8080/api`

## What's in the starter vs templates

| Starter library (dependency) | Generated app (template) |
|-----------------------------|--------------------------|
| JWT, Security, `@Guard` | `*Application.java` |
| Auth, Users, Roles, Permissions | `application.properties` |
| API envelopes, Redis, seeders | Domain controllers & entities |
| `PermissionCatalog` framework | Domain permission catalogs |

See [USER_GUIDE.md](USER_GUIDE.md) for publishing, consuming from GitHub Packages, and extending apps.

## Develop the platform locally

```bash
# Start infrastructure
docker compose up -d

# Build starter
mvn -pl spring-base-starter clean install

# Build CLI
cd create-app-be && npm install && npm run build
```

## Stack

- Java 17 · Spring Boot 3.5 · PostgreSQL · Redis
- Spring Security + JWT · Spring Data JPA

## Infrastructure ports

| Service    | Port |
|-----------|------|
| PostgreSQL | 5434 |
| Redis      | 6380 |
