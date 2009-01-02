package com.jakeapp.jake.ics.impl.sockets.filetransfer;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Use InetSocketAddress
 * @author johannes
 *
 */
@Deprecated
public class TcpAddress {
	private InetAddress ip;
	private int port;
	
	public InetAddress getIp() {
		return this.ip;
	}
	
	public int getPort() {
		return this.port;
	}

	public TcpAddress(InetAddress ip, int port) {
		super();
		this.ip = ip;
		this.port = port;
	}

	public TcpAddress(String value) throws UnknownHostException, NumberFormatException {
		String[] add = value.split(":", 2);
		
		this.ip = InetAddress.getByName(add[0]);
		this.port = Integer.parseInt(add[1]);
	}
}
