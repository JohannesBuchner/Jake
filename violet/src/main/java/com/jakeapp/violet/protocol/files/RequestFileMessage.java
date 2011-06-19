package com.jakeapp.violet.protocol.files;

import java.util.UUID;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.violet.model.LogEntry;
import com.jakeapp.violet.protocol.Message;

/**
 * Request
 * 
 * - full file (type = "file"). file:UUID -- uuid is identifier
 * 
 * - delta (type = "delta"). delta:UUID -- uuid is identifier
 * 
 * - signature (type = "signature"). signature:relpath -- relpath is identifier
 */
public class RequestFileMessage extends Message {

	private RequestType type;

	private String identifier;

	public enum RequestType {
		FILE("file"), LOGS("logs"), DELTA("delta"), SIGNATURE("signature");

		private String s;

		private RequestType(String s) {
			this.s = s;
		}

		@Override
		public String toString() {
			return s;
		}
	}

	public void setType(RequestType type) {
		this.type = type;
	}

	public RequestType getType() {
		return type;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return identifier;
	}

	RequestFileMessage(UUID projectId, UserId user, RequestType type,
			String identifier) {
		super(projectId, user);
		setType(type);
		setIdentifier(identifier);
	}

	/**
	 * Requesting the full file
	 * 
	 * @param projectId
	 *            Project
	 * @param user
	 *            other User
	 * @param le
	 *            relevant Log entry
	 * @return the message
	 */
	public static RequestFileMessage createRequestFileMessage(UUID projectId,
			UserId user, LogEntry le) {
		return new RequestFileMessage(projectId, user, RequestType.FILE, le
				.getId().toString());
	}

	/**
	 * Requesting the logs
	 * 
	 * @param projectId
	 *            Project
	 * @param user
	 *            other User
	 * @param le
	 *            relevant Log entry
	 * @return the message
	 */
	public static RequestFileMessage createRequestLogsMessage(UUID projectId,
			UserId user) {
		return new RequestFileMessage(projectId, user, RequestType.LOGS, "");
	}

	/**
	 * Requesting a delta update file
	 * 
	 * @param projectId
	 *            Project
	 * @param user
	 *            other User
	 * @param le
	 *            relevant Log entry
	 * @return the message
	 */
	public static RequestFileMessage createRequestDeltaMessage(UUID projectId,
			UserId user, LogEntry le) {
		return new RequestFileMessage(projectId, user, RequestType.DELTA, le
				.getId().toString());
	}

	/**
	 * Requesting a signature of what the current checksums are, so that a delta
	 * can be created
	 * 
	 * @param projectId
	 *            Project
	 * @param user
	 *            other User
	 * @param le
	 *            relevant Log entry
	 * @return the message
	 */
	public static RequestFileMessage createRequestSignatureMessage(
			UUID projectId, UserId user, LogEntry le) {
		return new RequestFileMessage(projectId, user, RequestType.SIGNATURE,
				le.getId().toString());
	}
}
