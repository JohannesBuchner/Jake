package com.jakeapp.jake.ics.impl.xmpp.filetransfer;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.filetransfer.FileRequestFileMapper;
import com.jakeapp.jake.ics.filetransfer.IncomingTransferListener;
import com.jakeapp.jake.ics.filetransfer.exceptions.CommunicationProblemException;
import com.jakeapp.jake.ics.filetransfer.exceptions.OtherUserDoesntHaveRequestedContentException;
import com.jakeapp.jake.ics.filetransfer.methods.ITransferMethod;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;
import com.jakeapp.jake.ics.impl.xmpp.XmppConnectionData;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;
import com.jakeapp.jake.ics.msgservice.IMessageReceiveListener;
import com.jakeapp.jake.ics.msgservice.IMsgService;
import com.jakeapp.jake.ics.status.ILoginStateListener;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * {@link ITransferMethod} for Xmpp. Use {@link XmppFileTransferFactory} to
 * obtain.
 * 
 * @author johannes
 * 
 */
public class XmppFileTransferMethod implements ITransferMethod, IMessageReceiveListener,
				ILoginStateListener, IOutgoingRequestManager {


	private static Logger log = Logger.getLogger(XmppFileTransferMethod.class);

	private static final String FILE_REQUEST = "<filerequest/>";

	private static final Object FILE_RESPONSE_DONT_HAVE = "<fileresponseno/>";
	
	/**
	 * How long we wait for a response from a server after we have sent
	 * a request to this server, in milliseconds
	 */
	private static final long REQUEST_TIMEOUT = 5000;

	private IMsgService negotiationService;

	private Map<FileRequest, INegotiationSuccessListener> listeners = new HashMap<FileRequest, INegotiationSuccessListener>();

	private Queue<FileRequest> outgoingRequests = new LinkedBlockingQueue<FileRequest>();
	
	private Map<FileRequest, TimerTask> outgoingRequestTimeoutTasks = new HashMap<FileRequest, TimerTask>();

	private Timer timeoutTimer;

	private FileRequestFileMapper mapper;

	private UserId myUserId;

	private IncomingTransferListener incomingTransferListener;

	private XMPPConnection connection;

	private XmppConnectionData con;

	public XmppFileTransferMethod(XmppConnectionData con, IMsgService negotiationService,
			UserId user) {
		log.debug("creating XmppFileTransferMethod for user " + user);
		this.myUserId = user;
		this.con = con;
		this.connection = con.getConnection();
		this.negotiationService = negotiationService;
		this.negotiationService.registerReceiveMessageListener(this);
		this.con.getService().getStatusService().addLoginStateListener(this);
		this.setTimeoutTimer(new Timer());
		startReceiving();
	}
	
	private void setTimeoutTimer(Timer timeoutTimer) {
		this.timeoutTimer = timeoutTimer;
	}

	private Timer getTimeoutTimer() {
		return timeoutTimer;
	}

	/*
	 * first step, client requests something
	 */
	@Override
	public void request(FileRequest r, INegotiationSuccessListener nsl) {
		TimerTask timeoutTask;
		
		log.debug(myUserId + ": We request " + r);
		String request = XmppFileTransferFactory.START + FILE_REQUEST + r.getFileName()
				+ XmppFileTransferFactory.END;

		this.listeners.put(r, nsl);
		this.outgoingRequests.add(r);
		//add timer and schedule it for execution
		timeoutTask = new TimeoutTask(this,nsl,r);
		this.outgoingRequestTimeoutTasks.put(r, timeoutTask);
		this.getTimeoutTimer().schedule(timeoutTask, XmppFileTransferMethod.REQUEST_TIMEOUT);

		log.debug("requests I have to " + r.getPeer() + " : "
				+ this.outgoingRequests.size() + " : " + this.outgoingRequests);

		try {
			this.negotiationService.sendMessage(r.getPeer(), request);
		} catch (Exception e) {
			XmppFileTransferFactory.log.info("negotiation failed", e);
			try {
				nsl.failed(e);
			} catch (Exception ignored) {
			}
			removeOutgoing(r);
		}
	}

	@Override
	public void receivedMessage(UserId from, String content) {
		XmppUserId from_userid = new XmppUserId(from.getUserId());
		if (!content.startsWith(XmppFileTransferFactory.START)
				|| !content.endsWith(XmppFileTransferFactory.END))
			return;

		String inner = content.substring(XmppFileTransferFactory.START.length(), content
				.length()
				- XmppFileTransferFactory.END.length());

		log.debug(myUserId + ": receivedMessage : " + from_userid + " : " + inner);

		if (inner.startsWith(FILE_REQUEST)) {
			handleFileRequest(from_userid, inner);
			/*
			 * ADDRESS_RESPONSE:
			 * [GOT_REQUESTED_FILE<uuid><filename>]ADDRESS_RESPONSE[<address>]
			 */
		} else if (inner.equals(FILE_RESPONSE_DONT_HAVE)) {
			/*
			 * third step, client receives no ok from server
			 */
			// we are the client, server doesn't have it
			for (FileRequest r : getRequestsForUser(outgoingRequests, from_userid)) {
				INegotiationSuccessListener nsl = this.listeners.get(r);
				try {
					nsl.failed(new OtherUserDoesntHaveRequestedContentException());
				} catch (Exception ignored) {
				}
				removeOutgoing(r);
			}
		} else {
			log.warn("unknown request from " + from_userid + ": " + content);
		}
	}

	private void handleFileRequest(XmppUserId from_userid, String inner) {
		/*
		 * second step, server receives request
		 */
		// we are the server, client wants a file.
		String filename = inner.substring(FILE_REQUEST.length());
		FileRequest fr = new FileRequest(filename, true, from_userid);

		log
				.debug(this.myUserId.getUserId() + " : " + from_userid + " wants  "
						+ filename);
		String response = XmppFileTransferFactory.START;
		boolean success = true;
		if (!isServing()) {
			log.debug("We don't serve");
			success = false;
		} else {
			log.debug("Do we accept?");
			if (!this.incomingTransferListener.accept(fr)) {
				success = false;
			} else {
				log.debug("Do we have the file?");

				File localFile = this.mapper.getFileForRequest(fr);

				if (localFile == null) {
					success = false;
				} else {
					log.debug("Does the partner have a resource?");
					if (from_userid.getResource().length() == 0)
						success = false;
					else {
						IFileTransfer ft = sendFile(localFile, from_userid
								.getUserIdWithResource(), filename, fr);
						incomingTransferListener.started(ft);
					}
				}
			}
		}
		if (!success) {
			response += FILE_RESPONSE_DONT_HAVE;
			log.debug("Not answering with a positive response");
			response += XmppFileTransferFactory.END;

			try {
				this.negotiationService.sendMessage(from_userid, response);
			} catch (Exception e) {
				XmppFileTransferFactory.log.warn("sending failed", e);
			}
		}
	}

	public IFileTransfer sendFile(File f, String to, String filename, FileRequest request) {
		log.debug(this.myUserId + " : Sending file " + f.getAbsolutePath() + " to " + to);
		// Create the file transfer manager
		FileTransferManager manager = new FileTransferManager(this.connection);

		// Create the outgoing file transfer
		OutgoingFileTransfer ft = manager.createOutgoingFileTransfer(to);

		// Send the file
		try {
			ft.sendFile(f, filename);
		} catch (XMPPException e) {
			log.error("Sending file failed!");
			log.debug(e);
		}
		IFileTransfer xft = new XmppFileTransfer(ft, request, f);
		return xft;
	}

	/*
	 * private class LocalFile extends AdditionalFileTransferData {
	 * 
	 * public LocalFile(File f) { this.setDataFile(f); } }
	 */

	private List<FileRequest> getRequestsForUser(Queue<FileRequest> frq, XmppUserId userid) {
		List<FileRequest> rq = new LinkedList<FileRequest>();
		for (FileRequest r : frq) {
			log.debug(r.getPeer() + " - " + r.getFileName());
			if (userid.isSameUserAs(r.getPeer())) {
				rq.add(r);
			}
		}
		return rq;
	}

	@Override
	public void removeOutgoing(FileRequest r) {
		log.debug("I'm done with outgoing request " + r + " (one way or the other)");
		TimerTask task = this.outgoingRequestTimeoutTasks.remove(r);
		if (task != null)
			task.cancel();
		this.outgoingRequests.remove(r);
		this.listeners.remove(r);
	}

	public boolean isServing() {
		return this.incomingTransferListener != null;
	}

	@Override
	public void startServing(IncomingTransferListener l, FileRequestFileMapper mapper)
			throws NotLoggedInException {
		log.debug(this.myUserId + ": starting to serve");
		this.incomingTransferListener = l;
		this.mapper = mapper;
	}

	@Override
	public void stopServing() {
		this.incomingTransferListener = null;
	}

	/**
	 * registers the filetransfer hook
	 */
	public void startReceiving() {
		log.debug(myUserId);

		if (this.connection == null) {
			log.debug(myUserId + ": not logged in");
			return;
		}
		
		/*
		 * The connection may have been closed or may not be open yet.
		 * In this case, adding a FileTransferListener would cause an evil
		 * exception. To prevent this, we must make sure, that there is a running
		 * connection to the server.
		 */
		if (!this.connection.isConnected())
			try {
				log.debug("we are not connected...connect now!");
				this.connection.connect();
			} catch (XMPPException e1) {
				log.warn(e1);
			}
		
		// Create the file transfer manager
		final FileTransferManager manager = new FileTransferManager(this.connection);

		log.debug(myUserId + ": adding receiver hook");
		// Create the listener
		manager.addFileTransferListener(new FileTransferListener() {

			// incoming transfer
			synchronized public void fileTransferRequest(FileTransferRequest request) {
				// Check to see if the request should be accepted
				log.debug(XmppFileTransferMethod.this.myUserId
						+ "incoming fileTransfer: " + request.getDescription() + " from "
						+ request.getRequestor());
				for (FileRequest r : getRequestsForUser(outgoingRequests, new XmppUserId(
						request.getRequestor()))) {
					if (r.getFileName().equals(request.getDescription())) {
						// Accept it
						INegotiationSuccessListener nsl = XmppFileTransferMethod.this.listeners
								.get(r);
						IncomingFileTransfer transfer = request.accept();

						File tempFile;
						try {
							tempFile = File.createTempFile("recvFile", r.getFileName());
						} catch (IOException e) {
							log.debug(e);
							log.error("creating temporary file failed.");
							break;
						}
						try {
							transfer.recieveFile(tempFile);
							IFileTransfer ft = new XmppFileTransfer(transfer, r, tempFile);
							try {
								nsl.succeeded(ft);
							} catch (Exception ignored) {
								removeOutgoing(r);
							}
						} catch (XMPPException e) {
							try {
								nsl.failed(new CommunicationProblemException(e.getCause()));
							} catch (Exception ignored) {
							}
						}
						break;
					}
				}

				log.info("ignoring incoming transfer (no request): "
						+ request.getRequestor() + " - " + request.getFileName());
				// Do not reject it, it might be for some other
				// ressource (e.g. the normal chat client).
			}
		});
	}


	@Override
	public void connectionStateChanged(ConnectionState le, Exception ex) {
		if(ConnectionState.LOGGED_IN == le) {
			this.startReceiving();
		}
	}
}