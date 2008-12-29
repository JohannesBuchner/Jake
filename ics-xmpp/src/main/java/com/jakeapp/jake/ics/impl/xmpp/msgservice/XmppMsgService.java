package com.jakeapp.jake.ics.impl.xmpp.msgservice;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.exceptions.NoSuchUseridException;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.exceptions.OtherUserOfflineException;
import com.jakeapp.jake.ics.exceptions.TimeoutException;
import com.jakeapp.jake.ics.impl.xmpp.XmppConnectionData;
import com.jakeapp.jake.ics.msgservice.IMessageReceiveListener;
import com.jakeapp.jake.ics.msgservice.IMsgService;
import com.jakeapp.jake.ics.msgservice.IObjectReceiveListener;


public class XmppMsgService implements IMsgService {

	public XmppMsgService(XmppConnectionData connection) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void registerReceiveMessageListener(
			IMessageReceiveListener receiveListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerReceiveObjectListener(IObjectReceiveListener rl) {
		// TODO Auto-generated method stub

	}

	@Override
	public Boolean sendMessage(UserId to_userid, String content)
			throws NetworkException, NotLoggedInException, TimeoutException,
			NoSuchUseridException, OtherUserOfflineException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean sendObject(UserId to_userid, String objectidentifier,
			byte[] content) throws NetworkException, NotLoggedInException,
			TimeoutException, NoSuchUseridException, OtherUserOfflineException {
		// TODO Auto-generated method stub
		return null;
	}

}
