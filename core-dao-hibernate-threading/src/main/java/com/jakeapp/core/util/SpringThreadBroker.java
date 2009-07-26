package com.jakeapp.core.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;



/**
 * This thread loads Spring and thus owns the Hibernate Session. All global
 * beans are created within this thread and can be retrieved through this.
 * 
 * @see DaoThreadBroker
 * @see ThreadBroker
 * 
 * @author johannes
 */
//@DarkMagic
public class SpringThreadBroker extends ThreadBroker {

	private static final Logger log = Logger.getLogger(SpringThreadBroker.class);

	private static SpringThreadBroker instance;

	public static SpringThreadBroker getInstance() {
		if (instance == null) {
			instance = new SpringThreadBroker();
			new Thread(instance, instance.getClass().getSimpleName()).start();
		}
		return instance;
	}

	/**
	 * get the ThreadBroker owning this Object
	 * 
	 * @param o
	 * @param applicationContextThread
	 * @return
	 */
	public static ApplicationContextThread getThreadForObject(Object o) {
		//log.debug("getting thread for " + o);
		return getInstance().objectOwners.get(o);
	}

	public static boolean isInUse() {
		return getInstance().applicationContext != null;
	}

	/**
	 * set the ThreadBroker owning this Object
	 * 
	 * @param o
	 * @param applicationContextThread
	 */
	public static void setThreadForObject(Object o,
			ApplicationContextThread applicationContextThread) {
		log.debug("assigning " + o + " to " + applicationContextThread);
		getInstance().objectOwners.put(o, applicationContextThread);
	}


	public static void stopInstance() {
		getInstance().cancel();
	}

	public Map<Object, ApplicationContextThread> objectOwners = new HashMap<Object, ApplicationContextThread>();

	private ApplicationContext applicationContext;

	protected SpringThreadBroker() {
		super();
	}

	@Override
	public void cancel() {
		log.debug("stopping all ApplicationContextThreads");
		for (ApplicationContextThread subthread : this.objectOwners.values()) {
			subthread.cancel();
		}
		log.debug("stopping.");
		super.cancel();
	}

	/**
	 * load the bean in the SpringThreadBroker Thread, and return it.
	 * 
	 * @param name
	 * @return the bean
	 */
	public Object getBean(final String name) {
		try {
			return doTask(new InjectableTask<Object>("getting bean " + name) {

				@Override
				public Object calculate() throws Exception {
					return SpringThreadBroker.this.applicationContext.getBean(name);
				}
			});
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * load the Spring context (equivalent to new
	 * ClassPathXmlApplicationContext), but within the SpringThreadBroker
	 * thread.
	 * 
	 * @param args
	 */
	public void loadSpring(final String[] args) {
		try {
			this.applicationContext = doTask(new InjectableTask<ApplicationContext>(
					"loading spring") {

				@Override
				public ApplicationContext calculate() throws Exception {
					return new ClassPathXmlApplicationContext(args);
				}
			});
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

}
