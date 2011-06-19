package com.jakeapp.core.synchronization;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.jakeapp.jake.fss.FSService;
import com.jakeapp.jake.fss.HashValue;
import com.jakeapp.jake.fss.ProjectDir;
import com.jakeapp.jake.test.TmpdirEnabledTestCase;
import com.jakeapp.violet.actions.project.local.AttributedCalculator;
import com.jakeapp.violet.model.JakeObject;
import com.jakeapp.violet.model.Log;
import com.jakeapp.violet.model.LogEntry;
import com.jakeapp.violet.model.LogImpl;
import com.jakeapp.violet.model.User;
import com.jakeapp.violet.model.attributes.Attributed;
import com.jakeapp.violet.model.attributes.Existence;
import com.jakeapp.violet.model.attributes.SyncStatus;

public class TestSyncService extends TmpdirEnabledTestCase {

	private static final String ORIGINAL_CONTENT = "mycontent";

	private static final String hash = "be23baf848cb92aabbc648b89a92cb68e434369b87fe0415b2642b2ec0ddff7a7fb52f3dc9786a712acc127822356d79f0260ed1570084a4baab806ec513d1ab";

	private static final String MODIFIED_CONTENT = "hello foo bar\nbla";

	private JakeObject jo = new JakeObject("my/foo");

	private User me = new User("me@localhost");

	private FSService fss;

	private Log log;

	@Override
	public void setup() throws Exception {
		super.setup();

		HashValue.DIGEST = "SHA-512";
		HashValue.N_BITS = 512;

		fss = new FSService();
		fss.setRootPath(new ProjectDir(tmpdir));
		LogImpl log = new LogImpl();
		log.setFile(tmpdir);
		this.log = log;
	}

	@After
	public void teardown() throws Exception {
		log.disconnect();
		fss.unsetRootPath();
		super.teardown();
	}

	private void countLogEntries(int count) throws SQLException {
		Assert.assertEquals(count, log.getAll(true).size());
	}

	private Attributed getAttributed() throws Exception {
		return AttributedCalculator.calculateAttributed(fss, log, jo);
	}

	@Test
	public void testStatus_NonExistantNote() throws Exception {
		Attributed status = getAttributed();

		countLogEntries(0);

		Assert.assertEquals(jo, status.getJakeObject());
		Assert.assertEquals(Existence.NON_EXISTANT, status.getExistence());
		Assert.assertEquals(SyncStatus.SYNC, status.getSyncStatus());
		countLogEntries(0);

	}

	@Test
	public void testStatus_NewNote() throws Exception {
		fss.writeFile(jo.getRelPath(), ORIGINAL_CONTENT.getBytes());

		Attributed status = getAttributed();

		Assert.assertEquals(jo, status.getJakeObject());
		Assert.assertEquals(Existence.EXISTS_LOCAL, status.getExistence());
		Assert.assertEquals(SyncStatus.MODIFIED_LOCALLY, status.getSyncStatus());
	}

	@Test
	public void testStatus_AnnounceNote() throws Exception {
		testStatus_NewNote();
		LogEntry le = new LogEntry(null, null, me, jo, "done", hash, true);
		log.add(le);

		Attributed status = getAttributed();

		Assert.assertEquals(jo, status.getJakeObject());
		Assert.assertEquals(Existence.EXISTS_ON_BOTH, status.getExistence());
		Assert.assertEquals(SyncStatus.SYNC, status.getSyncStatus());
	}

	@Test
	public void testStatus_ModifyAnnouncedNote() throws Exception {
		testStatus_AnnounceNote();
		fss.writeFile(jo.getRelPath(), MODIFIED_CONTENT.getBytes());

		Attributed status = getAttributed();

		Assert.assertEquals(jo, status.getJakeObject());
		Assert.assertEquals(Existence.EXISTS_ON_BOTH, status.getExistence());
		Assert.assertEquals(SyncStatus.MODIFIED_LOCALLY, status.getSyncStatus());
	}

	@Test
	public void testStatus_DeleteNewNote() throws Exception {
		testStatus_ModifyAnnouncedNote();
		LogEntry le = new LogEntry(null, null, me, jo, "done", hash, true);
		log.add(le);

		Attributed status = getAttributed();

		Assert.assertEquals(jo, status.getJakeObject());
		Assert.assertEquals(Existence.NON_EXISTANT, status.getExistence());
		Assert.assertEquals(SyncStatus.SYNC, status.getSyncStatus());
	}

	@Test
	public void testStatus_DeleteAnnouncedNote() throws Exception {
		testStatus_AnnounceNote();
		fss.deleteFile(jo.getRelPath());
		Attributed status = getAttributed();

		Assert.assertEquals(jo, status.getJakeObject());
		Assert.assertEquals(Existence.EXISTS_REMOTE, status.getExistence());
		Assert.assertEquals(SyncStatus.MODIFIED_LOCALLY, status.getSyncStatus());
	}
}
