package com.jakeapp.gui.swing.actions.project;

import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.globals.JakeContext;
import com.jakeapp.gui.swing.actions.abstracts.ProjectAction;
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

        JakeMainApp.getCore().rejectProject(getProject());

	    // Hide the damn thing
	    JakeContext.setInvitation(null);
    }


    @Override
    public void updateAction() {
    }
}