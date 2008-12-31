package com.jakeapp.core.dao;

import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.XMPPUserId;
import com.jakeapp.core.domain.exceptions.InvalidUserIdException;
import com.jakeapp.core.dao.exceptions.NoSuchUserIdException;

import java.util.List;
import java.util.UUID;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.exception.DataException;
import org.apache.log4j.Logger;


public class HibernateUserIdDao extends HibernateDaoSupport implements IUserIdDao {

    private static Logger log = Logger.getLogger(HibernateUserIdDao.class);

    @Override
    @Transactional
    public UserId persist(UserId user) throws InvalidUserIdException
    {
        if(user == null)
            throw new InvalidUserIdException();

        log.debug("Persisting User with uuid : " + user.getUuid().toString());
        

        this.getHibernateTemplate().save(user);
        return user;
    }

    @Override
    public UserId read(UserId user) throws InvalidUserIdException {
        log.debug("Reading user by example " + user.toString());
        return (UserId) this.getHibernateTemplate().findByExample(user);
    }

    @Override
    public UserId read(UUID uuid) throws InvalidUserIdException, NoSuchUserIdException {
        log.debug("Reading user by example uuid : " + uuid.toString());
        //UserId bla = new XMPPUserId(null,uuid,null,null,null,null);
        //List results = this.getHibernateTemplate().findByExample(bla);
        List results = this.getHibernateTemplate().find("FROM users WHERE uuid = ? ", uuid.toString());
        if(results.size() > 0)
        {
            return (UserId) results.get(0);
        }
        throw new NoSuchUserIdException(); 

    }


    @Override
    public List<UserId> getAll(ServiceCredentials credentials) {
        List result = this.getHibernateTemplate().find("FROM UserId WHERE ServiceCredentials = ? ", credentials);
        if(result.size() > 0)
        {
            System.out.println("found something");
        }
        else
        {
            System.out.println("found nothing");
        }
        return null; // TODO
    }

    @Override
    public void delete(UserId user) throws NoSuchUserIdException {
        // TODO

        try
        {
            this.getHibernateTemplate().delete(user);
        }
        catch(DataException e)
        {
            throw new NoSuchUserIdException(); 
        }

    }
}
