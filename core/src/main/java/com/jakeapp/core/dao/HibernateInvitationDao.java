package com.jakeapp.core.dao;

import com.jakeapp.core.domain.Account;
import com.jakeapp.core.domain.Invitation;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.User;
import com.jakeapp.core.domain.exceptions.InvalidProjectException;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.ArrayList;
import java.util.List;


/**
 * Hibernate Implementation of the InvitationDAO
 */
public class HibernateInvitationDao extends HibernateDaoSupport implements IInvitationDao {

	private static Logger log = Logger.getLogger(HibernateInvitationDao.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Invitation create(Invitation invitation) throws InvalidProjectException {
		log.debug("persisting invitation " + invitation);
		try {
			this.getHibernateTemplate().getSessionFactory().getCurrentSession().persist(invitation);
			log.debug(" should be persisted now");
			return invitation;
		}
		catch (Exception e) {
			throw new InvalidProjectException(e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Invitation> getAll() {
		List<Invitation> results;
		try {
			results = this.getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery("FROM invitation").list();
			if (results == null) return new ArrayList<Invitation>();
			return results;
		}
		catch (DataAccessException e) {
			e.printStackTrace();
			return new ArrayList<Invitation>();
		}
		catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Invitation>();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Invitation> getAll(User user) {
		List<Invitation> results;
		try {
			// TODO hopefully user.getUserId is really to column to join to...
			results = this.getHibernateTemplate().getSessionFactory()
					.getCurrentSession().createQuery(
							"FROM invitation WHERE invitedOn = ?").setSerializable(0,
							user).list();
			if (results == null)
				return new ArrayList<Invitation>();
			return results;
		} catch (DataAccessException e) {
			e.printStackTrace();
			return new ArrayList<Invitation>();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Invitation>();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Project accept(Invitation invitation) {

		Project project = invitation.createProject();

		List<Account> accounts = this.getHibernateTemplate().getSessionFactory().
				getCurrentSession().createQuery(
				"FROM servicecredentials WHERE protocol = ? AND username = ?").
				setString(0, invitation.getInvitedOn().getProtocolType().toString()).
				setString(1, invitation.getInvitedOn().getUserId()).list();

		if (accounts.size() > 0) {
			project.setCredentials(accounts.get(0));
			project.setRootPath(invitation.getRootPath());
			this.getHibernateTemplate().getSessionFactory().getCurrentSession().persist(project);
			this.getHibernateTemplate().getSessionFactory().getCurrentSession().delete(invitation);
		} else {
			log.fatal("credentials not found!");
			throw new RuntimeException();
		}

		return project;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reject(Invitation invitation) {
		// TODO implement
	}
}
