package com.jakeapp.gui.swing.xcore;

import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;
import com.jakeapp.gui.swing.helpers.FileUtilities;

import java.io.File;

/**
 * This creates an example directory for the user
 *
 * @author studpete
 */
public class CreateExampleDirectory {
	public CreateExampleDirectory() {
	}

	public static void create() {
		try {
			// create a new Directory "Jake Shared Folder" on the Desktop
			File newDir = new File(FileUtilities.getUserDesktopDirectory(), "JakeShared");
			newDir.mkdir();

			// add a file
			File infoFile = new File(newDir, "Put your data here to share it.txt");
			infoFile.createNewFile();

			JakeMainApp.getCore().createProject("JakeShared", newDir.getAbsolutePath(),
							JakeMainApp.getMsgService());
		} catch (Exception e) {
			ExceptionUtilities.showError(e);
		}
	}
}
