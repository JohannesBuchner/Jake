package com.jakeapp.core.dao;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.Tag;
import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;

import java.util.List;
import java.util.UUID;
import java.lang.reflect.ParameterizedType;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.apache.log4j.Logger;

/**
 * A generic hibernate DAO for <code>JakeObject</code>s.
 *
 * @param <T> The type of the persisted Entity, subtype of
 * <code>JakeObject</code>
 */
@Repository
public abstract class HibernateJakeObjectDao<T extends JakeObject>
        extends HibernateDaoSupport implements IJakeObjectDao<T> {

    private static Logger log = Logger.getLogger(HibernateJakeObjectDao.class);

    /**
     * {@inheritDoc}
     */
    public T persist(T jakeObject) {

        this.getHibernateTemplate().getSessionFactory().getCurrentSession().save(jakeObject);

        return jakeObject;
    }

    /**
     * {@inheritDoc}
     */
    public T get(UUID objectId) throws NoSuchJakeObjectException  
    {
        log.debug("getAll(Project) Test ========================================");

        ParameterizedType parameterizedType = (ParameterizedType) this.getClass().getGenericSuperclass();
        String parameterName = ((Class) parameterizedType.getActualTypeArguments()[0]).getName();
        log.debug("xxx: " + parameterName);


        String queryString = "FROM " + parameterName + " WHERE objectId = ? ";

        List<T> results = this.getHibernateTemplate().getSessionFactory().getCurrentSession().
                createQuery(queryString).setString(0, objectId.toString()).list();

        if(results.size() > 0)
             return (T) results.get(0);

        throw new NoSuchJakeObjectException("jakeobject with uuid " +  objectId.toString() +"not found");
    }


    /**
     * {@inheritDoc}
     */
    public List<T> getAll() {
        log.debug("getAll(Project) Test ========================================");

        ParameterizedType parameterizedType = (ParameterizedType) this.getClass().getGenericSuperclass();
        String parameterName = ((Class) parameterizedType.getActualTypeArguments()[0]).getName();
        log.debug("xxx: " + parameterName);


        String queryString = "FROM " + parameterName + " ";

        List<T> results = this.getHibernateTemplate().getSessionFactory().getCurrentSession().
                createQuery(queryString).list();

        return results;
    }

    /**
     * {@inheritDoc}
     */
    public void delete(T jakeObject) {       
        this.getHibernateTemplate().getSessionFactory().getCurrentSession().delete(jakeObject);
    }
/*
    *//**
     * {@inheritDoc}
     *//*
    public List<Tag> getTags(T jakeObject) {


        String query = "FROM Tag WHERE object = ?";

        List<Tag> results = this.getHibernateTemplate().getSessionFactory().getCurrentSession().
                createQuery(query).setString(0, jakeObject.getUuid().toString()).list();

        return results;
    }*/

    /**
     * {@inheritDoc}
     */
    public T addTagTo(T jakeObject, Tag tag) {

        log.debug("Adding object "+ jakeObject + " to tag " + tag);
        tag.setObject(jakeObject);

        log.debug("persisting tag");
        this.getHibernateTemplate().getSessionFactory().getCurrentSession().persist(tag);

        return jakeObject;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void addTagsTo(T jakeObject, Tag... tags) {
         for(Tag t : tags)
         {
             t.setObject(jakeObject);
             this.getHibernateTemplate().getSessionFactory().getCurrentSession().persist(t);
         }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tag> getTagsFor(T jakeObject) {

        List<Tag> results = this.getHibernateTemplate().getSessionFactory().getCurrentSession().
                createQuery("FROM tag WHERE objectid = ? ").setString(0, jakeObject.getUuid().toString()).list();


                //createCriteria(Tag.class).createCriteria("object", jakeObject.getUuid().toString()).list();

                //createQuery("FROM Tag WHERE jakeObject = ? ").setEntity(0, jakeObject).list();

        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeTagsFrom(T jakeObject, Tag... tags) {
        
        for(Tag t : tags)
        {
            t.setObject(jakeObject);
            
             int result = this.getHibernateTemplate().getSessionFactory().getCurrentSession().
//                     createQuery("DELETE FROM tag WHERE text = ? AND objectid = ? ").
                createSQLQuery("DELETE FROM tag WHERE text = ? AND objectid = ? ").
                     setString(0, t.getName()).setString(1, jakeObject.getUuid().toString()).executeUpdate();
            log.debug("result: " + result);
        }
	}


    /**
     * {@inheritDoc}
     */
    public T removeTagFrom(T jakeObject, Tag tag) {

        tag.setObject(jakeObject);
        
        this.getHibernateTemplate().getSessionFactory().getCurrentSession().
                createSQLQuery("DELETE FROM tag WHERE text = ? AND objectid = ? ").
                setString(0, tag.getName()).setString(1, jakeObject.getUuid().toString()).executeUpdate();

        return jakeObject;
    }

}
