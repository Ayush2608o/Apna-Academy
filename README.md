# Apna Academy — Spring Boot

This workspace contains a Java 21 Spring Boot application backed by the MySQL schema `apna_academy`. It uses Spring MVC, Thymeleaf, Spring Data JPA, Spring Security, Hibernate validation, and MySQL Connector/J.

## Requirements

- Java 21 or newer (the source compiles with `--release 21`)
- Maven 3.9+ or IntelliJ IDEA's bundled Maven
- MySQL 8 with the existing `apna_academy` schema

## Database setup

Set `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` in your shell or IDE run configuration. `DB_URL` must be a JDBC URL for a reachable MySQL 8 database, such as `jdbc:mysql://localhost:3306/apna_academy?serverTimezone=UTC` for local development. Certificate verification reads existing records from `certificate_system.certificates` by default (`CERTIFICATE_SCHEMA` can override the schema), then checks certificates issued in `apna_academy.certificates`.

The app uses `spring.jpa.hibernate.ddl-auto=validate`. Startup validates mapped tables and columns; it does not create, drop, or alter schema objects. The structure-only export at `database/apna_academy_schema.sql` contains no records and excludes DROP statements. Import the schema and your own application records into the hosted database before deploying; the schema file by itself produces an empty website.

PowerShell one-session run (enter the password at a secure prompt):

```powershell
$mvn = 'C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.3\plugins\maven-plugin\lib\maven3\bin\mvn.cmd'
& $mvn -DskipTests package
$env:DB_URL = 'jdbc:mysql://localhost:3306/apna_academy?serverTimezone=UTC'
$env:DB_USERNAME = 'root'
$secureDbPassword = Read-Host 'MySQL password' -AsSecureString
$env:DB_PASSWORD = [System.Net.NetworkCredential]::new('', $secureDbPassword).Password
& $mvn spring-boot:run
Remove-Item Env:DB_PASSWORD
```

## Deploy free tier (Render + Netlify)

The app renders its HTML with Spring Boot and Thymeleaf. Netlify is configured as the public domain/reverse proxy, while Render runs the Java app and serves its pages, styles, and form handlers. Render does not provide a free managed MySQL database; choose an external MySQL-compatible database provider and check its current free-tier, connection, storage, backup, and sleep limits. A free-tier database and Render service can sleep or change availability; this setup is not an uptime guarantee.

1. **Create and populate online MySQL.** Create the `apna_academy` database using a MySQL-compatible provider that allows connections from Render. Import `database/apna_academy_schema.sql`, then migrate the existing records from your current MySQL server if you want your courses, users, and other content online. The checked-in SQL file has structure only and contains no records. Use the provider's TLS JDBC URL, username, and password. Do not put credentials in GitHub.
2. **Deploy the backend.** In Render choose **New → Blueprint**, select this GitHub repository, and apply `render.yaml`. Set `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` when prompted. `DB_URL` should look like `jdbc:mysql://HOST:PORT/apna_academy?sslMode=REQUIRED&serverTimezone=UTC` (use the exact host/port and TLS settings from the database provider). Render uses the included Dockerfile, Java 21, the Render-provided `PORT`, and `/health` for health checks. Wait for the service to become live and copy its HTTPS origin, for example `https://apna-academy.onrender.com`.
3. **Deploy the Netlify site.** In Netlify import the same GitHub repository, keep the base directory at the repository root, and add the site environment variable `BACKEND_URL` with the Render HTTPS origin only (no path or trailing slash). `netlify.toml` runs `scripts/netlify-build.sh`, which validates that setting and generates a proxy rewrite. Deploy the site; the proxy forwards page and static-resource requests to Render.
4. **Finish public URL and administrator setup.** Copy the Netlify HTTPS URL (for example, `https://your-site.netlify.app`) into Render as `PUBLIC_APP_URL`. For direct cross-origin browser requests, set `APP_CORS_ALLOWED_ORIGINS` to that exact Netlify origin; normal page traffic through the proxy is same-origin and does not require CORS. Set `ADMIN_BOOTSTRAP_EMAIL` and a unique `ADMIN_BOOTSTRAP_PASSWORD` of 12–72 characters (at most 72 UTF-8 bytes) in Render if you need the admin dashboard. On first startup, a new admin is created only if that email does not already exist. Existing accounts/passwords are never overwritten. Redeploy after setting these variables and sign in through the Netlify site.
5. **Issue and verify certificates.** As an administrator, use **Generate certificate**, select a student who has an assigned course, and submit. The app creates a unique `certificate_number`, a printable certificate, and a QR code. Scanning the QR code with a phone camera opens the Netlify verification page and checks the number automatically. Existing certificate numbers from your data import remain verifiable.

Netlify and Render environment variables are required deployment settings; they cannot be inferred safely from the source code. Keep all passwords and database credentials in the hosting providers' environment-variable settings, never in `netlify.toml`, `render.yaml`, or Git.

## Run

Build the runnable JAR with Maven using `-DskipTests package`. Start it with `spring-boot:run` after setting `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`, or use `java -jar target/apna-academy-1.0.0.jar` with those same variables.

For local runs the app defaults to **http://localhost:8081**. On Render, the platform-provided `PORT` takes precedence; use `SERVER_PORT` only to change the local default.

## Pages

- Public: `/`, `/about`, `/courses`, `/faculty`, `/events`, `/enquiry`, `/contact`, `/certificate/verify`, `/certificate/{certificate_number}`, `/placements`, `/jobs`, `/notes`, `/tests`
- Student: register at `/register`, sign in at `/login`, then use `/student`, `/student/profile`, and `/student/results`
- Admin: `/admin` and `/admin/manage/{module}` for academy record modules

Student registration stores BCrypt-hashed passwords. The schema represents admissions through `students` (there is no separate `admissions` table), and the question module uses `test_questions`.

## Migration notes

The PHP project, page content, and frontend assets were not present in the workspace. The Spring app was built from the supplied requirements and live schema, so original PHP-specific text, images, and behavior could not be copied. Add the original source and assets to finish an exact visual and behavior migration.

The supplied schema export has 15 tables but no application records. See [database/README.md](database/README.md) for schema inspection and import details.
