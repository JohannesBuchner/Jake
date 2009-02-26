package com.jakeapp.jake.ics.impl.xmpp;

import com.jakeapp.jake.ics.UserId;

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

		return this.userId.contains("@") && this.userId.lastIndexOf("@") == this.userId
						.lastIndexOf("@") && this.userId.indexOf("@") > 0 && this.userId
						.indexOf("@") < this.userId.length() - 1;
	}

	public String getHost() {
		return this.userId.substring(this.userId.indexOf("@") + 1);
	}

	public String getUsername() {
		return this.userId.substring(0, this.userId.indexOf("@"));
	}

	/**
	 * compares the UserIds ignoring the resource part
	 * 
	 * @param a
	 * @param b
	 * @return
	 * @param ua
	 * @param ub
	 */
	public static boolean isSameUser(UserId ua, UserId ub) {
		return isSameUser(ua.getUserId(), ub.getUserId());
	}

	public String getUserIdWithOutResource() {
		return stripResource(this.userId);
	}

	private static String stripResource(String userId) {
		if (userId != null && userId.contains("/"))
			return userId.substring(0, userId.lastIndexOf("/"));

		return userId;
	}

	/**
	 * @return null if no resource
	 */
	public String getResource() {
		if (this.userId != null && this.userId.contains("/"))
			return this.userId.substring(this.userId.lastIndexOf("/") + 1);
		else
			return null;
	}

	public String getUserIdWithResource() {
		return this.userId;
	}

	@Override
	public String getUserId() {
		return getUserIdWithResource();
	}

	private static boolean isSameUser(String a, String b) {
		return stripResource(a).equals(stripResource(b));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof UserId)) {
			// added string compare magic:
			return obj.getClass().equals(String.class) && isSameUser(this.userId,
							(String) obj);
		}
		UserId other = (UserId) obj;
		if (this.userId == null) {
			if (other.getUserId() != null)
				return false;
		} else if (!isSameUser(this.userId, other.getUserId()))
			return false;
		return true;
	}

	public boolean isSameUserAs(UserId peer) {
		return isSameUser(this.userId, peer.getUserId());
	}
}
