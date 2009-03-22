package com.jakeapp.core.services;

import com.jakeapp.core.Injected;
import com.jakeapp.core.dao.IAccountDao;
import com.jakeapp.core.dao.exceptions.NoSuchServiceCredentialsException;
import com.jakeapp.core.domain.Account;
import com.jakeapp.core.domain.ProtocolType;
import com.jakeapp.core.domain.User;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.domain.exceptions.NoSuchMsgServiceException;
import com.jakeapp.core.services.exceptions.ProtocolNotSupportedException;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Manager that creates and stores MsgServices
 */
@Transactional
public class MsgServiceManager {

	private static Logger log = Logger.getLogger(MsgServiceManager.class);

	/**
	 * Key is the Credentials UUID
	 */
	private Map<String, MsgService<User>> msgServices = new HashMap<String, MsgService<User>>();

	@Injected
	private IAccountDao accountDao;

	@Injected
	private ICSManager icsManager;

	@Injected
	private IProjectInvitationListener coreProjectInvitationListener;

	/**
	 * Default Constructor for <code>MsgServiceManager</code>
	 * irt. Bug #91 this method should be removed if possible. Dont use it
	 *
	 * @Deprecated
	 */
	@Deprecated
	public MsgServiceManager() {
	}


	public MsgServiceManager(ICSManager icsManager, IAccountDao accountDao, IProjectInvitationListener coreProjectInvitationListener) {
		this.setICSManager(icsManager);
		this.setAccountDao(accountDao);
		this.setCoreProjectInvitationListener(coreProjectInvitationListener);
	}


	private void setICSManager(ICSManager icsManager) {
		this.icsManager = icsManager;
	}

	private IAccountDao getAccountDao() {
		return this.accountDao;
	}

	private void setAccountDao(IAccountDao accountDao) {
		this.accountDao = accountDao;
	}


	private IProjectInvitationListener getCoreProjectInvitationListener() {
		return coreProjectInvitationListener;
	}

	private void setCoreProjectInvitationListener(IProjectInvitationListener coreProjectInvitationListener) {
		this.coreProjectInvitationListener = coreProjectInvitationListener;
	}

	/**
	 * Creates a new MessageService if one for the specified credentials does
	 * not exist yet.
	 *
	 * @param credentials The AccountDao to create a MsgService for.
	 * @throws ProtocolNotSupportedException if the protocol specified in the credentials is not
	 *                                       supported.
	 */
	private MsgService<User> create(Account credentials)
			throws ProtocolNotSupportedException {
		log.trace("Creating MsgService for " + credentials);
		MsgService<User> msgService;

		if (credentials == null) {
			throw new InvalidCredentialsException("Credentials cannot be null!");
		}
		if (credentials.getUuid() == null) {
			log.warn("no UUID set in credentials. fixing ...");
			credentials.setUuid(UUID.randomUUID().toString());
		}

		MsgService.setServiceCredentialsDao(getAccountDao());

		log.trace("creating MsgService with crendentials: " + credentials);
		log.trace("User="
				+ credentials.getUserId()
				+ " pwl = "
				+ ((credentials.getPlainTextPassword() == null) ? "null" : ""
				+ credentials.getPlainTextPassword().length()));

		if (credentials.getProtocol() != null
				&& credentials.getProtocol().equals(ProtocolType.XMPP)) {
			log
					.debug("Creating new XMPPMsgService for userId "
							+ credentials.getUserId());
			msgService = new XMPPMsgService();
			msgService.registerInvitationListener(coreProjectInvitationListener);
			msgService.setIcsManager(this.icsManager);
			msgService.setServiceCredentials(credentials);
			this.msgServices.put(credentials.getUuid(), msgService);
			msgService.setUserId(createUserforMsgService(credentials));

			log.trace("resulting MsgService is " + msgService);


			return msgService;
		} else {
			log.warn("Currently unsupported protocol given");
			throw new ProtocolNotSupportedException();
		}
	}

	/**
	 * Every MsgService has a UserId connected. This creates the UserId from the
	 * ServiceCredentals.
	 *
	 * @param credentials ServiceCredentials that are used to create the UserId
	 * @return the <code>User</code> created for this <code>MsgService</code>
	 * @throws ProtocolNotSupportedException if the given <code>ProtocolType</code> is currently not supported.
	 */
	private User createUserforMsgService(Account credentials)
			throws ProtocolNotSupportedException {

		// switch through the supported protocols and create the user
		switch (credentials.getProtocol()) {
			case XMPP:
				UUID res;

				if (credentials.getUuid() != null)
					res = UUID.fromString(credentials.getUuid());
				else
					res = UUID.randomUUID();
				credentials.setUuid(res);

				return new User(ProtocolType.XMPP, credentials.getUserId());

			default:
				throw new ProtocolNotSupportedException("Backend not yet implemented");
		}
	}

	/**
	 * Returns a <code>List</code> of loaded <code>MsgService</code>s.
	 *
	 * @return a (possible empty) <code>List</code> of <code>MsgService</code>s
	 */
	public List<MsgService<User>> getLoaded() {
		List<MsgService<User>> result = new ArrayList<MsgService<User>>();
		result.addAll(this.msgServices.values());
		log.debug("got " + result.size() + " messageservices");
		return result;
	}


