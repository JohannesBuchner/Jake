-- this is the content of /src/main/resources/com/jakeapp/core/domain/db-setup.sql

DROP TABLE IF EXISTS projectoptions CASCADE;
DROP TABLE IF EXISTS projectmember CASCADE;
DROP TABLE IF EXISTS note CASCADE;
DROP TABLE IF EXISTS file CASCADE;
DROP TABLE IF EXISTS logEntry CASCADE;
DROP TABLE IF EXISTS jakeobject CASCADE;
DROP TABLE IF EXISTS tag CASCADE;



-- CREATE TABLE projectoptions (       name				VARCHAR(255)		PRIMARY KEY,    autoannounce			BOOLEAN,  autopull			BOOLEAN);

CREATE TABLE projectmember (memberID			CHAR(36), nickname			VARCHAR(100)		NULL, trustlevel			VARCHAR(100)			NOT NULL, PRIMARY KEY (memberID) );

CREATE TABLE jakeobject ( objectID			CHAR(36), deleted				BOOLEAN, modified			BOOLEAN, PRIMARY KEY (objectID) );

CREATE TABLE note ( objectID			CHAR(36), text				VARCHAR(255)		NOT NULL, PRIMARY KEY (objectID), FOREIGN KEY (objectID) REFERENCES jakeobject(objectID) );

CREATE TABLE file (    objectID			CHAR(36),  path				VARCHAR(255),  hash				VARCHAR(255),  isDirectory			BOOLEAN,  PRIMARY KEY (objectID),  FOREIGN KEY (objectID) REFERENCES jakeobject(objectID));

CREATE TABLE tag (               	objectId CHAR(36) NOT NULL,	text				VARCHAR(255)		NOT NULL, PRIMARY KEY (objectId, text).   FOREIGN KEY (objectId) REFERENCES jakeobject(objectId)  );

CREATE TABLE logEntry (  ID				CHAR(36)		PRIMARY KEY,  memberID			CHAR(36)		NOT NULL,  objectID			CHAR(36),  hash				VARCHAR(255)		NOT NULL,  time				DATETIME		NOT NULL,  processed			BOOLEAN,  action				INTEGER			NOT NULL,  FOREIGN KEY (memberID) REFERENCES projectmember(memberID),  FOREIGN KEY (objectID) REFERENCES jakeobject(objectID));
