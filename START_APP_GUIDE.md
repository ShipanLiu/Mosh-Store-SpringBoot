# 🚀 Application Startup & Flyway Migration Guide

## 📋 Prerequisites

### 1. MySQL Database Setup
Ensure MySQL is running on port **3308** with the following databases:

```sql
-- Development Database
CREATE DATABASE IF NOT EXISTS cdb_mosh_p1_ecommerce_app 
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- UAT Database  
CREATE DATABASE IF NOT EXISTS cdb_mosh_p1_ecommerce_app_uat 
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Production Database
CREATE DATABASE IF NOT EXISTS cdb_mosh_p1_ecommerce_app_prod 
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Environment Variables
Set these environment variables (optional, defaults provided):

```bash
export DB_USERNAME=root
export DB_PASSWORD=your_mysql_password
export DB_HOST=localhost
export DB_PORT=3308
```

## 🔧 Complete MySQL Datasource Configuration

The application now uses these **complete MySQL connection parameters**:

### Development Profile (`dev`)
```yaml
datasource:
  url: jdbc:mysql://localhost:3308/cdb_mosh_p1_ecommerce_app?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8
  driver-class-name: com.mysql.cj.jdbc.Driver
  username: ${DB_USERNAME:root}
  password: ${DB_PASSWORD:}
  hikari:
    maximum-pool-size: 15
    minimum-idle: 5
    connection-timeout: 30000
    idle-timeout: 600000
    max-lifetime: 1800000
    auto-commit: false
    connection-test-query: SELECT 1
    validation-timeout: 5000
    leak-detection-threshold: 60000
```

### URL Parameters Explained:
- `useSSL=false` - Disable SSL for local development
- `allowPublicKeyRetrieval=true` - Allow public key retrieval for authentication
- `serverTimezone=UTC` - Set server timezone to UTC
- `createDatabaseIfNotExist=true` - Auto-create database if it doesn't exist
- `useUnicode=true` - Enable Unicode support
- `characterEncoding=UTF-8` - Set character encoding to UTF-8

### HikariCP Connection Pool Settings:
- `maximum-pool-size: 15` - Maximum number of connections in pool
- `minimum-idle: 5` - Minimum number of idle connections
- `connection-timeout: 30000` - Connection timeout (30 seconds)
- `idle-timeout: 600000` - Idle timeout (10 minutes)
- `max-lifetime: 1800000` - Maximum connection lifetime (30 minutes)
- `auto-commit: false` - Disable auto-commit for better transaction control
- `connection-test-query: SELECT 1` - Query to test connection validity
- `validation-timeout: 5000` - Connection validation timeout (5 seconds)
- `leak-detection-threshold: 60000` - Connection leak detection (60 seconds)

## 🚀 Starting the Application

### Method 1: Using Maven Wrapper (Recommended)

#### 1. Start with Development Profile (Default)
```bash
./mvnw spring-boot:run
```

#### 2. Start with Specific Profile
```bash
# Development
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# UAT
./mvnw spring-boot:run -Dspring-boot.run.profiles=uat

# Production
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

#### 3. Start in Background
```bash
./mvnw spring-boot:run -Dspring-boot.run.fork=false &
```

#### 4. Start with Custom Database Settings
```bash
./mvnw spring-boot:run -DDB_USERNAME=myuser -DDB_PASSWORD=mypass
```

### Method 2: Using Compiled JAR

#### 1. Build the Application
```bash
./mvnw clean package -DskipTests
```

#### 2. Run the JAR
```bash
# Development
java -jar target/store-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev

# UAT
java -jar target/store-0.0.1-SNAPSHOT.jar --spring.profiles.active=uat

# Production
java -jar target/store-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## 🗄️ Flyway Migration Management

### Automatic Migration (Default Behavior)

**Flyway runs automatically** when the application starts because:
```yaml
spring:
  flyway:
    enabled: true  # Auto-run on startup
    baseline-on-migrate: true
    validate-on-migrate: true
```

### Manual Migration Control

#### 1. Using Maven Flyway Plugin

**Check Migration Status:**
```bash
./mvnw flyway:info -Dflyway.configFiles=src/main/resources/flyway.conf
```

**Run Migrations:**
```bash
# Development
./mvnw flyway:migrate -Dflyway.configFiles=src/main/resources/flyway.conf

