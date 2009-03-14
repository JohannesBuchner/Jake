package com.jakeapp.jake.test;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.exceptions.NoSuchUseridException;
import com.jakeapp.jake.ics.exceptions.OtherUserOfflineException;
import com.jakeapp.jake.ics.exceptions.TimeoutException;
import com.jakeapp.jake.ics.msgservice.IMessageReceiveListener;
import com.jakeapp.jake.ics.msgservice.IMsgService;
import com.jakeapp.jake.ics.status.ILoginStateListener;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class SimpleFakeMessageExchanger {

	static Logger log = Logger.getLogger(SimpleFakeMessageExchanger.class);

	private Map<UserId, Spitter> users = new HashMap<UserId, Spitter>();

	public IMsgService addUser(UserId user) {
		Spitter spitter = new Spitter(user);
		log.debug("adding user " + user);
		users.put(user, spitter);
		return spitter;
	}

	/**
	 * a simple messaging broker for mocking.
	 * 
	 * @see IMsgService
	 */
	private class Spitter implements IMsgService {

		private List<IMessageReceiveListener> listener = new LinkedList<IMessageReceiveListener>();

		private UserId user;

		public Spitter(UserId user) {
			this.user = user;
		}

		@Override
		public void registerReceiveMessageListener(IMessageReceiveListener receiveListener) {
			log.debug("registering MessageReceiveListener on " + user);
			listener.add(receiveListener);
		}

		@Override
		public void registerLoginStateListener(ILoginStateListener loginListener) {
		}

		@Override
		public Boolean sendMessage(UserId to_userid, String content)
				throws NetworkException, TimeoutException, NoSuchUseridException,
				OtherUserOfflineException {
			log.debug(user + " -> " + to_userid + " : " + content);
			users.get(to_userid).notify(user, content);
			return true;
		}

		/**
		 * we got a message from sender
		 * 
		 * @param sender
		 * @param content
		 */
		public void notify(UserId sender, String content) {
			for (IMessageReceiveListener l : listener) {
				try {
					log.debug("notifying a listener");
					l.receivedMessage(sender, content);
				} catch (Exception ignored) {
				}
			}
		}

		@Override
		public IMsgService getFriendMsgService() {
			return this;
		}

		@Override
		public void unRegisterReceiveMessageListener(
				IMessageReceiveListener receiveListener) {
			try {
				listener.remove(receiveListener);
			} catch (Exception ignored) {
			}

		}

		@Override
		public void unRegisterLoginStateListener(ILoginStateListener loginListener) {
		}
	}
}
