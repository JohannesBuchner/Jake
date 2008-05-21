package com.doublesignal.sepm.jake.core.dao;

import com.doublesignal.sepm.jake.core.domain.FileObject;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * RowMapper for FileObject objects
 */
public class JdbcFileObjectRowMapper implements ParameterizedRowMapper<FileObject> {
	public FileObject mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new FileObject(rs.getString(1));
	}
}
