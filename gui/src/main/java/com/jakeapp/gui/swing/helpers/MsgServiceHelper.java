package com.jakeapp.gui.swing.helpers;

import com.jakeapp.core.domain.exceptions.NotLoggedInException;
import com.jakeapp.core.services.MsgService;
import com.jakeapp.core.services.VisibilityStatus;
import com.jakeapp.gui.swing.JakeMainApp;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Various helpers for MsgService.
 *
 * @author: studpete
 */
public class MsgServiceHelper {
	private static final Logger log = Logger.getLogger(MsgServiceHelper.class);

	/**
	 * Searches the currently logged in msg service.
	 *
	 * @return logged in msg service or null if noone is logged in.
	 */
	public static MsgService getLoggedInMsgService() {
		try {
			List<MsgService> msgServices = JakeMainApp.getCore().getMsgServics();

			for (MsgService ms : msgServices) {
				if (isLoggedIn(ms)) {
					return ms;
				}
			}
		} catch (NotLoggedInException e) {
			log.warn(e);
			ExceptionUtilities.showError(e);
		}
		return null;
	}

	/**
	 * Checks if the message service is logged in.
	 * There may be serveral Visibility stati.
	 * The service is online if it's not OFFLINE.
	 *
	 * @param msg
	 * @return boolean: online/offline state.
	 */
	public static boolean isLoggedIn(MsgService msg) {
		return msg.getVisibilityStatus() != VisibilityStatus.OFFLINE;
	}


	/**
	 * Returns true if there is a user logged in.
	 *
	 * @return true if there is a user logged in.
	 */
	public static boolean isUserLoggedIn() {
		return getLoggedInMsgService() != null;
	}
}
