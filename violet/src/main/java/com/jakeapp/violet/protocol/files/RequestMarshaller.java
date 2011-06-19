package com.jakeapp.violet.protocol.files;

import java.util.UUID;

import org.apache.log4j.Logger;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.violet.protocol.files.RequestFileMessage.RequestType;

/**
 * This class is used to marshal and unmarshal (build and analyze) the "names"
 * of files requested.
 */
public class RequestMarshaller implements IRequestMarshaller {

	private static final String FIELD_SEPERATOR = ".";

	private static Logger log = Logger.getLogger(RequestMarshaller.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.jakeapp.violet.protocol.files.IRequestMarshaller#serialize(com.jakeapp
	 * .violet.protocol.files.RequestFileMessage)
	 */
	@Override
	public String serialize(RequestFileMessage msg) {
		// can't use XML characters
		return msg.getProjectId() + FIELD_SEPERATOR + msg.getType()
				+ FIELD_SEPERATOR + msg.getIdentifier();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.jakeapp.violet.protocol.files.IRequestMarshaller#decodeRequestFileMessage
	 * (java.lang.String, com.jakeapp.jake.ics.UserId)
	 */
	@Override
	public RequestFileMessage decodeRequestFileMessage(String incomingMessage,
			UserId from) {
		try {
			String[] parts = incomingMessage.split("\\" + FIELD_SEPERATOR, 3);
			RequestType t = RequestFileMessage.RequestType.valueOf(parts[1]);
			UUID projectId = UUID.fromString(parts[0]);
			return new RequestFileMessage(projectId, from, t, parts[2]);
		} catch (Exception e) {
			log.warn("decoding problem", e);
			return null;
		}
	}

}
