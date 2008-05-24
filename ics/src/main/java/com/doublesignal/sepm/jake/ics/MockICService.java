package com.doublesignal.sepm.jake.ics;

import java.util.HashSet;
import java.util.Iterator;

import com.doublesignal.sepm.jake.ics.exceptions.NetworkException;
import com.doublesignal.sepm.jake.ics.exceptions.NoSuchUseridException;
import com.doublesignal.sepm.jake.ics.exceptions.NotLoggedInException;
import com.doublesignal.sepm.jake.ics.exceptions.OtherUserOfflineException;
import com.doublesignal.sepm.jake.ics.exceptions.TimeoutException;

public class MockICService implements IICService {
	Boolean loggedinstatus = false;
	String myuserid = null;
	HashSet<IOnlineStatusListener> onlinereceivers = new HashSet<IOnlineStatusListener>();
	HashSet<IObjectReceiveListener> objreceivers = new HashSet<IObjectReceiveListener>();
	HashSet<IMessageReceiveListener> msgreceivers = new HashSet<IMessageReceiveListener>();
	
	/**
	 * userids have a @ like email adresses
	 */
	public Boolean isValidUserid(String userid){
		if(userid.contains("@") )
			return true;
		return false;
	}
	
	public String getFirstname(String userid) throws NoSuchUseridException {
		if(!isValidUserid(userid)) 
			throw new NoSuchUseridException();
		
		if(!userid.contains(".")){
			return "";
		}
		return userid.substring(0, userid.indexOf("."));
	}

	public String getLastname(String userid) throws NoSuchUseridException {
		if(!isValidUserid(userid)) 
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
	public Boolean isLoggedIn(String userid) throws NetworkException,
			NotLoggedInException, TimeoutException {
		if(!isValidUserid(userid)) 
			throw new NoSuchUseridException();
		if(userid.equals(myuserid)) 
			return loggedinstatus;
		if(loggedinstatus == false)
			throw new NotLoggedInException();
		
		/* everyone else not having a s in the name is offline */
		return userid.substring(0,userid.indexOf("@")).contains("s"); 
	}

	public Boolean login(String userid, String pw) throws NetworkException,
			TimeoutException {
		if(!isValidUserid(userid)) 
			throw new NoSuchUseridException();
		if(loggedinstatus == true)
			logout();
		if(userid.equals(pw)){
			myuserid = userid;
			loggedinstatus = true;
			return true;
		}else
			return false;
	}

	public Boolean logout() throws NetworkException, TimeoutException {
		myuserid = null;
		loggedinstatus = false;
		return true;
	}

	public void registerOnlineStatusListener(IOnlineStatusListener osc,
			String userid) throws NoSuchUseridException {
		if(!isValidUserid(userid)) 
			throw new NoSuchUseridException();
		/* about userid: we don't care, because we are tired. */
		onlinereceivers.add(osc);
	}

	public void registerReceiveMessageListener(IMessageReceiveListener rl) {
		msgreceivers.add(rl);
	}

	public void registerReceiveObjectListener(IObjectReceiveListener rl) {
		objreceivers.add(rl);
	}

	public Boolean sendMessage(String to_userid, String content)
			throws NetworkException, NotLoggedInException, TimeoutException,
			NoSuchUseridException, OtherUserOfflineException 
	{
		if(!isValidUserid(to_userid)) 
			throw new NoSuchUseridException();
		if(loggedinstatus == false)
			throw new NotLoggedInException();
		
		if(!to_userid.equals(myuserid)){
			/* autoreply feature */
			for (Iterator<IMessageReceiveListener> it = msgreceivers.iterator(); it.hasNext();) {
				IMessageReceiveListener rl = it.next();
				rl.receivedMessage(to_userid, content + " to you too");
			}
		}else{
			for (Iterator<IMessageReceiveListener> it = msgreceivers.iterator(); it.hasNext();) {
				IMessageReceiveListener rl = it.next();
				rl.receivedMessage(myuserid, content);
			}
		}
		return true;
	}

	public Boolean sendObject(String to_userid, String objectidentifier,
			byte[] content) throws NetworkException, NotLoggedInException,
			TimeoutException, NoSuchUseridException, OtherUserOfflineException {
		if(!isValidUserid(to_userid)) 
			throw new NoSuchUseridException();
		if(loggedinstatus == false)
			throw new NotLoggedInException();
		if(!isLoggedIn(to_userid))
			throw new OtherUserOfflineException();
		if(to_userid.equals(myuserid)){
			for (Iterator<IObjectReceiveListener> it = objreceivers.iterator(); it.hasNext();) {
				IObjectReceiveListener rl = it.next();
				rl.receivedObject(to_userid, objectidentifier, content);
			}
			return true;
		}else{
			/* we can't do anything with the object, so we just accept it. */
			return true;
		}
	}

}
