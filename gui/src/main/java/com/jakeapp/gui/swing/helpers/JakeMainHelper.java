package com.jakeapp.gui.swing.helpers;

import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.dialogs.generic.JSheet;
import org.apache.log4j.Logger;

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
public class JakeMainHelper {
	private static final Logger log = Logger.getLogger(JakeMainHelper.class);

	public static void initializeJakeMainHelper() {
	}

	/**
	 * Shows the default message dialog with custom msgCode
	 *
	 * @param msgCode: string, queried from JakeMainView-properties file.
	 */
	public static void showMsg(String msgCode, int msgType) {
		JSheet.showMessageSheet(JakeMainView.getMainView().getFrame(),
				  JakeMainView.getMainView().getResourceMap().getString(msgCode), msgType);
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
}
