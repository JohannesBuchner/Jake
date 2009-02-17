package com.jakeapp.gui.swing.helpers;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.exceptions.FileOperationFailedException;
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
	 * @param fo
	 * @return Icon Component
	 */
	public static Component getIconLabel(FileObject fo) {
		JLabel label = new JLabel();
		label.setOpaque(true);
		label.setBackground(new Color(255, 255, 255, 0));

		File file = null;
		try {
			file = JakeMainApp.getCore().getFile(fo);
		} catch (FileOperationFailedException e) {
			ExceptionUtilities.showError(e);
		}

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
