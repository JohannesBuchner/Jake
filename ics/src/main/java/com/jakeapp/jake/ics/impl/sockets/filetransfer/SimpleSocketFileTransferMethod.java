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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.exceptions.OtherUserOfflineException;
import com.jakeapp.jake.ics.filetransfer.AdditionalFileTransferData;
import com.jakeapp.jake.ics.filetransfer.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.IncomingTransferListener;
import com.jakeapp.jake.ics.filetransfer.exceptions.OtherUserDoesntHaveRequestedContentException;
import com.jakeapp.jake.ics.filetransfer.methods.ITransferMethod;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;
import com.jakeapp.jake.ics.msgservice.IMessageReceiveListener;
import com.jakeapp.jake.ics.msgservice.IMsgService;

// TODO: timeouts for negotiations
public class SimpleSocketFileTransferMethod implements ITransferMethod,
		IMessageReceiveListener {

	private static Logger log = Logger.getLogger(SimpleSocketFileTransferMethod.class);

	private static final String ADDRESS_REQUEST = "<addressrequest/>";

	private static final String ADDRESS_RESPONSE = "<addressresponse/>";

	private static final int BLOCKSIZE = 1024;

	private static final String GOT_REQUESTED_FILE = "<file/>";

	private IMsgService negotiationService;

	private Map<UserId, InetSocketAddress> serverAdresses = new HashMap<UserId, InetSocketAddress>();

	private Map<FileRequest, INegotiationSuccessListener> listeners = new HashMap<FileRequest, INegotiationSuccessListener>();

	private Queue<FileRequest> outgoingRequests = new LinkedBlockingQueue<FileRequest>();

	private Map<UUID, FileRequest> incomingRequests = new HashMap<UUID, FileRequest>();

	private ServerSocket socket;

	private FileRequestFileMapper mapper;

	private UserId myUserId;

	private static final int UUID_LENGTH = UUID.randomUUID().toString().length();

	public SimpleSocketFileTransferMethod(IMsgService negotiationService, UserId user)
			throws NotLoggedInException {
		log.debug("creating SimpleSocketFileTransferMethod for user " + user);
		this.myUserId = user;
		this.negotiationService = negotiationService;
		this.negotiationService.registerReceiveMessageListener(this);
	}

	@Override
	public void request(FileRequest r, INegotiationSuccessListener nsl) {
		log.debug(myUserId + ": We request " + r);
		String request = SimpleSocketFileTransferFactory.START + ADDRESS_REQUEST
				+ r.getFileName() + SimpleSocketFileTransferFactory.END;

		this.listeners.put(r, nsl);
		this.outgoingRequests.add(r);

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
			// we are the server
			String filename = inner.substring(ADDRESS_REQUEST.length());
			FileRequest fr = new FileRequest(filename, true, from_userid);
			File localFile = mapper.getFileForRequest(fr);

			InetSocketAddress add = provideAddresses();
			log.debug(myUserId.getUserId() + " : " + from_userid + " wants our ip, "
					+ add);
			log.debug(myUserId.getUserId() + " : " + from_userid + " wants  " + filename
					+ " which is our file, " + localFile);
			String response = SimpleSocketFileTransferFactory.START;
			if (add != null) {
				if (localFile != null) {
					response += GOT_REQUESTED_FILE + registerTransferKey(localFile, fr)
							+ filename;
				}
				response += ADDRESS_RESPONSE + add.toString();
			} else {
				response += ADDRESS_RESPONSE;
			}
			response += SimpleSocketFileTransferFactory.END;
			try {
				this.negotiationService.sendMessage(from_userid, response);
			} catch (Exception e) {
				SimpleSocketFileTransferFactory.log.warn("sending failed", e);
			}
			/*
			 * ADDRESS_RESPONSE:
			 * [GOT_REQUESTED_FILE<uuid><filename>]ADDRESS_RESPONSE[<address>]
			 */
		} else if (inner.equals(ADDRESS_RESPONSE)) {
			// we are the client, server doesn't have it
			for (FileRequest r : getRequestsForUser(outgoingRequests, from_userid)) {
				INegotiationSuccessListener nsl = this.listeners.get(r);
				nsl.failed(new OtherUserDoesntHaveRequestedContentException());
				removeOutgoing(r);
			}
		} else if (inner.startsWith(GOT_REQUESTED_FILE)) {
			// we are the client
			try {
				String innerWithoutType = inner.substring(GOT_REQUESTED_FILE.length());
				UUID transferKey = UUID.fromString(innerWithoutType.substring(0,
						UUID_LENGTH));
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

						SimpleSocketFileTransfer ft = null;
						ft = new SimpleSocketFileTransfer(r, other, transferKey);
						new Thread(ft).start();
						log.info("negotiation with " + from_userid + " succeeded");
						nsl.succeeded(ft);
						removeOutgoing(r);
					}
				} catch (Exception e) {
					log.info("negotiation with " + from_userid + " failed: " + e);
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
				log.warn(from_userid
						+ " violated the protocol (sent invalid transferKey)");
				return;
			}
			log.debug("done with " + ADDRESS_RESPONSE + " from " + from_userid);
			return;
		} else {
			log.warn("unknown request from " + from_userid + ": " + content);
		}
	}

	private class LocalFile extends AdditionalFileTransferData {

		public LocalFile(File f) {
			this.setDataFile(f);
		}
	}

	private UUID registerTransferKey(File localFile, FileRequest fr) {
		UUID u = UUID.randomUUID();
		fr.setData(new LocalFile(localFile));
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
		this.outgoingRequests.remove(r);
		this.listeners.remove(r);
	}

	@Override
	public void startServing(IncomingTransferListener l, FileRequestFileMapper mapper)
			throws NotLoggedInException {
		log.debug(this.myUserId + ": startServing");

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
				if (!iaddress.isLoopbackAddress() && !iaddress.isLinkLocalAddress()) {
					// host candidate
					return new InetSocketAddress(iaddress.getHostAddress(), socket
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
				while (!SimpleSocketFileTransferMethod.this.socket.isClosed()) {
					Socket client = this.socket.accept();
					this.log
							.debug("Incoming connection. Starting another ClientHandler thread.");
					new Thread(new ClientHandler(client, this.listener)).run();
				};
			} catch (IOException e) {
				this.log.error("SocketServer quitting unexpectedly", e);
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
			try {
				char[] uuidContent = new char[UUID_LENGTH];
				InputStreamReader bis = new InputStreamReader(this.client
						.getInputStream());

				this.log.debug("reading UUID");
				bis.read(uuidContent, 0, UUID_LENGTH);
				UUID requestKey = UUID.fromString(new String(uuidContent));
				this.log.debug("requestKey: " + requestKey);

				FileRequest fr = SimpleSocketFileTransferMethod.this.incomingRequests
						.get(requestKey);
				if (fr != null && this.listener.accept(fr)) {
					this.log.debug("request: " + fr + " accepted");
					sendContent(fr, this.listener);
					// prevent replay attacks
					SimpleSocketFileTransferMethod.this.incomingRequests.remove(requestKey);
				} else if (fr == null) {
					log.warn("got invalid/unknown requestKey");
				} else {
					log.info("we denied the client");
				}
			} catch (IOException e) {
				log.warn("serving client failed", e);
			}
			try {
				log.debug("closing client connection");
				this.client.close();
			} catch (IOException e) {
				log.error("closing failed", e);
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
				SimpleSocketFileTransferMethod.this.socket.close();
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