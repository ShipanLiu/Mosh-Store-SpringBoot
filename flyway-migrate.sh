#!/bin/bash

# Flyway Migration Script for Different Environments
# Usage: ./flyway-migrate.sh [environment] [command]
# Example: ./flyway-migrate.sh dev migrate
# Example: ./flyway-migrate.sh prod info

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Default values
ENVIRONMENT=${1:-dev}
COMMAND=${2:-migrate}

echo -e "${BLUE}🚀 Flyway Migration Tool${NC}"
echo -e "${BLUE}Environment: ${YELLOW}$ENVIRONMENT${NC}"
echo -e "${BLUE}Command: ${YELLOW}$COMMAND${NC}"
echo ""

# Function to run flyway command
run_flyway() {
    local env=$1
    local cmd=$2
    local url=$3
    local user=$4
    local password=$5
    
    echo -e "${GREEN}Running flyway:$cmd for $env environment...${NC}"
    
    ./mvnw flyway:$cmd \
        -Dflyway.url="$url" \
        -Dflyway.user="$user" \
        -Dflyway.password="$password" \
        -Dflyway.locations="classpath:db/migration" \
        -Dflyway.baselineOnMigrate=true \
        -Dflyway.validateOnMigrate=true
}

# Environment-specific configurations
case $ENVIRONMENT in
    "dev")
        echo -e "${YELLOW}📋 Development Environment${NC}"
        DB_URL="jdbc:mysql://localhost:3306/cdb_mosh_p1_ecommerce"
        DB_USER="${DB_USERNAME:-root}"
        DB_PASS="${DB_PASSWORD:-}"
        ;;
    
    "uat")
        echo -e "${YELLOW}📋 UAT Environment${NC}"
        if [[ -z "$DB_HOST" ]]; then
            echo -e "${RED}❌ Error: DB_HOST environment variable is required for UAT${NC}"
            echo "Set it with: export DB_HOST=your-uat-server.com"
            exit 1
        fi
        DB_URL="jdbc:mysql://${DB_HOST}:${DB_PORT:-3306}/cdb_mosh_p1_ecommerce_uat"
        DB_USER="${DB_USERNAME}"
        DB_PASS="${DB_PASSWORD}"
        ;;
    
    "prod")
        echo -e "${RED}⚠️  PRODUCTION Environment - Use with caution!${NC}"
        read -p "Are you sure you want to run flyway:$COMMAND on PRODUCTION? (yes/no): " confirm
        if [[ $confirm != "yes" ]]; then
            echo -e "${YELLOW}Operation cancelled.${NC}"
            exit 0
        fi
        
        if [[ -z "$DB_HOST" || -z "$DB_USERNAME" || -z "$DB_PASSWORD" ]]; then
            echo -e "${RED}❌ Error: DB_HOST, DB_USERNAME, and DB_PASSWORD environment variables are required for production${NC}"
            exit 1
        fi
        
        DB_URL="jdbc:mysql://${DB_HOST}:${DB_PORT:-3306}/cdb_mosh_p1_ecommerce_prod"
        DB_USER="${DB_USERNAME}"
        DB_PASS="${DB_PASSWORD}"
        ;;
    
    *)
        echo -e "${RED}❌ Error: Unknown environment '$ENVIRONMENT'${NC}"
        echo "Supported environments: dev, uat, prod"
        exit 1
        ;;
esac

# Validate command
case $COMMAND in
    "migrate"|"info"|"validate"|"baseline"|"repair")
        ;;
    "clean")
        if [[ $ENVIRONMENT == "prod" ]]; then
            echo -e "${RED}❌ Error: 'clean' command is not allowed in production!${NC}"
            exit 1
        fi
        echo -e "${RED}⚠️  WARNING: This will DROP ALL database objects!${NC}"
        read -p "Are you sure? (yes/no): " confirm
        if [[ $confirm != "yes" ]]; then
            echo -e "${YELLOW}Operation cancelled.${NC}"
            exit 0
        fi
        ;;
    *)
        echo -e "${RED}❌ Error: Unknown command '$COMMAND'${NC}"
        echo "Supported commands: migrate, info, validate, baseline, repair, clean"
        exit 1
        ;;
esac

echo -e "${BLUE}Database URL: ${NC}$DB_URL"
echo -e "${BLUE}Database User: ${NC}$DB_USER"
echo ""

# Run the flyway command
run_flyway "$ENVIRONMENT" "$COMMAND" "$DB_URL" "$DB_USER" "$DB_PASS"

echo ""
echo -e "${GREEN}✅ Flyway $COMMAND completed successfully for $ENVIRONMENT environment!${NC}"

# Show helpful next steps
case $COMMAND in
    "migrate")
        echo -e "${BLUE}💡 Next steps:${NC}"
        echo "  - Run './flyway-migrate.sh $ENVIRONMENT info' to see migration status"
        echo "  - Run './flyway-migrate.sh $ENVIRONMENT validate' to validate migrations"
        ;;
    "info")
        echo -e "${BLUE}💡 Tip:${NC} Run './flyway-migrate.sh $ENVIRONMENT migrate' to apply pending migrations"
        ;;
esac 