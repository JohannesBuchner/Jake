package com.doublesignal.sepm.jake.ics;

import com.doublesignal.sepm.jake.ics.exceptions.NetworkException;
import com.doublesignal.sepm.jake.ics.exceptions.NoSuchUseridException;
import com.doublesignal.sepm.jake.ics.exceptions.NotLoggedInException;
import com.doublesignal.sepm.jake.ics.exceptions.OtherUserOfflineException;
import com.doublesignal.sepm.jake.ics.exceptions.TimeoutException;

public class MockICService implements IICService {

	public String getFirstname(String userid) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLastname(String userid) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean isLoggedIn() {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean isLoggedIn(String userid) throws NetworkException,
			NotLoggedInException, TimeoutException {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean login(String userid, String pw) throws NetworkException,
			TimeoutException {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean logout() throws NetworkException, TimeoutException {
		// TODO Auto-generated method stub
		return null;
	}

	public void registerOnlineStatusListener(IOnlineStatusListener osc,
			String userid) {
		// TODO Auto-generated method stub

	}

	public void registerReceiveMessageListener(IMessageReceiveListener rl) {
		// TODO Auto-generated method stub

	}

	public void registerReceiveObjectListener(IObjectReceiveListener rl) {
		// TODO Auto-generated method stub

	}

	public Boolean sendMessage(String to_userid, String content)
			throws NetworkException, NotLoggedInException, TimeoutException,
			NoSuchUseridException, OtherUserOfflineException {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean sendObject(String to_userid, String objectidentifier,
			byte[] content) throws NetworkException, NotLoggedInException,
			TimeoutException, NoSuchUseridException, OtherUserOfflineException {
		// TODO Auto-generated method stub
		return null;
	}

}
