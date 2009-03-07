package com.jakeapp.gui.swing.xcore;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.synchronization.change.ChangeListener;
import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;
import org.apache.log4j.Logger;

public class ProjectsChangeListener implements ChangeListener {
	private static final Logger log = Logger.getLogger(ProjectsChangeListener.class);

	public ProjectsChangeListener() {
	}

	@Override public INegotiationSuccessListener beganRequest(JakeObject jo) {
		log.debug("beganRequest for " + jo);
		return null;
	}

	@Override public void pullNegotiationDone(JakeObject jo) {
		log.debug("pullNegitiationDone: " + jo);
	}

	@Override public void pullDone(JakeObject jo) {
		log.debug("pullDone: " + jo);
	}

	@Override public void pullProgressUpdate(JakeObject jo, Status status,
					double progress) {
		log.debug("pullProgressUpdate: " + jo + ", status: " + status + ", progress: " + progress);
	}

	@Override
	public void pullFailed(JakeObject jo, Exception reason) {
		log.debug("pullFailed: " + jo, reason);
	}
}