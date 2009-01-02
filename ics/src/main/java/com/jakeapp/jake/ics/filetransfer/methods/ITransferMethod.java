package com.jakeapp.jake.ics.filetransfer.methods;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.filetransfer.INegotiationSuccessListener;
import com.jakeapp.jake.ics.msgservice.IMsgService;


public abstract class ITransferMethod {
	protected IMsgService msgService;
	protected INegotiationSuccessListener negListener;
	
	public ITransferMethod(IMsgService negotiationService,
			INegotiationSuccessListener negListener) {
		this.msgService = negotiationService;
		this.negListener = negListener;
		startServer();
	}
	
	/**
	 * Start the server so others can request files
	 */
	protected abstract void startServer();
	
	/**
	 * starts the negotiation and calls back the
	 * {@link INegotiationSuccessListener}
	 * 
	 * @param partner the user to connect to
	 */
	public abstract void startNegotiation(UserId partner);

	/**
	 * 
	 */
	public abstract void send();

	
}
