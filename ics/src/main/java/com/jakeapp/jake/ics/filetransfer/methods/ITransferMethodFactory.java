package com.jakeapp.jake.ics.filetransfer.methods;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.msgservice.IMsgService;

/**
 * Each TransferMethod (XMPP inband, sockets, jingle, etc...) has to provide
 * this factory to allow fallback
 * 
 * @author johannes
 */
public interface ITransferMethodFactory {

	/**
	 * 
	 * @param negotiationService
	 * @return
	 * @throws NotLoggedInException
	 */
	public ITransferMethod getTransferMethod(IMsgService negotiationService, UserId user) throws NotLoggedInException;
}
