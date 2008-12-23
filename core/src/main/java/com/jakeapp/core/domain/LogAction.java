package com.jakeapp.core.domain;

/**
 * The logAction represents the status of a certain JakeObject/LogEntry.
 *
 * @author Dominik, Christopher
 */
public enum LogAction {

	/**
	 * the project was just created, first log entry ever.
	 */
    PROJECT_CREATED,

    /**
	 * a new version of a FileObject was created.
	 */
    FILE_NEW_VERSION,

    /**
     * The file has been added.
     */
    FILE_ADD,

    /**
	 * the FileObject in question was deleted.
	 */
    FILE_DELETE,

    /**
	 * a new version of a NoteObject was created.
	 */
    NOTE_NEW_VERSION,

    /**
     * A note has been added.
     */
    NOTE_ADD,

    /**
	 * the NoteObject in question was deleted.
	 */
    NOTE_DELETE,

    /**
	 * the tag in the comment field of the log entry was added to the.
     * JakeObject in question
	 */
    TAG_ADD,

	/**
	 * the tag in the comment field of the LogEntry was removed from the.
     * JakeObject in question
	 */
    TAG_REMOVE,

    /**
     * This logEntry indicates, that the given
     * userId got invited by this user.
     */
    PROJECTMEMBER_INVITED,

    /**
     * This logEntry indicates, that the given userId
     * was removed by this user.
     */
    PROJECTMEMBER_REMOVED,

    /**
     * This logEntry indicates, that the given userId accepted the invitation.
     * and therefore is now a projectmember
     */
    PROJECTMEMBER_INVITATION_ACCEPTED,

    /**
     * This logEntry indicates, that the given userId was (also)
     * added by this user.
     */
    PROJECTMEMBER_ADDED,

    /**
     * This logEntry indicates, that the trustlevel between this
     * user and the supplied.
     * userId is changed
     */
    PROJECTMEMBER_TRUSTCHANGE,

    /**
	 * the object has been soft locked.
	 */
	OBJECT_LOCK,

	/**
	 * the object has been unlocked.
	 */
	OBJECT_UNLOCK
}
