package com.doublesignal.sepm.jake.core.services;

import com.doublesignal.sepm.jake.core.domain.JakeMessage;


/**
 * Objects wanting to receive JakeMessages have to implement this
 *
 * @see JakeGuiAccess
 * @author chris
 */

public interface IJakeMessageReceiveListener {
	public void receivedJakeMessage(JakeMessage message);
}