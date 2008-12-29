package com.jakeapp.jake.ics.impl.xmpp.status;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.exceptions.NoSuchUseridException;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.exceptions.OtherUserOfflineException;
import com.jakeapp.jake.ics.exceptions.TimeoutException;
import com.jakeapp.jake.ics.impl.xmpp.XmppConnectionData;
import com.jakeapp.jake.ics.status.IOnlineStatusListener;
import com.jakeapp.jake.ics.status.IStatusService;


public class XmppStatusService implements IStatusService {

	public XmppStatusService(XmppConnectionData connection) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getFirstname(UserId userid) throws NoSuchUseridException,
			OtherUserOfflineException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLastname(UserId userid) throws NoSuchUseridException,
			OtherUserOfflineException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserId getUserId(String userid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserId getUserid() throws NotLoggedInException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean isLoggedIn() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean isLoggedIn(UserId userid) throws NoSuchUseridException,
			NetworkException, NotLoggedInException, TimeoutException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean login(UserId userid, String pw) throws NetworkException,
			TimeoutException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void logout() throws NetworkException, TimeoutException {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerOnlineStatusListener(
			IOnlineStatusListener onlineStatusListener, UserId userid)
			throws NoSuchUseridException {
		// TODO Auto-generated method stub

	}

}
