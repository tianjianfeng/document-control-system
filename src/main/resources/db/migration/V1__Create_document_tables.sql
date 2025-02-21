CREATE TABLE document_types (
  id UUID PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description TEXT
);

CREATE TABLE documents (
  id UUID PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  document_type_id UUID NOT NULL REFERENCES document_types(id),
  project_id UUID NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE revisions (
  id UUID PRIMARY KEY,
  document_id UUID NOT NULL REFERENCES documents(id),
  version INT NOT NULL,
  content TEXT NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  created_by UUID NOT NULL,
  UNIQUE (document_id, version)
); 