package com.jakeapp.violet.protocol.invites;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.msgservice.IMessageReceiveListener;
import com.jakeapp.violet.model.User;

/**
 * The <code>ProjectInvitationHandler</code> handles all incoming
 * <code>Invitation</code>s from other clients, but also encapsulates the logic
 * needed to invite other <code>User</code>s to the <code>Project</code>.
 */
public class ProjectInvitationHandler implements IMessageReceiveListener,
		IInvitationHandler {

	private static final Logger log = Logger
			.getLogger(ProjectInvitationHandler.class);

	private static final String INVITEMSG = "<invite/>";

	private Set<IProjectInvitationListener> invitationListeners = new HashSet<IProjectInvitationListener>();

	private static final int uuidlen = UUID.randomUUID().toString().length();

	/**
	 * Method implemented from <code>IMessageReceiveListener</code>, to handle
	 * incoming packets.
	 * 
	 * @param from_userid
	 *            the user id which sent the message
	 * @param content
	 *            the content of the message
	 */
	@Override
	public void receivedMessage(UserId from_userid, String content) {
		log.debug("receivedMessage: " + content + " from " + from_userid);
		if (content.startsWith(INVITEMSG)) {
			try {
				String innercontent = content.substring(INVITEMSG.length());
				UUID id = UUID.fromString(innercontent.substring(0, uuidlen));
				String name = innercontent.substring(uuidlen);
				User user = new User(from_userid.getUserId());

				for (IProjectInvitationListener listener : invitationListeners) {
					try {
						listener.invited(user, name, id);
					} catch (Exception e) {
						// TODO
						log.debug(e.getStackTrace()[0].toString()); // e.printStackTrace();
					}
				}

			} catch (Exception e) {
				log.warn("error decoding invite message", e);
			}
		} else {
			log.info("ignoring unknown message: " + content);
		}
	}

	public static String createInviteMessage(String projectname, UUID id) {
		return INVITEMSG + id + projectname;
	}

	/**
	 * Adds an <code>IProjectInvitationListener</code> to the list of
	 * <code>IProjectInvitationListeners</code> to get notified when an
	 * Invitation-Event occurs.
	 * 
	 * @param invitationListener
	 *            the <code>IProjectInvitationListener</code> to be added to the
	 *            list
	 */
	public void registerInvitationListener(
			IProjectInvitationListener invitationListener) {
		if (invitationListener != null)
			this.invitationListeners.add(invitationListener);
	}

	/**
	 * Removes this <code>IProjectInvitationListener</code> from the list of
	 * <code>IProjectInvitationListener</code>s.
	 * 
	 * @param invitationListener
	 *            the <code>IProjectInvitationListener</code> to be removed from
	 *            the list
	 */
	public void unregisterInvitationListener(
			IProjectInvitationListener invitationListener) {
		if (invitationListener != null)
			this.invitationListeners.remove(invitationListener);
	}
}
