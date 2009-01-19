package com.jakeapp.core.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.hibernate.LockMode;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import com.jakeapp.core.dao.exceptions.NoSuchProjectException;
import com.jakeapp.core.domain.InvitationState;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.exceptions.InvalidProjectException;

/**
 * Hibernate implementation of the <code>IProjectDAO</code> Interface.
 */

public class HibernateProjectDao extends HibernateDaoSupport
        implements IProjectDao {
    private static Logger log = Logger.getLogger(HibernateProjectDao.class);


    @Override
    //@Transactional
    public Project create(Project project) throws InvalidProjectException {
        if (project == null ||
                project.getName() == null ||
                project.getName().length() == 0
                ) {
            throw new InvalidProjectException();
        }

        if (project.getProjectId() == null)
            throw new InvalidProjectException("projectId must not be null");

        log.debug("persisting project with uuid " + ((project != null) ? project.getProjectId() : "null"));
        try {
            if(project.getProjectId().isEmpty())
                project.setProjectId(UUID.randomUUID());
            

            this.getHibernateTemplate().getSessionFactory().getCurrentSession().persist(project);
        } catch (DataAccessException dae) {
            throw new InvalidProjectException(dae);
        }


//        Project result = (Project) getHibernateTemplate().get(Project.class, project.getProjectId());

        //getHibernateTemplate().flush();
        //      System.out.println("result = " + result);

//        getHibernateTemplate().initialize(project);
//        this.getHibernateTemplate().persist(project);


        //getSessionFactory().getCurrentSession().persist(project);
        //    log.debug("persisted");
        // getSessionFactory().getCurrentSession().flush();
//        log.debug("flushed");
//        getSessionFactory().getCurrentSession().getTransaction().commit();

//        this.getHibernateTemplate().initialize(project);
//        this.getHibernateTemplate().executeWithNewSession();
        //      this.getHibernateTemplate().save(project);
        //this.getHibernateTemplate().persist(project);

        //    return null;
        return project;
    }

    @Override
//    @Transactional
public Project read(UUID uuid) throws NoSuchProjectException {
        List<Project> results;

        Project result = null;

        if (uuid == null) {
            throw new NoSuchProjectException();
        }

        log.debug("calling get on uuid  " + uuid.toString());

        try {
//        	result = (Project) this.getHibernateTemplate().get(Project.class, uuid.toString());
            results = this.getHibernateTemplate().getSessionFactory().getCurrentSession()
                    .createQuery("FROM Project WHERE uuid = ?").setString(0, uuid.toString()).list();
            if (results.size() < 1) {
                log.debug("Didn't find a project belonging to uuid " + uuid.toString());
                throw new NoSuchProjectException();
            }

            return results.get(0);

        } catch (DataAccessException dae) {
            log.warn(dae);
            throw new NoSuchProjectException();
        }

        
/*


        if(result == null)
        {
            log.debug("normal get didn't worked.... ");
            //ArrayList results = (ArrayList) getHibernateTemplate().find("FROM Project WHERE projectId = ? ", uuid.toString());
            //ArrayList results = (ArrayList) getHibernateTemplate().getSessionFactory().getCurrentSession().
            ArrayList results = (ArrayList) getHibernateTemplate().find("FROM Project WHERE 1=1 ");
            log.debug("results.size() = " + results.size());
            if(results.size() > 0)
                result = (Project) results.get(0);

            
            if(result == null)
            {
                log.debug("find also not...");
            }
            else
            {
                log.debug(" YEAH we've got something!!! ");
                log.debug("found: " + result.getProjectId());

            }
        }


        if(result == null)
        {
            log.debug("found nothing");
            throw new NoSuchProjectException();
        }

        log.debug("found something!");
        log.debug("result.getProjectId() = " + result.getProjectId());

        return result;*/
    }

    /**
     * {@inheritDoc}
     */
//    @Transactional
    public Project update(Project project) throws NoSuchProjectException {
        if (project == null || project.getProjectId() == null) {
            throw new NoSuchProjectException();
        }

        try {
            this.getHibernateTemplate().getSessionFactory().getCurrentSession().update(project);
//            this.getHibernateTemplate().update(project, LockMode.WRITE);
        } catch (DataAccessException dae) {
            throw new NoSuchProjectException();
        }

        return project;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
//    @Transactional
    public List<Project> getAll() {
//        List projects;
//        List<Project> result = new ArrayList<Project>();

        log.debug("Retrieving a list of all projects.");

        return this.getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery("FROM Project").list();
        
//        projects = this.getHibernateTemplate().loadAll(Project.class);
//        for (Object o : projects) {
//            if (o instanceof Project) {
//                result.add((Project) o);
//            }
//        }
//
//        return result;
    }

    /**
     * {@inheritDoc}
     */
    public void delete(Project project) throws NoSuchProjectException {
        if (project == null || project.getProjectId() == null) {
            throw new NoSuchProjectException();
        }

        log.info("deleting project: " + project.getProjectId());

        try {
            //FIXME LockMode.WRITE causes an Exception
            //   this.getHibernateTemplate().delete(project/*, LockMode.WRITE*/);
//            this.getHibernateTemplate().delete(project, LockMode.NONE);
//            this.getHibernateTemplate().getSessionFactory().getCurrentSession().lock(project, LockMode.NONE);
//            this.getHibernateTemplate().getSessionFactory().getCurrentSession().delete(project);

            int row = this.getHibernateTemplate().getSessionFactory().getCurrentSession()
                    .createQuery("DELETE FROM Project WHERE uuid = ?")
                    .setString(0, project.getProjectId()).executeUpdate();
            if(row < 1)
                throw new NoSuchProjectException("affected rows count < 1");


        } catch (DataAccessException dae) {
            throw new NoSuchProjectException(dae);
        }
    }

    @Override
    public List<Project> getAll(InvitationState state) {
        List<Project> result = this.getHibernateTemplate().getSessionFactory().getCurrentSession().
                createQuery("FROM Project WHERE invitationstate = ?").setInteger(0, state.ordinal()).list();
        return result;


//         TODO 4 domdorn implement WHERE clause via DB-Select
//		List<Project> all = this.getAll();
//		List<Project> result = new ArrayList<Project>();
//
//		for (Project p : all)
//			if (p.getInvitationState().equals(state))
//				result.add(p);
//
//		return result;
	}
}