	/**
	 * Returns a <code>List</code> of all <code>MsgService</code>s
	 *
	 * @return a (possible empty) <code>List</code> of <code>MsgService</code>
	 */
	@Transactional
	public List<MsgService<User>> getAll() {
		log.debug("calling getAll");

		List<Account> credentialsList = this.accountDao.getAll();
		log.debug("Found " + credentialsList.size() + " Credentials in the DB");
		log.debug("Found " + this.msgServices.size() + " Credentials in the Cache");


		for (Account credentials : credentialsList) {
			if (!this.msgServices.containsKey(credentials.getUuid())) {
				try {
					MsgService<User> service = this.create(credentials);
					if (credentials.getUuid() == null) {
						throw new IllegalStateException(
								"createMsgService didn't fill uuid");
					}
					this.msgServices.put(credentials.getUuid(), service);
				} catch (ProtocolNotSupportedException e) {
					log.warn("Protocol not supported: ", e);
					log.info("ignoring unsupported entry " + credentials);
				}
			}
		}
		return getLoaded();
	}

	/**
	 * Returns the <code>MsgService</code> for a given <code>User</code>
	 *
	 * @param user the <code>User</code> in question
	 * @return the <code>MsgService</code> belonging to that <code>User</code>
	 * @throws NoSuchMsgServiceException if the <code>User</code> belongs to no <code>MsgService</code>
	 */
	public MsgService<User> getMsgServiceForUser(User user) throws NoSuchMsgServiceException {
		List<MsgService<User>> msgservices = this.getAll();

		for (MsgService<User> m : msgservices) {
			if (m.getUserId() == user) return m;
		}

		throw new NoSuchMsgServiceException("The supplied User belongs to no MsgService");
	}

	/**
	 * Creates and Adds a <code>MsgService</code> for the right <code>ProtocolType</code> This adds the
	 * <code>Account</code> from the <code>MsgService</code> into the database.
	 *
	 * @param credentials the <code>Account</code>
	 * @return the <code>MsgService</code> created
	 * @throws InvalidCredentialsException   if the <code>Account</code> is invalid
	 * @throws ProtocolNotSupportedException if the given <code>ProtocolType</code> is not yet supported
	 */
	private MsgService add(Account credentials)
			throws InvalidCredentialsException, ProtocolNotSupportedException {
		log.trace("calling addMsgService");

		if (credentials.getUuid() == null) {
			log.info("no UUID in credentials. fixing ...");
			credentials.setUuid(UUID.randomUUID());
		}

		// persist the Account!
		try {
			this.getAccountDao().read(UUID.fromString(credentials.getUuid()));
			this.getAccountDao().update(credentials);
		} catch (NoSuchServiceCredentialsException e) {
			this.getAccountDao().create(credentials);
		}
		getAll();
		return getOrCreate(credentials);
	}

	/**
	 * Tries to return the <code>MsgService</code> that is associated with the
	 * <code>Account</code>. The <code>Account</code> is saved in the DB, while
	 * the <code>MsgService</code> is not. If no <code>MsgService</code> was created until now,
	 * we try to created it.
	 *
	 * @param credentials the <code>Account</code> on which the <code>MsgService</code> is based
	 * @return the MsgService or null.
	 * @throws InvalidCredentialsException when the <code>Account</code> is not valid
	 */
	public MsgService getOrCreate(Account credentials) throws InvalidCredentialsException {
		log.trace("Get MsgService by credentials: " + credentials);

		MsgService<User> msg = find(credentials);
		if (msg != null) {
			log.trace("reused already-loaded msgservice");
			return msg;
		}
		log.trace("not found in cache");

		try {
			return this.add(credentials);
		} catch (Exception e) {
			log.error("Unable to create MessageService:", e);
			throw new InvalidCredentialsException();
//			return null;
		}
	}



	private MsgService<User> find(Account credentials) {
		if (this.msgServices.containsKey(credentials.getUuid())) {
			return this.msgServices.get(credentials.getUuid());
		}
		List<MsgService<User>> list = getAll();
		if (this.msgServices.containsKey(credentials.getUuid())) {
			return this.msgServices.get(credentials.getUuid());
		}

		// find a similar one
		for (MsgService<User> m : list) {
			Account c = m.getServiceCredentials();
			if (credentials.getProtocol() == c.getProtocol()
					&& credentials.getUserId().equals(c.getUserId())) {
				credentials.setUuid(c.getUuid());
				log.trace("found a similar one");
				break;
			}
		}
		if (this.msgServices.containsKey(credentials.getUuid())) {
			log.trace("found after adjusting UUID");
			return this.msgServices.get(credentials.getUuid());
		}
		return null;
	}

	/**
	 * Removes a MsgService from the cache.
	 *
	 * @param owner The <code>Account</code> that owns the <code>MsgService</code> to be removed
	 * @return true, if a <code>MsgService</code> was removed from the cache, false if nothing was removed.
	 */
	public boolean remove(Account owner) {
		boolean result;

		log.debug("removing for user " + owner);
		result = this.msgServices.remove(owner.getUuid()) != null;
		log.debug("removed=" + result);
		return result;
	}

	/**
	 * releases all stored MessageServices
	 */
	public void free() {
		this.msgServices.clear();
	}
}