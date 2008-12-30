package com.jakeapp.core.dao;

import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.exceptions.InvalidUserIdException;
import com.jakeapp.core.dao.exceptions.NoSuchUserIdException;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.hibernate.exception.DataException;


public class HibernateUserIdDao extends HibernateDaoSupport implements IUserIdDao {
    @Override
    public UserId persist(UserId user) throws InvalidUserIdException
    {
        if(user == null)
            throw new InvalidUserIdException();

        

        this.getHibernateTemplate().save(user);
        return user;
    }

    @Override
    public List<UserId> getAll(ServiceCredentials credentials) {
        List result = this.getHibernateTemplate().find("FROM User WHERE ServiceCredentials = ? ", credentials);
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
