package com.jakeapp.gui.swing.helpers;

import com.jakeapp.gui.swing.globals.JakeContext;
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
		internalShowError(e.getMessage(), e);
	}

	private static String getExceptionText(Throwable e, String newline) {
		StringBuilder sb = new StringBuilder();
		if(e.getMessage() != null) {
			sb.append(e.getMessage()).append(newline + newline);
		}
        StackTraceElement[] trace = e.getStackTrace();
        for (int i=0; i < trace.length; i++)
            sb.append("\tat ").append(trace[i]).append(newline);

        Throwable ourCause = e;
        if (ourCause.getCause() != null) {
        	ourCause = ourCause.getCause();
            sb.append("caused by: " + newline);
            sb.append(getExceptionText(ourCause, newline));
        }
		return sb.toString();
	}
	
	private static void internalShowError(String msg, Exception e) {
		if(e == null) {
			log.info("Tried to show empty exception");
		}

		// log.warn("showing error", e);
		e.printStackTrace();

		if (msg == null) {
			msg = e.getClass().getName();
		}
		JSheet.showMessageSheet(JakeContext.getFrame(), "<html><h2>" + msg
				+ "</h2><br><b>" + e.getMessage() + "</b><br><br> + "
				+ getExceptionText(e, "<br>") + "<br><br>Sorry.</html>",
				JOptionPane.ERROR_MESSAGE, null);
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
