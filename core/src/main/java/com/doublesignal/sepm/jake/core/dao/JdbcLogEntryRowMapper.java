package com.doublesignal.sepm.jake.core.dao;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import com.doublesignal.sepm.jake.core.domain.LogEntry;
import com.doublesignal.sepm.jake.core.domain.LogAction;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Row mapper for LogEntry objects
 */
public class JdbcLogEntryRowMapper implements ParameterizedRowMapper<LogEntry> {
	public LogEntry mapRow(ResultSet resultSet, int i) throws SQLException {
		LogAction action = LogAction.valueOf(resultSet.getString(4));
		return new LogEntry(action, resultSet.getDate(3), resultSet.getString(1),
				  resultSet.getString(6), resultSet.getString(2), resultSet.getString(5));
	}
}
