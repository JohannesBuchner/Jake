/*
 * JakeMock2App.java
 */

package com.jakeapp.gui.swing;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.services.MsgService;
import com.jakeapp.core.util.SpringThreadBroker;
import com.jakeapp.gui.swing.callbacks.CoreChanged;
import com.jakeapp.gui.swing.callbacks.MsgServiceChanged;
import com.jakeapp.gui.swing.callbacks.ProjectSelectionChanged;
import com.jakeapp.gui.swing.dialogs.SplashWindow;
import com.jakeapp.gui.swing.helpers.ApplicationInstanceListener;
import com.jakeapp.gui.swing.helpers.ApplicationInstanceManager;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;
import com.jakeapp.gui.swing.helpers.Platform;
import org.apache.log4j.Logger;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * The main class of the application.
 */
public class JakeMainApp extends SingleFrameApplication implements ProjectSelectionChanged {

	private static final Logger log = Logger.getLogger(JakeMainApp.class);

	private static JakeMainApp app;

	private ICoreAccess core;

	private Project project = null;

	// this is the message service the user chooses.
	// only one per application.
	private MsgService msgService = null;

	private final List<ProjectSelectionChanged> projectSelectionChanged =
					new LinkedList<ProjectSelectionChanged>();
	private final List<MsgServiceChanged> msgServiceChanged =
					new ArrayList<MsgServiceChanged>();
	private final List<CoreChanged> coreChanged = new ArrayList<CoreChanged>();

	public JakeMainApp() {
		app = this;

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

		// show splash
		// SplashWindow.splash(new
		// ImageIcon(Toolkit.getDefaultToolkit().getImage(
		// getClass().getResource("/icons/jakeapp-large.png"))).getImage());

		//initCore();
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
                }else {
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
		for(CoreChanged callback: coreChanged) {
			callback.coreChanged();
		}
	}

	/**
	 * Convenience call to get the main gui frame faster.
	 *
	 * @return
	 */
	public static JFrame getFrame() {
		return JakeMainView.getMainView().getFrame();
	}

	public static Project getProject() {
		return getApp().project;
	}


	public void setProject(Project project) {

		if (this.project != project) {
			this.project = project;

			// fire the event and relay to all items/components/actions/panels
			fireProjectSelectionChanged();
		}
	}

	/**
	 * Fires a project selection change event, calling all registered members of
	 * the event.
	 */
	private void fireProjectSelectionChanged() {
		for (ProjectSelectionChanged psc : projectSelectionChanged) {
			try {
				psc.setProject(getProject());
			} catch (RuntimeException ex) {
				log.error("Catched an exception while setting the new project: ", ex);
				ExceptionUtilities.showError(ex);
			}
		}
	}

	public void addProjectSelectionChangedListener(ProjectSelectionChanged psc) {
		projectSelectionChanged.add(psc);
	}

	public void removeProjectSelectionChangedListener(ProjectSelectionChanged psc) {
		if (projectSelectionChanged.contains(psc)) {
			projectSelectionChanged.remove(psc);
		}
	}

	/**
	 * Fires when a new User is selected.
	 */
	private void fireMsgServiceChanged() {
		log.info("Fire Message Service Changed: " + getMsgService());
		for (MsgServiceChanged msc : msgServiceChanged) {
			try {
				msc.msgServiceChanged(getMsgService());
			} catch (RuntimeException ex) {
				log.error("Catched an exception while setting message service: ", ex);
				ExceptionUtilities.showError(ex);
			}
		}
	}

	public void addMsgServiceChangedListener(MsgServiceChanged msc) {
		msgServiceChanged.add(msc);
	}

	public void removeMsgServiceChangedListener(MsgServiceChanged msc) {
		msgServiceChanged.remove(msc);
	}


	public void saveQuit() {
		log.debug("Calling saveQuit");

		if (this.core != null) {
			this.core.backendLogOff();
			this.core = null;
		}
		SpringThreadBroker.stopInstance();
		System.exit(0);
	}

	/**
	 * Set a new Msg Service.
	 * @param msg
	 */
	public static void setMsgService(MsgService msg) {
		getApp().msgService = msg;

		getApp().fireMsgServiceChanged();
	}

	/**
	 * Returns the global Message Service (if a user was chosen)
	 *
	 * @return
	 */
	public static MsgService getMsgService() {
		return getApp().msgService;
	}

	public SplashWindow getSplashFrame() {
		return SplashWindow.getInstance();
	}

	/**
	 * Logs the current user out of the system, deselects current user.
	 */
	public static void logoutUser() {
		log.info("Logging out: " + JakeMainApp.getMsgService());

		if (getMsgService() == null) {
			log.warn("tried to sign out with no user active!");
		}

		try {
			getMsgService().logout();

		} catch (Exception e) {
			log.warn(e);
			ExceptionUtilities.showError(e);
		}
		setMsgService(null);
	}

	/**
	 * Returns the one and only project user that is within app (and project) context.
	 *
	 * @return current user
	 */
	public static UserId getCurrentUser() {
		return getProject().getMessageService().getUserId();
	}

	public static boolean isCoreInitialized() {
		return getCore() != null;
	}

	public void addCoreChangedListener(CoreChanged coreCallback) {
		coreChanged.add(coreCallback);
	}
}