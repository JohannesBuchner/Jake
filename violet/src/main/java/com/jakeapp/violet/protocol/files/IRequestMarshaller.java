package com.jakeapp.violet.protocol.files;

import com.jakeapp.jake.ics.UserId;

public interface IRequestMarshaller {

	/**
	 * Serialize the file request
	 * 
	 * @param msg
	 * @return
	 */
	public abstract String serialize(RequestFileMessage msg);

	/**
	 * De-serialize the file request
	 * 
	 * @param incomingMessage
	 * @param from
	 * @return
	 */
	public abstract RequestFileMessage decodeRequestFileMessage(
			String incomingMessage, UserId from);

}