package com.jakeapp.gui.swing.actions.project;

import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.globals.JakeContext;
import com.jakeapp.gui.swing.actions.abstracts.ProjectAction;
import com.jakeapp.gui.swing.actions.abstracts.JakeAction;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * User: studpete
 * Date: Dec 29, 2008
 * Time: 12:20:54 AM
 */
public class RejectInvitationAction extends JakeAction {
    private static final Logger log = Logger.getLogger(RejectInvitationAction.class);

    public RejectInvitationAction() {
        super();

        putValue(Action.NAME, JakeMainView.getMainView().getResourceMap().
                getString("rejectProjectMenuItem"));
    }


	public void actionPerformed(ActionEvent actionEvent) {
        log.info("Rejecting Invitation: " + JakeContext.getInvitation());

        JakeMainApp.getCore().rejectProject();

	    // Hide the damn thing
	    JakeContext.setInvitation(null);
    }
}