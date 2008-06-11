package com.doublesignal.sepm.jake.gui;

import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.doublesignal.sepm.jake.gui.i18n.ITranslationProvider;
import com.doublesignal.sepm.jake.gui.i18n.TranslatorFactory;

/**
 * Wrapper for dialogs and standards the user is served with (e.g. dates). This
 * is just copied from another project, modify as you wish.
 */
public class UserDialogHelper {
	private static final Logger log = Logger.getLogger(UserDialogHelper.class);
	
	private static final ITranslationProvider translator = TranslatorFactory.getTranslator();

    public static void inform(Component parent, String title, String text) {
        inform(parent, title, text, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Generally a messagebox and a log4j log entry
     *
     * @param parent the parent Component of this dialog (usually &quot;this&quot;)
     * @param title  the title of the dialog
     * @param text   the text of the dialog
     * @param type   e.g. JOptionPane.WARNING_MESSAGE, JOptionPane.ERROR_MESSAG or
     *               JOptionPane.INFORMATION_MESSAGE
     */
    public static void inform(Component parent, String title, String text,
                              int type) {
        switch (type) {
            case JOptionPane.WARNING_MESSAGE:
                log.warn(text);
                break;
            case JOptionPane.ERROR_MESSAGE:
                log.error(text);
                break;
            default:
                type = JOptionPane.INFORMATION_MESSAGE;
                log.info(text);
                break;
        }
        showMessageDialog(parent, title + "\n\n" + text, title, type);
    }

    /**
     * a yes-no question box
     *
     * @param parent   the parent component of this dialog, usually &quot;this&quot;
     * @param title    the title of this dialog
     * @param question the text/question of this dialog to be asked. should be so
     *                 that it can answered with yes/no
     * @return true, if the user answered with yes, false otherwise
     */
    public static boolean askForConfirmation(Component parent, String title,
                                             String question) {
        return showConfirmDialog(parent, title + "\n\n" + question, title,
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
    
    private static int showMessageDialog(Component parent, String msg,
                                         String title, int msgtype) {
        JOptionPane pane = getNarrowOptionPane(0);
        pane.setMessage(msg);
        pane.setMessageType(msgtype);
        JDialog dialog = pane.createDialog(parent, title);
        dialog.setVisible(true);
        return (Integer) pane.getValue();
    }

    private static int showConfirmDialog(Component parent, String msg,
                                         String title, int msgtype) {
        JOptionPane pane = getNarrowOptionPane(0);
        pane.setMessage(msg);
        pane.setMessageType(JOptionPane.QUESTION_MESSAGE);
        pane.setOptionType(msgtype);
        JDialog dialog = pane.createDialog(parent, title);
        dialog.setVisible(true);
        return (Integer) pane.getValue();
    }

    /**
     * This wrapper ensures the messagebox doesn't explode when the content is
     * too long (i.e. window longer than screen).
     *
     * @param maxCharactersPerLineCount set 0 to take default
     * @return the JOptionPane that can be used for the dialog
     */
    private static JOptionPane getNarrowOptionPane(int maxCharactersPerLineCount) {
        class NarrowOptionPane extends JOptionPane {
            private static final long serialVersionUID = 1L;

            int maxCharactersPerLineCount = 150;

            NarrowOptionPane(int maxCharactersPerLineCount) {
                if (maxCharactersPerLineCount != 0)
                    this.maxCharactersPerLineCount = maxCharactersPerLineCount;
            }

            public int getMaxCharactersPerLineCount() {
                return maxCharactersPerLineCount;
            }
        }
        return new NarrowOptionPane(maxCharactersPerLineCount);
    }

    /**
     * creates an error-dialog box
     *
     * @param parent       the parent component, usually &quot;this&quot;
     * @param errorMessage the message to be shown.
     */
    public static void error(Component parent, String errorMessage) {
        error(parent, errorMessage, "");
    }

    /**
     * creates an error-dialog box with a sepcific title
     *
     * @param parent       the parent component, usually &quot;this&quot;
     * @param title        the title of the dialog box
     * @param errorMessage the error message
     */
    public static void error(Component parent, String title, String errorMessage) {
    	inform(parent, errorMessage, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * creates a warning-dialog box
     *
     * @param parent         the parent component, usually &quot;this&quot;
     * @param warningMessage the warning message
     */
    public static void warning(Component parent, String warningMessage) {
        warning(parent, "", warningMessage);
    }

    /**
     * creates a warning-dialog box with a specific title
     *
     * @param parent         the parent component, usually &quot;this&quot;
     * @param title          the title of the dialog box
     * @param warningMessage the warning message
     */
    public static void warning(Component parent, String title, String warningMessage) {
    	inform(parent, warningMessage, title, JOptionPane.WARNING_MESSAGE);
    }


    /**
     * creates an error-dialog box using the i18n-identifier
     *
     * @param parent         the parent component, usually &quot;this&quot;
     * @param i18nIdentifier the i18nIdentifier to be looked up and shown.
     */
    public static void translatedError(Component parent, String i18nIdentifier) {
        error(parent, translator.get(i18nIdentifier), "");
    }
    public static void translatedError(Component parent, String i18ntitle, String i18nmessage) {
        error(parent, translator.get(i18ntitle), translator.get(i18nmessage));
    }

    /**
     * creates an inform-dialog box using the i18n-identifier
     *
     * @param parent         the parent component, usually &quot;this&quot;
     * @param i18nIdentifier the identifier of the message to be shown
     */
    public static void translatedInform(Component parent, String i18nIdentifier) {
        inform(parent, translator.get(i18nIdentifier), "");
    }

    /**
     * creates an error-dialog box using the i18n-identifier
     *
     * @param parent         the parent component, usually &quot;this&quot;
     * @param i18nIdentifier the identifier of the message to be shown
     */
    public static void translatedWarning(Component parent, String i18nIdentifier) {
        warning(parent, translator.get(i18nIdentifier), "");
    }
    
    /**
     * Asks the user to enter a line
     * @param parent
     * @param title
     * @param question
     * @return null if canceled, answer else
     */
    public static String showTextInputDialog(Component parent, String title, String question){
        return JOptionPane.showInputDialog(parent, title + "\n" + question, question);
    }
    
    /**
     * Asks the user to enter a line, uses the translator
     * @param parent
     * @param title
     * @param question
     * @return null if canceled, answer else
     */
    public static String showTranslatedTextInputDialog(
    		Component parent, String i18ntitle, String i18nquestion)
    {
        return showTextInputDialog(parent, translator.get(i18ntitle), 
        		translator.get(i18nquestion)); 
    }

}
