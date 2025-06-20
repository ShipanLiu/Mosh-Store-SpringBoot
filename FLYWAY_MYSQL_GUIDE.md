# Flyway MySQL Database Migration Guide

## Overview

This project uses **Flyway** for database schema management with **two complementary approaches**:

1. **Spring Boot Integration** - Automatic migrations on application startup
2. **Flyway Maven Plugin** - Manual migration control for CI/CD and development

## 🚀 Quick Start

### Automatic Migration (Spring Boot)
```bash
# Migrations run automatically when starting the application
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Manual Migration (Maven Plugin)
```bash
# Run migrations manually for development
./mvnw flyway:migrate -Dflyway.configFiles=src/main/resources/flyway-dev.conf

# Or use profile-specific commands (see below)
```

## 📋 Two Migration Approaches

### 1. Spring Boot Integration (Automatic)

**When it runs:**
- ✅ **Automatically** when Spring Boot application starts
- ✅ Integrated with Spring profiles (`dev`, `uat`, `prod`)
- ✅ Uses `application.yml` configuration

**Pros:**
- Zero configuration needed
- Always up-to-date database
- Perfect for development

**Cons:**
- No manual control
- Migrations tied to app startup
- Can slow down startup time

**Configuration:** `src/main/resources/application.yml`
```yaml
spring:
  flyway:
    enabled: true  # Set to false to disable auto-migration
    baseline-on-migrate: true
    validate-on-migrate: true
```

### 2. Flyway Maven Plugin (Manual)

**When it runs:**
- ✅ **On-demand** via Maven commands
- ✅ Independent of application startup
- ✅ Perfect for CI/CD pipelines
- ✅ Advanced features (rollback, repair, etc.)

**Pros:**
- Full control over when migrations run
- CI/CD pipeline integration
- Advanced Flyway features
- Can run without starting application

**Cons:**
- Requires manual execution
- Need to remember to run migrations

## 🛠️ Maven Plugin Commands

### Environment-Specific Commands

#### Development Environment
```bash
# Migrate development database
./mvnw flyway:migrate -Dflyway.url=jdbc:mysql://localhost:3308/cdb_mosh_p1_ecommerce -Dflyway.user=root -Dflyway.password=

# Check migration status
./mvnw flyway:info -Dflyway.url=jdbc:mysql://localhost:3308/cdb_mosh_p1_ecommerce -Dflyway.user=root -Dflyway.password=

# Validate migrations
./mvnw flyway:validate -Dflyway.url=jdbc:mysql://localhost:3308/cdb_mosh_p1_ecommerce -Dflyway.user=root -Dflyway.password=
```

#### UAT Environment
```bash
# Set environment variables first
export DB_HOST=uat-server.com
export DB_PORT=3306
export DB_USERNAME=uat_user
export DB_PASSWORD=uat_password

# Migrate UAT database
./mvnw flyway:migrate -Dflyway.url=jdbc:mysql://${DB_HOST}:${DB_PORT}/cdb_mosh_p1_ecommerce_uat -Dflyway.user=${DB_USERNAME} -Dflyway.password=${DB_PASSWORD}
```

#### Production Environment
```bash
# Set environment variables first
export DB_HOST=prod-server.com
export DB_PORT=3306
export DB_USERNAME=prod_user
export DB_PASSWORD=prod_password

# Migrate production database
./mvnw flyway:migrate -Dflyway.url=jdbc:mysql://${DB_HOST}:${DB_PORT}/cdb_mosh_p1_ecommerce_prod -Dflyway.user=${DB_USERNAME} -Dflyway.password=${DB_PASSWORD}
```

### Common Flyway Commands

```bash
# 1. MIGRATE - Apply pending migrations
./mvnw flyway:migrate [options]

# 2. INFO - Show migration status
./mvnw flyway:info [options]

# 3. VALIDATE - Validate applied migrations
./mvnw flyway:validate [options]

# 4. BASELINE - Baseline existing database
./mvnw flyway:baseline [options]

# 5. REPAIR - Repair schema history table
./mvnw flyway:repair [options]

# 6. CLEAN - Drop all objects (DANGEROUS - disabled in production)
./mvnw flyway:clean [options]
```

## 🏗️ Database Setup

### 1. Install MySQL
```bash
# macOS with Homebrew
brew install mysql
brew services start mysql

# Or use Docker
docker run --name mysql-dev -e MYSQL_ROOT_PASSWORD= -e MYSQL_ALLOW_EMPTY_PASSWORD=yes -p 3308:3306 -d mysql:8.0
```

### 2. Create Databases
```sql
-- Connect to MySQL
mysql -u root -p

-- Create databases for different environments
CREATE DATABASE cdb_mosh_p1_ecommerce;
CREATE DATABASE cdb_mosh_p1_ecommerce_uat;
CREATE DATABASE cdb_mosh_p1_ecommerce_prod;

-- Create users (optional)
CREATE USER 'mosh_dev'@'localhost' IDENTIFIED BY 'dev_password';
GRANT ALL PRIVILEGES ON cdb_mosh_p1_ecommerce.* TO 'mosh_dev'@'localhost';

