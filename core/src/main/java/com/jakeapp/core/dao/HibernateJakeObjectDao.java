package com.jakeapp.core.dao;

import com.jakeapp.core.DarkMagic;
import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;
import com.jakeapp.core.domain.JakeObject;
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
		if(jakeObject.getUuid() == null) {
			// TODO: maybe we should generate it here?
			throw new NullPointerException("JakeObject has to have UUID");
		}
		sess().saveOrUpdate(jakeObject);

		return jakeObject;
	}

	private Session sess() {
		return this.getHibernateTemplate().getSessionFactory().getCurrentSession();
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public T get(final UUID objectId) throws NoSuchJakeObjectException {
		if (objectId == null)
			throw new NoSuchJakeObjectException();
		String queryString = "FROM " + getTableByType() + " WHERE objectId = ? ";
		List<T> results = sess().createQuery(queryString).setString(0,
				objectId.toString()).list();

		if (results.size() > 0)
			return results.get(0);

		throw new NoSuchJakeObjectException("jakeobject with uuid " + objectId.toString()
				+ "not found");
	}


	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<T> getAll() {
		log.trace("getAll()");
		return sess().createQuery("FROM " + getTableByType()).list();
	}

	@DarkMagic
	private String getTableByType() {
		ParameterizedType parameterizedType = (ParameterizedType) this.getClass()
				.getGenericSuperclass();
		String parameterName = ((Class) parameterizedType.getActualTypeArguments()[0])
				.getName();
		return parameterName;
	}

	/**
	 * {@inheritDoc}
	 */
	public void delete(final T jakeObject) {
		sess().delete(jakeObject);
	}
}