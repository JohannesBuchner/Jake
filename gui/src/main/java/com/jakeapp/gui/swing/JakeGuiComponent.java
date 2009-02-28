package com.jakeapp.gui.swing;

import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.callbacks.ProjectSelectionChanged;
import org.jdesktop.application.ResourceMap;

public abstract class JakeGuiComponent implements ProjectSelectionChanged {
	private Project project;

    public JakeGuiComponent() {
    }

    protected ResourceMap getResourceMap() {
        return JakeMainView.getResouceMap();
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
        projectUpdated();
    }

    /**
     * Called when project is changed.
     */
    protected abstract void projectUpdated();
}
