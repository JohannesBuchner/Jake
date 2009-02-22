package com.jakeapp.core.domain;

//import org.hibernate.annotations.Entity;
//import org.hibernate.type.DiscriminatorType;

import org.apache.log4j.Logger;


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
		return userId;
	}

	public void setProtocolType(ProtocolType protocolType) {
		this.protocolType = protocolType;
	}

	public ProtocolType getProtocolType() {
		return protocolType;
	}
}
