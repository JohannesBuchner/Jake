package com.jakeapp.jake.ics.impl.xmpp.msgservice;


import org.apache.log4j.Logger;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.provider.ProviderManager;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.exceptions.NoSuchUseridException;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.exceptions.OtherUserOfflineException;
import com.jakeapp.jake.ics.exceptions.TimeoutException;
import com.jakeapp.jake.ics.impl.xmpp.XmppConnectionData;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;
import com.jakeapp.jake.ics.msgservice.IMessageReceiveListener;
import com.jakeapp.jake.ics.msgservice.IMsgService;


public class XmppMsgService implements IMsgService {

	private static final Logger log = Logger.getLogger(XmppMsgService.class);

	private XmppConnectionData con;

	private IncomingGenericPacketListener packetListener;

	private void initialize() throws NotLoggedInException {
		if (!this.con.getService().getStatusService().isLoggedIn())
			throw new NotLoggedInException();

		ProviderManager.getInstance().addExtensionProvider("custom",
				this.con.getNamespace(),
				new GenericPacketProvider(this.con.getNamespace()));

		this.packetListener = new IncomingGenericPacketListener(this.con
				.getNamespace());
		this.con.getConnection().addPacketListener(this.packetListener,
				this.packetListener);
	}

	public XmppMsgService(XmppConnectionData connection) {
		this.con = connection;
	}

	@Override
	public void registerReceiveMessageListener(
			IMessageReceiveListener receiveListener)
			throws NotLoggedInException {
		if (this.packetListener == null)
			initialize();
		this.packetListener.add(receiveListener);
	}

	@Override
	public Boolean sendMessage(UserId to_userid, String content)
			throws NotLoggedInException, NoSuchUseridException {

		if (!this.con.getService().getStatusService().isLoggedIn())
			throw new NotLoggedInException();
		if (!new XmppUserId(to_userid).isOfCorrectUseridFormat())
			throw new NoSuchUseridException();

		Message m = new Message(to_userid.getUserId());
		m.addExtension(new GenericPacketExtension(this.con.getNamespace(),
				content));
		log.info("Sending message to " + to_userid.getUserId());
		log.debug("Content:" + content);
		this.con.getConnection().sendPacket(m);

		return true;
	}
}
