package com.jakeapp.gui.swing.helpers;

import com.jakeapp.gui.swing.JakeMainApp;
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
	 * @param msg
	 * @param e: the exception to parse and show
	 */
	public static void showError(final String msg, final Exception e) {
		if (SwingUtilities.isEventDispatchThread()) {
			internalShowError(e);
		} else {
			Runnable runner = new Runnable() {
				public void run() {
					internalShowError(msg, e);
				}
			};

			SwingUtilities.invokeLater(runner);
		}
	}

	// TODO: provide a same way of logging, showing details.
	public static void showError(final Exception e) {
		showError(null, e);
	}

	/**
	 * Always will be invoked in thread awt-0
	 *
	 * @param e
	 */
	private static void internalShowError(Exception e) {
		internalShowError(null, e);
	}

	private static void internalShowError(String msg, Exception e) {
		if(e == null) {
			log.info("Tried to show empty exception");
		}

		//log.warn("showing error", e);
		e.printStackTrace();

		if (msg == null) {
			JSheet.showMessageSheet(JakeMainApp.getFrame(),
							"<html><h2>" + e.getClass().getName() + "</h2><br><b>" + e
											.getMessage() + "</b><br><br> + " + DebugHelper
											.arrayToString(e.getStackTrace(),
															DebugHelper.DebugFormat.BRACES, 12) + "</html>",
							JOptionPane.ERROR_MESSAGE, null);
		} else {
			JSheet.showMessageSheet(JakeMainApp.getFrame(),
							"<html><h2>" + msg + "</h2><br><b>" + e.getClass().getName() + ": " + e
											.getMessage() + "</b><br><br> + " + DebugHelper
											.arrayToString(e.getStackTrace(),
															DebugHelper.DebugFormat.BRACES, 12) + "</html>",
							JOptionPane.ERROR_MESSAGE, null);
		}
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

	public static void showError(String s, IllegalArgumentException e) {
		showError(s, e);
	}
}
