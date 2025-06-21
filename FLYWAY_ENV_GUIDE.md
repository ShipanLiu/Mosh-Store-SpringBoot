# Using Environment Variables with Flyway

This guide explains how to use environment variables with Flyway for database migrations in this project.

## Environment Variables Setup

The project is configured to use environment variables for database connection details. These variables are stored in:

```
src/main/resources/.env
```

The file contains the following variables:

```
DB_USERNAME=root
DB_PASSWORD=19980223
DB_HOST=localhost
DB_PORT=3308
```

## Automatic Migrations with Spring Boot

When you start the Spring Boot application, Flyway will automatically run migrations using the environment variables loaded from the `.env` file. This is configured in `application.yml`:

```yaml
spring:
  config:
    import: "optional:file:./src/main/resources/.env"
  flyway:
    enabled: true
    baseline-on-migrate: true
    validate-on-migrate: true
    locations: classpath:db/migration
```

## Manual Migrations with Maven

To run Flyway migrations manually, you can use the provided script:

```bash
./run-flyway.sh [profile]
```

Where `[profile]` is one of:
- `dev` (default if not specified)
- `uat`
- `prod`

The script will:
1. Load environment variables from `src/main/resources/.env`
2. Create a temporary properties file with the correct database URL for the specified profile
3. Run the Flyway Maven plugin with these properties

## Manual Migration with Maven Directly

If you prefer to run Maven commands directly, you can use:

```bash
mvn flyway:migrate -Dflyway.configFiles=src/main/resources/flyway.conf \
    -Dflyway.placeholders.DB_USERNAME=your_username \
    -Dflyway.placeholders.DB_PASSWORD=your_password \
    -Dflyway.placeholders.DB_HOST=localhost \
    -Dflyway.placeholders.DB_PORT=3308
```

## How It Works

1. The `pom.xml` file contains Flyway Maven plugin configuration with placeholders like `${DB_USERNAME}`
2. The `flyway.conf` file contains default configuration for all environments
3. When running migrations:
   - Spring Boot automatically loads variables from `.env` during startup
   - The `run-flyway.sh` script loads variables from `.env` for manual migrations
   - Maven commands can use `-Dflyway.placeholders.*` to provide values

## Troubleshooting

If you encounter issues with migrations:

1. Verify the `.env` file exists and contains correct values
2. Check database connection details in `application.yml` and `flyway.conf`
3. Ensure the database server is running and accessible
4. Check migration SQL files in `src/main/resources/db/migration` for syntax errors 