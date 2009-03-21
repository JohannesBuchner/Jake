package com.jakeapp.core.services;

import java.util.UUID;
import java.util.List;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.User;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.exceptions.NoSuchUseridException;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.exceptions.OtherUserOfflineException;
import com.jakeapp.jake.ics.exceptions.TimeoutException;
import com.jakeapp.jake.ics.msgservice.IMessageReceiveListener;

/**
 * The <code>ProjectInvitationHandler</code> handles all incoming Invitations from other clients, but also
 * encapsulates the logic needed to invite other <code>User</code>s to the project.
 */
public class ProjectInvitationHandler implements IMessageReceiveListener, IInvitationHandler {

	private static final Logger log = Logger.getLogger(ProjectInvitationHandler.class);

	private static final String INVITEMSG = "<invite/>";
	private static final String ACCEPTMSG = "<accept/>";
	private static final String REJECTMSG = "<reject/>";

	private List<IProjectInvitationListener> invitationListeners = new LinkedList<IProjectInvitationListener>();


	private int uuidlen = UUID.randomUUID().toString().length();

	private MsgService msg;

	/**
	 * Creates the <code>ProjectInvitationHandler</code> listening on the specified <code>MsgService</code>
	 * @param msg The MsgService to work on.
	 */
	public ProjectInvitationHandler(MsgService msg) {
		this.msg = msg;
	}

