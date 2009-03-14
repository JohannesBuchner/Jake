/**
 * 
 */
package com.jakeapp.jake.ics.impl.mock;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.exceptions.NoSuchUseridException;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.msgservice.IMessageReceiveListener;
import com.jakeapp.jake.ics.users.IUsersService;

public class FriendsOnlyReceiveFilter implements IMessageReceiveListener {

	private IMessageReceiveListener innerListener;

	private IUsersService users;

	public FriendsOnlyReceiveFilter(IMessageReceiveListener receiveListener, IUsersService users) {
		this.innerListener = receiveListener;
		this.users = users;
	}

	@Override
	public void receivedMessage(UserId from_userid, String content) {
		try {
			if (this.users.isFriend(from_userid)) {
				try {
					this.innerListener.receivedMessage(from_userid, content);
				} catch (Exception ignored) {
				}
			}
		} catch (NotLoggedInException e) {
			MockMsgAndStatusService.log.debug("should never happen", e);
		} catch (NoSuchUseridException e) {
			MockMsgAndStatusService.log.debug("should never happen", e);
		}
	}
}