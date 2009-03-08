/*
 * JakeMock2App.java
 */

package com.jakeapp.gui.swing;

import com.jakeapp.core.util.SpringThreadBroker;
import com.jakeapp.gui.swing.callbacks.CoreChanged;
import com.jakeapp.gui.swing.helpers.ApplicationInstanceListener;
import com.jakeapp.gui.swing.helpers.ApplicationInstanceManager;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;
import com.jakeapp.gui.swing.helpers.Platform;
import org.apache.log4j.Logger;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The main class of the application.
 *
 * Configuration properties (Add to VM propertes)
 *  -Dcom.jakeapp.gui.ignoresingleinstance=true  	  	Disable single instance checking
 */
public class JakeMainApp extends SingleFrameApplication {
	private static final Logger log = Logger.getLogger(JakeMainApp.class);
	private static JakeMainApp app;
	private ICoreAccess core;

	private final List<CoreChanged> coreChanged = new ArrayList<CoreChanged>();

	public JakeMainApp() {
		app = this;

		if (System.getProperty("com.jakeapp.gui.ignoresingleinstance") == null) {
			if (!ApplicationInstanceManager.registerInstance()) {
				// instance already running.
				log.error("Another instance of Jake is already running.  Exiting.");
				System.exit(0);
			}

			ApplicationInstanceManager
							.setApplicationInstanceListener(new ApplicationInstanceListener() {

								public void newInstanceCreated() {
									log.info("New Jake instance detected, showing current!");

									// User tried to load jake a second time.
									// so open first instane!
									JakeMainView.setMainWindowVisible(true);
								}
							});
		}
	}


	/**
	 * At startup create and show the main frame of the application. (Called
	 * from the Swing Application Framework)
	 */
	@Override
	protected void startup() {
		this.setMainFrame(new JFrame("Jake"));
		show(new JakeMainView(this));
	}

	/**
	 * This method is to initialize the specified window by injecting resources.
	 * Windows shown in our application come fully initialized from the GUI
	 * builder, so this additional configuration is not needed.
	 */
	@Override
	protected void configureWindow(java.awt.Window root) {
	}

	/**
	 * A convenient static getter for the application instance.
	 *
	 * @return the instance of JakeMock2App
	 */

	public static JakeMainApp getInstance() {
		return Application.getInstance(JakeMainApp.class);
	}

	/**
	 * Main method launching the application.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		startGui(args);
	}

	/**
	 * Starts the GUI!
	 *
	 * @param args
	 */
	private static void startGui(String[] args) {
		// we use the system laf everywhere except linux.
		// gtk is ugly here - we us nimbus (when available)
		try {
			if (Platform.isWin() || Platform.isMac()) {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} else {
				// try to use nimbus (available starting j6u10)

				// FIXME: detect <= j6u10
				if (false) {
					try {
						UIManager.setLookAndFeel(
										"com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");

					} catch (Exception r) {

						// and stick to the system laf if nimbus fails (may be gtk on linux pre j6u10)
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					}
				} else {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				}
			}
		} catch (Exception e) {
			log.warn("LAF Exception: ", e);
		}

		if (Platform.isMac()) {
			// MacOSX specific: set menu name to 'Jake'
			// has to be called VERY early to succeed (prior to any gui stuff, later
			// calls will be ignored)
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Jake");

			// TODO: also not working as expected, see macwidgets issue 48
			//System.setProperty("apple.awt.draggableWindowBackground", "true");

			// TODO: broken? has no use on mac java 6?
			System.setProperty("apple.awt.brushMetalRounded", "true");
		} else if (Platform.isLin()) {
			Platform.fixWmClass();
		}


		launch(JakeMainApp.class, args);
	}


	/**
	 * Returns single instance of the App.
	 *
	 * @return
	 */
	public static JakeMainApp getApp() {
		return app;
	}

	/**
	 * The getCore is made static for convenience reasons.
	 * We only have one core, and one app.
	 *
	 * @return
	 */
	public static ICoreAccess getCore() {
		return getApp().core;
	}

	public void setCore(ICoreAccess core) {
		this.core = core;

		// shout out our core change!
		for (CoreChanged callback : coreChanged) {
			callback.coreChanged();
		}
	}


	public void saveQuit() {
		log.trace("Calling saveQuit");

		if (this.core != null) {
			this.core.backendLogOff();
			this.core = null;
		}
		SpringThreadBroker.stopInstance();
		System.exit(0);
	}

	/**
	 * Logs the current user out of the system, deselects current user.
	 */
	public static void logoutUser() {
		log.info("Logging out: " + JakeContext.getMsgService());

		if (JakeContext.getMsgService() == null) {
			log.warn("tried to sign out with no user active!");
		}

		try {
			JakeContext.getMsgService().logout();

		} catch (Exception e) {
			log.warn(e);
			ExceptionUtilities.showError(e);
		}
		JakeContext.setMsgService(null);
	}

	public void addCoreChangedListener(CoreChanged coreCallback) {
		coreChanged.add(coreCallback);
	}
}