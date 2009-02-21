package com.jakeapp.core.services;

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

	
	// this fails == bug 32
	@Test
	public void testCreateProject() throws Exception {
		ServiceCredentials cred = new ServiceCredentials(id, password);
		cred.setProtocol(ProtocolType.XMPP);
		MsgService msg = frontend.addAccount(sessionId, cred);

		Project project = pms.createProject(tmpdir.getName(), tmpdir.getAbsolutePath(),
				msg);
		Assert.assertNotNull(project.getMessageService());
		//Assert.assertNull(project.getUserId());

		Assert.assertNotNull(project.getUserId());

		Assert.assertEquals(1, pms.getProjectUsers(project).size());
		Assert.assertEquals(project.getUserId().getUserId(), pms.getProjectUsers(
				project).get(0).getUser().getUserId());

		Assert.assertEquals(msg.getUserId(), project.getUserId());
	}

	// this fails == bug 33
	@Test
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
				project).get(0).getUser().getUserId());

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
				project).get(0).getUser().getUserId());

		Assert.assertEquals(msg.getUserId(), project.getUserId());
	}
}
