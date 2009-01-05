package com.jakeapp.gui.swing.actions.abstracts;

import javax.swing.*;

/**
 * User: studpete
 * Date: Dec 29, 2008
 * Time: 2:11:31 AM
 */
public abstract class JakeAction extends AbstractAction {
    /**
     * Executes the Action
     */
    public void execute() {
        actionPerformed(null);
    }
}
