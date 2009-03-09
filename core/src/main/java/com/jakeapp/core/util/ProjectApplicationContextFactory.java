package com.jakeapp.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jakeapp.core.Injected;
import com.jakeapp.core.dao.IFileObjectDao;
import com.jakeapp.core.dao.ILogEntryDao;
import com.jakeapp.core.dao.INoteObjectDao;
import com.jakeapp.core.dao.IProjectDao;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.User;
import com.jakeapp.core.services.futures.AllProjectFilesFuture;
import com.jakeapp.jake.fss.IFSService;

/**
 * A factory that creates and configures spring application contexts.
 * Application contexts for a certain <code>Project</code>are only created once
 * and then reused.
 * 
 * @author Simon
 */
public class ProjectApplicationContextFactory extends ApplicationContextFactory {

	@SuppressWarnings("unused")
	private SessionFactory sessionFactory;

	private IProjectDao projectDao;

	private List<UUID> project_uuids = null;

	private Map<Project, IFileObjectDao> fileObjectDaoProxies = new HashMap<Project, IFileObjectDao>();

	private Map<Project, INoteObjectDao> noteObjectDaoProxies = new HashMap<Project, INoteObjectDao>();

	/**
	 * Construct the ProjectApplicationContextFactory
	 * 
	 * @param projectDao
	 * @param sessionFactory
	 */
	@Injected
	public ProjectApplicationContextFactory(IProjectDao projectDao,
			SessionFactory sessionFactory) {
		super();
		log.debug("Creating the ProjectApplicationContextFactory");
		this.projectDao = projectDao;
		this.sessionFactory = sessionFactory;

		// do not load here, it deadlocks the Spring thread
		// loadProjectUUIDs();
	}

	/**
	 * The IFileObjectDao is per Project. Thus it can't be injected and has to
	 * be created here.
	 * 
	 * @param p
	 * @return
	 */
	private IFileObjectDao getOrCreateFileObjectDao(Project p) {
		if (!this.fileObjectDaoProxies.containsKey(p)) {
			IFileObjectDao innerDao = (IFileObjectDao) getApplicationContextThread(p)
					.getBean("fileObjectDao");
			this.fileObjectDaoProxies.put(p, new FileObjectDaoProxy(innerDao, p));
		}
		return this.fileObjectDaoProxies.get(p);
	}

	private INoteObjectDao getOrCreateNoteObjectDao(Project p) {
		if (!this.noteObjectDaoProxies.containsKey(p)) {
			INoteObjectDao innerDao = (INoteObjectDao) getApplicationContextThread(p)
					.getBean("noteObjectDao");
			this.noteObjectDaoProxies.put(p, new NoteObjectDaoProxy(innerDao, p));
		}
		return this.noteObjectDaoProxies.get(p);
	}

	private void ensureInitialised() {
		if (this.project_uuids == null)
			this.loadProjectUUIDs();
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	private void loadProjectUUIDs() {
		List<UUID> project_uuids = new ArrayList<UUID>();
		List<Project> projects = ProjectApplicationContextFactory.this.projectDao
				.getAll();
		project_uuids.clear();
		for (Project proj : projects) {
			project_uuids.add(UUID.fromString(proj.getProjectId()));
		}
		this.project_uuids = project_uuids;
	}

	public ILogEntryDao getUnprocessedAwareLogEntryDao(Project p) {
		return (ILogEntryDao) getApplicationContextThread(p).getBean("logEntryDao");
	}

	public ILogEntryDao getUnprocessedAwareLogEntryDao(JakeObject jo) {
		return (ILogEntryDao) getApplicationContextThread(jo.getProject()).getBean(
				"logEntryDao");
	}

	public UnprocessedBlindLogEntryDaoProxy getLogEntryDao(Project p) {
		return new UnprocessedBlindLogEntryDaoProxy(getUnprocessedAwareLogEntryDao(p));
	}

	public UnprocessedBlindLogEntryDaoProxy getLogEntryDao(JakeObject jo) {
		return new UnprocessedBlindLogEntryDaoProxy(getUnprocessedAwareLogEntryDao(jo));
	}

	public INoteObjectDao getNoteObjectDao(Project p) {
		return getOrCreateNoteObjectDao(p);
	}

	public IFileObjectDao getFileObjectDao(Project p) {
		return getOrCreateFileObjectDao(p);
	}

	public Collection<User> getProjectMembers(Project p) {
		return getLogEntryDao(p).getCurrentProjectMembers(p.getUserId());
	}

	public List<User> getTrustedProjectMembers(Project p) {
		return getLogEntryDao(p).getTrustGraph().get(p.getUserId());
	}

	/**
	 * Returns a new instance of an AllProjectsFilesFuture belonging to the
	 * corresponding project.
	 * 
	 * @param project
	 *            The Project in question
	 * @return an AllProjectFilesFuture
	 */
	public AllProjectFilesFuture getAllProjectFilesFuture(Project project,IFSService fss) {
		return new AllProjectFilesFuture(project,this.getFileObjectDao(project),fss);
	}

	/**
	 * Get the <code>ApplicationContext</code> for a given <code>
	 * Project</code>. If an
	 * <code>ApplicationContext</code> for the given <code>Project</code>
	 * already exists, the existing context is returned, otherwise a new context
	 * will be created. This method is <code>synchronized</code>
	 * 
	 * @param project
	 *            the project for which the application context is used
	 * @return the <code>ApplicationContext</code>
	 */
	public synchronized ApplicationContextThread getApplicationContextThread(
			Project project) {

		UUID identifier = UUID.fromString(project.getProjectId());
		ensureInitialised();
		if (this.project_uuids.contains(identifier)) {
			return super.getApplicationContextThread(identifier);
		} else {
			log.debug("loading project context");
			this.loadProjectUUIDs();
			if (!this.project_uuids.contains(identifier)) {
				log.fatal("tried to open invalid context (database)!");
				throw new IllegalStateException("tried to load invalid database context");
			}
			return super.getApplicationContextThread(identifier);
		}
	}
}