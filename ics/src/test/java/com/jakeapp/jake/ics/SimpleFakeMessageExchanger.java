package com.jakeapp.jake.ics;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.exceptions.NoSuchUseridException;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.exceptions.OtherUserOfflineException;
import com.jakeapp.jake.ics.exceptions.TimeoutException;
import com.jakeapp.jake.ics.msgservice.IMessageReceiveListener;
import com.jakeapp.jake.ics.msgservice.IMsgService;


public class SimpleFakeMessageExchanger {

	static Logger log = Logger.getLogger(SimpleFakeMessageExchanger.class);

	private Map<UserId, Spitter> users = new HashMap<UserId, Spitter>();

	public IMsgService addUser(UserId user) {
		Spitter spitter = new Spitter(user);
		log.debug("adding user " + user);
		users.put(user, spitter);
		return spitter;
	}

	private class Spitter implements IMsgService {

		private List<IMessageReceiveListener> listener = new LinkedList<IMessageReceiveListener>();
		private UserId user;

		public Spitter(UserId user) {
			this.user = user;
		}

		@Override
		public void registerReceiveMessageListener(
				IMessageReceiveListener receiveListener)
				throws NotLoggedInException {
			log.debug("registering MessageReceiveListener on " + user);
			listener.add(receiveListener);
		}

		@Override
		public Boolean sendMessage(UserId to_userid, String content)
				throws NetworkException, NotLoggedInException,
				TimeoutException, NoSuchUseridException,
				OtherUserOfflineException {
			log.debug(user + " -> " + to_userid + " : " + content);
			users.get(to_userid).notify(to_userid, content);
			return true;
		}
		
		public void notify(UserId userid, String content) {
			for(IMessageReceiveListener l : listener) {
				log.debug("notifying a listener");
				l.receivedMessage(userid, content);
			}
		}
	}
}
