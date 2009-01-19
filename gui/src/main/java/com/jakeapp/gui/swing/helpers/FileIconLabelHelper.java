package com.jakeapp.gui.swing.helpers;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Creates a fancy label for file and folder nodes in the FileTreeTable
 */
public class FileIconLabelHelper {
	private static final Logger log = Logger.getLogger(FileIconLabelHelper.class);

	private static JFileChooser fileChooser = new JFileChooser();

	/**
	 * @param file
	 * @return
	 */
	public static Component getIconLabel(File file) {
		JLabel label = new JLabel();
		label.setOpaque(true);
		label.setBackground(new Color(255, 255, 255, 0));

		// TODO: resolve this error!
		if (file == null) {
			log.warn("Error: called getIconLabel with file NULL!");
			label.setText("<NULL>");
			return label;
		}

		Icon nativeIcon;
		try {
			nativeIcon = fileChooser.getIcon(file);
		} catch (NullPointerException e) {
			log.info("Error getting icon for " + file.getAbsolutePath());
			nativeIcon = null;
		}

		// File or folder name
		label.setText(file.getName());

		// Folders don't get annotations
		if (file.isDirectory()) {
			label.setIcon(nativeIcon);
			return label;
		}

		label.setIcon(nativeIcon);

		return label;
	}
}
