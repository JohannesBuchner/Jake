/**
 * 
 */
package com.jakeapp.core.synchronization.request;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hsqldb.lib.StringInputStream;

import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.domain.logentries.LogEntry;
import com.jakeapp.core.domain.User;
import com.jakeapp.core.domain.*;
import com.jakeapp.core.services.IProjectsFileServices;
import com.jakeapp.core.util.ProjectApplicationContextFactory;
import com.jakeapp.jake.fss.IFSService;

public class TrustAllRequestHandlePolicy implements RequestHandlePolicy {

	public TrustAllRequestHandlePolicy(ProjectApplicationContextFactory db,
			IProjectsFileServices projectsFileServices) {
		super();
		this.db = db;
		this.projectsFileServices = projectsFileServices;
	}

	private static final Logger log = Logger.getLogger(TrustAllRequestHandlePolicy.class);

	private ProjectApplicationContextFactory db;

    private IProjectsFileServices projectsFileServices;

	@Override
	public Iterable<LogEntry> getPotentialJakeObjectProviders(JakeObject jo) {
		List<LogEntry> providers = new LinkedList<LogEntry>();
		try {
			providers.add(db.getUnprocessedAwareLogEntryDao(jo).getLastOfJakeObject(jo, true)/*.getMember()*/);
			log.debug("first-hand provider: " + providers.get(0));
		} catch (NoSuchLogEntryException e) {
		}
		return providers;
	}

	@Override
	public InputStream handleJakeObjectRequest(User from, JakeObject jo) {
		try {
			if (jo instanceof NoteObject) {
				NoteObject no = db.getNoteObjectDao(jo.getProject()).complete(
						(NoteObject) jo);
				return new StringInputStream(no.getContent());
			} else {
				FileObject fo = db.getFileObjectDao(jo.getProject()).complete(
						(FileObject) jo);
				IFSService fss = this.projectsFileServices.getProjectFSService(jo.getProject());
				return new FileInputStream(fss.getFullpath(fo.getRelPath()));
			}
		} catch (Exception e) {
			log.warn("invalid request", e);
			return null;
		}
	}

	@Override
	public boolean handleLogSyncRequest(Project project, User from) {
		return true;
	}

}