package com.doublesignal.sepm.jake.core.dao;

import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import com.doublesignal.sepm.jake.core.dao.exceptions.NoSuchConfigOptionException;
import static junit.framework.Assert.assertEquals;

/**
 * Tests for the JDBC Configuration DAO
 */
public class JdbcConfigurationDaoTest extends DBTest {
	private static JdbcConfigurationDao dao;
	@BeforeClass
	public static void setDAO() {
		dao = new JdbcConfigurationDao();
		DriverManagerDataSource ds = new DriverManagerDataSource();
		ds.setDriverClassName("org.hsqldb.jdbcDriver");
		ds.setUrl("jdbc:hsqldb:mem:db");
		ds.setUsername("sa");
		dao.setDataSource(ds);
	}

	@Test
	public void testExistsConfigurationValue() {
		assertEquals("Config option 'foo' should exist", true, dao.existsConfigurationValue("foo"));
		assertEquals("Config option 'asdfgh' should exist", false, dao.existsConfigurationValue("asdfgh"));
	}

	@Test
	public void testGetExistingConfigurationValue() throws NoSuchConfigOptionException {
		String foo = dao.getConfigurationValue("foo");
		assertEquals("Value of config option 'foo' should equal bar", "bar", foo);
	}

	@Test(expected=NoSuchConfigOptionException.class)
	public void testGetNonexistingConfigurationValue() throws NoSuchConfigOptionException {
		dao.getConfigurationValue("DOESNOTEXIST");
	}

	@Test
	public void testDeleteExistingConfigurationValue() {
		assertEquals("Config option 'deleteme' should exist", true, dao.existsConfigurationValue("deleteme"));
		dao.deleteConfigurationValue("deleteme");
		assertEquals("Config option 'deleteme' should no longer exist", false, dao.existsConfigurationValue("deleteme"));
	}

	@Test
	public void testDeleteNonexistingConfigurationValue() {
		assertEquals("Config option 'DOESNOTEXIST' should not exist", false, dao.existsConfigurationValue("DOESNOTEXIST"));
		dao.deleteConfigurationValue("DOESNOTEXIST");
		assertEquals("Config option 'DOESNOTEXIST' should still not exist", false, dao.existsConfigurationValue("DOESNOTEXIST"));
	}

	@Test
	public void testSetExistingConfigurationValue() throws NoSuchConfigOptionException {
		assertEquals("Value of config option 'userid' should equal 'chris@jabber.doublesignal.com'",
				       "chris@jabber.doublesignal.com", dao.getConfigurationValue("userid"));
		dao.setConfigurationValue("userid", "dominik@jabber.fsinf.at");
		assertEquals("Value of config option 'userid' should now equal 'dominik@jabber.fsinf.at'",
				       "dominik@jabber.fsinf.at", dao.getConfigurationValue("userid"));
	}

	@Test
	public void testSetNonexistingConfigurationValue() throws NoSuchConfigOptionException {
		assertEquals("Config option 'loremipsum' should not exist", false, dao.existsConfigurationValue("loremipsum"));
		dao.setConfigurationValue("loremipsum", "dolor sit amet");
		assertEquals("Value of config option 'loremipsum' should now exist AND equal 'dolor sit amet'",
				       "dolor sit amet", dao.getConfigurationValue("loremipsum"));
	}
}
