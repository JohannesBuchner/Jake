package com.jakeapp.gui.swing;

import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.callbacks.ContextChanged;
import org.jdesktop.application.ResourceMap;

import java.util.EnumSet;

public abstract class JakeGuiComponent implements ContextChanged {
	private Project project;

    public JakeGuiComponent() {
    }

    protected ResourceMap getResourceMap() {
        return JakeMainView.getResouceMap();
    }

    public Project getProject() {
        return JakeContext.getProject();
    }

    /**
     * Called when project is changed.
     */
    protected abstract void projectUpdated();

		public void contextChanged(EnumSet<Reason> reason, Object context) {
			if(reason.contains(Reason.Project)) {
				projectUpdated();
			}
		}
}
