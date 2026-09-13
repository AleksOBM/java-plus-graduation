CREATE DATABASE "ewm-users";
CREATE DATABASE "ewm-events";
CREATE DATABASE "ewm-requests";
CREATE DATABASE "ewm-ratings";

\c "ewm-users"
CREATE SCHEMA IF NOT EXISTS user_service;
GRANT ALL PRIVILEGES ON SCHEMA user_service TO dbuser;

\c "ewm-events"
CREATE SCHEMA IF NOT EXISTS event_service;
GRANT ALL PRIVILEGES ON SCHEMA event_service TO dbuser;

\c "ewm-requests"
CREATE SCHEMA IF NOT EXISTS request_service;
GRANT ALL PRIVILEGES ON SCHEMA request_service TO dbuser;

\c "ewm-ratings"
CREATE SCHEMA IF NOT EXISTS rating_service;
GRANT ALL PRIVILEGES ON SCHEMA rating_service TO dbuser;
