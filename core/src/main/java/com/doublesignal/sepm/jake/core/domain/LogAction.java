package com.doublesignal.sepm.jake.core.domain;

/**
 * A LogAction is intended to show the Status of a certain JakeObject/LogEntry.
 * The available status can be  <code>PROJECT_CREATED</code>,<code>NEW_VERSION</code>,<code>DELETE</code>,
 * <code>TAG_ADD</code> and <code>TAG_REMOVE</code>.
 *
 * @author Dominik, Philipp
 */

public enum LogAction {

	/**
	 * Create a new LogAction Enum Set.
	 *
	 * @param PROJECT_CREATED
	 * @param NEW_VERSION
	 * @param CHANGE
	 * @param DELETE
	 * @param TAG_ADD
	 * @param TAG_REMOVE
	 */

	/**
	 * the project was just created, 1st Logentry ever
	 */
	PROJECT_CREATED,
	
    /**
	 * a new version of a JakeObject was created
	 */
	NEW_VERSION,
	/**
	 * the JakeObject in question was deleted
	 */
	DELETE,
	/**
	 * the tag in the comment field of the Logentry was added to the JakeObject in question
	 */
	TAG_ADD,
	/**
	 * the tag in the comment field of the LogEntry was removed from the JakeObject in question
	 */
	TAG_REMOVE,
	/**
	 * the object has been locked
	 */
	LOCK,
	/**
	 * the object has been unlocked
	 */
	UNLOCK
	


	}
