package com.jakeapp.core.services;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.filetransfer.IFileTransferService;

import java.util.Collection;


/**
 * Holds the subsystems and their mapping to the Project
 *
 * @author johannes
 */
public interface ICSManager {


	/**
	 * Checks if a transferservice exists for the project.
	 * @param p
	 * @return
	 */
	public boolean hasTransferService(Project p);


	/**
	 * get the ICS for the project
	 *
	 * @param p
	 * @return
	 */
	public ICService getICService(Project p);

	/**
	 * get a transfer service to transmit something
	 *
	 * @param p
	 * @return
	 * @throws NotLoggedInException
	 */
	public IFileTransferService getTransferService(Project p) throws NotLoggedInException;

	/**
	 * translate a userId to something the ICS can work with
	 *
	 * @param u
	 * @param p
	 * @return
	 */
	public com.jakeapp.jake.ics.UserId getBackendUserId(Project p, UserId u);

	/**
	 * translate an ICS userId to something the "real world" can work with
	 *
	 * @param p
	 * @param u
	 * @return
	 */
	public UserId getFrontendUserId(Project p, com.jakeapp.jake.ics.UserId u);

	/**
	 * get your own userId
	 *
	 * @param p
	 * @return
	 */
	public com.jakeapp.jake.ics.UserId getBackendUserId(Project p);

	/**
	 * get all subsystems
	 *
	 * @return
	 */
	public Collection<ICService> getAll();

}