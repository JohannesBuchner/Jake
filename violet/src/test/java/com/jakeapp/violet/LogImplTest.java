package com.jakeapp.violet;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.jakeapp.jake.test.TmpdirEnabledTestCase;
import com.jakeapp.violet.di.DI;
import com.jakeapp.violet.di.KnownProperty;
import com.jakeapp.violet.model.JakeObject;
import com.jakeapp.violet.model.Log;
import com.jakeapp.violet.model.LogEntry;
import com.jakeapp.violet.model.LogImpl;
import com.jakeapp.violet.model.ProjectModelImpl;
import com.jakeapp.violet.model.User;
import com.jakeapp.violet.model.exceptions.NoSuchLogEntryException;

public class LogImplTest extends TmpdirEnabledTestCase {

	private File f;

	private Log log;

	private Boolean known = true;

	private String how = "1234567898765432123456789";

	private String why = "because I want";

	private JakeObject what = new JakeObject("my/file.txt");

	private User me = new User("me@localhost");

	private User who = new User("someone@localhost");

	private Timestamp when1 = new Timestamp(12000000);

	private Timestamp when2 = new Timestamp(12001000);

	private Timestamp when3 = new Timestamp(12002000);

	private UUID id = new UUID(12, 31);

	@Before
	public void setUp() throws Exception {
		super.setup();

		String filename = DI.getProperty(KnownProperty.PROJECT_FILENAMES_LOG);

		f = new File(tmpdir, filename);
		LogImpl log = new LogImpl();
		log.setFile(tmpdir);
		Assert.assertFalse(f.exists());
		this.log = log;
		log.connect();
		Assert.assertTrue(f.exists());

		LogEntry le;
		le = new LogEntry(null, when1, me, what, why, how, known);
		log.add(le);
		le = new LogEntry(null, when2, me, what, why, "", known);
		log.add(le);
		le = new LogEntry(null, when3, who, what, why, how, false);
		log.add(le);

	}

	@Test
	public void disconnect() throws Exception {
		log.disconnect();
		Assert.assertTrue(f.exists());
		log.connect();
		LogEntry le;
		le = new LogEntry(id, when2, who, what, why, how, known);
		log.add(le);
		log.disconnect();
	}

	@Test
	public void testAddGetById() throws SQLException, NoSuchLogEntryException {
		LogEntry le = new LogEntry(null, when1, who, what, why, how, true);
		log.add(le);
		LogEntry le2 = log.getById(le.getId(), false);
		Assert.assertEquals(le, le2);
	}

	@Test(expected = NoSuchLogEntryException.class)
	public void testNoAddGetById() throws SQLException, NoSuchLogEntryException {
		LogEntry le = new LogEntry(null, when1, who, what, why, how, true);
		log.getById(le.getId(), false);
	}

	@Test(expected = NoSuchLogEntryException.class)
	public void testAddUnprocessedGetById() throws SQLException,
			NoSuchLogEntryException {
		LogEntry le = new LogEntry(null, when1, who, what, why, how, false);
		log.add(le);
		log.getById(le.getId(), false);
	}

}
