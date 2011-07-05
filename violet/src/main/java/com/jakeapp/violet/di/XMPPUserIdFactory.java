package com.jakeapp.violet.di;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;

/**
 * TODO: move to ICS-XMPP or GUI
 * 
 * @author user
 */
public class XMPPUserIdFactory implements IUserIdFactory {

	@Override
	public UserId get(String user) {
		return new XmppUserId(user);
	}
}
