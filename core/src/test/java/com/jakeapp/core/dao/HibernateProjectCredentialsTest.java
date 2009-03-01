package com.jakeapp.core.dao;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.transaction.annotation.Transactional;
import org.apache.log4j.Logger;

import com.jakeapp.TestingConstants;
import com.jakeapp.core.dao.exceptions.NoSuchProjectException;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProtocolType;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.domain.exceptions.InvalidProjectException;
import com.jakeapp.core.services.MsgService;
import com.jakeapp.core.services.XMPPMsgService;


/**
 * Unit Tests for HibernateProjectDao
 */
@ContextConfiguration(locations = { "/com/jakeapp/core/dao/jake_core_test_hibernateGlobal_context.xml" })
public class HibernateProjectCredentialsTest extends AbstractJUnit4SpringContextTests {

	private static final String PROJECT_DAO_BEAN_ID = "projectDao";

	private static final String SERVICE_CREDENTIALS_DAO_BEAN_ID = "serviceCredentialsDao";

	private static final String TEMPLATE_BEAN_ID = "hibernateTemplate";

	private static Logger log = Logger.getLogger(HibernateProjectCredentialsTest.class);

	private IProjectDao projectDao;

	private IServiceCredentialsDao serviceCredentialsDao;

	private File systemTmpDir = new File(System.getProperty("java.io.tmpdir"));

	private HibernateTemplate template;

	private String project_1_uuid = "480f3166-06f5-40db-93b7-a69789d1fcd2";

	ServiceCredentials credentials1;

	Project project1 = new Project("someName", UUID.fromString(project_1_uuid), null,
			systemTmpDir);

	/**
	 * @return the projectDao
	 */
	private IProjectDao getProjectDao() {
		return projectDao;
	}

	/**
	 * @param projectDao
	 *            the projectDao to set
	 */
	private void setProjectDao(IProjectDao projectDao) {
		this.projectDao = projectDao;
	}

	private void setServiceCredentialsDao(IServiceCredentialsDao serviceCredentialsDao) {
		this.serviceCredentialsDao = serviceCredentialsDao;
	}

	private IServiceCredentialsDao getServiceCredentialsDao() {
		return serviceCredentialsDao;
	}

	/**
	 * @return the template
	 */
	private HibernateTemplate getTemplate() {
		return template;
	}

	/**
	 * @param template
	 *            the template to set
	 */
	private void setTemplate(HibernateTemplate template) {
		this.template = template;
	}

	@Before
	public void setUp() {
		this.setProjectDao((IProjectDao) this.applicationContext
				.getBean(PROJECT_DAO_BEAN_ID));
		this.setServiceCredentialsDao(((IServiceCredentialsDao) applicationContext
				.getBean(SERVICE_CREDENTIALS_DAO_BEAN_ID)));
		this.setTemplate((HibernateTemplate) applicationContext
				.getBean(HibernateProjectCredentialsTest.TEMPLATE_BEAN_ID));

		credentials1 = new ServiceCredentials("me@localhost", "mypasswd", ProtocolType.XMPP);
		credentials1.setUuid(project_1_uuid);
		credentials1.setSavePassword(true);
		project1.setCredentials(credentials1);

		if (!this.getTemplate().getSessionFactory().getCurrentSession().isOpen()) {
			log.debug("opening session");
			this.getTemplate().getSessionFactory().openSession();
		} else {
			log.debug("session already open");
		}
		this.getTemplate().getSessionFactory().getCurrentSession().getTransaction()
				.begin();
	}

	@After
	public void tearDown() {
		/* rollback for true unit testing */
		this.getTemplate().getSessionFactory().getCurrentSession().getTransaction()
				.rollback();
	}

	@Transactional
	@Test
	public final void testProjectDao() throws InvalidProjectException,
			NoSuchProjectException {
		Project actual;
		ServiceCredentials credentials = this.getServiceCredentialsDao().create(
				credentials1);
		actual = this.getProjectDao().create(project1);
		Assert.assertEquals(project1, actual);
		Assert.assertEquals(1, this.getProjectDao().getAll().size());
	}

	@Transactional
	@Test
	public final void testServiceCredentialsDao() throws InvalidProjectException,
			NoSuchProjectException {
		
		//since rollback may not have happened, delete possibly inserted credentials1
		try {
			this.getServiceCredentialsDao().delete(credentials1);
		}
		catch (Exception ex) {
			//empty handling
		}
		
		int before = this.getServiceCredentialsDao().getAll().size();
		ServiceCredentials credentials = this.getServiceCredentialsDao().create(
				credentials1);
		Assert.assertEquals(credentials.getUuid(), credentials1.getUuid());
		Assert.assertEquals(credentials.getPlainTextPassword(), credentials1.getPlainTextPassword());
		Assert.assertEquals(credentials.getUserId(), credentials1.getUserId());
		Assert.assertEquals(credentials.getProtocolType(), credentials1.getProtocolType());
		Assert.assertEquals(before + 1, this.getServiceCredentialsDao().getAll().size());
	}


