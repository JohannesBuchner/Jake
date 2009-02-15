/**
 * 
 */
package com.jakeapp.gui.console;

import org.apache.log4j.Logger;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;

public class PrintingNegotiationSuccessListener implements
		INegotiationSuccessListener {
	private final static Logger log = Logger.getLogger(PrintingNegotiationSuccessListener.class);

	private JakeObject jo;

	public PrintingNegotiationSuccessListener(JakeObject jo) {
		this.jo = jo;
	}

	@Override
	public void failed(Throwable reason) {
		log.warn(jo + " failed: " + reason.getMessage());
	}

	@Override
	public void succeeded(IFileTransfer ft) {
		log.warn(jo + " succeeded");
	}

}