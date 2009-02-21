package com.jakeapp.core.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.jakeapp.core.dao.IServiceCredentialsDao;
import com.jakeapp.core.domain.ProtocolType;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.services.exceptions.ProtocolNotSupportedException;
import com.jakeapp.core.services.futures.CreateAccountFuture;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.jake.ics.exceptions.NetworkException;

/**
 * Factory Class to create MsgServices by giving ServiceCredentials
 */
@Transactional
public class MsgServiceFactory {

	private static Logger log = Logger.getLogger(MsgServiceFactory.class);

	private IServiceCredentialsDao serviceCredentialsDao;

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

	public MsgServiceFactory(IServiceCredentialsDao serviceCredentialsDao) {
		log.debug("calling constructor with serviceCredentialsDao");
		this.serviceCredentialsDao = serviceCredentialsDao;
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
		log.debug("creating MsgService for " + credentials.getUserId() + " pwl: "
				+ credentials.getPlainTextPassword().length());
		ensureInitialised();
		MsgService result = null;
		if (credentials.getProtocol() != null
				&& credentials.getProtocol().equals(ProtocolType.XMPP)) {
			log
					.debug("Creating new XMPPMsgService for userId "
							+ credentials.getUserId());
			result = new XMPPMsgService();
			result.setServiceCredentials(credentials);

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
		log.debug("resulting MsgService is "
				+ result.getServiceCredentials().getProtocol() + " for "
				+ result.getServiceCredentials().getUserId() + " pwl: "
				+ result.getServiceCredentials().getPlainTextPassword().length());

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
	public AvailableLaterObject<Void> createAccount(ServiceCredentials credentials)
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

			case XMPP:

				UUID res = null;

				if (credentials.getUuid() != null)
					res = UUID.fromString(credentials.getUuid());

				if (res == null)
					res = UUID.randomUUID();

				credentials.setUuid(res);

				user = new UserId(ProtocolType.XMPP, credentials.getUserId());
				break;

			default:
				throw new ProtocolNotSupportedException("Backend not yet implemented");

		}
		if (user != null)
			svc.setUserId(user);

		msgServices.add(svc);

		this.getServiceCredentialsDao().create(credentials);
		return svc;

	}

	public ServiceCredentials get(MsgService service) {
		return this.map.get(service);
	}

	public MsgService getByCredentials(ServiceCredentials credentials) {
		for(MsgService msg : getAll()) {
			if(msg.getServiceCredentials().equals(credentials)) {
				return msg;
			}
		}
		return null;
	}
}
