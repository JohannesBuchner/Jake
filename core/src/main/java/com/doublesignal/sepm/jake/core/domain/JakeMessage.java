package com.doublesignal.sepm.jake.core.domain;

import java.util.Date;


/**
 * A JakeMessage is intended to be sent from one project member to another. It
 * consists of a <code>sender</code>, a <code>recipient</code>, the <code>time</code>
 * when the message was created and a <code>content</code>.
 * @author Dominik, Philipp, Simon
 */

public class JakeMessage {

    private ProjectMember recipient;
    private ProjectMember sender;

    private Date time;
    private String content;

    /**
     * Create a new JakeMessage. The <code>time</code> is set automatically to
     * the current time.
     *
     * @param recipient
     * @param sender
     * @param content the content of the message. 
     */
    public JakeMessage(ProjectMember recipient, ProjectMember sender, String content) {
        this.recipient = recipient;
        this.sender = sender;
        this.content = content;
        this.time = new Date();

    }

    /**
     * Get the sender of the message.
     *
     * @return sender of the message
     */
    public ProjectMember getSender() {
        return sender;
    }

    /**
     * Get the content of the message.
     *
     * @return the content
     */
    public String getContent() {
        return content;
    }

  
    /**
	 * Get the time when the message has been created.
	 * 
	 * @return the time
	 */
	public Date getTime() {
		return time;
	}
	
	/**
	 * Get the recipient of the message
	 * @return recipient
	 */
	public ProjectMember getRecipient() {
		return recipient;
	}

	/**
	 * Tests if two messages are equal.
	 * 
	 * @return <code>true</code> if <code>recipient, sender, time, content</code>
	 * are equal.
	 */
	@Override
	public boolean equals(Object obj) {
        if (obj != null && obj.getClass().equals(this.getClass())) {
        	JakeMessage that = (JakeMessage) obj;
        	return (this.recipient.equals(that.getRecipient()) &&
        	        this.sender.equals(that.getSender()) &&
        	        this.time.equals(that.getTime()) &&
        	        this.content.equals(that.getContent()));
        }
        return false;
    }
}
