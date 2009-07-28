package com.jakeapp.core.dao;

import com.jakeapp.core.dao.exceptions.NoSuchProjectException;
import com.jakeapp.core.domain.Account;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProtocolType;
import com.jakeapp.core.domain.exceptions.InvalidProjectException;
import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.UUID;


/**
 * Unit Tests for HibernateProjectDao
 */
@ContextConfiguration
public class AbstractProjectDaoTest extends AbstractTransactionalJUnit4SpringContextTests {
	private final int UNITTESTTIME = 1000;
	private static final String DAO_BEAN_ID = "projectDao";

	private static final String TEMPLATE_BEAN_ID = "hibernateTemplate";

	private static final String SERVICE_CREDENTIALS_DAO_BEAN_ID = "accountDao";

	private static Logger log = Logger.getLogger(AbstractProjectDaoTest.class);

	@Autowired
	private IProjectDao projectDao;

	@Autowired
	private IAccountDao accountDao;

	private File systemTmpDir = new File(System.getProperty("java.io.tmpdir"));



	private String project_1_uuid = "480f3166-06f5-40db-93b7-a69789d1fcd2";

	private String project_2_uuid = "b310b970-9bf0-4d30-a028-c236c0ebc0cd";

	private String project_3_uuid = "9f4f23c3-2437-4e2b-9b76-50f8e3333a97";

	private String project_4_uuid = "abf7783a-f1f8-41fc-9268-650cce915a6e";

	Project project1 = new Project("someName", UUID.fromString(project_1_uuid), null,
			systemTmpDir);

	Project project2 = new Project("someName", UUID.fromString(project_2_uuid), null,
			systemTmpDir);

	private Account cred;

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


	@Before
	public void setUp() {
		this.setProjectDao((IProjectDao) this.applicationContext.getBean(DAO_BEAN_ID));
		this.setServiceCredentialsDao(((IAccountDao) applicationContext
				.getBean(SERVICE_CREDENTIALS_DAO_BEAN_ID)));



		// if(this.getTemplate().getSessionFactory().getCurrentSession().
		// getTransaction().)


		// TODO BEGIN TRANSACTION

		cred = new Account("foo@bar", "", ProtocolType.XMPP);
		cred.setUuid(UUID.randomUUID());
		getServiceCredentialsDao().create(cred);
		
		project1.setCredentials(cred);
		project2.setCredentials(cred);
	}

	@After
	public void tearDown() {


		// this.getTemplate().getSessionFactory().getCurrentSession().
		// getTransaction().commit();

		/* rollback for true unit testing */
		// TODO ROLLBACK TRANSACTION
	}

	/**
	 * This test simply tries to persist a new Project into the database without
	 * getting an error
	 * 
	 * @throws NoSuchProjectException
	 * @throws com.jakeapp.core.domain.exceptions.InvalidProjectException
	 */
	// @Test(timeout = TestingConstants.UNITTESTTIME)
	@Transactional
	@Test
	public final void create_shouldAddProject() throws InvalidProjectException,
			NoSuchProjectException {
		Project actual;

		/* CALL */
		actual = this.getProjectDao().create(project1);
		/* CALL */
		// Test evaluation
		Assert.assertEquals(project1, actual);
	}

	/**
	 * This test tries to create a Project into the database and read it
	 * afterwards.
	 * 
	 * @throws NoSuchProjectException
	 *             if the Project is not found, indicating the persisting didn't
	 *             work.
	 * @throws com.jakeapp.core.domain.exceptions.InvalidProjectException
	 */
	// @Test(timeout = TestingConstants.UNITTESTTIME)
	@Transactional
	@Test
	public final void create_ProjectShouldBeReadable() throws NoSuchProjectException,
			InvalidProjectException {
		Project project_result;

		/* CALL */
		projectDao.create(project2);
		/* CALL */

		// Test evaluation
		project_result = projectDao.read(UUID.fromString(project_2_uuid));
		assertEquals(project2, project_result);
	}


	/**
	 * This test tries to read a non-existing project from the database
	 * 
	 * @throws NoSuchProjectException
	 *             If the Project requested is not found.
	 */
	@Transactional
	// @Test(timeout = TestingConstants.UNITTESTTIME, expected =
	// NoSuchProjectException.class)
	@Test(expected = NoSuchProjectException.class)
	public final void read_NonExistingProjectShouldFail() throws NoSuchProjectException {
		UUID uuid = UUID.randomUUID();
		/* CALL */
		projectDao.read(uuid);
		/* CALL */
	}

	/**
	 * This test creates a project and deletes it afterwards.
	 * 
	 * @throws com.jakeapp.core.dao.exceptions.NoSuchProjectException
	 *
	 * @throws com.jakeapp.core.domain.exceptions.InvalidProjectException
	 */
	@Transactional
	// @Test(timeout = TestingConstants.UNITTESTTIME)
	@Test
	public final void delete_shouldCreateAndDeleteAProject()
			throws NoSuchProjectException, InvalidProjectException {
		UUID current = UUID.fromString(project_3_uuid);
		Project project = new Project();
		project.setName("projectName");
		project.setProjectId(current);
		project.setRootPath(systemTmpDir);
		project.setCredentials(cred);

		/* CALL */
		projectDao.create(project);
		projectDao.delete(project);
		/* CALL */
	}

