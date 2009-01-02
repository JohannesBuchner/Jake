package com.jakeapp.jake.ics.impl.sockets.filetransfer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.filetransfer.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.IncomingTransferListener;
import com.jakeapp.jake.ics.filetransfer.methods.ITransferMethod;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;
import com.jakeapp.jake.ics.msgservice.IMessageReceiveListener;
import com.jakeapp.jake.ics.msgservice.IMsgService;

public class SimpleSocketFileTransferMethod implements ITransferMethod,
		IMessageReceiveListener {

	private static Logger log = Logger.getLogger(SimpleSocketFileTransferMethod.class);

	private static final String ADDRESS_REQUEST = "address?";

	private static final String ADDRESS_RESPONSE = "address!";

	private static final int BLOCKSIZE = 1024;

	private IMsgService negotiationService;

	private Map<UserId, InetSocketAddress> servers = new HashMap<UserId, InetSocketAddress>();

	private Map<FileRequest, INegotiationSuccessListener> listeners = new HashMap<FileRequest, INegotiationSuccessListener>();

	private Queue<FileRequest> started = new LinkedBlockingQueue<FileRequest>();

	private ServerSocket socket;

	private FileRequestFileMapper mapper;

	private UserId user;

	public SimpleSocketFileTransferMethod(IMsgService negotiationService,
			UserId user) throws NotLoggedInException {
		this.user = user;
		this.negotiationService = negotiationService;
		this.negotiationService.registerReceiveMessageListener(this);
	}

	@Override
	public void request(FileRequest r, INegotiationSuccessListener nsl) {
		log.debug(user + ": requesting " + r);
		String offer = SimpleSocketFileTransferFactory.START + ADDRESS_REQUEST
				+ SimpleSocketFileTransferFactory.END;
		try {
			this.listeners.put(r, nsl);
			this.started.add(r);
			this.negotiationService.sendMessage(r.getPeer(), offer);
		} catch (Exception e) {
			SimpleSocketFileTransferFactory.log.info("negotiation failed", e);
			nsl.failed();
			this.started.remove(r);
			return;
		}
	}

	@Override
	public void receivedMessage(UserId from_userid, String content) {
		// TODO: security issue: take only requests from friends
		if (!content.startsWith(SimpleSocketFileTransferFactory.START)
				|| !content.endsWith(SimpleSocketFileTransferFactory.END))
			return;
		String inner = content.substring(SimpleSocketFileTransferFactory.START
				.length(), content.length()
				- SimpleSocketFileTransferFactory.END.length());
		log
				.debug(user + ": receivedMessage : " + from_userid + " : "
						+ content);
		log.debug(user + " : " + this.started.size() + " : " + this.started);
		if (inner.equals(ADDRESS_REQUEST)) {
			InetSocketAddress add = provideAddresses();
			log.debug(user + " : " + add);
			if (add != null) {
				String response = SimpleSocketFileTransferFactory.START
						+ ADDRESS_RESPONSE + add.toString()
						+ SimpleSocketFileTransferFactory.END;
				try {
					this.negotiationService.sendMessage(from_userid, response);
				} catch (Exception e) {
					SimpleSocketFileTransferFactory.log.error("sending failed",
							e);
				}
			}
		} else if (inner.startsWith(ADDRESS_RESPONSE)) {
			String[] add = inner.substring(ADDRESS_RESPONSE.length()).split(
					":", 2);

			InetSocketAddress other = new InetSocketAddress(add[0], Integer
					.parseInt(add[1]));

			this.servers.put(from_userid, other);
			for (FileRequest r : this.started) {
				if (r.getPeer().equals(from_userid)) {
					INegotiationSuccessListener nsl = this.listeners.get(r);

					try {
						SimpleSocketFileTransfer ft = new SimpleSocketFileTransfer(
								r, other);
						new Thread(ft).start();
						nsl.succeeded(ft);
					} catch (IOException e) {
						nsl.failed();
					}
					this.started.remove(r);
				}
			}

		}
	}

	@Override
	public void startServing(IncomingTransferListener l,
			FileRequestFileMapper mapper) throws NotLoggedInException {
		log.debug(this.user + ": startServing");

		this.mapper = mapper;
		try {
			this.socket = new ServerSocket(SimpleSocketFileTransferFactory.PORT);
		} catch (IOException e) {
			SimpleSocketFileTransferFactory.log.error(e);
			throw new NotLoggedInException();
		}
		new Thread(new ServingThread(this.socket, l)).start();
	}

	public InetSocketAddress provideAddresses() {
		Enumeration<NetworkInterface> ifaces;
		try {
			ifaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			log.error(e);
			return null;
		}
		while (ifaces.hasMoreElements()) {
			NetworkInterface iface = ifaces.nextElement();
			Enumeration<InetAddress> iaddresses = iface.getInetAddresses();
			while (iaddresses.hasMoreElements()) {
				InetAddress iaddress = iaddresses.nextElement();
				if (!iaddress.isLoopbackAddress()
						&& !iaddress.isLinkLocalAddress()) {
					// host candidate
					return new InetSocketAddress(iaddress.getHostAddress(),
							socket.getLocalPort());
				}
			}
		}
		return null;
	}

	public class ServingThread implements Runnable {

		private ServerSocket socket;

		private IncomingTransferListener listener;

		private FileRequest fr;

		public ServingThread(ServerSocket serverSocket,
				IncomingTransferListener l) {
			this.socket = serverSocket;
			this.listener = l;
		}

		public void run() {
			try {
				while (!SimpleSocketFileTransferMethod.this.socket.isClosed()) {
					Socket client = this.socket.accept();
					handleClient(client);
				};
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private void handleClient(Socket client) {
			try {
				InputStream is;
				is = client.getInputStream();

				StringBuilder sb = new StringBuilder();
				while (true) {
					int i;
					i = is.read();
					if (i == '|') {
						String request = sb.toString();
						String s = SimpleSocketFileTransfer.FILE_REQUEST;
						if (request.startsWith(s)) {
							FileRequest fr = new FileRequest(request
									.substring(s.length()), false, null);
							if (this.listener.accept(fr)) {
								new ClientHandler(client, fr, this.listener,
										user);
							}
						}
						break;
					} else if (i == -1) {
						break;
					} else {
						char b = (char) i;
						sb.append(b);
					}
				}
			} catch (IOException e) {
				log.debug("serving failed", e);
			}
			try {
				client.close();
			} catch (IOException e) {
				log.debug("closing failed", e);
			}
			return;
		}
	}

	public class ClientHandler extends FileTransfer implements IFileTransfer {

		private Socket client;

		public ClientHandler(Socket client, FileRequest request,
				IncomingTransferListener listener, UserId user) {

			this.client = client;
			this.request = request;
			this.peer = user;

			status = Status.negotiated;
			listener.started(this);
			localFile = SimpleSocketFileTransferMethod.this.mapper
					.getFileForRequest(request);

			InputStream source;
			try {
				source = new FileInputStream(localFile);
				status = Status.in_progress;
				if (source != null) {
					byte[] b = new byte[BLOCKSIZE];
					while (true && status == Status.in_progress) {
						int len = source.read(b);
						if (len == -1)
							break;
						amountWritten += len;
						client.getOutputStream().write(b, 0, len);
					}
					return;
				}
			} catch (FileNotFoundException e) {
				setError(e);
			} catch (IOException e) {
				setError(e);
			}
		}

		@Override
		public void cancel() {
			super.cancel();
			try {
				socket.close();
			} catch (IOException e) {
				log.error(e);
			}
		}

		@Override
		public Boolean isReceiving() {
			return false;
		}
	}
}