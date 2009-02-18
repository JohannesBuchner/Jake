/**
 * 
 */
package com.jakeapp.core.synchronization;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.dao.exceptions.NoSuchProjectException;
import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.core.domain.ProjectMember;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.util.ApplicationContextFactory;
import com.jakeapp.core.util.ProjectApplicationContextFactory;
import com.jakeapp.core.services.IProjectsFileServices;

public class TrustAwareRequestHandlePolicy extends TrustAllRequestHandlePolicy {

	public TrustAwareRequestHandlePolicy(ProjectApplicationContextFactory db,
			IProjectsFileServices projectsFileServices, UserTranslator userTranslator) {
		super(db, projectsFileServices, userTranslator);
	}

	private static final Logger log = Logger
			.getLogger(TrustAwareRequestHandlePolicy.class);

	@Override
	public Iterable<UserId> getPotentialJakeObjectProviders(JakeObject jo) {
		/*
		 * TODO: add fallback providers. this only selects the one that made the revision
		 * if he/she is offline, you're screwed
		 */
		List<UserId> providers = new LinkedList<UserId>();
		try {
			LogEntry<? extends ILogable> newest = db.getLogEntryDao(jo).getLastOfJakeObject(
					jo);
			LogEntry<ILogable> le = new LogEntry<ILogable>(null,
					LogAction.JAKE_OBJECT_NEW_VERSION, newest.getTimestamp());
			Collection<LogEntry<? extends ILogable>> allVersions = db.getLogEntryDao(jo).findMatchingBefore(le);
			List<ProjectMember> members = db.getTrustedProjectMembers(jo.getProject());
			
			for(LogEntry<? extends ILogable> entry : allVersions) {
				if(members.contains(entry.getMember())) {
					providers.add(userTranslator.getUserIdFromProjectMember(jo.getProject(),
							entry.getMember()));
				}
			}
		} catch (NoSuchLogEntryException e) {
		} catch (IllegalArgumentException e) {
		} catch (NoSuchProjectException e) {
		}
		return providers;
	}
}