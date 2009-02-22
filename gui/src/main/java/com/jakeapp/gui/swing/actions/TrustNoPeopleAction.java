package com.jakeapp.gui.swing.actions;

import com.jakeapp.core.domain.TrustState;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.PeopleListAction;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * The Invite people action.
 * Opens a Dialog that let you add people to the project.
 * They get an invitation and can join/refuse the project.
 */
public class TrustNoPeopleAction extends PeopleListAction {
    private static final Logger log = Logger.getLogger(TrustNoPeopleAction.class);
    private static final TrustState actionTrustState = TrustState.NO_TRUST;

    public TrustNoPeopleAction(JList list) {
        super(list);

        String actionStr = JakeMainView.getMainView().getResourceMap().
                getString("notTrustedPeopleMenuItem.text");

        putValue(Action.NAME, actionStr);

        // update state
        putValue(Action.SELECTED_KEY, checkUserIdStatus(actionTrustState));
    }


    public void actionPerformed(ActionEvent actionEvent) {
        log.info("Don't trust ProjectMember " + getList() + " from" + getProject());
        actionOnSelectedPeople(actionTrustState);
    }
}