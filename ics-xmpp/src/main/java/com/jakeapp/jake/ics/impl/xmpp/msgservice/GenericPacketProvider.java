package com.jakeapp.jake.ics.impl.xmpp.msgservice;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;

public class GenericPacketProvider implements PacketExtensionProvider {

	private String namespace;

	public GenericPacketProvider(String namespace) {
		if(namespace == null)
			throw new NullPointerException();
		this.namespace = namespace;
	}
	
	public PacketExtension parseExtension(XmlPullParser parser)
			throws Exception {
		String content = null;

		boolean done = false;
		while (!done) {
			parser.next();
			String elementName = parser.getName();
			if (parser.getEventType() == XmlPullParser.START_TAG) {
				if ("content".equals(elementName)) {
					content = parser.nextText();
				}
			} else if (parser.getEventType() == XmlPullParser.END_TAG
					&& GenericPacketExtension.ELEMENT_NAME.equals(elementName)) {
				done = true;
			}
		}
		return new GenericPacketExtension(namespace, content);
	}
}
