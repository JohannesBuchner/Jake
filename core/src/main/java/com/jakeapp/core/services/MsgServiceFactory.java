package com.jakeapp.core.services;

import com.jakeapp.core.dao.IServiceCredentialsDao;
import com.jakeapp.core.dao.IUserIdDao;
import com.jakeapp.core.dao.exceptions.NoSuchUserIdException;
import com.jakeapp.core.domain.ProtocolType;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.XMPPUserId;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.domain.exceptions.InvalidUserIdException;
import com.jakeapp.core.services.exceptions.ProtocolNotSupportedException;
import com.jakeapp.core.services.futures.CreateAccountFuture;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Factory Class to create MsgServices by giving ServiceCredentials
 */
@Transactional
public class MsgServiceFactory {

	private static Logger log = Logger.getLogger(MsgServiceFactory.class);

	private IServiceCredentialsDao serviceCredentialsDao;
	private IUserIdDao userIdDao;

	private List<MsgService> msgServices = new ArrayList<MsgService>();
	private Map<MsgService, ServiceCredentials> map = new HashMap<MsgService, ServiceCredentials>();

	private boolean initialised = false;

	private void ensureInitialised() {
		log.debug("calling ensureInitialised");
		if (!initialised) {
			log.debug("was not initialized");
			initialised = true;
			createTestdata();

		}
	}

	public MsgServiceFactory() {
		log.debug("calling empty Constructor");
	}

	public MsgServiceFactory(IServiceCredentialsDao serviceCredentialsDao,
			IUserIdDao userIdDao) {
		log.debug("calling constructor with serviceCredentialsDao");
		this.serviceCredentialsDao = serviceCredentialsDao;
		this.userIdDao = userIdDao;
		// can not initialise here, this produces spring/hibernate errors!

		MsgService.setUserIdDao(userIdDao);
	}

	private IServiceCredentialsDao getServiceCredentialsDao() {
		return serviceCredentialsDao;
	}

	@Transactional
	private void createTestdata() {
		log.debug("creating testData");
	}

	public MsgService createMsgService(ServiceCredentials credentials)
			throws ProtocolNotSupportedException {

		log.debug("calling createMsgService ");

		ensureInitialised();
		MsgService result = null;
		if (credentials.getProtocol() != null
				&& credentials.getProtocol().equals(ProtocolType.XMPP)) {
			log.debug("Creating new XMPPMsgService for userId "
					+ credentials.getUserId());
			result = new XMPPMsgService();
			result.setCredentials(credentials);

			// try {
			// if(!credentials.getUuid().isEmpty())
			// {
			// log.debug("uuid is not empty");
			//
			// if(userIdDao == null)
			// log.debug("userIdDao = null");
			//
			// UserId userId = userIdDao.get(
			// UUID.fromString(
			// credentials.getUuid()
			// )
			// );
			//
			// log.debug("setting userid");
			// result.setUserId(userId);
			//
			// }
			// else
			// {
			// log.debug("uuid is empty");
			//
			//
			//
			// }
			//
			// } catch (InvalidUserIdException e) {
			// e.printStackTrace();
			// } catch (NoSuchUserIdException e) {
			// e.printStackTrace();
			// }

		} else {
			log.warn("Currently unsupported protocol given");
			throw new ProtocolNotSupportedException();
		}

		return result;
	}

	@Transactional
	public List<MsgService> getAll() {
		log.debug("calling getAll");
		// ensureInitialised();

		List<ServiceCredentials> credentialsList = new ArrayList<ServiceCredentials>();

		credentialsList = this.serviceCredentialsDao.getAll();

		List<MsgService> msgServices = new ArrayList<MsgService>();

		for (ServiceCredentials credentials : credentialsList) {
			try {
				MsgService service = null;
				service = this.createMsgService(credentials);
				if (!msgServices.contains(service)) {
					msgServices.add(service);
					this.map.put(service, credentials);
				}
			} catch (ProtocolNotSupportedException e) {

			}
		}

		return msgServices;
	}

	/**
	 * create a account with the given credentials. You are not logged in
	 * afterwards
	 * 
	 * @param credentials
	 * @return success state
	 * @throws ProtocolNotSupportedException
	 * @throws Exception
	 */
	public AvailableLaterObject<Void> createAccount(
			ServiceCredentials credentials)
			throws ProtocolNotSupportedException, NetworkException {
		log.debug("calling AvailableLaterObject");
		MsgService svc = createMsgService(credentials);

		return new CreateAccountFuture(svc);
	}

	/**
	 * creates and adds a msgservice for the right protocol
	 * 
	 * @param credentials
	 * @return the service
	 * @throws InvalidCredentialsException
	 * @throws ProtocolNotSupportedException
	 */
	public MsgService addMsgService(ServiceCredentials credentials)
			throws InvalidCredentialsException, ProtocolNotSupportedException {
		log.debug("calling addMsgService");

		MsgService svc = this.createMsgService(credentials);
		UserId user = null;
		switch (credentials.getProtocol()) {

		case ICQ:
			throw new ProtocolNotSupportedException("ICQ not yet implemented");

		case MSN:
			throw new ProtocolNotSupportedException("MSN not yet implemented");

		case XMPP:

			UUID res = null;

			if (credentials.getUuid() != null)
				res = UUID.fromString(credentials.getUuid());

			if (res == null)
				res = UUID.randomUUID();

			credentials.setUuid(res);

			user = new XMPPUserId(credentials, res, credentials.getUserId(),
					credentials.getUserId(), "", "");
			break;
		}
		if (user != null)
			svc.setUserId(user);

		msgServices.add(svc);

		try {

			this.getServiceCredentialsDao().create(credentials);
			this.userIdDao.create(user);
			return svc;
		} catch (InvalidUserIdException e) {
			e.printStackTrace();
		}
		// add account in database

		throw new InvalidCredentialsException("something went wrong");

	}

	public ServiceCredentials get(MsgService service) {
		return this.map.get(service);
	}
}
