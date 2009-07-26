package com.jakeapp.core.services;

import com.jakeapp.TestingConstants;
import com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;


public class FrontEndServiceTest {
	private static final String SESSIONID1 = "paula";
	private static final String SESSIONID2 = "brillant";

	private static Map<String, String> VALID_CREDENTIALS;

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
		VALID_CREDENTIALS = new HashMap<String, String>();
		//valid credentials == empty hashmap
	}

	@Before
	public void setUp() throws Exception {

		this.setService(new FrontendServiceImpl(null, null, null, null)
		);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test(timeout = TestingConstants.UNITTESTTIME)
	public void authenticate_shouldNOTFailWithEmptyCredentials() throws IllegalArgumentException, InvalidCredentialsException {
		this.getService().authenticate(new HashMap<String, String>(), null);
	}

	@Test(timeout = TestingConstants.UNITTESTTIME, expected = FrontendNotLoggedInException.class)
	public void logout_shouldFailForIllegalSession() throws IllegalArgumentException, FrontendNotLoggedInException {
		this.getService().logout(SESSIONID2);
	}

	@Test(timeout = TestingConstants.UNITTESTTIME, expected = FrontendNotLoggedInException.class)
	public void logout_shouldNotFailSecondTime() throws IllegalArgumentException, FrontendNotLoggedInException, InvalidCredentialsException {
		String sessionid = this.getService().authenticate(VALID_CREDENTIALS, null);
		this.getService().logout(sessionid);

		//CALL
		this.getService().logout(sessionid);
		//CALL
	}

	@Test(timeout = TestingConstants.UNITTESTTIME)
	public void logout_shouldSucceed() throws IllegalArgumentException, FrontendNotLoggedInException, InvalidCredentialsException {
		String sessionid = this.getService().authenticate(VALID_CREDENTIALS, null);

		//CALL
		this.getService().logout(sessionid);
		//CALL
	}

/*	@Test(timeout = TestingConstants.UNITTESTTIME,expected = FrontendNotLoggedInException.class)
	public void ping_shouldFailWithNoLogin() throws IllegalArgumentException, FrontendNotLoggedInException {
		this.getService().ping(SESSIONID1);
	}*/
}
