package com.jakeapp.core.synchronization;

import java.util.Map;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.exceptions.IllegalProtocolException;

/**
 * Provides loops for {@link ISyncService}
 * @author johannes
 */
public interface IFriendlySyncService extends ISyncService {

	/**
	 * @throws IllegalProtocolException 
	 * @see ISyncService#startLogSync(Project, UserId) 
	 */
	@SuppressWarnings("unchecked")
	public Map<UserId, Iterable<LogEntry>> startLogSync(Project project) throws IllegalArgumentException, IllegalProtocolException;

	/**
	 * @see ISyncService#poke(Project, UserId)
	 */
	public void poke(Project project);

	/**
	 * @see ISyncService#pullObject(JakeObject)
	 */
	public void pullObjects(Project project) throws IllegalArgumentException;

	/**
	 * @see ISyncService#pullObject(JakeObject)
	 */
	public void pullObjects(Iterable<JakeObject> objects);

}
