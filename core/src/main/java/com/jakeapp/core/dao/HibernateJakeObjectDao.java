package com.jakeapp.core.dao;

import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.Tag;
import org.apache.log4j.Logger;
import org.hibernate.classic.Session;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.UUID;

/**
 * A generic hibernate DAO for <code>JakeObject</code>s.
 * 
 * @param <T>
 *            The type of the persisted Entity, subtype of
 *            <code>JakeObject</code>
 */
@Repository
public abstract class HibernateJakeObjectDao<T extends JakeObject> extends
		HibernateDaoSupport implements IJakeObjectDao<T> {

	private static Logger log = Logger.getLogger(HibernateJakeObjectDao.class);

	/**
	 * {@inheritDoc}
	 */
	public T persist(final T jakeObject) {
		sess().saveOrUpdate(jakeObject);

		return jakeObject;
	}

	private Session sess() {
		return this.getHibernateTemplate().getSessionFactory().getCurrentSession();
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional
	public T get(final UUID objectId) throws NoSuchJakeObjectException {
		ParameterizedType parameterizedType = (ParameterizedType) this.getClass()
				.getGenericSuperclass();
		String parameterName = ((Class) parameterizedType.getActualTypeArguments()[0])
				.getName();
		log.debug("getting JakeObject by id from table " + parameterName);
		if (objectId == null)
			throw new NoSuchJakeObjectException();
		String queryString = "FROM " + parameterName + " WHERE objectId = ? ";

		List<T> results = sess().createQuery(queryString).setString(0,
				objectId.toString()).list();

		if (results.size() > 0)
			return (T) results.get(0);

		throw new NoSuchJakeObjectException("jakeobject with uuid " + objectId.toString()
				+ "not found");
	}


	/**
	 * {@inheritDoc}
	 */
	@Transactional
	public List<T> getAll() {
		log.debug("getAll(Project) Test ========================================");

		ParameterizedType parameterizedType = (ParameterizedType) this.getClass()
				.getGenericSuperclass();
		String parameterName = ((Class) parameterizedType.getActualTypeArguments()[0])
				.getName();
		log.debug("xxx: " + parameterName);


		String queryString = "FROM " + parameterName + " ";

		List<T> results = sess().createQuery(queryString).list();

		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public void delete(final T jakeObject) {
		sess().delete(jakeObject);
	}

}
