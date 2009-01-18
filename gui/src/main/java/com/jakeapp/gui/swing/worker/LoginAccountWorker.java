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
	private String password;
	private boolean rememberPassword;

	public LoginAccountWorker(MsgService msg) {
		this(msg, null, false);
	}

	public LoginAccountWorker(MsgService msg, String password, boolean rememberPassword) {
		this.msg = msg;
		this.password = password;
		this.rememberPassword = rememberPassword;

		log.info("Login Account Worker: " + msg + " pw: " + password + " remember: " + rememberPassword);
	}

	@Override
	protected AvailableLaterObject<Void> calculateFunction() {
		try {
			// TODO: return object!
			if (password == null) {
				msg.login();
			} else {
				msg.login(password, rememberPassword);
			}
		} catch (Exception e) {
			log.warn("Login failed: " + e);
			ExceptionUtilities.showError(e);
		}
		return null;
	}

	@Override
	protected void done() {
	}
}