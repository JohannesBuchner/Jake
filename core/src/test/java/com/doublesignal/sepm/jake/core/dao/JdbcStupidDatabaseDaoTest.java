package com.doublesignal.sepm.jake.core.dao;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.doublesignal.sepm.jake.core.dao.exceptions.NoSuchConfigOptionException;

/**
 * Tests for the JDBC Configuration DAO
 */
public class JdbcStupidDatabaseDaoTest extends DBTest {
	private static JdbcConfigurationDao dao;
	@BeforeClass
	public static void setDAO() {
		dao = new JdbcConfigurationDao();
		DriverManagerDataSource ds = new DriverManagerDataSource();
		ds.setDriverClassName("org.hsqldb.jdbcDriver");
		ds.setUrl("jdbc:hsqldb:mem:dbNewNoSchema");
		ds.setUsername("sa");
		dao.setDataSource(ds);
	}

	@Test(expected=BadSqlGrammarException.class)
	public void testExistsConfigurationValue() {
		dao.existsConfigurationValue("foo");
	}

	@Test(expected=BadSqlGrammarException.class)
	public void testGetConfigurationValue() throws NoSuchConfigOptionException {
		dao.getConfigurationValue("foo");
	}
}
