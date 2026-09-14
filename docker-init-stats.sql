CREATE DATABASE "ewm-analyzer";

\c "ewm-analyzer"
CREATE SCHEMA IF NOT EXISTS analyzer;
GRANT ALL PRIVILEGES ON SCHEMA analyzer TO dbuser;

