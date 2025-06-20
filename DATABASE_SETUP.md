# Database Setup Guide

This document explains how to set up and manage the database for the Mosh Store application using Flyway migrations and different Spring profiles.

## 🗄️ Database Technologies

- **Development**: H2 (in-memory database)
- **UAT/Production**: PostgreSQL
- **Migration Tool**: Flyway

## 🔧 Spring Profiles

### Development Profile (`dev`)
- **Database**: H2 in-memory
- **Auto-activated**: Default profile
- **Features**: 
  - H2 Console enabled at `/h2-console`
  - SQL logging enabled
  - Schema auto-creation
  - Flyway disabled (uses JPA auto-DDL)

```bash
# Run with development profile (default)
./mvnw spring-boot:run

# Or explicitly
./mvnw spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=dev
```

### Test Profile (`test`)
- **Database**: H2 in-memory (separate instance)
- **Features**:
  - Isolated test database
  - Schema auto-creation
  - Flyway disabled

```bash
# Run tests
./mvnw test -Dspring.profiles.active=test
```

### UAT Profile (`uat`)
- **Database**: PostgreSQL
- **Features**:
  - Flyway migrations enabled
  - Production-like environment
  - Reduced logging

```bash
# Run with UAT profile
./mvnw spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=uat
```

### Production Profile (`prod`)
- **Database**: PostgreSQL
- **Features**:
  - Flyway migrations enabled
  - Connection pooling optimized
  - Minimal logging
  - Environment variables required

```bash
# Run with production profile
./mvnw spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=prod
```

## 🐘 PostgreSQL Setup

### For UAT Environment
```sql
-- Create database and user
CREATE DATABASE store_uat;
CREATE USER store_uat WITH PASSWORD 'store_uat_pass';
GRANT ALL PRIVILEGES ON DATABASE store_uat TO store_uat;
```

### For Production Environment
```sql
-- Create database and user
CREATE DATABASE store_prod;
CREATE USER store_prod WITH PASSWORD 'your_secure_password';
GRANT ALL PRIVILEGES ON DATABASE store_prod TO store_prod;
```

## 🚀 Flyway Migrations

### Migration Files Location
```
src/main/resources/db/migration/
├── V1__Create_initial_schema.sql
├── V2__Add_audit_and_features.sql
└── V3__Your_next_migration.sql
```

### Running Migrations Manually

```bash
# Migrate UAT database
./mvnw flyway:migrate -Dflyway.configFiles=src/main/resources/flyway.conf -Dflyway.environment=uat

# Migrate Production database
./mvnw flyway:migrate -Dflyway.configFiles=src/main/resources/flyway.conf -Dflyway.environment=prod

# Check migration status
./mvnw flyway:info -Dflyway.configFiles=src/main/resources/flyway.conf

# Validate migrations
./mvnw flyway:validate -Dflyway.configFiles=src/main/resources/flyway.conf
```

## 🔐 Environment Variables

### UAT Environment
```bash
export DB_USERNAME=store_uat
export DB_PASSWORD=store_uat_pass
```

### Production Environment
```bash
export DB_HOST=your-postgres-host
export DB_PORT=5432
export DB_NAME=store_prod
export DB_USERNAME=store_prod
export DB_PASSWORD=your_secure_password
```

## 📊 Database Schema

### Core Tables
- **users**: User accounts and profiles
- **user_roles**: User role assignments
- **user_addresses**: User billing/shipping addresses
- **categories**: Product categories
- **products**: Product catalog
- **product_reviews**: Product reviews and ratings
- **orders**: Customer orders
- **order_items**: Order line items
- **payments**: Payment transactions
- **payment_methods**: Saved payment methods
- **audit_log**: Audit trail for changes

## 🔍 Accessing Databases

### H2 Console (Development)
- **URL**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:storedb`
- **Username**: `sa`
- **Password**: (empty)

### PostgreSQL (UAT/Production)
```bash
# Connect to UAT database
psql -h localhost -U store_uat -d store_uat

# Connect to Production database
psql -h your-postgres-host -U store_prod -d store_prod
```

## 🧪 Sample Data

The initial migration includes sample data:
- 3 users (john_doe, jane_smith, admin_user)
- 4 product categories
- 5 sample products
- User roles and addresses
- Sample product reviews

## 📝 Creating New Migrations

1. Create a new SQL file with version number:
   ```
   V3__Add_new_feature.sql
   ```

2. Follow naming convention:
   - Version number (V3, V4, etc.)
   - Double underscore separator
   - Descriptive name
   - .sql extension

3. Test migration on development environment first
4. Apply to UAT, then production

## 🚨 Troubleshooting

### Common Issues

1. **Migration Checksum Mismatch**
   ```bash
   ./mvnw flyway:repair
   ```

2. **Database Connection Issues**
   - Verify PostgreSQL is running
   - Check connection parameters
   - Ensure user has proper permissions

3. **H2 Console Not Accessible**
   - Ensure development profile is active
   - Check if port 8080 is available

### Useful Commands

```bash
# Clean database (⚠️ DESTRUCTIVE - Development only!)
./mvnw flyway:clean

# Repair migration metadata
./mvnw flyway:repair

# Baseline existing database
./mvnw flyway:baseline

# Show migration history
./mvnw flyway:info
```

## 📚 Additional Resources

- [Flyway Documentation](https://flywaydb.org/documentation/)
- [Spring Boot Database Initialization](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/) 