package com.jakeapp.gui.swing.components.componenthelper;

import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.callbacks.ContextChangedCallback;
import com.jakeapp.gui.swing.globals.JakeContext;
import com.jakeapp.gui.swing.JakeMainView;
import org.jdesktop.application.ResourceMap;

import java.util.EnumSet;

public abstract class JakeGuiComponent implements ContextChangedCallback {
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
