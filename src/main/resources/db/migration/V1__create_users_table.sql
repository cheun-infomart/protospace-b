CREATE TABLE users (
   id       SERIAL             NOT NULL,
   name VARCHAR(128)    NOT NULL,
   email VARCHAR(128)    NOT NULL UNIQUE,
   profile VARCHAR(256)    NOT NULL,
   department VARCHAR(128)    NOT NULL,
   position VARCHAR(128)    NOT NULL,
   password VARCHAR(512)    NOT NULL,
   PRIMARY KEY (id)
);