CREATE TABLE apis
(
  name VARCHAR(100) PRIMARY KEY,
  swagger_url VARCHAR(100),
  port int,
  version VARCHAR(100),
  swagger json,
  date_updated TIMESTAMP DEFAULT current_timestamp NOT NULL
);