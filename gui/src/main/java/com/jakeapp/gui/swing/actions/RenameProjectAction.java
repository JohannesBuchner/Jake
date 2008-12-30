package com.jakeapp.gui.swing.actions;

import com.jakeapp.gui.swing.JakeMainView;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * User: studpete
 * Date: Dec 29, 2008
 * Time: 12:20:54 AM
 */
public class RenameProjectAction extends ProjectAction {
    private static final Logger log = Logger.getLogger(RenameProjectAction.class);

    public RenameProjectAction() {
        super();

        putValue(Action.NAME, JakeMainView.getMainView().getResourceMap().
                getString("renameMenuItem.text"));
    }


    public void actionPerformed(ActionEvent actionEvent) {
        log.info("Rename Project: " + getProject());


    }


    @Override
    public void updateAction() {
    }
}