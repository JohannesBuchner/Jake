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

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.classic.Session;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.logentries.LogEntry;
import com.jakeapp.core.domain.TrustState;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.Tag;

public class HibernateLogEntryDao extends HibernateDaoSupport implements ILogEntryDao {

	private static Logger log = Logger.getLogger(HibernateLogEntryDao.class);

	private Session sess() {
		return this.getHibernateTemplate().getSessionFactory().getCurrentSession();
	}

	private Query query(String query) {
		return unsortedQuery(query + " ORDER BY time asc, id asc");
	}

	private Query unsortedQuery(String query) {
		// log.debug("query:" + query);
		debugDump();
		return sess().createQuery(query);
	}

	// @SuppressWarnings("unchecked")
	private void debugDump() {
		// log.debug("Current LogEntries: ");
		// for(LogEntry le : (List<LogEntry<? extends
		// ILogable>>)sess().createQuery
		// ("FROM logentries ORDER by timestamp asc, id asc").list()) {
		// log.debug(le);
		// }
		// log.debug("Current LogEntries done ");
	}

	@Override
	public void create(LogEntry<? extends ILogable> logEntry) {
		log.debug("create:" + logEntry);
		debugDump();
		if (!logEntry.isProcessed()) {
			switch (logEntry.getLogAction()) {
				case JAKE_OBJECT_DELETE:
				case JAKE_OBJECT_NEW_VERSION:
					break;
				default:
					logEntry.setProcessed(true);
			}
		}
		switch (logEntry.getLogAction()) {
			case JAKE_OBJECT_DELETE:
			case JAKE_OBJECT_NEW_VERSION:
			case JAKE_OBJECT_LOCK:
			case JAKE_OBJECT_UNLOCK:
			case TAG_ADD:
			case TAG_REMOVE:
				if (logEntry.getObjectuuid() == null) {
					// functions need this for indexing / lazy deserialization
					throw new IllegalArgumentException("ObjectUUID index not set!");
				}
				break;
			default:
				if (logEntry.getObjectuuid() != null) {
					// other types are very very seldom, and we need the object
					// anyway. so no index there.
					throw new IllegalArgumentException(
							"ObjectUUID index should not be set!");
				}
				break;
		}
		sess().persist(logEntry);
		debugDump();
	}

	@SuppressWarnings("unchecked")
	@Override
	public LogEntry<? extends ILogable> get(UUID uuid, boolean includeUnprocessed)
			throws NoSuchLogEntryException {
		List<LogEntry<? extends ILogable>> result = processedAwareLogEntryQuery(
				"AND id = ?", includeUnprocessed).setString(0, uuid.toString()).list();

		if (result.size() > 0) {
			return result.get(0);
		} else {
			throw new NoSuchLogEntryException();
		}
	}


	@Override
	public void setProcessed(LogEntry<JakeObject> logEntry) {
		debugDump();
		log.debug("setProcessed:" + logEntry);
		logEntry.setProcessed(true);
		sess().update(logEntry);
		debugDump();
	}

