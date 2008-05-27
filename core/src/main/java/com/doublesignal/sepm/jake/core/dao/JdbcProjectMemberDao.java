package com.doublesignal.sepm.jake.core.dao;

import com.doublesignal.sepm.jake.core.dao.exceptions.NoSuchProjectMemberException;
import com.doublesignal.sepm.jake.core.domain.ProjectMember;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

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
	private static final String PROJECTMEMBER_SELECT = "SELECT userid, nick, notes FROM projectmembers";
	private static final String PROJECTMEMBER_WHERE_USERID = " WHERE userid=?";

	public ProjectMember getByUserId(String networkId) throws NoSuchProjectMemberException {
		List<ProjectMember> matches = getSimpleJdbcTemplate().query(
				  PROJECTMEMBER_SELECT + PROJECTMEMBER_WHERE_USERID,
				  new JdbcProjectMemberRowMapper(),
				  networkId
		);

		if (matches.size() == 0) {
			throw new NoSuchProjectMemberException("ProjectMember \"" + networkId + "\" does not exist");
		}

		return matches.get(0);
	}

	public List<ProjectMember> getAll() {
		return getSimpleJdbcTemplate().query(
				  PROJECTMEMBER_SELECT,
				  new JdbcProjectMemberRowMapper()
		);
	}

	public void save(ProjectMember member) {
		ProjectMember existing = null;
		try {
			this.getByUserId(member.getUserId());

			/* If we're still in here, the ProjectMember already exists */
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("nick", member.getNickname());
			parameters.put("notes", member.getNotes());
			getSimpleJdbcTemplate().update(PROJECTMEMBER_UPDATE, parameters);
		} catch (NoSuchProjectMemberException e) {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("userid", member.getUserId());
			parameters.put("nick", member.getNickname());
			parameters.put("notes", member.getNotes());
			getSimpleJdbcTemplate().update(PROJECTMEMBER_INSERT, parameters);
		}
	}

	public void remove(ProjectMember member) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("userid", member.getUserId());
		getSimpleJdbcTemplate().update(PROJECTMEMBER_DELETE, parameters);
	}
}
