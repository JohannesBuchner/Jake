package com.jakeapp.jake.ics.impl.xmpp;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.exceptions.NoSuchUseridException;
import com.jakeapp.jake.ics.exceptions.OtherUserOfflineException;
import com.jakeapp.jake.ics.impl.mock.MockUserId;

/**
 * Identifies a user within the XMPP network
 * 
 * @author johannes
 */
public class XmppUserId extends UserId {

	public XmppUserId(String userId) {
		super(userId);
	}

	public XmppUserId(UserId userId) {
		super(userId.getUserId());
	}

	@Override
	public boolean isOfCorrectUseridFormat() {
		// TODO: this might not be the full wisdom ...

		if (this.userId.contains("@")
				&& this.userId.lastIndexOf("@") == this.userId.lastIndexOf("@")
				&& this.userId.indexOf("@") > 0
				&& this.userId.indexOf("@") < this.userId.length() - 1)
			return true;
		return false;
	}

	public String getHost() {
		return this.userId.substring(this.userId.indexOf("@") + 1);
	}

	public String getUsername() {
		return this.userId.substring(0, this.userId.indexOf("@"));
	}

	/**
	 * compares the UserIds ignoring the resource part
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean isSameUser(UserId ua, UserId ub) {
		return isSameUser(ua.getUserId(), ub.getUserId());
	}
	
	private static boolean isSameUser(String a, String b) {
		if(a.contains("/"))
			a = a.substring(0,a.lastIndexOf("/"));
		if(b.contains("/"))
			b = b.substring(0,b.lastIndexOf("/"));
		return a.equals(b);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass()) {
			// added string compare magic:
			if(obj.getClass().equals(String.class) && isSameUser(userId, (String)obj))
				return true;
			return false;
		}
		UserId other = (UserId) obj;
		if (userId == null) {
			if (other.getUserId() != null)
				return false;
		} else if (!isSameUser(userId,other.getUserId()))
			return false;
		return true;
	}
}
