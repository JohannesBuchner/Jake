package com.doublesignal.sepm.jake.core.dao;

import com.doublesignal.sepm.jake.core.domain.ProjectMember;
import com.doublesignal.sepm.jake.core.domain.ProjectInvitation;
import com.doublesignal.sepm.jake.core.domain.exceptions.NoSuchProjectMemberException;
import com.doublesignal.sepm.jake.ics.exceptions.NoSuchNetworkUserException;

/**
 * SEPM SS08
 * Gruppe: 3950
 * Projekt: Jake - a collaborative Environment
 * User: domdorn
 * Date: May 9, 2008
 * Time: 1:11:28 AM
 */
public interface IProjectMemberDao
{


    /**
     * Loads an existing ProjectMember from the database using his
     * networkId.
     * @param networkId
     * @return the ProjectMember requested
     * @throws NoSuchProjectMemberException if no such ProjectMember is found
     */
    public ProjectMember load(String networkId)
            throws NoSuchProjectMemberException;


    /**
     * Saves an existing ProjectMember to the database.
     * @param member
     * @return the inserted project member
     * @throws NoSuchProjectMemberException if user not in database
     */
    public ProjectMember save(ProjectMember member)
            throws NoSuchProjectMemberException;


    /**
     * Add a new ProjectMember to the database with the Information
     * contained in the corresponding invitation.
     * @param invitation
     * @return the added ProjectMember
     */
    public ProjectMember importInvitation(ProjectInvitation invitation);
    



}
