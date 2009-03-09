package com.jakeapp.core.domain;

import org.apache.log4j.Logger;

import java.util.regex.Pattern;
import java.util.regex.Matcher;


/**
 * Identifies a user
 */
public class User implements ILogable, Comparable<User> {
	private static final long serialVersionUID = 3356457614479155943L;
	private static Pattern XMPPpattern = Pattern.compile("(.*)(/(.*))?");

	/*
 dominik.dorn@jabber.fsinf.at/jake
 __thecerial@jabber.fsinf.at/jake
 -myimagination@jabber.fsinf.at
 */
	public User() {
	}

	public User(ProtocolType protocolType, String userId) {
		super();
		this.setProtocolType(protocolType);
		this.setUserId(userId);
	}

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(User.class);

	private String userId;
	private ProtocolType protocolType;

	public final void setUserId(String userId) {
//		log.fatal("calling setUserId with userId: " + userId);

		int idx = userId.indexOf('/');
		if (idx > 0) {
			this.userId = userId.substring(0, idx);
		} else {
			this.userId = userId;
		}

//		log.fatal("userid is now" + this.userId);

	}

	public final String getUserId() {
//		log.fatal("calling getUserId with userid: " + userId);
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((protocolType == null) ? 0 : protocolType.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (protocolType == null) {
			if (other.protocolType != null)
				return false;
		} else if (!protocolType.equals(other.protocolType))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	@Override
	public int compareTo(User o) {
		if (this.equals(o)) return 0;
		else if (o == null) return 1;
		else return this.getUserId().compareTo(o.getUserId());
	}
}