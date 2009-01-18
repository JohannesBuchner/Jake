package com.jakeapp.gui.swing.worker;

import com.jakeapp.core.services.MsgService;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;
import org.apache.log4j.Logger;

/**
 * @author: studpete
 */

/**
 * Private inner worker for account registration.
 */
public class LoginAccountWorker extends SwingWorkerWithAvailableLaterObject<Void> {
	private static final Logger log = Logger.getLogger(LoginAccountWorker.class);

	private MsgService msg;

	public LoginAccountWorker(MsgService msg) {
		this.msg = msg;

		log.info("Login Account Worker: " + msg);
	}

	@Override
	protected AvailableLaterObject<Void> calculateFunction() {
		try {
			// TODO: return object!
			msg.login();
		} catch (Exception e) {
			ExceptionUtilities.showError(e);
		}
		return null;
	}

	@Override
	protected void done() {
	}
}