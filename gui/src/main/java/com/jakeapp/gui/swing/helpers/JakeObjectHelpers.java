package com.jakeapp.gui.swing.helpers;

import com.jakeapp.core.domain.ProjectMember;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.panels.NotesPanel;
import org.jdesktop.application.ResourceMap;

/**
 * Various Helpers for JakeObjects.
 *
 * @author: studpete
 */
public class JakeObjectHelpers {

	// get notes resource map
	private static final ResourceMap newsResourceMap = org.jdesktop.application.Application.getInstance(
			  com.jakeapp.gui.swing.JakeMainApp.class).getContext()
			  .getResourceMap(NotesPanel.class);

	/**
	 * Returns the Nickname, or - if no nickname is set - the Full Name.
	 *
	 * @param member: the member to evaluate
	 * @return nickname or full name, if nn not set.
	 */
	public static String getNickOrFullName(ProjectMember member) {
		String nickOrFullName;
		if (member.getUserId().getNickname().length() == 0) {
			nickOrFullName = member.getUserId().getFirstName() + " " + member.getUserId().getSurName();
		} else {
			nickOrFullName = member.getUserId().getNickname();
		}
		return nickOrFullName;
	}

	/**
	 * Returns the Nickname/FullName OR localized 'You' if its yourself.
	 *
	 * @param member: the member to evaluate
	 * @return nick/fullname or "you" localized
	 */
	public static String getLocalizedUserNick(ProjectMember member) {
		ProjectMember curMember = JakeMainApp.getApp().getCore().getCurrentProjectMember();

		if (member == curMember) {
			return newsResourceMap.getString("eventsYourself");
		} else {
			return getNickOrFullName(member);
		}
	}

	public static String getNickOrFullName(ProjectMember member, int maxlen) {
		return StringUtilities.maxLen(getNickOrFullName(member), maxlen);
	}
}
