package com.jakeapp.gui.swing.xcore;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.User;

import java.util.HashMap;

/**
 * @author studpete
 */
public class InvitationManager {
	private final HashMap<Project, User> invitationSource = new HashMap<Project, User>();
	private static final InvitationManager instance = new InvitationManager();

	public static InvitationManager get() {
		return instance;
	}

	// singleton
	private InvitationManager() {
	}

	public User getInvitationSource(Project p) {
		return invitationSource.get(p);
	}

	public void saveInvitationSource(Project p, User user) {
		invitationSource.put(p, user);
	}
}
