package com.jakeapp.gui.swing.xcore;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;

public class JakeDatabaseTools {
	private static final Logger log = Logger.getLogger(JakeDatabaseTools.class);

	public JakeDatabaseTools() {
	}

	/**
	 * Checks if a Key is pressed on startup - forces a reset of the database
	 */
	public static void checkKeysResetDatabase() {
		Toolkit kit = Toolkit.getDefaultToolkit();
		try {
		// fixme: change key to someting more reasonable
		if (kit.getLockingKeyState(KeyEvent.VK_CAPS_LOCK)) {
			// ask to delete database
			if (JOptionPane.showConfirmDialog(null,
							"Do you want to reset the database?",
							"Jake",
							JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				log.warn("Resetting the Database!");
				String curDir = System.getProperty("user.dir");
				log.warn("Current Directory " + curDir);

				try {
					deleteHelper(new File(curDir, ".jake"));
				} catch (Exception ex) {
					log.warn("Failed resetting database.", ex);
				}
			}
		}
		}catch(Exception ex){
			// this may fail silently
		}
	}

	private static void deleteHelper(File f) {
		if(f.isFile()) {
			f.delete();
		}else {
			for(File af : f.listFiles()) {
				deleteHelper(af);
			}
		}
	}
}