package com.jakeapp.gui.swing.xcore;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.UserId;

import java.util.HashMap;

/**
 * @author studpete
 */
public class InvitationManager {
	private final HashMap<Project, UserId> invitationSource = new HashMap<Project, UserId>();
	private static final InvitationManager instance = new InvitationManager();

	public static InvitationManager get() {
		return instance;
	}

	// singleton
	private InvitationManager() {
	}

	public UserId getInvitationSource(Project p) {
		return invitationSource.get(p);
	}

	public void saveInvitationSource(Project p, UserId user) {
		invitationSource.put(p, user);
	}
}
