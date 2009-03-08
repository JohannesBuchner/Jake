package com.jakeapp.gui.swing.xcore;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.User;
import com.jakeapp.core.services.IProjectInvitationListener;
import com.jakeapp.gui.swing.callbacks.ProjectChanged;
import com.jakeapp.gui.swing.components.JakeStatusBar;
import com.jakeapp.gui.swing.dialogs.generic.JSheet;
import com.jakeapp.gui.swing.globals.JakeContext;
import org.apache.log4j.Logger;

/**
 * @author studpete
 */
class ProjectInvitationListener implements IProjectInvitationListener {
	private static Logger log = Logger.getLogger(ProjectInvitationListener.class);

	public ProjectInvitationListener() {
		log.trace("Created ProjectInvitationListener in GUI ");
	}


	// fixme: should be a invitation, not a project.
	@Override public void invited(User user, Project p) {
		log.debug("received invitation from " + user + " for project: " + p);

		ObjectCache.get().updateProjects();

		EventCore.get().fireProjectChanged(new ProjectChanged.ProjectChangedEvent(null,
						ProjectChanged.ProjectChangedEvent.Reason.Invited));
	}

	@Override public void accepted(User user, Project p) {
		log.debug("accepted: " + user + ", project" + p);

		EventCore.get().fireLogChanged(p);

		JakeStatusBar.showMessage("User " + user + " accepted your Invitation to " + p);

		//debug
		JSheet.showMessageSheet(JakeContext.getFrame(), "User " + user + " accepted your Invitation to " + p);
	}

	@Override public void rejected(User user, Project p) {
		log.debug("rejected" + user + ", project" + p);

		EventCore.get().fireLogChanged(p);

		JakeStatusBar.showMessage("User " + user + " rejected your Invitation to " + p);

		//debug
		JSheet.showMessageSheet(JakeContext.getFrame(), "User " + user + " rejected your Invitation to " + p);		
	}
}