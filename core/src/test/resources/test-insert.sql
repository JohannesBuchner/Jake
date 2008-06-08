INSERT INTO configuration (name, value) VALUES ('foo', 'bar');
INSERT INTO configuration (name, value) VALUES ('deleteme', 'deleteme');
INSERT INTO configuration (name, value) VALUES ('userid', 'chris@jabber.doublesignal.com');
INSERT INTO configuration (name, value) VALUES ('password', 'mypassword08');

INSERT INTO projectmembers (userid, nick, notes, active) VALUES ('chris@jabber.doublesignal.com', 'Chris', 'Supreme leader of earth', 1);
INSERT INTO projectmembers (userid, nick, notes, active) VALUES ('dominik@jabber.fsinf.at', 'Dominik', 'Takes pride in his code', 1);
INSERT INTO projectmembers (userid, nick, notes, active) VALUES ('j13r@jabber.ccc.de', 'Johannes', 'Likes chocolate cookies', 1);

INSERT INTO objects (name) VALUES ('test.docx');
INSERT INTO objects (name) VALUES ('subfolder/sepm.txt');
INSERT INTO objects (name) VALUES ('pr0n.jpg');

INSERT INTO objects (name) VALUES ('note:chris@jabber.doublesignal.com:20080531201500');
INSERT INTO noteobjects (name, content) VALUES ('note:chris@jabber.doublesignal.com:20080531201500', 'I am a machine.');
INSERT INTO objects (name) VALUES ('note:j13r@jabber.ccc.de:20080531201910');
INSERT INTO noteobjects (name, content) VALUES ('note:j13r@jabber.ccc.de:20080531201910', 'Lorem ipsum dolor sit amet.');
INSERT INTO objects (name) VALUES ('note:dominik@jabber.fsinf.at:20080531201955');
INSERT INTO noteobjects (name, content) VALUES ('note:dominik@jabber.fsinf.at:20080531201955', '99 bottles of beer on the wall!');

INSERT INTO tags (object_name, tag) VALUES ('test.docx', 'word');
INSERT INTO tags (object_name, tag) VALUES ('test.docx', 'microsoft');
INSERT INTO tags (object_name, tag) VALUES ('test.docx', 'test');
INSERT INTO tags (object_name, tag) VALUES ('test.docx', 'foobar');
INSERT INTO tags (object_name, tag) VALUES ('pr0n.jpg', 'private');
INSERT INTO tags (object_name, tag) VALUES ('note:chris@jabber.doublesignal.com:20080531201500', 'important');
INSERT INTO tags (object_name, tag) VALUES ('note:chris@jabber.doublesignal.com:20080531201500', 'university');
INSERT INTO tags (object_name, tag) VALUES ('note:chris@jabber.doublesignal.com:20080531201500', 'foobar');
INSERT INTO tags (object_name, tag) VALUES ('note:j13r@jabber.ccc.de:20080531201910', 'foobar');
INSERT INTO tags (object_name, tag) VALUES ('note:j13r@jabber.ccc.de:20080531201910', 'loremipsum');

INSERT INTO logentries (object_name, projectmember, timestamp, action, message, hash, is_last_pulled) VALUES ('test.docx', 'dominik@jabber.fsinf.at', '2008-05-31 21:00:00', 'NEW_VERSION', '', '07E547D9586F6A73F73FBAC0435ED76951218FB7D0C8D788A309D785436BBB642E93A252A954F23912547D1E8A3B5ED6E1BFD7097821233FA0538F3DB854FEE6', 1);
INSERT INTO logentries (object_name, projectmember, timestamp, action, message, hash, is_last_pulled) VALUES ('test.docx', 'chris@jabber.doublesignal.com', '2008-05-31 21:18:00', 'TAG_ADD', 'important', '07E547D9586F6A73F73FBAC0435ED76951218FB7D0C8D788A309D785436BBB642E93A252A954F23912547D1E8A3B5ED6E1BFD7097821233FA0538F3DB854FEE6', 0);
INSERT INTO logentries (object_name, projectmember, timestamp, action, message, hash, is_last_pulled) VALUES ('test.docx', 'dominik@jabber.fsinf.at', '2008-06-01 07:22:10', 'NEW_VERSION', '', '51687BE5C2800D2C5C54D088261B2B55EC9A5CB62AEC7CAAF4F82613F84A47E2BA0330EDF3A181D2B31684ADB53AA67A7D350C81C84F009D5030FC8C1C308989', 0);
INSERT INTO logentries (object_name, projectmember, timestamp, action, message, hash, is_last_pulled) VALUES ('pr0n.jpg', 'dominik@jabber.fsinf.at', '2008-05-31 23:19:55', 'NEW_VERSION', '', '4B88C813C37F1AF842CA7FF14FE3E3D4BBA89CD3D4F36B7838243F8B84E961E4FBBE0E66412D7EE85B184DB1466A0677E40D9CE3FFF4831F45F91FCB43E1C3ED', 1);
INSERT INTO logentries (object_name, projectmember, timestamp, action, message, hash, is_last_pulled) VALUES ('note:j13r@jabber.ccc.de:20080531201910', 'j13r@jabber.ccc.de', '2008-05-31 18:10:00', 'NEW_VERSION', '', '1B63A2FBDB8D0BB0DB83F60877E964C1D0EF480152D983C5C406D89338D71EBF14567F591954FA160AF48FF97DBA0008AAC4291DDC45A9E4BFB31D72BCE1CE53', 0);
INSERT INTO logentries (object_name, projectmember, timestamp, action, message, hash, is_last_pulled) VALUES ('note:j13r@jabber.ccc.de:20080531201910', 'j13r@jabber.ccc.de', '2008-06-01 13:14:15', 'DELETE', '', '1B63A2FBDB8D0BB0DB83F60877E964C1D0EF480152D983C5C406D89338D71EBF14567F591954FA160AF48FF97DBA0008AAC4291DDC45A9E4BFB31D72BCE1CE53', 1);
INSERT INTO logentries (object_name, projectmember, timestamp, action, message, hash, is_last_pulled) VALUES ('subfolder/sepm.txt', 'chris@jabber.doublesignal.com', '2008-05-31 17:22:12', 'NEW_VERSION', '', 'CF83E1357EEFB8BDF1542850D66D8007D620E4050B5715DC83F4A921D36CE9CE47D0D13C5D85F2B0FF8318D2877EEC2F63B931BD47417A81A538327AF927DA3E', 0);