package com.jakeapp.core.services;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProtocolType;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.services.exceptions.ProtocolNotSupportedException;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.filetransfer.FailoverCapableFileTransferService;
import com.jakeapp.jake.ics.filetransfer.methods.ITransferMethodFactory;
import com.jakeapp.jake.ics.impl.sockets.filetransfer.SimpleSocketFileTransferFactory;
import com.jakeapp.jake.ics.impl.xmpp.XmppICService;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;
import com.jakeapp.jake.ics.msgservice.IMsgService;


public class FailoverICSManager implements ICSManager {

	private static final boolean SOCKETS_ENABLED = false;

	private static Logger log = Logger.getLogger(FailoverICSManager.class);

	private Map<String, ICService> services = new HashMap<String, ICService>();

	private Map<String, FailoverCapableFileTransferService> transfer = new HashMap<String, FailoverCapableFileTransferService>();

	private int SOCKET_TIMEOUT_SECONDS;

	private Map<String, ICService> activeServices = new HashMap<String, ICService>();
	
	@Override
	public ICService getICService(Project p) {
		ICService ics = null;

		if (this.services.containsKey(p.getProjectId()))
			ics = this.services.get(p.getProjectId());
		else {
			ics = this.createICService(p);
			this.services.put(p.getProjectId(), ics);
		}

		return ics;
	}

	@Override
	public FailoverCapableFileTransferService getTransferService(Project p)
			throws NotLoggedInException {
		ICService ics = getICService(p);
		IMsgService msg = ics.getMsgService();
		FailoverCapableFileTransferService fcfts = null;

		if (this.transfer.containsKey(p.getProjectId())) {
			fcfts = this.transfer.get(p.getProjectId());
		} else {
			fcfts = createTransferService(this.getBackendUserId(p), ics, msg);

			this.transfer.put(p.getProjectId(), fcfts);
		}

		return fcfts;
	}

	private FailoverCapableFileTransferService createTransferService(
			com.jakeapp.jake.ics.UserId user, ICService ics, IMsgService msg)
			throws NotLoggedInException {
		FailoverCapableFileTransferService fcfts;
		fcfts = new FailoverCapableFileTransferService();
		if (SOCKETS_ENABLED)
			fcfts.addTransferMethod(new SimpleSocketFileTransferFactory(
					SOCKET_TIMEOUT_SECONDS), msg, user);

		ITransferMethodFactory inbandMethod = ics.getTransferMethodFactory();
		if (inbandMethod == null) {
			log.fatal("inband method not provided");
		} else {
			fcfts.addTransferMethod(inbandMethod, msg, user);
		}
		return fcfts;
	}

	@Override
	public com.jakeapp.jake.ics.UserId getBackendUserId(Project p, UserId u) {
		if (p.getCredentials().getProtocol().equals(ProtocolType.XMPP)) {
			log.debug("Creating new XMPPICService for userId "
					+ p.getCredentials().getUserId());
			return new XmppUserId(u.getUserId() + "/" + p.getProjectId());
		} else {
			log.fatal("Currently unsupported protocol given");
			throw new IllegalArgumentException(new ProtocolNotSupportedException());
		}
	}
	
	@Override
	public com.jakeapp.jake.ics.UserId getBackendUserId(Project p) {
		return this.getBackendUserId(p, p.getUserId());
	}

	private ICService createICService(Project p) {
		log.debug("creating ICS");
		ServiceCredentials cred = p.getCredentials();
		ICService ics = null;

		if (p.getCredentials().getProtocol().equals(ProtocolType.XMPP)) {
			log.debug("Creating new XMPPICService for cred:  " + cred);
			ics = new XmppICService(XMPPMsgService.namespace, p.getName());
		} else {
			log.fatal("Currently unsupported protocol given");
			throw new IllegalArgumentException(new ProtocolNotSupportedException());
		}
		return ics;
	}
	
	
	public Collection<ICService> getAll() {
		return this.services.values();
	}
}
