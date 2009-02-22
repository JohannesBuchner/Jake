package com.jakeapp.core.services;

import com.jakeapp.core.dao.IServiceCredentialsDao;
import com.jakeapp.core.domain.ProtocolType;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.services.exceptions.ProtocolNotSupportedException;
import com.jakeapp.core.services.futures.CreateAccountFuture;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

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

	private List<MsgService> msgServices = new ArrayList<MsgService>();

	private Map<MsgService, ServiceCredentials> map = new HashMap<MsgService, ServiceCredentials>();

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

	public MsgService<UserId> createMsgService(ServiceCredentials credentials)
					throws ProtocolNotSupportedException {

		log.debug("calling createMsgService ");
		log.debug("creating MsgService for " + credentials
						.getUserId() + " pwl: " + credentials.getPlainTextPassword().length());

		MsgService<UserId> msgService;
		if (credentials.getProtocol() != null && credentials.getProtocol()
						.equals(ProtocolType.XMPP)) {
			log.debug("Creating new XMPPMsgService for userId " + credentials.getUserId());
			msgService = new XMPPMsgService();
			msgService.setIcsManager(new FailoverICServicesManager());
			msgService.setServiceCredentials(credentials);
		} else {
			log.warn("Currently unsupported protocol given");
			throw new ProtocolNotSupportedException();
		}

		// create the UserId from the credentials.
		msgService.setUserId(createUserforMsgService(credentials));

		log.debug("resulting MsgService is " + msgService.getServiceCredentials()
						.getProtocol() + " for " + msgService.getServiceCredentials()
						.getUserId() + " pwl: " + msgService.getServiceCredentials()
						.getPlainTextPassword().length() + "with UserId: " + msgService
						.getUserId());

		return msgService;
	}

	/**
	 * Every MsgService has a UserId connected.
	 * This creates the UserId from the ServiceCredentals.
	 *
	 * @param credentials ServiceCredentials that are used to create the UserId
	 * @return
	 * @throws ProtocolNotSupportedException
	 */
	private UserId createUserforMsgService(ServiceCredentials credentials)
					throws ProtocolNotSupportedException {

		// switch through the supported protocols and create the user
		switch (credentials.getProtocol()) {
			case XMPP:
				UUID res = UUID.randomUUID();

				if (credentials.getUuid() != null)
					res = UUID.fromString(credentials.getUuid());

				credentials.setUuid(res);

				return new UserId(ProtocolType.XMPP, credentials.getUserId());

			default:
				throw new ProtocolNotSupportedException("Backend not yet implemented");
		}
	}

	@Transactional
	public List<MsgService<UserId>> getAll() {
		log.debug("calling getAll");
		// ensureInitialised();

		List<ServiceCredentials> credentialsList = this.serviceCredentialsDao.getAll();

		List<MsgService<UserId>> msgServices = new ArrayList<MsgService<UserId>>();

		for (ServiceCredentials credentials : credentialsList) {
			try {
				MsgService<UserId> service = this.createMsgService(credentials);
				if (!msgServices.contains(service)) {
					msgServices.add(service);
					this.map.put(service, credentials);
				}
			} catch (ProtocolNotSupportedException e) {
				log.warn("Protocol not supported: ", e);
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
	 * This adds the ServiceCrenentials from the MsgService into the database.
	 *
	 * @param credentials
	 * @return the service
	 * @throws InvalidCredentialsException
	 * @throws ProtocolNotSupportedException
	 */
	public MsgService addMsgService(ServiceCredentials credentials)
					throws InvalidCredentialsException, ProtocolNotSupportedException {
		log.debug("calling addMsgService");

		MsgService<UserId> msgService = this.createMsgService(credentials);

		// add to global array (NOT USED?)
		msgServices.add(msgService);

		// persist the ServicCredentials!
		this.getServiceCredentialsDao().create(credentials);

		return msgService;
	}

	public ServiceCredentials get(MsgService service) {
		return this.map.get(service);
	}

	public MsgService getByCredentials(ServiceCredentials credentials) {
		for (MsgService msg : getAll()) {
			if (msg.getServiceCredentials().equals(credentials)) {
				return msg;
			}
		}
		try {
			return this.addMsgService(credentials);
		} catch (Exception e) {
			log.error("Unable to create MessageService:", e);
			return null;
		}
	}
}