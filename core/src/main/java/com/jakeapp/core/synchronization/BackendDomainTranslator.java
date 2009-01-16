package com.jakeapp.core.synchronization;

import com.jakeapp.core.domain.UserId;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;


public class BackendDomainTranslator {

	public static com.jakeapp.jake.ics.UserId userIdBack(UserId u) {
		return new XmppUserId(u.getUserId());
	}
}
