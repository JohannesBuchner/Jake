package com.doublesignal.sepm.jake.gui;

import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

/***
 * Wrapper for dialogs and standards the user is served with (e.g. dates).
 * This is just copied from another project, modify as you wish.
 */
public class UserDialogHelper {
	static Logger logger = Logger.getRootLogger();
	
	/***
	 * Generally a messagebox and a log entry
	 * @param type e.g. JOptionPane.WARNING_MESSAGE
	 */
	public static void inform(Component parent, String title, String text,
			int type) 
	{
		switch (type) {
			case JOptionPane.WARNING_MESSAGE:
				logger.warn(text);
				break;
			case JOptionPane.ERROR_MESSAGE:
				logger.error(text);
				break;
			default:
				type = JOptionPane.INFORMATION_MESSAGE;
				logger.info(text);
				break;
		}
		showMessageDialog(parent, title + "\n\n" + text, title, type);
	}
	
	/***
	 * generally a question box 
	 * @return user answered with yes
	 */
	public static boolean askForConfirmation(Component parent, String title, String question) {
		return showConfirmDialog(parent, title + "\n\n" + question, title, 
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
	}
	
	private static int showMessageDialog(Component parent, String msg, String title, int msgtype){
		JOptionPane pane = getNarrowOptionPane(0);
		pane.setMessage(msg);
		pane.setMessageType(msgtype);
		JDialog dialog = pane.createDialog(parent, title);
		dialog.setVisible(true);
		return ((Integer)pane.getValue()).intValue();
	}
	private static int showConfirmDialog(Component parent, String msg, String title, int msgtype){
		JOptionPane pane = getNarrowOptionPane(0);
		pane.setMessage(msg);
		pane.setMessageType(JOptionPane.QUESTION_MESSAGE);
		pane.setOptionType(msgtype);
		JDialog dialog = pane.createDialog(parent, title);
		dialog.setVisible(true);
		return ((Integer)pane.getValue()).intValue();
	}
	
	/***
	 * This wrapper ensures the messagebox doesn't explode when the content is 
	 * too long (i.e. window longer than screen).
	 * @param maxCharactersPerLineCount set 0 to take default
	 * @return the JOptionPane that can be used for the dialog 
	 */
	private static JOptionPane getNarrowOptionPane(
		int maxCharactersPerLineCount) 
	{
		class NarrowOptionPane extends JOptionPane {
			private static final long serialVersionUID = 1L;
			
			int maxCharactersPerLineCount = 150;
			NarrowOptionPane(int maxCharactersPerLineCount) {
				if(maxCharactersPerLineCount != 0)
					this.maxCharactersPerLineCount = maxCharactersPerLineCount;
			}
			public int getMaxCharactersPerLineCount() {
				return maxCharactersPerLineCount;
			}
		}
		return new NarrowOptionPane(maxCharactersPerLineCount);
	}
		
}