	/**
	 * Invites a <code>User</code> to a <code>Project</code> by sending a invitation packet.
	 *
	 * @param project The project to invite the user to.
	 * @param user	The userId of the User. There is already a corresponding
	 *                ProjectMember-Object stored in the project-local database.
	 * @return if the packet could be sent.
	 */
	public static boolean invite(Project project, User user) {
		ICService ics = project.getMessageService().getIcsManager().getICService(project);
		com.jakeapp.jake.ics.UserId bu = project.getMessageService().getIcsManager().getBackendUserId(user);

		String msg = createInviteMessage(project);

		try {
			boolean result = ics.getMsgService().sendMessage(bu, msg);
			if (result) {
				log.debug("message " + msg + " sent successfully");
			} else
				log.warn("message " + msg + " could not be sent!");

			return result;
		} catch (NotLoggedInException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (NoSuchUseridException e) {
			e.printStackTrace();
		} catch (NetworkException e) {
			e.printStackTrace();
		} catch (OtherUserOfflineException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Method implemented from <code>IMessageReceiveListener</code>, to handle incoming packets.
	 * @param from_userid the user id which sent the message
	 * @param content		 the content of the message
	 */
	@Override
	public void receivedMessage(UserId from_userid, String content) {
		// TODO make this cleaner, Redmine Issue #16

		log.debug("receivedMessage: " + content + " from " + from_userid);
		if (content.startsWith(INVITEMSG)) {
			try {
				String innercontent = content.substring(INVITEMSG.length());
				String uuidstr = innercontent.substring(0, uuidlen);
				Project p = getProject(innercontent, uuidstr);


				User user = msg.getIcsManager().getFrontendUserId(p, from_userid);

				log.info("got invited to Project " + p + " by " + from_userid);
				for (IProjectInvitationListener listener : invitationListeners) {
					try {
						listener.invited(user, p);
					}
					catch (Exception e) {
						// TODO
						log.debug(e.getStackTrace()[0].toString()); //e.printStackTrace();
					}
				}

			} catch (Exception e) {
				log.warn("error decoding invite message", e);
			}
		} else if (content.startsWith(ACCEPTMSG)) {
			try {
				log.debug("it seems to be an accept-invitation message");
				String innercontent = content.substring(ACCEPTMSG.length());
				String uuidstr = innercontent.substring(0, uuidlen);
				Project p = getProject(innercontent, uuidstr);

				User user = msg.getIcsManager().getFrontendUserId(p, from_userid);
				user.setProtocolType(msg.getProtocolType());


				log.info("got accept to Project " + p + " from " + from_userid);
				for (IProjectInvitationListener listener : invitationListeners) {
					try{
						listener.accepted(user, p);
					}
					catch(Exception e)
					{
						log.warn("A listener throw an exception in the accepted method");
					}
				}
			} catch (Exception e) {
				log.warn("error decoding accept message", e);
			}
		} else if (content.startsWith(REJECTMSG)) {
			try {
				String innercontent = content.substring(REJECTMSG.length());
				String uuidstr = innercontent.substring(0, uuidlen);
				Project p = getProject(innercontent, uuidstr);

				User user = msg.getIcsManager().getFrontendUserId(p, from_userid);
				user.setProtocolType(msg.getProtocolType());

				log.info("got reject to Project " + p + " from " + from_userid);
				for (IProjectInvitationListener listener : invitationListeners) {
					try{
						listener.rejected(user, p);
					}
					catch(Exception e)
					{
						log.warn("A listener throw an exception in the accepted method");
					}
				}
			} catch (Exception e) {
				log.warn("error decoding reject message", e);
			}
		} else {
			log.info("ignoring unknown message: " + content);
		}
	}

	private Project getProject(String innercontent, String uuidstr) {
		UUID uuid = UUID.fromString(uuidstr);
		String projectname = innercontent.substring(uuidlen);

		Project p = new Project(projectname, uuid, msg, null);

//		p.setRootPath("");
		p.setCredentials(msg.getServiceCredentials());
		return p;
	}

	private static String createInviteMessage(Project project) {
		return INVITEMSG + project.getProjectId() + project.getName();
	}


	private static String createInviteAcceptedMessage(Project project){
		return ACCEPTMSG + project.getProjectId() + project.getName();
	}

	private static String createInviteRejectedMessage(Project project){
		return REJECTMSG + project.getProjectId() + project.getName(); 
	}




	/**
	 * Informs the person who invited us to a project that we accept the
	 * invitation by sending a packet to the main ics of the other user.
	 *
	 * @param project The <code>Project</code> we where invited to
	 * @param inviter The <code>User</code> who invited us.
	 */
	public static void notifyInvitationAccepted(Project project, User inviter) {
		UserId backendUser = project.getMessageService().getIcsManager().getBackendUserId(inviter);

		String msg = createInviteAcceptedMessage(project);
		try {
			project.getMessageService().getMainIcs().getMsgService().sendMessage(backendUser, msg);
		} catch (Exception e) {
			log.warn("sending accept failed", e);
		}
	}

	/**
	 * Informs the person who invited us to a project that we reject the
	 * invitation by sending a packet to the main ics of the other user.
	 *
	 * @param project The <code>Project</code> we where invited to
	 * @param inviter The <code>User</code> who invited us.
	 */
	public static void notifyInvitationRejected(Project project, User inviter) {
		UserId backendUser = project.getMessageService().getIcsManager().getBackendUserId(inviter);
		String msg = createInviteRejectedMessage(project);
		try {
			project.getMessageService().getMainIcs().getMsgService().sendMessage(backendUser, msg);
		} catch (Exception e) {
			log.warn("sending reject failed", e);
		}
	}

	/**
	 * Adds an <code>IProjectInvitationListener</code> to the list of <code>IProjectInvitationListeners</code>
	 * to get notified when an Invitation-Event occurs. 
	 * @param invitationListener the <code>IProjectInvitationListener</code> to be added to the list
	 */
	public void registerInvitationListener(IProjectInvitationListener invitationListener) {
		if (invitationListener != null)
			this.invitationListeners.add(invitationListener);
	}

	/**
	 * Removes this <code>IProjectInvitationListener</code> from the list of <code>IProjectInvitationListener</code>s.
	 * @param invitationListener the <code>IProjectInvitationListener</code> to be removed from the list
	 */
	public void unregisterInvitationListener(IProjectInvitationListener invitationListener) {
		if (invitationListener != null)
			this.invitationListeners.remove(invitationListener);
	}
}
