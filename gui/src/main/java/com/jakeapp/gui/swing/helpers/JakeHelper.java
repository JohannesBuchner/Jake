package com.jakeapp.gui.swing.helpers;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.synchronization.AttributedJakeObject;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.dialogs.generic.JSheet;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * JakeMainHelper has static functions that are used all across the ui codebase.
 * User: studpete
 * Date: Dec 21, 2008
 * Time: 5:41:44 PM
 */
public class JakeHelper {
	private static final Logger log = Logger.getLogger(JakeHelper.class);

	public static void initializeJakeMainHelper() {
	}

	/**
	 * Shows the default message dialog with custom msgCode
	 *
	 * @param msgCode: string, queried from JakeMainView-properties file.
	 */
	public static void showMsgTranslated(String msgCode, int msgType) {
		showMsg(JakeMainView.getMainView().getResourceMap().getString(msgCode), msgType);
	}

	/**
	 * Shows a message to the user.
	 */
	public static void showMsg(String msg, int msgType) {
		log.info("Show Msg: " + msg + "(Type: " + msgType + ")");
		JSheet.showMessageSheet(JakeMainView.getMainView().getFrame(),
				  msg, msgType);
	}


	public static String getPluralModifer(int clickCount) {
		return clickCount == 1 ? "" : "s";
	}

	public static void showJakeWebsite() {
		try {
			Desktop.getDesktop().browse(new URI(JakeMainView.getMainView().getResourceMap().getString("JakeWebsite")));
		} catch (IOException e) {
			log.warn("Unable to open Website!", e);
		} catch (URISyntaxException e) {
			log.warn("Unable to open Website, invalid syntax", e);
		}
	}

	public static void showInfoMsg(String s) {
		showMsg(s, JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Determine whether a jake object is editable. 'editable' in that case means that it is either
	 * not locked or the lock owner equals the local user.
	 * @param attributedJakeObject the attributedJakeObject that is to be tested.
	 * @return <code>true</code> iff the JakeObject is either not locked or the lock owner equals the
	 * local user. <code>false</code> else, i.e. if the JakeObject is locked by someone.
	 */
	public static boolean isEditable (AttributedJakeObject<? extends JakeObject> attributedJakeObject) {
		if (attributedJakeObject.isLocked()) {
			return JakeMainApp.getCore().getLockOwner(attributedJakeObject.getJakeObject()).getUserId()
			.equals(JakeMainApp.getProject().getUserId());
		}
		return true;
	}
}
