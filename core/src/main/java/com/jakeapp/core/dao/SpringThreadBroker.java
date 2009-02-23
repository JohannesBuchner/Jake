package com.jakeapp.core.dao;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * This thread loads Spring and thus owns the Hibernate Session
 * All beans can be retrieved through this.
 * 
 * @see DaoThreadBroker
 * @see ThreadBroker
 * 
 * @author johannes
 */
public class SpringThreadBroker extends ThreadBroker {
	
	private static final Logger log = Logger.getLogger(SpringThreadBroker.class);

	private static SpringThreadBroker instance;

	public static SpringThreadBroker getInstance() {
		if (instance == null) {
			instance = new SpringThreadBroker();
			new Thread(instance).start();
		}
		return instance;
	}

	public static void stopInstance() {
		getInstance().cancel();
	}
	
	protected SpringThreadBroker() {
		super();
	}

	private ApplicationContext applicationContext;
	
	public void loadSpring(final String[] args) {
		try {
			this.applicationContext = doTask(new InjectableTask<ClassPathXmlApplicationContext>() {

				@Override
				public ClassPathXmlApplicationContext calculate() throws Exception {
					return new ClassPathXmlApplicationContext(args);
				}
			});
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public Object getBean(final String name) {
		try {
			return doTask(new InjectableTask<Object>() {

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
}