# UAT
./mvnw flyway:migrate -Dflyway.configFiles=src/main/resources/flyway.conf -Dflyway.url=jdbc:mysql://localhost:3308/cdb_mosh_p1_ecommerce_app_uat

# Production
./mvnw flyway:migrate -Dflyway.configFiles=src/main/resources/flyway.conf -Dflyway.url=jdbc:mysql://localhost:3308/cdb_mosh_p1_ecommerce_app_prod
```

**Validate Migrations:**
```bash
./mvnw flyway:validate -Dflyway.configFiles=src/main/resources/flyway.conf
```

**Clean Database (⚠️ DANGER - Drops all objects):**
```bash
./mvnw flyway:clean -Dflyway.configFiles=src/main/resources/flyway.conf
```

#### 2. Using Shell Script (Recommended)

Make the script executable and run:
```bash
chmod +x flyway-migrate.sh

# Development
./flyway-migrate.sh dev

# UAT  
./flyway-migrate.sh uat

# Production
./flyway-migrate.sh prod
```

#### 3. Disable Auto-Migration

To disable automatic migration on startup, set:
```yaml
spring:
  flyway:
    enabled: false
```

Then run migrations manually before starting the app.

## 🔍 Verification Steps

### 1. Check Application Status
```bash
curl http://localhost:8080/actuator/health
```

### 2. Check Database Connection
```bash
curl http://localhost:8080/actuator/info
```

### 3. Check Flyway Migration History
```sql
SELECT * FROM flyway_schema_history ORDER BY installed_on DESC;
```

### 4. Access Swagger UI
```
http://localhost:8080/swagger-ui.html
```

### 5. Test Payment Endpoints
```bash
# Get payment info
curl http://localhost:8080/payment/info

# Process payment
curl -X POST "http://localhost:8080/payment/process?amount=100.00&method=paypal"
```

## 🛠️ Troubleshooting

### Common Issues & Solutions

#### 1. **Connection Refused**
```
Error: Connection refused to localhost:3308
```
**Solution:** Ensure MySQL is running on port 3308
```bash
sudo lsof -i :3308  # Check if port is in use
```

#### 2. **Database Access Denied**
```
Error: Access denied for user 'root'@'localhost'
```
**Solution:** Check credentials and permissions
```sql
GRANT ALL PRIVILEGES ON cdb_mosh_p1_ecommerce_app.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
```

#### 3. **Flyway Baseline Issues**
```
Error: Found non-empty schema without schema history table
```
**Solution:** Run baseline first
```bash
./mvnw flyway:baseline -Dflyway.configFiles=src/main/resources/flyway.conf
```

#### 4. **Bean Definition Override Error**
```
Error: Bean 'creditCard' already defined
```
**Solution:** This is already fixed by removing PaymentConfig.java

#### 5. **Port Already in Use**
```
Error: Port 8080 already in use
```
**Solution:** Change port or kill existing process
```bash
# Change port
./mvnw spring-boot:run -Dserver.port=8081

# Or kill existing process
sudo lsof -ti:8080 | xargs kill -9
```

## 📊 Application Profiles Summary

| Profile | Database | Port | SSL | Auto-Migration | Logging |
|---------|----------|------|-----|----------------|---------|
| `dev` | `cdb_mosh_p1_ecommerce_app` | 3308 | No | Yes | DEBUG |
| `uat` | `cdb_mosh_p1_ecommerce_app_uat` | 3308 | Yes | Yes | INFO |
| `prod` | `cdb_mosh_p1_ecommerce_app_prod` | 3308 | Yes | Yes | WARN |

## 🔐 Security Notes

- **Development:** SSL disabled, public key retrieval allowed
- **UAT/Production:** SSL enabled, enhanced security settings
- **Credentials:** Use environment variables, never hardcode passwords
- **Connection Pooling:** Optimized for each environment

## 📝 Next Steps

1. **Start MySQL** on port 3308
2. **Set environment variables** (optional)
3. **Run the application** using `./mvnw spring-boot:run`
4. **Verify** using the endpoints above
5. **Check logs** for any issues

The application will automatically:
- Create the database if it doesn't exist
- Run Flyway migrations
- Start the web server on port 8080
- Initialize payment services
- Enable Swagger UI for API documentation 