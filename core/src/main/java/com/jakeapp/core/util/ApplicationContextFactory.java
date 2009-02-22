package com.jakeapp.core.util;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Hashtable;
import java.util.Properties;
import java.util.UUID;

public class ApplicationContextFactory {
	protected final static Logger log = Logger.getLogger(ProjectApplicationContextFactory.class);
	protected Hashtable<UUID, ConfigurableApplicationContext> contextTable;
	private String[] configLocation;

	public ApplicationContextFactory() {
		this.contextTable = new Hashtable<UUID, ConfigurableApplicationContext>();
	}


	/**
	 * Get the <code>ApplicationContext</code> for a given <code>
	 * UUID</code>. If an
	 * <code>ApplicationContext</code> for the given <code>UUID</code>
	 * already exists, the existing context is returned, otherwise a new context
	 * will be created. This method is <code>synchronized</code>
	 *
	 * @param identifier the UUID for which the application context is used
	 * @return the <code>ApplicationContext</code>
	 */
	public synchronized ApplicationContext getApplicationContext(UUID identifier) {
		if (identifier == null)
			return null;

		ConfigurableApplicationContext applicationContext = null;

		//log.debug("acquiring context for identifier: " + identifier.toString());
		if (this.contextTable.containsKey(identifier)) {
			// log.debug("context for UUID: " + identifier.toString()
			//        + " already created, returning existing...");
			applicationContext = this.contextTable.get(identifier);
		} else {
			Properties props = new Properties();
			props.put("db_path", identifier.toString());
			PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
			cfg.setProperties(props);

			applicationContext = new ClassPathXmlApplicationContext(this.configLocation);

			log.debug("configuring context with UUID: " + identifier.toString());
			applicationContext.addBeanFactoryPostProcessor(cfg);
			applicationContext.refresh();

			this.contextTable.put(identifier, applicationContext);
		}
		return applicationContext;
	}

	/**
	 * Set the location of the spring config file to be used by the factory to
	 * create the application contexts.
	 *
	 * @param configLocation the location of the config file (i.e. beans.xml etc.)
	 * @see org.springframework.context.support.ClassPathXmlApplicationContext
	 */
	public synchronized void setConfigLocation(String[] configLocation) {
		log.debug("Setting config Location to: ");
		for (String bla : configLocation) {
			log.debug(bla);
		}
		this.configLocation = configLocation;
	}

}
