package com.jakeapp.gui.swing;

import com.jakeapp.core.domain.Project;

import java.util.List;


public interface ICoreAccess {

    /**
     * Get all my projects(started/stopped), but not the invited ones.
     * List is alphabetically sorted.
     *
     * @return list of projects.
     */
    List<Project> getMyProjects();

    /**
     * Get projects where i am invited to.
     * List is alphabetically sorted.
     *
     * @return list of invited projects.
     */
    List<Project> getInvitedProjects();
}
