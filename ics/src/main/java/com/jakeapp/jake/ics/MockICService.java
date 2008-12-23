package com.jakeapp.jake.ics;

import java.util.HashSet;

import org.apache.log4j.Logger;

import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.exceptions.NoSuchUseridException;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.exceptions.OtherUserOfflineException;
import com.jakeapp.jake.ics.exceptions.TimeoutException;

public class MockICService implements IICService {
	Logger log = Logger.getLogger(MockICService.class);
	/**
	 * are we online?
	 */
	Boolean loggedinstatus = false;
	/**
	 * the userid is stored between login and logout, then cleared again
	 */
	String myuserid = null;

	HashSet<IOnlineStatusListener> onlinereceivers = new HashSet<IOnlineStatusListener>();
	HashSet<IObjectReceiveListener> objreceivers = new HashSet<IObjectReceiveListener>();
	HashSet<IMessageReceiveListener> msgreceivers = new HashSet<IMessageReceiveListener>();
	
	/**
	 * userids have a @ like email adresses
	 */
	public boolean isOfCorrectUseridFormat(String userid){
		if(userid.contains("@") && userid.lastIndexOf("@") == userid.lastIndexOf("@")
			&& userid.indexOf("@") > 0 && userid.indexOf("@") < userid.length()-1 )
			return true;
		return false;
	}
	
	public String getFirstname(String userid) throws NoSuchUseridException {
		if(!isOfCorrectUseridFormat(userid)) 
			throw new NoSuchUseridException();
		
		if(!userid.contains(".")){
			return "";
		}
		return userid.substring(0, userid.indexOf("."));
	}

	public String getLastname(String userid) throws NoSuchUseridException {
		if(!isOfCorrectUseridFormat(userid)) 
			throw new NoSuchUseridException();
		
		if(!userid.contains(".")){
			return "";
		}
		return userid.substring(userid.indexOf(".")+1, userid.indexOf("@"));
	}
	
	public Boolean isLoggedIn() {
		return loggedinstatus;
	}
	/**
	 * users having a s in the userid before the \@ are online
	 */
	public Boolean isLoggedIn(String userid) throws NoSuchUseridException, 
			NetworkException, NotLoggedInException, TimeoutException {
		if(!isOfCorrectUseridFormat(userid)) 
			throw new NoSuchUseridException();
		if(userid.equals(myuserid)) 
			return loggedinstatus;
		if(!loggedinstatus)
			throw new NotLoggedInException();
		
		/* everyone else not having a s in the name is offline */
		return userid.substring(0,userid.indexOf("@")).contains("s"); 
	}
	/**
	 * Login is successful, if userid == pw
	 */
	public Boolean login(String userid, String pw) throws NetworkException,
			TimeoutException {
		if(!isOfCorrectUseridFormat(userid)) 
			throw new NoSuchUseridException();
		if(loggedinstatus)
			logout();
		if(userid.equals(pw)){
			myuserid = userid;
			loggedinstatus = true;
			return true;
		}else
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
			String userid) throws NoSuchUseridException {
		if(!isOfCorrectUseridFormat(userid)) 
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
	public Boolean sendMessage(String to_userid, String content)
			throws NetworkException, NotLoggedInException, TimeoutException,
			NoSuchUseridException, OtherUserOfflineException 
	{
		log.info("Sending message to "+ to_userid +" with content \"" + content + "\"");
		if(!isOfCorrectUseridFormat(to_userid)) {
			log.warn("Couldn't send message: Recipient invalid");
			throw new NoSuchUseridException();
		}

		if(!loggedinstatus) {
			log.warn("Couldn't send message: Not logged in");
			throw new NotLoggedInException();
		}
		
		if(!to_userid.equals(myuserid)){
			/* autoreply feature */
			for (IMessageReceiveListener rl : msgreceivers) {
				log.info("Propagating message to a listener...");
				rl.receivedMessage(to_userid, content + " to you too");
			}
		}else{
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
	public Boolean sendObject(String to_userid, String objectidentifier,
			byte[] content) throws NetworkException, NotLoggedInException,
			TimeoutException, NoSuchUseridException, OtherUserOfflineException {
		if(!isOfCorrectUseridFormat(to_userid)) 
			throw new NoSuchUseridException();
		if(!loggedinstatus)
			throw new NotLoggedInException();
		if(!isLoggedIn(to_userid))
			throw new OtherUserOfflineException();
		if(to_userid.equals(myuserid)){
            for (IObjectReceiveListener rl : objreceivers) {
                rl.receivedObject(to_userid, objectidentifier, content);
            }
			return true;
		}else{
			/* we can't do anything with the object, so we just accept it. */
			return true;
		}
	}

	public String getUserid() throws NotLoggedInException {
		if(!isLoggedIn())
			throw new NotLoggedInException();
		
		return myuserid;
	}

	public String getServiceName() {
		return "Mock";
	}

}
