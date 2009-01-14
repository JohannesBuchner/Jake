package com.jakeapp.jake.ics.impl.xmpp.filetransfer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

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
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;
import com.jakeapp.jake.ics.msgservice.IMessageReceiveListener;
import com.jakeapp.jake.ics.msgservice.IMsgService;

/**
 * {@link ITransferMethod} for Xmpp. Use {@link XmppFileTransferFactory} to
 * obtain.
 * 
 * @author johannes
 * 
 */
// TODO: timeouts for negotiations
public class XmppFileTransferMethod implements ITransferMethod, IMessageReceiveListener {


	private static Logger log = Logger.getLogger(XmppFileTransferMethod.class);

	private static final String FILE_REQUEST = "<filerequest/>";

	private static final Object FILE_RESPONSE_DONT_HAVE = "<fileresponseno/>";

	private IMsgService negotiationService;

	private Map<FileRequest, INegotiationSuccessListener> listeners = new HashMap<FileRequest, INegotiationSuccessListener>();

	private Queue<FileRequest> outgoingRequests = new LinkedBlockingQueue<FileRequest>();

	private Map<UUID, FileRequest> incomingRequests = new HashMap<UUID, FileRequest>();

	private FileRequestFileMapper mapper;

	private UserId myUserId;

	private IncomingTransferListener incomingTransferListener;

	private static final int UUID_LENGTH = UUID.randomUUID().toString().length();

	private XMPPConnection connection;

	public XmppFileTransferMethod(XMPPConnection connection,
			IMsgService negotiationService, UserId user) throws NotLoggedInException {
		log.debug("creating XmppFileTransferMethod for user " + user);
		this.myUserId = user;
		this.connection = connection;
		this.negotiationService = negotiationService;
		this.negotiationService.registerReceiveMessageListener(this);
		startReceiving();
	}

	/*
	 * first step, client requests something
	 */
	@Override
	public void request(FileRequest r, INegotiationSuccessListener nsl) {
		log.debug(myUserId + ": We request " + r);
		String request = XmppFileTransferFactory.START + FILE_REQUEST + r.getFileName()
				+ XmppFileTransferFactory.END;

		this.listeners.put(r, nsl);
		this.outgoingRequests.add(r);

		log.debug("requests I have to " + r.getPeer() + " : "
				+ this.outgoingRequests.size() + " : " + this.outgoingRequests);

		try {
			this.negotiationService.sendMessage(r.getPeer(), request);
		} catch (Exception e) {
			XmppFileTransferFactory.log.info("negotiation failed", e);
			nsl.failed(e);
			removeOutgoing(r);
			return;
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
				nsl.failed(new OtherUserDoesntHaveRequestedContentException());
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
		if (isServing()) {
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
					IFileTransfer ft = sendFile(localFile, from_userid
							.getUserIdWithResource(), filename, fr);
					incomingTransferListener.started(ft);
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

	public boolean isServing() {
		return this.incomingTransferListener != null;
	}

	@Override
	public void startServing(IncomingTransferListener l, FileRequestFileMapper mapper)
			throws NotLoggedInException {
		log.debug(this.myUserId + ": startServing");

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
		// Create the file transfer manager
		final FileTransferManager manager = new FileTransferManager(this.connection);

		// Create the listener
		manager.addFileTransferListener(new FileTransferListener() {

			// incoming transfer
			synchronized public void fileTransferRequest(FileTransferRequest request) {
				// Check to see if the request should be accepted

				for (FileRequest r : getRequestsForUser(outgoingRequests, new XmppUserId(
						request.getRequestor()))) {
					if (r.getFileName().equals(request.getFileName())) {
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
							return;
						}
						try {
							transfer.recieveFile(tempFile);
							IFileTransfer ft = new XmppFileTransfer(transfer, r, tempFile);
							nsl.succeeded(ft);
						} catch (XMPPException e) {
							nsl.failed(new CommunicationProblemException(e.getCause()));
						}
						return;
					}
				}

				log.info("ignoring incoming transfer (no request): "
						+ request.getRequestor() + " - " + request.getFileName());
				// Do not reject it, it might be for some other
				// ressource (e.g. the normal chat client).
			}
		});
	}
}