package com.jakeapp.violet.actions.global;

import java.util.UUID;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.jake.fss.ProjectDir;
import com.jakeapp.violet.di.IProjectPreferencesFactory;
import com.jakeapp.violet.gui.Projects;
import com.jakeapp.violet.model.ProjectPreferences;

/**
 * Announces a <code>List</code> of <code>JakeObject</code>s.
 */
public class FollowInvitationAction extends AvailableLaterObject<Void> {

	private static final Logger log = Logger
			.getLogger(FollowInvitationAction.class);

	@Inject
	private Projects projects;

	private ProjectDir dir;

	private String user;

	private UUID projectid;

	@Inject
	IProjectPreferencesFactory preferences;

	public FollowInvitationAction(ProjectDir dir, String user, UUID projectid) {
		this.dir = dir;
		this.user = user;
		this.projectid = projectid;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Void calculate() throws Exception {
		ProjectPreferences prefs = preferences.get(new ProjectDir(dir));
		prefs.set("id", projectid.toString());
		prefs.set("inviter", user);
		projects.add(dir);
		return null;
	}
}