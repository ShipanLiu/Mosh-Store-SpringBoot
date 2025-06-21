#!/bin/bash

# Load environment variables from .env file in resources folder
if [ -f src/main/resources/.env ]; then
    echo "Loading environment variables from src/main/resources/.env file"
    export $(grep -v '^#' src/main/resources/.env | xargs)
else
    echo "Error: src/main/resources/.env file not found"
    exit 1
fi

# Default to dev profile if not specified
PROFILE=${1:-dev}

echo "Running Flyway migration with profile: $PROFILE"

# Create a temporary properties file with the environment variables
TEMP_PROPS=$(mktemp)
cat > $TEMP_PROPS << EOF
flyway.${PROFILE}.user=$DB_USERNAME
flyway.${PROFILE}.password=$DB_PASSWORD
flyway.${PROFILE}.url=jdbc:mysql://${DB_HOST}:${DB_PORT}/cdb_mosh_p1_ecommerce_app$([ "$PROFILE" != "dev" ] && echo "_$PROFILE" || echo "")?useSSL=$([ "$PROFILE" != "dev" ] && echo "true" || echo "false")&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true
EOF

# Run Flyway migration with the temporary properties file
mvn flyway:migrate -Dflyway.configFiles=src/main/resources/flyway.conf,$TEMP_PROPS \
    -Dflyway.placeholders.DB_USERNAME=$DB_USERNAME \
    -Dflyway.placeholders.DB_PASSWORD=$DB_PASSWORD \
    -Dflyway.placeholders.DB_HOST=$DB_HOST \
    -Dflyway.placeholders.DB_PORT=$DB_PORT

# Clean up
rm $TEMP_PROPS

echo "Flyway migration completed for profile: $PROFILE" 