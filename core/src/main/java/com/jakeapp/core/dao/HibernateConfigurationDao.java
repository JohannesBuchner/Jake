package com.jakeapp.core.dao;

import com.jakeapp.core.dao.exceptions.NoSuchConfigOptionException;
import com.jakeapp.core.domain.Configuration;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.dao.DataAccessException;
import org.apache.log4j.Logger;

import java.util.List;


/**
 * Hibernate implementation of the IConfigurationInterface.
 *
 * @author Simon
 */
public class HibernateConfigurationDao extends HibernateDaoSupport implements IConfigurationDao {
    private Logger log = Logger.getLogger(HibernateConfigurationDao.class);


    /**
     * {@inheritDoc}
     */
    @Override
    public final void deleteConfigurationValue(final String name) {
        try
        {
        this.getHibernateTemplate().getSessionFactory().getCurrentSession().
                createQuery("DELETE FROM configuration WHERE key = ? ").setString(0, name).executeUpdate();
        }
        catch (DataAccessException e)
        {
            log.debug("cought dataAccessException");
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean configurationValueExists(final String name) {
        List<String> result = this.getHibernateTemplate().getSessionFactory().getCurrentSession().
                createQuery("SELECT TRUE FROM configuration WHERE key = ? ").setString(0, name).list();

        return (result.size() > 0);
    }

    @Override
    public Configuration update(final Configuration configuration) {
        this.getHibernateTemplate().getSessionFactory().getCurrentSession().saveOrUpdate(configuration);
        return configuration;
    }

    @Override
    public List<Configuration> getAll() {
        List<Configuration> result = this.getHibernateTemplate().getSessionFactory().getCurrentSession().
                createQuery("FROM configuration").list();

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getConfigurationValue(final String name)
            throws NoSuchConfigOptionException {

        List<Configuration> result = this.getHibernateTemplate().getSessionFactory().getCurrentSession().
                createQuery("FROM configuration WHERE key = ? ").setString(0, name).list();

        if(result.size() > 0)
        {
            return result.get(0).getValue();
        }
        else
        {
            return "";
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setConfigurationValue(final String name,
                                            final String value) {
        Configuration conf = new Configuration(name, value);
        this.getHibernateTemplate().getSessionFactory().getCurrentSession().saveOrUpdate(conf);
    }

}
