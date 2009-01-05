package com.jakeapp.gui.swing.actions;

import com.jakeapp.core.domain.TrustState;
import com.jakeapp.gui.swing.JakeMainView;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * The Invite people action.
 * Opens a Dialog that let you add people to the project.
 * They get an invitation and can join/refuse the project.
 */
public class TrustFullPeopleAction extends PeopleListAction {
    private static final Logger log = Logger.getLogger(TrustFullPeopleAction.class);
    private static final TrustState actionTrustState = TrustState.AUTO_ADD_REMOVE;

    public TrustFullPeopleAction(JList list) {
        super(list);

        String actionStr = JakeMainView.getMainView().getResourceMap().
                getString("trustedAddPeoplePeopleMenuItem.text");

        putValue(Action.NAME, actionStr);

        // update state
        putValue(Action.SELECTED_KEY, checkProjectMemberStatus(actionTrustState));
    }


    public void actionPerformed(ActionEvent actionEvent) {
        log.info("Fully trust ProjectMember " + getList() + " from" + getProject());
        actionOnSelectedPeople(actionTrustState);
    }
}