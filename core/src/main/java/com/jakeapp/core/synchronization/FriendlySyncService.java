package com.jakeapp.core.synchronization;

import java.util.HashMap;
import java.util.Map;

import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.dao.exceptions.NoSuchProjectException;
import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.logentries.LogEntry;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.User;
import com.jakeapp.core.domain.exceptions.IllegalProtocolException;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;

/**
 * Provides loops for {@link ISyncService} Only loops belong here, add
 * functionality in {@link ISyncService}. The class is only for convenience methods.
 * 
 * @author johannes
 */
public abstract class FriendlySyncService implements IFriendlySyncService {

	/**
	 * @param project A Project to get all members for.
	 * @return An iterable of all users that are in the project, including the current user
	 * 	if they are in the project.
	 * @throws NoSuchProjectException if <code>project</code> is null or does not exist.
	 */
	abstract protected Iterable<User> getProjectMembers(Project project) throws NoSuchProjectException;

	/*
	 * starts the logsync with all members of the project.
	 * @see com.jakeapp.core.synchronization.IFriendlySyncService#startLogSync(
	 * 	com.jakeapp.core.domain.Project)
	 */
	public Map<User, Iterable<LogEntry<ILogable>>> startLogSync(Project project)
			throws IllegalArgumentException, IllegalProtocolException, NoSuchProjectException {
		Map<User, Iterable<LogEntry<ILogable>>> lm = new HashMap<User, Iterable<LogEntry<ILogable>>>();
		for (User pm : getProjectMembers(project)) {
			lm.put(pm, this.startLogSync(project, pm));
		}
		return lm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.jakeapp.core.synchronization.ISyncService#poke(com.jakeapp
	 * .core.domain.Project)
	 */
	public void poke(Project project) throws NoSuchProjectException {
		for (User pm : getProjectMembers(project)) {
			if(project.getUserId().equals(pm)) continue;
			this.poke(project, pm);
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
