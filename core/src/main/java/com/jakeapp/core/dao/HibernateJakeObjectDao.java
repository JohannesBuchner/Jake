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

	/*
    *//**
	 * {@inheritDoc}
	 */
	/*
	 * public List<Tag> getTags(T jakeObject) {
	 * 
	 * 
	 * String query = "FROM Tag WHERE object = ?";
	 * 
	 * List<Tag> results =
	 * this.getHibernateTemplate().getSessionFactory().getCurrentSession().
	 * createQuery(query).setString(0, jakeObject.getUuid().toString()).list();
	 * 
	 * return results; }
	 */

	/**
	 * {@inheritDoc}
	 */
	public T addTagTo(final T jakeObject, final Tag tag) {

		log.debug("Adding object " + jakeObject + " to tag " + tag);
		tag.setObject(jakeObject);

		log.debug("persisting tag");
		sess().persist(tag);

		return jakeObject;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addTagsTo(final T jakeObject, final Tag... tags) {
		for (Tag t : tags) {
			t.setObject(jakeObject);
			sess().persist(t);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Tag> getTagsFor(final T jakeObject) {

		List<Tag> results = sess().createQuery("FROM tag WHERE objectid = ? ").setString(
				0, jakeObject.getUuid().toString()).list();


		// createCriteria(Tag.class).createCriteria("object",
		// jakeObject.getUuid().toString()).list();

		// createQuery("FROM Tag WHERE jakeObject = ? ").setEntity(0,
		// jakeObject).list();

		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeTagsFrom(final T jakeObject, final Tag... tags) {

		for (Tag t : tags) {
			t.setObject(jakeObject);

			int result = sess()
					.
					// createQuery(
					// "DELETE FROM tag WHERE text = ? AND objectid = ? ").
					createSQLQuery("DELETE FROM tag WHERE text = ? AND objectid = ? ")
					.setString(0, t.getName()).setString(1,
							jakeObject.getUuid().toString()).executeUpdate();
			log.debug("result: " + result);
		}
	}


	/**
	 * {@inheritDoc}
	 */
	public T removeTagFrom(final T jakeObject, final Tag tag) {

		tag.setObject(jakeObject);

		sess().createSQLQuery("DELETE FROM tag WHERE text = ? AND objectid = ? ")
				.setString(0, tag.getName())
				.setString(1, jakeObject.getUuid().toString()).executeUpdate();

		return jakeObject;
	}
}
