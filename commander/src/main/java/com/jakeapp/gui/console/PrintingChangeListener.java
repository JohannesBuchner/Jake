/**
 * 
 */
package com.jakeapp.gui.console;

import org.apache.log4j.Logger;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.synchronization.change.ChangeListener;
import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;

public class PrintingChangeListener implements ChangeListener {
	private final static Logger log = Logger.getLogger(PrintingChangeListener.class);

	@Override
	public INegotiationSuccessListener beganRequest(JakeObject jo) {
		log.debug(jo.toString());
		return new PrintingNegotiationSuccessListener(jo);
	}

	@Override
	public void pullDone(JakeObject jo) {
		log.info(jo);
	}

	@Override
	public void pullNegotiationDone(JakeObject jo) {
		log.info(jo);
	}

	@Override
	public void pullProgressUpdate(JakeObject jo, Status status, double progress) {
		log.info(jo + " - " + progress + " - " + status);
	}

	@Override public void onlineStatusChanged(Project p) {
	}

	@Override public void syncStateChanged(Project p, SyncState state) {
	}

	@Override
	public void pullFailed(JakeObject jo, Throwable reason) {
		log.info(jo,reason);
	}

}