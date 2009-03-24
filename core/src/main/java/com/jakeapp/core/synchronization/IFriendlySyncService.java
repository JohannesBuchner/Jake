package com.jakeapp.core.synchronization;

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


public interface IFriendlySyncService extends ISyncService {

	/**
	 * @param project a Project for which a logsync with all users should occur.
	 * @see ISyncService#startLogSync(com.jakeapp.core.domain.Project, com.jakeapp.core.domain.User)
	 * @return Entries to sync for each user in a project
	 */
	public Map<User, Iterable<LogEntry<ILogable>>> startLogSync(Project project)
			throws IllegalArgumentException, IllegalProtocolException, NoSuchProjectException;

	/**
	 * @param project to poke all members for.
	 * @throws NoSuchProjectException if no such project exists or <code>project</code> is null.
	 * @see ISyncService#poke(com.jakeapp.core.domain.Project, com.jakeapp.core.domain.User)
	 */
	public void poke(Project project) throws NoSuchProjectException;

	/**
	 * Pulls all Objects specified in <code>Objects</code>
	 * @param objects a collection of Objects, which may not be null.
	 * @see ISyncService#pullObject(JakeObject)
	 */
	public void pullObjects(Iterable<JakeObject> objects)
			throws NoSuchLogEntryException, NotLoggedInException, IllegalArgumentException;

}