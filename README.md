# Apna Academy — Spring Boot

This workspace contains a Java 21 Spring Boot application backed by the existing MySQL schema `apna_academy`. It uses Spring MVC, Thymeleaf, Spring Data JPA, Spring Security, Hibernate validation, and MySQL Connector/J. It does not create or seed database records.

## Requirements

- Java 21 or newer (the source compiles with `--release 21`)
- Maven 3.9+ or IntelliJ IDEA's bundled Maven
- MySQL 8 with the existing `apna_academy` schema

## Database setup

The app points to `localhost:3306/apna_academy` by default. Certificate verification reads `certificate_system.certificates` (`CERTIFICATE_SCHEMA` can override the schema name). The MySQL account must have `SELECT` access to both schemas. Set `DB_USERNAME` and `DB_PASSWORD` in your shell or IDE run configuration. The password is not stored in this repository. The default username is `root`.

The app uses `spring.jpa.hibernate.ddl-auto=validate`. Startup validates mapped tables and columns; it does not create, drop, or alter schema objects. The structure-only export at `database/apna_academy_schema.sql` contains no records and excludes DROP statements.

PowerShell one-session run (enter the password at a secure prompt):

```powershell
$mvn = 'C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.3\plugins\maven-plugin\lib\maven3\bin\mvn.cmd'
& $mvn -DskipTests package
$secureDbPassword = Read-Host 'MySQL password' -AsSecureString
$env:DB_PASSWORD = [System.Net.NetworkCredential]::new('', $secureDbPassword).Password
& $mvn spring-boot:run
Remove-Item Env:DB_PASSWORD
```

Set `DB_URL` to use another host/schema and `DB_USERNAME` to use another account.

## GitHub + Netlify deployment

This is a server-rendered Spring Boot app. Netlify serves the public domain and proxies all routes to the Spring Boot service; deploy the `Dockerfile` to a Java container host first. The Netlify build requires `BACKEND_URL`, set to the HTTPS origin of that running service (for example, `https://your-app.example.com`, with no trailing slash). Connect this GitHub repository to Netlify and set that variable in the Netlify site build environment.

The app also needs a MySQL server reachable from the Java host. A Workbench connection to `localhost` is local to your PC and will not work from a cloud host. Set `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` on the Java service, plus `CERTIFICATE_SCHEMA` if certificate records are hosted in a separate schema. Do not put passwords in GitHub or `netlify.toml`. Preserve the existing data by exporting/importing it to a hosted MySQL instance before launch; the schema-only `database/apna_academy_schema.sql` file contains no application records.

After the Java service is running and connected to its database, set `BACKEND_URL` in Netlify and trigger a deploy. The Netlify rule forwards paths, query strings, and requests to Spring Boot, including Thymeleaf pages and static assets.

## Run

Build the runnable JAR with the Maven executable bundled in IntelliJ IDEA using `-DskipTests package`. Start it with the same executable and `spring-boot:run`, or use `java -jar target/apna-academy-1.0.0.jar` after setting `DB_PASSWORD`.

The app defaults to **http://localhost:8081** because Tomcat already listens on port 8080. Override this with `SERVER_PORT` if needed.

## Pages

- Public: `/`, `/about`, `/courses`, `/faculty`, `/events`, `/enquiry`, `/contact`, `/certificate/verify`, `/placements`, `/jobs`, `/notes`, `/tests`
- Student: register at `/register`, sign in at `/login`, then use `/student`, `/student/profile`, and `/student/results`
- Admin: `/admin` and `/admin/manage/{module}` for academy record modules

The `users` table currently has no admin account. The app does not invent one; an existing `ADMIN` account with a BCrypt password is required for the admin area. Student registration stores BCrypt-hashed passwords. The schema represents admissions through `students` (there is no separate `admissions` table), and the question module uses `test_questions`.

## Migration notes

The PHP project, page content, and frontend assets were not present in the workspace. The Spring app was built from the supplied requirements and live schema, so original PHP-specific text, images, and behavior could not be copied. Add the original source and assets to finish an exact visual and behavior migration.

The live schema has 15 tables. The latest read-only check found 5 courses, 4 faculty, 4 events, 1 placement, 1 job, 7 notes, and 0 certificates. The app does not add sample records. See [database/README.md](database/README.md) for schema inspection details.
