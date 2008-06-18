package com.doublesignal.sepm.jake.core.dao;

import com.doublesignal.sepm.jake.core.dao.exceptions.NoSuchProjectMemberException;
import com.doublesignal.sepm.jake.core.domain.ProjectMember;

import java.util.List;

/**
 * Serves as a frontend for database-independent ProjectMember management.
 *
 * @author Chris
 */
public interface IProjectMemberDao {
	/**
	 * Loads an existing ProjectMember from the database using their networkId.
	 *
	 * @param userid The ProjectMember's network user ID
	 * @return the ProjectMember requested
	 * @throws NoSuchProjectMemberException if no such ProjectMember is found
	 */
	public ProjectMember getByUserId(String userid) throws NoSuchProjectMemberException;

	/**
	 * Gets all ProjectMembers.
	 *
	 * @return A list of all existing ProjectMembers
	 */
	public List<ProjectMember> getAll();

	/**
	 * Saves an existing ProjectMember to the database or creates it should it not exist (this is determined by whether
	 * the ID already exists in the database).
	 *
	 * @param member The member to save/insert
	 * @return The inserted/updated project member
	 */
	public void save(ProjectMember member);

	/**
	 * Removes a ProjectMember from the database. If the ProjectMember didn't exist in the first place, nothing is done.
	 *
	 * @param member
	 */
	public void remove(ProjectMember member);

	/**
	 * Edits a note of a project member
	 *
	 * @param member The member to edit
	 * @param note The note to be inserted
	 */
	public void editNote(ProjectMember member, String note);

	/**
	 * Edit a ProjectMember's NickName from the database. If the ProjectMember didn't exist in the first place, nothing is done.
	 *
	 * @param member The member to edit
	 * @param nickName The note to be inserted
	 */
	public void editNickName(ProjectMember member, String nickName);

	/**
	 * Edits a ProjectMember's UserId from the database. If the ProjectMember didn't exist in the first place, nothing is done.
	 *
	 * @param member The member to edit
	 * @param userId The UserId to be edited
	 */
	public void editUserId(ProjectMember member , String userId);
}
