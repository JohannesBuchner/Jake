package com.jakeapp.gui.swing.actions;

import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.ProjectAction;
import com.jakeapp.gui.swing.helpers.FileUtilities;
import com.jakeapp.gui.swing.helpers.ProjectHelper;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Project Action for creating a new project.
 */
public class CreateProjectAction extends ProjectAction {
	private static final Logger log = Logger.getLogger(CreateProjectAction.class);

	/**
	 * Create new <code>CreateProjectAction</code>.
	 *
	 * @param ellipsis if <code>true</code> the <code>Action.NAME</code> ends with an ellipsis (...), if
	 *                 <code>false</code> the dots are omitted.
	 */
	public CreateProjectAction(boolean ellipsis) {
		super();

		putValue(Action.NAME, JakeMainView.getMainView().getResourceMap().
						getString("createProjectMenuItem.text") + (ellipsis ? "..." : ""));

		Icon createProjectIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
						getClass().getResource("/icons/createproject.png")).getScaledInstance(32,
						32, Image.SCALE_SMOOTH));
		putValue(Action.LARGE_ICON_KEY, createProjectIcon);
	}


	public void actionPerformed(ActionEvent actionEvent) {
		log.info("Create Project action invoked");

		String path = FileUtilities.openDirectoryChooser(null,
						JakeMainView.getMainView().getResourceMap().getString(
										"createProjectDialogTitle"));
		log.info("Directory was: " + path);

		// create the directory if path was not null
		if (path != null) {
			JakeMainApp.getCore()
							.createProject(ProjectHelper.createDefaultPath(path), path,
											JakeMainApp.getMsgService());
		}
	}


	@Override
	public void updateAction() {
	}
}