   dafdadsf  DROP TABLE IF EXISTS Configuration CASCADE;
  DROP TABLE IF EXISTS user CASCADE;
  DROP TABLE IF EXISTS project CASCADE;
  
  CREATE TABLE configuration (
	key				CHAR(36)		PRIMARY KEY,
	value				VARCHAR(255)
  );

  CREATE TABLE servicecredentials (
	UUID				CHAR(36)		PRIMARY KEY,
	protocol			VARCHAR(255)		NOT NULL,
	username			VARCHAR(255)		NOT NULL,
	password			VARCHAR(255),
	autologin			BOOLEAN,
    server              VARCHAR(255),
    port                INTEGER,
    encryption          BOOLEAN DEFAULT FALSE
  );

  CREATE TABLE project (
    uuid                CHAR(36)        PRIMARY KEY,
    userid              CHAR(36)            NOT NULL,
    rootpath            VARCHAR(255)        NOT NULL,
    name                VARCHAR(255)        NOT NULL,
    opened              BOOLEAN DEFAULT FALSE,
    started             BOOLEAN DEFAULT FALSE,
    autoannounce        BOOLEAN DEFAULT FALSE,
    autopull            BOOLEAN DEFAULT FALSE,
    autologin           BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (userid) REFERENCES usercredentials(UUID)
  );