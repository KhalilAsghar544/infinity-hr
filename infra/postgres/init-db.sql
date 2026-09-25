-- One-time setup: one application user, one database per microservice.
-- Run as the postgres superuser:
--   psql -U postgres -f infra/postgres/init-db.sql

CREATE ROLE infinityhr WITH LOGIN PASSWORD 'infinityhr';

CREATE DATABASE infinityhr_auth         OWNER infinityhr;
CREATE DATABASE infinityhr_employee     OWNER infinityhr;
CREATE DATABASE infinityhr_attendance   OWNER infinityhr;
CREATE DATABASE infinityhr_leave        OWNER infinityhr;
CREATE DATABASE infinityhr_payroll      OWNER infinityhr;
CREATE DATABASE infinityhr_notification OWNER infinityhr;
