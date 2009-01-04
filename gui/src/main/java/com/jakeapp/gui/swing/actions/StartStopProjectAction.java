package com.jakeapp.gui.swing.actions;

import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.helpers.JakeMainHelper;
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

        // do nothing if we don't have a project
        if (getProject() == null) {
            return;
        }

        if (!getProject().isStarted()) {
            // TODO: exception handling
            JakeMainApp.getApp().getCore().startProject(getProject());
        } else {
            JakeMainApp.getApp().getCore().stopProject(getProject());
        }
    }


    @Override
    public void updateAction() {

        if (getProject() != null) {
            String oldName = (String) getValue(Action.NAME);
            String newName = JakeMainHelper.getProjectStartStopString(getProject());

            putValue(Action.NAME, newName);

            firePropertyChange(Action.NAME, oldName, newName);
        }

        setEnabled(getProject() != null);
    }
}