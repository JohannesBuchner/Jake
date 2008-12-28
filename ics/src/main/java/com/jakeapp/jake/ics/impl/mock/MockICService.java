package com.jakeapp.jake.ics.impl.mock;

import com.jakeapp.jake.ics.ICService;


public class MockICService extends ICService {

	public MockICService() {
		this.fileTransferService = null; // not implemented
		/*
		 * This implements both, usually you will want to share common data
		 * using the constructors
		 */
		MockMsgAndStatusService service = new MockMsgAndStatusService();
		this.msgService = service;
		this.statusService = service;
	}

	@Override
	public String getServiceName() {
		return "Mock";
	}
}
