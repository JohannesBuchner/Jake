/*
 * JakeMock2App.java
 */

package com.jakeapp.gui.swing;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.ProtocolType;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.services.MsgService;
import com.jakeapp.gui.swing.callbacks.MsgServiceChanged;
import com.jakeapp.gui.swing.callbacks.ProjectSelectionChanged;
import com.jakeapp.gui.swing.controls.GlassJFrame;
import com.jakeapp.gui.swing.dialogs.generic.JSheet;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;
import com.jakeapp.gui.swing.helpers.Platform;
import org.apache.log4j.Logger;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.swing.*;
import java.util.*;
import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * The main class of the application.
 */
public class JakeMainApp extends SingleFrameApplication implements
		  ProjectSelectionChanged {

	private static final Logger log = Logger.getLogger(JakeMainApp.class);

	private static JakeMainApp app;

	private ICoreAccess core;

	private Project project = null;

	// this is the message service the user chooses.
	// only one per application.
	private MsgService msgService = null;

	private List<ProjectSelectionChanged> projectSelectionChanged = new LinkedList<ProjectSelectionChanged>();
	private List<MsgServiceChanged> msgServiceChanged = new ArrayList<MsgServiceChanged>();

	public JakeMainApp() {
		this.app = this;


		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				  new String[]{"/com/jakeapp/core/applicationContext.xml"
							 /**
							  * Uncomment the following line to use the real
							  * implementation
							  */
							 , "/com/jakeapp/gui/swing/applicationContext-gui.xml"

							 /**
							  * Uncomment the following line to use peter/chris
							  * mock implementation
							  */
							 //	  , "/com/jakeapp/gui/swing/applicationContext-gui-mock.xml"


				  });
		setCore((ICoreAccess) applicationContext.getBean("coreAccess"));


		Map<String, String> backendCredentials = new HashMap<String, String>();
		backendCredentials.put("frontendUsername", "swingGui");
		backendCredentials.put("frontendPassword", "JKL@SJKLA**SDJ@MMSA");
		backendCredentials.put("backendHost", "127.0.0.1");
		backendCredentials.put("backendPort", "5000");
		backendCredentials.put("backendName", "defaultBackendServiceName");


		try {
			core.authenticateOnBackend(backendCredentials);


            /** DEBUG ***/
            ServiceCredentials sc1 = new ServiceCredentials("domdorn@jabber.fsinf.at",
                    "somepass");
            sc1.setUuid("02918516-062d-4028-9d7a-ed0393d0a90d");
            sc1.setProtocol(ProtocolType.XMPP);
            try {
                sc1.setServerAddress(Inet4Address.getLocalHost());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            sc1.setServerPort(9000);
            sc1.setEncryptionUsed(false);


            ServiceCredentials sc2 = new ServiceCredentials("pstein@jabber.fsinf.at",
                    "somepass");
            sc2.setUuid("48cce803-c878-46d3-b1e6-6165f75dcf88");
            sc2.setProtocol(ProtocolType.XMPP);
            try {
                sc2.setServerAddress(Inet4Address.getLocalHost());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            sc2.setServerPort(9000);
            sc2.setEncryptionUsed(false);


            ServiceCredentials sc3 = new ServiceCredentials("pstein@jabber.fsinf.at",
                    "somepass");
            sc3.setUuid("db9ac8a3-581f-42cc-ad81-2900eb74c390");
            sc3.setProtocol(ProtocolType.XMPP);
            try {
                sc3.setServerAddress(Inet4Address.getLocalHost());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            sc3.setServerPort(9000);
            sc3.setEncryptionUsed(false);


//            try {
//                getCore().addAccount(sc1);
//                getCore().addAccount(sc2);
//                getCore().addAccount(sc3);
////
////
//            }
//            catch (ProtocolNotSupportedException e) {
//                e.printStackTrace();
//            } catch (NotLoggedInException e) {
//                e.printStackTrace();
//            } catch (NetworkException e) {
//                e.printStackTrace();
//            }


		} catch (InvalidCredentialsException e) {
			/**
			 * TODO @ Peter: In Zukuenftigen versionen koennte es moeglich
			 * sein, dass GUI und Core entkoppelt sind und uebers netzwerk
			 * kommunizieren. Sofern das Gui sich nicht beim core
			 * authentifizieren kann (weil die credentials falsch sind),
			 * soll dem user eine box angezeigt werden, wo er dann spaeter
			 * auch die core-daten (host, port, serviceName etc.) aendern
			 * kann. Muss in dieser Phase des Projektes noch nicht gemacht
			 * werden, nur damit du's weisst.
			 *
			 * TODO: @ anonymous author: in english, please ;)
			 */
			String msg = "Failed to login to backend";
			JSheet.showMessageSheet(JakeMainView.getMainView().getFrame(), msg);
			log.warn(msg);
			ExceptionUtilities.showError(msg);
		}


		if (System.getProperty("com.jakeapp.gui.test.instantquit") != null) {
			saveQuit();
		}

	}

	/**
	 * At startup create and show the main frame of the application. (Called
	 * from the Swing Application Framework)
	 */
	@Override
	protected void startup() {
		this.setMainFrame(new GlassJFrame("Jake"));
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
	public static JakeMainApp getApplication() {
		return Application.getInstance(JakeMainApp.class);
	}

	/**
	 * Main method launching the application.
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
				try {
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
				} catch (Exception r) {
					// and stick to the system laf if nimbus fails (may be gtk on linux pre j6u10)
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
		log.info("Fire Message Service Changed");
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
		this.core.backendLogOff();
		this.core = null;

		System.exit(0);
	}

	/**
	 * Set a new Msg Service.
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
}
