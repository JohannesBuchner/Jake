package com.jakeapp.core.dao;

import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;
import com.jakeapp.core.domain.FileObject;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@ContextConfiguration // local
public abstract class AbstractFileObjectDaoTest extends AbstractTransactionalJUnit4SpringContextTests {

	private static Logger log = Logger.getLogger(AbstractFileObjectDaoTest.class);

	private IFileObjectDao fileObjectDao;

	public IJakeObjectDao getFileObjectDao() {
		return fileObjectDao;
	}

	public void setFileObjectDao(IFileObjectDao fileObjectDao) {
		this.fileObjectDao = fileObjectDao;
	}

	@Before
	public void setUp() {
		// Add your code here
		this.setFileObjectDao((IFileObjectDao) this.applicationContext
				.getBean("fileObjectDao"));

		// TODO BEGIN TRANSACTION
	}

	@After
	public void tearDown() {
		// TODO COMMIT TRANSACTION -- should this really be done? can we proove hibernate is working with rolling back also?

		/* rollback for true unit testing */
		// this.getTemplate().getSessionFactory().getCurrentSession().
		// getTransaction().rollback();
	}


	@Transactional
	@Test
	public void testPersist() {
		FileObject obj1 = new FileObject(UUID
				.fromString("201fbce5-f910-4557-80b5-2be3eab2f0dd"), null, // project
				"/testPersist_test.txt");

		fileObjectDao.persist(obj1);

	}

	@Transactional
	@Test
	public void testGet() throws NoSuchJakeObjectException {
		FileObject obj1 = new FileObject(UUID
				.fromString("201fbce5-f910-aaaa-80b5-2be3eab2f0dd"), null, // project
				"/testGet_test.txt");

		this.fileObjectDao.persist(obj1);
		FileObject result;

		result = this.fileObjectDao.get(obj1.getUuid());
		Assert.assertEquals(obj1, result);
	}

	@Transactional
	@Test
	public void testGetAll() {
		FileObject obj1 = new FileObject(UUID
				.fromString("efcc5d3e-f7f1-4475-beac-7b5bd3bc34a5"), null, // project
				"/testGetAll_test1.txt");
		FileObject obj2 = new FileObject(UUID
				.fromString("91091dc4-eb7a-4d20-b4ac-e53ed0e24fc1"), null, // project
				"/testGetAll_test2.txt");
		FileObject obj3 = new FileObject(UUID
				.fromString("91e5628f-28df-4bb2-a472-5a347608134c"), null, // project
				"/testGetAll_test3.txt");
		FileObject obj4 = new FileObject(UUID
				.fromString("9271a754-73f4-40c4-a46f-7ab5f02391aa"), null, // project
				"/testGetAll_test4.txt");
		FileObject obj5 = new FileObject(UUID
				.fromString("84aa1f69-9c1c-401c-93f6-7616910ece06"), null, // project
				"/testGetAll_test5.txt");


		fileObjectDao.persist(obj1);
		fileObjectDao.persist(obj2);
		fileObjectDao.persist(obj3);
		fileObjectDao.persist(obj4);
		fileObjectDao.persist(obj5);

		List<FileObject> results = fileObjectDao.getAll();

		Assert.assertTrue(results.contains(obj1));
		Assert.assertTrue(results.contains(obj2));
		Assert.assertTrue(results.contains(obj3));
		Assert.assertTrue(results.contains(obj4));
		Assert.assertTrue(results.contains(obj5));

	}

	@Transactional
	@Test
	public void testDelete() throws NoSuchJakeObjectException {
		FileObject obj1 = new FileObject(UUID
				.fromString("efcc5d3e-f7f1-4475-aaaa-7b5bd3bc34a5"), null, // project
				"/testDelete_test1.txt");

		fileObjectDao.persist(obj1);
		List<FileObject> results;
		results = fileObjectDao.getAll();

		Assert.assertTrue(results.contains(obj1));

		fileObjectDao.delete(obj1);

		results.clear();

		results = fileObjectDao.getAll();

		Assert.assertFalse(results.contains(obj1));


	}

	@Transactional
	@Test
	public void testGetByRelPath() throws NoSuchJakeObjectException {
		FileObject fileObject, result;
		fileObject = new FileObject(UUID
				.fromString("3d32858a-166f-4204-8074-d11a2d745b9d"), null, "/blabla");

		fileObjectDao.persist(fileObject);

		result = fileObjectDao.get("/blabla");

		Assert.assertEquals(fileObject, result);

	}

}
