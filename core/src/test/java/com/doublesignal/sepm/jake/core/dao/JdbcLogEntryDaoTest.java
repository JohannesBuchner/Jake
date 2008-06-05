package com.doublesignal.sepm.jake.core.dao;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import com.doublesignal.sepm.jake.core.dao.exceptions.NoSuchLogEntryException;
import com.doublesignal.sepm.jake.core.domain.LogEntry;
import com.doublesignal.sepm.jake.core.domain.LogAction;
import com.doublesignal.sepm.jake.core.domain.JakeObject;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * Test for the JDBC LogEntry DAO
 */
public class JdbcLogEntryDaoTest extends DBTest {
	private static JdbcLogEntryDao dao;
	@BeforeClass
	public static void setDAO() {
		dao = new JdbcLogEntryDao();
		DriverManagerDataSource ds = new DriverManagerDataSource();
		ds.setDriverClassName("org.hsqldb.jdbcDriver");
		ds.setUrl("jdbc:hsqldb:mem:db");
		ds.setUsername("sa");
		dao.setDataSource(ds);
	}

	@Test
	public void testCreate() throws NoSuchLogEntryException {
		dao.create(new LogEntry(LogAction.TAG_REMOVE, new Date(new GregorianCalendar(2008, 5, 5, 3, 51, 0).getTimeInMillis()), "test.docx", "04B33F0D249353F473622BD3F60A5465681680FE64B376D78BBC0A3B5FF6F1437B9C2ACA299DB48162DE8B169165D14F9FD47BA8EE753694ACA3E51D9E5CCBB9", "chris@jabber.doublesignal.com", "microsoft"));

		LogEntry le = dao.get("test.docx", "chris@jabber.doublesignal.com", new Date(new GregorianCalendar(2008, 5, 5, 3, 51, 0).getTimeInMillis()));

		assertEquals("Name should match", "test.docx", le.getJakeObjectName());
		assertEquals("User should match", "chris@jabber.doublesignal.com", le.getUserId());
		assertEquals("Timestamp should match", new Date(new GregorianCalendar(2008, 5, 5, 3, 51, 0).getTimeInMillis()), le.getTimestamp());
		assertEquals("Type should match", LogAction.TAG_REMOVE, le.getAction());
		assertEquals("Hash should match", "04B33F0D249353F473622BD3F60A5465681680FE64B376D78BBC0A3B5FF6F1437B9C2ACA299DB48162DE8B169165D14F9FD47BA8EE753694ACA3E51D9E5CCBB9", le.getHash());
		assertEquals("Message should match", "microsoft", le.getComment());
		assertEquals("IsLastPulled should match", false, le.getIsLastPulled());
	}

	@Test
	public void testGet() throws NoSuchLogEntryException {
		LogEntry le = dao.get("test.docx", "dominik@jabber.fsinf.at", new Date(new GregorianCalendar(2008, 5, 1, 7, 22, 10).getTimeInMillis()));
		assertEquals("Name should match", "test.docx", le.getJakeObjectName());
		assertEquals("User should match", "dominik@jabber.fsinf.at", le.getUserId());
		assertEquals("Timestamp should match", new Date(new GregorianCalendar(2008, 5, 1, 7, 22, 10).getTimeInMillis()), le.getTimestamp());
		assertEquals("Type should match", LogAction.NEW_VERSION, le.getAction());
		assertEquals("Hash should match", "51687BE5C2800D2C5C54D088261B2B55EC9A5CB62AEC7CAAF4F82613F84A47E2BA0330EDF3A181D2B31684ADB53AA67A7D350C81C84F009D5030FC8C1C308989", le.getHash());
		assertEquals("Message should match", "", le.getComment());
		assertEquals("IsLastPulled should match", false, le.getIsLastPulled());
	}

	@Test(expected= NoSuchLogEntryException.class)
	public void testGetNonexisting() throws NoSuchLogEntryException {
		dao.get("wooooo.docx", "dominik@jabber.fsinf.at", new Date(new GregorianCalendar(2008, 6, 1, 7, 22, 10).getTimeInMillis()));
	}

	@Test
	public void testGetAll() {
		List<LogEntry> entries = dao.getAll();

		int testCount = 0;
		int pr0nCount = 0;
		int sepmCount = 0;
		int noteCount = 0;

		for(LogEntry le: entries) {
			if("test.docx".equals(le.getJakeObjectName())) {
				testCount++;
			}
			if("pr0n.jpg".equals(le.getJakeObjectName())) {
				assertEquals("pr0n.jpg should belong to dominik@jabber.fsinf.at", "dominik@jabber.fsinf.at", le.getUserId());
				pr0nCount++;
			}
			if("subfolder/sepm.txt".equals(le.getJakeObjectName())) {
				assertEquals("subfolder/sepm.txt should belong to chris@jabber.doublesignal.com", "chris@jabber.doublesignal.com", le.getUserId());
				sepmCount++;
			}
			if("note:j13r@jabber.ccc.de:20080531201910".equals(le.getJakeObjectName())) {
				noteCount++;
			}
		}

		assertEquals("There should be 3 entries for test.docx", 3, testCount);
		assertEquals("There should be 1 entry for pr0n.jpg", 1, pr0nCount);
		assertEquals("There should be 2 entries for the note", 2, noteCount);
		assertEquals("There should be 1 entry for subfolder/sepm.txt", 1, sepmCount);
	}

	@Test
	public void testGetAllOfJakeObject() {

	}

	@Test
	public void testGetMostRecent() {

	}

	@Test(expected= NoSuchLogEntryException.class)
	public void testGetMostRecentNonexisting() throws NoSuchLogEntryException {
		dao.getMostRecentFor(new JakeObject("jaksfsafsadfds"));
	}

	public void testGetAllOfJakeObjectNonexisting() throws NoSuchLogEntryException {
		List<LogEntry> entries = dao.getAllOfJakeObject(new JakeObject("jaksfsafsadfds"));
		assertEquals("Should return an empty list", 0, entries.size());
	}
}
