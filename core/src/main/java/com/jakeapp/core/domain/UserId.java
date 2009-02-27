package com.jakeapp.core.domain;

//import org.hibernate.annotations.Entity;
//import org.hibernate.type.DiscriminatorType;

import org.apache.log4j.Logger;

import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;


/**
 * Identifies a user
 */
public class UserId implements ILogable {
	private static final long serialVersionUID = 3356457614479149943L;

	public UserId() {
		super();
	}

	public UserId(ProtocolType protocolType, String userId) {
		super();
		this.protocolType = protocolType;
		this.userId = userId;
	}

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(UserId.class);

	private String userId;

	private transient ProtocolType protocolType;

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setProtocolType(ProtocolType protocolType) {
		this.protocolType = protocolType;
	}

	public ProtocolType getProtocolType() {
		return this.protocolType;
	}
	
	@Override
	public String toString() {
		return this.protocolType + ":" + getUserId();
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserId userId1 = (UserId) o;

        if (protocolType != userId1.protocolType) return false;
        if (!userId.equals(userId1.userId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userId.hashCode();
        result = 31 * result + protocolType.hashCode();
        return result;
    }
}
