package com.jakeapp.jake.ics.impl.xmpp.users;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.exceptions.NoSuchUseridException;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.impl.xmpp.XmppConnectionData;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;
import com.jakeapp.jake.ics.status.IOnlineStatusListener;
import com.jakeapp.jake.ics.users.IUsersService;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.packet.DiscoverInfo;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


public class XmppUsersService implements IUsersService {

	public static final Logger log = Logger.getLogger(XmppUsersService.class);

	public XmppConnectionData con;

	public Set<IOnlineStatusListener> onlinereceivers = new HashSet<IOnlineStatusListener>();

	public XmppUsersService(XmppConnectionData connection) {
		this.con = connection;
	}

	@Override
	public void registerOnlineStatusListener(IOnlineStatusListener osl) {
		this.onlinereceivers.add(osl);
	}

	public void addUser(UserId user, String name) throws NoSuchUseridException,
			  NotLoggedInException, IOException {
		assertLoggedIn();
		RosterEntry re = getGroup().getEntry(getXmppId(user));
		if (re == null) {
			String[] groups = {this.con.getGroupname()};
			try {
				getRoster().createEntry(getXmppId(user), name, groups);
			} catch (XMPPException e) {
				throw new IOException(e);
			}
		}
	}

	private String getXmppId(UserId user) throws NoSuchUseridException {
		XmppUserId xu = new XmppUserId(user);
		if (!xu.isOfCorrectUseridFormat())
			throw new NoSuchUseridException();
		return xu.getUserIdWithOutResource();
	}

	private void assertLoggedIn() throws NotLoggedInException {
		if (!this.con.getService().getStatusService().isLoggedIn()) {
			log.warn("user not logged in: " + this.con.getService().getStatusService().getUserid());
			throw new NotLoggedInException();
		}
	}

	public void removeUser(UserId user) throws NotLoggedInException,
			  NoSuchUseridException, IOException {
		assertLoggedIn();
		RosterEntry re = getGroup().getEntry(getXmppId(user));

		if (re == null)
			return; // we silently ignore double deletes

		try {
			getGroup().removeEntry(re);
		} catch (XMPPException e) {
			throw new IOException(e);
		}
	}

	public Roster getRoster() throws NotLoggedInException {
		assertLoggedIn();
		return this.con.getConnection().getRoster();
	}

	public String getNickName(UserId user) throws NotLoggedInException {
		assertLoggedIn();

		RosterEntry entry = getGroup().getEntry(user.getUserId());
		if(entry == null) {
			return "";
		}else{
			return entry.getName();
		}
	}

	public Iterable<UserId> getUsers() throws NotLoggedInException {
		assertLoggedIn();

		Set<UserId> users = new HashSet<UserId>();

		for (RosterEntry re : getGroup().getEntries()) {
			users.add(new XmppUserId(re.getUser()));
		}
		return users;
	}
	
	public Iterable<UserId> getAllUsers() throws NotLoggedInException {
		assertLoggedIn();

		Set<UserId> users = new HashSet<UserId>();

		for (RosterEntry re : getRoster().getEntries()) {
			users.add(new XmppUserId(re.getUser()));
		}
		return users;
	}


	public RosterGroup getGroup() throws NotLoggedInException {
		assertLoggedIn();
		log.debug("Using group " + this.con.getGroupname());
		RosterGroup rg = getRoster().getGroup(this.con.getGroupname());
		if (rg == null) {
			rg = getRoster().createGroup(this.con.getGroupname());
		}
		return rg;
	}

	private boolean isFriend(String xmppid) throws NotLoggedInException {
		assertLoggedIn();
		return getGroup().getEntry(xmppid) != null;
	}

	public class DiscoveryThread implements Runnable {

		private final Logger log = Logger.getLogger(DiscoveryThread.class);

		String xmppid;

		DiscoveryThread(String who) {
			super();
			this.xmppid = who;
		}

		public void run() {
			Thread.currentThread().setName("Discovering " + this.xmppid);
			this.log.trace(Thread.currentThread() + " starting ... ");

			this.log.trace("trying to discover capabilities for user ...");
			int tries = 2;
			while (tries > 0
					  && XmppUsersService.this.con.getService()
					  .getStatusService().isLoggedIn())
				try {
					if (isCapable(this.xmppid)) {
						if (isFriend(this.xmppid)) {
							this.log.info("It is a friend!");
							notifyAboutPresenceChange(this.xmppid);
						}
					}
					break;
				} catch (IOException e) {
					// fixme: why is this fired that often?
					this.log.debug("discovering capabilities failed!" + e.getMessage());
					tries--;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						// not important, don't care, best effort
					}
				} catch (NotLoggedInException e) {
					this.log.warn("We got logged out somehow", e);
				}
			this.log.debug(Thread.currentThread() + " done");
		}
	}

	public void notifyAboutPresenceChange(String xmppid) {
		for (IOnlineStatusListener osl : this.onlinereceivers) {
			osl.onlineStatusChanged(new XmppUserId(xmppid));
		}
	}

	private boolean isCapable(String xmppid) throws IOException {
		ServiceDiscoveryManager discoManager = ServiceDiscoveryManager
				  .getInstanceFor(this.con.getConnection());
		log.trace("discovering user " + xmppid);
		DiscoverInfo discoInfo;
		try {
			discoInfo = discoManager.discoverInfo(xmppid);
		} catch (XMPPException e) {
			log.debug("Something weird happened (mostly just no response)" + e.getMessage());
			throw new IOException(e);
		}
		log.trace("discovery returned: " + discoInfo.getExtensions().size()
				  + " features");
		for (PacketExtension i : discoInfo.getExtensions()) {
			log.debug("discovery returned: namespace: " + i.getNamespace());
		}

		if (discoInfo.containsFeature(this.con.getNamespace())) {
			log.trace("user came online with our feature");
			return true;
		} else {
			log.trace("user came online withOUT our feature");
			return false;
		}
	}

	public boolean isCapable(UserId userid) throws IOException,
			  NotLoggedInException, NoSuchUseridException {
		assertLoggedIn();
		return isCapable(getXmppId(userid));
	}

	public void requestOnlineNotification(UserId userid)
			  throws NotLoggedInException {
		Presence presence = getRoster().getPresence(userid.getUserId());
		String xmppid = userid.getUserId();
		log.trace("presenceChanged: " + xmppid + " - " + presence);
		if (presence.isAvailable()) {
			new Thread(new DiscoveryThread(xmppid)).start();
		} else {
			notifyAboutPresenceChange(xmppid);
		}
		new Thread(new DiscoveryThread(xmppid)).start();
	}

	@Override
	public boolean isFriend(UserId userid) throws NotLoggedInException,
			  NoSuchUseridException {
		assertLoggedIn();
		return getGroup().contains(getXmppId(userid));
	}
}
