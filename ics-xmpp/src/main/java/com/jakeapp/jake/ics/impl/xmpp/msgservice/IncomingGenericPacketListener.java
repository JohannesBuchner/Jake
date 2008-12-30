package com.jakeapp.jake.ics.impl.xmpp.msgservice;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.Base64;

import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;
import com.jakeapp.jake.ics.msgservice.IMessageReceiveListener;

public class IncomingGenericPacketListener implements PacketListener,
		PacketFilter {

	List<IMessageReceiveListener> listeners = new LinkedList<IMessageReceiveListener>();
	
	public boolean add(IMessageReceiveListener e) {
		return listeners.add(e);
	}

	public boolean remove(IMessageReceiveListener e) {
		return listeners.remove(e);
	}

	private static final Logger log = Logger.getLogger(IncomingGenericPacketListener.class);

	private String namespace;

	public IncomingGenericPacketListener(String namespace) {
		super();
		this.namespace = namespace;
	}

	public void processPacket(Packet packet) {
		GenericPacketExtension fre = (GenericPacketExtension) packet
				.getExtension(this.namespace);

		log.info("incoming (generice) packet from " + packet.getFrom());

		String content = new String(Base64.decode(fre.getContent()));
		notifyOthersAboutNewPacket(new XmppUserId(packet.getFrom()), content);
	}

	public boolean accept(Packet packet) {
		GenericPacketExtension fre = (GenericPacketExtension) packet
				.getExtension(this.namespace);
		if (fre != null) {
			return true;
		} else {
			return false;
		}
	}

	public void notifyOthersAboutNewPacket(XmppUserId xmppUserId, String content) {
		for(IMessageReceiveListener imrl : listeners) {
			imrl.receivedMessage(xmppUserId, content);
		}
	}
}