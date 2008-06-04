package com.doublesignal.sepm.jake.core.dao;

import com.doublesignal.sepm.jake.core.domain.ProjectMember;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * RowMapper for ProjectMember objects
 */
public class JdbcProjectMemberRowMapper implements ParameterizedRowMapper<ProjectMember> {
	public ProjectMember mapRow(ResultSet rs, int rowNum) throws SQLException {
		ProjectMember newMember = new ProjectMember(rs.getString(1));
		newMember.setNickname(rs.getString(2));
		newMember.setNotes(rs.getString(3));
		newMember.setActive(rs.getBoolean(4));
		return newMember;
	}
}
