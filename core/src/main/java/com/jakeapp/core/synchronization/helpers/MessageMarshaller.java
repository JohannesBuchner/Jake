package com.jakeapp.core.synchronization.helpers;

import com.jakeapp.core.domain.*;
import com.jakeapp.core.domain.exceptions.IllegalProtocolException;
import com.jakeapp.core.domain.logentries.LogEntry;
import com.jakeapp.core.synchronization.ChangeListener;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * This class is used to marshall and demarshall (serialize & deserialize) messages
 * sent to a msgService or received from a msgService
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

	public String pokeProject(String projectId) {
		return BEGIN_PROJECT_UUID + projectId + END_PROJECT_UUID + POKE_MESSAGE;
	}

	public String requestLogs(String projectId) {
		return BEGIN_PROJECT_UUID + projectId + END_PROJECT_UUID + REQUEST_LOGS_MESSAGE;
	}


	public String packLogEntries(Project project, List<LogEntry<? extends ILogable>> logs) {

		StringBuffer sb = new StringBuffer(getUUIDStringForProject(project)).append(LOGENTRIES_MESSAGE);

		log.debug("Starting to process log entries...");
		for (LogEntry l : logs) {
			try {
				sb.append(BEGIN_LOGENTRY).append(logEntrySerializer.serialize(l, project)).append(END_LOGENTRY);
				log.debug("Serialised log entry, new sb content: " + sb.toString());
			} catch (Throwable e) {
				log.info("Failed to serialize log entry: " + l.getLogAction().toString() + "(" + l.toString() + ")", e);
			}
		}
		log.debug("Finished processing log entries! Now sending.");

		return sb.toString();
	}


	private String getUUIDStringForProject(Project project) {
		return BEGIN_PROJECT_UUID + project.getProjectId() + END_PROJECT_UUID;
	}

	public List<LogEntry> unpackLogEntries(String les)
	{
		List<LogEntry> results = new ArrayList<LogEntry>();
		String[] logentries = les.split(END_LOGENTRY + BEGIN_LOGENTRY);

			for (String l : logentries) {
				try {
					results.add(logEntrySerializer.deserialize(l));
				} catch (Throwable t) {
					log.debug("Failed to deserialize and/or save", t);
				}
			}
		return results;
	}




}
