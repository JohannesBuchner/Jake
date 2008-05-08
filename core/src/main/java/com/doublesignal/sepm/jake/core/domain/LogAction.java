package com.doublesignal.sepm.jake.core.domain;

/**
 * SEPM SS08
 * Gruppe: 3950
 * Projekt: Jake - a collaborative Environment
 * User: domdorn
 * Date: May 8, 2008
 * Time: 11:05:41 PM
 */
public enum LogAction {

    /**
     * the project was just created, 1st Logentry ever
     */
    PROJECTCREATED,
    /**
     * a new JakeObject was created
     */
    NEW,
    /**
     * the JakeObject in question was modified
     */
    CHANGE,

    /**
     * the JakeObject in question was deleted
     */
    DELETE,
    /**
     * the tag in the comment field of the Logentry was added to the JakeObject in question
     */
    TAGADD,
    /**
     * the tag in the comment field of the LogEntry was removed from the JakeObject in question
     */
    TAGREMOVE


}
