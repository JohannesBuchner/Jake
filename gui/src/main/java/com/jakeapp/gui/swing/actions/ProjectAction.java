package com.jakeapp.gui.swing.actions;

import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.callbacks.ProjectSelectionChanged;

/**
 * ProjectAction is an abstract action class for all project related actions.
 * User: studpete
 * Date: Dec 29, 2008
 * Time: 1:16:39 AM
 */
public abstract class ProjectAction extends JakeAction implements ProjectSelectionChanged {
    //private static final Logger log = Logger.getLogger(ProjectAction.class);

    private Project project;

    public ProjectAction() {
        JakeMainApp.getApp().addProjectSelectionChangedListener(this);

        // initial load
        setProject(JakeMainApp.getApp().getProject());
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
