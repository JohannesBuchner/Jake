package com.jakeapp.core.dao;

import com.jakeapp.core.domain.Invitation;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.Account;
import com.jakeapp.core.domain.logentries.ProjectJoinedLogEntry;
import com.jakeapp.core.domain.logentries.StartTrustingProjectMemberLogEntry;
import com.jakeapp.core.domain.exceptions.InvalidProjectException;

import java.util.List;
import java.util.ArrayList;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;


public class HibernateInvitationDao  extends HibernateDaoSupport implements IInvitationDao {

	private static Logger log = Logger.getLogger(HibernateInvitationDao.class);

	@Override
	public Invitation create(Invitation invitation) throws InvalidProjectException {
		log.debug("persisting invitation " + invitation);
		try
		{
			this.getHibernateTemplate().getSessionFactory().getCurrentSession().persist(invitation);
			log.debug(" should be persisted now");
//			this.getHibernateTemplate().getSessionFactory().getCurrentSession().getTransaction().commit();
			return invitation;
		}
		catch(Exception e) {
			throw new InvalidProjectException(e.getMessage());
		}
	}

	@Override
	public List<Invitation> getAll() {
		List<Invitation> results;
		try
		{
			results = this.getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery("FROM invitation").list();
			if(results == null) return new ArrayList<Invitation>();
			return results;
		}
		catch (DataAccessException e)
		{
			e.printStackTrace();
			return new ArrayList<Invitation>();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return new ArrayList<Invitation>();			   
		}
	}

	@Override
	public Project accept(Invitation invitation) {

		Project project = invitation.createProject();

		List<Account> accounts = this.getHibernateTemplate().getSessionFactory().
				getCurrentSession().createQuery(
				"FROM servicecredentials WHERE protocol = ? AND username = ?").
				setString(0, invitation.getInvitedOn().getProtocolType().toString()).
				setString(1, invitation.getInvitedOn().getUserId()).list();

		if(accounts.size() > 0)
		{
		    project.setCredentials(accounts.get(0));
			project.setRootPath(invitation.getRootPath());
			this.getHibernateTemplate().getSessionFactory().getCurrentSession().persist(project);
			this.getHibernateTemplate().getSessionFactory().getCurrentSession().delete(invitation);

//			ProjectJoinedLogEntry logEntry1 = new ProjectJoinedLogEntry(project, invitation.getInvitedOn());
//			StartTrustingProjectMemberLogEntry logEntry2 = new StartTrustingProjectMemberLogEntry(invitation.getInviter(), invitation.getInvitedOn());
//			this.getHibernateTemplate().getSessionFactory().getCurrentSession().persist(logEntry1);
//			this.getHibernateTemplate().getSessionFactory().getCurrentSession().persist(logEntry2);
		}
		else
		{
			log.fatal("credentials not found!");
			throw new RuntimeException();
		}

		return project;
	}

	@Override
	public void reject(Invitation invitation) {
		//To change body of implemented methods use File | Settings | File Templates.
	}
}
