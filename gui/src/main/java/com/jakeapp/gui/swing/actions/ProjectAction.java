package com.jakeapp.gui.swing.actions;

import com.jakeapp.core.domain.Project;
import org.apache.log4j.Logger;

/**
 * User: studpete
 * Date: Dec 29, 2008
 * Time: 1:16:39 AM
 */
public abstract class ProjectAction extends JakeAction {
    private static final Logger log = Logger.getLogger(ProjectAction.class);

    private Project project;

    public ProjectAction() {
    }

    public ProjectAction(Project project) {
        this.setProject(project);
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;

        updateAction();
    }

    public abstract void updateAction();
}
