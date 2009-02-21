package com.jakeapp.core.domain;

//import org.hibernate.annotations.Entity;
//import org.hibernate.type.DiscriminatorType;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinTable;
import javax.persistence.Transient;

import org.apache.log4j.Logger;


/**
 * Identifies a user
 */
public class UserId implements ILogable {

	public UserId() {
		super();
		// TODO Auto-generated constructor stub
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
