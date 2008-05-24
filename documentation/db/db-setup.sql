CREATE TABLE `configuration` (
	name				VARCHAR(50)		PRIMARY KEY,
	value				VARCHAR(255)
);

CREATE TABLE `projectmembers` (
	userid			VARCHAR(255)	PRIMARY KEY,
	nick				VARCHAR(50),
	notes				LONGVARCHAR
);

CREATE TABLE `objects` (
	name				VARCHAR,
	type				TINYINT,
	PRIMARY KEY (name, type)
);

CREATE TABLE `noteobjects` (
	name				VARCHAR,
	content			VARCHAR NOT NULL,
	PRIMARY KEY (name),
	FOREIGN KEY (name) REFERENCES objects(name)
);

CREATE TABLE `tags` (
	object_name		VARCHAR,
	object_type		TINYINT,
	tag				VARCHAR(50),
	PRIMARY KEY(object_name, object_type, tag),	
	FOREIGN KEY (object_name, object_type) REFERENCES objects(name, type)
);

CREATE TABLE `logentries` (
	object_name		VARCHAR,
	object_type		TINYINT,
	projectmember	VARCHAR(255),
	timestamp		DATETIME,
	action			VARCHAR(10) NOT NULL,
	message			VARCHAR(255),
	hash				VARCHAR(65) NOT NULL,
	is_last_pulled	BOOLEAN,
	PRIMARY KEY(object_name, object_type, projectmember, timestamp),	
	FOREIGN KEY (object_name, object_type) REFERENCES objects(name, type),
	FOREIGN KEY (projectmember) REFERENCES projectmembers(userid)
);

