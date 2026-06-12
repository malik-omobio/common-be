# Spring Base Platform — User Guide

Hybrid backend platform mirroring the Omobio FE stack (`@omobio/ui` + `@omobio/create-app-ui`).

## Architecture

| Layer | Location | Updates via |
|-------|----------|-------------|
| **Starter library** | `spring-base-starter` → `com.omobio:spring-base-starter` | Bump Maven dependency version |
| **Templates** | `templates/service-blank`, `templates/service-crud` | Regenerate or manual merge |
| **CLI** | `create-app-be` → `@omobio/create-app-be` | `npx @omobio/create-app-be@latest` |

### In the starter (never copy into apps)

- JWT + Spring Security + `@Guard`
- Auth login/logout, User/Role/Permission admin APIs
- API response envelope, exceptions, Redis config
- Core seeders + `PermissionCatalog` framework
- `SpringBaseAutoConfiguration`

### In generated apps (you own)

- `*Application.java` main class
- `application.properties`
- Domain entities, controllers, services
- Domain `PermissionCatalog` implementations
- `docker-compose.yml`, tests, CI

## Quick start — new service

```bash
npx @omobio/create-app-be@latest my-hr-service -y --template service-crud
cd my-hr-service
docker compose up -d
mvn spring-boot:run "-Dspring-boot.run.profiles=dev,seed"
```

PowerShell: quote the `-D` flag as shown above.

### Templates

| Template | Description |
|----------|-------------|
| `service-blank` | Auth + health + RBAC platform only |
| `service-crud` | Above + Employee CRUD example (admin + public web) |

### CLI options

```bash
create-app-be <name> [options]

  -y, --yes                 Non-interactive
  -t, --template <name>     service-blank | service-crud
  -c, --company <name>      Company slug for Java package (default: Omobio)
  -p, --package <name>      Override base package (e.g. com.acme.hrservice)
  --starter-version <ver>   spring-base-starter version (default: 1.0.0)
  --git                     Initialize git repository
```

Generated package example: `my-hr-service` + company `Acme` → `com.acme.myhrservice`.

## Consuming spring-base-starter

Add to `pom.xml`:

```xml
<dependency>
  <groupId>com.omobio</groupId>
  <artifactId>spring-base-starter</artifactId>
  <version>1.0.0</version>
</dependency>
```

Generated apps include the GitHub Packages `<repositories>` block. Configure `~/.m2/settings.xml`:

```xml
<settings>
  <servers>
    <server>
      <id>github</id>
      <username>YOUR_GITHUB_USER</username>
      <password>YOUR_GITHUB_PAT</password>
    </server>
  </servers>
</settings>
```

Or use environment variables (see `settings.xml.example` in this repo):

```powershell
$env:GITHUB_ACTOR = "your-github-username"
$env:GITHUB_TOKEN = "ghp_your_pat"
```

Publish from this repo:

```powershell
.\scripts\deploy-starter.ps1
# or: mvn -pl spring-base-starter clean deploy
```

Package URL: `https://maven.pkg.github.com/malik-omobio/common-be`

Git remote: `git@github.com:malik-omobio/common-be.git`

## Extending a generated app

### Add permissions

```java
@Component
public class OrderPermissionCatalog implements PermissionCatalog {
    @Override
    public Map<String, Map<String, String>> categories() {
        return Map.of("Orders", Map.of(
            "VIEW_ORDER", "View orders",
            "CREATE_ORDER", "Create orders"
        ));
    }
}
```

Use `@Guard({"VIEW_ORDER"})` on controllers. Run with `seed` profile to populate permissions.

### Add public (unauthenticated) routes

```properties
app.security.excluded-paths=/api/v1/web/my-resource,/api/v1/web/my-resource/**
```

### CORS for Vite admin portal

```properties
app.cors.allowed-origins=http://localhost:5173
```

Default `server.port=8081` in generated templates matches the Vite proxy in `@omobio/create-app-ui` (`/api` → `http://localhost:8081`).

### Pair with admin-portal (FE)

| FE page | BE endpoint | Template |
|---------|-------------|----------|
| Login | `POST /api/v1/admin/auth/login` | service-blank or service-crud |
| Users | `GET /api/v1/admin/users` | starter (both templates) |
| Employees | `GET /api/v1/admin/employees` | **service-crud** only |

Seed credentials (both templates): **admin@example.com** / **admin123**

```bash
# Backend
npx @omobio/create-app-be@latest my-api -y --template service-crud
cd my-api && docker compose up -d
mvn spring-boot:run "-Dspring-boot.run.profiles=dev,seed"

# Frontend (separate repo / folder)
npx @omobio/create-app-ui@latest my-app -y --template admin-portal
cd my-app && npm install && npm run dev
```

## Full-stack alignment (FE + BE)

| FE (`@omobio/create-app-ui`) | BE (`@omobio/create-app-be`) |
|------------------------------|------------------------------|
| `admin-portal` template | `service-crud` template |
| `blank` template | `service-blank` template |
| Mock login → wire to API | `/api/v1/admin/auth/login` |
| `localhost:5173` | CORS preconfigured |
| Users page | `/api/v1/admin/users` (starter) |
| Employees page (admin-portal) | `/api/v1/admin/employees` (service-crud) |
| Domain pages | Your controllers in app package |

## Developing the platform (this repo)

```bash
# Build starter
mvn -pl spring-base-starter clean install

# Run CLI locally
cd create-app-be && npm install && npm run build
node dist/bin/index.js my-test-service -y --template service-crud

# Test generated app against local starter
cd ../my-test-service
# Set spring-base-starter.version to 1.0.0-SNAPSHOT and install from local .m2
mvn spring-boot:run "-Dspring-boot.run.profiles=dev,seed"
```
