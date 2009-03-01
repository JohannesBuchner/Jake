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
 */
public class HibernateServiceCredentialsDao extends HibernateDaoSupport implements
		IServiceCredentialsDao {

	private static Logger log = Logger.getLogger(HibernateServiceCredentialsDao.class);


	@Override
	public ServiceCredentials create(ServiceCredentials credentials)
			throws InvalidCredentialsException {
		if (credentials == null)
			throw new InvalidCredentialsException();

		if (credentials.getUuid() == null) {
			credentials.setUuid(UUID.randomUUID());
		}
		
		if (credentials.getUserId() == null)
			throw new InvalidCredentialsException();

		if (credentials.getServerAddress() == null)
			throw new InvalidCredentialsException();

		String origpw = credentials.getPlainTextPassword();
		if (!credentials.isSavePassword()) {
			credentials.setPlainTextPassword("");
		}


		log.debug("persisting ServiceCredentials with uuid " + credentials.getUuid());
		
		// TODO: beautify. 
		try {
			getHibernateTemplate().persist(credentials);
		} catch (DataAccessException e) {
			throw new InvalidCredentialsException(e);
		}
		credentials.setPlainTextPassword(origpw);
		return credentials;
	}

	@Override
	public ServiceCredentials read(UUID uuid) throws NoSuchServiceCredentialsException {
		String s = uuid.toString();
		return read(s);
	}

	private ServiceCredentials read(String s) throws NoSuchServiceCredentialsException {
		ServiceCredentials result = (ServiceCredentials) getHibernateTemplate().get(
				ServiceCredentials.class, s);
		if (result == null)
			throw new NoSuchServiceCredentialsException();

		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ServiceCredentials> getAll() {

		List<ServiceCredentials> results = this.getHibernateTemplate()
				.getSessionFactory().getCurrentSession().createQuery(
						"FROM servicecredentials").list();

		return results;
	}

	@Override
	public ServiceCredentials update(ServiceCredentials credentials)
			throws NoSuchServiceCredentialsException {
		if (credentials == null)
			throw new InvalidCredentialsException();

		if (credentials.getUuid() == null) {
			credentials.setUuid(UUID.randomUUID());
		}
		
		if (credentials.getUserId() == null)
			throw new InvalidCredentialsException();

		if (credentials.getServerAddress() == null)
			throw new InvalidCredentialsException();

		String origpw = credentials.getPlainTextPassword();
		if (!credentials.isSavePassword()) {
			credentials.setPlainTextPassword("");
		}

		try {
			
			getHibernateTemplate().update(credentials/* , LockMode.WRITE */);
		} catch (DataAccessException e) {
			throw new NoSuchServiceCredentialsException(e);
		}

		credentials.setPlainTextPassword(origpw);
		return credentials;
	}

	@Override
	public void delete(ServiceCredentials credentials)
			throws NoSuchServiceCredentialsException {
		try {
			getHibernateTemplate().delete(credentials/* , LockMode.WRITE */);
		} catch (DataAccessException e) {
			throw new NoSuchServiceCredentialsException(e);
		}

	}


}
