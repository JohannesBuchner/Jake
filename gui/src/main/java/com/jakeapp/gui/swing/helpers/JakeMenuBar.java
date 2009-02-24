package com.jakeapp.gui.swing.helpers;

import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.*;
import com.jakeapp.gui.swing.panels.FilePanel;
import net.roydesign.app.AboutJMenuItem;
import net.roydesign.app.Application;
import net.roydesign.app.QuitJMenuItem;
import net.roydesign.mac.MRJAdapter;
import net.roydesign.ui.StandardMacAboutFrame;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.XMPPConnection;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

/**
 * The Main Jake Menu Bar.
 *
 * @author: studpete
 */
public class JakeMenuBar extends JMenuBar {
	// TODO: refactor that shit !!!
	private static final Logger log = Logger.getLogger(JakeMenuBar.class);

	public JakeMenuBar() {
		super();

		// Get the application instance
		Application app = Application.getInstance();
		org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application
						.Application.getInstance(com.jakeapp.gui.swing.JakeMainApp.class)
						.getContext().getResourceMap(JakeMainView.class);


		/****************************** Project *********************************/
		final JMenu projectMenu = new JMenu();
		projectMenu.setText(resourceMap.getString("projectMenu.text"));

		projectMenu.add(new JMenuItem(new CreateProjectAction(true)));
		projectMenu.add(new JMenuItem(new SyncProjectAction()));
		projectMenu.addSeparator();
		projectMenu.add(new JMenuItem(new StartStopProjectAction()));
		projectMenu.add(new JMenuItem(new RenameFileAction()));
		projectMenu.add(new JMenuItem(new DeleteFileAction()));
		projectMenu.addSeparator();
		projectMenu.add(new JMenuItem(new InvitePeopleAction(true)));
		projectMenu.addSeparator();
		//TODO: sign in action

		this.add(projectMenu);

		/****************************** View **********************************/
		final JMenu viewMenu = new JMenu();
		viewMenu.setText(resourceMap.getString("viewMenu.text"));

		viewMenu.add(new JMenuItem(new SwitchNewsProjectContextAction()));
		viewMenu.add(new JMenuItem(new SwitchFilesProjectContextAction()));
		viewMenu.add(new JMenuItem(new SwitchNotesProjectContextAction()));
		viewMenu.addSeparator();
		viewMenu.add(new JMenuItem(new SwitchLoginProjectContextAction()));

		this.add(viewMenu);

		/******************************* File *****************************/
		final JMenu fileMenu = new JMenu();
		fileMenu.setText(resourceMap.getString("fileMenu.text"));

		fileMenu.add(new JMenuItem(new OpenFileAction()));
		fileMenu.add(new JMenuItem(new ResolveConflictFileAction()));
		fileMenu.addSeparator();
		fileMenu.add(new JMenuItem(new AnnounceFileAction()));
		fileMenu.add(new JMenuItem(new PullFileAction()));
		fileMenu.addSeparator();
		fileMenu.add(new JMenuItem(new DeleteFileAction()));
		fileMenu.add(new JMenuItem(new RenameFileAction()));
		fileMenu.addSeparator();
		fileMenu.add(new JMenuItem(new ImportFileAction()));
		fileMenu.add(new JMenuItem(new CreateFolderFileAction()));
		fileMenu.addSeparator();
		fileMenu.add(new JMenuItem(new LockFileAction()));

		this.add(fileMenu);

		/****************************** Notes *******************************/
		final JMenu notesMenu = new JMenu();
		notesMenu.setText(resourceMap.getString("notesMenu.text"));

		notesMenu.add(new JMenuItem(new CreateNoteAction()));
		notesMenu.addSeparator();
		notesMenu.add(new JMenuItem(new DeleteNoteAction()));
		notesMenu.add(new JMenuItem(new CommitNoteAction()));
		notesMenu.addSeparator();
		notesMenu.add(new JMenuItem(new SoftlockNoteAction()));

		this.add(notesMenu);


		/******************************** Help **************************/
		JMenu helpMenu = new JMenu();
		helpMenu.setText(resourceMap.getString("helpMenu.text"));

		JMenuItem visitWebsiteMenuItem = new JMenuItem();
		javax.swing.ActionMap actionMap = org.jdesktop.application.Application
						.getInstance(com.jakeapp.gui.swing.JakeMainApp.class).getContext()
						.getActionMap(JakeMainView.class, JakeMainView.getMainView());
		visitWebsiteMenuItem.setAction(actionMap.get("showJakeWebsite")); // NOI18N
		visitWebsiteMenuItem
						.setText(resourceMap.getString("visitWebsiteMenuItem.text")); // NOI18N
		visitWebsiteMenuItem.setName("visitWebsiteMenuItem"); // NOI18N
		helpMenu.add(visitWebsiteMenuItem);


		// Get an About item instance.
		AboutJMenuItem aboutMenuItem = app.getAboutJMenuItem();
		//aboutMenuItem.setAction(actionMap.get("showAboutBox"));
		aboutMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StandardMacAboutFrame aboutFrame = new StandardMacAboutFrame(
								AppUtilities.getAppName(), AppUtilities.getAppVersion());
				aboutFrame
								.setApplicationIcon(UIManager.getIcon("OptionPane.informationIcon"));
				aboutFrame.setBuildVersion("001");
				aboutFrame.setCopyright("Copyright 2007-2009, Best ASE Team TU Vienna");
				aboutFrame.setCredits(
								"<html><body>Jake<br>" + "<a href=\"http://jakeapp.com/\">jakeapp.com</a><br>" + "<br>We are proud to present you Jake." + "<b></b><br>" + "Send your Feedback to: " + "<a href=\"mailto:jake@jakeapp.com\">jake@jakeapp.com</a>" + "</body></html>",
								"text/html");
				aboutFrame.setHyperlinkListener(new HyperlinkListener() {
					public void hyperlinkUpdate(HyperlinkEvent e) {
						if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
							try {
								Desktop.getDesktop().browse(new URI(e.getURL().toString()));
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					}
				});
				aboutFrame.setVisible(true);
			}
		});
		// If the menu is not already present because it's provided by
		// the OS (like on Mac OS X), then append it to our menu
		if (!AboutJMenuItem.isAutomaticallyPresent()) helpMenu.add(aboutMenuItem);

		this.add(helpMenu);


		/***************************** Debug *****************************/
		JMenu debugMenu = new JMenu();
		debugMenu.setText("Debug");

		JMenuItem reloadFileDebugItem = new JMenuItem("Reload File View");
		reloadFileDebugItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				log.info("called: reload file view");
				FilePanel.getInstance().resetFilter();
			}
		});
		debugMenu.add(reloadFileDebugItem);

		JMenuItem cureGetFilesDebugViewItem = new JMenuItem("core().getFiles()");
		cureGetFilesDebugViewItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				log.info("called: core().getFiles()");
				JakeMainApp.getCore().getFiles(JakeMainApp.getProject());
			}
		});
		debugMenu.add(cureGetFilesDebugViewItem);

		final JCheckBoxMenuItem xmppDebugViewItem = new JCheckBoxMenuItem("XMPP Debug Window (activates on new connection)");
		xmppDebugViewItem.setSelected(XMPPConnection.DEBUG_ENABLED);
		xmppDebugViewItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				log.info("XMPP Debug Window");
				XMPPConnection.DEBUG_ENABLED = true;
				xmppDebugViewItem.setSelected(XMPPConnection.DEBUG_ENABLED);
			}
		});
		debugMenu.add(xmppDebugViewItem);		


		this.add(debugMenu);

		QuitJMenuItem quitMenuItem = app.getQuitJMenuItem();
		quitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quit();
			}
		});
		if (!QuitJMenuItem.isAutomaticallyPresent()) projectMenu.addSeparator();
		projectMenu.add(quitMenuItem);

		// add special mac os event listener
		if (Platform.isMac()) {
			MRJAdapter.addReopenApplicationListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					log.debug("reopen");
					//TODO: reshow window
				}
			});

			MRJAdapter.addOpenDocumentListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					log.debug("openDocument");
					// TODO
				}
			});

			// TODO: does not work?
			Application.getInstance().setFramelessJMenuBar(this);
		}
	}


	private void quit() {
		log.debug("calling quit from MenuBar");
		JakeMainView.getMainView().quit();
	}
}