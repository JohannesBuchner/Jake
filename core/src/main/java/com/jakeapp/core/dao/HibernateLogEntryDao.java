package com.jakeapp.core.dao;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.dao.exceptions.NoSuchProjectException;
import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProjectMember;
import com.jakeapp.core.domain.Tag;

public class HibernateLogEntryDao extends HibernateDaoSupport implements ILogEntryDao {

	private static Logger log = Logger.getLogger(HibernateLogEntryDao.class);


	@Override
	public void create(LogEntry<? extends ILogable> logEntry) {
		// if (logEntry.getBelongsTo() instanceof JakeObject) {
		// log.debug("create: jakeObject");
		//
		//
		// this.getHibernateTemplate().getSessionFactory().getCurrentSession().
		//
		// createSQLQuery("INSERT INTO logEntry (id, memberid, objectid, hash, time, processed, action) "
		// +
		// "VALUES (?,?,?,?,?,?,?)")
		// .setString(0, logEntry.getUuid().toString())
		// .setString(1, logEntry.getMember().getUserId().toString())
		//
		//
		// .setString(2, ((JakeObject)
		// logEntry.getBelongsTo()).getUuid().toString())
		// .setString(3, "adfadsfasdfasdf")
		// .setDate(4, logEntry.getTimestamp())
		// .setBoolean(5, false)
		// .setInteger(6, logEntry.getLogAction().ordinal())
		// .executeUpdate();
		// }
		// else if(logEntry.getBelongsTo() instanceof Project)
		// {
		// log.debug("create: Project");
		// }

		this.getHibernateTemplate().getSessionFactory().getCurrentSession().persist(
				logEntry);
	}

	@Override
	public LogEntry<? extends ILogable> get(String name, String projectmember,
			Date timestamp) throws NoSuchLogEntryException, NoSuchProjectException {
		return null; // To change body of implemented methods use File |
		// Settings | File Templates.
	}

	@Override
	public List<LogEntry<? extends ILogable>> getAll() {
		log.debug("\n\n\n\n\ngetAll() \n\n\n\n");
		List<LogEntry<? extends ILogable>> result = this.getHibernateTemplate()
				.getSessionFactory().getCurrentSession().createQuery(
						"FROM logentries WHERE 1=1").list();

		return result;
	}

	@Override
	public <T extends JakeObject> List<LogEntry<T>> getAllOfJakeObject(T jakeObject) {
		return null; // To change body of implemented methods use File |
		// Settings | File Templates.
	}

	@Override
	public LogEntry<? extends ILogable> getMostRecentFor(JakeObject jakeObject)
			throws NoSuchLogEntryException {
		return null; // To change body of implemented methods use File |
		// Settings | File Templates.
	}

	@Override
	public <T extends JakeObject> LogEntry<T> getLastPulledFor(T jakeObject)
			throws NoSuchLogEntryException {
		return null; // To change body of implemented methods use File |
		// Settings | File Templates.
	}

	@Override
	public void setProcessed(LogEntry<? extends ILogable> logEntry) {
		// To change body of implemented methods use File | Settings | File
		// Templates.
	}

	@Override
	public List<LogEntry<? extends ILogable>> getAllUnprocessed() {
		return null; // To change body of implemented methods use File |
		// Settings | File Templates.
	}

	@Override
	public LogEntry<? extends ILogable> getUnprocessed(Project project)
			throws NoSuchProjectException, NoSuchLogEntryException {
		return null; // To change body of implemented methods use File |
		// Settings | File Templates.
	}

	@Override
	public Collection<LogEntry<? extends ILogable>> findMatching(
			LogEntry<? extends ILogable> le) {
		// TODO
		return new LinkedList<LogEntry<? extends ILogable>>();
	}

	@Override
	public Collection<LogEntry<? extends ILogable>> findMatchingAfter(
			LogEntry<? extends ILogable> le) throws NullPointerException {
		if (le.getTimestamp() == null)
			throw new NullPointerException("timestamp not set");
		// TODO
		return new LinkedList<LogEntry<? extends ILogable>>();
	}

	@Override
	public Collection<LogEntry<? extends ILogable>> findMatchingBefore(
			LogEntry<? extends ILogable> le) throws NullPointerException {
		if (le.getTimestamp() == null)
			throw new NullPointerException("timestamp not set");
		// TODO
		return new LinkedList<LogEntry<? extends ILogable>>();
	}

	@Override
	public LogEntry<? extends ILogable> findLastMatching(LogEntry<? extends ILogable> le) {
		// TODO
		return null;
	}

	@Override
	public Boolean getDeleteState(ILogable belongsTo) {
		LogEntry<? extends ILogable> leNew = findLastMatching(new LogEntry<ILogable>(
				null, LogAction.JAKE_OBJECT_NEW_VERSION, null, null, belongsTo));
		LogEntry<? extends ILogable> leDel = findLastMatching(new LogEntry<ILogable>(
				null, LogAction.JAKE_OBJECT_DELETE, null, null, belongsTo));
		if (leNew == null && leDel == null) {
			return null;
		}
		if (leNew == null) { // weird, but we support it
			return true;
		}
		if (leDel == null) {
			return false;
		}
		// both exist
		if (leDel.getTimestamp().getTime() > leNew.getTimestamp().getTime()) {
			return true;
		} else
			return false;

		// List<LogEntry<? extends ILogable>> le = new LinkedList<LogEntry<?
		// extends ILogable>>();
		/*
		 * le.addAll(findMatching(len)); le.addAll(findMatching(led)); for(entry
		 * : le) {
		 * 
		 * }
		 */
	}