	/**
	 * This test creates a project, deletes it and then tries to query for the
	 * deleted project.
	 * 
	 * @throws com.jakeapp.core.dao.exceptions.NoSuchProjectException
	 *
	 * @throws com.jakeapp.core.domain.exceptions.InvalidProjectException
	 */
	// @Test(timeout = TestingConstants.UNITTESTTIME, expected =
	// NoSuchProjectException.class)
	@Test(expected = NoSuchProjectException.class)
	@Transactional
	public final void delete_shouldCreateAndReallyDeleteProject()
			throws NoSuchProjectException, InvalidProjectException {
		UUID current = null;

		try {
			current = UUID.fromString(project_4_uuid);
			Project project = new Project();
			project.setName("projectName");
			project.setProjectId(current);
			project.setRootPath(systemTmpDir);
			project.setCredentials(cred);

			/* CALL */
			projectDao.create(project);
			projectDao.delete(project);
			/* CALL */
		} catch (NoSuchProjectException nex) {

		}
		// Test evaluation
		projectDao.read(current);

		// fails if it gets here
	}

	/**
	 * This test tries to delete a non-existing project.
	 * 
	 * @throws com.jakeapp.core.dao.exceptions.NoSuchProjectException
	 * 
	 */
	@Transactional
	@Test(expected = NoSuchProjectException.class)
	public final void deleteNonExistingProject_shouldThrowException()
			throws NoSuchProjectException {
		UUID random_non_existing = null;
		Project example = null;
		random_non_existing = UUID.randomUUID();
		example = new Project();
		example.setProjectId(random_non_existing);


		projectDao.delete(example);

		// fails if it gets here
	}


	/**
	 * This test tries to create a null-project
	 * @throws com.jakeapp.core.domain.exceptions.InvalidProjectException
	 */
	@Test(timeout = UNITTESTTIME, expected = InvalidProjectException.class)
	public final void create_persistNullshouldFail() throws InvalidProjectException {
		projectDao.create(null);
	}

	/**
	 * This test tries to update a null-project
	 * @throws com.jakeapp.core.dao.exceptions.NoSuchProjectException
	 */
	@Test(timeout = UNITTESTTIME, expected = NoSuchProjectException.class)
	public final void update_persistNullShouldFail() throws NoSuchProjectException {
		projectDao.update(null);
	}

	/**
	 * This test tries to delete a null-project
	 * @throws com.jakeapp.core.dao.exceptions.NoSuchProjectException
	 */
	@Test(timeout = UNITTESTTIME, expected = NoSuchProjectException.class)
	public final void delete_persistNullShouldFail() throws NoSuchProjectException {
		projectDao.delete(null);
	}


	/**
	 * This test tries to get a null-project
	 * @throws com.jakeapp.core.dao.exceptions.NoSuchProjectException
	 */
	@Test(timeout = UNITTESTTIME, expected = NoSuchProjectException.class)
	public final void read_persistNullShouldFail() throws NoSuchProjectException {
		projectDao.read(null);
	}


	/**
	 * This test tries to persist a project with null as uuid
	 * 
	 * @throws InvalidProjectException
	 */
	@Test(timeout = UNITTESTTIME, expected = InvalidProjectException.class)
	public final void create_persistProjectWithNullUUID() throws InvalidProjectException {
		Project project = new Project();
		project.setName("projectName");
		project.setProjectId((UUID) null);
		project.setCredentials(cred);

		projectDao.create(project);
	}

	/**
	 * This test tries to persist a project with null as uuid
	 * 
	 * @throws InvalidProjectException
	 * @throws com.jakeapp.core.dao.exceptions.NoSuchProjectException
	 */
	@Test(timeout = UNITTESTTIME, expected = NoSuchProjectException.class)
	public final void update_persistProjectWithNullUUID() throws NoSuchProjectException {
		Project project = new Project();
		project.setProjectId((UUID) null);

		projectDao.update(project);
	}

	/**
	 * This test tries to delete a project with null as uuid
	 * 
	 * @throws InvalidProjectException
	 * @throws com.jakeapp.core.dao.exceptions.NoSuchProjectException
	 */
	@Test(timeout = UNITTESTTIME, expected = NoSuchProjectException.class)
	public final void delete_persistProjectWithNullUUID() throws NoSuchProjectException {
		Project project = new Project();
		project.setProjectId((UUID) null);

		projectDao.delete(project);
	}

	//
	// @Test(expected = NoSuchProjectException.class)
	// public void persistAndRead() throws NoSuchProjectException {
	// projectDao.update(project);
	// List<Project> result = projectDao.getAll();
	// assertTrue(result.contains(project));
	// }
	//
	// @Test
	// public void persistReadMakeTransientRead() {
	// projectDao.update(project);
	// List<Project> result = projectDao.getAll();
	// assertTrue(result.contains(project));
	//
	// try {
	// projectDao.delete(project);
	// assertFalse(result.contains(project));
	// } catch (NoSuchProjectException e) {
	// log.debug("failed to delete projec");
	// fail();
	// }
	// }

	public void setServiceCredentialsDao(IAccountDao accountDao) {
		this.accountDao = accountDao;
	}

	public IAccountDao getServiceCredentialsDao() {
		return accountDao;
	}
}
