package com.jakeapp.core.synchronization;

import java.util.HashMap;
import java.util.Map;

import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.exceptions.IllegalProtocolException;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;

/**
 * Provides loops for {@link ISyncService} Only loops belong here, add
 * functionality in {@link ISyncService}
 * 
 * @author johannes
 */
public abstract class FriendlySyncService implements IFriendlySyncService {

	abstract protected Iterable<UserId> getProjectMembers(Project project);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.jakeapp.core.synchronization.IFriendlySyncService#startLogSync(com
	 * .jakeapp.core.domain.Project)
	 */
	@SuppressWarnings("unchecked")
	public Map<UserId, Iterable<LogEntry<ILogable>>> startLogSync(Project project)
			throws IllegalArgumentException, IllegalProtocolException {
		Map<UserId, Iterable<LogEntry<ILogable>>> lm = new HashMap<UserId, Iterable<LogEntry<ILogable>>>();
		for (UserId userId : getProjectMembers(project)) {
			lm.put(userId, this.startLogSync(project, userId));
		}
		return lm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.jakeapp.core.synchronization.IFriendlySyncService#poke(com.jakeapp
	 * .core.domain.Project)
	 */
	public void poke(Project project) {
		for (UserId userid : getProjectMembers(project)) {
			this.poke(project, userid);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.jakeapp.core.synchronization.IFriendlySyncService#pullObjects(java
	 * .lang.Iterable)
	 */
	public void pullObjects(Iterable<JakeObject> objects) throws NoSuchLogEntryException, NotLoggedInException, IllegalArgumentException {
		for (JakeObject jo : objects) {
			pullObject(jo);
		}
	}
}
