package com.jakeapp.gui.swing.globals;

import com.jakeapp.core.domain.Invitation;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.User;
import com.jakeapp.core.services.MsgService;
import com.jakeapp.gui.swing.callbacks.ContextChangedCallback;
import com.jakeapp.gui.swing.xcore.EventCore;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.JakeMainApp;

import javax.swing.*;

/**
 * Static Context Class. Called by various code to get the right gui states.
 */
public class JakeContext {
	private static Project project = null;
	private static Invitation invitation = null;

	// this is the message service the user chooses (one per application)
	private static MsgService msgService = null;


	/**
	 * Convenience call to get the main gui frame faster.
	 */
	public static JFrame getFrame() {
		return JakeMainView.getMainView().getFrame();
	}

	public static Project getProject() {
		return project;
	}


	public static void setProject(Project project) {
		if (JakeContext.project != project) {
			JakeContext.project = project;

			// xor: project <-> invitation
			if(project != null && getInvitation() != null) {
				setInvitation(null);
			}

			// fire the event and relay to all items/components/actions/panels
			EventCore.get().fireContextChanged(ContextChangedCallback.Reason.Project, project);
		}
	}

	/**
	 * Returns the one and only project user that is within app (and project) context.
	 */
	public static User getCurrentUser() {
		return getProject().getMessageService().getUserId();
	}

	public static boolean isCoreInitialized() {
		return JakeMainApp.getCore() != null;
	}

	/**
	 * Set a new Msg Service.
	 *
	 * @param msg
	 */
	public static void setMsgService(MsgService msg) {
		JakeContext.msgService = msg;

		// inform the event core for this change
		EventCore.get().fireContextChanged(ContextChangedCallback.Reason.MsgService, msg);
	}

	/**
	 * Returns the global Message Service (if a user was chosen)
	 *
	 * @return
	 */
	public static MsgService getMsgService() {
		return JakeContext.msgService;
	}

	public static Invitation getInvitation() {
		return invitation;
	}

	public static void setInvitation(Invitation invitation) {
		JakeContext.invitation = invitation;

			// xor: project <-> invitation
			if(invitation != null && getProject() != null) {
				setProject(null);
			}

		EventCore.get().fireContextChanged(ContextChangedCallback.Reason.Invitation, invitation);
	}
}