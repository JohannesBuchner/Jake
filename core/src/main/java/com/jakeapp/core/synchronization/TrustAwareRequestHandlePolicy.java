/**
 * 
 */
package com.jakeapp.core.synchronization;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hsqldb.lib.StringInputStream;

import com.jakeapp.core.dao.ILogEntryDao;
import com.jakeapp.core.domain.logentries.LogEntry;
import com.jakeapp.core.domain.User;
import com.jakeapp.core.domain.*;
import com.jakeapp.core.services.IProjectsFileServices;
import com.jakeapp.core.util.ProjectApplicationContextFactory;
import com.jakeapp.jake.fss.IFSService;

public class TrustAwareRequestHandlePolicy implements RequestHandlePolicy {

	public TrustAwareRequestHandlePolicy(ProjectApplicationContextFactory db,
			IProjectsFileServices projectsFileServices) {
		this.setDb(db);
		this.setProjectsFileServices(projectsFileServices);
	}
	
	private static final Logger log = Logger.getLogger(TrustAwareRequestHandlePolicy.class);
	
	private ProjectApplicationContextFactory db;

    private IProjectsFileServices projectsFileServices;
    
	public ProjectApplicationContextFactory getDb() {
		return db;
	}
	
	private void setDb(ProjectApplicationContextFactory db) {
		this.db = db;
	}

	public IProjectsFileServices getProjectsFileServices() {
		return projectsFileServices;
	}

	private void setProjectsFileServices(IProjectsFileServices projectsFileServices) {
		this.projectsFileServices = projectsFileServices;
	}

	@Override
	public Iterable<LogEntry> getPotentialJakeObjectProviders(JakeObject jo) {
		List<User> providers = new LinkedList<User>();
		List<LogEntry> entries = new LinkedList<LogEntry>();

		List<LogEntry<JakeObject>> allVersions = db.getLogEntryDao(jo)
				.getAllVersionsOfJakeObject(jo);

		List<User> members = db.getTrustedProjectMembers(jo.getProject());

		for (LogEntry<? extends ILogable> entry : allVersions) {
			if (members.contains(entry.getMember())
					&& !providers.contains(entry.getMember())) {
				providers.add(entry.getMember());
				entries.add(entry);
			}
		}

		return entries;
	}

	@Override
	public InputStream handleJakeObjectRequest(User from, JakeObject jo) {
		InputStream result = null;
		
		if ( this.getTrustLevelBetween(jo.getProject(), from) != TrustState.NO_TRUST) {
			try {
				if (jo instanceof NoteObject) {
					NoteObject no = db.getNoteObjectDao(jo.getProject()).complete(
							(NoteObject) jo);
					result = new StringInputStream(no.getContent());
				} else {
					FileObject fo = db.getFileObjectDao(jo.getProject()).complete(
							(FileObject) jo);
					IFSService fss = this.projectsFileServices.getProjectFSService(jo.getProject());
					result = new FileInputStream(fss.getFullpath(fo.getRelPath()));
				}
			} catch (Exception e) {
				log.warn("invalid request", e);
			}
		}
		
		return result;
	}

	@Override
	public boolean handleLogSyncRequest(Project project, User from) {
		try {
			return getTrustLevelBetween(project, from) != TrustState.NO_TRUST;
		}
		catch (IllegalArgumentException iae) {
			return false;
		}
	}

	/**
	 * @param project
	 * @param from
	 * @return how much the user of <code>project</code> trusts <code>from</code>.
	 * @throws IllegalArgumentException
	 * 	if project, from or project.getUserId were not valid (e.g. null).
	 */
	private TrustState getTrustLevelBetween(Project project, User from) {
		User thisUser;
		ILogEntryDao dao;
		TrustState lvl;
		
		if (project==null || from==null)
			throw new IllegalArgumentException();
		
		//Acquire LogEntryDao
		dao = this.getDb().getUnprocessedAwareLogEntryDao(project);
		//Acquire the Project's user
		thisUser=project.getUserId();
		if (thisUser==null) throw new IllegalArgumentException();
		
		//check the level of trust
		lvl = dao.trustsHow(thisUser, from);
		return lvl;
	}
}