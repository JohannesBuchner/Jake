package com.jakeapp.violet.synchronization.request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.jakeapp.violet.model.LogEntry;
import com.jakeapp.violet.protocol.PokeMessage;
import com.jakeapp.violet.protocol.RequestFileMessage;
import com.jakeapp.violet.protocol.RequestLogsMessage;

/**
 * This class is used to marshal and unmarshal (build and analyze) messages sent
 * to a msgService or received from a msgService
 */
public class MessageMarshaller {

	private static final String BEGIN_PROJECT_UUID = "<project>";

	private static final String END_PROJECT_UUID = "</project>";

	private static final String POKE_MESSAGE = "<poke/>";

	private static final String REQUEST_LOGS_MESSAGE = "<requestlogs/>";

	private static final String BEGIN_LOGENTRY = "<le>";

	private static final String END_LOGENTRY = "</le>";

	private static final String LOGENTRIES_MESSAGE = "<logentries/>";

	private static Logger log = Logger.getLogger(MessageMarshaller.class);

	private ObjectMapper objectMapper = new ObjectMapper();

	public String packLogEntries(UUID projectid, List<LogEntry> logs)
			throws IOException {
		StringBuffer sb = new StringBuffer(getUUIDStringForProject(projectid))
				.append(LOGENTRIES_MESSAGE);
		String s = objectMapper.writeValueAsString(logs);
		return sb.append(s).toString();
	}

	public String serialize(PokeMessage msg) {
		return getUUIDStringForProject(msg.getProjectId()) + POKE_MESSAGE;
	}

	public String serialize(RequestLogsMessage msg) {
		return getUUIDStringForProject(msg.getProjectId())
				+ REQUEST_LOGS_MESSAGE;
	}

	public String serialize(RequestFileMessage msg) {
		// can't use XML characters
		return msg.getProjectId() + "." + msg.getJakeObject().getRelPath();
	}

	private String getUUIDStringForProject(UUID projectid) {
		return BEGIN_PROJECT_UUID + projectid + END_PROJECT_UUID;
	}

	public List<LogEntry> unpackLogEntries(String les) throws IOException {
		log.debug("unpacking string " + les);
		List<LogEntry> logs = objectMapper.readValue(les, List.class);
		return logs;
	}

	public UUID getProjectUUIDFromRequestMessage(String incomingMessage) {
		try {
			String[] parts = incomingMessage.split("\\.", 2);
			if (parts.length != 2)
				return null;
			return UUID.fromString(parts[0]);
		} catch (Exception e) {
			return null;
		}
	}

	public UUID getLogEntryUUIDFromRequestMessage(String incomingMessage) {
		try {
			String[] parts = incomingMessage.split("\\.", 2);
			if (parts.length != 2)
				return null;
			return UUID.fromString(parts[1]);
		} catch (Exception e) {
			return null;
		}
	}

}
