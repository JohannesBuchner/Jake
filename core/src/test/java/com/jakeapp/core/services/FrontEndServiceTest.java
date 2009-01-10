package com.jakeapp.core.services;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jakeapp.TestingConstants;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.domain.exceptions.NotLoggedInException;


public class FrontEndServiceTest {
	private static final String SESSIONID1 = "paula";
	private static final String SESSIONID2 = "brillant";
	
	private static Map<String,String> VALID_CREDENTIALS;
	
	private IFrontendService service;

	/**
	 * @return the service
	 */
	private IFrontendService getService() {
		return service;
	}

	/**
	 * @param service the service to set
	 */
	private void setService(IFrontendService service) {
		this.service = service;
	}

	@BeforeClass
	public static void setUpClass() {
		VALID_CREDENTIALS = new HashMap<String,String>();
		//TODO find out valid credentials
		VALID_CREDENTIALS.put("to", "do");
	}
	
	@Before
	public void setUp() throws Exception {

		this.setService(new FrontendServiceImpl(
                 null // IProjectsManagingService
                ,null
                )
        );
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test(timeout = TestingConstants.UNITTESTTIME)
	public void authenticate_shouldNOTFailWithEmptyCredentials() throws IllegalArgumentException, InvalidCredentialsException {
		this.getService().authenticate(new HashMap<String,String>());
	}

	@Test(timeout = TestingConstants.UNITTESTTIME, expected = NotLoggedInException.class)
	public void logout_shouldFailForIllegalSession() throws IllegalArgumentException, NotLoggedInException {
		this.getService().logout(SESSIONID2);
	}
	
	@Test(timeout = TestingConstants.UNITTESTTIME, expected = NotLoggedInException.class)
	public void logout_shouldNotFailSecondTime() throws IllegalArgumentException, NotLoggedInException, InvalidCredentialsException {
		String sessionid = this.getService().authenticate(VALID_CREDENTIALS);
		this.getService().logout(sessionid);
		
		//CALL
		this.getService().logout(sessionid);
		//CALL
	}
	
	@Test(timeout = TestingConstants.UNITTESTTIME)
	public void logout_shouldSucceed() throws IllegalArgumentException, NotLoggedInException, InvalidCredentialsException {
		String sessionid = this.getService().authenticate(VALID_CREDENTIALS);
		
		//CALL
		this.getService().logout(sessionid);
		//CALL
	}

/*	@Test(timeout = TestingConstants.UNITTESTTIME,expected = NotLoggedInException.class)
	public void ping_shouldFailWithNoLogin() throws IllegalArgumentException, NotLoggedInException {
		this.getService().ping(SESSIONID1);
	}*/
}
