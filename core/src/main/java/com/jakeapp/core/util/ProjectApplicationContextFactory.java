package com.jakeapp.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jakeapp.core.dao.IFileObjectDao;
import com.jakeapp.core.dao.ILogEntryDao;
import com.jakeapp.core.dao.INoteObjectDao;
import com.jakeapp.core.dao.IProjectDao;
import com.jakeapp.core.dao.exceptions.NoSuchProjectException;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.services.futures.AllProjectFilesFuture;

/**
 * A factory that creates and configures spring application contexts.
 * Application contexts for a certain <code>Project</code>are only created once
 * and then reused.
 * 
 * @author Simon
 */
public class ProjectApplicationContextFactory extends ApplicationContextFactory {


     private SessionFactory sessionFactory;






	private IProjectDao projectDao;

	private List<UUID> project_uuids = new ArrayList<UUID>();

	private Map<Project, IFileObjectDao> fileObjectDaoProxies = new HashMap<Project, IFileObjectDao>();

	private Map<Project, INoteObjectDao> noteObjectDaoProxies = new HashMap<Project, INoteObjectDao>();

	private IFileObjectDao getOrCreateFileObjectDao(Project p) {
		if (!this.fileObjectDaoProxies.containsKey(p)) {
			IFileObjectDao innerDao = (IFileObjectDao) getApplicationContext(p).getBean(
					"fileObjectDao");
			this.fileObjectDaoProxies.put(p, new FileObjectDaoProxy(innerDao, p));
		}
		return this.fileObjectDaoProxies.get(p);
	}

	private INoteObjectDao getOrCreateNoteObjectDao(Project p) {
		if (!this.noteObjectDaoProxies.containsKey(p)) {
			INoteObjectDao innerDao = (INoteObjectDao) getApplicationContext(p).getBean(
					"noteObjectDao");
			this.noteObjectDaoProxies.put(p, new NoteObjectDaoProxy(innerDao, p));
		}
		return this.noteObjectDaoProxies.get(p);
	}

	public ProjectApplicationContextFactory(IProjectDao projectDao, SessionFactory sessionFactory) {
		super();
		log.debug("Creating the ProjectApplicationContextFactory");
		this.projectDao = projectDao;
        this.sessionFactory = sessionFactory;



//        sessionFactory.getCurrentSession().beginTransaction();
		loadProjectUUIDs();


//        sessionFactory.getCurrentSession().getTransaction().commit();
	}

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	private void loadProjectUUIDs() {
		List<Project> projects = this.projectDao.getAll();
		this.project_uuids.clear();
		for (Project proj : projects) {
			this.project_uuids.add(UUID.fromString(proj.getProjectId()));
		}
	}

	public ILogEntryDao getUnprocessedAwareLogEntryDao(Project p) {
		return (ILogEntryDao) getApplicationContext(p).getBean("logEntryDao");
	}

	public ILogEntryDao getUnprocessedAwareLogEntryDao(JakeObject jo) {
		return (ILogEntryDao) getApplicationContext(jo.getProject()).getBean(
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

	public Collection<UserId> getProjectMembers(Project p)
			throws NoSuchProjectException {
		return getLogEntryDao(p).getCurrentProjectMembers();
	}

	public List<UserId> getTrustedProjectMembers(Project p)
			throws NoSuchProjectException {
		return getLogEntryDao(p).getTrustGraph().get(p.getUserId());
	}

    /**
     * Returns a new instance of an AllProjectsFilesFuture belonging to the corresponding project.
     * @param project The Project in question
     * @return an AllProjectFilesFuture
     */
    public AllProjectFilesFuture getAllProjectFilesFuture(Project project)
    {
        return (AllProjectFilesFuture) getApplicationContext(project).getBean("allProjectFilesFuture");
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
	public synchronized ApplicationContext getApplicationContext(Project project) {

		UUID identifier = UUID.fromString(project.getProjectId());
		if (this.project_uuids.contains(identifier)) {
			return getApplicationContext(identifier);
		} else {
			loadProjectUUIDs();
			if (!this.project_uuids.contains(identifier)) {
				log.fatal("tried to open invalid context (database)!");
				throw new IllegalStateException("tried to load invalid database context");
			}
			return getApplicationContext(identifier);
		}
	}
}
