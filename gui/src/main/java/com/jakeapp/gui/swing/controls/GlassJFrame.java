package com.jakeapp.gui.swing.controls;

import javax.swing.*;

/**
 * Exposes the glass panel within jframe.
 */
public class GlassJFrame extends JFrame {

	JComponent sheet;
	JPanel glass;

	public GlassJFrame(String name) {
		super(name);
		glass = (JPanel) getGlassPane();
	}


	public void hideGlassPanel() {
		glass.setVisible(false);
	}
}