package com.jakeapp.gui.swing.helpers;

import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.callbacks.ErrorCallback;
import com.jakeapp.gui.swing.dialogs.generic.JSheet;
import org.apache.log4j.Logger;

import javax.swing.*;

/**
 * Exception handling utilites.
 *
 * @author: studpete
 */
public class ExceptionUtilities {
	private static final Logger log = Logger.getLogger(ExceptionUtilities.class);

	/**
	 * Displays an exception to the user.
	 * Function is Thread-Safe.
	 *
	 * @param e: the exception to parse and show
	 */
	// TODO: provide a same way of logging, showing details.
	// Custom Dialog would be great!
	public static void showError(final Exception e) {
		if (SwingUtilities.isEventDispatchThread()) {
			internalShowError(e);
		} else {
			Runnable runner = new Runnable() {
				public void run() {
					internalShowError(e);
				}
			};

			SwingUtilities.invokeLater(runner);
		}
	}

	/**
	 * Always will be invoked in thread awt-0
	 *
	 * @param e
	 */
	private static void internalShowError(Exception e) {

		// /log.warn(e.getMessage());
		//e.printStackTrace();

		JSheet.showMessageSheet(JakeMainApp.getFrame(),
				  "<html><h2>" + e.getClass().getName() + "</h2><br><b>" + e.getMessage() + "</b><br><br> + " +
							 DebugHelper.arrayToString(e.getStackTrace(), true, 12) + "</html>",
				  JOptionPane.ERROR_MESSAGE, null);
	}

	/**
	 * Convenience wrapper for JakeErrorEvent.
	 * Function is Thread-Safe.
	 *
	 * @param ee
	 */
	public static void showError(ErrorCallback.JakeErrorEvent ee) {
		showError(ee.getException());
	}

	/**
	 * Displays an error to the user
	 * Function is Thread-Safe.
	 *
	 * @param msg: message as string
	 */
	public static void showError(String msg) {
		showError(new RuntimeException(msg));
	}
}
