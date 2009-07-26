package com.jakeapp.core.util;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.HibernateTemplate;

import java.util.Properties;
import java.util.UUID;


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

	private Session session;

	private SessionFactory sessionFactory;

	private HibernateTemplate hibernateTemplate;

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
		try {
			log.debug("requesting context");
			this.applicationContext = new ClassPathXmlApplicationContext(
					ApplicationContextThread.this.configLocation);
			log.debug("configuring context");
			Properties props = new Properties();
			props.put("db_path", this.identifier.toString());
			PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
			cfg.setProperties(props);
			this.applicationContext.addBeanFactoryPostProcessor(cfg);
			this.applicationContext.refresh();
			log.debug("configuring context done");

			this.hibernateTemplate = (HibernateTemplate) this.applicationContext
					.getBean("hibernateTemplate");
			this.sessionFactory = this.hibernateTemplate.getSessionFactory();

			checkSession();
		} catch (Exception e) {
			log.fatal("starting ApplicationContextThread failed", e);
			return;
		}
		super.run();
	}

	private void checkSession() {
		try {
			this.session = this.sessionFactory.getCurrentSession();
			return;
		} catch (HibernateException e) {
			log.info("no session in this thread", e);
		}
		log.info("lets a new session");
		try {
			this.session = this.sessionFactory.openSession();
			log.info("session created.");
		} catch (HibernateException e) {
			log.fatal("creating a session failed", e);
			throw e;
		}
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
		log.debug("stopping; closing session");
		super.cancel();
		if (this.session != null && this.session.isOpen())
			this.session.close();
	}

	/*
	 * we could check here if the task failed (threw a exception) and do a
	 * rollback, but our tasks are simple enough to not do anything wrong that
	 * has to be rolled back if an exception occurs.
	 */
	@Override
	protected void runTask(InjectableTask<?> task) {
		checkSession();
		Transaction transaction = this.session.beginTransaction();
		super.runTask(task);
		transaction.commit();
	}
}
