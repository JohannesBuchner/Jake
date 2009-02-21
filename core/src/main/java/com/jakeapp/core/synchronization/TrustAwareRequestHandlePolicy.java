/**
 * 
 */
package com.jakeapp.core.synchronization;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.dao.exceptions.NoSuchProjectException;
import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.services.IProjectsFileServices;
import com.jakeapp.core.util.ProjectApplicationContextFactory;

public class TrustAwareRequestHandlePolicy extends TrustAllRequestHandlePolicy {

	public TrustAwareRequestHandlePolicy(ProjectApplicationContextFactory db,
			IProjectsFileServices projectsFileServices) {
		super(db, projectsFileServices);
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
			List<LogEntry<JakeObject>> allVersions = db.getLogEntryDao(jo).getAllVersionsOfJakeObject(jo);
			
			List<UserId> members = db.getTrustedProjectMembers(jo.getProject());
			
			for(LogEntry<? extends ILogable> entry : allVersions) {
				if(members.contains(entry.getMember())) {
					providers.add(entry.getMember());
				}
			}
		} catch (NoSuchLogEntryException e) {
		} catch (IllegalArgumentException e) {
		} catch (NoSuchProjectException e) {
		}
		return providers;
	}
}