#!/bin/bash
set -e

# Wait for PostgreSQL to be ready
echo "Waiting for PostgreSQL to be ready..."
until PGPASSWORD=postgres psql -h localhost -U postgres -d postgres -c '\q' 2>/dev/null; do
  echo "PostgreSQL is unavailable - sleeping"
  sleep 1
done

# Create database if it doesn't exist
PGPASSWORD=postgres psql -h localhost -U postgres -d postgres -c "CREATE DATABASE document_control;" 2>/dev/null || true

echo "PostgreSQL is up - executing migrations"

# Set environment variables for Flyway
export FLYWAY_URL="jdbc:postgresql://localhost:5432/document_control"
export FLYWAY_USER="postgres"
export FLYWAY_PASSWORD="postgres"

# Run Flyway migrations
sbt "flywayMigrate"

echo "Migrations completed successfully" 