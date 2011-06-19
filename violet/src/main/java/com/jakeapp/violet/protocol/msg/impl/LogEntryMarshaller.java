package com.jakeapp.violet.protocol.msg.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.jakeapp.violet.model.JakeObject;
import com.jakeapp.violet.model.LogEntry;
import com.jakeapp.violet.model.User;
import com.jakeapp.violet.protocol.msg.ILogEntryMarshaller;

/**
 * This class is used to marshal and unmarshal (build and analyze) messages sent
 * to a msgService or received from a msgService
 */
public class LogEntryMarshaller implements ILogEntryMarshaller {

	private static Logger log = Logger.getLogger(LogEntryMarshaller.class);

	private ObjectMapper objectMapper = new ObjectMapper();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.jakeapp.violet.protocol.msg.ILogEntryMarshaller#packLogEntries(java
	 * .util.UUID, java.util.List, java.io.OutputStream)
	 */
	@Override
	public void packLogEntries(UUID projectid, List<LogEntry> logs,
			OutputStream os) throws IOException {
		List<String> logStrings = new ArrayList<String>(logs.size());
		for (LogEntry le : logs) {
			logStrings.add(serializeLogEntry(le));
		}
		objectMapper.writeValue(os, logStrings);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.jakeapp.violet.protocol.msg.ILogEntryMarshaller#unpackLogEntries(
	 * java.util.UUID, java.io.InputStream)
	 */
	@Override
	public List<LogEntry> unpackLogEntries(UUID projectid, InputStream is)
			throws IOException {
		List<String> logStrings = objectMapper.readValue(is, List.class);
		List<LogEntry> logs = new ArrayList<LogEntry>(logStrings.size());
		for (String s : logStrings) {
			logs.add(deSerializeLogEntry(s));
		}
		return logs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.jakeapp.violet.protocol.msg.ILogEntryMarshaller#serializeLogEntry
	 * (com.jakeapp.violet.model.LogEntry)
	 */
	@Override
	public String serializeLogEntry(LogEntry le) throws IOException {
		String[] s = { le.getId().toString(),
				Long.toString(le.getWhen().getTime()), le.getWho().getUserId(),
				le.getWhat().getRelPath(), le.getHow(), le.getWhy() };
		return objectMapper.writeValueAsString(s);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.jakeapp.violet.protocol.msg.ILogEntryMarshaller#deSerializeLogEntry
	 * (java.lang.String)
	 */
	@Override
	public LogEntry deSerializeLogEntry(String s) throws IOException {
		String[] a = objectMapper.readValue(s, String[].class);
		return new LogEntry(UUID.fromString(a[0]), new Timestamp(
				Long.parseLong(a[1])), new User(a[2]), new JakeObject(a[3]),
				a[5], a[4], false);
	}

}
