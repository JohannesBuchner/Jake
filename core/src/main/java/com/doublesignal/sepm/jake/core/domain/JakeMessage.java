package com.doublesignal.sepm.jake.core.domain;

import java.util.Date;
import java.io.Serializable;

/**
 * SEPM SS08
 * Gruppe: 3950
 * Projekt: Jake - a collaborative Environment
 * User: domdorn
 * Date: May 8, 2008
 * Time: 11:04:37 PM
 */
public class JakeMessage implements Serializable {

    {
        /**
         * Set the time to the current date.
         */
        time = new Date();

        /**
         * Note: I really like this Java Trick!
         */
    }

    private ProjectMember recipient;
    private ProjectMember sender;

    private Date time;
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

  
    /**
	 * Get the time of the message
	 * 
	 * @return the time
	 */
	public Date getTime() {
		return time;
	}

	@Override
	public boolean equals(Object o) {
        /* TODO */
        return super.equals(o);
    }
}
