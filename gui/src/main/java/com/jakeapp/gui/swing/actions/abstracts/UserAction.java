package com.jakeapp.gui.swing.actions.abstracts;

import com.jakeapp.core.domain.TrustState;
import com.jakeapp.core.synchronization.UserInfo;
import com.jakeapp.gui.swing.JakeMainApp;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;

/**
 * PeopleListActions - extends the ProjectAction to save the PeopleList
 * (needed to determine the people that are selected)
 */
public abstract class UserAction extends ProjectAction {
	private static final Logger log = Logger.getLogger(UserAction.class);
	private JList list;

	public UserAction(JList list) {
		setList(list);
	}

	/**
	 * Checks a UserId in the list for a specific TrustState.
	 *
	 * @param trust: The TrustState to compare.
	 * @return true if TrustState is equal, false if not equal or != 1 members selected.
	 */
	protected boolean checkUserStatus(TrustState trust) {
		boolean selected;
		if (getList().getSelectedValue() != null) {
			UserInfo userInfo = (UserInfo) getList().getSelectedValue();
			selected = userInfo.getTrust() == trust;
		} else {
			selected = false;
		}
		return selected;
	}

	/**
	 * Set the TrustState for selected People in JList.
	 *
	 * @param trust: new truststate.
	 */
	// fixme: refactor out
	protected void setUserTrustState(TrustState trust) {
		// support multiselect
		for (Object oMember : getList().getSelectedValues()) {
			UserInfo member = (UserInfo) oMember;

			if (member == null) {
				log.warn("Action TrustNoPeopleAction failed for " + oMember);
				return;
			} else {
				JakeMainApp.getCore().setTrustState(getProject(), member.getUser(), trust);
			}
		}
	}

	/**
	 * Checks if a user is selected in the list
	 * @return
	 */
	protected boolean hasSelectedUser() {
		return getList().getSelectedValue() != null;
	}

	/**
	 * Checks if at least one user is selected in the list
	 * @return
	 */
	protected boolean hasSelectedUsers() {
		return getList().getSelectedValues().length > 0;
	}

	/**
	 * Returns the UserInfo of the selected User.
	 * Returns null if more than one user are selected.
	 * @return
	 */
	protected UserInfo getSelectedUser() {
		UserInfo userInfo = (UserInfo)getList().getSelectedValue();
		if(userInfo != null) {
			return userInfo;
		} else {
			return null;
		}
	}

	/**
	 * Returns a List of selected users.
	 * Returns empty list if no users were selected.
	 * @return
	 */
	protected List<UserInfo> getSelectedUsers() {
		List<UserInfo> userInfos = new ArrayList<UserInfo>();
		Object[] usersRaw = getList().getSelectedValues();
		for(Object userRaw : usersRaw) {
			userInfos.add((UserInfo)userRaw);
		}
		return userInfos;
	}


	public JList getList() {
		return list;
	}

	public void setList(JList list) {
		this.list = list;
	}
}
