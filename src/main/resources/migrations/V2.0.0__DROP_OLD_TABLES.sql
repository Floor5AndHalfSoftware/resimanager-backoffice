-- Drop old tables from previous migrations that don't match the JPA entities

DROP TABLE IF EXISTS owners CASCADE;
DROP TABLE IF EXISTS users CASCADE;
