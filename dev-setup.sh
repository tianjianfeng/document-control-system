#!/bin/bash
set -e

echo "Starting PostgreSQL container for local development..."
docker run --name document-control-postgres \
  -e POSTGRES_DB=document_control \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -d postgres:14

echo "Waiting for PostgreSQL to be ready..."
until docker exec document-control-postgres pg_isready -U postgres > /dev/null 2>&1; do
  echo "PostgreSQL is unavailable - sleeping"
  sleep 1
done

echo "PostgreSQL is ready!"
echo "You can now run 'sbt run' to start the application" 