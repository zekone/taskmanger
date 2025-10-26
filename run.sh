#!/bin/bash
set -e

if [ -f .env ]; then
    export $(grep -v '^#' .env | xargs)
fi

INIT_SQL="./tasks.sql"

# Create local data directory
mkdir -p ./$DATA_DIR

# Stop and remove existing container (if any)
if [ "$(docker ps -aq -f name=$CONTAINER_NAME)" ]; then
  echo "Stopping and removing existing container: $CONTAINER_NAME"
  docker stop $CONTAINER_NAME >/dev/null 2>&1
  docker rm $CONTAINER_NAME >/dev/null 2>&1
fi

# Run MySQL container with persistent volume
echo "Starting new MySQL container..."
docker compose up -d

# Wait for MySQL to initialize
echo "Waiting for MySQL to initialize..."
MAX_RETRIES=5
COUNT=0
until docker exec "$CONTAINER_NAME" mysqladmin ping -h "localhost" --silent; do
    COUNT=$((COUNT + 1))
    if [ "$COUNT" -ge "$MAX_RETRIES" ]; then
        echo "MySQL did not start after $((MAX_RETRIES * 3)) seconds. Exiting."
        exit 1
    fi
    sleep 3
done

# Create tasks table
echo "Creating 'tasks' table..."
docker exec -i $CONTAINER_NAME mysql -u$DB_USER -p$DB_PASS $DB_NAME < "$INIT_SQL"

# Build project
echo "Cleaning and building project..."
./gradlew clean build

# Run
echo "Running Spring Boot application..."
./gradlew bootRun
