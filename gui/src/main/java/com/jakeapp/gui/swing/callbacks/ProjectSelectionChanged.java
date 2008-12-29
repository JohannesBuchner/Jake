package com.jakeapp.gui.swing.callbacks;

import com.jakeapp.core.domain.Project;

/**
 * This Event is fired when another Project is selected.
 * Controls have to be updated and so on.
 * <p/>
 * User: studpete
 * Date: Dec 29, 2008
 * Time: 11:35:48 AM
 */
public interface ProjectSelectionChanged {
    void setProject(Project pr);
}
