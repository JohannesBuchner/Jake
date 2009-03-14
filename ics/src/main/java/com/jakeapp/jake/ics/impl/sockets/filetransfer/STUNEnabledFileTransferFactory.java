package com.jakeapp.jake.ics.impl.sockets.filetransfer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.filetransfer.FileRequestFileMapper;
import com.jakeapp.jake.ics.filetransfer.IncomingTransferListener;
import com.jakeapp.jake.ics.filetransfer.methods.ITransferMethod;
import com.jakeapp.jake.ics.filetransfer.methods.ITransferMethodFactory;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.msgservice.IMessageReceiveListener;
import com.jakeapp.jake.ics.msgservice.IMsgService;

public class STUNEnabledFileTransferFactory implements ITransferMethodFactory {

	protected static final String START = "<filetransfer><![CDATA[";

	protected static final String END = "]]></filetransfer>";

	public static final int PORT = 43214;

	private static Logger log = Logger
			.getLogger(STUNEnabledFileTransferFactory.class);

	public STUNEnabledFileTransferFactory() {
		// nothing to do in this case. maybe determine IP address?
		// XMPPs TransferFactory will have to be constructed with the
		// XmppConnection
	}

	@Override
	public ITransferMethod getTransferMethod(IMsgService negotiationService, UserId user) {
		return new SocketFileTransfer(negotiationService, user);
	}

	static public class SocketFileTransfer implements ITransferMethod,
			IMessageReceiveListener {

		private IMsgService negotiationService;

		@SuppressWarnings("unused")
		private Map<UUID, NegotiationState> runningrequests = new HashMap<UUID, NegotiationState>();

		private ServerSocket socket;

		public SocketFileTransfer(IMsgService negotiationService, UserId user) {
			this.negotiationService = negotiationService;
		}

		@Override
		public void request(FileRequest r, INegotiationSuccessListener nsl) {
			String offer = START + "request|file|" + r.getFileName() + END;
			try {
				negotiationService.sendMessage(r.getPeer(), offer);
			} catch (Exception e) {
				log.info("negotiation failed", e);
				try {
					nsl.failed(e);
				} catch (Exception ignored) {
				}
			}
		}

		@Override
		public void receivedMessage(UserId from_userid, String content) {
			if (!content.startsWith(START) || !content.endsWith(END))
				return;
			String inner = content.substring(START.length(), content.length()
					- END.length());
			String[] fields = inner.split("\\|");
			String response = handlePacket(from_userid, fields);
			try {
				if (response != null)
					negotiationService.sendMessage(from_userid, response);
			} catch (Exception e) {
				log.error("sending failed", e);
			}
		}

		private String handlePacket(UserId from_userid, String[] fields) {
			if (fields.length == 0)
				return null;
			/*if (fields[0].equals(NegotiationState.request)
					&& fields.length == 3) {
				String type = fields[1];
				String filename = fields[2];
				String response = START + NegotiationState.serverips;
				for (InetSocketAddress ip : provideAddresses()) {
					response += "|" + ip.toString();
				}
				response += END;
				return response;
			} else if (fields[0].equals(NegotiationState.serverips)
					&& fields.length > 1) {
				try {
					InetSocketAddress other = new InetSocketAddress(fields[1]);
					String response = START + NegotiationState.clientips;
					// TODO: improvable (multiple addresses, STUN, etc)
					response += END;
					return response;
				} catch (UnknownHostException e) {
					log.error("unpacking ip failed", e);
					return null;
				} catch (NumberFormatException e) {
					log.error("unpacking port failed", e);
					return null;
				}
			} else if (fields[0].equals(NegotiationState.clientips)) {
				// TODO: improvable (ie: care)
				String response = START + NegotiationState.serverresults;
				response += END;
				return response;
			} else if (fields[0].equals(NegotiationState.serverresults)) {
				// TODO: improvable (ie: care)
				String response = START + NegotiationState.clientdecision;
				response += END;
				return response;
			}*/
			return null;
		}

		@Override
		public void startServing(IncomingTransferListener l,
				FileRequestFileMapper mapper) throws NotLoggedInException {
			negotiationService.registerReceiveMessageListener(this);
			try {
				socket = new ServerSocket(PORT);
			} catch (IOException e) {
				log.error(e);
				throw new NotLoggedInException();
			}
		}

		public Iterable<InetSocketAddress> provideAddresses() {
			List<InetSocketAddress> adds = new LinkedList<InetSocketAddress>();
			adds.add(new InetSocketAddress(socket.getInetAddress(), socket
					.getLocalPort()));
			return adds;
		}

		@Override
		public void stopServing() {
			// TODO Auto-generated method stub
			
		}

	}

}
