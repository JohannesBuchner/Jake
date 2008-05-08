package com.doublesignal.sepm.jake.core.domain;

import java.util.Date;

/**
 * SEPM SS08
 * Gruppe: 3950
 * Projekt: Jake - a collaborative Environment
 * User: domdorn
 * Date: May 8, 2008
 * Time: 11:04:37 PM
 */
public class JakeMessage {

    {
        /**
         * Set the timestamp to the current date.
         */
        timestamp = new Date();

        /**
         * Note: I really like this Java Trick!
         */
    }

    private ProjectMember recipient;
    private ProjectMember sender;

    private Date timestamp;
    private String content;

    /**
     * Create a new JakeMessage
     *
     * @param recipient
     * @param sender
     * @param content
     */
    public JakeMessage(ProjectMember recipient, ProjectMember sender, String content) {
        this.recipient = recipient;
        this.sender = sender;
        this.content = content;

    }

    /**
     * get the sender of the message
     *
     * @return sender of the message
     */
    public ProjectMember getSender() {
        return sender;
    }

    /**
     * Get the content of the message
     *
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /*
   TODO:
     * send() funktion?
     * delete() funktion?
    */

    public boolean equals(Object o) {
        /* TODO */
        return super.equals(o);
    }
}
