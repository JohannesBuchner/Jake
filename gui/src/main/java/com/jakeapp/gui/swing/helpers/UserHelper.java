package com.jakeapp.gui.swing.helpers;

import com.jakeapp.core.domain.UserId;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.panels.NewsPanel;
import org.apache.log4j.Logger;
import org.jdesktop.application.ResourceMap;

/**
 * Various Helpers for JakeObjects.
 *
 */
public class UserHelper {
	private static final Logger log = Logger.getLogger(UserHelper.class);

	// get notes resource map
	// TODO: move to own
	private static final ResourceMap newsResourceMap = org.jdesktop.application.Application.getInstance(
			  com.jakeapp.gui.swing.JakeMainApp.class).getContext()
			  .getResourceMap(NewsPanel.class);

	/**
	 * Returns the Nickname, or - if no nickname is set - the Full Name.
	 *
	 * @param member: the member to evaluate
	 * @return nickname or full name, if nn not set.
	 */
	public static String getNickOrFullName(UserId member) {
		String nickOrFullName = "TODO";

		// TODO
		//nickOrFullName = member.getNickname();

		return nickOrFullName;
	}

	/**
	 * Returns the Nickname/FullName OR localized 'You' if its yourself.
	 *
	 * @param member: the member to evaluate
	 * @return nick/fullname or "you" localized
	 */
	public static String getLocalizedUserNick(UserId member) {
		if (isCurrentProjectMember(member)) {
			return newsResourceMap.getString("eventsYourself");
		} else {
			return getNickOrFullName(member);
		}
	}

	public static boolean isCurrentProjectMember(UserId member) {
		return member == JakeMainApp.getCurrentUser();
	}


	public static String getNickOrFullName(UserId member, int maxlen) {
		return StringUtilities.maxLen(getNickOrFullName(member), maxlen);
	}
}