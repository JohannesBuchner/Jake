package com.doublesignal.sepm.jake.core.dao;

import com.doublesignal.sepm.jake.core.dao.exceptions.NoSuchLogEntryException;
import com.doublesignal.sepm.jake.core.domain.LogEntry;
import com.doublesignal.sepm.jake.core.domain.JakeObject;

import java.util.*;

import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.dao.EmptyResultDataAccessException;

/**
 * JDBC implementation of the LogEntry DAO
 */
public class JdbcLogEntryDao extends SimpleJdbcDaoSupport
		  implements ILogEntryDao {
	private static final String LOGENTRY_INSERT =
			  "INSERT INTO logentries (object_name, projectmember, timestamp, action, " +
						 "message, hash, is_last_pulled) VALUES (:object_name, " +
						 ":projectmember, :timestamp, :action, :message, :hash, " +
						 ":is_last_pulled)";
	private static final String LOGENTRY_SELECT =
			  "SELECT object_name, projectmember, timestamp, action, message, hash, " +
						 "is_last_pulled FROM logentries";
	private static final String LOGENTRY_WHERE_SPECIFIC =
			  " WHERE object_name=? AND projectmember=? AND timestamp=?";
	private static final String LOGENTRY_WHERE_JAKEOBJECT =
			  " WHERE object_name=?";
	private static final String LOGENTRY_MOSTRECENT = " ORDER BY timestamp DESC LIMIT 1";
	private static final String LOGENTRY_LASTPULLED = " AND is_last_pulled=1";
	private static final String LOGENTRY_ORDER_BY_TIMESTAMP_DESC = " ORDER BY timestamp DESC";

	public void create(LogEntry logEntry) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("object_name", logEntry.getJakeObjectName());
		parameters.put("projectmember", logEntry.getUserId());
		parameters.put("timestamp", logEntry.getTimestamp());
		parameters.put("action", logEntry.getAction().toString());
		parameters.put("message", logEntry.getComment());
		parameters.put("hash", logEntry.getHash());
		parameters.put("is_last_pulled", logEntry.getIsLastPulled());

		getSimpleJdbcTemplate().update(LOGENTRY_INSERT, parameters);
	}

	public LogEntry get(String name, String projectmember, Date timestamp)
			  throws NoSuchLogEntryException {
		try {
			return getSimpleJdbcTemplate().queryForObject(LOGENTRY_SELECT + LOGENTRY_WHERE_SPECIFIC, new JdbcLogEntryRowMapper(), name, projectmember, timestamp);
		} catch (EmptyResultDataAccessException e) {
			throw new NoSuchLogEntryException();
		}
	}

	public List<LogEntry> getAll() {
		return getSimpleJdbcTemplate().query(
				  LOGENTRY_SELECT,
				  new JdbcLogEntryRowMapper()
		);
	}

	public List<LogEntry> getAllOfJakeObject(JakeObject jakeObject) {
		return getSimpleJdbcTemplate().query(
				  LOGENTRY_SELECT + LOGENTRY_WHERE_JAKEOBJECT + LOGENTRY_ORDER_BY_TIMESTAMP_DESC,
				  new JdbcLogEntryRowMapper(),
				  jakeObject.getName()
		);
	}

	public LogEntry getMostRecentFor(JakeObject jakeObject) throws NoSuchLogEntryException {
		try {
			return getSimpleJdbcTemplate().queryForObject(LOGENTRY_SELECT + LOGENTRY_WHERE_JAKEOBJECT + LOGENTRY_MOSTRECENT, new JdbcLogEntryRowMapper(), jakeObject.getName());
		} catch (EmptyResultDataAccessException e) {
			throw new NoSuchLogEntryException();
		}
	}

	public LogEntry getLastPulledFor(JakeObject jakeObject) throws NoSuchLogEntryException {
		try {
			return getSimpleJdbcTemplate().queryForObject(LOGENTRY_SELECT + LOGENTRY_WHERE_JAKEOBJECT + LOGENTRY_LASTPULLED, new JdbcLogEntryRowMapper(), jakeObject.getName());
		} catch (EmptyResultDataAccessException e) {
			throw new NoSuchLogEntryException();
		}
	}
}