CREATE USER 'mosh_uat'@'%' IDENTIFIED BY 'uat_password';
GRANT ALL PRIVILEGES ON cdb_mosh_p1_ecommerce_uat.* TO 'mosh_uat'@'%';

FLUSH PRIVILEGES;
```

## 📁 Migration Files Structure

```
src/main/resources/db/migration/
├── V1__Create_initial_schema.sql
├── V2__Add_audit_and_features.sql
├── V3__Add_new_feature.sql
└── V4__Update_existing_table.sql
```

### Naming Convention
- **Prefix:** `V` (version)
- **Version:** `1`, `2`, `3`, etc.
- **Separator:** `__` (double underscore)
- **Description:** `Create_initial_schema` (underscores, descriptive)
- **Extension:** `.sql`

## 🔧 Environment Configuration

### Development (application-dev.yml)
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3308/cdb_mosh_p1_ecommerce
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:}
  flyway:
    enabled: true  # Auto-migrate on startup
```

### UAT (application-uat.yml)
```yaml
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3308}/cdb_mosh_p1_ecommerce_uat
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  flyway:
    enabled: false  # Manual migration recommended
```

### Production (application-prod.yml)
```yaml
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST}:${DB_PORT}/cdb_mosh_p1_ecommerce_prod
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  flyway:
    enabled: false  # ALWAYS manual in production
```

## 🚀 CI/CD Pipeline Integration

### GitHub Actions Example
```yaml
name: Database Migration
on:
  push:
    branches: [main]

jobs:
  migrate:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'
          
      - name: Run Flyway Migration
        run: |
          ./mvnw flyway:migrate \
            -Dflyway.url=jdbc:mysql://${{ secrets.DB_HOST }}:3306/cdb_mosh_p1_ecommerce_prod \
            -Dflyway.user=${{ secrets.DB_USERNAME }} \
            -Dflyway.password=${{ secrets.DB_PASSWORD }}
```

## 🎯 Best Practices

### 1. **Development Workflow**
```bash
# 1. Create new migration file
touch src/main/resources/db/migration/V3__Add_user_preferences.sql

# 2. Write SQL migration
# 3. Test with Maven plugin first
./mvnw flyway:migrate -Dflyway.url=jdbc:mysql://localhost:3308/cdb_mosh_p1_ecommerce -Dflyway.user=root -Dflyway.password=

# 4. Start application (will auto-migrate)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### 2. **Production Deployment**
```bash
# NEVER use auto-migration in production
# Always use manual migration

# 1. Backup database first
mysqldump -u root -p cdb_mosh_p1_ecommerce_prod > backup_$(date +%Y%m%d_%H%M%S).sql

# 2. Run migration manually
./mvnw flyway:migrate [production-options]

# 3. Verify migration
./mvnw flyway:info [production-options]

# 4. Start application with flyway.enabled=false
```

### 3. **Migration File Guidelines**
- ✅ **Backward compatible** when possible
- ✅ **Idempotent** operations
- ✅ **Test on copy of production data**
- ✅ **Include rollback instructions in comments**
- ❌ **Never modify existing migration files**
- ❌ **Never delete migration files**

## 🆘 Troubleshooting

### Common Issues

#### 1. "Migration checksum mismatch"
```bash
# Repair the schema history
./mvnw flyway:repair [options]
```

#### 2. "Database is not empty"
```bash
# Baseline the existing database
./mvnw flyway:baseline [options]
```

#### 3. "Failed to connect to database"
- Check database is running
- Verify connection parameters
- Check firewall/network settings

#### 4. "Migration failed"
```sql
-- Check flyway_schema_history table
SELECT * FROM flyway_schema_history ORDER BY installed_on DESC;

-- Manual cleanup if needed (CAREFUL!)
DELETE FROM flyway_schema_history WHERE version = 'failed_version';
```

## 📊 Monitoring

### Check Migration Status
```bash
# See all migrations and their status
./mvnw flyway:info [options]

# Validate all applied migrations
./mvnw flyway:validate [options]
```

### Database Schema History
```sql
-- View migration history
SELECT 
    version,
    description,
    type,
    script,
    installed_on,
    execution_time,
    success
FROM flyway_schema_history 
ORDER BY installed_on DESC;
```

## 🔗 Useful Links

- [Flyway Documentation](https://flywaydb.org/documentation/)
- [Flyway Maven Plugin](https://flywaydb.org/documentation/usage/maven/)
- [Spring Boot Flyway Integration](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.migration-tool.flyway)
- [MySQL Connector/J](https://dev.mysql.com/doc/connector-j/8.0/en/)

---

## 📝 Summary

**Use Spring Boot Integration when:**
- ✅ Development environment
- ✅ Simple applications
- ✅ You want automatic migrations

**Use Maven Plugin when:**
- ✅ Production deployments
- ✅ CI/CD pipelines
- ✅ You need manual control
- ✅ Advanced Flyway features needed

**Best Approach:** Use **both** - Spring Boot for development, Maven plugin for production! 