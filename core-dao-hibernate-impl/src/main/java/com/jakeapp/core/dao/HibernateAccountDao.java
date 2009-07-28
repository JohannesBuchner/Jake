package com.jakeapp.core.dao;

import com.jakeapp.core.domain.Account;
import com.jakeapp.core.domain.ProtocolType;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.dao.exceptions.NoSuchServiceCredentialsException;

import java.util.UUID;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.apache.log4j.Logger;

/**
 * Hibernate implementation of the IAccountDao.
 */
public class HibernateAccountDao extends HibernateDaoSupport implements
		IAccountDao {

	private static Logger log = Logger.getLogger(HibernateAccountDao.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Account create(final Account credentials)
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


		Account persistingAccount = null;

		try {
			persistingAccount = credentials.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Account could not be cloned");
		}


		if (!credentials.isSavePassword()) {
			persistingAccount.setPlainTextPassword("");
		}
		log.debug("persisting Account with uuid " + credentials.getUuid());

		// TODO: beautify. 
		try {
			getHibernateTemplate().persist(persistingAccount);
		} catch (DataAccessException e) {
			throw new InvalidCredentialsException(e);
		}

		return credentials;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Account read(UUID uuid) throws NoSuchServiceCredentialsException {
		String s = uuid.toString();
		return read(s);
	}

	private Account read(String s) throws NoSuchServiceCredentialsException {
		Account result = (Account) getHibernateTemplate().get(
				Account.class, s);
		if (result == null)
			throw new NoSuchServiceCredentialsException();

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Account> getAll() {

		List<Account> results = this.getHibernateTemplate()
				.getSessionFactory().getCurrentSession().createQuery(
						"FROM servicecredentials").list();

		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Account update(Account credentials)
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(Account credentials)
			throws NoSuchServiceCredentialsException {
		try {
			getHibernateTemplate().delete(credentials/* , LockMode.WRITE */);
		} catch (DataAccessException e) {
			throw new NoSuchServiceCredentialsException(e);
		}

	}


}
