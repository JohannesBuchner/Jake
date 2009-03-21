package com.jakeapp.gui.swing.helpers;

import com.jakeapp.core.domain.User;
import com.jakeapp.core.synchronization.UserInfo;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.globals.JakeContext;
import com.jakeapp.gui.swing.panels.NewsPanel;
import org.apache.log4j.Logger;
import org.jdesktop.application.ResourceMap;

/**
 * Various Helpers for JakeObjects.
 */
public class UserHelper {
	private static final Logger log = Logger.getLogger(UserHelper.class);

	// get notes resource map
	// TODO: move to own
	private static final ResourceMap newsResourceMap =
					org.jdesktop.application.Application
									.getInstance(com.jakeapp.gui.swing.JakeMainApp.class).getContext()
									.getResourceMap(NewsPanel.class);

	/**
	 * Returns the Nickname, or - if no nickname is set - the Full Name.
	 *
	 * @param user: the user to evaluate
	 * @return nickname or full name, if nn not set.
	 */
	public static String getNickOrFullName(User user) {
		return getNickOrFullName(JakeMainApp.getCore().getUserInfo(user));
	}


	/**
	 * Returns the Nickname, or - if no nickname is set - the First Name.
	 *
	 * @param user: the user to evaluate
	 * @return nickname or full name, if nn not set.
	 */
	public static String getNickOrFullName(UserInfo user) {
		if (user.getNickName().length() > 0) {
			return cleanUserId(user.getNickName());
		}
		if (user.getFirstName().length() > 0) {
			return cleanUserId(user.getFirstName());
		} else {
			return cleanUserId(user.getUser().getUserId());
		}
	}

	/**
	 * Returns the Nickname/FullName OR localized 'You' if its yourself.
	 *
	 * @param member: the member to evaluate
	 * @return nick/fullname or "you" localized
	 */
	public static String getLocalizedUserNick(User member) {
		if (member == null || isCurrentProjectMember(member)) {
			return newsResourceMap.getString("eventsYourself");
		} else {
			return getNickOrFullName(member);
		}
	}

	public static boolean isCurrentProjectMember(User member) {
		return member.equals(JakeContext.getCurrentUser());
	}


	public static String getNickOrFullName(User member, int maxlen) {
		return StringUtilities.maxLen(getNickOrFullName(member), maxlen);
	}

	/**
	 * Cleans the User id in form of xx@yy.com / 8885ca-... into xx@yy.com
	 * @param userId
	 * @return
	 */
	public static String cleanUserId(String userId) {
		int slashPos = userId.lastIndexOf('/');
		if (slashPos <= 0)
			slashPos = userId.length();

		return userId.substring(0, slashPos);
	}

	public static String cleanUserId(User user) {
		return cleanUserId(user.getUserId());
	}
}