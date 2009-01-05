package com.jakeapp.gui.swing.actions;

import com.jakeapp.gui.swing.JakeMainView;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * The Invite people action.
 * Opens a Dialog that let you add people to the project.
 * They get an invitation and can join/refuse the project.
 */
public class InvitePeopleAction extends ProjectAction {
    private static final Logger log = Logger.getLogger(InvitePeopleAction.class);

    public InvitePeopleAction(boolean addPoints) {
        super();

        String actionStr = JakeMainView.getMainView().getResourceMap().
                getString("invitePeopleMenuItem.text");

        if (addPoints) {
            actionStr += "...";
        }

        putValue(Action.NAME, actionStr);

        // add large icon (for toolbar only)
        Icon invitePeopleIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
                getClass().getResource("/icons/people.png")).getScaledInstance(32, 32, Image.SCALE_SMOOTH));

        this.putValue(Action.LARGE_ICON_KEY, invitePeopleIcon);
    }


    public void actionPerformed(ActionEvent actionEvent) {
        log.info("Invite People to: " + getProject());

        // TODO: open invite people selector
        /*
        String path = JakeMainHelper.openDirectoryChooser(null);
        log.info("Directory was: " + path);

        // create the directory if path was not null
        if (path != null) {
            JakeMainApp.getApp().getCore().createProject(
                    JakeMainHelper.getLastFolderFromPath(path), path);
        }
        */
    }
}