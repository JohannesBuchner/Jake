package com.jakeapp.violet.protocol.msg;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import com.jakeapp.violet.model.LogEntry;

public interface ILogEntryMarshaller {

	/**
	 * serialize the log entries
	 * 
	 * @param projectid
	 * @param logs
	 * @return
	 * @throws IOException
	 */
	public abstract void packLogEntries(UUID projectid, List<LogEntry> logs,
			OutputStream os) throws IOException;

	/**
	 * de-serialize the log entries
	 * 
	 * @param projectid
	 * @param logs
	 * @return
	 * @throws IOException
	 */
	public abstract List<LogEntry> unpackLogEntries(UUID projectid,
			InputStream is) throws IOException;

	public abstract String serializeLogEntry(LogEntry le) throws IOException;

	public abstract LogEntry deSerializeLogEntry(String s) throws IOException;

}