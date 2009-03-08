package com.jakeapp.gui.swing.helpers.styler;

import org.jdesktop.swingx.painter.Painter;

import javax.swing.*;
import java.awt.*;

/**
 * @author studpete
 */
public interface Styler {
	/**
	 * Adapts the toggle button in the toolbar, changes via state.
	 *
	 * @param btn
	 */
	public void makeWhiteRecessedButton(JButton btn);

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

	/**
	 * Returns the Font for Bold large fonts.
	 *
	 * @return
	 */
	Font getH1Font();

	/**
	 * Returns the Font for Bold medium fonts.
	 * @return
	 */
	Font getH2Font();

	/**
	 * Returns the large font used in sheets/dialogs
	 *
	 * @return larger font than normal
	 */
	Font getSheetLargeFont();

	/**
	 * Returns the default color for window background
	 *
	 * @return
	 */
	Color getWindowBackground();

	/**
	 * Returns the (dark) filter color for e.g. filepane
	 *
	 * @param windowFocus
	 * @return
	 */
	Color getFilterPaneColor(boolean windowFocus);
}
