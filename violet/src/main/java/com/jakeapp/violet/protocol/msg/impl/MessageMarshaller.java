package com.jakeapp.violet.protocol.msg.impl;

import java.io.IOException;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.violet.di.DI;
import com.jakeapp.violet.model.LogEntry;
import com.jakeapp.violet.protocol.msg.ILogEntryMarshaller;
import com.jakeapp.violet.protocol.msg.IMessageMarshaller;
import com.jakeapp.violet.protocol.msg.PokeMessage;

/**
 * This class is used to marshal and unmarshal (build and analyze) messages sent
 * to a msgService or received from a msgService
 */
public class MessageMarshaller implements IMessageMarshaller {

	static final String FIELD_SEPERATOR = ".";

	static final String POKE_MESSAGE = "<poke/>";

	private static Logger log = Logger.getLogger(MessageMarshaller.class);

	private ILogEntryMarshaller logEntryMarshaller = DI
			.getImpl(LogEntryMarshaller.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.jakeapp.violet.protocol.msg.IMessageMarshaller#decodePokeMessage(
	 * java.lang.String, com.jakeapp.jake.ics.UserId)
	 */
	@Override
	public PokeMessage decodePokeMessage(String s, UserId from)
			throws IOException {
		log.debug("unpacking string " + s);
		String[] parts = s.split("\\" + FIELD_SEPERATOR, 3);
		if (parts.length != 3)
			throw new IOException("unexpected format: " + s);

		if (!POKE_MESSAGE.equals(parts[2]))
			throw new IOException("unexpected type: " + s);
		LogEntry le = null;
		if (parts[1].length() != 0)
			le = logEntryMarshaller.deSerializeLogEntry(parts[1]);
		PokeMessage msg = PokeMessage.createPokeMessage(
				UUID.fromString(parts[0]), from, le);
		return msg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.jakeapp.violet.protocol.msg.IMessageMarshaller#serialize(com.jakeapp
	 * .violet.protocol.msg.PokeMessage)
	 */
	@Override
	public String serialize(PokeMessage msg) throws IOException {
		String leString = "";
		if (msg.getLogEntry() != null)
			leString = logEntryMarshaller.serializeLogEntry(msg.getLogEntry());
		return msg.getProjectId().toString() + FIELD_SEPERATOR + POKE_MESSAGE
				+ FIELD_SEPERATOR + leString;
	}

}
