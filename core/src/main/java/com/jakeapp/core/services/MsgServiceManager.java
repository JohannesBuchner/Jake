package com.jakeapp.core.services;

import com.jakeapp.core.Injected;
import com.jakeapp.core.dao.IAccountDao;
import com.jakeapp.core.dao.exceptions.NoSuchServiceCredentialsException;
import com.jakeapp.core.domain.Account;
import com.jakeapp.core.domain.ProtocolType;
import com.jakeapp.core.domain.User;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
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
	private ProjectInvitationListener coreProjectInvitationListener;

	public void setICSManager(ICSManager icsManager) {
		this.icsManager = icsManager;
	}

	private IAccountDao getAccountDao() {
		return this.accountDao;
	}

	public void setAccountDao(IAccountDao accountDao) {
		this.accountDao = accountDao;
	}


	public ProjectInvitationListener getCoreProjectInvitationListener() {
		return coreProjectInvitationListener;
	}

	public void setCoreProjectInvitationListener(ProjectInvitationListener coreProjectInvitationListener) {
		this.coreProjectInvitationListener = coreProjectInvitationListener;
	}

	/**
	 * Creates a new MessageService if one for the specified credentials does
	 * not exist yet.
	 * 
	 * @param credentials
	 *            The AccountDao to create a MsgService for.
	 * @throws ProtocolNotSupportedException
	 *             if the protocol specified in the credentials is not
	 *             supported.
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
			msgService.setIcsManager(this.icsManager);
			msgService.setServiceCredentials(credentials);
			msgService.registerInvitationListener(coreProjectInvitationListener);
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
	 * @param credentials
	 *            ServiceCredentials that are used to create the UserId
	 * @return
	 * @throws ProtocolNotSupportedException
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

	public List<MsgService<User>> getLoaded() {
		List<MsgService<User>> result = new ArrayList<MsgService<User>>();
		result.addAll(this.msgServices.values());
		log.debug("got " + result.size() + " messageservices");
		return result;
	}

	@Transactional
	public List<MsgService<User>> getAll() {
		log.trace("calling getAll");

		List<Account> credentialsList = this.accountDao.getAll();
		log.trace("Found " + credentialsList.size() + " Credentials in the DB");
		log.trace("Found " + this.msgServices.size() + " Credentials in the Cache");


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
	 * creates and adds a msgservice for the right protocol This adds the
	 * ServiceCrenentials from the MsgService into the database.
	 * 
	 * @param credentials
	 * @return the service
	 * @throws InvalidCredentialsException
	 * @throws ProtocolNotSupportedException
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
	 * Tries to return the MsgService that is associated with
	 * ServiceCredentials. ServiceCredentials is saved in the DB,
	 * MsgServiccreateMsgServicee not. If no MsgService was created until now,
	 * we try to created it.
	 * 
	 * @param credentials
	 * @return the MsgService or null.
	 */
	public MsgService getOrCreate(Account credentials) {
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
			return null;
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
	 * @param owner The Account that owns the MsgService to be removed
	 * @return true, if a MsgService was removed from the cache, false if nothing was removed.
	 */
	public boolean remove(Account owner) {
		boolean result;
		
		log.debug("removing for user "+ owner);
		result = this.msgServices.remove(owner.getUuid())!=null;
		log.debug("removed="+result);
		return result;
	}

	/**
	 * releases all stored MessageServices
	 */
	public void free() {
		this.msgServices.clear();
	}
}