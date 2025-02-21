#!/bin/bash

# Base URL for the API
BASE_URL="http://localhost:8080/api"

# Generate random UUIDs for testing
DOC_TYPE_ID=$(uuidgen)
PROJECT_ID=$(uuidgen)

echo "Testing Document API endpoints..."

# Create a new document
echo "\n1. Creating a new document..."
DOCUMENT_ID=$(curl -X POST "$BASE_URL/documents" \
  -H "Content-Type: application/json" \
  -d "{
    \"title\": \"Test Document\",
    \"documentTypeId\": \"$DOC_TYPE_ID\",
    \"projectId\": \"$PROJECT_ID\"
  }" | jq -r '.id')

echo "Created document with ID: $DOCUMENT_ID"

# Get the document by ID
echo "\n2. Retrieving the document..."
curl -X GET "$BASE_URL/documents/$DOCUMENT_ID"

# Update the document
echo "\n3. Updating the document..."
curl -X PUT "$BASE_URL/documents/$DOCUMENT_ID" \
  -H "Content-Type: application/json" \
  -d "{
    \"title\": \"Updated Test Document\"
  }"

# Get all documents for a project
echo "\n4. Getting all documents for project..."
curl -X GET "$BASE_URL/documents?projectId=$PROJECT_ID"

# Create a new revision
echo "\n5. Creating a new revision..."
curl -X POST "$BASE_URL/documents/$DOCUMENT_ID/revisions" \
  -H "Content-Type: application/json" \
  -d "{
    \"content\": \"This is the document content\",
    \"userId\": \"$(uuidgen)\"
  }"

# Get all revisions for a document
echo "\n6. Getting all revisions for document..."
curl -X GET "$BASE_URL/documents/$DOCUMENT_ID/revisions"

# Delete the document
echo "\n7. Deleting the document..."
curl -X DELETE "$BASE_URL/documents/$DOCUMENT_ID" 