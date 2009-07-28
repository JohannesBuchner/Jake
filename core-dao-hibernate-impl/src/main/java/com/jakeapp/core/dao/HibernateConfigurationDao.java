package com.jakeapp.core.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import com.jakeapp.core.domain.Configuration;


/**
 * Hibernate implementation of the IConfigurationInterface.
 *
 * @author Simon
 */
@Transactional
public class HibernateConfigurationDao extends HibernateDaoSupport implements
		IConfigurationDao {

	private Logger log = Logger.getLogger(HibernateConfigurationDao.class);


	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = false)
	public final void deleteConfigurationValue(final String name) {
		try {
			this.getHibernateTemplate().getSessionFactory().getCurrentSession()
					.createQuery("DELETE FROM configuration WHERE key = ? ").setString(0,
					name).executeUpdate();
		} catch (DataAccessException e) {
			log.debug("cought dataAccessException");
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public final boolean configurationValueExists(final String name) {
		List<String> result = this.getHibernateTemplate().getSessionFactory()
				.getCurrentSession().createQuery(
						"SELECT TRUE FROM configuration WHERE key = ? ").setString(0,
						name).list();

		return (result.size() > 0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = false)
	public Configuration update(final Configuration configuration) {
		this.getHibernateTemplate().getSessionFactory().getCurrentSession().merge(
				configuration);
		return configuration;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public List<Configuration> getAll() {
		List<Configuration> result = this.getHibernateTemplate().getSessionFactory()
				.getCurrentSession().createQuery("FROM configuration").list();

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public final String getConfigurationValue(final String name) {

		List<Configuration> result = this.getHibernateTemplate().getSessionFactory()
				.getCurrentSession().createQuery("FROM configuration WHERE key = ? ")
				.setString(0, name).list();

		if (result.size() > 0) {
			return result.get(0).getValue();
		} else {
			return "";
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = false)
	public final void setConfigurationValue(final String name, final String value) {
		Configuration conf = new Configuration(name, value);
		this.getHibernateTemplate().getSessionFactory().getCurrentSession().merge(conf);
	}

}
