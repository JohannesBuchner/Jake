package com.jakeapp.jake.ics;

import com.jakeapp.jake.ics.filetransfer.IFileTransferService;
import com.jakeapp.jake.ics.msgservice.IMsgService;
import com.jakeapp.jake.ics.status.IStatusService;
import com.jakeapp.jake.ics.users.IUsersService;

/**
 * 
 * The task of the InterClient Communication Service (ICService) is to provide a
 * communication layer based on the network for communication between users
 * based on messages and objects.
 * 
 * <p>
 * userid: A way of identifying the user within the used network protocol.
 * example: user@host
 * </p>
 * <p>
 * network service: The implementation of IICService use some sort of
 * Interclient Communication protocol. We reference to this underlying system as
 * network service. examples: XMPP, TCP-Sockets, ...
 * </p>
 * 
 * Implementation note: The constructor has to create the instances and
 * (possibly) share common data
 * 
 * @author johannes
 */
abstract public class ICService {

	protected IStatusService statusService;

	protected IMsgService msgService;
	
	protected IUsersService usersService;

	protected IFileTransferService fileTransferService;

	/**
	 * login, logout, etc.
	 * 
	 * @return the service
	 */
	public IStatusService getStatusService() {
		return this.statusService;
	}

	/**
	 * for sending small packages
	 * 
	 * @return the service
	 */
	public IMsgService getMsgService() {
		return this.msgService;
	}

	/**
	 * our friends ... the people we talk to, you know?
	 * 
	 * @return the service
	 */
	public IUsersService getUsersService() {
		return this.usersService;
	}

	/**
	 * for sending huge files
	 * 
	 * @return the service
	 */
	public IFileTransferService getFileTransferService() {
		return this.fileTransferService;
	}

	/**
	 * @return the name of the implemented service
	 */
	public abstract String getServiceName();

}