	@Override
	public boolean hasUnprocessed(JakeObject jakeObject) {
		return getUnprocessed(jakeObject).size() > 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LogEntry<JakeObject>> getUnprocessed(JakeObject jakeObject) {
		if (jakeObject.getUuid() == null)
			return new LinkedList<LogEntry<JakeObject>>();
		return query("FROM logentries WHERE processed = false AND objectuuid = ?")
				.setString(0, jakeObject.getUuid().toString()).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LogEntry<JakeObject>> getUnprocessed() {
		return query("FROM logentries WHERE processed = false").list();
	}

	@Override
	public LogEntry<JakeObject> getNextUnprocessed() throws NoSuchLogEntryException {
		return topLogEntry(getUnprocessed());
	}

	private Query processedAwareLogEntryQuery(String whereclause,
			boolean includeUnprocessed) {
		String query;
		if (!includeUnprocessed)
			query = "FROM logentries WHERE processed = true ";
		else
			query = "FROM logentries WHERE 1 = 1 ";
		return query(query + whereclause);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LogEntry<? extends ILogable>> getAll(boolean includeUnprocessed) {
		return processedAwareLogEntryQuery("", includeUnprocessed).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends JakeObject> List<LogEntry<T>> getAllOfJakeObject(T jakeObject,
			boolean includeUnprocessed) {
		if (jakeObject.getUuid() == null)
			return new LinkedList<LogEntry<T>>();
		return processedAwareLogEntryQuery("AND objectuuid = ?", includeUnprocessed)
				.setString(0, jakeObject.getUuid().toString()).list();
	}

	@Override
	public LogEntry<JakeObject> getLastOfJakeObject(JakeObject jakeObject,
			boolean includeUnprocessed) throws NoSuchLogEntryException {
		return lastLogEntry(getAllOfJakeObject(jakeObject, includeUnprocessed));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends JakeObject> List<LogEntry<T>> getAllVersions(
			boolean includeUnprocessed) {
		return processedAwareLogEntryQuery("AND (action = ? OR action = ?)",
				includeUnprocessed).setInteger(0,
				LogAction.JAKE_OBJECT_NEW_VERSION.ordinal()).setInteger(1,
				LogAction.JAKE_OBJECT_DELETE.ordinal()).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends JakeObject> List<LogEntry<T>> getAllVersionsOfJakeObject(
			T jakeObject, boolean includeUnprocessed) {
		if (jakeObject.getUuid() == null)
			return new LinkedList<LogEntry<T>>();
		return processedAwareLogEntryQuery("AND objectuuid = ? AND action = ?",
				includeUnprocessed).setString(0, jakeObject.getUuid().toString())
				.setInteger(1, LogAction.JAKE_OBJECT_NEW_VERSION.ordinal()).list();
	}



	@SuppressWarnings("unchecked")
	private <T extends JakeObject> List<LogEntry<T>> getAllDeletesOfJakeObject(
			T jakeObject, boolean includeUnprocessed) {
		if (jakeObject.getUuid() == null)
			return new LinkedList<LogEntry<T>>();
		return processedAwareLogEntryQuery("AND objectuuid = ? AND action = ?",
				includeUnprocessed).setString(0, jakeObject.getUuid().toString())
				.setInteger(1, LogAction.JAKE_OBJECT_DELETE.ordinal()).list();
	}

	/**
	 * @param list
	 * @return the last of the list
	 * @throws NoSuchLogEntryException
	 *             if the list is empty
	 */
	private <T extends ILogable> LogEntry<T> topLogEntry(List<LogEntry<T>> list)
			throws NoSuchLogEntryException {
		if (list.size() == 0)
			throw new NoSuchLogEntryException();
		return list.get(0);
	}

	/**
	 * @param list
	 * @return the first of the list
	 * @throws NoSuchLogEntryException
	 *             if the list is empty
	 */
	@SuppressWarnings("unused")
	private LogEntry<? extends ILogable> topLogEntryMixed(
			List<LogEntry<? extends ILogable>> list) throws NoSuchLogEntryException {
		if (list.size() == 0)
			throw new NoSuchLogEntryException();
		return list.get(0);
	}

	/**
	 * @param list
	 * @return the last of the list
	 * @throws NoSuchLogEntryException
	 *             if the list is empty
	 */
	private LogEntry<? extends ILogable> lastLogEntryMixed(
			List<LogEntry<? extends ILogable>> list) throws NoSuchLogEntryException {
		if (list.size() == 0)
			throw new NoSuchLogEntryException();
		return list.get(list.size() - 1);
	}

	/**
	 * @param list
	 * @return the last of the list
	 * @throws NoSuchLogEntryException
	 *             if the list is empty
	 */
	private <T extends ILogable> LogEntry<T> lastLogEntry(List<LogEntry<T>> list)
			throws NoSuchLogEntryException {
		if (list.size() == 0)
			throw new NoSuchLogEntryException();
		return list.get(list.size() - 1);
	}

	/**
	 * @param list
	 * @return the last of the list, or null if empty
	 */
	private <T extends ILogable> LogEntry<T> lastLogEntryOrNull(List<LogEntry<T>> list) {
		if (list.size() == 0)
			return null;
		return list.get(list.size() - 1);
	}

	@Override
	public LogEntry<JakeObject> getLastVersionOfJakeObject(JakeObject jakeObject,
			boolean includeUnprocessed) throws NoSuchLogEntryException {
		return lastLogEntry(getAllVersionsOfJakeObject(jakeObject, includeUnprocessed));
	}


	@Override
	public Boolean getDeleteState(JakeObject belongsTo, boolean includeUnprocessed) {
		LogEntry<JakeObject> leNew = lastLogEntryOrNull(getAllVersionsOfJakeObject(
				belongsTo, includeUnprocessed));
		LogEntry<JakeObject> leDel = lastLogEntryOrNull(getAllDeletesOfJakeObject(
				belongsTo, includeUnprocessed));
		if (leNew == null && leDel == null) {
			return null;
		}
		if (leNew == null) { // weird, but we support it
			log.info("There is a delete LogEntry but no new_version Entry for "
					+ belongsTo + ". No problem, just saying.");
			return true;
		}
		if (leDel == null) {
			return false;
		}
		// both exist
		return leDel.getTimestamp().getTime() > leNew.getTimestamp().getTime();
	}

	@Override
	public LogEntry<JakeObject> getLastVersion(JakeObject belongsTo,
			boolean includeUnprocessed) {
		LogEntry<JakeObject> leNew = lastLogEntryOrNull(getAllVersionsOfJakeObject(
				belongsTo, includeUnprocessed));
		LogEntry<JakeObject> leDel = lastLogEntryOrNull(getAllDeletesOfJakeObject(
				belongsTo, includeUnprocessed));
		if (leNew == null && leDel == null) {
			return null;
		}
		if (leNew == null) { // weird, but we support it
			log.info("There is a delete LogEntry but no new_version Entry for "
					+ belongsTo + ". No problem, just saying.");
			return null;
		}
		if (leDel == null) {
			return leNew;
		}
		// both exist
		if (leDel.getTimestamp().getTime() > leNew.getTimestamp().getTime()) {
			return null;
		} else {
			return leNew;
		}
	}

	@Override
	public List<FileObject> getExistingFileObjects(boolean includeUnprocessed) {
		Collection<LogEntry<JakeObject>> all = getAllVersions(includeUnprocessed);
		Map<FileObject, Boolean> existState = new HashMap<FileObject, Boolean>();
		for (LogEntry<JakeObject> entry : all) {
			JakeObject jo = entry.getBelongsTo();
			if (jo instanceof FileObject
					&& entry.getLogAction() == LogAction.JAKE_OBJECT_NEW_VERSION) {
				existState.put((FileObject) jo, true);
			}
			if (jo instanceof FileObject
					&& entry.getLogAction() == LogAction.JAKE_OBJECT_DELETE) {
				existState.put((FileObject) jo, false);
			}
		}
		for (JakeObject k : existState.keySet()) {
			if (!existState.get(k))
				existState.remove(k);
		}
		return new LinkedList<FileObject>(existState.keySet());
	}

	/**
	 * finds all that match any of the two LogActions
	 * LogAction.START_TRUSTING_PROJECTMEMBER and
	 * LogAction.STOP_TRUSTING_PROJECTMEMBER that belong to the given
	 * JakeObject, sorted ascending by timestamp
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Collection<LogEntry<UserId>> getAllProjectMemberLogEntries() {
		return query("FROM logentries WHERE (action = ? OR action = ? OR action = ?)")
				.setInteger(0, LogAction.START_TRUSTING_PROJECTMEMBER.ordinal())
				.setInteger(1, LogAction.STOP_TRUSTING_PROJECTMEMBER.ordinal())
				.setInteger(2, LogAction.STOP_TRUSTING_PROJECTMEMBER.ordinal()).list();
	}

	/**
	 * finds all that match any of the two LogActions LogAction.TAG_ADD and
	 * LogAction.TAG_REMOVE that belong to the given JakeObject, sorted
	 * ascending by timestamp
	 * 
	 * @param belongsTo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Collection<LogEntry<Tag>> getTagEntries(JakeObject belongsTo) {
		if (belongsTo.getUuid() == null)
			return new HashSet<LogEntry<Tag>>();
		return query(
				"FROM logentries WHERE objectuuid = ? AND (action = ? OR action = ?)")
				.setString(0, belongsTo.getUuid().toString()).setInteger(1,
						LogAction.TAG_ADD.ordinal()).setInteger(2,
						LogAction.TAG_REMOVE.ordinal()).list();
	}

	/**
	 * finds all that match any of the two LogActions LogAction.JAKE_OBJECT_LOCK
	 * and LogAction.JAKE_OBJECT_UNLOCK that belong to the given JakeObject,
	 * sorted ascending by timestamp
	 * 
	 * @param belongsTo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Collection<LogEntry<JakeObject>> getLockEntries(JakeObject belongsTo) {
		if (belongsTo.getUuid() == null)
			return new HashSet<LogEntry<JakeObject>>();
		return query(
				"FROM logentries WHERE objectuuid = ? AND (action = ? OR action = ?)")
				.setString(0, belongsTo.getUuid().toString()).setInteger(1,
						LogAction.JAKE_OBJECT_LOCK.ordinal()).setInteger(2,
						LogAction.JAKE_OBJECT_UNLOCK.ordinal()).list();
	}


	@Override
	public LogEntry<JakeObject> getLock(JakeObject jakeObject) {
		Collection<LogEntry<JakeObject>> entries = getLockEntries(jakeObject);
		LogEntry<JakeObject> lockLogEntry = null;
		for (LogEntry<JakeObject> entry : entries) {
			if (entry.getLogAction() == LogAction.JAKE_OBJECT_LOCK) {
				lockLogEntry = entry;
			}
			if (entry.getLogAction() == LogAction.JAKE_OBJECT_UNLOCK) {
				lockLogEntry = null;
			}
		}
		return lockLogEntry;
	}

	@Override
	public Collection<Tag> getTags(JakeObject jakeObject) {
		List<Tag> tags = new LinkedList<Tag>();
		for (LogEntry<Tag> le : getTagEntries(jakeObject)) {
			if (le.getLogAction() == LogAction.TAG_ADD) {
				tags.add(le.getBelongsTo());
			}
			if (le.getLogAction() == LogAction.TAG_REMOVE) {
				tags.remove(le.getBelongsTo());
			}
		}
		return tags;
	}

	@SuppressWarnings("unchecked")
	@Override
	public LogEntry<? extends ILogable> getProjectCreatedEntry() {
		try {
			return (LogEntry<? extends ILogable>) sess().createQuery(
					"FROM logentries WHERE action = ?").setInteger(0,
					LogAction.PROJECT_CREATED.ordinal()).list().get(0);
		} catch (IndexOutOfBoundsException ioobe) {
			return null;
		}
	}

	@Override
	public List<UserId> getCurrentProjectMembers() {
		LogEntry<? extends ILogable> pce;
		Map<UserId, List<UserId>> people = getTrustGraph();
		pce = getProjectCreatedEntry();
		UserId creator;
		SortedSet<UserId> trusted = new TreeSet<UserId>();

		if (pce != null) {
			creator = pce.getMember();
			//creator MUST be the first item in the result
			trusted.add(creator);

			//TODO? run more often, until trusted does not change any more?
			for (UserId member : people.keySet()) {
				if (trusted.contains(member)) {
					for (UserId trustedMember : people.get(member)) {
						trusted.add(trustedMember);
					}
				}
			}
		}

		log.debug("len="+trusted.size());
		return new ArrayList<UserId>(trusted);
	}

	@Override
	public boolean trusts(UserId a, UserId b) {
		List<UserId> trusted = getTrustGraph().get(a);
		return trusted != null && trusted.contains(b);
	}

	@Override
	public TrustState trustsHow(UserId a, UserId b) {
		Map<UserId, TrustState> trusted = getExtendedTrustGraph().get(a);
		TrustState trustState;
		if (trusted != null) {
			trustState = trusted.get(b);
			return (trustState==null)?TrustState.NO_TRUST:trustState;
		}
		else
			return TrustState.NO_TRUST;
	}

	@Override
	public Collection<UserId> trusts(UserId a) {
		return getTrustGraph().get(a);
	}

	@Override
	public Map<UserId, TrustState> trustsHow(UserId a) {
		return getExtendedTrustGraph().get(a);
	}

	@Override
	public Map<UserId, List<UserId>> getTrustGraph() {
		Map<UserId, List<UserId>> people = new HashMap<UserId, List<UserId>>();
		LogEntry<? extends ILogable> first = getProjectCreatedEntry();
		if (first != null) {
			people.put(first.getMember(), new LinkedList<UserId>());
		} else {
			log.error("Invalid database: no ProjectCreatedEntry!");
			throw new IllegalStateException("No ProjectCreatedEntry");
		}
		
		Collection<LogEntry<UserId>> entries = getAllProjectMemberLogEntries();
		for (LogEntry<UserId> le : entries) {
			UserId who = le.getMember();
			UserId whom = le.getBelongsTo();
			if (people.get(who) == null) {
				people.put(who, new LinkedList<UserId>());
			}
			if (le.getLogAction() == LogAction.START_TRUSTING_PROJECTMEMBER) {
				people.get(who).add(whom);
			}
			else if (le.getLogAction() == LogAction.STOP_TRUSTING_PROJECTMEMBER) {
				people.get(who).remove(whom);
			}
			else if (le.getLogAction() == LogAction.FOLLOW_TRUSTING_PROJECTMEMBER) {
				people.get(who).add(whom);
			}
		}
		return people;
	}


	@Override
	public Map<UserId, Map<UserId, TrustState>> getExtendedTrustGraph() {
		Map<UserId, Map<UserId, TrustState>> people = new HashMap<UserId, Map<UserId, TrustState>>();
		LogEntry<? extends ILogable> first = getProjectCreatedEntry();
		if (first != null) {
			people.put(first.getMember(), new HashMap<UserId, TrustState>());
		} else {
			log.error("Invalid database: no ProjectCreatedEntry!");
			throw new IllegalStateException("No ProjectCreatedEntry");
		}
		Collection<LogEntry<UserId>> entries = getAllProjectMemberLogEntries();
		for (LogEntry<UserId> le : entries) {
			UserId who = le.getMember();
			UserId whom = le.getBelongsTo();
			if (people.get(who) == null) {
				people.put(who, new HashMap<UserId, TrustState>());
			}
			if (le.getLogAction() == LogAction.START_TRUSTING_PROJECTMEMBER) {
				people.get(who).put(whom, TrustState.TRUST);
			}
			else if (le.getLogAction() == LogAction.STOP_TRUSTING_PROJECTMEMBER) {
				people.get(who).put(whom, TrustState.NO_TRUST);
			}
			else if (le.getLogAction() == LogAction.FOLLOW_TRUSTING_PROJECTMEMBER) {
				people.get(who).put(whom, TrustState.AUTO_ADD_REMOVE);
			}
		}
		return people;
	}

	@Override
	public void setAllPreviousProcessed(LogEntry<? extends ILogable> logEntry) {
		// TODO do this with sql, something like
		// UPDATE LOGENTRY SET PROCESSED = TRUE WHERE belongsTo = ? AND timestamp < ?
		JakeObject jo = (JakeObject) logEntry.getBelongsTo();

		List<LogEntry<JakeObject>> versions = this.getAllVersionsOfJakeObject(jo, true);
		for (LogEntry<JakeObject> version : versions)
			if (!version.isProcessed() && version.getTimestamp().before(logEntry.getTimestamp()))
				this.setProcessed(version);
	}
}
