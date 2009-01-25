package com.jakeapp.gui.swing.worker;

import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.core.util.availablelater.AvailableErrorObject;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.synchronization.exceptions.SyncException;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;

public class AnnounceJakeObjectWorker extends SwingWorkerWithAvailableLaterObject<Void> {
	private JakeObject jo;

	public AnnounceJakeObjectWorker(JakeObject jo) {
		this.jo = jo;
	}

	@Override
	protected AvailableLaterObject<Void> calculateFunction() {
		// TODO: Commit Messages?
		try {
			return JakeMainApp.getCore().announceJakeObject(jo, "");
		} catch (SyncException e) {
			return new AvailableErrorObject<Void>(e);
		}
	}

	@Override
	public void error(Exception e) {
		ExceptionUtilities.showError(e);
	}
}
