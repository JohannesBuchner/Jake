package com.doublesignal.sepm.jake.core.dao;

import com.doublesignal.sepm.jake.core.dao.exceptions.QueryFailedException;
import com.doublesignal.sepm.jake.core.domain.Tag;
import com.doublesignal.sepm.jake.core.domain.exceptions.InvalidTagNameException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * RowMapper for Tag objects
 */
public class JdbcTagRowMapper implements ParameterizedRowMapper<Tag> {
	public Tag mapRow(ResultSet rs, int rowNum) throws SQLException {
		try {
			return new Tag(rs.getString(1));
		} catch (InvalidTagNameException e) {
			throw new QueryFailedException("Database corrupt: Invalid tag name " + rs.getString(1));
		}
	}
}
