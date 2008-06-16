package com.doublesignal.sepm.jake.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXLoginPane;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.Filter;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.PatternFilter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

import com.doublesignal.sepm.jake.core.InvalidApplicationState;
import com.doublesignal.sepm.jake.core.dao.exceptions.NoSuchConfigOptionException;
import com.doublesignal.sepm.jake.core.domain.FileObject;
import com.doublesignal.sepm.jake.core.domain.JakeObject;
import com.doublesignal.sepm.jake.core.domain.NoteObject;
import com.doublesignal.sepm.jake.core.domain.Project;
import com.doublesignal.sepm.jake.core.services.IConflictCallback;
import com.doublesignal.sepm.jake.core.services.IJakeGuiAccess;
import com.doublesignal.sepm.jake.core.services.exceptions.LoginDataNotValidException;
import com.doublesignal.sepm.jake.core.services.exceptions.LoginDataRequiredException;
import com.doublesignal.sepm.jake.core.services.exceptions.LoginUseridNotValidException;
import com.doublesignal.sepm.jake.gui.helper.MultiColPatternFilter;
import com.doublesignal.sepm.jake.gui.i18n.ITranslationProvider;
import com.doublesignal.sepm.jake.gui.i18n.TranslatorFactory;
import com.doublesignal.sepm.jake.ics.exceptions.NetworkException;
import com.doublesignal.sepm.jake.ics.exceptions.NotLoggedInException;
import com.doublesignal.sepm.jake.ics.exceptions.OtherUserOfflineException;

/**
 * @author Peter Steinberger
 */
@SuppressWarnings("serial")
public class JakeGui extends JPanel implements Observer, IConflictCallback {
	
	private static final Logger log = Logger.getLogger(JakeGui.class);
	
	private static final ITranslationProvider translator = TranslatorFactory.getTranslator();
	
	private Project currentProject = null;
	private SearchMode searchMode = SearchMode.Both;
	
	private LinkedList<ActionListener> loginStatusListeners;

	public JTabbedPane getMainTabbedPane() {
		return mainTabbedPane;
	}

	/**
	 * Set a status message for 5 seconds.
	 * 
	 * @param msg
	 */
	public void setStatusMsg(String msg) {
		statusPanel.setStatusMsg(msg);
	}


	private IJakeGuiAccess jakeGuiAccess = null;

	public IJakeGuiAccess getJakeGuiAccess() {
		log.debug("Getting IJakeGuiAccess Object; is null? " + (jakeGuiAccess == null));
		return jakeGuiAccess;
	}

	public void setJakeGuiAccess(IJakeGuiAccess jakeGuiAccess) {
		log.debug("Setting IJakeGuiAccess Object; is null? " + (jakeGuiAccess == null));
		this.jakeGuiAccess = jakeGuiAccess;
	}

	public JakeGui(IJakeGuiAccess jakeGuiAccess, boolean justCreated) {
		loginStatusListeners = new LinkedList<ActionListener>();
		
		setJakeGuiAccess(jakeGuiAccess);
		setSystemProperties();
		setNativeLookAndFeel();
		log.debug("Initializing Components");
		initComponents();
		initSearchPopupMenu();
		registerUpdateObservers();
		updateAll();
		setStatusMsg(translator.get("JakeGuiStatusMessage"));
		log.debug("JakeGui loaded.");
		if(justCreated)
			peopleViewMenuItemActionPerformed(null);
		
		jakeGuiAccess.setConflictCallback(this);
	}
	
	public static void showSelectProjectDialog(String foldersuggestion) {
		new NewProject(foldersuggestion);
	}

