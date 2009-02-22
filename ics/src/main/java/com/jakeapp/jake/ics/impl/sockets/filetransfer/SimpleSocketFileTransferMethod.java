package com.jakeapp.jake.ics.impl.sockets.filetransfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.exceptions.OtherUserOfflineException;
import com.jakeapp.jake.ics.exceptions.TimeoutException;
import com.jakeapp.jake.ics.filetransfer.AdditionalFileTransferData;
import com.jakeapp.jake.ics.filetransfer.FileRequestFileMapper;
import com.jakeapp.jake.ics.filetransfer.IncomingTransferListener;
import com.jakeapp.jake.ics.filetransfer.exceptions.OtherUserDoesntHaveRequestedContentException;
import com.jakeapp.jake.ics.filetransfer.methods.ITransferMethod;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;
import com.jakeapp.jake.ics.msgservice.IMessageReceiveListener;
import com.jakeapp.jake.ics.msgservice.IMsgService;

// TODO: timeouts for negotiations
public class SimpleSocketFileTransferMethod implements ITransferMethod,
		IMessageReceiveListener {

	private static final Logger log = Logger
			.getLogger(SimpleSocketFileTransferMethod.class);

	private static final String ADDRESS_REQUEST = "<addressrequest/>";

	private static final String ADDRESS_RESPONSE = "<addressresponse/>";

	private static final int BLOCKSIZE = 1024;

	private static final String GOT_REQUESTED_FILE = "<file/>";

	private IMsgService negotiationService;

	private Map<UserId, InetSocketAddress> serverAdresses = new HashMap<UserId, InetSocketAddress>();

	private Map<FileRequest, INegotiationSuccessListener> listeners = new HashMap<FileRequest, INegotiationSuccessListener>();

	private Queue<FileRequest> outgoingRequests = new LinkedBlockingQueue<FileRequest>();

	private Map<UUID, FileRequest> incomingRequests = new HashMap<UUID, FileRequest>();

	private Map<FileRequest, Long> requestAge = new HashMap<FileRequest, Long>();

	private ServerSocket server;

	private FileRequestFileMapper mapper;

	private UserId myUserId;

	private IncomingTransferListener incomingTransferListener;

	private final int maximalRequestAgeSeconds;

	private final int port;

	private static final int UUID_LENGTH = UUID.randomUUID().toString().length();

	public SimpleSocketFileTransferMethod(int maximalRequestAgeSeconds, int port,
			IMsgService negotiationService, UserId user) {
		log.debug("creating SimpleSocketFileTransferMethod for user " + user);
		this.maximalRequestAgeSeconds = maximalRequestAgeSeconds;
		this.port = port;
		this.myUserId = user;
		this.negotiationService = negotiationService;
		this.negotiationService.registerReceiveMessageListener(this);

		startTimeoutTimer();
	}

	private void startTimeoutTimer() {
		Timer timer = new Timer(true);
		timer.schedule(new TimerTask() {

			private final Logger log = Logger
					.getLogger(SimpleSocketFileTransferMethod.class);

			@Override
			public void run() {
				long now = new Date().getTime();

				for (FileRequest key : SimpleSocketFileTransferMethod.this.requestAge
						.keySet()) {
					long starttime = SimpleSocketFileTransferMethod.this.requestAge
							.get(key);
					long age = (now - starttime) / 1000;
					this.log.debug("checking timeout: " + key + " age: " + age);
					if (age > SimpleSocketFileTransferMethod.this.maximalRequestAgeSeconds) {
						this.log.debug("removing old request");
						synchronized (SimpleSocketFileTransferMethod.this.requestAge) {
							SimpleSocketFileTransferMethod.this.requestAge.remove(key);
							SimpleSocketFileTransferMethod.this.outgoingRequests
									.remove(key);
							SimpleSocketFileTransferMethod.this.listeners.remove(key)
									.failed(new TimeoutException());
						}
					}
				}
			}
		}, 0, this.maximalRequestAgeSeconds * 1000 / 2);
	}

	/*
	 * first step, client requests something
	 */
	@Override
	public void request(FileRequest r, INegotiationSuccessListener nsl) {
		log.debug(myUserId + ": We request " + r);
		String request = SimpleSocketFileTransferFactory.START + ADDRESS_REQUEST
				+ r.getFileName() + SimpleSocketFileTransferFactory.END;

		synchronized (this.requestAge) {
			this.listeners.put(r, nsl);
			this.outgoingRequests.add(r);
			this.requestAge.put(r, new Date().getTime());
		}

		log.debug("requests I have to " + r.getPeer() + " : "
				+ this.outgoingRequests.size() + " : " + this.outgoingRequests);

		try {
			this.negotiationService.sendMessage(r.getPeer(), request);
		} catch (Exception e) {
			SimpleSocketFileTransferFactory.log.info("negotiation failed", e);
			nsl.failed(e);
			removeOutgoing(r);
			return;
		}
	}

	@Override
	public void receivedMessage(UserId from_userid, String content) {
		if (!content.startsWith(SimpleSocketFileTransferFactory.START)
				|| !content.endsWith(SimpleSocketFileTransferFactory.END))
			return;

		String inner = content.substring(SimpleSocketFileTransferFactory.START.length(),
				content.length() - SimpleSocketFileTransferFactory.END.length());

		log.debug(myUserId + ": receivedMessage : " + from_userid + " : " + inner);

		if (inner.startsWith(ADDRESS_REQUEST)) {
			handleAddressRequest(from_userid, inner);
			/*
			 * ADDRESS_RESPONSE:
			 * [GOT_REQUESTED_FILE<uuid><filename>]ADDRESS_RESPONSE[<address>]
			 */
		} else if (inner.equals(ADDRESS_RESPONSE)) {
			/*
			 * third step, client receives no ok from server
			 */
			// we are the client, server doesn't have it
			for (FileRequest r : getRequestsForUser(outgoingRequests, from_userid)) {
				INegotiationSuccessListener nsl = this.listeners.get(r);
				nsl.failed(new OtherUserDoesntHaveRequestedContentException());
				removeOutgoing(r);
			}
		} else if (inner.startsWith(GOT_REQUESTED_FILE)) {
			/*
			 * third step, client receives ok from server
			 */
			handleServerOk(from_userid, inner);
			return;
		} else {
			log.warn("unknown request from " + from_userid + ": " + content);
		}
	}

	private void handleServerOk(UserId from_userid, String inner) {
		// we are the client
		try {
			String innerWithoutType = inner.substring(GOT_REQUESTED_FILE.length());
			UUID transferKey = UUID
					.fromString(innerWithoutType.substring(0, UUID_LENGTH));
			log.debug(myUserId + ": I got the transferKey " + transferKey);

			String innerWithoutTransferKey = innerWithoutType.substring(UUID_LENGTH);

			int pos = innerWithoutTransferKey.indexOf(ADDRESS_RESPONSE);

			String filename = innerWithoutTransferKey.substring(0, pos);

			String address = innerWithoutTransferKey.substring(pos
					+ ADDRESS_RESPONSE.length());

			FileRequest fr = new FileRequest(filename, false, from_userid);

			log.debug(myUserId + ": Do I have the request to " + from_userid + "? "
					+ outgoingRequests.contains(fr) + " : " + fr);

			if (!outgoingRequests.contains(fr)) {
				log.warn(from_userid
						+ " violated the protocol (sent offer without request)");
				return;
			}

			try {
				if (!address.contains(":")) {
					throw new OtherUserOfflineException();
				}
				String[] add = address.split(":", 2);
				if (add[0].charAt(0) == '/')
					add[0] = add[0].substring(1);
				log.debug("setting ip address ...");
				InetSocketAddress other = new InetSocketAddress(add[0], Integer
						.parseInt(add[1]));
				log.debug("setting ip address :" + other);

				this.serverAdresses.put(from_userid, other);
				for (FileRequest r : getRequestsForUser(outgoingRequests, from_userid)) {
					INegotiationSuccessListener nsl = this.listeners.get(r);

					/*
					 * fourth step, the client starts the out-of-band transfer
					 */
					SimpleSocketFileTransfer ft = new SimpleSocketFileTransfer(r, other,
							transferKey, maximalRequestAgeSeconds);
					new Thread(ft).start();
					log.info("negotiation with " + from_userid + " succeeded");
					nsl.succeeded(ft);
					removeOutgoing(r);
				}
			} catch (Exception e) {
				log.info("negotiation with " + from_userid + " failed: ", e);
				for (FileRequest r : getRequestsForUser(outgoingRequests, from_userid)) {
					INegotiationSuccessListener nsl = this.listeners.get(r);
					nsl.failed(e);
					removeOutgoing(r);
				}
			}
		} catch (IndexOutOfBoundsException e) {
			log.warn(from_userid
					+ " packet came not as [GOT_REQUESTED_FILE<uuid><filename>]"
					+ "ADDRESS_RESPONSE[<address>]");
			return;
		} catch (IllegalArgumentException e) {
			log.warn(from_userid + " violated the protocol (sent invalid transferKey)");
			return;
		}
		log.debug("done with " + ADDRESS_RESPONSE + " from " + from_userid);
	}

	private void handleAddressRequest(UserId from_userid, String inner) {
		/*
		 * second step, server receives request
		 */
		// we are the server
		String filename = inner.substring(ADDRESS_REQUEST.length());
		FileRequest fr = new FileRequest(filename, true, from_userid);

		InetSocketAddress add = provideAddresses();
		log.debug(myUserId.getUserId() + " : " + from_userid + " wants our ip, " + add
				+ " and wants  " + filename);
		log.debug("Do we serve at all? ");
		String response = SimpleSocketFileTransferFactory.START;
		boolean success = true;
		if (add == null) {
			success = false;
		} else {
			log.debug("Do we accept?");
			if (!incomingTransferListener.accept(fr)) {
				success = false;
			} else {
				log.debug("Do we have the file?");

				File localFile = mapper.getFileForRequest(fr);

				if (localFile != null) {
					response += GOT_REQUESTED_FILE + registerTransferKey(localFile, fr)
							+ filename;
					response += ADDRESS_RESPONSE + add.toString();
				} else {
					success = false;
				}
			}
		}
		if (!success) {
			response += ADDRESS_RESPONSE;
			log.debug("Not answering with a positive response");
		}
		response += SimpleSocketFileTransferFactory.END;

		try {
			this.negotiationService.sendMessage(from_userid, response);
		} catch (Exception e) {
			SimpleSocketFileTransferFactory.log.warn("sending failed", e);
		}
	}

	private UUID registerTransferKey(File localFile, FileRequest fr) {
		UUID u = UUID.randomUUID();
		fr.setData(new AdditionalFileTransferData(localFile));
		this.incomingRequests.put(u, fr);
		return u;
	}

	private List<FileRequest> getRequestsForUser(Queue<FileRequest> frq, UserId userid) {
		List<FileRequest> rq = new LinkedList<FileRequest>();
		for (FileRequest r : frq) {
			if (r.getPeer().equals(userid)) {
				rq.add(r);
			}
		}
		return rq;
	}

	private void removeOutgoing(FileRequest r) {
		log.debug("I'm done with outgoing request " + r + " (one way or the other)");
		synchronized (this.requestAge) {
			this.outgoingRequests.remove(r);
			this.listeners.remove(r);
			this.requestAge.remove(r);
		}
	}

	public boolean isServing() {
		return this.incomingTransferListener != null;
	}

	@Override
	public void startServing(IncomingTransferListener l, FileRequestFileMapper mapper)
			throws NotLoggedInException {
		log.debug(this.myUserId + ": startServing");

		this.incomingTransferListener = l;
		this.mapper = mapper;
		try {
			this.server = new ServerSocket(this.port);
		} catch (IOException e) {
			SimpleSocketFileTransferFactory.log.error(e);
			throw new NotLoggedInException();
		}
		new Thread(new ServingThread(this.server, l)).start();
	}

	@Override
	public void stopServing() {
		this.incomingTransferListener = null;
		if (this.server != null) {
			try {
				this.server.close();
				Thread.yield();
			} catch (IOException e) {
				log.warn("closing server socket failed", e);
			}
		}
	}

	public InetSocketAddress provideAddresses() {
		if (!isServing())
			return null;

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
				if (!iaddress.isLoopbackAddress() && !iaddress.isLinkLocalAddress()) {
					// host candidate
					return new InetSocketAddress(iaddress.getHostAddress(), server
							.getLocalPort());
				}
			}
		}
		log.error("No NetworkInterfaces found!");
		return null;
	}

	public class ServingThread implements Runnable {

		private final Logger log = Logger.getLogger(ServingThread.class);

		private ServerSocket socket;

		private IncomingTransferListener listener;

		public ServingThread(ServerSocket serverSocket, IncomingTransferListener l) {
			this.socket = serverSocket;
			this.listener = l;
		}

		public void run() {
			try {
				while (!SimpleSocketFileTransferMethod.this.server.isClosed()) {
					Socket client = this.socket.accept();
					this.log
							.debug("Incoming connection. Starting another ClientHandler thread.");
					new ClientHandler(client, this.listener).run();
				};
			} catch (IOException e) {
				if (!isServing()) {
					this.log.info("SocketServer shutted down");
				} else {
					stopServing();
					this.log.error("SocketServer quitting unexpectedly", e);
				}
			}
		}


	}

	public class ClientHandler extends FileTransfer implements Runnable {

		private Socket client;

		private final Logger log = Logger.getLogger(ClientHandler.class);

		private IncomingTransferListener listener;

		public ClientHandler(Socket client, IncomingTransferListener listener) {
			this.log.debug("ClientHandler for " + client + " created");
			this.client = client;
			this.listener = listener;
		}

		public void run() {
			InputStreamReader bis = null;
			try {
				bis = new InputStreamReader(this.client.getInputStream());

				char[] uuidContent = new char[UUID_LENGTH];
				this.log.debug("reading UUID");
				bis.read(uuidContent, 0, UUID_LENGTH);
				UUID requestKey = UUID.fromString(new String(uuidContent));
				this.log.debug("requestKey: " + requestKey);

				FileRequest fr = SimpleSocketFileTransferMethod.this.incomingRequests
						.get(requestKey);
				if (fr != null) {
					this.log.debug("request: " + fr + " accepted");
					sendContent(fr, this.listener);
					// prevent replay attacks
					SimpleSocketFileTransferMethod.this.incomingRequests
							.remove(requestKey);
				} else {
					log.warn("got invalid/unknown requestKey");
				}
			} catch (IOException e) {
				log.warn("serving client failed", e);
			} finally {
				try {
					log.debug("closing client connection");
					if (bis != null)
						bis.close();
					this.client.close();
				} catch (IOException e) {
					log.error("closing failed", e);
				}
			}
		}

		public void sendContent(FileRequest request, IncomingTransferListener listener) {

			this.request = request;

			this.status = Status.negotiated;
			listener.started(this);
			this.localFile = request.getData().getDataFile();

			log.debug("starting content");
			InputStream source;
			try {
				source = new FileInputStream(this.localFile);
				OutputStream out = this.client.getOutputStream();
				this.status = Status.in_progress;
				if (source != null) {
					byte[] b = new byte[BLOCKSIZE];
					while (this.status == Status.in_progress) {
						log.debug("sending content ... ");
						int len = source.read(b);
						if (len == -1)
							break;
						this.amountWritten += len;
						out.write(b, 0, len);
					}
					out.flush();
					out.close();
					log.debug("sending content done:" + amountWritten + " bytes written");
				} else {
					setError(new FileNotFoundException());
				}
			} catch (FileNotFoundException e) {
				setError(e);
			} catch (IOException e) {
				setError(e);
			}
			if (this.status != Status.cancelled) {
				log.debug("setting myself complete");
				this.status = Status.complete;
			}
		}

		@Override
		public void cancel() {
			super.cancel();
			try {
				SimpleSocketFileTransferMethod.this.server.close();
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