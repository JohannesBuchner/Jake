package com.jakeapp.gui.swing.panels;

import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXPanel;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Inspector Panel
 * Shows extended File Info
 *
 * @author: studpete
 */
public class InspectorPanel2 extends JXPanel {

	public InspectorPanel2() {

		this.setLayout(new MigLayout("nogrid, debug"));

		initComponents();
	}

	private void initComponents() {
		this.add(new JLabel("works"));

		// TODO: cleanup mess

		// TODO: enable as soon as this is in repo
		//Icon ico = ch.randelshofer.quaqua.filechooser.Files.getIcon(new File("/"), 128);


		JLabel icolabel = new JLabel();
		//icolabel.setIcon(ico);
		this.add(icolabel);

		// Create a File instance of the file
		File file = new File("C:\\");

		try {
			sun.awt.shell.ShellFolder sf = sun.awt.shell.ShellFolder.getShellFolder(file);

			// Get large icon
			Icon icon = new ImageIcon(sf.getIcon(true), sf.getFolderType());
			JLabel icolabel2 = new JLabel("winico");
			icolabel2.setIcon(icon);
			this.add(icolabel2);
		} catch (
				  FileNotFoundException e) {
		}
	}
}


