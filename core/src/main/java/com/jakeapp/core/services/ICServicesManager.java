package com.jakeapp.core.services;

import java.util.Map;

import org.apache.log4j.Logger;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProtocolType;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.services.exceptions.ProtocolNotSupportedException;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.impl.xmpp.XmppICService;


public class ICServicesManager {

	private static Logger log = Logger.getLogger(ICServicesManager.class);

	private Map<Project, ICService> services;

	public ICService getICService(Project p) throws ProtocolNotSupportedException {
		ServiceCredentials cred = p.getCredentials();
		ICService ics = null;
		if (!this.services.containsKey(p)) {
			if (cred.getProtocol().equals(ProtocolType.XMPP)) {
				log.debug("Creating new XMPPICService for userId " + cred.getUserId());
				ics = new XmppICService(XMPPMsgService.namespace, p.getName());
			} else {
				log.warn("Currently unsupported protocol given");
				throw new ProtocolNotSupportedException();
			}
			if(ics != null)
				this.services.put(p, ics);
		}
		return this.services.get(p);
	}
}
