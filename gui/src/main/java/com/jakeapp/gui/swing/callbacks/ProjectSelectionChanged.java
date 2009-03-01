package com.jakeapp.gui.swing.callbacks;

import com.jakeapp.core.domain.Project;

/**
 * This Event is fired when another Project is selected.
 * Controls have to be updated and so on.
 */
public interface ProjectSelectionChanged {
    public void setProject(Project pr);
}
