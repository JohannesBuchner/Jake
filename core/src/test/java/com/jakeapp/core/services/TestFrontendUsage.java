package com.jakeapp.core.services;

import java.io.File;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProtocolType;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.services.IFrontendService;
import com.jakeapp.core.services.IProjectsManagingService;
import com.jakeapp.core.services.MsgService;
import com.jakeapp.jake.test.FSTestCommons;
import com.jakeapp.jake.test.TmpdirEnabledTestCase;


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

		Assert.assertEquals(1, pms.getProjectMembers(project).size());
		Assert.assertEquals(project.getUserId().getUserId(), pms.getProjectMembers(
				project).get(0).getUserId());

		Assert.assertEquals(msg.getUserId(), project.getUserId());
	}

	@Test(expected = IllegalAccessException.class)
	public void testCreateProjectAndUnnecessaryAssign() throws Exception {
		ServiceCredentials cred = new ServiceCredentials(id, password);
		cred.setProtocol(ProtocolType.XMPP);
		MsgService msg = frontend.addAccount(sessionId, cred);

		Project project = pms.createProject(tmpdir.getName(), tmpdir.getAbsolutePath(),
				msg);

		pms.assignUserToProject(project, msg.getUserId());
	}

	@Test
	public void testCreateAndAssignAfterwards() throws Exception {
		Project project = pms.createProject(tmpdir.getName(), tmpdir.getAbsolutePath(),
				null);
		Assert.assertNull(project.getMessageService());
		// Assert.assertNull(project.getUserId().getUuid());

		ServiceCredentials cred = new ServiceCredentials(id, password);
		cred.setProtocol(ProtocolType.XMPP);
		MsgService msg = frontend.addAccount(sessionId, cred);

		pms.assignUserToProject(project, msg.getUserId());

		Assert.assertNotNull(project.getMessageService());

		Assert.assertNotNull(project.getUserId());

		Assert.assertEquals(1, pms.getProjectMembers(project).size());
		Assert.assertEquals(project.getUserId().getUserId(), pms.getProjectMembers(
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
		try {
			pms.assignUserToProject(project, msg.getUserId());
		} catch (IllegalAccessException e) {
			// we ignore that, just like the gui does
		}

		Assert.assertNotNull(project.getMessageService());
		Assert.assertNotNull(project.getUserId());

		Assert.assertEquals(1, pms.getProjectMembers(project).size());
		Assert.assertEquals(project.getUserId().getUuid(), pms.getProjectMembers(
				project).get(0).getUserId());

		Assert.assertEquals(msg.getUserId(), project.getUserId());
	}
}
