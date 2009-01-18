package com.jakeapp.gui.swing.helpers;

import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.*;
import com.jakeapp.gui.swing.actions.abstracts.ProjectAction;
import net.roydesign.app.AboutJMenuItem;
import net.roydesign.app.Application;
import net.roydesign.app.QuitJMenuItem;
import net.roydesign.mac.MRJAdapter;
import net.roydesign.ui.StandardMacAboutFrame;
import org.apache.log4j.Logger;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.plaf.SeparatorUI;

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
	private static final Logger log = Logger.getLogger(JakeMenuBar.class);

	public JakeMenuBar() {
		super();

		// Get the application instance
		Application app = Application.getInstance();
		org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(com.jakeapp.gui.swing.JakeMainApp.class).getContext().getResourceMap(JakeMainView.class);

		
		/****************************** Notes *******************************/
		final JMenu notesMenu = new JMenu();
		notesMenu.setText(resourceMap.getString("notesMenu.text"));
		notesMenu.setName(resourceMap.getString("notesMenu.name"));
		
		notesMenu.add(new JMenuItem(new NewNoteAction()));
		notesMenu.addSeparator();
		notesMenu.add(new JMenuItem(new DeleteNoteAction()));
		notesMenu.add(new JMenuItem(new CommitNoteAction()));
		notesMenu.addSeparator();
		notesMenu.add(new JMenuItem(new SoftlockNoteAction()));
		
		this.add(notesMenu);
		
		/****************************** foo *********************************/
		

		final JMenu projectMenu = new JMenu();

		JMenuItem createProjectMenuItem = new JMenuItem();
		JMenuItem startStopJoinProjectMenuItem = new JMenuItem();
		JMenuItem renameProjectMenuItem = new JMenuItem();
		JMenuItem deleteOrRejectProjectMenuItem = new JMenuItem();
		JSeparator projectSeparator1 = new JSeparator();
		JMenuItem invitePeopleMenuItem = new JMenuItem();
		JMenuItem createNoteMenuItem = new JMenuItem();
		JSeparator jSeparator13 = new JSeparator();
		JMenuItem signInOutMenuItem = new JMenuItem();
		JSeparator exitSeparator = new JSeparator();
		/*
		JMenu editMenu = new JMenu();
		JMenuItem cutMenuItem = new JMenuItem();
		JMenuItem copyMenuItem = new JMenuItem();
		JMenuItem selectAllMenuItem = new JMenuItem();
		JSeparator editMenuSeparator = new JSeparator();
		*/
		JMenu viewMenu = new JMenu();
		JMenuItem showProjectMenuItem = new JMenuItem();
		JMenuItem showFilesMenuItem = new JMenuItem();
		JMenuItem showNotesMenuItem = new JMenuItem();
		JMenu actionMenu = new JMenu();
		JMenuItem openMenuItem = new JMenuItem();
		JSeparator openMenuSeparator = new JSeparator();
		JMenuItem announceMenuItem = new JMenuItem();
		JMenuItem pullMenuItem = new JMenuItem();
		JMenuItem fixFilenameMenuItem = new JMenuItem();
		JSeparator actionNetworkSeparator = new JSeparator();
		JMenuItem deleteMenuItem = new JMenuItem();
		JMenuItem renameMenuItem = new JMenuItem();
		JSeparator actionFileSeparator = new JSeparator();
		JMenuItem showHideInspectorMenuItem = new JMenuItem();
		JSeparator actionInspectorMenuItem = new JSeparator();
		JMenuItem importMenuItem = new JMenuItem();
		JMenuItem newFolderMenuItem = new JMenuItem();
		JSeparator actionImportSeparator = new JSeparator();
		JMenuItem lockMenuItem = new JMenuItem();
		JMenuItem lockWithMessageMenuItem = new JMenuItem();
		JMenu helpMenu = new JMenu();
		JMenuItem visitWebsiteMenuItem = new JMenuItem();

		projectMenu.setText(resourceMap.getString("projectMenu.text")); // NOI18N
		projectMenu.setName("projectMenu"); // NOI18N

		createProjectMenuItem.setAction(new CreateProjectAction(true));
		projectMenu.add(createProjectMenuItem);

		startStopJoinProjectMenuItem.setAction(new StartStopOrJoinProjectAction()); // NOI18N
		projectMenu.add(startStopJoinProjectMenuItem);

		renameProjectMenuItem.setAction(new RenameProjectAction()); // NOI18N		
		projectMenu.add(renameProjectMenuItem);

		deleteOrRejectProjectMenuItem.setAction(new DeleteOrRejectProjectAction()); // NOI18N
		projectMenu.add(deleteOrRejectProjectMenuItem);

		projectSeparator1.setName("projectSeparator1"); // NOI18N
		projectMenu.add(projectSeparator1);

		invitePeopleMenuItem.setAction(new InvitePeopleAction(true));

		projectMenu.add(invitePeopleMenuItem);

		createNoteMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.META_MASK));
		createNoteMenuItem.setText(resourceMap.getString("createNoteMenuItem.text")); // NOI18N
		createNoteMenuItem.setName("createNoteMenuItem"); // NOI18N
		projectMenu.add(createNoteMenuItem);

		jSeparator13.setName("jSeparator13"); // NOI18N
		projectMenu.add(jSeparator13);

		signInOutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.META_MASK));
		signInOutMenuItem.setText(resourceMap.getString("signInOutMenuItem.text")); // NOI18N
		signInOutMenuItem.setName("signInOutMenuItem"); // NOI18N
		projectMenu.add(signInOutMenuItem);

		exitSeparator.setName("exitSeparator"); // NOI18N
		projectMenu.add(exitSeparator);

		this.add(projectMenu);

		/*
		editMenu.setText(resourceMap.getString("editMenu.text")); // NOI18N
		editMenu.setName("editMenu"); // NOI18N

		cutMenuItem.setText(resourceMap.getString("cutMenuItem.text")); // NOI18N
		cutMenuItem.setName("cutMenuItem"); // NOI18N
		editMenu.add(cutMenuItem);

		copyMenuItem.setText(resourceMap.getString("copyMenuItem.text")); // NOI18N
		copyMenuItem.setName("copyMenuItem"); // NOI18N
		editMenu.add(copyMenuItem);

		selectAllMenuItem.setText(resourceMap.getString("selectAllMenuItem.text")); // NOI18N
		selectAllMenuItem.setName("selectAllMenuItem"); // NOI18N
		editMenu.add(selectAllMenuItem);

		
		// Do the same thing for the Preferences and Quit items
		PreferencesJMenuItem preferencesMenuItem = app.getPreferencesJMenuItem();
		preferencesMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JSheet.showMessageSheet(JakeMainView.getMainView().getFrame(), "Preferences");
			}
		});
		if (!PreferencesJMenuItem.isAutomaticallyPresent()) {
			editMenu.add(editMenuSeparator);
			editMenu.add(preferencesMenuItem);
		}

		this.add(editMenu);
		*/


		viewMenu.setText(resourceMap.getString("viewMenu.text")); // NOI18N
		viewMenu.setName("viewMenu"); // NOI18N

		showProjectMenuItem.setAction(new SwitchNewsProjectContextAction());
		viewMenu.add(showProjectMenuItem);

		showFilesMenuItem.setAction(new SwitchFilesProjectContextAction());
		viewMenu.add(showFilesMenuItem);

		showNotesMenuItem.setAction(new SwitchNotesProjectContextAction());
		viewMenu.add(showNotesMenuItem);

		this.add(viewMenu);

		actionMenu.setText(resourceMap.getString("actionMenu.text")); // NOI18N
		actionMenu.setName("actionMenu"); // NOI18N

		openMenuItem.setText(resourceMap.getString("openMenuItem.text")); // NOI18N
		openMenuItem.setName("openMenuItem"); // NOI18N
		actionMenu.add(openMenuItem);

		openMenuSeparator.setName("openMenuSeparator"); // NOI18N
		actionMenu.add(openMenuSeparator);

		announceMenuItem.setText(resourceMap.getString("announceMenuItem.text")); // NOI18N
		announceMenuItem.setName("announceMenuItem"); // NOI18N
		actionMenu.add(announceMenuItem);

		pullMenuItem.setText(resourceMap.getString("pullMenuItem.text")); // NOI18N
		pullMenuItem.setName("pullMenuItem"); // NOI18N
		actionMenu.add(pullMenuItem);

		fixFilenameMenuItem.setText(resourceMap.getString("fixFilenameMenuItem.text")); // NOI18N
		fixFilenameMenuItem.setName("fixFilenameMenuItem"); // NOI18N
		actionMenu.add(fixFilenameMenuItem);

		actionNetworkSeparator.setName("actionNetworkSeparator"); // NOI18N
		actionMenu.add(actionNetworkSeparator);

		deleteMenuItem.setText(resourceMap.getString("deleteMenuItem.text")); // NOI18N
		deleteMenuItem.setName("deleteMenuItem"); // NOI18N
		actionMenu.add(deleteMenuItem);

		renameMenuItem.setText(resourceMap.getString("renameMenuItem.text")); // NOI18N
		renameMenuItem.setName("renameMenuItem"); // NOI18N
		actionMenu.add(renameMenuItem);

		actionFileSeparator.setName("actionFileSeparator"); // NOI18N
		actionMenu.add(actionFileSeparator);

		showHideInspectorMenuItem.setText(resourceMap.getString("showHideInspectorMenuItem.text")); // NOI18N
		showHideInspectorMenuItem.setName("showHideInspectorMenuItem"); // NOI18N
		actionMenu.add(showHideInspectorMenuItem);

		actionInspectorMenuItem.setName("actionInspectorMenuItem"); // NOI18N
		actionMenu.add(actionInspectorMenuItem);

		importMenuItem.setText(resourceMap.getString("importMenuItem.text")); // NOI18N
		importMenuItem.setName("importMenuItem"); // NOI18N
		actionMenu.add(importMenuItem);

		newFolderMenuItem.setText(resourceMap.getString("newFolderMenuItem.text")); // NOI18N
		newFolderMenuItem.setName("newFolderMenuItem"); // NOI18N
		actionMenu.add(newFolderMenuItem);

		actionImportSeparator.setName("actionImportSeparator"); // NOI18N
		actionMenu.add(actionImportSeparator);

		lockMenuItem.setText(resourceMap.getString("lockMenuItem.text")); // NOI18N
		lockMenuItem.setName("lockMenuItem"); // NOI18N
		actionMenu.add(lockMenuItem);

		lockWithMessageMenuItem.setText(resourceMap.getString("lockWithMessageMenuItem.text")); // NOI18N
		lockWithMessageMenuItem.setName("lockWithMessageMenuItem"); // NOI18N
		actionMenu.add(lockWithMessageMenuItem);

		this.add(actionMenu);

		helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
		helpMenu.setName("helpMenu"); // NOI18N

		javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(com.jakeapp.gui.swing.JakeMainApp.class).getContext().getActionMap(JakeMainView.class, JakeMainView.getMainView());
		visitWebsiteMenuItem.setAction(actionMap.get("showJakeWebsite")); // NOI18N
		visitWebsiteMenuItem.setText(resourceMap.getString("visitWebsiteMenuItem.text")); // NOI18N
		visitWebsiteMenuItem.setName("visitWebsiteMenuItem"); // NOI18N
		helpMenu.add(visitWebsiteMenuItem);


		// Get an About item instance.
		AboutJMenuItem aboutMenuItem = app.getAboutJMenuItem();
		//aboutMenuItem.setAction(actionMap.get("showAboutBox"));
		aboutMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StandardMacAboutFrame f =
						  new StandardMacAboutFrame(AppUtilities.getAppName(), AppUtilities.getAppVersion());
				f.setApplicationIcon(UIManager.getIcon("OptionPane.informationIcon"));
				f.setBuildVersion("001");
				f.setCopyright("Copyright 2007-2009, Best ASE Team TU Vienna");
				f.setCredits("<html><body>Jake<br>" +
						  "<a href=\"http://jakeapp.com/\">jakeapp.com</a><br>" +
						  "<br>We are proud to present you Jake." +
						  "<b></b><br>" +
						  "Send your Feedback to: " +
						  "<a href=\"mailto:jake@jakeapp.com\">jake@jakeapp.com</a>" +
						  "</body></html>", "text/html");
				f.setHyperlinkListener(new HyperlinkListener() {
					public void hyperlinkUpdate(HyperlinkEvent e) {
						if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
							try {
								Desktop.getDesktop().browse(new URI(e.getURL().toString()));
							}
							catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					}
				});
				f.setVisible(true);
			}
		});
		// If the menu is not already present because it's provided by
		// the OS (like on Mac OS X), then append it to our menu
		if (!AboutJMenuItem.isAutomaticallyPresent())
			helpMenu.add(aboutMenuItem);

		this.add(helpMenu);


		QuitJMenuItem quitMenuItem = app.getQuitJMenuItem();
		quitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quit();
			}
		});
		if (!QuitJMenuItem.isAutomaticallyPresent())
			projectMenu.add(quitMenuItem);

		// menu actions
		ProjectAction startStopProjectAction = new StartStopProjectAction();


		// set menu actions


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