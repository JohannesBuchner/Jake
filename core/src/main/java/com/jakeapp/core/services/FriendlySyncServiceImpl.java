package com.jakeapp.core.services;

import java.util.LinkedList;
import java.util.List;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.exceptions.IllegalProtocolException;
import com.jakeapp.core.domain.exceptions.ProjectNotLoadedException;


public abstract class FriendlySyncServiceImpl implements IFriendlySyncService {

	abstract protected Iterable<UserId> getProjectMembers(Project project);

	abstract protected Iterable<JakeObject> getMissingJakeObjects(Project project);

	@Override
	public void poke(Project project) {
		for (UserId userid : getProjectMembers(project)) {
			this.poke(project, userid);
		}
	}

	@Override
	public void pullObjects(Project project) throws IllegalArgumentException {
		pullObjects(getMissingJakeObjects(project));
	}

	@Override
	public void pullObjects(Iterable<JakeObject> objects) {
		for (JakeObject jo : objects) {
			pullObject(jo);
		}
	}

	@Override
	public Iterable<LogEntry> startLogSync(Project project)
			throws IllegalArgumentException, IllegalProtocolException {
		List<LogEntry> le = new LinkedList<LogEntry>();
		for (UserId userId : getProjectMembers(project)) {
			le.addAll(this.startLogSync(project, userId));
		}
		return le;
	}

}
