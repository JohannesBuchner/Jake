package com.jakeapp.gui.swing.helpers.styler;

import org.jdesktop.swingx.painter.Painter;

import javax.swing.*;

/**
 * @author studpete
 */
public interface Styler {
    /**
     * Adapts the toggle button in the toolbar, changes via state.
     *
     * @param btn
     */
    public void MakeWhiteRecessedButton(JButton btn);

    /**
     * Styles the Toolbar Buttons depending on the PF
     *
     * @param jToggleButton
     */
    void styleToolbarButton(JToggleButton jToggleButton);

    /**
     * Get the content panel background painter.
     * Can be adapted per platform
     *
     * @return
     */
    Painter getContentPanelBackgroundPainter();
}
