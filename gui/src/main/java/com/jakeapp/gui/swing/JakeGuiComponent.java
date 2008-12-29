package com.jakeapp.gui.swing;

import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.callbacks.ProjectSelectionChanged;
import org.jdesktop.application.ResourceMap;

/**
 * User: studpete
 * Date: Dec 29, 2008
 * Time: 11:47:36 AM
 */
public abstract class JakeGuiComponent implements ProjectSelectionChanged {
    private Project project;
    protected ICoreAccess core;

    public JakeGuiComponent(ICoreAccess core) {
        this.core = core;
    }

    protected ResourceMap getResourceMap() {
        return JakeMainView.getResouceMap();
    }

    protected ICoreAccess getCore() {
        return core;
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
