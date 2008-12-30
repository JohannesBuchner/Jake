package com.jakeapp.gui.swing.callbacks;

import com.jakeapp.core.domain.Project;

/**
 * The global ProjectChanged-Callback Interface.
 * Fires events when projects change.
 * Every registrant get all events.
 * Project is saved as event source.
 * User: studpete
 * Date: Dec 28, 2008
 * Time: 11:39:30 PM
 */
public interface ProjectChanged {

    /**
     * Inner class that saves project & change reason
     */
    class ProjectChangedEvent {
        private Project project;
        private ProjectChangedReason reason;

        public ProjectChangedEvent(Project project, ProjectChangedReason reason) {
            this.project = project;
            this.reason = reason;
        }

        public Project getProject() {
            return project;
        }

        public ProjectChangedReason getReason() {
            return reason;
        }

        // TODO: need more reasons
        public enum ProjectChangedReason {
            Created, State, Name, Invited
        }
    }

    void projectChanged(final ProjectChangedEvent ev);
}
