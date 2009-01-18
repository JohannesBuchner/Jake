package com.jakeapp.gui.swing.helpers;

import com.jakeapp.core.dao.exceptions.NoSuchProjectMemberException;
import com.jakeapp.core.domain.ProjectMember;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.panels.NewsPanel;
import org.jdesktop.application.ResourceMap;

/**
 * Various Helpers for JakeObjects.
 *
 * @author: studpete
 */
public class ProjectMemberHelpers {

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
	public static String getNickOrFullName(ProjectMember member) {
		String nickOrFullName = "";
		if (member.getNickname().length() == 0) {
			// TODO: where did those properties go???
			//nickOrFullName = member.getFirstName() + " " + member.getSurName();
		} else {
			nickOrFullName = member.getNickname();
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
		ProjectMember curMember;
		try {
			JakeMainApp.getApp();
			curMember = JakeMainApp.getCore().getProjectMember(JakeMainApp.getProject(),MsgServiceHelper.getLoggedInMsgService());
		} catch (NoSuchProjectMemberException e) {
			curMember = null;
		}

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
