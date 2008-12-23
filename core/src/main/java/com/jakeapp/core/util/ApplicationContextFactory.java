package com.jakeapp.core.util;

import java.util.Hashtable;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jakeapp.core.domain.Project;

/**
 * A factory that creates and configures spring application contexts. 
 * Application contexts for a certain <code>Project</code>are only 
 * created once and then reused. 
 * @author Simon
 */
public class ApplicationContextFactory {
	private static Logger log = Logger.getLogger(ApplicationContextFactory.class);
	private Hashtable<String, ConfigurableApplicationContext> contextTable;
	private String configLocation;
	
	{
		this.contextTable = new Hashtable<String, ConfigurableApplicationContext>(); 
	}
	
	/**
	 * Get the <code>ApplicationContext</code> for a given <code>
	 * Project</code>. If an <code>ApplicationContext</code> for the 
	 * given <code>Project</code> already exists, the existing context
	 * is returned, otherwise a new context will be created. This 
	 * method is <code>synchronized</code>
	 * @param project the project for which the application context is
	 * used 
	 * @return the <code>ApplicationContext</code>
	 */
	public synchronized ApplicationContext getApplicationContext(Project project) {

		ConfigurableApplicationContext applicationContext = null;

		log.debug("acquiring context for project: " + project.getProjectId());
		if (this.contextTable.containsKey(project.getProjectId())) {
			log.debug("context for project: " + project.getProjectId() + " already created, returning existin...");
			applicationContext = this.contextTable.get(project.getProjectId());
		} else {
			applicationContext = new ClassPathXmlApplicationContext(this.configLocation);
			Properties props = new Properties();
			props.put("db_path", project.getRootPath());
			PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
			cfg.setProperties(props);
			
			log.debug("configuring context with path: " + project.getRootPath());
			applicationContext.addBeanFactoryPostProcessor(cfg);
			applicationContext.refresh();

			this.contextTable.put(project.getProjectId(), applicationContext);
		}
		return applicationContext;
	}
	
	/**
	 * Set the location of the spring config file to be used by the
	 * factory to create the application contexts.
	 * @param configLocation the location of the config file (i.e. 
	 * beans.xml etc.)
	 * @see ClassPathXmlApplicationContext
	 */
	public void setConfigLocation(String configLocation) {
		this.configLocation = configLocation;
	}
}
