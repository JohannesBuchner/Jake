package com.doublesignal.sepm.jake.core.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.util.Date;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import junit.framework.TestCase;

import com.doublesignal.sepm.jake.core.dao.HsqlJakeDatabase;
import com.doublesignal.sepm.jake.core.dao.JdbcConfigurationDao;
import com.doublesignal.sepm.jake.core.dao.JdbcJakeObjectDao;
import com.doublesignal.sepm.jake.core.dao.JdbcLogEntryDao;
import com.doublesignal.sepm.jake.core.dao.JdbcProjectMemberDao;
import com.doublesignal.sepm.jake.core.dao.exceptions.NoSuchProjectMemberException;
import com.doublesignal.sepm.jake.core.domain.FileObject;
import com.doublesignal.sepm.jake.core.domain.JakeObject;
import com.doublesignal.sepm.jake.core.domain.NoteObject;
import com.doublesignal.sepm.jake.core.domain.LogAction;
import com.doublesignal.sepm.jake.core.domain.LogEntry;
import com.doublesignal.sepm.jake.core.domain.ProjectMember;
import com.doublesignal.sepm.jake.core.services.JakeGuiAccess;
import com.doublesignal.sepm.jake.core.services.exceptions.*;
import com.doublesignal.sepm.jake.ics.exceptions.NotLoggedInException;
import com.doublesignal.sepm.jake.sync.exceptions.SyncException;
import com.doublesignal.sepm.jake.sync.MockSyncServiceTest;

public class JakeGuiAccessTest extends TestCase {
	
	String tmpdir = System.getProperty("java.io.tmpdir","") + File.separator;
	//String tmpdir = "/home/user/Desktop/foo2/";
	IJakeGuiAccess jga;
	ProjectMember pm;
	Date date;
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
	
	@Before
	public void setup() throws Exception{
		File rootPath = new File(tmpdir, "testProject");
		rootPath.mkdir();
		File jakeFile = new File(tmpdir, "testProject" + ".script");
		jakeFile.delete();
		File jakeFile2 = new File(tmpdir, "testProject" + ".properties");
		jakeFile2.delete();
		String rootfolder = rootPath.getAbsolutePath();
		if(!(rootPath.exists() && rootPath.isDirectory()))
			return;
		try {
			jga = JakeGuiAccess.openProjectByRootpath(rootfolder);
			fail("NonExistantDatabaseException");
		} catch (NonExistantDatabaseException e) {
			jga = JakeGuiAccess.createNewProjectByRootpath(rootfolder, 
					"testProject", "test@host");
		} catch (RuntimeException e){
			e.printStackTrace();
			fail();
		}
		assertNotNull(jga);
	}
	@Test
	public void testA() throws Exception{
		setup();
		assertNotNull(jga);
		assertNotNull(jga.getProject());
		assertEquals(jga.getProject().getName(),"testProject");
		assertEquals(jga.getLoginUserid(), "test@host");
	}
	
	@Test
	public void testEditProjectMemberUserId()throws Exception	{
		
		setup();
		String userNameOne = "testuser@domain.de";
		String userNameTwo = "user@domain.com";
	
		jga.addProjectMember(userNameOne);
		
		jga.editProjectMemberUserId(jga.getProjectMember(userNameOne) , userNameTwo);
		try	{
		jga.getProjectMember(userNameOne);
		fail();
		}	catch (NoSuchProjectMemberException e)	{
			 
		
		}
		
		
	}
		
	
	@Test
	public void testAddProjectMember()throws Exception	{
		setup();
		String userNameOne = "testuser@domain.de";
		jga.addProjectMember(userNameOne);
		assertNotNull(jga.getProjectMember(userNameOne));
		
		
	}
	
	@Test
	public void testGetProjectMember()throws Exception	{
		setup();
		String userNameOne = "testuser@domain.de";
		jga.addProjectMember(userNameOne);
		assertNotNull(jga.getProjectMember(userNameOne));
		
		
	}
	
	@Test
	public void testEditProjectMemberNickName()throws Exception	{
		setup();
		String userNameOne = "testuser@domain.de";
		String nickName = "User Nick Name";
		jga.addProjectMember(userNameOne);
		jga.editProjectMemberNickName(	jga.getProjectMember(userNameOne), nickName);
		assertEquals(jga.getProjectMember(userNameOne).getNickname() , nickName);
		
	}
	
	@Test
	public void testEditProjectMemberNote()throws Exception	{
		setup();
		String userNameOne = "testuser@domain.de";
		String note = "This is a Note!";
	
		jga.addProjectMember(userNameOne);
		jga.editProjectMemberNote(	jga.getProjectMember(userNameOne), note);
		assertEquals(jga.getProjectMember(userNameOne).getNotes() , note);
		
	}
	
	@Test
	public void testGetLog()throws Exception	{
		setup();
		String userId = "testuser@domain.de";
		String hash = "4567897";
		String comment = "This is a comment!";
		String jakeObjectName = "note::1213053294203";
		String jakeObjectName2 = "note::1213953294203";
		date = new Date();
		HsqlJakeDatabase db = setUpDatabase();
		NoteObject noteObject = new NoteObject(jakeObjectName, jakeObjectName);
		ProjectMember projectMember = new ProjectMember(userId);
		NoteObject noteObject2 = new NoteObject(jakeObjectName2, jakeObjectName2);
		db.getProjectMemberDao().save(projectMember);
		db.getJakeObjectDao().save(noteObject);
		db.getJakeObjectDao().save(noteObject2);
		
		LogEntry logEntry = new LogEntry(LogAction.TAG_ADD, date,jakeObjectName, hash, userId, comment);
		LogEntry logEntry3 = new LogEntry(LogAction.NEW_VERSION, date,jakeObjectName2, hash, userId, comment);
		
		db.getLogEntryDao().create(logEntry);
		db.getLogEntryDao().create(logEntry3);
		
		assertTrue(db.getLogEntryDao().getAll().size()==2);
		assertTrue(db.getLogEntryDao().getAll().get(0).equals(logEntry));
		assertFalse(db.getLogEntryDao().getAll().get(0).equals(db.getLogEntryDao().getAll().get(1)));
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
	
	@After
	public void tearDown(){
		File rootPath = new File(tmpdir, "testProject");
		rootPath.delete();
		File jakeFile = new File(tmpdir, "testProject" + ".script");
		jakeFile.delete();
		File jakeFile2 = new File(tmpdir, "testProject" + ".properties");
		jakeFile2.delete();
	}
	
}
