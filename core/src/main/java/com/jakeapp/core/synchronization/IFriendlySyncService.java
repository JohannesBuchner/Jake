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

	/**
	 * Invites a User to a project.
	 * @param project The project to invite the user to.
	 * @param userId The userId of the User. There is already a corresponding
	 * ProjectMember-Object stored in the project-local database. 
	 */
	void invite(Project project, UserId userId);

	/**
	 * Informs the person who invited us to a project that
	 * we accept the invitation.
	 */
	void notifyInvitationAccepted(Project project, UserId inviter);

	/**
	 * Informs the person who invited us to a project that
	 * we reject the invitation.
	 */
	void notifyInvitationRejected(Project project, UserId inviter);

}
