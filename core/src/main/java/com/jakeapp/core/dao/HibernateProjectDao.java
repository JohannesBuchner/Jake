package com.jakeapp.core.dao;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.exceptions.InvalidProjectException;
import com.jakeapp.core.dao.exceptions.NoSuchProjectException;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.io.Serializable;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataAccessException;
import org.apache.log4j.Logger;
import org.hibernate.LockMode;

/**
 * Hibernate implementation of the <code>IProjectDAO</code> Interface.
 */

public class HibernateProjectDao extends HibernateDaoSupport
        implements IProjectDao {
    private static Logger log = Logger.getLogger(HibernateProjectDao.class);


    @Override
    //@Transactional
    public Project create(Project project) throws InvalidProjectException {
        


        log.debug("persisting project with uuid " + project.getProjectId());
        getHibernateTemplate().persist(project);

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
    @Transactional
    public Project read(UUID uuid) throws NoSuchProjectException {
        log.debug("calling read on uuid  " + ((Serializable) uuid).toString());

        Project result = (Project) getHibernateTemplate().get(Project.class, uuid.toString());

        if(result == null)
        {
            log.debug("Didn't find a project belonging to uuid " + uuid.toString());
            throw new NoSuchProjectException();
        }

        return result;
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
    @Transactional
    public Project update(Project project) throws NoSuchProjectException {
        this.getHibernateTemplate().update(project);
        return project;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    public List<Project> getAll() {
        return this.getHibernateTemplate().loadAll(Project.class);
    }

    /**
     * {@inheritDoc}
     */
    public void delete(Project project) throws NoSuchProjectException {
        log.info("deleting project: " + project.getProjectId());
        
        try
        {
           this.getHibernateTemplate().delete("Project", project, LockMode.READ);
        }
        catch( ObjectOptimisticLockingFailureException e)
        {
            log.debug("Catched an ObjectOptimisticLockingFailureException");
            throw new NoSuchProjectException(e.getMessage());
        }
        catch (DataAccessException e)
        {
            log.warn("Catched a DataAccessException indicating a hibernate error");
            throw new NoSuchProjectException(e.getMessage());
        }
    }
}
