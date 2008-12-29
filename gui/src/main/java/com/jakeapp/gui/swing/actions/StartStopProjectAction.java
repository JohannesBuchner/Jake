package com.jakeapp.gui.swing.actions;

import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.JakeMainView;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * User: studpete
 * Date: Dec 29, 2008
 * Time: 12:20:54 AM
 */
public class StartStopProjectAction extends ProjectAction {
    private static final Logger log = Logger.getLogger(StartStopProjectAction.class);

    public StartStopProjectAction(Project project) {
        super(project);
    }

    public StartStopProjectAction() {
        super();
    }


    public void actionPerformed(ActionEvent actionEvent) {
        log.info("Start/Stop Project: " + getProject());

        if (!getProject().isStarted()) {
            // TODO: exception handling
            JakeMainView.getMainView().getCore().startProject(getProject());
        } else {
            JakeMainView.getMainView().getCore().stopProject(getProject());
        }
    }


    @Override
    public void updateAction() {

        if (getProject() != null) {
            String oldName = (String) getValue(Action.NAME);
            String newName = JakeMainView.getMainView().getProjectStartStopString(getProject());

            putValue(Action.NAME, newName);

            firePropertyChange(Action.NAME, oldName, newName);
        }

        setEnabled(getProject() != null);
    }
}