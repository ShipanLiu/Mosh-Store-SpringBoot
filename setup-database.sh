#!/bin/bash

# Database Setup Script for Mosh Store SpringBoot
# This script helps you set up the MySQL databases

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🗄️  MySQL Database Setup for Mosh Store${NC}"
echo ""

echo -e "${YELLOW}📋 This script will help you:${NC}"
echo "  1. Create the required databases"
echo "  2. Set up proper user permissions"
echo "  3. Test the connection"
echo ""

# Check if MySQL is running
if ! pgrep -x "mysqld" > /dev/null; then
    echo -e "${RED}❌ MySQL is not running!${NC}"
    echo -e "${BLUE}💡 Start MySQL with: ${YELLOW}brew services start mysql${NC}"
    exit 1
fi

echo -e "${GREEN}✅ MySQL is running${NC}"
echo ""

# Prompt for MySQL root password
echo -e "${BLUE}🔑 Please enter your MySQL root password:${NC}"
read -s MYSQL_ROOT_PASSWORD

# Test connection
echo -e "${BLUE}🔍 Testing MySQL connection...${NC}"
if ! mysql -u root -p"$MYSQL_ROOT_PASSWORD" -e "SELECT 1;" > /dev/null 2>&1; then
    echo -e "${RED}❌ Failed to connect to MySQL with provided password${NC}"
    echo -e "${BLUE}💡 Make sure you have the correct root password${NC}"
    exit 1
fi

echo -e "${GREEN}✅ MySQL connection successful${NC}"
echo ""

# Create databases
echo -e "${BLUE}🏗️  Creating databases...${NC}"

mysql -u root -p"$MYSQL_ROOT_PASSWORD" << EOF
-- Create databases for different environments
CREATE DATABASE IF NOT EXISTS cdb_mosh_p1_ecommerce;
CREATE DATABASE IF NOT EXISTS cdb_mosh_p1_ecommerce_uat;
CREATE DATABASE IF NOT EXISTS cdb_mosh_p1_ecommerce_prod;

-- Show created databases
SHOW DATABASES LIKE 'cdb_mosh_p1_%';

-- Grant permissions to root for all databases (for development)
GRANT ALL PRIVILEGES ON cdb_mosh_p1_ecommerce.* TO 'root'@'localhost';
GRANT ALL PRIVILEGES ON cdb_mosh_p1_ecommerce_uat.* TO 'root'@'localhost';
GRANT ALL PRIVILEGES ON cdb_mosh_p1_ecommerce_prod.* TO 'root'@'localhost';

FLUSH PRIVILEGES;

-- Display success message
SELECT 'Database setup completed successfully!' as Status;
EOF

echo -e "${GREEN}✅ Databases created successfully!${NC}"
echo ""

# Test Flyway connection
echo -e "${BLUE}🧪 Testing Flyway connection...${NC}"
if DB_PASSWORD="$MYSQL_ROOT_PASSWORD" ./flyway-migrate.sh dev info > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Flyway connection successful${NC}"
else
    echo -e "${YELLOW}⚠️  Flyway connection test failed, but databases are created${NC}"
    echo -e "${BLUE}💡 You may need to run migrations manually${NC}"
fi

echo ""
echo -e "${GREEN}🎉 Database setup completed!${NC}"
echo ""
echo -e "${BLUE}📝 Next steps:${NC}"
echo "  1. Set environment variable: ${YELLOW}export DB_PASSWORD='$MYSQL_ROOT_PASSWORD'${NC}"
echo "  2. Run migrations: ${YELLOW}DB_PASSWORD='$MYSQL_ROOT_PASSWORD' ./flyway-migrate.sh dev migrate${NC}"
echo "  3. Start application: ${YELLOW}DB_PASSWORD='$MYSQL_ROOT_PASSWORD' ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev${NC}"
echo ""
echo -e "${BLUE}🔐 Security Note:${NC} For production, create dedicated database users instead of using root." 