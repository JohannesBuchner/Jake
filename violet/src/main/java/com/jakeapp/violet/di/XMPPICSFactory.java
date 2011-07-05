package com.jakeapp.violet.di;

import java.util.UUID;

import javax.inject.Named;

import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.filetransfer.FailoverCapableFileTransferService;
import com.jakeapp.jake.ics.filetransfer.IFileTransferService;
import com.jakeapp.jake.ics.filetransfer.methods.ITransferMethodFactory;
import com.jakeapp.jake.ics.impl.sockets.filetransfer.SimpleSocketFileTransferFactory;
import com.jakeapp.jake.ics.impl.xmpp.XmppICService;
import com.jakeapp.jake.ics.msgservice.IMsgService;


public class XMPPICSFactory implements ICSFactory {

	@Named("ics global resource name")
	private String resourceName;

	@Named("xmpp namespace")
	private String namespace;

	@Named("ics project resource prefix")
	private String jakeProjectPrefix;

	@Named("use p2p")
	private boolean useSockets;


	public void setUseSockets(boolean useSockets) {
		this.useSockets = useSockets;
	}

	public void setJakeProjectPrefix(String jakeProjectPrefix) {
		this.jakeProjectPrefix = jakeProjectPrefix;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}


	@Override
	public ICService getICS() {
		return new XmppICService(namespace, resourceName);
	}

	@Override
	public ICService getICS(UUID project) {
		return new XmppICService(namespace, jakeProjectPrefix + " "
				+ project.toString());
	}

	@Override
	public IFileTransferService getFileTransferService(IMsgService msg,
			UserId user, ICService ics) {
		IFileTransferService fcfts;
		fcfts = new FailoverCapableFileTransferService();
		if (useSockets) {
			fcfts.addTransferMethod(new SimpleSocketFileTransferFactory(), msg,
					user);
			// TODO: replace by ICE/UDT
		}

		ITransferMethodFactory inbandMethod = ics.getTransferMethodFactory();
		if (inbandMethod != null) {
			fcfts.addTransferMethod(inbandMethod, msg, user);
		}
		return fcfts;
	}
}
