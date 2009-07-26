package com.jakeapp.core.domain;

import org.springframework.transaction.annotation.Transactional;
import com.jakeapp.core.domain.exceptions.UserFormatException;
import com.jakeapp.core.services.VisibilityStatus;
import com.jakeapp.core.services.ICSManager;
import com.jakeapp.core.services.IProjectInvitationListener;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.status.ILoginStateListener;
import com.jakeapp.jake.ics.status.IOnlineStatusListener;
import com.jakeapp.jake.ics.msgservice.IMessageReceiveListener;

/**
 * @author Dominik Dorn
 */
public interface IMsgService<T extends User> {
	void setIcsManager(ICSManager icsManager);

	ICSManager getIcsManager();

	@Transactional
	boolean login(Account newCreds) throws NetworkException;

	void logout() throws NetworkException;

	void setServiceCredentials(Account credentials);//  A GETTER SHOULD *never ever* to lengthy tasks when run in gui-thread.

	VisibilityStatus getVisibilityStatus();

	T getUserId();

	/**
	 * Get a UserId Instance from this Messaging-Service
	 *
	 * @param userId the String representation of the userId
	 * @return a &lt;T extends UserId&gt; Object
	 * @throws com.jakeapp.core.domain.exceptions.UserFormatException if the format of the input is not valid for this
	 *                               Messaging-Service
	 */
	T getUserId(String userId) throws UserFormatException;

	ProtocolType getProtocolType();

	/**
	 * Creates an account for the Service, with the specified
	 * ServiceCredentials. You have to have setCredentials first.
	 *
	 * @throws NetworkException
	 */
	void createAccount() throws NetworkException;

	boolean isPasswordSaved();

	public  ICService getMainIcs();

	public com.jakeapp.jake.ics.UserId getMainUserId();


		/**
	 * Activates a specific subsystem (=project)
	 * Caled via startServing (start a project)
	 *
	 * @param ics
	 * @param receiveListener
	 * @param lsl
	 * @param onlineStatusListener
	 * @param name
	 * @throws NetworkException
	 */
	public void activateSubsystem(ICService ics,
					IMessageReceiveListener receiveListener, ILoginStateListener lsl,
					IOnlineStatusListener onlineStatusListener, String name)
					throws NetworkException;

	public void deactivateSubsystem(ICService ics) throws NetworkException;

	public Account getServiceCredentials();



	void registerInvitationListener(IProjectInvitationListener il);

	void unregisterInvitationListener(IProjectInvitationListener il);
}
