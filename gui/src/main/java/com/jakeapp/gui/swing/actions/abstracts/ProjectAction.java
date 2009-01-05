package com.jakeapp.gui.swing.actions.abstracts;

import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.callbacks.ProjectChanged;
import com.jakeapp.gui.swing.callbacks.ProjectSelectionChanged;

/**
 * ProjectAction is an abstract action class for all project
 * related actions.
 * Implements the changed and selection interface.
 * User: studpete
 * Date: Dec 29, 2008
 * Time: 1:16:39 AM
 */
public abstract class ProjectAction extends JakeAction
        implements ProjectSelectionChanged, ProjectChanged {
    //private static final Logger log = Logger.getLogger(ProjectAction.class);

    private Project project;

    public ProjectAction() {
        JakeMainApp.getApp().addProjectSelectionChangedListener(this);
        JakeMainApp.getApp().getCore().addProjectChangedCallbackListener(this);

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

    public void updateAction() {
        // defaults to null
    }

    public void projectChanged(final ProjectChangedEvent ev) {
        updateAction();
    }
}
