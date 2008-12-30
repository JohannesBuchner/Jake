package com.jakeapp.jake.ics.impl.xmpp.msgservice;
import org.jivesoftware.smack.packet.PacketExtension;


public class GenericPacketExtension implements PacketExtension {

	/**
	 * Element name of the packet extension.
	 */
	public static final String ELEMENT_NAME = "custom";

	/**
	 * Namespace of the packet extension.
	 */
	private String namespace;

	/**
	 * content of interest
	 */
	private String content;
	
	private GenericPacketExtension() {
		
	}
	
	public GenericPacketExtension(String namespace) {
		this.namespace = namespace;
	}

	public GenericPacketExtension(String namespace, String content) {
		this(namespace);
		this.content = content;
	}

	public String getElementName() {
		return ELEMENT_NAME;
	}

	public String getNamespace() {
		return namespace;
	}

	public String toXML() {
		StringBuilder buf = new StringBuilder();

		buf.append("<").append(ELEMENT_NAME).append(" xmlns=\"");
		buf.append(namespace).append("\">");

		if (content != null) {
			buf.append("<content>").append(content).append("</content>");
		}
		// Add packet extensions, if any are defined.
		buf.append("</").append(ELEMENT_NAME).append("> ");

		return buf.toString();
	}

	public String getContent() {
		return content;
	}
}
