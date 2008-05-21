package com.doublesignal.sepm.jake.core.dao;

import com.doublesignal.sepm.jake.core.domain.NoteObject;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * RowMapper for FileObject objects
 */
public class JdbcNoteObjectRowMapper implements ParameterizedRowMapper<NoteObject> {
	public NoteObject mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new NoteObject(rs.getString(1), rs.getString(2));
	}
}