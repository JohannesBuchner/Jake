


--  DROP TABLE IF EXISTS projectoptions CASCADE;
-- this is already done in db-setup-global.sql
--  CREATE TABLE projectoptions (
--	name				VARCHAR(255)		PRIMARY KEY,
--  	autoannounce			BOOLEAN,
--	autopull			BOOLEAN
--  );


  DROP TABLE IF EXISTS projectmember CASCADE;
  CREATE TABLE projectmember (
	memberID			CHAR(36),
	nickname			VARCHAR(100)		NULL,
	trustlevel			INTEGER			NOT NULL,
	PRIMARY KEY (memberID)
  );

  DROP TABLE IF EXISTS jakeobject CASCADE;
  CREATE TABLE jakeobject (
	objectID			CHAR(36),
	deleted				BOOLEAN,
	modified			BOOLEAN,
	PRIMARY KEY (objectID)
  );

  DROP TABLE IF EXISTS note CASCADE;
  CREATE TABLE note (
	objectID			CHAR(36),
	text				VARCHAR(255)		NOT NULL,
	PRIMARY KEY (objectID),
	FOREIGN KEY (objectID) REFERENCES jakeobject(objectID)
  );


  DROP TABLE IF EXISTS file CASCADE;
  CREATE TABLE file (
	objectID			CHAR(36),
	path				VARCHAR(255),
	hash				VARCHAR(255),
	isDirectory			BOOLEAN,
	PRIMARY KEY (objectID),
	FOREIGN KEY (objectID) REFERENCES jakeobject(objectID)
  );

  DROP TABLE IF EXISTS tag CASCADE;
  CREATE TABLE tag (
  	objectId CHAR(36) NOT NUL,
	text				VARCHAR(255)		NOT NULL,
    PRIMARY KEY (objectid, text),
    FOREIGN KEY (objectId) REFERENCES jakeobject(objectId)
  );



--  CREATE TABLE object_taggedwith_tag (
--	objectID			CHAR(36),
--	tag				CHAR(36),
--	PRIMARY KEY (objectID,tag),
	--FOREIGN KEY (objectID) REFERENCES jakeobject(objectID),
--	FOREIGN KEY (tag) REFERENCES tag(ID)
--  );

  DROP TABLE IF EXISTS logEntry CASCADE;
  CREATE TABLE logEntry (
	ID				CHAR(36)		PRIMARY KEY,
	memberID			CHAR(36)		NOT NULL,
	objectID			CHAR(36),
	comment				VARCHAR(255),
	hash				VARCHAR(255)		NOT NULL,
	time				DATETIME		NOT NULL,
	processed			BOOLEAN,
	action				INTEGER			NOT NULL,
	FOREIGN KEY (memberID) REFERENCES projectmember(memberID),
	FOREIGN KEY (objectID) REFERENCES jakeobject(objectID)
  );
