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

}
