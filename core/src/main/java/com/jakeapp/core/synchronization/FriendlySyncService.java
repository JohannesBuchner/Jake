package com.jakeapp.core.synchronization;

import java.util.HashMap;
import java.util.Map;

import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.exceptions.IllegalProtocolException;

/**
 * Provides loops for {@link ISyncService}
 * Only loops belong here, add functionality in {@link ISyncService}
 * 
 * @author johannes
 */
public abstract class FriendlySyncService implements ISyncService {

	abstract protected Iterable<UserId> getProjectMembers(Project project);

	/**
	 * @throws IllegalProtocolException
	 * @see ISyncService#startLogSync(Project, UserId)
	 */
	@SuppressWarnings("unchecked")
	public Map<UserId, Iterable<LogEntry>> startLogSync(Project project)
			throws IllegalArgumentException, IllegalProtocolException {
		Map<UserId, Iterable<LogEntry>> lm = new HashMap<UserId, Iterable<LogEntry>>();
		for (UserId userId : getProjectMembers(project)) {
			lm.put(userId, this.startLogSync(project, userId));
		}
		return lm;
	}

	/**
	 * @see ISyncService#poke(Project, UserId)
	 */
	public void poke(Project project) {
		for (UserId userid : getProjectMembers(project)) {
			this.poke(project, userid);
		}
	}

	/**
	 * @see ISyncService#pullObject(JakeObject)
	 */
	public void pullObjects(Project project) throws IllegalArgumentException {
		try {
			pullObjects(getPullableFileObjects(project));
		} catch (NoSuchLogEntryException e) {
		}
	}

	/**
	 * @throws NoSuchLogEntryException
	 * @see ISyncService#pullObject(JakeObject)
	 */
	public void pullObjects(Iterable<JakeObject> objects) throws NoSuchLogEntryException {
		for (JakeObject jo : objects) {
			pullObject(jo);
		}
	}

}
