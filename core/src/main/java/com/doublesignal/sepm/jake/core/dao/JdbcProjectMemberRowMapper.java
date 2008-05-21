package com.doublesignal.sepm.jake.core.dao;

import com.doublesignal.sepm.jake.core.dao.exceptions.QueryFailedException;
import com.doublesignal.sepm.jake.core.domain.ProjectMember;
import com.doublesignal.sepm.jake.core.domain.exceptions.InputLenghtException;
import com.doublesignal.sepm.jake.core.domain.exceptions.InvalidCharactersException;
import com.doublesignal.sepm.jake.core.domain.exceptions.InvalidNicknameException;
import com.doublesignal.sepm.jake.ics.exceptions.InvalidUserIdException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * RowMapper for ProjectMember objects
 */
public class JdbcProjectMemberRowMapper implements ParameterizedRowMapper<ProjectMember> {
	public ProjectMember mapRow(ResultSet rs, int rowNum) throws SQLException {
		try {
			ProjectMember newMember = new ProjectMember(rs.getString(1));
			newMember.setNickname(rs.getString(2));
			newMember.setNotes(rs.getString(3));
			return newMember;
		} catch (InvalidCharactersException e) {
			throw new QueryFailedException("Database corrupt: Invalid characters in notes for ProjectMember " + rs.getString(1));
		} catch (InvalidNicknameException e) {
			throw new QueryFailedException("Database corrupt: Nickname invalid for ProjectMember " + rs.getString(1));
		} catch (InputLenghtException e) {
			throw new QueryFailedException("Database corrupt: Invalid length of notes for ProjectMember " + rs.getString(1));
		} catch (InvalidUserIdException e) {
			throw new QueryFailedException("Database corrupt: Invalid user ID for ProjectMember " + rs.getString(1));
		}
	}
}
