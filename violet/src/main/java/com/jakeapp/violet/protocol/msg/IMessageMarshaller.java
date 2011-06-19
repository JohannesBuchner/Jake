package com.jakeapp.violet.protocol.msg;

import java.io.IOException;

import com.jakeapp.jake.ics.UserId;

public interface IMessageMarshaller {

	/**
	 * de-serialize the poke message
	 * 
	 * @param s
	 *            message in string format
	 * @param from
	 * @return the Message
	 * @throws IOException
	 */
	public abstract PokeMessage decodePokeMessage(String s, UserId from)
			throws IOException;

	/**
	 * serialize the poke message
	 * 
	 * @param msg
	 * @return
	 * @throws IOException
	 */
	public abstract String serialize(PokeMessage msg) throws IOException;

}