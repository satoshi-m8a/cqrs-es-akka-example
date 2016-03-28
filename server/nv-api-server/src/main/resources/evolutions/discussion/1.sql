# --- !Ups

CREATE TABLE DISCUSSIONS(
  ID          VARCHAR(36) NOT NULL PRIMARY KEY,
  TITLE       TEXT        NOT NULL,
  ALLOW_ANONYMOUS BOOLEAN DEFAULT FALSE NULL
);

CREATE TABLE PROJECTIONS(
  ID        VARCHAR(36) NOT NULL PRIMARY KEY,
  PROGRESS    BIGINT      NOT NULL
);

# --- !Downs
DROP TABLE PROJECTIONS;
DROP TABLE DISCUSSIONS;