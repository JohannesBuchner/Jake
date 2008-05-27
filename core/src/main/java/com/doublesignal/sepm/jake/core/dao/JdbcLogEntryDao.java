package com.doublesignal.sepm.jake.core.dao;

import com.doublesignal.sepm.jake.core.dao.exceptions.NoSuchLogEntryException;
import com.doublesignal.sepm.jake.core.domain.LogEntry;
import com.doublesignal.sepm.jake.core.domain.JakeObject;

import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

/**
 * JDBC implementation of the LogEntry DAO
 */
public class JdbcLogEntryDao extends SimpleJdbcDaoSupport implements ILogEntryDao {
	private static final String LOGENTRY_INSERT = "";
	private static final String LOGENTRY_SELECT = "";
	private static final String LOGENTRY_WHERE = "";

	public LogEntry create(LogEntry logEntry) {
		return null;
	}

	public LogEntry get(String name, String type, Date timestamp) throws NoSuchLogEntryException {
		return null;
	}

	public List<LogEntry> getAll() {
		return null;
	}

	public List<LogEntry> getAllOfJakeObject(JakeObject jakeObject) {
		return null;
	}

	public LogEntry getMostRecentFor(JakeObject jakeObject) {
		return null;
	}

	public String getProjectMemberUserIdFor(LogEntry logEntry) {
		return null;
	}
}
