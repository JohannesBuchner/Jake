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
	public void testCreate() {
		
	}

	@Test
	public void testGet() throws NoSuchLogEntryException {
		LogEntry le = dao.get("test.docx", "dominik@jabber.fsinf.at", new Date(new GregorianCalendar(2008, 6, 1, 7, 22, 10).getTimeInMillis()));
		assertEquals("Name should match", "test.docx", le.getJakeObjectName());
		assertEquals("User should match", "test.docx", le.getUserId());
		assertEquals("Timestamp should match", new Date(new GregorianCalendar(2008, 6, 1, 7, 22, 10).getTimeInMillis()), le.getTimestamp());
		assertEquals("Type should match", LogAction.NEW_VERSION, le.getAction());
		assertEquals("Hash should match", "51687BE5C2800D2C5C54D088261B2B55EC9A5CB62AEC7CAAF4F82613F84A47E2BA0330EDF3A181D2B31684ADB53AA67A7D350C81C84F009D5030FC8C1C308989", le.getHash());
		assertEquals("Message should match", "", le.getComment());
		/*
		 * TODO: Update this once we know where the hell we get it from
		 */
		assertEquals("IsLastPulled should match", false, le.getIsLastPulled());
	}

	@Test(expected= NoSuchLogEntryException.class)
	public void testGetNonexisting() throws NoSuchLogEntryException {
		dao.get("wooooo.docx", "dominik@jabber.fsinf.at", new Date(new GregorianCalendar(2008, 6, 1, 7, 22, 10).getTimeInMillis()));
	}

	@Test
	public void testGetAll() {

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
}
