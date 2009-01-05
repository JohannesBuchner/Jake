package com.jakeapp.core.domain;

import java.util.UUID;

import com.jakeapp.jake.ics.UserId;

/**
 * 
 * @author johannes
 *
 */
abstract public class ICSUserId extends UserId {

	private UUID uuid;

	/**
	 * @deprecated never use locally, only for hibernate. UserId should never change when running
	 * @param userid
	 */
	@Deprecated
	public ICSUserId() {
		// default ctor for hibernate
		super(null);
	}

	public ICSUserId(UUID uuid, String userId) {
		super(userId);
		setUuid(uuid);
	}
	
	public UUID getUuid() {
		return this.uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	/**
	 * @deprecated never use locally, only for hibernate. UserId should never change when running
	 * @param userid
	 */
	@Deprecated
	public void setUserId(String userid) {
		this.userId = userid;
	}
}
