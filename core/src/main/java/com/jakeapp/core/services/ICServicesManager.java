package com.jakeapp.core.services;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.filetransfer.FailoverCapableFileTransferService;
import com.jakeapp.jake.ics.filetransfer.IFileTransferService;


public interface ICServicesManager {

	/**
	 * get the ICS for the project
	 * @param p
	 * @return
	 */
	ICService getICService(Project p);

	/**
	 * get a transfer service to transmit something
	 * @param p
	 * @return
	 * @throws NotLoggedInException
	 */
	IFileTransferService getTransferService(Project p) throws NotLoggedInException;

	/**
	 * translate a userId to something the ICS can work with
	 * @param u
	 * @param p
	 * @return
	 */
	com.jakeapp.jake.ics.UserId getBackendUserId(Project p, UserId u);

	/**
	 * get your own userId
	 * @param p
	 * @return
	 */
	com.jakeapp.jake.ics.UserId getBackendUserId(Project p);

}