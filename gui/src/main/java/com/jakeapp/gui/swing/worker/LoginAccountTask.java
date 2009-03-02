package com.jakeapp.gui.swing.worker;

import com.jakeapp.core.services.MsgService;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeStatusBar;
import org.apache.log4j.Logger;

/**
 * @author: studpete
 */

/**
 * Private inner worker for account registration.
 */
public class LoginAccountTask extends AbstractTask<Boolean> {
	private static final Logger log = Logger.getLogger(LoginAccountTask.class);

	private MsgService msg;
	private String password;
	private boolean rememberPassword;

	public LoginAccountTask(MsgService msg) {
		this(msg, null, false);
	}

	public LoginAccountTask(MsgService msg, String password,
					boolean rememberPassword) {
		this.msg = msg;
		this.password = password;
		this.rememberPassword = rememberPassword;

		log.debug("Login Account Worker: " + msg + " pw: " + password + " remember: " + rememberPassword);
	}

	@Override
	protected AvailableLaterObject<Boolean> calculateFunction() {
		//JakeStatusBar.showMessage("Logging in...", 1);
		return JakeMainApp.getCore().login(msg, password, rememberPassword);
	}

	@Override
	protected void done() {
		super.done();
		
		//JakeStatusBar.showProgressAnimation(false);
		try {
			if (!this.get()) {
				log.warn("Wrong User/Password");
				JakeStatusBar.showMessage("Login unsuccessful: Wrong User/Password.", 100);
			} else {
				JakeStatusBar.showMessage("Successfully logged in");
				//JakeStatusBar.updateMessage();
			}
		} catch (Exception e) {
			log.warn("Login failed: " + e);
			//ExceptionUtilities.showError("Log In did not succeed.", e);
			JakeStatusBar.showMessage("Login unsuccessful: " + e.getMessage(), 100);
		}

		// update the statusbar!
		JakeStatusBar.getInstance().updateAll();
	}
}