package com.jakeapp.violet.model;

import java.io.File;
import java.util.UUID;

import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.filetransfer.IFileTransferService;
import com.jakeapp.violet.di.DI;

/**
 * Data the Views and actions may want to access. Allows access to everything
 * and all modifications that are valid within the data model.
 */
public abstract class ProjectModel {

	protected IFSService fss;

	protected Log log;

	protected ProjectPreferences preferences;

	protected ICService ics;

	protected IFileTransferService transfer;

	public Log getLog() {
		return log;
	}

	public IFSService getFss() {
		return fss;
	}

	public ProjectPreferences getPreferences() {
		return preferences;
	}

	public ICService getIcs() {
		return ics;
	}

	public IFileTransferService getTransfer() {
		return transfer;
	}

	// some convinience methods
	public User getUser() {
		return new User(this.preferences.get("userid"));
	}

	public String getUserid() {
		return this.preferences.get("userid");
	}

	public UUID getProjectid() {
		return UUID.fromString(this.preferences.get("id"));
	}

	public String getProjectname() {
		return this.preferences.get("name");
	}

}
