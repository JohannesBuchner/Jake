package com.jakeapp.jake.ics.impl.mock;

import java.util.HashSet;

import org.apache.log4j.Logger;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.exceptions.NoSuchUseridException;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.exceptions.OtherUserOfflineException;
import com.jakeapp.jake.ics.exceptions.TimeoutException;
import com.jakeapp.jake.ics.msgservice.IMessageReceiveListener;
import com.jakeapp.jake.ics.msgservice.IMsgService;
import com.jakeapp.jake.ics.msgservice.IObjectReceiveListener;
import com.jakeapp.jake.ics.status.IOnlineStatusListener;
import com.jakeapp.jake.ics.status.IStatusService;

/**
 * This implements both the IMsgService and the IStatusService, usually you will
 * want to share common data using the constructors
 * 
 * @author johannes
 */
public class MockMsgAndStatusService implements IMsgService, IStatusService {

	private static Logger log = Logger.getLogger(MockMsgAndStatusService.class);

	/**
	 * are we online?
	 */
	private Boolean loggedinstatus = false;

	/**
	 * the userid is stored between login and logout, then cleared again
	 */
	private UserId myuserid = null;

	private HashSet<IOnlineStatusListener> onlinereceivers = new HashSet<IOnlineStatusListener>();

	private HashSet<IObjectReceiveListener> objreceivers = new HashSet<IObjectReceiveListener>();

	private HashSet<IMessageReceiveListener> msgreceivers = new HashSet<IMessageReceiveListener>();

	public String getFirstname(UserId userid) throws NoSuchUseridException {
		if (!new MockUserId(userid).isOfCorrectUseridFormat())
			throw new NoSuchUseridException();

		if (!userid.getUserId().contains(".")) {
			return "";
		}
		return userid.getUserId().substring(0, userid.getUserId().indexOf("."));
	}

	public String getLastname(UserId userid) throws NoSuchUseridException {
		if (!new MockUserId(userid).isOfCorrectUseridFormat())
			throw new NoSuchUseridException();

		if (!userid.getUserId().contains(".")) {
			return "";
		}
		return userid.getUserId().substring(
				userid.getUserId().indexOf(".") + 1,
				userid.getUserId().indexOf("@"));
	}

	public Boolean isLoggedIn() {
		return loggedinstatus;
	}

	/**
	 * users having a s in the userid before the \@ are online
	 */
	public Boolean isLoggedIn(UserId to) throws NoSuchUseridException,
			NetworkException, NotLoggedInException, TimeoutException {
		if (!new MockUserId(to).isOfCorrectUseridFormat())
			throw new NoSuchUseridException();
		if (to.equals(myuserid))
			return loggedinstatus;
		if (!loggedinstatus)
			throw new NotLoggedInException();

		/* everyone else not having a s in the name is offline */
		return to.getUserId().substring(0, to.getUserId().indexOf("@"))
				.contains("s");
	}

	/**
	 * Login is successful, if userid == pw
	 */
	public Boolean login(UserId userid, String pw) throws NetworkException,
			TimeoutException {
		if (!new MockUserId(userid).isOfCorrectUseridFormat())
			throw new NoSuchUseridException();
		if (loggedinstatus)
			logout();
		if (userid.equals(pw)) {
			myuserid = userid;
			loggedinstatus = true;
			return true;
		} else
			return false;
	}

	public void logout() {
		myuserid = null;
		loggedinstatus = false;
	}

	/**
	 * noone comes or goes offline, so this is futile
	 */
	public void registerOnlineStatusListener(IOnlineStatusListener osc,
			UserId userid) throws NoSuchUseridException {
		if (!new MockUserId(userid).isOfCorrectUseridFormat())
			throw new NoSuchUseridException();
		/* about userid: we don't care, because we are tired. */
		onlinereceivers.add(osc);
	}

	public void registerReceiveMessageListener(IMessageReceiveListener rl) {
		log.info("Message receive listener registered");
		msgreceivers.add(rl);
	}

	public void registerReceiveObjectListener(IObjectReceiveListener rl) {
		log.info("Object receive listener registered...");
		objreceivers.add(rl);
	}

	/**
	 * If you send a message to someone, a reply is generated.
	 */
	public Boolean sendMessage(UserId to_userid, String content)
			throws NetworkException, NotLoggedInException, TimeoutException,
			NoSuchUseridException, OtherUserOfflineException {
		log.info("Sending message to " + to_userid + " with content \""
				+ content + "\"");
		UserId to = new MockUserId(to_userid);
		if (!to.isOfCorrectUseridFormat()) {
			log.warn("Couldn't send message: Recipient invalid");
			throw new NoSuchUseridException();
		}

		if (!loggedinstatus) {
			log.warn("Couldn't send message: Not logged in");
			throw new NotLoggedInException();
		}

		if (!to_userid.equals(myuserid)) {
			/* autoreply feature */
			for (IMessageReceiveListener rl : msgreceivers) {
				log.info("Propagating message to a listener...");
				rl.receivedMessage(to, content + " to you too");
			}
		} else {
			for (IMessageReceiveListener rl : msgreceivers) {
				log.info("Propagating message to a listener...");
				rl.receivedMessage(myuserid, content);
			}
		}
		return true;
	}

	/**
	 * objects sent to other online users are accepted, but ignored.
	 */
	public Boolean sendObject(UserId to, String objectidentifier, byte[] content)
			throws NetworkException, NotLoggedInException, TimeoutException,
			NoSuchUseridException, OtherUserOfflineException {
		if (!new MockUserId(to).isOfCorrectUseridFormat())
			throw new NoSuchUseridException();
		if (!loggedinstatus)
			throw new NotLoggedInException();
		if (!isLoggedIn(to))
			throw new OtherUserOfflineException();
		if (to.equals(myuserid)) {
			for (IObjectReceiveListener rl : objreceivers) {
				rl.receivedObject(to, objectidentifier, content);
			}
			return true;
		} else {
			/* we can't do anything with the object, so we just accept it. */
			return true;
		}
	}

	public UserId getUserid() throws NotLoggedInException {
		if (!isLoggedIn())
			throw new NotLoggedInException();

		return myuserid;
	}

	public String getServiceName() {
		return "Mock";
	}

	public UserId getUserId(String userid) {
		UserId ui = new MockUserId(userid);
		if (ui.isOfCorrectUseridFormat())
			return ui;
		else
			return null;
	}
}
