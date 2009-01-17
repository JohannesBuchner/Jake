package com.jakeapp.gui.swing.helpers;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.File;
import java.util.Random;
import java.awt.*;

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
