# --- !Ups
CREATE TABLE SITES (
  ID          VARCHAR(36) NOT NULL PRIMARY KEY,
  NAME        TEXT        NOT NULL
);



# --- !Downs
DROP TABLE SITES;