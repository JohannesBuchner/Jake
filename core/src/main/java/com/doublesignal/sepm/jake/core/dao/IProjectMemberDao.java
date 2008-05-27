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
	 * @param networkId The ProjectMember's network ID
	 * @return the ProjectMember requested
	 * @throws NoSuchProjectMemberException if no such ProjectMember is found
	 */
	public ProjectMember getByUserId(String networkId) throws NoSuchProjectMemberException;

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
	 * Removes a ProjectMember from the database.
	 *
	 * @param member
	 */
	public void remove(ProjectMember member);
}
