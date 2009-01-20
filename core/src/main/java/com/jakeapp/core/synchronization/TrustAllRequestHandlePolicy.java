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

import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProjectMember;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.util.ApplicationContextFactory;
import com.jakeapp.core.util.ProjectApplicationContextFactory;
import com.jakeapp.core.services.IProjectsFileServices;
import com.jakeapp.jake.fss.IFSService;

public class TrustAllRequestHandlePolicy implements RequestHandlePolicy {

	public TrustAllRequestHandlePolicy(ProjectApplicationContextFactory db,
			IProjectsFileServices projectsFileServices, UserTranslator userTranslator) {
		super();
		this.db = db;
		this.projectsFileServices = projectsFileServices;
		this.userTranslator = userTranslator;
	}

	private static final Logger log = Logger.getLogger(TrustAllRequestHandlePolicy.class);

	protected ProjectApplicationContextFactory db;

	protected UserTranslator userTranslator;


    private IProjectsFileServices projectsFileServices;



	@Override
	public Iterable<UserId> getPotentialJakeObjectProviders(JakeObject jo) {
		List<UserId> providers = new LinkedList<UserId>();
		try {
			ProjectMember member = db.getLogEntryDao(jo).getMostRecentFor(jo).getMember();
			providers.add(userTranslator.getUserIdFromProjectMember(jo.getProject(),
					member));
		} catch (NoSuchLogEntryException e) {
		}
		return providers;
	}

	@Override
	public InputStream handleJakeObjectRequest(UserId from, JakeObject jo) {
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
	public boolean handleLogSyncRequest(Project project, UserId from) {
		return true;
	}

}