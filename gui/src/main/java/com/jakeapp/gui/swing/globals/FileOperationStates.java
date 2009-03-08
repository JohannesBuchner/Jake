package com.jakeapp.gui.swing.globals;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.User;

import java.util.HashMap;

/**
 * @author studpete
 */
public class FileOperationStates {
	private final HashMap<Project, User> invitationSource = new HashMap<Project, User>();
	private static final FileOperationStates instance = new FileOperationStates();

	public static FileOperationStates get() {
		return instance;
	}

	// singleton
	private FileOperationStates() {
	}

	public User getInvitationSource(Project p) {
		return invitationSource.get(p);
	}

	public void saveInvitationSource(Project p, User user) {
		invitationSource.put(p, user);
	}
}