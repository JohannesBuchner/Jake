/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jakeapp.gui.swing.interfaces;

import javax.swing.*;

/**
 * @author studpete
 */
public interface Sheet {
    public JButton defaultEnterButton();

    public void setClosableCallback(Closeable master);
    // public JButton defaultCancelButton();
}
