/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jakeapp.gui.swing.controls;

import com.jakeapp.gui.swing.interfaces.Closeable;

import javax.swing.*;

/**
 * @author studpete
 */
public class CloseableDialogCallback implements Closeable {
    JFrame frame;
    JDialog dialog;

    public CloseableDialogCallback(JFrame frame, JDialog dialog) {
        this.frame = frame;
        this.dialog = dialog;
    }

    @Override
    public void close() {
        DisabledGlassPane glass = (DisabledGlassPane) frame.getGlassPane();
        glass.deactivate();
        glass.remove(dialog);
    }

}
