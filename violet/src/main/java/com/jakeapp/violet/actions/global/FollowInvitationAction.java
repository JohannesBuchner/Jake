package com.jakeapp.violet.actions.global;

import java.io.File;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.jake.fss.ProjectDir;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.violet.di.DI;
import com.jakeapp.violet.di.KnownProperty;
import com.jakeapp.violet.gui.Projects;
import com.jakeapp.violet.model.ProjectModel;
import com.jakeapp.violet.model.ProjectPreferences;

/**
 * Announces a <code>List</code> of <code>JakeObject</code>s.
 */
public class FollowInvitationAction extends AvailableLaterObject<Void> {

	private static final Logger log = Logger
			.getLogger(FollowInvitationAction.class);

	private Projects projects = DI.getImpl(Projects.class);

	private ProjectDir dir;

	private String user;

	private UUID projectid;

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
		ProjectPreferences prefs = DI.getPreferencesImpl(new File(dir, DI
				.getProperty(KnownProperty.PROJECT_FILENAMES_PREFERENCES)));
		prefs.set("id", projectid.toString());
		prefs.set("inviter", user);
		projects.add(dir);
		return null;
	}
}