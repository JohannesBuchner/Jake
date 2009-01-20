package com.jakeapp.core.util;

import java.io.File;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.jakeapp.core.domain.Project;


/**
 * Unit test class for the application context factory.
 * @see ProjectApplicationContextFactory
 * @author Simon
 *
 */
public class ApplicationContextFactoryTest {

	private static final Project PROJECT_1 = new Project("foo", new UUID(1, 1), null, new File("pro_1"));
	private static final Project PROJECT_2 = new Project("foo", new UUID(2, 2), null, new File("pro_2"));
	
	private static Logger log = Logger.getLogger(ApplicationContextFactoryTest.class);

	private static ApplicationContextFactory factory;

	@BeforeClass
	public static void beforeClass() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContextFactory_test_context.xml");
		factory = (ApplicationContextFactory) context.getBean("applicationContextFactory");
	}
	
	@Before public void setup() {
		log.debug("--------------------------------------------------");
	}
	
	@Test (timeout = 1000)
	public void getApplicationContext_getAnyContext() {
		ApplicationContext context = factory.getApplicationContext(UUID.fromString(PROJECT_1.getProjectId()));
		Assert.assertNotNull(context);
	}
	
	@Test (timeout = 1000)
	public void getApplicationContext_getTwoDifferentContexts() {
		ApplicationContext context1 = factory.getApplicationContext(UUID.fromString(PROJECT_1.getProjectId()));
		ApplicationContext context2 = factory.getApplicationContext(UUID.fromString(PROJECT_2.getProjectId()));
		Assert.assertNotSame("factory returned same context", context1, context2);
	}

	@Test (timeout = 1000)
	public void getApplicationContext_FactoryReturnsSameContext() {
		ApplicationContext context1 = factory.getApplicationContext(UUID.fromString(PROJECT_1.getProjectId()));
		ApplicationContext context2 = factory.getApplicationContext(UUID.fromString(PROJECT_1.getProjectId()));
		Assert.assertSame("factory did not return the same context", context1, context2);
	}

    // TODO REPAIR 
//	@Test (timeout = 1000)
//	public void getApplicationContext_getTwoDifferentUrlsAndConfiguration() {
//		ApplicationContext context1 = factory.getApplicationContext(PROJECT_1);
//		ApplicationContext context2 = factory.getApplicationContext(PROJECT_2);
//
//		DriverManagerDataSource dataSource1 = (DriverManagerDataSource) context1.getBean("dataSource");
//		DriverManagerDataSource dataSource2 = (DriverManagerDataSource) context2.getBean("dataSource");
//
//		Assert.assertEquals(dataSource1.getUrl(), "jdbc:hsqldb:mem:test/" + PROJECT_1.getRootPath());
//		Assert.assertEquals(dataSource2.getUrl(), "jdbc:hsqldb:mem:test/" + PROJECT_2.getRootPath());
//
//	}
}
