#!/bin/bash

echo "Stopping and removing PostgreSQL container..."
docker stop document-control-postgres
docker rm document-control-postgres 