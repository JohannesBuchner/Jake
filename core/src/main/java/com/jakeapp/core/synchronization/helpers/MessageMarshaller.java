package com.jakeapp.core.synchronization.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.ILogable;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogEntrySerializer;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.logentries.LogEntry;

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

	private LogEntrySerializer logEntrySerializer;

	public MessageMarshaller(LogEntrySerializer logEntrySerializer) {
		this.logEntrySerializer = logEntrySerializer;
	}

	public String pokeProject(Project project) {
		return getUUIDStringForProject(project) + POKE_MESSAGE;
	}

	public String requestLogs(Project project) {
		return getUUIDStringForProject(project) + REQUEST_LOGS_MESSAGE;
	}

	public String requestFile(Project project, LogEntry<JakeObject> le) {
		// can't use XML characters
		return project.getProjectId() + "." + le.getUuid();
	}


	public String packLogEntries(Project project, List<LogEntry<? extends ILogable>> logs) {

		StringBuffer sb = new StringBuffer(getUUIDStringForProject(project))
				.append(LOGENTRIES_MESSAGE);
		StringBuffer partResult = new StringBuffer();

		log.debug("Starting to process log entries...");
		for (LogEntry<? extends ILogable> l : logs) {
			try {
				//clear partResult
				partResult.delete(0, partResult.length());
				
				//build serialized version of l 
				partResult.append(BEGIN_LOGENTRY).append(
						this.logEntrySerializer.serialize(l, project)).append(
						END_LOGENTRY);
				
				log.debug("Serialised log entry " + partResult.toString());
				//only if building succeeded, add the logEntry to sb
				sb.append(partResult);
				log.debug("New sb content: " + sb.toString());
			} catch (Throwable e) {
				log.info("Failed to serialize log entry: " + l.getLogAction().toString()
						+ "(" + l.toString() + ")", e);
			}
		}
		log.debug("Finished processing log entries! Now sending.");

		return sb.toString();
	}


	private String getUUIDStringForProject(Project project) {
		return BEGIN_PROJECT_UUID + project.getProjectId() + END_PROJECT_UUID;
	}

	public List<LogEntry<? extends ILogable>> unpackLogEntries(String les) {
		log.debug("unpacking string " + les);
		List<LogEntry<? extends ILogable>> results = new ArrayList<LogEntry<? extends ILogable>>();
		String[] logentries = les.split(END_LOGENTRY + BEGIN_LOGENTRY);

		for (String l : logentries) {
			try {
				log.debug("unpacking substring " + l);
				LogEntry<? extends ILogable> logentry = this.logEntrySerializer.deserialize(l);
				log.debug("got logentry: " + logentry.toString());
				results.add(logentry);
			} catch (Throwable t) {
				log.warn("Failed to deserialize and/or save", t);
			}
		}
		return results;
	}

	public UUID getProjectUUIDFromRequestMessage(String incomingMessage) {
		try {
			String[] parts = incomingMessage.split("\\.", 2);
			if(parts.length != 2)
				return null;
			return UUID.fromString(parts[0]);
		} catch (Exception e) {
			return null;
		}
	}

	public UUID getLogEntryUUIDFromRequestMessage(String incomingMessage) {
		try {
			String[] parts = incomingMessage.split("\\.", 2);
			if(parts.length != 2)
				return null;
			return UUID.fromString(parts[1]);
		} catch (Exception e) {
			return null;
		}
	}


}
