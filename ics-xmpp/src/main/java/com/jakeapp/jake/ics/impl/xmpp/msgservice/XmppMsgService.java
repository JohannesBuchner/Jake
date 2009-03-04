package com.jakeapp.jake.ics.impl.xmpp.msgservice;


import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.exceptions.NoSuchUseridException;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.impl.mock.FriendsOnlyMsgService;
import com.jakeapp.jake.ics.impl.xmpp.XmppConnectionData;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;
import com.jakeapp.jake.ics.msgservice.IMessageReceiveListener;
import com.jakeapp.jake.ics.msgservice.IMsgService;
import com.jakeapp.jake.ics.status.ILoginStateListener;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.Base64;

/**
 * The Xmpp Message Service. There's only one per User.
 */
public class XmppMsgService implements IMsgService, ILoginStateListener {

	private static final Logger log = Logger.getLogger(XmppMsgService.class);

	private XmppConnectionData con;

	private IncomingGenericPacketListener packetListener;

	private ILoginStateListener loginListener = null;

	private boolean initialized = false;

	private void initialize() {
		if (this.initialized || !this.con.getService().getStatusService().isLoggedIn())
			return;

		ProviderManager.getInstance().addExtensionProvider("custom",
						this.con.getNamespace(),
						new GenericPacketProvider(this.con.getNamespace()));

		this.con.getConnection()
						.addPacketListener(this.packetListener, this.packetListener);
		

		this.initialized = true;
	}

	public XmppMsgService(XmppConnectionData connection) {
		this.con = connection;
		this.con.getService().getStatusService().addLoginStateListener(this);
		this.packetListener = new IncomingGenericPacketListener(this.con.getNamespace());
		initialize();
	}

	@Override
	public void registerReceiveMessageListener(
					IMessageReceiveListener receiveListener) {
		initialize();
		this.packetListener.add(receiveListener);
	}

	@Override public void registerLoginStateListener(ILoginStateListener loginListener) {
		this.loginListener = loginListener;
	}

	/*
	 * note: you can only send packets that are a acceptable XML content
	 * Base64-encoding might be a good idea...
	 */
	@Override
	public Boolean sendMessage(UserId to_userid, String content)
					throws NotLoggedInException, NoSuchUseridException {

		if (!this.con.getService().getStatusService().isLoggedIn())
			throw new NotLoggedInException();
		if (!new XmppUserId(to_userid).isOfCorrectUseridFormat())
			throw new NoSuchUseridException();

		String safecontent = Base64.encodeBytes(content.getBytes(), Base64.GZIP);
		Message m = new Message(to_userid.getUserId());
		m.addExtension(new GenericPacketExtension(this.con.getNamespace(), safecontent));
		log.info("Sending message to " + to_userid.getUserId());
		log.debug("Content:" + content);
		this.con.getConnection().sendPacket(m);

		return true;
	}

	@Override
	public IMsgService getFriendMsgService() {
		return new FriendsOnlyMsgService(this.con.getService().getUsersService(), this);
	}

	@Override
	public void unRegisterReceiveMessageListener(
					IMessageReceiveListener receiveListener) {
		this.packetListener.remove(receiveListener);
	}

	@Override public void unRegisterLoginStateListener(ILoginStateListener loginListener) {
		this.loginListener = null;
	}

	@Override
	public void connectionStateChanged(ConnectionState le, Exception ex) {
		if (ConnectionState.LOGGED_IN == le) {
			initialize();
		} else if (ConnectionState.LOGGED_OUT == le) {
			this.initialized = false;
		}

		// forward event to gui (only master has a reference)
		if(loginListener != null) {
			loginListener.connectionStateChanged(le, null);
		}
	}
}
