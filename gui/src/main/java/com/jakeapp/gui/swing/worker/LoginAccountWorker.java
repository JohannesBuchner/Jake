package com.jakeapp.gui.swing.worker;

import com.jakeapp.core.services.MsgService;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeStatusBar;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;
import org.apache.log4j.Logger;

/**
 * @author: studpete
 */

/**
 * Private inner worker for account registration.
 */
public class LoginAccountWorker extends SwingWorkerWithAvailableLaterObject<Boolean> {
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
	protected AvailableLaterObject<Boolean> calculateFunction() {
		JakeStatusBar.showMessage("Logging in...", 1);
		return JakeMainApp.getCore().login(msg, password, rememberPassword);
	}

	@Override
	protected void done() {
		try {			
			if (!this.get()) {
				log.warn("Wrong User/Password");
				JakeStatusBar.showMessage("Logging in unsuccessful: Wrong User/Password.", 100);
			} else {
				JakeStatusBar.showProgressAnimation(false);
				JakeStatusBar.showMessage("Successfully logged in");
				//JakeStatusBar.updateMessage();
			}
		} catch (Exception e) {
			log.warn("Login failed: " + e);
			ExceptionUtilities.showError(e);
		}
	}
}