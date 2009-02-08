package com.jakeapp.core.synchronization;

import com.jakeapp.core.domain.JakeObject;

/**
 * struct for giving over to the gui
 * 
 * @author johannes
 */
public class JakeObjectSyncStatus {

	private JakeObject jo;

	private long lastModification;

	/**
	 * This file has been modified by *us*, but hasn't been announced yet
	 */
	private boolean locallyModified;

	/**
	 * This file has been modified by someone else, but hasn't been pulled yet
	 */
	private boolean remotelyModified;

	/**
	 * This file only exists locally.
	 */
	private boolean onlyLocal;

	/**
	 * This file only exists in the cloud.
	 */
	private boolean onlyRemote;

	public JakeObjectSyncStatus(JakeObject jo, long lastModification, boolean locallyModified,
			boolean remotelyModified, boolean onlyLocal, boolean onlyRemote) {
		super();
		this.jo = jo;
		this.lastModification = lastModification;
		this.locallyModified = locallyModified;
		this.remotelyModified = remotelyModified;
		this.onlyLocal = onlyLocal;
		this.onlyRemote = onlyRemote;
	}


	public JakeObject getJakeObject() {
		return this.jo;
	}


	/**
	 * Whether or not this file is in conflict (i.e. there is a local file and a
	 * remote file that have both been modified)
	 */
	public boolean isInConflict() {
		return this.locallyModified && this.remotelyModified;
	}

	/**
	 * Whether or not we have the lastest version of the file
	 */
	public boolean isLocalLatest() {
		return !(this.locallyModified || this.remotelyModified);
	}


	public long getLastModification() {
		return this.lastModification;
	}

	/**
	 * Whether or not the file has been modified by *us*
	 */
	public boolean isLocallyModified() {
		return this.locallyModified;
	}

	/**
	 * Whether or not the file has been modified by someone else
	 */
	public boolean isRemotelyModified() {
		return this.remotelyModified;
	}

	public boolean isOnlyLocal() {
		return onlyLocal;
	}

	public boolean isOnlyRemote() {
		return onlyRemote;
	}


	public String toString() {
		return jo.toString() + " (modified:" + lastModification + ") locallyModified:" + locallyModified
				+ " onlyLocal:" + onlyLocal + " onlyRemote:" + onlyRemote + " remotelyModified:"
				+ remotelyModified;
	}

}
