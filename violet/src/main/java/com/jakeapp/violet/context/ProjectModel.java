package com.jakeapp.violet.context;

import java.util.UUID;

import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.filetransfer.IFileTransferService;
import com.jakeapp.violet.model.Log;
import com.jakeapp.violet.model.ProjectPreferences;
import com.jakeapp.violet.model.User;

/**
 * Data the Views and actions may want to access. Allows access to everything
 * and all modifications that are valid within the data model.
 */
public class ProjectModel {

	public static final String USERID_PROPERTY_KEY = "userid";

	public static final String PROJECT_ID_PROPERTY_KEY = "id";

	protected final IFSService fss;

	protected final Log log;

	protected final ProjectPreferences preferences;

	protected final ICService ics;

	protected final IFileTransferService transfer;

	ProjectModel(IFSService fss, Log log, ProjectPreferences preferences,
			ICService ics, IFileTransferService transfer) {
		super();
		this.fss = fss;
		this.log = log;
		this.preferences = preferences;
		this.ics = ics;
		this.transfer = transfer;
	}

	public IFSService getFss() {
		return fss;
	}

	public ICService getIcs() {
		return ics;
	}

	public Log getLog() {
		return log;
	}

	public ProjectPreferences getPreferences() {
		return preferences;
	}

	public UUID getProjectid() {
		return UUID.fromString(preferences.get(PROJECT_ID_PROPERTY_KEY));
	}

	public String getProjectname() {
		return preferences.get("name");
	}

	public IFileTransferService getTransfer() {
		return transfer;
	}

	// some convinience methods
	public User getUser() {
		return new User(preferences.get(USERID_PROPERTY_KEY));
	}

	public String getUserid() {
		return preferences.get(USERID_PROPERTY_KEY);
	}

}
