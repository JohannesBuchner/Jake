INSERT INTO configuration (name, value) VALUES ('foo', 'bar');
INSERT INTO configuration (name, value) VALUES ('deleteme', 'deleteme');
INSERT INTO configuration (name, value) VALUES ('userid', 'chris@jabber.doublesignal.com');
INSERT INTO configuration (name, value) VALUES ('password', 'mypassword08');

INSERT INTO projectmembers (userid, nick, notes) VALUES ('chris@jabber.doublesignal.com', 'Chris', 'Supreme leader of earth');
INSERT INTO projectmembers (userid, nick, notes) VALUES ('dominik@jabber.fsinf.at', 'Dominik', 'Takes pride in his code');
INSERT INTO projectmembers (userid, nick, notes) VALUES ('j13r@jabber.ccc.de', 'Johannes', 'Likes chocolate cookies');

INSERT INTO objects (name) VALUES ('test.docx');
INSERT INTO objects (name) VALUES ('subfolder/sepm.txt');
INSERT INTO objects (name) VALUES ('pr0n.jpg');

INSERT INTO objects (name) VALUES ('note:chris@jabber.doublesignal.com:20080531201500');
INSERT INTO noteobjects (name, content) VALUES ('note:chris@jabber.doublesignal.com:20080531201500', 'I am a machine.');
INSERT INTO objects (name) VALUES ('note:j13r@jabber.ccc.de:20080531201910');
INSERT INTO noteobjects (name, content) VALUES ('note:j13r@jabber.ccc.de:20080531201910', 'Lorem ipsum dolor sit amet.');
INSERT INTO objects (name) VALUES ('note:dominik@jabber.fsinf.at:20080531201955');
INSERT INTO noteobjects (name, content) VALUES ('note:dominik@jabber.fsinf.at:20080531201955', '99 bottles of beer on the wall!');