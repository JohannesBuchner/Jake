package com.jakeapp.core.synchronization;

import com.jakeapp.core.domain.TrustState;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.services.VisibilityStatus;

/**
 * Detailed Infos about the User
 *
 */
public class UserInfo {

	private TrustState trust;
	private VisibilityStatus status;
	private String nickName;
	private String firstName;
	private String lastName;
	private UserId userId;

	public UserInfo(TrustState trust, VisibilityStatus status, String nickName,
					String firstName, String lastName, UserId userId) {
		this.trust = trust;
		this.status = status;
		this.nickName = nickName;
		this.firstName = firstName;
		this.lastName = lastName;
		this.userId = userId;
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

	public UserId getUser() {
		return userId;
	}

	public boolean isOnline() {
		return getStatus() == VisibilityStatus.ONLINE;
	}
}
