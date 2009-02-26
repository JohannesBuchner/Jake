/**
 * 
 */
package com.jakeapp.core.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.jakeapp.core.dao.ILogEntryDao;
import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.core.domain.Tag;
import com.jakeapp.core.domain.TrustState;
import com.jakeapp.core.domain.UserId;

public final class UnprocessedBlindLogEntryDaoProxy {

	private final ILogEntryDao innerDao;

	public UnprocessedBlindLogEntryDaoProxy(ILogEntryDao innerDao) {
		super();
		this.innerDao = innerDao;
	}

	private static final boolean includeUnprocessed = false;

	public void create(LogEntry<? extends ILogable> logEntry) {
		this.innerDao.create(logEntry);
	}

	/**
	 * @see ILogEntryDao#getAll(boolean)
	 * @return
	 */
	public List<LogEntry<? extends ILogable>> getAll() {
		return this.innerDao.getAll(includeUnprocessed);
	}

	/**
	 * @param jakeObject
	 * @see ILogEntryDao#getAllOfJakeObject(JakeObject, boolean)
	 * @return
	 */
	public <T extends JakeObject> List<LogEntry<T>> getAllOfJakeObject(T jakeObject) {
		return this.innerDao.getAllOfJakeObject(jakeObject, includeUnprocessed);
	}

	/**
	 * @see ILogEntryDao#getAllVersions(boolean)
	 * @return
	 */
	public <T extends JakeObject> List<LogEntry<T>> getAllVersions() {
		return this.innerDao.getAllVersions(includeUnprocessed);
	}

	/**
	 * @param jakeObject
	 * @see ILogEntryDao#getAllVersionsOfJakeObject(JakeObject, boolean)
	 * @return
	 */
	public <T extends JakeObject> List<LogEntry<T>> getAllVersionsOfJakeObject(
			T jakeObject) {
		return this.innerDao.getAllVersionsOfJakeObject(jakeObject, includeUnprocessed);
	}

	/**
	 * @see ILogEntryDao#getCurrentProjectMembers()
	 * @return
	 */
	public List<UserId> getCurrentProjectMembers() {
		return this.innerDao.getCurrentProjectMembers();
	}

	/**
	 * @param belongsTo
	 * @see ILogEntryDao#getTags(JakeObject)
	 * @return
	 */
	public Collection<Tag> getCurrentTags(JakeObject belongsTo) {
		return this.innerDao.getTags(belongsTo);
	}

	/**
	 * @param jakeObject
	 * @see ILogEntryDao#getDeleteState(JakeObject, boolean)
	 * @return
	 */
	public Boolean getDeleteState(JakeObject jakeObject) {
		return this.innerDao.getDeleteState(jakeObject, includeUnprocessed);
	}

	/**
	 * @see ILogEntryDao#getExistingFileObjects(boolean)
	 * @return
	 */
	public Iterable<FileObject> getExistingFileObjects() {
		return this.innerDao.getExistingFileObjects(includeUnprocessed);
	}

	/**
	 * @param jakeObject
	 * @see ILogEntryDao#getLastOfJakeObject(JakeObject, boolean)
	 * @throws com.jakeapp.core.dao.exceptions.NoSuchLogEntryException
	 * @return
	 */
	public LogEntry<JakeObject> getLastOfJakeObject(JakeObject jakeObject)
			throws NoSuchLogEntryException {
		return this.innerDao.getLastOfJakeObject(jakeObject, includeUnprocessed);
	}

	/**
	 * @param jakeObject
	 * @see ILogEntryDao#getLastVersion(JakeObject, boolean)
	 * @return
	 */
	public LogEntry<JakeObject> getLastVersion(JakeObject jakeObject) {
		return this.innerDao.getLastVersion(jakeObject, includeUnprocessed);
	}

	/**
	 * @param jakeObject
	 * @see ILogEntryDao#getLastVersionOfJakeObject(JakeObject, boolean)
	 * @throws com.jakeapp.core.dao.exceptions.NoSuchLogEntryException
	 * @return
	 */
	public LogEntry<JakeObject> getLastVersionOfJakeObject(JakeObject jakeObject)
			throws NoSuchLogEntryException {
		return this.innerDao.getLastVersionOfJakeObject(jakeObject, includeUnprocessed);
	}

	/**
	 * @param belongsTo
	 * @see ILogEntryDao#getLock(JakeObject)
	 * @return
	 */
	public LogEntry<JakeObject> getLock(JakeObject belongsTo) {
		return this.innerDao.getLock(belongsTo);
	}

	/**
	 * @see ILogEntryDao#getProjectCreatedEntry()
	 * @return
	 */
	public LogEntry<? extends ILogable> getProjectCreatedEntry() {
		return this.innerDao.getProjectCreatedEntry();
	}
	
	/**
	 * @see ILogEntryDao#getTrustGraph()
	 * @return
	 */
	public Map<UserId, List<UserId>> getTrustGraph() {
		return this.innerDao.getTrustGraph();
	}

	/**
	 * @param a
	 * @param b
	 * @see ILogEntryDao#trusts(ProjectMember, ProjectMember)
	 * @return
	 */
	public Boolean trusts(UserId a, UserId b) {
		return this.innerDao.trusts(a, b);
	}

	/**
	 * @ see ILogEntryDao
	 * @param a
	 * @param b
	 * @return
	 */
	public TrustState trustsHow(UserId a, UserId b) {
		return this.innerDao.trustsHow(a, b);
	}

	/**
	 * @param a
	 * @see ILogEntryDao#trusts(ProjectMember)
	 * @return
	 */
	@Deprecated
	public Collection<UserId> trusts(UserId a) {
		return this.innerDao.trusts(a);
	}

}