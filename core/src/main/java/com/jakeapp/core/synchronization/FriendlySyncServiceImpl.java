package com.jakeapp.core.synchronization;

import java.util.HashMap;
import java.util.Map;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.exceptions.IllegalProtocolException;


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
	public Map<UserId, Iterable<LogEntry>> startLogSync(Project project)
			throws IllegalArgumentException, IllegalProtocolException {
		Map<UserId, Iterable<LogEntry>> lm = new HashMap<UserId, Iterable<LogEntry>>();
		for (UserId userId : getProjectMembers(project)) {
			lm.put(userId, this.startLogSync(project, userId));
		}
		return lm;
	}
}