	@Transactional
	@Test
	public final void testProjectWithServiceCredentialsDao()
			throws InvalidProjectException, NoSuchProjectException {
		Project project;
		project = this.getProjectDao().create(project1);
		Assert.assertEquals(project1, project);
		Assert.assertEquals(1, this.getProjectDao().getAll().size());

		// ServiceCredentials credentials =
		// this.getServiceCredentialsDao().create(credentials1);
		// Assert.assertEquals(credentials, credentials1);
		// Assert.assertEquals(1,
		// this.getServiceCredentialsDao().getAll().size());

		project.setCredentials(credentials1);
		project = this.getProjectDao().update(project);
		Assert.assertEquals(credentials1, project.getCredentials());

		Assert.assertEquals(1, this.getProjectDao().getAll().size());
		project = this.getProjectDao().getAll().get(0);
		Assert.assertEquals(credentials1.getUserId(), project.getCredentials()
				.getUserId());
	}

	@Transactional
	@Test
	public final void testProjectWithMsgServiceDao() throws Exception {
		Project p;
		ServiceCredentials sc = new ServiceCredentials("username", "password", ProtocolType.XMPP);
		sc.setUuid(project_1_uuid);
		
		// don't do this, the project's create saves it for us
		// this.getServiceCredentialsDao().create(sc);
		
		// create Project
		p = new Project("lol", UUID.fromString(project_1_uuid), new XMPPMsgService(),
				new File("/home/lol"));

		// connect Project and MessageService
		p.getMessageService().setServiceCredentials(sc);
		p.setCredentials(sc);

		// persist Project
		this.projectDao.create(p);

		/* CALL */
		p = this.getProjectDao().read(UUID.fromString(project_1_uuid));
		/* VALIDATE METHOD RESULTS */
		Assert.assertNotNull(p);
		Assert.assertNotNull(p.getMessageService());
		Assert.assertNotNull(p.getCredentials());
		Assert.assertEquals("username", p.getCredentials().getUserId());
	}


	@Transactional
	@Test(expected = InvalidCredentialsException.class)
	public final void testDoubleCreateShouldFail() throws Exception {
		UUID uuid = UUID.randomUUID();
		ServiceCredentials sc1 = new ServiceCredentials("username", "password",
				ProtocolType.XMPP);
		sc1.setUuid(uuid);
		sc1.setSavePassword(true);

		ServiceCredentials sc2 = new ServiceCredentials("username2", "password2",
				ProtocolType.XMPP);
		sc2.setUuid(uuid);
		sc2.setSavePassword(true);

		this.serviceCredentialsDao.create(sc1);
		this.serviceCredentialsDao.create(sc2);

	}
	
	@Transactional
	@Test
	public final void testSilentUpdateCredentials() throws Exception {
		UUID uuid = UUID.randomUUID();
		ServiceCredentials sc1 = new ServiceCredentials("username", "password", ProtocolType.XMPP);
		sc1.setUuid(uuid);
		sc1.setSavePassword(true);
		
		ServiceCredentials sc2 = new ServiceCredentials("username2", "password2", ProtocolType.XMPP);
		sc2.setUuid(uuid);
		sc2.setSavePassword(true);
		
		int origsize = this.serviceCredentialsDao.getAll().size();
		
		this.serviceCredentialsDao.create(sc1);
		this.serviceCredentialsDao.update(sc2);
	
		List<ServiceCredentials> list = this.serviceCredentialsDao.getAll();
		
		Assert.assertEquals(origsize + 1, list.size());
		ServiceCredentials entry = list.get(origsize);
		
		Assert.assertEquals(sc2.getUserId(), entry.getUserId());
		Assert.assertEquals(sc2.getUuid(), entry.getUuid());
		// Assert.assertEquals(sc2, entry);
		Assert.assertEquals(sc2.getPlainTextPassword(), entry.getPlainTextPassword());
		Assert.assertFalse(sc1.equals(entry));
	}

	@Test
	public final void testSilentReplaceCredentials() throws Exception {
		UUID uuid = UUID.randomUUID();

		int origsize = this.serviceCredentialsDao.getAll().size();
		ServiceCredentials sc1 = new ServiceCredentials("username", "password", ProtocolType.XMPP);
		sc1.setUuid(uuid);
		sc1.setSavePassword(true);
		this.serviceCredentialsDao.create(sc1);
		
		sc1 = this.serviceCredentialsDao.read(UUID.fromString(sc1.getUuid())); 
		
		ServiceCredentials sc2 = new ServiceCredentials("username2", "password2", ProtocolType.XMPP);
		sc2.setUuid(UUID.fromString(uuid.toString()));
		sc2.setSavePassword(true);
		
		this.serviceCredentialsDao.update(sc2);
	
		List<ServiceCredentials> list = this.serviceCredentialsDao.getAll();
		
		Assert.assertEquals(origsize + 1, list.size());
		ServiceCredentials entry = list.get(origsize);
		
		Assert.assertEquals(sc2.getUserId(), entry.getUserId());
		Assert.assertEquals(sc2.getUuid(), entry.getUuid());
		// Assert.assertEquals(sc2, entry);
		Assert.assertEquals(sc2.getPlainTextPassword(), entry.getPlainTextPassword());
		Assert.assertFalse(sc1.equals(entry));
	}
}
