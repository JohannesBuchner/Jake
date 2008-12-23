DROP TABLE IF EXISTS Configuration CASCADE;
DROP TABLE IF EXISTS user CASCADE;


CREATE TABLE configuration ( key CHAR(36) PRIMARY KEY, value VARCHAR(255) );

CREATE TABLE usercredentials ( UUID CHAR(36) PRIMARY KEY, protocol VARCHAR(255) NOT NULL, username VARCHAR(255) NOT NULL, password VARCHAR(255), autologin BOOLEAN );

--DROP TABLE IF EXISTS project CASCADE;
--CREATE TABLE project ( 	uuid				CHAR(36)		PRIMARY KEY, 	userid				CHAR(36)		NOT NULL, 	rootpath			VARCHAR(255)		NOT NULL, 	name				VARCHAR(255)		NOT NULL,  	wasopened			BOOLEAN, autoannounce        BOOLEAN DEFAULT FALSE, autopull        BOOLEAN DEFAULT FALSE,	FOREIGN KEY (userid) REFERENCES user(ID)   );
