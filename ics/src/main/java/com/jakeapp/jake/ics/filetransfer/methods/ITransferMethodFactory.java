package com.jakeapp.jake.ics.filetransfer.methods;

import com.jakeapp.jake.ics.filetransfer.INegotiationSuccessListener;
import com.jakeapp.jake.ics.msgservice.IMsgService;

/**
 * Each TransferMethod (XMPP inband, sockets, jingle, etc...) has to provide
 * this factory to allow fallback
 * 
 * @author johannes
 */
public interface ITransferMethodFactory {

	public ITransferMethod getTransferMethod(IMsgService negotiationService,
			INegotiationSuccessListener negListener);
}
