package com.jakeapp.core.services;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProtocolType;
import com.jakeapp.core.domain.Account;
import com.jakeapp.core.domain.User;
import com.jakeapp.core.services.exceptions.ProtocolNotSupportedException;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.filetransfer.FailoverCapableFileTransferService;
import com.jakeapp.jake.ics.filetransfer.IFileTransferService;
import com.jakeapp.jake.ics.filetransfer.methods.ITransferMethodFactory;
import com.jakeapp.jake.ics.impl.sockets.filetransfer.SimpleSocketFileTransferFactory;
import com.jakeapp.jake.ics.impl.xmpp.XmppICService;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;
import com.jakeapp.jake.ics.msgservice.IMsgService;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * The <code>FailoverICSManager</code> tries Socket-Transfers first, then XMPP-Inband-Transfers.
 * @see ICSManager
 */
public class FailoverICSManager implements ICSManager {

	private static final boolean SOCKETS_ENABLED = true;

	private static Logger log = Logger.getLogger(FailoverICSManager.class);

	private Map<String, ICService> services = new HashMap<String, ICService>();

	/**
	 * Map<Project.getProjectId(), IFileTransferService>
	 */
	private Map<String, IFileTransferService> transfer = new HashMap<String, IFileTransferService>();

	private Map<String, ICService> activeServices = new HashMap<String, ICService>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasTransferService(Project p) {
		return this.transfer.containsKey(p.getProjectId());
	}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IFileTransferService getTransferService(Project p)
			throws NotLoggedInException {
		ICService ics = getICService(p);
		IMsgService msg = ics.getMsgService();
		IFileTransferService fcfts;

		if (!this.transfer.containsKey(p.getProjectId())) {
			fcfts = createTransferServices(this.getBackendUserId(p), ics, msg);
			this.transfer.put(p.getProjectId(), fcfts);
		}

		return this.transfer.get(p.getProjectId());
	}

	/**
	 * This method creates the current supported <code>IFileTransferService</code>s in the correct order.
	 *
	 * @param user the <code>UserId</code> for which the <code>IFileTransferService</code> should work.
	 * @param ics the <code>ICService</code> on which this <code>IFileTransferService</code> should work on.
	 * @param msg the <code>IMsgService</code> used for negotiation, see:
	 * {@link IFileTransferService#addTransferMethod(ITransferMethodFactory, IMsgService, UserId)}
	 * @return a <code>FailoverCapableFileTransferService</code
	 * @throws NotLoggedInException if the <code>ICService</code> is not logged in.
	 */
	private IFileTransferService createTransferServices(
			UserId user, ICService ics, IMsgService msg)
			throws NotLoggedInException {
		IFileTransferService fcfts;
		fcfts = new FailoverCapableFileTransferService();
		if (SOCKETS_ENABLED)
			fcfts.addTransferMethod(new SimpleSocketFileTransferFactory(), msg, user);

		ITransferMethodFactory inbandMethod = ics.getTransferMethodFactory();
		if (inbandMethod == null) {
			log.fatal("inband method not provided");
		} else {
			fcfts.addTransferMethod(inbandMethod, msg, user);
		}
		return fcfts;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserId getBackendUserId(Project p, User u) {
		if (p.getCredentials().getProtocol().equals(ProtocolType.XMPP)) {
			return new XmppUserId(u.getUserId() + "/" + p.getProjectId());
		} else {
			log.fatal("Currently unsupported protocol given");
			throw new IllegalArgumentException(new ProtocolNotSupportedException());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserId getBackendUserId(User u) {
		return new XmppUserId(u.getUserId() + "/Jake"); // todo: this is not network unspecific!
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User getFrontendUserId(Project p, com.jakeapp.jake.ics.UserId u) {
		if (p.getMessageService().getProtocolType().equals(ProtocolType.XMPP)) {
			return new User(ProtocolType.XMPP, u.getUserId());
		} else {
			log.fatal("Currently unsupported protocol given");
			throw new IllegalArgumentException(new ProtocolNotSupportedException());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserId getBackendUserId(Project p) {
		return this.getBackendUserId(p, p.getUserId());
	}


	private ICService createICService(Project p) {
		log.debug("creating ICS");
		Account cred = p.getCredentials();
		ICService ics;

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
