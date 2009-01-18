package com.jakeapp.gui.swing.helpers;

import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.JakeMainView;

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

	/**
	 * @param project
	 * @return
	 */
	public static String printProjectStatus(Project project) {

		// TODO. just todo.
		StringBuilder str = new StringBuilder("Project is ");
		str.append(getProjectStartStopString(project));

		return str.toString();
	}

	/**
	 * Evaluates the Project and returns a Start/Stop-String depending on its state.
	 *
	 * @param project
	 * @return String with either Start or Stop.
	 */
	public static String getProjectStartStopString(Project project) {
		String startStopString;
		if (project == null || !project.isStarted()) {
			startStopString = JakeMainView.getMainView().getResourceMap().getString("projectTreeStartProject");
		} else {
			startStopString = JakeMainView.getMainView().getResourceMap().getString("projectTreeStopProject");
		}

		return startStopString;
	}
}
