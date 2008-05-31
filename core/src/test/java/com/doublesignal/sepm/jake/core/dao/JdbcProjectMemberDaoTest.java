package com.doublesignal.sepm.jake.core.dao;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import com.doublesignal.sepm.jake.core.domain.ProjectMember;
import com.doublesignal.sepm.jake.core.dao.exceptions.NoSuchProjectMemberException;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.util.List;

/**
 * Test for the JDBC ProjectMember DAO
 */
public class JdbcProjectMemberDaoTest extends DBTest {
	private static JdbcProjectMemberDao dao;
	@BeforeClass
	public static void setDAO() {
		dao = new JdbcProjectMemberDao();
		DriverManagerDataSource ds = new DriverManagerDataSource();
		ds.setDriverClassName("org.hsqldb.jdbcDriver");
		ds.setUrl("jdbc:hsqldb:mem:db");
		ds.setUsername("sa");
		dao.setDataSource(ds);
	}

	@Test
	public void testGetExistingByUserid() throws NoSuchProjectMemberException {
		ProjectMember chris = dao.getByUserId("chris@jabber.doublesignal.com");
		assertEquals("Should be the right user (userid)", "chris@jabber.doublesignal.com", chris.getUserId());
		assertEquals("Should be the right user (nick)", "Chris", chris.getNickname());
		assertEquals("Should be the right user (notes)", "Supreme leader of earth", chris.getNotes());
	}

	@Test(expected= NoSuchProjectMemberException.class)
	public void testGetNonexistingByUserid() throws NoSuchProjectMemberException {
		dao.getByUserId("foo@bar");
	}

	@Test
	public void testGetAll() {
		List<ProjectMember> members = dao.getAll();
		assertEquals("There should be 3 members", 3, members.size());

		boolean containsChris = ((members.get(0).getUserId().equals("chris@jabber.doublesignal.com")) ||
				  (members.get(1).getUserId().equals("chris@jabber.doublesignal.com")) ||
				  (members.get(2).getUserId().equals("chris@jabber.doublesignal.com")));
		boolean containsDominik = ((members.get(0).getUserId().equals("dominik@jabber.fsinf.at")) ||
				  (members.get(1).getUserId().equals("dominik@jabber.fsinf.at")) ||
				  (members.get(2).getUserId().equals("dominik@jabber.fsinf.at")));
		boolean containsJohannes = ((members.get(0).getUserId().equals("j13r@jabber.ccc.de")) ||
				  (members.get(1).getUserId().equals("j13r@jabber.ccc.de")) ||
				  (members.get(2).getUserId().equals("j13r@jabber.ccc.de")));

		assertTrue("Chris should be in project members", containsChris);
		assertTrue("Dominik should be in project members", containsDominik);
		assertTrue("Johannes should be in project members", containsJohannes);
	}

	@Test
	public void testSaveExisting() throws NoSuchProjectMemberException {
		ProjectMember johannes = dao.getByUserId("j13r@jabber.ccc.de");
		johannes.setNickname("jb");
		johannes.setNotes("Lorem ipsum");
		dao.save(johannes);
		assertEquals("New nickname should now be retrievable", "jb", dao.getByUserId("j13r@jabber.ccc.de").getNickname());
		assertEquals("New notes should now be retrievable", "Lorem ipsum", dao.getByUserId("j13r@jabber.ccc.de").getNotes());
	}

	@Test
	public void testSaveNonexisting() throws NoSuchProjectMemberException {
		ProjectMember simon = new ProjectMember("simon.wallner@jabber.ccc.de");
		simon.setNickname("Simon");
		simon.setNotes("The boss");
		dao.save(simon);
		assertEquals("New nickname should now be retrievable", "Simon", dao.getByUserId("simon.wallner@jabber.ccc.de").getNickname());
		assertEquals("New notes should now be retrievable", "The boss", dao.getByUserId("simon.wallner@jabber.ccc.de").getNotes());
	}

	@Test
	public void testDeleteExisting() throws NoSuchProjectMemberException {
		ProjectMember chris = dao.getByUserId("chris@jabber.doublesignal.com");
		assertEquals("Chris should exist and be correct", "Chris", chris.getNickname());
		dao.remove(chris);
		
		/* We're not using the @Test(expected=...) annotation here so we can be sure that NoSuchProjectMemberException
		 * is thrown here and NOT at the first line of the method, which could cause the test to pass erroneously
		 * if the first line throws the Exception.
		 */
		try {
			dao.getByUserId("chris@jabber.doublesignal.com");
			fail();
		} catch(NoSuchProjectMemberException e) {
			/* Nothing should happen here */
		}
	}
}
