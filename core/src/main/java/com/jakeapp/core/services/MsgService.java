package com.jakeapp.core.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.jakeapp.core.dao.IServiceCredentialsDao;
import com.jakeapp.core.domain.ProtocolType;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.domain.exceptions.UserIdFormatException;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.exceptions.TimeoutException;
import com.jakeapp.jake.ics.msgservice.IMessageReceiveListener;
import com.jakeapp.jake.ics.status.ILoginStateListener;
import com.jakeapp.jake.ics.status.IOnlineStatusListener;

/**
 * Abstract MessagingService declaring what the classes for the
 * instant-messaging protocols (XMPP, ICQ, etc.) need to implement.
 * 
 * Does not know anything about Projects.
 * 
 * @author dominik
 */
public abstract class MsgService<T extends UserId> {

	private static final Logger log = Logger.getLogger(MsgService.class);

	private String name = "notInitialized";

	private VisibilityStatus visibilityStatus = VisibilityStatus.OFFLINE;

	protected T userId;

	private ServiceCredentials serviceCredentials;

	private static IServiceCredentialsDao serviceCredentialsDao;

	protected ICSManager icsManager;

	private static class SubsystemListeners {

		public SubsystemListeners(ILoginStateListener lsl,
				IOnlineStatusListener onlineStatusListener,
				IMessageReceiveListener receiveListener) {
			super();
			this.lsl = lsl;
			this.onlineStatusListener = onlineStatusListener;
			this.receiveListener = receiveListener;
		}

		public IMessageReceiveListener receiveListener;

		public ILoginStateListener lsl;

		public IOnlineStatusListener onlineStatusListener;
	}

	private Map<ICService, SubsystemListeners> activeSubsystems = new HashMap<ICService, SubsystemListeners>();

	public void setIcsManager(ICSManager icsManager) {
		this.icsManager = icsManager;
	}

	public ICSManager getIcsManager() {
		return icsManager;
	}

	/**
	 * @return Servicecredentials if they are already set.
	 */
	protected ServiceCredentials getServiceCredentials() {
		return serviceCredentials;
	}

	protected void setName(String name) {
		this.name = name;
	}


	/**
	 * @return The name of the Service associated to this
	 *         <code>MsgService</code>.
	 */
	protected String getName() {
		return name;
	}

	/**
	 * This method gets called by clients to login on this message service.
	 * 
	 * @return true on success, false on wrong password
	 * @throws Exception
	 *             the login failed for another reason
	 */
	public final boolean login() throws Exception {
		boolean result;

		log.debug("calling plain login");

		if (this.getServiceCredentials() == null)
			throw new InvalidCredentialsException("serviceCredentials are null");

		if (!checkCredentials())
			return false;

		result = this.doLogin();

		updateActiveSubsystems();

		return result;
	}

	@Transactional
	public final boolean login(String newPassword, boolean shouldSavePassword)
			throws Exception {
		log.debug("calling login with newPwd-Size: " + newPassword.length()
				+ ", shouldSave: " + shouldSavePassword);

		if (this.getServiceCredentials() == null)
			throw new InvalidCredentialsException("serviceCredentials are null");

		this.getServiceCredentials().setPlainTextPassword(newPassword);
		this.getServiceCredentials().setSavePassword(shouldSavePassword);

		return this.login();

	}

	/**
	 * Checks whether the ServiceCredentials in <code>serviceCredentials</code>
	 * are valid.
	 * 
	 * @return <code>true</code> iff the credentials are valid.
	 * @throws InvalidCredentialsException
	 *             if the credentials stored in this <code>MsgService</code> are
	 *             insufficiently specified (e.g. they are null)
	 */
	protected boolean checkCredentials() throws InvalidCredentialsException {
		ServiceCredentials serviceCredentials = this.getServiceCredentials();
		if (serviceCredentials == null) {
			throw new InvalidCredentialsException("credentials must not be null");
		}
		if (serviceCredentials.getUserId() == null)
			throw new InvalidCredentialsException("credentials.userId must not be null");

		if (serviceCredentials.getPlainTextPassword() == null)
			throw new InvalidCredentialsException(
					"credentials.plainTextPassword must not be null");


		if (serviceCredentials.getServerAddress() == null)
			throw new InvalidCredentialsException(
					"credentials.serverAddress must not be null");

		return this.doCredentialsCheck();
	}

	protected abstract boolean doCredentialsCheck();

	protected abstract boolean doLogin() throws Exception;

