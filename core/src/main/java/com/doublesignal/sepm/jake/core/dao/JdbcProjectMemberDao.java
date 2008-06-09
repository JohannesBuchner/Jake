package com.doublesignal.sepm.jake.core.dao;

import com.doublesignal.sepm.jake.core.dao.exceptions.NoSuchProjectMemberException;
import com.doublesignal.sepm.jake.core.domain.ProjectMember;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Spring JDBC implementation of the ProjectMember DAO
 *
 * @author Chris
 */
public class JdbcProjectMemberDao extends SimpleJdbcDaoSupport implements IProjectMemberDao {
	private static final String PROJECTMEMBER_INSERT = "INSERT INTO projectmembers (userid, nick, notes) VALUES (:userid, :nick, :notes)";
	private static final String PROJECTMEMBER_UPDATE = "UPDATE projectmembers SET nick=:nick, notes=:notes WHERE userid=:userid";
	private static final String PROJECTMEMBER_DELETE = "DELETE FROM projectmembers WHERE userid=:userid";
	private static final String PROJECTMEMBER_SELECT = "SELECT userid, nick, notes, active FROM projectmembers";
	private static final String PROJECTMEMBER_WHERE_USERID = " WHERE userid=?";

	public ProjectMember getByUserId(String networkId) throws NoSuchProjectMemberException {
		try {
			return getSimpleJdbcTemplate().queryForObject(PROJECTMEMBER_SELECT + PROJECTMEMBER_WHERE_USERID, new JdbcProjectMemberRowMapper(), networkId);
		} catch (EmptyResultDataAccessException e) {
			throw new NoSuchProjectMemberException("ProjectMember \"" + networkId + "\" does not exist");
		}
	}

	public List<ProjectMember> getAll() {
		return getSimpleJdbcTemplate().query(
				  PROJECTMEMBER_SELECT,
				  new JdbcProjectMemberRowMapper()
		);
	}

	public void save(ProjectMember member) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("userid", member.getUserId());
		parameters.put("nick", member.getNickname());
		parameters.put("notes", member.getNotes());
		try {
			this.getByUserId(member.getUserId());
			/* If we're still in here, the ProjectMember already exists */
			getSimpleJdbcTemplate().update(PROJECTMEMBER_UPDATE, parameters);
		} catch (NoSuchProjectMemberException e) {
			getSimpleJdbcTemplate().update(PROJECTMEMBER_INSERT, parameters);
		}
	}

	public void remove(ProjectMember member) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("userid", member.getUserId());
		getSimpleJdbcTemplate().update(PROJECTMEMBER_DELETE, parameters);
	}


	public void editNickName(ProjectMember member, String nickName) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("userid", member.getUserId());
		parameters.put("nick", nickName);
		parameters.put("notes", member.getNotes());
		getSimpleJdbcTemplate().update(PROJECTMEMBER_UPDATE, parameters);
	}

	
	public void editNote(ProjectMember member, String note) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("userid", member.getUserId());
		parameters.put("notes", note);
		parameters.put("nick", member.getNickname());
		getSimpleJdbcTemplate().update(PROJECTMEMBER_UPDATE, parameters);
		
	}
	
}
