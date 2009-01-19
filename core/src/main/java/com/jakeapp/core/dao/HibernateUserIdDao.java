package com.jakeapp.core.dao;

import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.XMPPUserId;
import com.jakeapp.core.domain.exceptions.InvalidUserIdException;
import com.jakeapp.core.dao.exceptions.NoSuchUserIdException;

import java.util.List;
import java.util.UUID;
import java.util.LinkedList;
import java.util.ArrayList;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataAccessException;
import org.hibernate.exception.DataException;
import org.hibernate.LockMode;
import org.apache.log4j.Logger;


public class HibernateUserIdDao extends HibernateDaoSupport implements IUserIdDao {

    private static Logger log = Logger.getLogger(HibernateUserIdDao.class);

    @Override
    public UserId create(UserId user) throws InvalidUserIdException {
        if (user == null)
            throw new InvalidUserIdException();

        if (user.getCredentials() == null)
            throw new InvalidUserIdException();

        if (user.getProtocolType() == null)
            throw new InvalidUserIdException();

        if (user.getUuid() == null)
            throw new InvalidUserIdException();

        log.debug("Persisting User with uuid : " + user.getUuid().toString());


        this.getHibernateTemplate().save(user);
        return user;
    }

    @Override
    public UserId get(UserId user) throws InvalidUserIdException {
        log.debug("Reading user by example " + user.toString());
        List<UserId> results = this.getHibernateTemplate().findByExample(user);

        if (results.size() > 0) {
            return (UserId) results.get(0);
        } else
            throw new InvalidUserIdException("User not found");


    }

    @Override
    public UserId get(UUID uuid) throws InvalidUserIdException, NoSuchUserIdException {
        log.debug("Reading user by example uuid : " + uuid.toString());
        //UserId bla = new XMPPUserId(null,uuid,null,null,null,null);
        //List results = this.getHibernateTemplate().findByExample(bla);
        List results = this.getHibernateTemplate().find("FROM users WHERE uuid = ? ", uuid.toString());
        if (results.size() > 0) {
            return (UserId) results.get(0);
        }
        throw new NoSuchUserIdException();

    }


    @Override
    public List<UserId> getAll(ServiceCredentials credentials) {
        List<UserId> result = this.
                getHibernateTemplate().find("FROM UserId WHERE ServiceCredentials = ? ", credentials);
        if (result.size() > 0) {
            List<UserId> realResult = new LinkedList<UserId>();

            realResult.addAll(result);

            return realResult;
        } else {
            log.debug("found nothing");
//            return null;
            return result;
        }
    }

    @Override
    public UserId update(UserId userId) throws NoSuchUserIdException {
        try {
            getHibernateTemplate().update(userId);
        }
        catch (DataAccessException e) {
            e.printStackTrace();

            throw new NoSuchUserIdException();
        }

        return userId;
    }

    @Override
    public void delete(UserId user) throws NoSuchUserIdException {

        try {
            this.getHibernateTemplate().delete(user);
        }
        catch (DataException e) {
            throw new NoSuchUserIdException();
        }

    }

    @Override
    public void delete(UUID user) throws NoSuchUserIdException {
        if (user == null)
            throw new NoSuchUserIdException("uuid must not be null");


        UserId userId = null;
        try {
            userId = this.get(user);
        } catch (InvalidUserIdException e) {
            throw new NoSuchUserIdException(e);
        }

        if (userId == null)
            throw new NoSuchUserIdException("UserId with uuid " + user.toString() + " not found");
        this.getHibernateTemplate().delete(userId);
    }
}
