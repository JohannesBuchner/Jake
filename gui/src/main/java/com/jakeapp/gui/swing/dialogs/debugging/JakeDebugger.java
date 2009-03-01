package com.jakeapp.gui.swing.dialogs.debugging;

import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXPanel;

import javax.swing.*;

public class JakeDebugger extends JDialog {
	public JakeDebugger() {
		initComponents();
		this.setVisible(true);
	}

	private void initComponents() {
		JXPanel panel = new JXPanel(new MigLayout("wrap 2"));
		panel.add(new JLabel("Library Path:"));
		panel.add(new JLabel(System.getProperty("java.library.path")));
		this.add(panel);
		this.pack();
	}
}