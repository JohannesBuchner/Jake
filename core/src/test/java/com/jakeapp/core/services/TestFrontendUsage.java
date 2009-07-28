package com.jakeapp.core.services;

import com.jakeapp.TestingConstants;
import com.jakeapp.core.domain.Account;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProtocolType;
import com.jakeapp.jake.test.FSTestCommons;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.util.HashMap;


public class TestFrontendUsage {


	protected File tmpdir;

	@After
	public void teardown() throws Exception {
		if (tmpdir.exists())
			Assert.assertTrue(FSTestCommons.recursiveDelete(tmpdir));
		Assert.assertFalse("Cleanup done", tmpdir.exists());
	}


	private IFrontendService frontend;

	private String sessionId;

	private IProjectsManagingService pms;

	private static final String id = "testuser1@my.provider";

	private static final String password = "mypasswd";

	@Before
	public void setup() throws Exception {

		tmpdir = FSTestCommons.provideTempDir();

		FSTestCommons.recursiveDelete(new File(".jake"));
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				new String[]{"/com/jakeapp/core/applicationContext.xml"});
		frontend = (IFrontendService) applicationContext.getBean("frontendService");

		sessionId = frontend.authenticate(new HashMap<String, String>(), null);
		pms = frontend.getProjectsManagingService(sessionId);
	}


	@Test
	public void testCreateProject() throws Exception {
		Account cred = new Account(id, password, ProtocolType.XMPP);
		MsgService msg = frontend.addAccount(sessionId, cred);

		Project project = pms.createProject(tmpdir.getName(), tmpdir.getAbsolutePath(),
				msg);
		Assert.assertNotNull(project.getMessageService());

		Assert.assertNotNull(project.getUserId());

		Assert.assertEquals(1, pms.getProjectUsers(project).size());
		Assert.assertEquals(project.getUserId().getUserId(), pms.getProjectUsers(project)
				.get(0).getUserId());

		Assert.assertEquals(msg.getUserId(), project.getUserId());
	}

	/*
	 * should not work any more (and should throw a IllegalArgumentException),
	 * since creating a project must always include setting a correct
	 * MsgService.
	 */
	@Test(timeout = TestingConstants.UNITTESTTIME, expected = IllegalArgumentException.class)
	public void testCreateAndAssignAfterwards() throws Exception {
		Project project = pms.createProject(tmpdir.getName(), tmpdir.getAbsolutePath(),
				null);
		Assert.assertNull(project.getMessageService());

		Account cred = new Account(id, password, ProtocolType.XMPP);
		MsgService msg = frontend.addAccount(sessionId, cred);

		project.setMessageService(msg);

		Assert.assertNotNull(project.getMessageService());

		Assert.assertNotNull(project.getUserId());

		Assert.assertEquals(1, pms.getProjectUsers(project).size());
		Assert.assertEquals(project.getUserId().getUserId(), pms.getProjectUsers(project)
				.get(0).getUserId());

		Assert.assertEquals(msg.getUserId(), project.getUserId());
	}

	@Test
	public void testProjectRoundtrip() throws Exception {
		Account cred = new Account(id, password, ProtocolType.XMPP);
		MsgService msg = frontend.addAccount(sessionId, cred);
		int projectCount;

		Assert.assertNotNull(msg);
		Assert.assertEquals(cred.getProtocolType(), msg.getServiceCredentials().getProtocolType());
		Assert.assertEquals(cred.getUserId(), msg.getServiceCredentials().getUserId());
		Assert.assertEquals(cred.getUuid(), msg.getServiceCredentials().getUuid());

		projectCount = pms.getProjectList(msg).size();

		Project project = pms.createProject(tmpdir.getName(), tmpdir.getAbsolutePath(),
				msg);
		Assert.assertNotNull(project.getMessageService());
		Assert.assertNotNull(project.getUserId());

		Assert.assertEquals(1, pms.getProjectUsers(project).size());
		Assert.assertEquals(project.getUserId().getUserId(), pms.getProjectUsers(project)
				.get(0).getUserId());

		Assert.assertEquals(msg.getUserId(), project.getUserId());
		Assert.assertEquals(projectCount + 1, pms.getProjectList(msg).size());

		// Assert.assertEquals(project, pms.openProject(project));
		Assert.assertTrue(pms.getProjectList(msg).contains(project));

		pms.closeProject(project);
		Assert.assertFalse(pms.getProjectList(msg).contains(project));

		Assert.assertEquals(project, pms.openProject(project));
		Assert.assertTrue(pms.getProjectList(msg).contains(project));

		Assert.assertTrue(pms.deleteProject(project, true));
		Assert.assertFalse(pms.getProjectList(msg).contains(project));
	}


}
