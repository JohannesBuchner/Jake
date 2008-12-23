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

        log.debug("persisting project with uuid " + ((project != null) ? project.getProjectId() : "null"));
        try {
        	//TODO: create ID if it is still null
        	this.getHibernateTemplate().persist(project);	
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
    @Transactional
    public Project read(UUID uuid) throws NoSuchProjectException {
    	Project result = null;

    	if (uuid==null) {
    		throw new NoSuchProjectException();
    	}

        log.debug("calling read on uuid  " + uuid.toString());

        try {
        	result = (Project) this.getHibernateTemplate().get(Project.class, uuid.toString());
        } catch (DataAccessException dae) {
        	log.warn(dae);
        }

        if (result == null) {
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
    	if (project == null || project.getProjectId() == null) {
    		throw new NoSuchProjectException();
    	}
    	
    	try {
    		this.getHibernateTemplate().update(project, LockMode.WRITE);
    	} catch (DataAccessException dae) {
    		throw new NoSuchProjectException();
    	}

        return project;
    }

    /**
     * {@inheritDoc}
     */
	@SuppressWarnings("unchecked")
	@Transactional
    public List<Project> getAll() {
    	List projects;
    	List<Project> result = new ArrayList<Project>();

    	log.debug("Retrieving a list of all projects.");

    	projects = this.getHibernateTemplate().loadAll(Project.class);
		for (Object o : projects) {
			if (o instanceof Project) {
				result.add((Project) o);
			}
		}

		return result;
    }

    /**
     * {@inheritDoc}
     */
    public void delete(Project project) throws NoSuchProjectException {
    	if (project == null || project.getProjectId() == null) {
    		throw new NoSuchProjectException();
    	}

    	log.info("deleting project: " + project.getProjectId());

    	try
        {
    		//FIXME LockMode.WRITE causes an Exception
            this.getHibernateTemplate().delete(project/*, LockMode.WRITE*/);
        } catch (DataAccessException dae) {
            throw new NoSuchProjectException(dae);
        }
    }
}
