  DROP TABLE IF EXISTS configuration CASCADE;
  DROP TABLE IF EXISTS projectmembers CASCADE;
  DROP TABLE IF EXISTS objects CASCADE;
  DROP TABLE IF EXISTS noteobjects CASCADE;
  DROP TABLE IF EXISTS tags CASCADE;
  DROP TABLE IF EXISTS logentries CASCADE;

  CREATE TABLE configuration (
  	name				VARCHAR(50)		PRIMARY KEY,
  	value				VARCHAR(255)
  );

  CREATE TABLE projectmembers (
  	userid			VARCHAR(255)	PRIMARY KEY,
  	nick				VARCHAR(50),
  	notes				LONGVARCHAR
  );

  CREATE TABLE objects (
  	name				VARCHAR,
  	PRIMARY KEY (name)
  );

  CREATE TABLE noteobjects (
  	name				VARCHAR,
  	content			VARCHAR NOT NULL,
  	PRIMARY KEY (name),
  	FOREIGN KEY (name) REFERENCES objects(name)
  );

  CREATE TABLE tags (
  	object_name		VARCHAR,
  	tag				VARCHAR(50),
  	PRIMARY KEY(object_name, tag),
  	FOREIGN KEY (object_name) REFERENCES objects(name)
  );

  CREATE TABLE logentries (
  	object_name		VARCHAR,
  	projectmember	VARCHAR(255),
  	timestamp		DATETIME,
  	action			VARCHAR(10) NOT NULL,
  	message			VARCHAR(255),
  	hash				VARCHAR(65) NOT NULL,
  	is_last_pulled	BOOLEAN,
  	PRIMARY KEY(object_name, projectmember, timestamp),
  	FOREIGN KEY (object_name) REFERENCES objects(name),
  	FOREIGN KEY (projectmember) REFERENCES projectmembers(userid)
  );