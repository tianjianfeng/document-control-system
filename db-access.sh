#!/bin/bash

# Get the container ID or name
CONTAINER_NAME="document-control-db"  # adjust this to match your container name

# Connect directly to the PostgreSQL instance in the container
docker exec -it $CONTAINER_NAME psql -U postgres -d document_control

# Useful SQL commands to try:
# \dt                     -- List all tables
# \d+ documents          -- Describe documents table
# SELECT * FROM documents; -- Query documents
# SELECT * FROM revisions; -- Query revisions 