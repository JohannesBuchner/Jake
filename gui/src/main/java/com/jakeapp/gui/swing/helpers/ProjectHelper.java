package com.jakeapp.gui.swing.helpers;

/**
 * @author: studpete
 */
public class ProjectHelper {

	/**
	 * Chooses the default path for the project.
	 * Uses the last part of a path.
	 *
	 * @param path
	 * @return
	 */
	public static String createDefaultPath(String path) {
		// TODO: clean from unwanted chars!
		return FileUtilities.getLastFolderFromPath(path);
	}
}
