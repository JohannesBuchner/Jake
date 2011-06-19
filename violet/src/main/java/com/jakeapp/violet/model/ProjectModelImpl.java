package com.jakeapp.violet.model;

import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.filetransfer.IFileTransferService;

/**
 * implementation so that the data structures can actually be set
 */
public class ProjectModelImpl extends ProjectModel {

	public void setFss(IFSService fss) {
		this.fss = fss;
	}

	public void setPreferences(ProjectPreferences preferences) {
		this.preferences = preferences;
	}

	public void setIcs(ICService ics) {
		this.ics = ics;
	}

	public void setTransfer(IFileTransferService transfer) {
		this.transfer = transfer;
	}

	public void setLog(Log log) {
		this.log = log;
	}

}
