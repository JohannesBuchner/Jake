package com.jakeapp.gui.swing.actions.project;

import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.globals.JakeContext;
import com.jakeapp.gui.swing.actions.abstracts.ProjectAction;
import com.jakeapp.gui.swing.helpers.FileUtilities;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * User: studpete
 * Date: Dec 29, 2008
 * Time: 12:20:54 AM
 */
public class JoinProjectAction extends ProjectAction {
    private static final Logger log = Logger.getLogger(JoinProjectAction.class);
    private String projectLocation;

    public JoinProjectAction() {
        super();

        putValue(Action.NAME, JakeMainView.getMainView().getResourceMap().
                getString("joinProjectMenuItem"));
    }


    public void actionPerformed(ActionEvent actionEvent) {
        log.info("Joining Project: " + getProject());

        JakeMainApp.getCore().joinProject(
                getProjectLocation(),
				JakeContext.getInvitation());
    }


    @Override
    public void updateAction() {
        setProjectLocation(FileUtilities.getDefaultProjectLocation(JakeContext.getInvitation()));
    }

    public String getProjectLocation() {
        return projectLocation;
    }

    public void setProjectLocation(String projectLocation) {
        this.projectLocation = projectLocation;
    }
}