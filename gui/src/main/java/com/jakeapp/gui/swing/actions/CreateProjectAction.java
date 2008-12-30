package com.jakeapp.gui.swing.actions;

import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.helpers.JakeMainHelper;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * User: studpete
 * Date: Dec 29, 2008
 * Time: 12:20:54 AM
 */
public class CreateProjectAction extends ProjectAction {
    private static final Logger log = Logger.getLogger(CreateProjectAction.class);

    public CreateProjectAction() {
        super();

        putValue(Action.NAME, JakeMainView.getMainView().getResourceMap().
                getString("createProjectMenuItem.text"));

        Icon createProjectIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
                getClass().getResource("/icons/createproject.png")).getScaledInstance(32, 32, Image.SCALE_SMOOTH));
        putValue(Action.LARGE_ICON_KEY, createProjectIcon);
    }


    public void actionPerformed(ActionEvent actionEvent) {
        log.info("Create Project: " + getProject());

        String path = JakeMainHelper.openDirectoryChooser();
        log.info("Directory was: " + path);

        // create the directory if path was not null
        if (path != null) {
            JakeMainApp.getApp().getCore().createProject(
                    JakeMainHelper.getLastFolderFromPath(path), path);
        }
    }


    @Override
    public void updateAction() {
    }
}