	/**
	 * idempotent
	 * 
	 * @throws Exception
	 */
	public final void logout() throws Exception {
		log.debug("MsgService -> logout");
		this.setVisibilityStatus(VisibilityStatus.OFFLINE);
		this.doLogout();

		updateActiveSubsystems();
	}

	/**
	 * has to be idempotent
	 * 
	 * @throws Exception
	 */
	protected abstract void doLogout() throws Exception;

	public void setServiceCredentials(ServiceCredentials credentials) {
		log.debug("setting service credentials to " + credentials.getUserId() + " pwl: "
				+ credentials.getPlainTextPassword().length());
		this.serviceCredentials = credentials;
	}

	public boolean setVisibilityStatus(VisibilityStatus newStatus) {
		this.visibilityStatus = newStatus;
		return false;
	}

	public VisibilityStatus getVisibilityStatus() {
		return this.visibilityStatus;
	}

	public T getUserId() {
		return this.userId;
	}

	/**
	 * Get a UserId Instance from this Messaging-Service
	 * 
	 * @param userId
	 *            the String representation of the userId
	 * @return a &lt;T extends UserId&gt; Object
	 * @throws UserIdFormatException
	 *             if the format of the input is not valid for this
	 *             Messaging-Service
	 */
	public abstract T getUserId(String userId) throws UserIdFormatException;


	protected void setUserId(T userId) {
		this.userId = userId;
	}

	/**
	 * Get the type of this MsgService (e.g. to display fancy buttons)
	 * 
	 * @return The <code>ProtocolType</code> of this MessageService
	 */
	public final ProtocolType getProtocolType() {
		return this.getServiceCredentials().getProtocol();
	}

	/**
	 * Creates an account for the Service, with the specified
	 * ServiceCredentials. You have to have setCredentials first.
	 * 
	 * @throws NetworkException
	 */
	public abstract void createAccount() throws NetworkException;


	public final boolean isPasswordSaved() {
		return (this.getServiceCredentials() != null && !this.getServiceCredentials()
				.getPlainTextPassword().isEmpty());
	}

	protected static IServiceCredentialsDao getServiceCredentialsDao() {
		return serviceCredentialsDao;
	}

	protected static void setServiceCredentialsDao(
			IServiceCredentialsDao serviceCredentialsDao) {
		MsgService.serviceCredentialsDao = serviceCredentialsDao;
	}

	abstract protected ICService getMainIcs();

	abstract protected com.jakeapp.jake.ics.UserId getMainUserId();

	private void updateActiveSubsystems() throws NotLoggedInException, NetworkException,
			TimeoutException {
		for(Entry<ICService, SubsystemListeners> el : this.activeSubsystems.entrySet()) {
			this.updateSubsystemStatus(el.getKey(), el.getValue());
		}
	}

	public void activateSubsystem(ICService ics, IMessageReceiveListener receiveListener,
			ILoginStateListener lsl, IOnlineStatusListener onlineStatusListener)
			throws TimeoutException, NetworkException {
		
		SubsystemListeners listeners = new SubsystemListeners(lsl, onlineStatusListener,
				receiveListener);
		this.activeSubsystems.put(ics, listeners);
		
		updateSubsystemStatus(ics, listeners);
	}

	private void updateSubsystemStatus(ICService ics, SubsystemListeners listeners)
			throws NotLoggedInException, NetworkException, TimeoutException {
		if (this.getVisibilityStatus() == VisibilityStatus.ONLINE) {
			com.jakeapp.jake.ics.UserId user = getMainIcs().getStatusService()
					.getUserid();
			ics.getMsgService().registerReceiveMessageListener(listeners.receiveListener);
			ics.getUsersService().registerOnlineStatusListener(listeners.onlineStatusListener);
			ics.getStatusService().login(user,
					this.getServiceCredentials().getPlainTextPassword());
			ics.getStatusService().registerLoginStateListener(listeners.lsl);
		} else {
			ics.getStatusService().logout();
		}
	}

	public void deactivateSubsystem(ICService ics) throws NetworkException {
		this.activeSubsystems.remove(ics);
		ics.getStatusService().logout();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((serviceCredentials == null) ? 0 : serviceCredentials.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MsgService other = (MsgService) obj;
		if (serviceCredentials == null) {
			if (other.serviceCredentials != null)
				return false;
		} else if (!serviceCredentials.equals(other.serviceCredentials))
			return false;
		return true;
	}
	
}
