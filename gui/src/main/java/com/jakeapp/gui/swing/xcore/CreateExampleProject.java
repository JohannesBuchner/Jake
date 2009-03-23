package com.jakeapp.gui.swing.xcore;

import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.globals.JakeContext;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;
import com.jakeapp.gui.swing.helpers.FileUtilities;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * This creates an example directory for the user
 *
 * @author studpete
 */
public class CreateExampleProject {
	public CreateExampleProject() {
	}

	public static void create() {
		try {
			// create a new Directory "Jake Shared Folder" on the Desktop
			File newDir = new File(FileUtilities.getUserDesktopDirectory(), "JakeShared");
			newDir.mkdir();

			// add a file
			File infoFile = new File(newDir, "Put your data here to share it.txt");
			infoFile.createNewFile();

			FileWriter writer = new FileWriter(infoFile);
			PrintWriter out = new PrintWriter(writer);

			out.println("Jake is a modern, shortened form of the male name Jacob");
			out.close();
			writer.close();

			JakeMainApp.getCore().createProject("JakeShared", newDir.getAbsolutePath(),
							JakeContext.getMsgService(), true);
		} catch (Exception e) {
			ExceptionUtilities.showError(e);
		}
	}
}
