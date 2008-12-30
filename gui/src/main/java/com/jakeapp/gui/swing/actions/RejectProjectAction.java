package com.jakeapp.gui.swing.actions;

import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * User: studpete
 * Date: Dec 29, 2008
 * Time: 12:20:54 AM
 */
public class RejectProjectAction extends ProjectAction {
    private static final Logger log = Logger.getLogger(RejectProjectAction.class);

    public RejectProjectAction() {
        super();

        putValue(Action.NAME, JakeMainView.getMainView().getResourceMap().
                getString("rejectProjectMenuItem"));
    }


    public void actionPerformed(ActionEvent actionEvent) {
        log.info("Reject Project: " + getProject());

        JakeMainApp.getApp().getCore().rejectProject(getProject());
    }


    @Override
    public void updateAction() {
    }
}