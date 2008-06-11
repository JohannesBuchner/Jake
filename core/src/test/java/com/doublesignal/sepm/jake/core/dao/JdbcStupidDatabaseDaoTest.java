package com.doublesignal.sepm.jake.core.dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Random;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.doublesignal.sepm.jake.core.dao.exceptions.NoSuchConfigOptionException;
import com.doublesignal.sepm.jake.core.domain.FileObject;
import com.doublesignal.sepm.jake.core.domain.JakeObject;
import com.doublesignal.sepm.jake.core.services.exceptions.NoSuchFileException;

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
	
	public static HsqlJakeDatabase setUpDatabase() throws Exception {
		return setUpDatabase(null);
	}

	public static HsqlJakeDatabase setUpDatabase(String dbfile) throws Exception {
		HsqlJakeDatabase db = new HsqlJakeDatabase();
		JdbcConfigurationDao cd = new JdbcConfigurationDao();
		JdbcJakeObjectDao jod = new JdbcJakeObjectDao();
		JdbcProjectMemberDao pmd = new JdbcProjectMemberDao();
		JdbcLogEntryDao led = new JdbcLogEntryDao();
		
		DriverManagerDataSource ds = new DriverManagerDataSource();
		ds.setDriverClassName("org.hsqldb.jdbcDriver");
		Random r = new Random();
		if(dbfile == null)
			ds.setUrl("jdbc:hsqldb:mem:newAndEmptyDatabase" + r.nextInt()+";ifexists=false");
		else
			ds.setUrl("jdbc:hsqldb:file:" + dbfile + ";ifexists=false");
		ds.setUsername("sa");
		
		cd.setDataSource(ds);
		jod.setDataSource(ds);
		pmd.setDataSource(ds);
		led.setDataSource(ds);
		
		ClassPathResource scriptres = new ClassPathResource("skeleton.script");
		BufferedReader fis = new BufferedReader(new FileReader(scriptres.getFile()));
		
		while (true) {
			String l = fis.readLine();
			if (l == null)
				break;
			if (l.startsWith("CREATE TABLE ") || l.startsWith("INSERT ") || 
					l.startsWith("UPDATE ") || l.startsWith("DELETE ")) {
				ds.getConnection().createStatement().execute(l);
			}
		}
		if (fis != null)
			fis.close();
		ds.getConnection().commit();
		db.setConfigurationDao(cd);
		db.setJakeObjectDao(jod);
		db.setProjectMemberDao(pmd);
		db.setLogEntryDao(led);
		return db;
	}
	public static void teardownDatabase(HsqlJakeDatabase db) throws Exception{
		Connection c = ((JdbcConfigurationDao)db.getConfigurationDao()).getDataSource().
			getConnection();
		c.rollback();
		c.close();
	}
	
	/* got obsolete by explicit check in save()
	@Test
	public void testSave() throws Exception {
		FileObject jo = new FileObject("foo");
		HsqlJakeDatabase db = setUpDatabase();
		db.getJakeObjectDao().save(jo);
		try{
			db.getJakeObjectDao().getFileObjectByName("foo");
			// It seems that generic JakeObjects aren't saved 
			Assert.fail();
		}catch (NoSuchFileException e){
			
		}
	}
	*/
	
}
