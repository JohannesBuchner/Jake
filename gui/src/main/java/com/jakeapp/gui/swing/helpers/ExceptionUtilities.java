package com.jakeapp.gui.swing.helpers;

import com.jakeapp.gui.swing.dialogs.generic.JSheet;
import com.jakeapp.gui.swing.dialogs.generic.SheetEvent;
import com.jakeapp.gui.swing.dialogs.generic.SheetListener;
import com.jakeapp.gui.swing.globals.JakeContext;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.SynchronousQueue;

/**
 * Exception handling utilites.
 *
 * @author: studpete
 */
public class ExceptionUtilities {
	private static final Logger log = Logger.getLogger(ExceptionUtilities.class);
	private static boolean exceptionDisplayActive = false;
	private static Queue<ExceptionSaver> exceptionQueue =
					new SynchronousQueue<ExceptionSaver>();

	private static class ExceptionSaver {
		private ExceptionSaver(Exception exception, String msg) {
			this.exception = exception;
			this.msg = msg;
		}

		public Exception exception;
		public String msg;
	}

	/**
	 * Displays an exception to the user.
	 * Function is Thread-Safe.
	 *
	 * @param msg
	 * @param e:  the exception to parse and show
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

	private static String getExceptionText(Throwable e, String newline, int maxLen) {
		StringBuilder sb = new StringBuilder();
		if (e.getMessage() != null) {
			sb.append(e.getMessage()).append(newline + newline);
		}
		StackTraceElement[] trace = e.getStackTrace();
		for (int i = 0; i < trace.length && i < maxLen; i++)
			sb.append("\tat ").append(trace[i]).append(newline);

		Throwable ourCause = e;
		if (ourCause.getCause() != null) {
			ourCause = ourCause.getCause();
			sb.append("caused by: ");
			sb.append(newline);
			sb.append(getExceptionText(ourCause, newline, maxLen));
		}
		return sb.toString();
	}

	private static void internalShowError(String msg, final Exception e) {
		if (e == null) {
			log.info("Tried to show empty exception");
		}

		// log.warn("showing error", e);
		e.printStackTrace();

		if (msg == null) {
			msg = e.getClass().getName();
		}

		final String clipBoard = msg + "\n\n" + getExceptionText(e, "\n", 200);

		// show only one exception, save the rest
		if (exceptionDisplayActive) {
			exceptionQueue.add(new ExceptionSaver(e, msg));
		} else {
			exceptionDisplayActive = true;
			
			String okMsg = "Ok";
			int numExceptions = exceptionQueue.size();
			if (numExceptions > 0) {
				okMsg += " (" + numExceptions + " more)";
			}
			List<String> options = new ArrayList<String>();
			options.add(okMsg);
			options.add("Copy to Clipboard");

			if (numExceptions > 0) {
				options.add("Ignore All");
			}


			JSheet.showOptionSheet(JakeContext.getFrame(),
							"<html><h2>" + msg + "</h2><br><font size=2>" + getExceptionText(e,
											"<br>", 20) + "</font><br><br>" + getApologyText() + "</html>",
							JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, options.toArray(),
							options.get(0), new SheetListener() {
								@Override public void optionSelected(SheetEvent evt) {
									if (evt.getOption() == 1) {
										CopyText copyText = new CopyText();
										copyText.setString(clipBoard);
									}else if(evt.getOption() == 2) {
										exceptionQueue.clear();
									}
								}
							});

			// done - display more?
			exceptionDisplayActive = false;

			if(!exceptionQueue.isEmpty()) {
				ExceptionSaver exs = exceptionQueue.poll();
				internalShowError(exs.msg, exs.exception);
			}
		}
	}


	/**
	 * Helper to copy text into clipboard
	 */
	public final static class CopyText implements ClipboardOwner {

		public void lostOwnership(Clipboard aClipboard, Transferable aContents) {
			//do nothing
		}

		public void setString(String data) {
			StringSelection stringSelection = new StringSelection(data);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, this);
		}

		public String getString() {
			String result = "";
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			Transferable contents = clipboard.getContents(null);
			boolean hasTransferableText = (contents != null) && contents
							.isDataFlavorSupported(DataFlavor.stringFlavor);
			if (hasTransferableText) {
				try {
					result = (String) contents.getTransferData(DataFlavor.stringFlavor);
				} catch (UnsupportedFlavorException e) {
					System.out.println(e);
				} catch (IOException e) {
					System.out.println(e);
				}
			}
			return result;
		}
	}


	/**
	 * And people say computer science people are boring...
	 *
	 * @return
	 */
	private static String getApologyText() {
		String[] apologies = {"Sorry.", "Haha!", "Idiot.", "We are deeply sorry.",
						"We are so sorry. Really.", "In your face, sucker!",
						"Please accept our apologies", "Smack the developer in the face.",
						"Simon says: Jump in the air and cry!",
						"Cold and dead Jake lies<br>amidst many exceptions!<br>on the stony ground.",
						"Somebody set up us the exception!", "All your crash are belong to us.",
						"oMGfZ ePIC FaIL!!!!!1111oneeleven", "O RLY? I CAN HAS EXCEPTION?",
						"Try again tomorrow. Or the day after. Or how about: NEVER!",
						"Oh what a ******** piece of *********** *****! ****** this **** *********** is ******!!!!"};
		return apologies[new Random().nextInt(apologies.length)];
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
