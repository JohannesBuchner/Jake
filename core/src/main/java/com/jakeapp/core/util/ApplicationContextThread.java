package com.jakeapp.core.util;

import java.util.Properties;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Holds a ApplicationContext. All beans within the ApplicationContext are
 * loaded within this thread and can be retrieved.
 * 
 * @author johannes
 */
public class ApplicationContextThread extends ThreadBroker {

	private static final Logger log = Logger.getLogger(ApplicationContextThread.class);

	private ClassPathXmlApplicationContext applicationContext;

	private UUID identifier;

	private final String[] configLocation;

	/**
	 * starts a new Thread that loads the given ApplicationContext
	 * 
	 * @param identifier
	 * @param configLocation
	 */
	public ApplicationContextThread(UUID identifier, final String[] configLocation) {
		this.identifier = identifier;
		this.configLocation = configLocation;
		new Thread(this, identifier.toString()).start();
	}

	@Override
	public void run() {

		log.debug("requesting context");
		try {
			this.applicationContext = SpringThreadBroker.getInstance().doTask(
					new InjectableTask<ClassPathXmlApplicationContext>() {

						@Override
						public ClassPathXmlApplicationContext calculate()
								throws Exception {
							return new ClassPathXmlApplicationContext(
									ApplicationContextThread.this.configLocation);
						}
					});
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		log.debug("configuring context");
		Properties props = new Properties();
		props.put("db_path", this.identifier.toString());
		PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
		cfg.setProperties(props);
		this.applicationContext.addBeanFactoryPostProcessor(cfg);
		this.applicationContext.refresh();
		log.debug("configuring context done");
		super.run();
	}

	public Object getBean(final String name) {
		log.debug("getting bean from Project " + this.identifier);
		Object o;
		try {
			o = doTask(new InjectableTask<Object>("getting bean " + name) {

				@Override
				public Object calculate() throws Exception {
					return ApplicationContextThread.this.applicationContext.getBean(name);
				}
			});
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		SpringThreadBroker.setThreadForObject(o, this);
		return o;
	}

	@Override
	public void cancel() {
		super.cancel();
	}

}
