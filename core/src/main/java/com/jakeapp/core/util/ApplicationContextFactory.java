package com.jakeapp.core.util;

import java.util.Hashtable;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.jakeapp.core.Injected;

public class ApplicationContextFactory {

	protected final static Logger log = Logger
			.getLogger(ProjectApplicationContextFactory.class);

	protected Hashtable<UUID, ApplicationContextThread> contextTable = new Hashtable<UUID, ApplicationContextThread>();

	private String[] configLocation;

	/**
	 * Get the <code>ApplicationContext</code> for a given <code>
	 * UUID</code>. If an
	 * <code>ApplicationContext</code> for the given <code>UUID</code> already
	 * exists, the existing context is returned, otherwise a new context will be
	 * created. This method is <code>synchronized</code>
	 * 
	 * @param identifier
	 *            the UUID for which the application context is used
	 * @return the <code>ApplicationContext</code>
	 */
	public synchronized ApplicationContextThread getApplicationContextThread(UUID identifier) {
		if (identifier == null)
			return null;

		ApplicationContextThread applicationContext;
		if (this.contextTable.containsKey(identifier)) {
			applicationContext = this.contextTable.get(identifier);
		} else {
			applicationContext = new ApplicationContextThread(identifier,
					this.configLocation);
			this.contextTable.put(identifier, applicationContext);
		}
		return applicationContext;
	}

	/**
	 * Set the location of the spring config file to be used by the factory to
	 * create the application contexts.
	 * 
	 * @param configLocation
	 *            the location of the config file (i.e. beans.xml etc.)
	 * @see org.springframework.context.support.ClassPathXmlApplicationContext
	 */
	@Injected
	public synchronized void setConfigLocation(String[] configLocation) {
		log.debug("Setting config Location to: ");
		for (String v : configLocation) {
			log.debug(v);
		}
		this.configLocation = configLocation;
	}

}
