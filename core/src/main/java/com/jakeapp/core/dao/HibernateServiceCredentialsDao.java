package com.jakeapp.core.dao;

import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.dao.exceptions.NoSuchServiceCredentialsException;

import java.util.UUID;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;
import org.apache.log4j.Logger;
import org.hibernate.LockMode;

/**
 * Hibernate implementation of the ServiceCredentialsDao.
 *
 */
public class HibernateServiceCredentialsDao extends HibernateDaoSupport
        implements IServiceCredentialsDao {
    private static Logger log = Logger.getLogger(HibernateServiceCredentialsDao.class);


    @Override
    /**
     * {@inheritDoc}
     */
    public ServiceCredentials create(ServiceCredentials credentials)  throws InvalidCredentialsException {
        if(credentials == null)
            throw new InvalidCredentialsException();

        if(credentials.getUuid() == null)
            throw new InvalidCredentialsException();

        if(credentials.getUserId() == null)
            throw new InvalidCredentialsException();

        if(credentials.getServerAddress() == null)
            throw new InvalidCredentialsException();

        log.debug("persisting ServiceCredentials with uuid " + credentials.getUuid());

        try
        {
          getHibernateTemplate().save(credentials);
        }
        catch(DataAccessException e)
        {
            throw new InvalidCredentialsException(e);
        }
        return credentials;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public ServiceCredentials read(UUID uuid) throws NoSuchServiceCredentialsException {
        ServiceCredentials result = (ServiceCredentials) getHibernateTemplate().get(ServiceCredentials.class, uuid.toString());
        if(result == null)
            throw new NoSuchServiceCredentialsException();

        return result;
    }

    @Transactional
    @Override
    public List<ServiceCredentials> getAll() {
       
        List<ServiceCredentials> results = this.getHibernateTemplate().getSessionFactory().getCurrentSession().
                createQuery("FROM ServiceCredentials").list();

        return results;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public ServiceCredentials update(ServiceCredentials credentials) throws NoSuchServiceCredentialsException {

        try
        {
            getHibernateTemplate().update(credentials, LockMode.WRITE);
        }
        catch(DataAccessException e)
        {
            throw new NoSuchServiceCredentialsException(e);
        }

        return credentials;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public void delete(ServiceCredentials credentials) throws NoSuchServiceCredentialsException {
        try
        {
            getHibernateTemplate().delete(credentials, LockMode.WRITE);
        }
        catch (DataAccessException e)
        {
            throw new NoSuchServiceCredentialsException(e);
        }

    }


}
