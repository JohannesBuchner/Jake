package com.jakeapp.core.synchronization;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.exceptions.IllegalProtocolException;

import java.util.List;

/**
 * This file describes the interface of a general synchronization strategy,
 * used to
 * keep notes, files, folders, tags, etc. in sync.
 *
 *
 * ATTENTION Please don't delete for now
 */
@Deprecated
public interface SynchronizationStrategy {


    /**
     * Start synchronization of a specific JakeObject.
     *
     * @param object the corresponding JakeObject
     * @throws IllegalArgumentException
     */
    void synchronize(JakeObject object) throws IllegalArgumentException;

    /**
     * Start synchronization of a specific JakeObject with a list of Users
     *
     * @param object the corresponding JakeObject
     * @param users  a list of users which to synchronize with
     * @throws IllegalArgumentException
     */
    void synchronize(JakeObject object, List<UserId> users)
            throws IllegalArgumentException;


    /**
     * Start synchronization of a specific Project
     *
     * @param project the corresponding project
     * @throws IllegalArgumentException
     */
    void synchronize(Project project) throws IllegalArgumentException;


    /**
     * Start synchronization of the project with a list of users.
     *
     * @param project the corresponding project
     * @param users   a list of users which to synchronize with
     * @throws IllegalArgumentException if the supplied Project or list
     *                                  of users is null or invalid
     * @throws IllegalProtocolException if the supplied users
     *                                  correspond to the wrong protocol
     */
    void synchronize(Project project, List<UserId> users)
            throws IllegalArgumentException,
            IllegalProtocolException;

}
