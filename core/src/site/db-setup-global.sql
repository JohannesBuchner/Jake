  DROP TABLE IF EXISTS configuration CASCADE;  
  CREATE TABLE configuration (
	key                 CHAR(36)        PRIMARY KEY,
	value               VARCHAR(255)
  );

  DROP TABLE IF EXISTS servicecredentials CASCADE;
  CREATE TABLE servicecredentials (
	uuid				CHAR(36)		PRIMARY KEY,
	protocol			VARCHAR(255)		NOT NULL,
	username			VARCHAR(255)		NOT NULL,
	password			VARCHAR(255),
    resourcename        VARCHAR(255) NOT NULL,
	autologin			BOOLEAN DEFAULT FALSE,
    server              VARCHAR(255),
    port                INTEGER,
    encryption          BOOLEAN DEFAULT FALSE
  );

  DROP TABLE IF EXISTS project CASCADE;
  CREATE TABLE project (
    uuid                CHAR(36)        PRIMARY KEY,
    userid              CHAR(36),
    rootpath            VARCHAR(255)        NOT NULL,
    name                VARCHAR(255)        NOT NULL,
    opened              BOOLEAN DEFAULT FALSE,
    started             BOOLEAN DEFAULT FALSE,
    autoannounce        BOOLEAN DEFAULT FALSE,
    autopull            BOOLEAN DEFAULT FALSE,
    autologin           BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (userid) REFERENCES servicecredentials(UUID)
  );



  DROP TABLE IF EXISTS users CASCADE;
  CREATE TABLE users (
    uuid                CHAR(36)    PRIMARY KEY,
    userid              VARCHAR (255) NOT NULL,
    nickname            VARCHAR (255),
    firstname           VARCHAR (255),
    surname             VARCHAR (255),
    protocol            VARCHAR (255),
    sc_uuid             CHAR (36) NULL,
    FOREIGN KEY (sc_uuid) REFERENCES servicecredentials(UUID)
  );
  ALTER TABLE users ADD CONSTRAINT userid_uniq UNIQUE (sc_uuid, userid);

  