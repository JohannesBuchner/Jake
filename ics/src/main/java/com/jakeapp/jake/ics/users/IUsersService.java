package com.jakeapp.jake.ics.users;

import java.io.IOException;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.exceptions.NoSuchUseridException;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.status.IOnlineStatusListener;

/**
 * provides abstraction to our friends-group 
 * 
 * @author johannes
 */
public interface IUsersService {

	/**
	 * Registers a callback for the event that the userid goes online or offline
	 * 
	 * @param onlineStatusListener     object to be called
	 * @throws NoSuchUseridException    if the user does not exist
	 */
	public void registerOnlineStatusListener(IOnlineStatusListener onlineStatusListener);

	/**
	 * idempotent add to communications group function
	 * 
	 * @param user
	 * @param name
	 * @throws NoSuchUseridException
	 * @throws NotLoggedInException
	 * @throws IOException
	 */
	public void addUser(UserId user, String name)
			throws NoSuchUseridException, NotLoggedInException, IOException;

	/**
	 * idempotent remove from communication group function
	 * 
	 * @param user
	 * @throws NotLoggedInException
	 * @throws NoSuchUseridException
	 * @throws IOException
	 */
	public void removeUser(UserId user) throws NotLoggedInException,
			NoSuchUseridException, IOException;

	/**
	 * 
	 * @return all user we could talk to, logged in or not
	 * @throws NotLoggedInException
	 */
	public Iterable<UserId> getUsers() throws NotLoggedInException;

	/**
	 * Is this special someone in our group?
	 * 
	 * @param xmppid
	 * @return
	 * @throws NotLoggedInException
	 * @throws NoSuchUseridException 
	 */
	public boolean isFriend(UserId xmppid) throws NotLoggedInException, NoSuchUseridException;

	/**
	 * blocking request for a online-status notification You should prefer
	 * requestOnlineNotification
	 * 
	 * @param userid
	 * @return
	 * @throws IOException
	 * @throws NotLoggedInException 
	 * @throws NoSuchUseridException 
	 */
	public boolean isCapable(UserId userid) throws IOException, NotLoggedInException, NoSuchUseridException;

	/**
	 * nonblocking request for a online-status notification
	 * 
	 * @param userid
	 * @throws NotLoggedInException
	 */
	public void requestOnlineNotification(UserId userid)
			throws NotLoggedInException;
}
