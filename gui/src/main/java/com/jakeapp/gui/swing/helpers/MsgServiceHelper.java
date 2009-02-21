package com.jakeapp.gui.swing.helpers;

import com.jakeapp.core.services.MsgService;
import com.jakeapp.core.services.VisibilityStatus;
import com.jakeapp.gui.swing.JakeMainApp;
import org.apache.log4j.Logger;

/**
 * Various helpers for MsgService.
 *
 * @author: studpete
 */
public class MsgServiceHelper {
	private static final Logger log = Logger.getLogger(MsgServiceHelper.class);


	/**
	 * Checks if the message service is logged in.
	 * There may be serveral Visibility stati.
	 * The service is online if it's not OFFLINE.
	 *
	 * @param msg
	 * @return boolean: online/offline state.
	 */
	public static boolean isLoggedIn(MsgService msg) {
		if (msg == null) {
			log.warn("Tried to check LogIn state for NULL msg service!");
			return false;
		}
		log.debug("Getting msg-service visibility-status: " + msg.getVisibilityStatus().toString());
		return msg.getVisibilityStatus() != VisibilityStatus.OFFLINE;
	}


	/**
	 * Returns true if there is a user logged in.
	 *
	 * @return true if there is a user logged in.
	 */
	public static boolean isCurrentUserLoggedIn() {
		return isLoggedIn(JakeMainApp.getMsgService());
	}
}
