package com.jakeapp.core.dao;

import com.jakeapp.core.dao.exceptions.NoSuchProjectMemberException;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProjectMember;
import org.apache.log4j.Logger;
import org.hibernate.classic.Session;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A hibernate <code>ProjectMember</code> DAO
 */
public class HibernateProjectMemberDao extends HibernateDaoSupport
		  implements IProjectMemberDao {
	private static Logger log = Logger.getLogger(HibernateProjectMemberDao.class);

	/**
	 * {@inheritDoc}
	 */
	public ProjectMember persist(Project project, ProjectMember projectMember) {
		// TODO various checks
		log.debug("trying to persist " + projectMember.getUserId() + " in session "
				+ getCurrentSession());

		log.debug("transaction: " + getCurrentSession().getTransaction());


		getCurrentSession().save(projectMember);
		
		return projectMember;
	}

	private Session getCurrentSession() {
		return this.getHibernateTemplate().getSessionFactory().getCurrentSession();
	}

	@Override
	@SuppressWarnings({"unchecked"})
	public ProjectMember get(UUID memberId) throws NoSuchProjectMemberException {
		log.debug("trying to fetch " + memberId + " in session "
				+ getCurrentSession());
		log.debug("query: "
				+ getCurrentSession().createQuery(
						"FROM ProjectMember WHERE memberId = ?").setString(0, memberId.toString()).getQueryString());
		List<ProjectMember> results = getCurrentSession().
				  createQuery("FROM ProjectMember WHERE memberId = ?").setString(0, memberId.toString()).list();

		if (results == null)
			throw new NoSuchProjectMemberException("Results are null");

		if (results.isEmpty())
			throw new NoSuchProjectMemberException("Results are empty");

		ProjectMember result = results.get(0);
		if (result != null)
			return result;

		throw new NoSuchProjectMemberException("No ProjectMember found by this id");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@SuppressWarnings({"Unchecked"})
	public List<ProjectMember> getAll(Project project) {
		List<ProjectMember> results = new ArrayList<ProjectMember>();

		results.addAll(getCurrentSession().
				  createQuery("FROM ProjectMember").list());

		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public void delete(Project project, ProjectMember projectMember) throws NoSuchProjectMemberException {

		ProjectMember member = this.get(projectMember.getUserId());

		try {
			log.debug("Deleting ProejctMember with ID " + projectMember.getUserId().toString());
			getCurrentSession().delete(member);
		}
		catch (DataAccessException e) {
			log.debug("catched DataAccessException meaning User does not exist");
			throw new NoSuchProjectMemberException(e.getMessage());
		}
	}


}