	@Override
	public boolean getExistsState(ILogable belongsTo) {
		LogEntry<? extends ILogable> leNew = findLastMatching(new LogEntry<ILogable>(
				null, LogAction.JAKE_OBJECT_NEW_VERSION, null, null, belongsTo));
		LogEntry<? extends ILogable> leDel = findLastMatching(new LogEntry<ILogable>(
				null, LogAction.JAKE_OBJECT_DELETE, null, null, belongsTo));
		if (leNew == null && leDel == null) {
			return false;
		}
		if (leNew == null) { // weird, but we support it
			return false;
		}
		if (leDel == null) {
			return true;
		}
		// both exist
		if (leDel.getTimestamp().getTime() > leNew.getTimestamp().getTime()) {
			return false;
		} else
			return true;
	}

	/**
	 * finds all that match any of the two Logactions, sorted by timestamp
	 */
	private Collection<LogEntry<? extends ILogable>> findTwoMatching(LogAction a,
			LogAction b) {
		// TODO
		return new LinkedList<LogEntry<? extends ILogable>>();
	}

	/**
	 * finds all that match any of the two Logactions and belong to the given
	 * element, sorted by timestamp
	 */
	private Collection<LogEntry<? extends ILogable>> findTwoMatchingFor(LogAction a,
			LogAction b, ILogable belongsTo) {
		// TODO
		return new LinkedList<LogEntry<? extends ILogable>>();
	}

	Collection<LogEntry<ProjectMember>> getProjectMemberEntries() {
		List<LogEntry<ProjectMember>> pme = new LinkedList<LogEntry<ProjectMember>>();
		for (LogEntry<? extends ILogable> le : findTwoMatching(
				LogAction.START_TRUSTING_PROJECTMEMBER,
				LogAction.STOP_TRUSTING_PROJECTMEMBER)) {
			pme.add((LogEntry<ProjectMember>) le); // TODO?
		}
		return pme;
	}

	private Collection<LogEntry<ProjectMember>> getProjectMemberEntriesFor(
			ProjectMember belongsTo) {
		List<LogEntry<ProjectMember>> pme = new LinkedList<LogEntry<ProjectMember>>();
		for (LogEntry<? extends ILogable> le : findTwoMatchingFor(
				LogAction.START_TRUSTING_PROJECTMEMBER,
				LogAction.STOP_TRUSTING_PROJECTMEMBER, belongsTo)) {
			pme.add((LogEntry<ProjectMember>) le); // TODO?
		}
		return pme;
	}

	private Collection<LogEntry<Tag>> getTagEntries(ILogable belongsTo) {
		List<LogEntry<Tag>> pme = new LinkedList<LogEntry<Tag>>();
		for (LogEntry<? extends ILogable> le : findTwoMatchingFor(LogAction.TAG_ADD,
				LogAction.TAG_REMOVE, belongsTo)) {
			pme.add((LogEntry<Tag>) le); // TODO?
		}
		return pme;
	}

	@Override
	public Collection<ProjectMember> getCurrentProjectMembers() {
		Map<ProjectMember, List<ProjectMember>> people = getTrustGraph();
		for (ProjectMember member : people.keySet()) {
			if (people.get(member).size() == 0)
				// member isn't trusted by anyone -> removed
				people.remove(member);
		}
		return people.keySet();
	}

	@Override
	public Map<ProjectMember, List<ProjectMember>> getTrustGraph() {
		Map<ProjectMember, List<ProjectMember>> people = new HashMap<ProjectMember, List<ProjectMember>>();
		LogEntry<? extends ILogable> first = getProjectCreatedEntry();
		people.put(first.getMember(), new LinkedList<ProjectMember>());

		Collection<LogEntry<ProjectMember>> entries = getProjectMemberEntries();
		for (LogEntry<ProjectMember> le : entries) {
			ProjectMember who = le.getMember();
			ProjectMember whom = le.getBelongsTo();
			if (people.get(whom) == null) {
				people.put(whom, new LinkedList<ProjectMember>());
			}
			if (le.getLogAction() == LogAction.START_TRUSTING_PROJECTMEMBER) {
				people.get(whom).add(who);
			}
			if (le.getLogAction() == LogAction.STOP_TRUSTING_PROJECTMEMBER) {
				people.get(whom).remove(who);
			}
		}
		return people;
	}

	@Override
	public Collection<Tag> getCurrentTags(ILogable belongsTo) {
		List<Tag> tags = new LinkedList<Tag>();
		for (LogEntry<Tag> le : getTagEntries(belongsTo)) {
			if (le.getLogAction() == LogAction.TAG_ADD) {
				tags.add(le.getBelongsTo());
			}
			if (le.getLogAction() == LogAction.TAG_REMOVE) {
				tags.remove(le.getBelongsTo());
			}
		}
		return tags;
	}

	@Override
	public LogEntry<? extends ILogable> getProjectCreatedEntry() {
		return findLastMatching(new LogEntry<ILogable>(null, LogAction.PROJECT_CREATED));
	}

	@Override
	public Boolean trusts(ProjectMember a, ProjectMember b) {
		return getTrustGraph().get(a).contains(b);
	}

	@Override
	public Collection<ProjectMember> trusts(ProjectMember a) {
		return getTrustGraph().get(a);
	}
}
