package com.jakeapp.core.dao;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.domain.*;
import com.jakeapp.core.domain.logentries.LogEntry;
import com.jakeapp.core.util.InjectableTask;
import com.jakeapp.core.util.SpringThreadBroker;

public class ThreadedLogEntryDao implements ILogEntryDao {

	private ILogEntryDao dao;

	public ThreadedLogEntryDao(ILogEntryDao dao) {
		this.dao = dao;
	}

	// This file was automatically generated by generateDao.sh. Do not modify. 

	/**
	 * {@inheritDoc}
	 */	
	@Override
	public void create(final LogEntry<? extends ILogable> logEntry) {
		
		try {
			SpringThreadBroker.getThreadForObject(this).doTask(new InjectableTask<Void>() {

				@Override
				public Void calculate() throws Exception {
					ThreadedLogEntryDao.this.dao.create(logEntry);
					return null;
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
	public LogEntry<? extends ILogable> get(final UUID uuid, final boolean includeUnprocessed) 			throws NoSuchLogEntryException {
		
		try {
			return SpringThreadBroker.getThreadForObject(this).doTask(new InjectableTask<LogEntry<? extends ILogable>>() {

				@Override
				public LogEntry<? extends ILogable> calculate() throws Exception {
					return ThreadedLogEntryDao.this.dao.get(uuid, includeUnprocessed);
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
	public void setProcessed(final LogEntry<JakeObject> logEntry) throws NoSuchLogEntryException {
		
		try {
			SpringThreadBroker.getThreadForObject(this).doTask(new InjectableTask<Void>() {

				@Override
				public Void calculate() throws Exception {
					ThreadedLogEntryDao.this.dao.setProcessed(logEntry);
					return null;
				}
			});
		} catch (NoSuchLogEntryException  e) {
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
	public boolean hasUnprocessed(final JakeObject jakeObject) {
		
		try {
			return SpringThreadBroker.getThreadForObject(this).doTask(new InjectableTask<Boolean>() {

				@Override
				public Boolean calculate() throws Exception {
					return ThreadedLogEntryDao.this.dao.hasUnprocessed(jakeObject);
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
	public List<LogEntry<JakeObject>> getUnprocessed(final JakeObject jakeObject) {
		
		try {
			return SpringThreadBroker.getThreadForObject(this).doTask(new InjectableTask<List<LogEntry<JakeObject>>>() {

				@Override
				public List<LogEntry<JakeObject>> calculate() throws Exception {
					return ThreadedLogEntryDao.this.dao.getUnprocessed(jakeObject);
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
	public List<LogEntry<JakeObject>> getUnprocessed() {
		
		try {
			return SpringThreadBroker.getThreadForObject(this).doTask(new InjectableTask<List<LogEntry<JakeObject>>>() {

				@Override
				public List<LogEntry<JakeObject>> calculate() throws Exception {
					return ThreadedLogEntryDao.this.dao.getUnprocessed();
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
	public LogEntry<JakeObject> getNextUnprocessed() throws NoSuchLogEntryException {
		
		try {
			return SpringThreadBroker.getThreadForObject(this).doTask(new InjectableTask<LogEntry<JakeObject>>() {

				@Override
				public LogEntry<JakeObject> calculate() throws Exception {
					return ThreadedLogEntryDao.this.dao.getNextUnprocessed();
				}
			});
		} catch (NoSuchLogEntryException  e) {
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
	public List<LogEntry<? extends ILogable>> getAll(final boolean includeUnprocessed) {
		
		try {
			return SpringThreadBroker.getThreadForObject(this).doTask(new InjectableTask<List<LogEntry<? extends ILogable>>>() {

				@Override
				public List<LogEntry<? extends ILogable>> calculate() throws Exception {
					return ThreadedLogEntryDao.this.dao.getAll(includeUnprocessed);
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
	public <T extends JakeObject> List<LogEntry<T>> getAllOfJakeObject(final T jakeObject, final 			boolean includeUnprocessed) {
		
		try {
			return SpringThreadBroker.getThreadForObject(this).doTask(new InjectableTask<List<LogEntry<T>>>() {

				@Override
				public List<LogEntry<T>> calculate() throws Exception {
					return ThreadedLogEntryDao.this.dao.getAllOfJakeObject(jakeObject, includeUnprocessed);
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
	public LogEntry<JakeObject> getLastOfJakeObject(final JakeObject jakeObject, final 			boolean includeUnprocessed) throws NoSuchLogEntryException {
		
		try {
			return SpringThreadBroker.getThreadForObject(this).doTask(new InjectableTask<LogEntry<JakeObject>>() {

				@Override
				public LogEntry<JakeObject> calculate() throws Exception {
					return ThreadedLogEntryDao.this.dao.getLastOfJakeObject(jakeObject, includeUnprocessed);
				}
			});
		} catch (NoSuchLogEntryException  e) {
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
	public <T extends JakeObject> List<LogEntry<T>> getAllVersions(final  			boolean includeUnprocessed) {
		
		try {
			return SpringThreadBroker.getThreadForObject(this).doTask(new InjectableTask<List<LogEntry<T>>>() {

				@Override
				public List<LogEntry<T>> calculate() throws Exception {
					return ThreadedLogEntryDao.this.dao.getAllVersions(includeUnprocessed);
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
	public <T extends JakeObject> List<LogEntry<T>> getAllVersionsOfJakeObject(final  			T jakeObject, final boolean includeUnprocessed) {
		
		try {
			return SpringThreadBroker.getThreadForObject(this).doTask(new InjectableTask<List<LogEntry<T>>>() {

				@Override
				public List<LogEntry<T>> calculate() throws Exception {
					return ThreadedLogEntryDao.this.dao.getAllVersionsOfJakeObject(jakeObject, includeUnprocessed);
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
	public LogEntry<JakeObject> getLastVersionOfJakeObject(final JakeObject jakeObject, final 			boolean includeUnprocessed) throws NoSuchLogEntryException {
		
		try {
			return SpringThreadBroker.getThreadForObject(this).doTask(new InjectableTask<LogEntry<JakeObject>>() {

				@Override
				public LogEntry<JakeObject> calculate() throws Exception {
					return ThreadedLogEntryDao.this.dao.getLastVersionOfJakeObject(jakeObject, includeUnprocessed);
				}
			});
		} catch (NoSuchLogEntryException  e) {
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
	public Boolean getDeleteState(final JakeObject belongsTo, final boolean includeUnprocessed) {
		
		try {
			return SpringThreadBroker.getThreadForObject(this).doTask(new InjectableTask<Boolean>() {

				@Override
				public Boolean calculate() throws Exception {
					return ThreadedLogEntryDao.this.dao.getDeleteState(belongsTo, includeUnprocessed);
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
	public LogEntry<JakeObject> getLastVersion(final JakeObject belongsTo, final 			boolean includeUnprocessed) {
		
		try {
			return SpringThreadBroker.getThreadForObject(this).doTask(new InjectableTask<LogEntry<JakeObject>>() {

				@Override
				public LogEntry<JakeObject> calculate() throws Exception {
					return ThreadedLogEntryDao.this.dao.getLastVersion(belongsTo, includeUnprocessed);
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
	public List<FileObject> getExistingFileObjects(final boolean includeUnprocessed) {
		
		try {
			return SpringThreadBroker.getThreadForObject(this).doTask(new InjectableTask<List<FileObject>>() {

				@Override
				public List<FileObject> calculate() throws Exception {
					return ThreadedLogEntryDao.this.dao.getExistingFileObjects(includeUnprocessed);
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
	public LogEntry<JakeObject> getLock(final JakeObject jakeObject) {
		
		try {
			return SpringThreadBroker.getThreadForObject(this).doTask(new InjectableTask<LogEntry<JakeObject>>() {

				@Override
				public LogEntry<JakeObject> calculate() throws Exception {
					return ThreadedLogEntryDao.this.dao.getLock(jakeObject);
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
	public Collection<Tag> getTags(final JakeObject jakeObject) {
		
		try {
			return SpringThreadBroker.getThreadForObject(this).doTask(new InjectableTask<Collection<Tag>>() {

				@Override
				public Collection<Tag> calculate() throws Exception {
					return ThreadedLogEntryDao.this.dao.getTags(jakeObject);
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
	public LogEntry<? extends ILogable> getProjectCreatedEntry() {
		
		try {
			return SpringThreadBroker.getThreadForObject(this).doTask(new InjectableTask<LogEntry<? extends ILogable>>() {

				@Override
				public LogEntry<? extends ILogable> calculate() throws Exception {
					return ThreadedLogEntryDao.this.dao.getProjectCreatedEntry();
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
	public List<User> getCurrentProjectMembers() {
		
		try {
			return SpringThreadBroker.getThreadForObject(this).doTask(new InjectableTask<List<User>>() {

				@Override
				public List<User> calculate() throws Exception {
					return ThreadedLogEntryDao.this.dao.getCurrentProjectMembers();
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
	public boolean trusts(final User a, final User b) {
		
		try {
			return SpringThreadBroker.getThreadForObject(this).doTask(new InjectableTask<Boolean>() {

				@Override
				public Boolean calculate() throws Exception {
					return ThreadedLogEntryDao.this.dao.trusts(a, b);
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
	public TrustState trustsHow(final User a, final User b) {
		
		try {
			return SpringThreadBroker.getThreadForObject(this).doTask(new InjectableTask<TrustState>() {

				@Override
				public TrustState calculate() throws Exception {
					return ThreadedLogEntryDao.this.dao.trustsHow(a, b);
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
	public Map<User, TrustState> trustsHow(final User a) {
		
		try {
			return SpringThreadBroker.getThreadForObject(this).doTask(new InjectableTask<Map<User, TrustState>>() {

				@Override
				public Map<User, TrustState> calculate() throws Exception {
					return ThreadedLogEntryDao.this.dao.trustsHow(a);
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
	public Map<User, List<User>> getTrustGraph() {
		
		try {
			return SpringThreadBroker.getThreadForObject(this).doTask(new InjectableTask<Map<User, List<User>>>() {

				@Override
				public Map<User, List<User>> calculate() throws Exception {
					return ThreadedLogEntryDao.this.dao.getTrustGraph();
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
	public Map<User, Map<User, TrustState>> getExtendedTrustGraph() {
		
		try {
			return SpringThreadBroker.getThreadForObject(this).doTask(new InjectableTask<Map<User, Map<User, TrustState>>>() {

				@Override
				public Map<User, Map<User, TrustState>> calculate() throws Exception {
					return ThreadedLogEntryDao.this.dao.getExtendedTrustGraph();
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
	public void setAllPreviousProcessed(final LogEntry<? extends ILogable> logEntry) {
		
		try {
			SpringThreadBroker.getThreadForObject(this).doTask(new InjectableTask<Void>() {

				@Override
				public Void calculate() throws Exception {
					ThreadedLogEntryDao.this.dao.setAllPreviousProcessed(logEntry);
					return null;
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
	public void acceptInvitation(final Invitation invitation) {
		
		try {
			SpringThreadBroker.getThreadForObject(this).doTask(new InjectableTask<Void>() {

				@Override
				public Void calculate() throws Exception {
					ThreadedLogEntryDao.this.dao.acceptInvitation(invitation);
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
