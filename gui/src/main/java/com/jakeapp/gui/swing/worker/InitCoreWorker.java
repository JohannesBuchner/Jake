package com.jakeapp.gui.swing.worker;

import com.jakeapp.core.util.SpringThreadBroker;
import com.jakeapp.gui.swing.ICoreAccess;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.dialogs.generic.JSheet;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class InitCoreWorker extends SwingWorker<ICoreAccess, Void> implements IJakeTask {
	private static final Logger log = Logger.getLogger(InitCoreWorker.class);

	public InitCoreWorker() {
	}

	@Override protected ICoreAccess doInBackground() throws Exception {
		try {
			SpringThreadBroker.getInstance()
							.loadSpring(new String[]{"/com/jakeapp/core/applicationContext.xml",
											"/com/jakeapp/gui/swing/applicationContext-gui.xml"});

			return (ICoreAccess) SpringThreadBroker.getInstance().getBean("coreAccess");
		} catch (RuntimeException e) {
			SpringThreadBroker.stopInstance();
			e.printStackTrace();
			System.exit(1);
			return null;
		}
	}


	@Override
	public void done() {
		boolean success = false;
		try {
			// get core & authenticate
			Map<String, String> backendCredentials = new HashMap<String, String>();
			ICoreAccess core = get();
			core.authenticateOnBackend(backendCredentials);
			JakeMainApp.getInstance().setCore(core);
			JakeExecutor.removeTask(this);

			success = true;
		} catch (InterruptedException e) {
			ExceptionUtilities.showError(e);
		} catch (ExecutionException e) {
			ExceptionUtilities.showError(e);
		}

		if (!success) {
			String msg = "Failed to login to backend";
			JSheet.showMessageSheet(JakeMainView.getMainView().getFrame(), msg);
			log.warn(msg);
			ExceptionUtilities.showError(msg);
		}
	}
}