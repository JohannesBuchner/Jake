package com.jakeapp.gui.swing.actions;

import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.controls.JListMutable;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * The Invite people action.
 * Opens a Dialog that let you add people to the project.
 * They get an invitation and can join/refuse the project.
 */
public class RenamePeopleAction extends ProjectAction {
    private static final Logger log = Logger.getLogger(RenamePeopleAction.class);
    private JListMutable mutable;

    public RenamePeopleAction(JListMutable mutable) {
        super();
        setMutable(mutable);

        String actionStr = JakeMainView.getMainView().getResourceMap().
                getString("renamePeopleMenuItem.text");

        putValue(Action.NAME, actionStr);

        // only enable if exact one element is selected.
        setEnabled(getMutable().getSelectedValues().length == 1);
    }


    public void actionPerformed(ActionEvent actionEvent) {
        log.info("Rename ProjectMember " + getMutable() + " from" + getProject());

        // ensure that people panel is visible
        JakeMainView.getMainView().setContextViewPanel(JakeMainView.ContextPanels.Project);

        new JListMutable.StartEditingAction().actionPerformed(new ActionEvent(getMutable(), 0, ""));
    }

    public JListMutable getMutable() {
        return mutable;
    }

    public void setMutable(JListMutable mutable) {
        this.mutable = mutable;
    }
}