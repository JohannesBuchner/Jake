package com.jakeapp.core.synchronization;

import com.jakeapp.core.domain.TrustState;
import com.jakeapp.core.domain.User;
import com.jakeapp.core.services.VisibilityStatus;

/**
 * Detailed information about the user
 * 
 * @see User
 */
public class UserInfo {

	private TrustState trust;
	private VisibilityStatus status;
	private String nickName;
	private String firstName;
	private String lastName;
	private User user;

	public UserInfo(TrustState trust, VisibilityStatus status, String nickName,
					String firstName, String lastName, User user) {
		this.trust = trust;
		this.status = status;
		this.nickName = nickName;
		this.firstName = firstName;
		this.lastName = lastName;
		this.user = user;
	}

	public TrustState getTrust() {
		return trust;
	}

	public VisibilityStatus getStatus() {
		return status;
	}

	public String getNickName() {
		return nickName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public User getUser() {
		return user;
	}

	public boolean isOnline() {
		return getStatus() == VisibilityStatus.ONLINE;
	}
}
