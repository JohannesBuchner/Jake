package com.jakeapp.core.domain;

import org.jivesoftware.smackx.workgroup.packet.UserID;

import java.io.Serializable;


/**
 * The logAction represents the status of a certain JakeObject/LogEntry.
 * 
 * always important {@link LogEntry} attributes: who, when
 * 
 * @author Dominik, Christopher, johannes
 */
public enum LogAction implements Serializable {

	/**
	 * the project was just created. first log entry only. <br/>
	 * belongsTo: null<br />
	 * additional storing: UUID, Name <br/>
	 * can only be the first {@link LogEntry}
     * is always an instance of {@link ProjectLogEntry}
	 */
	PROJECT_CREATED,

	/**
	 * A user starts to use this project and wants to tell others (invitation
	 * accepted)<br />
	 * belongsTo: null<br />
	 */
	NOOP,

	/**
	 * a new version of a JakeObject was created. <br/>
	 * actions: announce belongsTo: {@link JakeObject}<br />
	 * additional storing: whether {@link FileObject} or {@link NoteObject},
	 * relpath/UUID<br/>
	 * important LogEntry attributes: comment, checksum
     * is always an instance of {@link JakeObjectLogEntry}
	 */
	JAKE_OBJECT_NEW_VERSION,

	/**
	 * the JakeObject was deleted. <br/>
	 * belongsTo: {@link JakeObject}<br />
	 * additional storing: whether {@link FileObject} or {@link NoteObject},
	 * relpath/UUID<br/>
	 * important LogEntry attributes: comment
     * is always an instance of {@link JakeObjectLogEntry}
	 */
	JAKE_OBJECT_DELETE,

	/**
	 * A tag was added to the {@link JakeObject} <br/>
	 * belongsTo: {@link JakeObject}<br/>
	 * important LogEntry attributes: none
	 */
	TAG_ADD,

	/**
	 * A tag was removed from the {@link JakeObject} <br/>
	 * belongsTo: {@link JakeObject} <br/>
	 * important LogEntry attributes: none
	 */
	TAG_REMOVE,

	/**
	 * The user adding this {@link LogEntry} states that the user adding it
	 * trusts incoming changes from the user in belongsTo <br/>
	 * belongsTo: {@link UserID} <br/>
	 * important LogEntry attributes: none
	 */
	START_TRUSTING_PROJECTMEMBER,

	/**
	 * The user adding this {@link LogEntry} states that the user adding it does
	 * not trust incoming changes from the user in belongsTo anymore<br/>
	 * belongsTo: {@link UserID} <br/>
	 * important LogEntry attributes: none
	 */
	STOP_TRUSTING_PROJECTMEMBER,

	/**
	 * The user adding this {@link LogEntry} states that the user adding it
	 * trusts incoming changes from the user in belongsTo <br>
	 * The user adding this automatically
	 * starts trusting users when they appear to be trusted by the user and 
	 * stops trusting users when they appear to be trusted by the user<br/>
	 * belongsTo: {@link UserID} <br/>
	 * important LogEntry attributes: none
	 */
	FOLLOW_TRUSTING_PROJECTMEMBER,

	/**
	 * the object has been soft locked. <br/>
	 * belongsTo: {@link JakeObject} <br/>
	 * important LogEntry attributes: commitmsg
	 */
	JAKE_OBJECT_LOCK,

	/**
	 * the object has been soft locked. <br/>
	 * belongsTo: {@link JakeObject} <br/>
	 * important LogEntry attributes: commitmsg
	 */
	JAKE_OBJECT_UNLOCK





}
