package com.jakeapp.gui.swing.actions;

import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.helpers.JakeMainHelper;
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

    public JoinProjectAction() {
        super();

        putValue(Action.NAME, JakeMainView.getMainView().getResourceMap().
                getString("joinProjectMenuItem"));
    }


    public void actionPerformed(ActionEvent actionEvent) {
        log.info("Joining Project: " + getProject());

        JakeMainApp.getApp().getCore().joinProject(
                JakeMainHelper.getDefaultProjectLocation(getProject()),
                getProject());
    }


    @Override
    public void updateAction() {
    }
}