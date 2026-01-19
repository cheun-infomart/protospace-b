CREATE TABLE prototypes (
   id       SERIAL             NOT NULL,
   name VARCHAR(256)    NOT NULL,
   catch_copy VARCHAR(512)    NOT NULL,
   concept VARCHAR(512)    NOT NULL,
   image VARCHAR(512)    NOT NULL,
   user_id INT  NOT NULL,
   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   PRIMARY KEY (id),
   FOREIGN KEY (user_id)  REFERENCES users(id)
);