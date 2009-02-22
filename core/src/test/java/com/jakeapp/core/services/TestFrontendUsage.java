package com.jakeapp.core.services;

import com.jakeapp.TestingConstants;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProtocolType;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.jake.test.FSTestCommons;
import com.jakeapp.jake.test.TmpdirEnabledTestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.util.HashMap;


public class TestFrontendUsage extends TmpdirEnabledTestCase {

	private IFrontendService frontend;

	private String sessionId;

	private IProjectsManagingService pms;

	private static final String id = "testuser1@my.provider";

	private static final String password = "mypasswd";

	@Override
	@Before
	public void setup() throws Exception {
		super.setup();

		FSTestCommons.recursiveDelete(new File(".jake"));
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				new String[] { "/com/jakeapp/core/applicationContext.xml" });
		frontend = (IFrontendService) applicationContext.getBean("frontendService");

		sessionId = frontend.authenticate(new HashMap<String, String>());
		pms = frontend.getProjectsManagingService(sessionId);
	}

	
	@Test
	public void testCreateProject() throws Exception {
		ServiceCredentials cred = new ServiceCredentials(id, password);
		cred.setProtocol(ProtocolType.XMPP);
		MsgService msg = frontend.addAccount(sessionId, cred);

		Project project = pms.createProject(tmpdir.getName(), tmpdir.getAbsolutePath(),
				msg);
		Assert.assertNotNull(project.getMessageService());

		Assert.assertNotNull(project.getUserId());

		Assert.assertEquals(1, pms.getProjectUsers(project).size());
		Assert.assertEquals(project.getUserId().getUserId(), pms.getProjectUsers(
				project).get(0).getUserId());

		Assert.assertEquals(msg.getUserId(), project.getUserId());
	}

	/*
	 * should not work any more (and should throw
	 * a IllegalArgumentException), since creating a project must always include
	 * setting a correct MsgService.
	 */
	@Test(timeout=TestingConstants.UNITTESTTIME,expected=IllegalArgumentException.class)
	public void testCreateAndAssignAfterwards() throws Exception {
		Project project = pms.createProject(tmpdir.getName(), tmpdir.getAbsolutePath(),
				null);
		Assert.assertNull(project.getMessageService());

		ServiceCredentials cred = new ServiceCredentials(id, password);
		cred.setProtocol(ProtocolType.XMPP);
		MsgService msg = frontend.addAccount(sessionId, cred);

		project.setMessageService(msg);

		Assert.assertNotNull(project.getMessageService());

		Assert.assertNotNull(project.getUserId());

		Assert.assertEquals(1, pms.getProjectUsers(project).size());
		Assert.assertEquals(project.getUserId().getUserId(), pms.getProjectUsers(
				project).get(0).getUserId());

		Assert.assertEquals(msg.getUserId(), project.getUserId());
	}

	@Test
	public void testCreateProjectWorkaround() throws Exception {
		ServiceCredentials cred = new ServiceCredentials(id, password);
		cred.setProtocol(ProtocolType.XMPP);
		MsgService msg = frontend.addAccount(sessionId, cred);

		Project project = pms.createProject(tmpdir.getName(), tmpdir.getAbsolutePath(),
				msg);

		Assert.assertNotNull(project.getMessageService());
		Assert.assertNotNull(project.getUserId());

		Assert.assertEquals(1, pms.getProjectUsers(project).size());
		Assert.assertEquals(project.getUserId().getUserId(), pms.getProjectUsers(
				project).get(0).getUserId());

		Assert.assertEquals(msg.getUserId(), project.getUserId());
	}
	

	@Test
	public void testProjectRoundtrip() throws Exception {
		ServiceCredentials cred = new ServiceCredentials(id, password);
		cred.setProtocol(ProtocolType.XMPP);
		MsgService msg = frontend.addAccount(sessionId, cred);
		
		Assert.assertNotNull(msg);
		Assert.assertEquals(cred,msg.getServiceCredentials());

		Assert.assertEquals(0, pms.getProjectList().size());
		
		Project project = pms.createProject(tmpdir.getName(), tmpdir.getAbsolutePath(),
				msg);
		Assert.assertNotNull(project.getMessageService());
		Assert.assertNotNull(project.getUserId());

		Assert.assertEquals(1, pms.getProjectUsers(project).size());
		Assert.assertEquals(project.getUserId().getUserId(), pms.getProjectUsers(
				project).get(0).getUserId());

		Assert.assertEquals(msg.getUserId(), project.getUserId());
		Assert.assertEquals(1, pms.getProjectList().size());

		Assert.assertEquals(project, pms.openProject(project));
		Assert.assertTrue(pms.getProjectList().contains(project));
		
		pms.closeProject(project);
		Assert.assertFalse(pms.getProjectList().contains(project));
		
		Assert.assertEquals(project, pms.openProject(project));
		Assert.assertTrue(pms.getProjectList().contains(project));
		
		Assert.assertTrue(pms.deleteProject(project, true));
		Assert.assertFalse(pms.getProjectList().contains(project));
	}	
}
