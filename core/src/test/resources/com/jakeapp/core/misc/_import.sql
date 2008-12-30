
DROP TABLE IF EXISTS usercredentials;


CREATE TABLE usercredentials (	UUID				CHAR(36)		PRIMARY KEY,	protocol			VARCHAR(255)		NOT NULL,	username			VARCHAR(255)		NOT NULL,	password			VARCHAR(255),	autologin			BOOLEAN,    server              VARCHAR(255),    port                INTEGER,    encryption          BOOLEAN DEFAULT FALSE  );

DROP TABLE IF EXISTS project CASCADE;
  CREATE TABLE project (                           	uuid				CHAR(36)		PRIMARY KEY,	userid				CHAR(36)		 NULL,    	rootpath			VARCHAR(255)		NULL,	name				VARCHAR(255)		NULL,	opened BOOLEAN DEFAULT FALSE,     started BOOLEAN DEFAULT FALSE,                autoannounce        BOOLEAN DEFAULT FALSE,    autopull            BOOLEAN DEFAULT FALSE,     autologin           BOOLEAN DEFAULT FALSE );