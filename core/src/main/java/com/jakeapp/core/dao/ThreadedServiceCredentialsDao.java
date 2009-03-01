package com.jakeapp.core.dao;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.dao.exceptions.NoSuchServiceCredentialsException;
import java.util.UUID;
import java.util.List;
import org.hibernate.LockMode;
import com.jakeapp.core.util.InjectableTask;
import com.jakeapp.core.util.SpringThreadBroker;

public class ThreadedServiceCredentialsDao implements IServiceCredentialsDao {

	private IServiceCredentialsDao dao;

	public ThreadedServiceCredentialsDao(IServiceCredentialsDao dao) {
		this.dao = dao;
	}

	// This file was automatically generated by generateDao.sh. Do not modify. 

	/**
	 * {@inheritDoc}
	 */	
	@Override
	public ServiceCredentials create(final ServiceCredentials credentials) 			throws InvalidCredentialsException {
		
		try {
			return SpringThreadBroker.getInstance().doTask(new InjectableTask<ServiceCredentials>() {

				@Override
				public ServiceCredentials calculate() throws Exception {
					return ThreadedServiceCredentialsDao.this.dao.create(credentials);
				}
			});
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	
	}

	/**
	 * {@inheritDoc}
	 */	
	@Override
	public ServiceCredentials read(final UUID uuid) throws NoSuchServiceCredentialsException {
		
		try {
			return SpringThreadBroker.getInstance().doTask(new InjectableTask<ServiceCredentials>() {

				@Override
				public ServiceCredentials calculate() throws Exception {
					return ThreadedServiceCredentialsDao.this.dao.read(uuid);
				}
			});
		} catch (NoSuchServiceCredentialsException  e) {
			throw e;
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	
	}

	/**
	 * {@inheritDoc}
	 */	
	@Override
	public List<ServiceCredentials> getAll() {
		
		try {
			return SpringThreadBroker.getInstance().doTask(new InjectableTask<List<ServiceCredentials>>() {

				@Override
				public List<ServiceCredentials> calculate() throws Exception {
					return ThreadedServiceCredentialsDao.this.dao.getAll();
				}
			});
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	
	}

	/**
	 * {@inheritDoc}
	 */	
	@Override
	public ServiceCredentials update(final ServiceCredentials credentials) 			throws NoSuchServiceCredentialsException {
		
		try {
			return SpringThreadBroker.getInstance().doTask(new InjectableTask<ServiceCredentials>() {

				@Override
				public ServiceCredentials calculate() throws Exception {
					return ThreadedServiceCredentialsDao.this.dao.update(credentials);
				}
			});
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	
	}

	/**
	 * {@inheritDoc}
	 */	
	@Override
	public void delete(final ServiceCredentials credentials) 			throws NoSuchServiceCredentialsException {
		
		try {
			SpringThreadBroker.getInstance().doTask(new InjectableTask<Void>() {

				@Override
				public Void calculate() throws Exception {
					ThreadedServiceCredentialsDao.this.dao.delete(credentials);
					return null;
				}
			});
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	
	}


}
