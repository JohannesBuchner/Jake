/*
 * JakeMock2App.java
 */

package com.jakeapp.gui.swing;

import com.jakeapp.core.util.SpringThreadBroker;
import com.jakeapp.gui.swing.callbacks.CoreChangedCallback;
import com.jakeapp.gui.swing.globals.JakeContext;
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
 * <p/>
 * Configuration properties (Add to VM properties)
 * -Dcom.jakeapp.gui.ignoresingleinstance=true  	  	Disable single instance checking
 */
public class JakeMainApp extends SingleFrameApplication {
	private static final String ALLOW_MULTIPLE_INSTANCES_PROPERTY = "com.jakeapp.gui.ignoresingleinstance";

	private static final Logger log = Logger.getLogger(JakeMainApp.class);

	private static JakeMainApp app;

	private ICoreAccess core;

	private final List<CoreChangedCallback> coreChanged = new ArrayList<CoreChangedCallback>();

	public JakeMainApp() {
		app = this;

		if (System.getProperty(ALLOW_MULTIPLE_INSTANCES_PROPERTY) == null) {
			log.debug("checking that this is the only instances");
			log.info("You can allow multiple instances by setting -D"
					+ ALLOW_MULTIPLE_INSTANCES_PROPERTY);
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

		lookAndFeelSetup(args);

		if (Platform.isMac()) {
			macMenuSetup();
		} else if (Platform.isLin()) {
			Platform.fixWmClass();
		}

		launch(JakeMainApp.class, args);
	}


	private static void macMenuSetup() {
		// MacOSX specific: set menu name to 'Jake'
		// has to be called VERY early to succeed (prior to any gui stuff, later
		// calls will be ignored)
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Jake");
		installMacScrollbars();
	}
	

	private static void lookAndFeelSetup(String[] args) {
		/**
		 * Laf detection code - get the best for every system!
		 */
		try {

			// find all available lafs
			UIManager.LookAndFeelInfo nimbusLaf = null;
			UIManager.LookAndFeelInfo gtkLaf = null;
			StringBuilder availableLafs = new StringBuilder();
			for (UIManager.LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
				availableLafs.append(laf.getName()).append(", ");
				if (laf.getName().toLowerCase().contains("gtk")) {
					gtkLaf = laf;
				} else if (laf.getName().toLowerCase().contains("nimbus")) {
					nimbusLaf = laf;
				}
			}
			log.info("Found LAFs: " + availableLafs);

			// override default laf if argument is given
			// fixme: proper argument check!
			if (false && args.length > 0 && nimbusLaf != null) {
				UIManager.setLookAndFeel(nimbusLaf.getClassName());
				installMacScrollbars();
			} else {

				/*// on windows & mac, use the native laf
				if (Platform.isWin() || Platform.isMac()) {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} else {
					// linux - it's a bit more tricky here
					try {
						if (gtkLaf != null) {
							UIManager.setLookAndFeel(gtkLaf.getClassName());
						} else if (nimbusLaf != null) {
							UIManager.setLookAndFeel(nimbusLaf.getClassName());
						}
					} catch (Exception r) {
						log.warn("Error setting laf: " + r.getMessage());
					}
				}*/
			}
		} catch (Exception e) {
			log.warn("LAF Exception: ", e);
		}
	}

	private static void installMacScrollbars() {// install the cool scrollbars!
		UIManager.put("ScrollBarUI",
				com.explodingpixels.macwidgets.plaf.IAppScrollBarUI.class.getName());
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
		for (CoreChangedCallback callback : coreChanged) {
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

	// there is no remove because the core is never unloaded...
	public void addCoreChangedListener(CoreChangedCallback coreCallback) {
		coreChanged.add(coreCallback);
	}
}