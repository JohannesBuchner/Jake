package com.jakeapp.jake.ics.impl.sockets.filetransfer;


public enum NegotiationState {
	/**
	 * Client requests a file
	 */
	request("request"),
	/**
	 * server responds with available ips
	 */
	serverips("serverips"),
	/**
	 * client responds with available ips
	 */
	clientips("clientips"),
	/**
	 * server responds with list of successful connections
	 */
	serverresults("serverresults"),
	/**
	 * client chooses which to choose
	 */
	clientdecision("clientdecision");

	private String status;

	NegotiationState(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return this.status;
	}
}
