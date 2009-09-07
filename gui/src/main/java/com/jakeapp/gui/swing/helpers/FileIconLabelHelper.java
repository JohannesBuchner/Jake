package com.jakeapp.gui.swing.helpers;

import com.explodingpixels.macwidgets.MacFontUtils;
import com.jakeapp.core.domain.FileObject;
import com.jakeapp.gui.swing.JakeMainApp;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Creates a fancy label for file and folder nodes in the FileTreeTable
 */
public class FileIconLabelHelper {
	private static final Logger log = Logger.getLogger(FileIconLabelHelper.class);
	private static final JLabel emptyLabel = new JLabel();
	private static JFileChooser fileChooser = new JFileChooser();

	public static Icon getIcon(File file) {
		try {
			return fileChooser.getIcon(file);
		} catch (NullPointerException e) {
			log.info("Error getting icon for " + file.getAbsolutePath());
			return null;
		}
	}


	public static Component getIconLabel(FileObject fo) {
		try {
			return getIconLabel(JakeMainApp.getCore().getFile(fo));
		} catch (Exception e) {
			log.warn("Couldn't get Icon for " + fo);
			return new JLabel();
		}
	}

	/**
	 * @param file
	 * @return Icon Component
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

		Icon nativeIcon = getIcon(file);

		// File or folder name
		label.setText(file.getName());

		// Folders don't get annotations
		if (file.isDirectory()) {
			label.setIcon(nativeIcon);
			return label;
		}

		label.setIcon(nativeIcon);
		label.setFont(MacFontUtils.ITUNES_FONT);

		return label;
	}
}
