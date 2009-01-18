package com.jakeapp.core.dao;

import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.domain.LogEntry;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.dao.exceptions.NoSuchProjectException;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.apache.log4j.Logger;

public class HibernateLogEntryDao extends HibernateDaoSupport implements ILogEntryDao {
    private static Logger log = Logger.getLogger(HibernateLogEntryDao.class);


    @Override
    public void create(LogEntry<? extends ILogable> logEntry) {
//        if (logEntry.getBelongsTo() instanceof JakeObject) {
//            log.debug("create: jakeObject");
//
//
//            this.getHibernateTemplate().getSessionFactory().getCurrentSession().
//
//                    createSQLQuery("INSERT INTO logEntry (id, memberid, objectid, hash, time, processed, action) " +
//                            "VALUES (?,?,?,?,?,?,?)")
//                    .setString(0, logEntry.getUuid().toString())
//                    .setString(1, logEntry.getMember().getUserId().toString())
//
//
//                    .setString(2, ((JakeObject) logEntry.getBelongsTo()).getUuid().toString())
//                    .setString(3, "adfadsfasdfasdf")
//                    .setDate(4, logEntry.getTimestamp())
//                    .setBoolean(5, false)
//                    .setInteger(6, logEntry.getLogAction().ordinal())
//                    .executeUpdate();
//        }
//        else if(logEntry.getBelongsTo() instanceof Project)
//        {
//            log.debug("create: Project");
//        }

        this.getHibernateTemplate().getSessionFactory().getCurrentSession().persist(logEntry);
    }

    @Override
    public LogEntry<? extends ILogable> get(String name, String projectmember, Date timestamp) throws NoSuchLogEntryException, NoSuchProjectException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<LogEntry<? extends ILogable>> getAll() {
        log.debug("\n\n\n\n\ngetAll() \n\n\n\n");
        List<LogEntry<? extends ILogable>> result = this.getHibernateTemplate().getSessionFactory().getCurrentSession()
                .createQuery("FROM logentries WHERE 1=1").list();

        return result;
    }

    @Override
    public <T extends JakeObject> List<LogEntry<T>> getAllOfJakeObject(T jakeObject) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public LogEntry<? extends ILogable> getMostRecentFor(JakeObject jakeObject) throws NoSuchLogEntryException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends JakeObject> LogEntry<T> getLastPulledFor(T jakeObject) throws NoSuchLogEntryException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setProcessed(LogEntry<? extends ILogable> logEntry) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<LogEntry<? extends ILogable>> getAllUnprocessed() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public LogEntry<? extends ILogable> getUnprocessed(Project project) throws NoSuchProjectException, NoSuchLogEntryException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

	@Override
	public Iterable<LogEntry<? extends ILogable>> findMatching(
			LogEntry<? extends ILogable> le) {
		return new LinkedList<LogEntry<? extends ILogable>>();
	}

	@Override
	public Iterable<LogEntry<? extends ILogable>> findMatchingAfter(
			LogEntry<? extends ILogable> le) throws NullPointerException {
		if(le.getTimestamp() == null)
			throw new NullPointerException("timestamp not set");
		return new LinkedList<LogEntry<? extends ILogable>>();
	}

	@Override
	public Iterable<LogEntry<? extends ILogable>> findMatchingBefore(
			LogEntry<? extends ILogable> le) throws NullPointerException {
		if(le.getTimestamp() == null)
			throw new NullPointerException("timestamp not set");
		return new LinkedList<LogEntry<? extends ILogable>>();
	}
}
