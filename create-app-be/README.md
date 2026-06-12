# @omobio/create-app-be

Scaffold Spring Boot backend services on top of [`spring-base-starter`](../spring-base-starter).

## Usage

```bash
npx @omobio/create-app-be@latest my-hr-service -y --template service-crud
```

### Templates

- **service-blank** — JWT auth, RBAC platform, health endpoint (pairs with FE `sor` or `blank`)
- **service-crud** — blank + Employee CRUD (pairs with FE `admin-portal` Employees page)

### Full-stack with `@omobio/create-app-ui`

| FE template | BE template | Live API pages |
|-------------|-------------|----------------|
| `admin-portal` | `service-crud` | Login, Users, **Employees** |
| `sor` | `service-blank` | Login only (SOR UI uses demo data) |

Both use port **8081** and seed **admin@example.com** / **admin123**.

### Options

```
create-app-be <name> [options]

  -y, --yes                 Non-interactive mode
  -t, --template <name>     service-blank | service-crud
  -c, --company <name>      Company name for Java package (default: Omobio)
  -p, --package <name>      Override base package (e.g. com.acme.hrservice)
  --starter-version <ver>   spring-base-starter version (default: 1.0.0)
  --git                     Initialize git repository
```

## Development

From the `common-be` monorepo:

```bash
cd create-app-be
npm install
npm run build
npm start my-hr-service -y --template service-crud
```

See [USER_GUIDE.md](../USER_GUIDE.md) for Maven/GitHub Packages setup and extension patterns.

## Publish to npm

```bash
cd create-app-be
npm run pack:check          # verify tarball contents
npm run publish:npm -- --otp=123456   # replace with your authenticator code
```