	/**
	 * Set the system NATIVE look & feel.
	 */
	public static void setNativeLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			log.info("Look & Feel set to: " + UIManager.getLookAndFeel());
		} catch (Exception e) {
			// no critical error, can be ignored.
		}
	}
	/*

	public static ResolveConflictDialog createConflictDialog(Frame owner, 
			IJakeGuiAccess jakeGuiAccess, JakeObject jo) {
		if (jo instanceof FileObject) {
			FileObject f = (FileObject) jo;
			return new ResolveConflictDialog(owner, jakeGuiAccess, f);
		} else if (jo instanceof NoteObject) {
			NoteObject n = (NoteObject) jo;
			/* TODO:
			 * Maybe just show current content and offer to override with remote
			 * User can still copy it away (clipboard)
			 * */
			
			/* or, just ignore and do nothing... */ /*
			InvalidApplicationState.notImplemented();
		}else{
			InvalidApplicationState.shouldNotHappen();
		}
		return null;
	}
	 */
	public void conflictOccured(JakeObject jo) {
		if (jo instanceof FileObject) {
			FileObject f = (FileObject) jo;
			try {
				new ResolveConflictDialog(mainFrame, jakeGuiAccess, f);
			} catch (NotLoggedInException e) {
				UserDialogHelper.translatedError(mainFrame, "NotLoggedInException");
				return;
			} catch (OtherUserOfflineException e) {
				UserDialogHelper.translatedError(mainFrame, "OtherUserOfflineException");
				return;
			}
		} else if (jo instanceof NoteObject) {
			InvalidApplicationState.notImplemented();
		}else{
			InvalidApplicationState.shouldNotHappen();
		}
	}

	/**
	 * ** File Menu ****
	 */
	private void exitApplicationMenuItemActionPerformed(ActionEvent e) {
		log.debug("ExitApplication");
		System.exit(0);
	}

	private void propertiesMenuItemActionPerformed(ActionEvent e) {
		log.debug("Open Preferences Dialog");
		PreferencesDialog prefs = new PreferencesDialog(mainFrame, this);
		prefs.addUpdateListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updatePrefMenuItems();
			}
		});
		prefs.setVisible(true);
	}
	
	private void updatePrefMenuItems() {
		try {
			autoFilePushCheckBoxMenuItem.setSelected(Boolean.parseBoolean(jakeGuiAccess.getConfigOption("autoPush")));
			autoFilePullCheckBoxMenuItem.setSelected(Boolean.parseBoolean(jakeGuiAccess.getConfigOption("autoPull")));
			showOfflineMembersCheckBoxMenuItem.setSelected(Boolean.parseBoolean(jakeGuiAccess.getConfigOption("showOfflineProjectMembers")));
			autoRefreshCheckBoxMenuItem.setSelected(Boolean.parseBoolean(jakeGuiAccess.getConfigOption("autoRefresh")));
		} catch (NoSuchConfigOptionException e) {
			log.warn("cannot retrieve pref config options...");
		}
	}

	/**
	 * ** View Menu ****
	 */

	private void systemLogViewMenuItemActionPerformed(ActionEvent e) {
		log.debug("Open ViewLog Dialog");
		new ViewLogDialog(mainFrame , getJakeGuiAccess()).setVisible(true);
	}

	private void peopleViewMenuItemActionPerformed(ActionEvent e) {
		mainTabbedPane.setSelectedComponent(peoplePanel);
	}

	private void filesViewMenuItemActionPerformed(ActionEvent e) {
		mainTabbedPane.setSelectedComponent(filesPanel);
	}

	private void notesViewMenuItemActionPerformed(ActionEvent e) {
		mainTabbedPane.setSelectedComponent(notesPanel);
	}

	/** *** Network Menu **** */
	/**
	 * @author johannes
	 */
	private void signInNetworkMenuItemActionPerformed(ActionEvent e) {
		signInNetwork(null, null, true);
	}

	/**
	 * Tries to sign in using dialog or stored configuration. <p/>
	 * <p>
	 * If fillFromConfig is false and at least one parameter is null, a dialog
	 * is used.
	 * </p>
	 * Use like this: <code>signInNetwork(null, null, true);</code>
	 * 
	 * @param username
	 *            username if you know it, null otherwise
	 * @param password
	 *            password if you know it, null otherwise
	 * @param fillFromConfig
	 *            Should we first load the stored config values
	 * @author johannes
	 */
	public void signInNetwork(String username, String password, boolean fillFromConfig) {
		log.debug("Network signin procedure");

		
		final JXLoginPane loginPane = new JXLoginPane();
		
		try {
			loginPane.setUserName(jakeGuiAccess.getConfigOption("userid"));
		} catch (NoSuchConfigOptionException e2) {
			log.debug("Username not stored");
		}
		try {
			loginPane.setPassword(jakeGuiAccess.getConfigOption("password").toCharArray());
		} catch (NoSuchConfigOptionException e2) {
			log.debug("Password not stored");
		}
		loginPane.setVisible(true);
		final JXLoginPane.JXLoginFrame loginFrame = JXLoginPane.showLoginFrame(loginPane);

		loginFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				log.debug("window close listener, WindowEvent e: " + e.toString());
				log.debug("Login Dialog was: " + loginFrame.getStatus());
				if (loginFrame.getStatus() != JXLoginPane.Status.SUCCEEDED) {
					return;
				}
				String username = loginPane.getUserName();
				String password = new String(loginPane.getPassword());

				log.debug("Trying login with username: " + username + ", password: " + password);
				try {
					jakeGuiAccess.login(username, password);
					log.debug("Savign log in credentials...");
					jakeGuiAccess.setConfigOption("userid", username);
					jakeGuiAccess.setConfigOption("password", password);
					log.debug("Login was successful");
					notifyLoginListeners();
					updateAll();
					return;
				} catch (LoginDataRequiredException e1) {
					log.debug("LoginDataRequired");
					signInNetwork(username, password, false);
					return;
				} catch (LoginDataNotValidException e1) {
					log.debug("LoginDataNotValid: LoginDataNotValidException");
					UserDialogHelper.translatedError(mainFrame, "LoginDialogInvalidLoginTitle", "LoginDialogInvalidLoginText");
					signInNetwork(username, null, false);
					return;
				} catch (LoginUseridNotValidException e1) {
					log.debug("LoginUseridNotValid: LoginUseridNotValidException");
					UserDialogHelper.translatedError(mainFrame, "LoginDialogInvalidLoginTitle", "LoginDialogInvalidLoginText");
					loginPane.setErrorMessage(translator.get("LoginUseridNotValid"));
					signInNetwork(username, null, false);
					return;
				} catch (NetworkException e1) {
					log.debug("NetworkException");
					UserDialogHelper.error(mainFrame, translator.get("Error"),
							translator.get("NetworkError", e1
									.getLocalizedMessage()));
					return;
				}
			}
		});
		loginFrame.setVisible(true);
		return;
	}

	public void addLoginStatusListener(ActionListener l) {
		loginStatusListeners.add(l);
	}
	
	private void notifyLoginListeners() {
		for (ActionListener listener : loginStatusListeners) {
			listener.actionPerformed(new ActionEvent(this, 0, "updateLoginStatus"));
			updateAll();
		}
	}

	/**
	 * @author johannes
	 */
	private void signOutNetworkMenuItemActionPerformed(ActionEvent e) {
		signOutNetwork();
	}
	
	/**
	 * Sign out...
	 */
	public void signOutNetwork() {
		try {
			jakeGuiAccess.logout();
		} catch (NetworkException e1) {
			UserDialogHelper.inform(mainFrame, "", translator.get("NetworkError", e1.getMessage()),
					JOptionPane.ERROR_MESSAGE);
		}
		notifyLoginListeners();
	}
	

	/**
	 * ** Help Menu ****
	 */
	private void aboutHelpMenuItemActionPerformed(ActionEvent e) {
		JOptionPane.showMessageDialog(mainFrame, translator.get("AboutDialogText"));
	}

	
	/**
	 * ** Tool Bar ****
	 */
	private void newNoteButtonActionPerformed(ActionEvent e) {
		notesPanel.createNewNote();
	}

	/**
	 * ** People Context Menu ****
	 */

	private void showInfoPeopleMenuItemActionPerformed(ActionEvent e) {
		new ProjectMemberInfoDialog(mainFrame).setVisible(true);
	}

	private void newNoteProjectMenuItemActionPerformed(ActionEvent e) {
		new NoteEditorDialog(mainFrame).setVisible(true);
	}
	
	private void addProjectMemberMenutItemActionPerformed(
			ActionEvent e) {
		log.info("add Project Member.");
		AddProjectMemberDialog addProjectMemberDialog = new AddProjectMemberDialog(getMainFrame());
		addProjectMemberDialog.setVisible(true);

		if (addProjectMemberDialog.isSaved()) {
			jakeGuiAccess.addProjectMember(addProjectMemberDialog.getContent());
		}

		peoplePanel.updatePeopleUi();
		
	}

	private void setAutoPushMenuItemActionPerformed(ActionEvent e) {
		try {
			jakeGuiAccess.setConfigOption("autoPush", String.valueOf(!Boolean.parseBoolean(jakeGuiAccess.getConfigOption("autoPush"))));
		} catch (NoSuchConfigOptionException e1) {
			log.warn("cannot retrieve autoPush config option, setting it true");
			jakeGuiAccess.setConfigOption("autoPush", String.valueOf(true));
		}
	}
	
	private void setAutoPullMenuItemActionPerformed(ActionEvent e) {
		try {
			jakeGuiAccess.setConfigOption("autoPull", String.valueOf(!Boolean.parseBoolean(jakeGuiAccess.getConfigOption("autoPull"))));
		} catch (NoSuchConfigOptionException e1) {
			log.warn("cannot retrieve  autoPull config option, setting it true");
			jakeGuiAccess.setConfigOption("autoPull", String.valueOf(true));
		}
	}
	
	private void setShowOfflineProjectMembersActionPerformed(ActionEvent e) {
		try {
			jakeGuiAccess.setConfigOption("showOfflineProjectMembers", String.valueOf(!Boolean.parseBoolean(jakeGuiAccess.getConfigOption("showOfflineProjectMembers"))));
		} catch (NoSuchConfigOptionException e1) {
			log.warn("cannot retrieve showOfflineProjectMembers config option, setting it true");
			jakeGuiAccess.setConfigOption("showOfflineProjectMembers", String.valueOf(true));
		}
	}
	
	private void setAutoDatapoolRefreshActionPerformed(ActionEvent e) {
		try {
			jakeGuiAccess.setConfigOption("autoRefresh", String.valueOf(!Boolean.parseBoolean(jakeGuiAccess.getConfigOption("autoRefresh"))));
		} catch (NoSuchConfigOptionException e1) {
			log.warn("cannot retrieve autoRefresh config option, setting it true");
			jakeGuiAccess.setConfigOption("autoRefresh", String.valueOf(true));
		}
	}
	
	private void registerUpdateObservers() {
		notesPanel.getNotesUpdater().addObserver(this);
	}

	public void update(Observable o, Object arg) {
		log.info("Got Observer Message: Updating Titles");
		mainTabbedPane.setTitleAt(1, peoplePanel.getTitle());
		mainTabbedPane.setTitleAt(2, notesPanel.getTitle());
		mainTabbedPane.updateUI();
	}

	public void updateAll() {
		// TODO: update interface !?
		// peoplePanel.updateData();
		notesPanel.updateData();
	}

	/**
	 * Set some system properties to integrate the app better to mac os. These
	 * calls are *not* harmful to other operating systems...
	 */
	public static void setSystemProperties() {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Jake");
	}

	private void initComponents() {
		mainFrame = new JFrame();
		statusPanel = new StatusPanel(this);
		mainPanel = new JPanel();
		mainTabbedPane = new JTabbedPane();
		filesPanel = new FilesPanel(this);
		notesPanel = new NotesPanel(this);
		peoplePanel = new PeoplePanel(this);
		mainToolBar = new JToolBar();
		refreshDatapoolViewButton = new JButton();
		pushFileButton = new JButton();
		pullFilesButton = new JButton();
		newNoteButton = new JButton();
		searchSpacer = new JPanel(null);
		searchButton = new JButton();
		searchTextField = new JTextField();
		mainMenuBar = new JMenuBar();
		fileMenu = new JMenu();
		preferencesMenuItem = new JMenuItem();
		exitApplicationMenuItem = new JMenuItem();
		viewMenu = new JMenu();
		peopleViewMenuItem = new JMenuItem();
		filesViewMenuItem = new JMenuItem();
		notesViewMenuItem = new JMenuItem();
		systemLogViewMenuItem = new JMenuItem();
		networkMenu = new JMenu();
		signInNetworkMenuItem = new JMenuItem();
		signOutNetworkMenuItem = new JMenuItem();
		showOfflineMembersCheckBoxMenuItem = new JCheckBoxMenuItem();
		autoRefreshCheckBoxMenuItem = new JCheckBoxMenuItem();
		autoFilePushCheckBoxMenuItem = new JCheckBoxMenuItem();
		autoFilePullCheckBoxMenuItem = new JCheckBoxMenuItem();
		projectMenu = new JMenu();
		openProjectFolderMenuItem = new JMenuItem();
		refreshDatapoolViewProjectMenuItem = new JMenuItem();
		newNoteProjectMenuItem = new JMenuItem();
		addFileToProjectMenuItem = new JMenuItem();
		addFolderToProjectMenuItem = new JMenuItem();
		addProjectMemberMenuItem = new JMenuItem();
		helpMenu = new JMenu();
		aboutHelpMenuItem = new JMenuItem();
		peoplePopupMenu = new JPopupMenu();
		showInfoPeopleMenuItem = new JMenuItem();
		renamePeopleMenuItem = new JMenuItem();
		changeUserIdMenuItem = new JMenuItem();
		removePeopleMenuItem = new JMenuItem();
		searchPopupMenu = new JPopupMenu();

		// ======== frame1 ========
		{
			mainFrame.setTitle("Jake");

			mainFrame.setIconImage(new ImageIcon(getClass().getResource("/icons/Jake.png"))
					.getImage());
			Container frame1ContentPane = mainFrame.getContentPane();
			frame1ContentPane.setLayout(new BorderLayout());

			// ======== this ========
			{
				this.setLayout(new BorderLayout());

				this.add(statusPanel, BorderLayout.SOUTH);

				// ======== mainPanel ========
				{
					mainPanel.setLayout(new BorderLayout());

					// ======== mainTabbedPane ========
					{

						// ======== peoplePanel ========
						{
							
						mainTabbedPane.addTab("People", new ImageIcon(getClass().getResource(
								"/icons/people.png")), peoplePanel);

						mainTabbedPane.addTab("Notes", new ImageIcon(getClass().getResource(
								"/icons/notes.png")), notesPanel);

					}
					mainPanel.add(mainTabbedPane, BorderLayout.CENTER);

					// ======== mainToolBar ========
					{
						mainToolBar.setBorderPainted(false);
						mainToolBar.setRollover(true);
						
						// ---- refreshDatapoolViewButton ----
						refreshDatapoolViewButton.setHorizontalAlignment(SwingConstants.RIGHT);
						refreshDatapoolViewButton.setToolTipText(translator.get("MainToolbarToolTipSync"));
						refreshDatapoolViewButton.setIcon(new ImageIcon(getClass().getResource(
								"/icons/sync_project_folder.png")));

						refreshDatapoolViewButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent event) {
								jakeGuiAccess.syncWithProjectMembers();
								jakeGuiAccess.refreshFileObjects();
								filesPanel.updateUI(true);
							}
						});

						mainToolBar.add(refreshDatapoolViewButton);
						mainToolBar.addSeparator();

						// ---- pushFileButton ----
						pushFileButton.setIcon(new ImageIcon(getClass().getResource(
								"/icons/push.png")));
						pushFileButton.setToolTipText(translator.get("MainToolbarToolTipPushFile"));
						mainToolBar.add(pushFileButton);

						// ---- pullFilesButton ----
						pullFilesButton.setIcon(new ImageIcon(getClass().getResource(
								"/icons/pull.png")));
						pullFilesButton.setToolTipText(translator.get("MainToolbarToolTipPull"));
						mainToolBar.add(pullFilesButton);

						// ---- LockFileToggleButton ----
//						LockFileToggleButton.setIcon(new ImageIcon(getClass().getResource(
//								"/icons/lock.png")));
//						LockFileToggleButton.setToolTipText("Lock File...");
//						mainToolBar.add(LockFileToggleButton);
						mainToolBar.addSeparator();

						// ---- newNoteButton ----
						newNoteButton.setToolTipText("New Note");
						newNoteButton.setIcon(new ImageIcon(getClass().getResource(
								"/icons/notes-new.png")));
						newNoteButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								newNoteButtonActionPerformed(e);
							}
						});
						mainToolBar.add(newNoteButton);
						mainToolBar.add(searchSpacer);

						// ---- searchTextField ----
						searchTextField.setToolTipText(translator.get("MainToolbarToolTipSearchField"));
						searchTextField.setMaximumSize(new Dimension(200, 40));
						searchTextField.setPreferredSize(new Dimension(150, 28));
						searchTextField.setComponentPopupMenu(searchPopupMenu);
						searchTextField.addCaretListener(new CaretListener() {
							public void caretUpdate(CaretEvent e) {
								updateSearch();
							}
						});
						searchTextField.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								searchPopupMenu.show(searchButton, 0, 0);
							}
						});
						mainToolBar.add(searchTextField);

						// ---- searchButton ----
						searchButton.setToolTipText(translator.get("MainToolbarToolTipSearch"));
						searchButton.setIcon(new ImageIcon(getClass().getResource(
								"/icons/search.png")));
						searchButton.setComponentPopupMenu(searchPopupMenu);
						searchButton.setBorder(null);
						searchButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								searchPopupMenu.show(searchButton, 0, 0);
							}
						});
						mainToolBar.add(searchButton);
					}
					mainPanel.add(mainToolBar, BorderLayout.NORTH);
				}
				this.add(mainPanel, BorderLayout.CENTER);

				// ======== mainMenuBar ========
				{

					// ======== fileMenu ========
					{
						fileMenu.setText(translator.get("MenuFile"));
						
						// ---- preferencesMenuItem ----
						preferencesMenuItem.setText(translator.get("MenuItemPreferences"));
						preferencesMenuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								propertiesMenuItemActionPerformed(e);
							}
						});
						fileMenu.add(preferencesMenuItem);

						// ---- exitApplicationMenuItem ----
						exitApplicationMenuItem.setText(translator.get("MenuItemQuit"));
						exitApplicationMenuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								exitApplicationMenuItemActionPerformed(e);
							}
						});
						fileMenu.add(exitApplicationMenuItem);
					}
					mainMenuBar.add(fileMenu);

					// ======== viewMenu ========
					{
						viewMenu.setText(translator.get("MenuView"));

						// ---- peopleViewMenuItem ----
						peopleViewMenuItem.setText(translator.get("MenuItemPeople"));
						peopleViewMenuItem.setIcon(new ImageIcon(getClass().getResource(
								"/icons/people.png")));
						peopleViewMenuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								peopleViewMenuItemActionPerformed(e);
							}
						});
						viewMenu.add(peopleViewMenuItem);

						// ---- filesViewMenuItem ----
						filesViewMenuItem.setText(translator.get("MenuItemFiles"));
						filesViewMenuItem.setIcon(new ImageIcon(getClass().getResource(
								"/icons/files.png")));
						filesViewMenuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								filesViewMenuItemActionPerformed(e);
							}
						});
						viewMenu.add(filesViewMenuItem);

						// ---- notesViewMenuItem ----
						notesViewMenuItem.setText(translator.get("MenuItemNotes"));
						notesViewMenuItem.setIcon(new ImageIcon(getClass().getResource(
								"/icons/notes.png")));
						notesViewMenuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								notesViewMenuItemActionPerformed(e);
							}
						});
						viewMenu.add(notesViewMenuItem);
						viewMenu.addSeparator();

						// ---- systemLogViewMenuItem ----
						systemLogViewMenuItem.setText(translator.get("MenuItemLog"));
						systemLogViewMenuItem.setIcon(new ImageIcon(getClass().getResource(
								"/icons/log.png")));
						systemLogViewMenuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								systemLogViewMenuItemActionPerformed(e);
							}
						});

						viewMenu.add(systemLogViewMenuItem);
					}
					mainMenuBar.add(viewMenu);

					// ======== networkMenu ========
					{
						networkMenu.setText(translator.get("MenuNetwork"));

						// ---- signInNetworkMenuItem ----
						signInNetworkMenuItem.setText(translator.get("MenuItemSignIn"));
						signInNetworkMenuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								signInNetworkMenuItemActionPerformed(e);
							}
						});
						networkMenu.add(signInNetworkMenuItem);

						// ---- signOutNetworkMenuItem ----
						signOutNetworkMenuItem.setText(translator.get("MenuItemSignOut"));
						signOutNetworkMenuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								signOutNetworkMenuItemActionPerformed(e);
							}
						});
						networkMenu.add(signOutNetworkMenuItem);
						networkMenu.addSeparator();

						// ---- showOfflineMembersCheckBoxMenuItem ----
						showOfflineMembersCheckBoxMenuItem.setText(translator.get("PreferencesLabelShowOfflineProjectMembers"));
						showOfflineMembersCheckBoxMenuItem.addActionListener(new ActionListener () {
							public void actionPerformed(ActionEvent e) {
								setShowOfflineProjectMembersActionPerformed(e);
							}
						});
						networkMenu.add(showOfflineMembersCheckBoxMenuItem);

						// ---- auto datapool refresh ----
						autoRefreshCheckBoxMenuItem.setText(translator.get("PreferencesLabelAutoRefresh"));
						autoRefreshCheckBoxMenuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								setAutoDatapoolRefreshActionPerformed(e);
							}
						});
						networkMenu.add(autoRefreshCheckBoxMenuItem);

						// ---- autoFilePushCheckBoxMenuItem ----
						autoFilePushCheckBoxMenuItem.setText(translator.get("PreferencesLabelAutoPush"));
						autoFilePushCheckBoxMenuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								setAutoPushMenuItemActionPerformed(e);
							}
						});
						networkMenu.add(autoFilePushCheckBoxMenuItem);

						// ---- autoFilePullCheckBoxMenuItem ----
						autoFilePullCheckBoxMenuItem.setText(translator.get("PreferencesLabelAutoPull"));
						autoFilePullCheckBoxMenuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								setAutoPullMenuItemActionPerformed(e);
							}
						});
						networkMenu.add(autoFilePullCheckBoxMenuItem);
						
						//update pref menu items
						updatePrefMenuItems();
					}
					mainMenuBar.add(networkMenu);

					// ======== projectMenu ========
					{
						projectMenu.setText("Project");

						// ---- openProjectFolderMenuItem ----
						openProjectFolderMenuItem.setText(translator.get("MenuItemOpenProjectFolder"));
						projectMenu.add(openProjectFolderMenuItem);

						// ---- refreshDatapoolViewProjectMenuItem ----
						refreshDatapoolViewProjectMenuItem.setText(translator.get("MenuItemRefreshDatapoolView"));
						projectMenu.add(refreshDatapoolViewProjectMenuItem);

						// ---- newNoteProjectMenuItem ----
						newNoteProjectMenuItem.setText(translator.get("MenuItemNewNote"));
						newNoteProjectMenuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								newNoteProjectMenuItemActionPerformed(e);
							}
						});
						projectMenu.add(newNoteProjectMenuItem);
						projectMenu.addSeparator();

						// ---- addFileToProjectMenuItem ----
						addFileToProjectMenuItem.setText(translator.get("MenuItemAddFile"));
                        addFileToProjectMenuItem.addActionListener(new ActionListener()
                        {
                            public void actionPerformed(ActionEvent event) {
                                addFileToProjectMenuItemActionPerformed(event);
                            }
                        });
						projectMenu.add(addFileToProjectMenuItem);

						// ---- addFolderToProjectMenuItem ----
						addFolderToProjectMenuItem.setText(translator.get("MenuItemAddFolder"));
						projectMenu.add(addFolderToProjectMenuItem);
						projectMenu.addSeparator();

						// ---- addProjectMemberMenuItem ----
						addProjectMemberMenuItem.setText(translator.get("MenuItemAddProjectMember"));
						addProjectMemberMenuItem.addActionListener(new ActionListener()	{
							public void actionPerformed(ActionEvent e)	{
								addProjectMemberMenutItemActionPerformed(e);
							}
						});
						projectMenu.add(addProjectMemberMenuItem);

					}
					mainMenuBar.add(projectMenu);

					// ======== helpMenu ========
					{
						helpMenu.setText(translator.get("MenuHelp"));

						// ---- aboutHelpMenuItem ----
						aboutHelpMenuItem.setText(translator.get("MenuItemAbout"));
						aboutHelpMenuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								aboutHelpMenuItemActionPerformed(e);
							}
						});
						helpMenu.add(aboutHelpMenuItem);
					}
					mainMenuBar.add(helpMenu);
				}
				this.add(mainMenuBar, BorderLayout.NORTH);
				mainFrame.setJMenuBar(mainMenuBar);
			}
			frame1ContentPane.add(this, BorderLayout.CENTER);
			mainFrame.pack();
			mainFrame.setLocationRelativeTo(mainFrame.getOwner());
			mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			mainFrame.setMinimumSize(new Dimension(400, 400));
			mainFrame.setVisible(true);
		}

			// ---- showInfoPeopleMenuItem ----
			showInfoPeopleMenuItem.setText(translator.get("PeoplePaneContextMenuItemShowInfoComments"));
			showInfoPeopleMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					showInfoPeopleMenuItemActionPerformed(e);
				}
			});
			peoplePopupMenu.add(showInfoPeopleMenuItem);

			// ---- renamePeopleMenuItem ----
			renamePeopleMenuItem.setText(translator.get("PeoplePaneContextMenuItemSetNickname"));
			peoplePopupMenu.add(renamePeopleMenuItem);

			// ---- changeUserIdMenuItem ----
			changeUserIdMenuItem.setText(translator.get("PeoplePaneContextMenuItemSetUserID"));
			peoplePopupMenu.add(changeUserIdMenuItem);

			// ---- removePeopleMenuItem ----
			removePeopleMenuItem.setText(translator.get("PeoplePaneContextMenuItemRemoveMember"));
			peoplePopupMenu.add(removePeopleMenuItem);
		}
	}
    private void addFileToProjectMenuItemActionPerformed(ActionEvent event) {
        new ImportFileDialog(this.getMainFrame()).setJakeGuiAccess(jakeGuiAccess).setVisible(true);
    }

	enum SearchMode {
		Name, Tag, Both
	}

	private void initSearchPopupMenu() {
		nameSearchMenuItem = new JCheckBoxMenuItem();
		tagsSearchMenuItem = new JCheckBoxMenuItem();
		bothSearchMenuItem = new JCheckBoxMenuItem();
		// ---- nameSearchMenuItem ----
		nameSearchMenuItem.setText(translator.get("SearchMenuItemName"));
		nameSearchMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setSearchMode(SearchMode.Name);
			}
		});
		searchPopupMenu.add(nameSearchMenuItem);

		// ---- tagsSearchMenuItem ----
		tagsSearchMenuItem.setText(translator.get("SearchMenuItemTags"));
		tagsSearchMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setSearchMode(SearchMode.Tag);
			}
		});
		searchPopupMenu.add(tagsSearchMenuItem);

		// ---- bothSearchMenuItem ----
		bothSearchMenuItem.setText(translator.get("SearchMenuItemBoth"));
		bothSearchMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setSearchMode(SearchMode.Both);
			}
		});
		searchPopupMenu.add(bothSearchMenuItem);

		searchPopupMenu.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				nameSearchMenuItem.setSelected(getSearchMode() == SearchMode.Name);
				tagsSearchMenuItem.setSelected(getSearchMode() == SearchMode.Tag);
				bothSearchMenuItem.setSelected(getSearchMode() == SearchMode.Both);
			}

			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			}

			public void popupMenuCanceled(PopupMenuEvent e) {
			}
		});
	}

	private void setSearchMode(SearchMode searchMode) {
		this.searchMode = searchMode;

		updateSearch();
	}

	/**
	 * Update the current search for all tabs
	 */
	private void updateSearch() {
		boolean nameSearch = getSearchMode() == SearchMode.Name;
		boolean tagsSearch = getSearchMode() == SearchMode.Tag;
		boolean bothSearch = getSearchMode() == SearchMode.Both;

		PatternFilter searchNoteName = new PatternFilter(searchTextField.getText(), 0, notesPanel
				.getNameColPos());
		PatternFilter searchNoteTags = new PatternFilter(searchTextField.getText(), 0, notesPanel
				.getTagsColPos());
		PatternFilter searchNoteNameAnTags = new MultiColPatternFilter(searchTextField.getText(),
				0, notesPanel.getNameColPos(), notesPanel.getTagsColPos());
		
		PatternFilter searchFileName = new PatternFilter(searchTextField.getText(), 0, filesPanel
				.getNameColPos());
		PatternFilter searchFileTags = new PatternFilter(searchTextField.getText(), 0, filesPanel
				.getTagsColPos());
		PatternFilter searchFileNameAnTags = new MultiColPatternFilter(searchTextField.getText(),
				0, filesPanel.getNameColPos(), filesPanel.getTagsColPos());
		
		PatternFilter searchPeopleName = new PatternFilter(searchTextField.getText(), 0, peoplePanel
				.getNameColPos());	

		
		if (nameSearch) {
			filesPanel.setFilters(new FilterPipeline(new Filter[] { searchFileName }));
			notesPanel.setFilters(new FilterPipeline(new Filter[] { searchNoteName }));
			peoplePanel.setFilters(new FilterPipeline(new Filter[] { searchPeopleName }));
		}
		
		if (tagsSearch) {
			filesPanel.setFilters(new FilterPipeline(new Filter[] { searchFileTags }));
			notesPanel.setFilters(new FilterPipeline(new Filter[] { searchNoteTags }));
			peoplePanel.setFilters(new FilterPipeline(new Filter[] {  }));
		}
		
		if (bothSearch) {
			filesPanel.setFilters(new FilterPipeline(new Filter[] { searchFileNameAnTags }));
			notesPanel.setFilters(new FilterPipeline(new Filter[] { searchNoteNameAnTags }));
			peoplePanel.setFilters(new FilterPipeline(new Filter[] { searchPeopleName }));
		}	
	}

	private SearchMode getSearchMode() {
		return searchMode;
	}

	private JFrame mainFrame;
	private StatusPanel statusPanel;
	private JPanel mainPanel;
	private JTabbedPane mainTabbedPane;
	private PeoplePanel peoplePanel;
	private FilesPanel filesPanel;
	private NotesPanel notesPanel;
	private JToolBar mainToolBar;
	private JButton refreshDatapoolViewButton;
	private JButton pushFileButton;
	private JButton pullFilesButton;
	private JButton newNoteButton;
	private JPanel searchSpacer;
	private JButton searchButton;
	private JTextField searchTextField;
	private JMenuBar mainMenuBar;
	private JMenu fileMenu;
	private JMenuItem preferencesMenuItem;
	private JMenuItem exitApplicationMenuItem;
	private JMenu viewMenu;
	private JMenuItem peopleViewMenuItem;
	private JMenuItem filesViewMenuItem;
	private JMenuItem notesViewMenuItem;
	private JMenuItem systemLogViewMenuItem;
	private JMenu networkMenu;
	private JMenuItem signInNetworkMenuItem;
	private JMenuItem signOutNetworkMenuItem;
	private JCheckBoxMenuItem showOfflineMembersCheckBoxMenuItem;
	private JCheckBoxMenuItem autoRefreshCheckBoxMenuItem;
	private JCheckBoxMenuItem autoFilePushCheckBoxMenuItem;
	private JCheckBoxMenuItem autoFilePullCheckBoxMenuItem;
	private JMenu projectMenu;
	private JMenuItem openProjectFolderMenuItem;
	private JMenuItem refreshDatapoolViewProjectMenuItem;
	private JMenuItem newNoteProjectMenuItem;
	private JMenuItem addFileToProjectMenuItem;
	private JMenuItem addFolderToProjectMenuItem;
	private JMenuItem addProjectMemberMenuItem;
	private JMenu helpMenu;
	private JMenuItem aboutHelpMenuItem;
	private JPopupMenu peoplePopupMenu;
	private JMenuItem showInfoPeopleMenuItem;
	private JMenuItem renamePeopleMenuItem;
	private JMenuItem changeUserIdMenuItem;
	private JMenuItem removePeopleMenuItem;
	private JPopupMenu searchPopupMenu;
	private JCheckBoxMenuItem nameSearchMenuItem;
	private JCheckBoxMenuItem tagsSearchMenuItem;
	private JCheckBoxMenuItem bothSearchMenuItem;

	public JFrame getMainFrame() {
		return mainFrame;
	}
}
