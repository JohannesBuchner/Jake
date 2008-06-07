package com.doublesignal.sepm.jake.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXLoginPane;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.Filter;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.decorator.PatternFilter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

import com.doublesignal.sepm.jake.core.dao.exceptions.NoSuchConfigOptionException;
import com.doublesignal.sepm.jake.core.domain.Project;
import com.doublesignal.sepm.jake.core.services.IJakeGuiAccess;
import com.doublesignal.sepm.jake.core.services.exceptions.LoginDataNotValidException;
import com.doublesignal.sepm.jake.core.services.exceptions.LoginDataRequiredException;
import com.doublesignal.sepm.jake.core.services.exceptions.LoginUseridNotValidException;
import com.doublesignal.sepm.jake.gui.helper.MultiColPatternFilter;
import com.doublesignal.sepm.jake.gui.i18n.ITranslationProvider;
import com.doublesignal.sepm.jake.ics.exceptions.NetworkException;

/**
 * @author Peter Steinberger
 */
@SuppressWarnings("serial")
public class JakeGui extends JPanel implements Observer {
	private static Logger log = Logger.getLogger(JakeGui.class);

	private Project currentProject = null;
	private SearchMode searchMode = SearchMode.Both;

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

	private final ITranslationProvider translator;

	private IJakeGuiAccess jakeGuiAccess = null;

	public IJakeGuiAccess getJakeGuiAccess() {
		log.debug("Getting IJakeGuiAccess Object; is null? " + (jakeGuiAccess == null));
		return jakeGuiAccess;
	}

	public void setJakeGuiAccess(IJakeGuiAccess jakeGuiAccess) {
		log.debug("Setting IJakeGuiAccess Object; is null? " + (jakeGuiAccess == null));
		this.jakeGuiAccess = jakeGuiAccess;
	}

	public JakeGui(IJakeGuiAccess jakeGuiAccess) {
		BeanFactory factory = new XmlBeanFactory(new ClassPathResource("beans.xml"));
		translator = (ITranslationProvider) factory.getBean("translationProvider");

		setJakeGuiAccess(jakeGuiAccess);
		setSystemProperties();
		setNativeLookAndFeel();
		log.debug("Initializing Components");
		initComponents();
		initSearchPopupMenu();
		registerUpdateObservers();
		updateAll();
		setStatusMsg("started");
		log.debug("JakeGui loaded.");

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

	/**
	 * ** File Menu ****
	 */

	private void newProjectMenuItemActionPerformed(ActionEvent e) {
		log.debug("Open new Project Dialog");
		// new NewProjectDialog(mainFrame, this).setVisible(true);
	}

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
			autoFilePropagateCheckBoxMenuItem.setSelected(Boolean.parseBoolean(jakeGuiAccess.getConfigOption("autoPush")));
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
		new ViewLogDialog(mainFrame).setVisible(true);
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
	private void signInNetwork(String username, String password, boolean fillFromConfig) {
		log.debug("Network signin procedure");

		boolean showDialog = (username == null || password == null) && !fillFromConfig;

		final JXLoginPane login = new JXLoginPane();
		log.debug("Do we need a login dialog? " + showDialog);
		if (showDialog) {
			try {
				login.setUserName(jakeGuiAccess.getConfigOption("userid"));
			} catch (NoSuchConfigOptionException e2) {
				log.debug("Username not stored");
			}
			try {
				login.setPassword(jakeGuiAccess.getConfigOption("password").toCharArray());
			} catch (NoSuchConfigOptionException e2) {
				log.debug("Password not stored");
			}
			login.setVisible(true);
			final JXLoginPane.JXLoginFrame frm = JXLoginPane.showLoginFrame(login);

			frm.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent e) {
					log.debug("Login Dialog was: " + frm.getStatus());
					if (frm.getStatus() != JXLoginPane.Status.SUCCEEDED) {
						return;
					}
					String username = login.getUserName();
					String password = new String(login.getPassword());
					if (login.getSaveMode() == JXLoginPane.SaveMode.USER_NAME
							|| login.getSaveMode() == JXLoginPane.SaveMode.BOTH) {
						log.debug("Saving username");
						jakeGuiAccess.setConfigOption("userid", username);
					}
					if (login.getSaveMode() == JXLoginPane.SaveMode.PASSWORD
							|| login.getSaveMode() == JXLoginPane.SaveMode.BOTH) {
						log.debug("Saving password");
						jakeGuiAccess.setConfigOption("password", username);
					}
					signInNetwork(username, password, false);
				}
			});
			frm.setVisible(true);
			return;
		}

		log.debug("Trying login with " + username + "...");
		try {
			jakeGuiAccess.login(username, password);
			log.debug("Login was successful");
			return;
		} catch (LoginDataRequiredException e1) {
			log.debug("LoginDataRequired");
			signInNetwork(username, password, false);
			return;
		} catch (LoginDataNotValidException e1) {
			log.debug("LoginDataNotValid");
			UserDialogHelper.error(mainFrame, translator.get("LoginDataNotValid"));
			signInNetwork(username, null, false);
			return;
		} catch (LoginUseridNotValidException e) {
			log.debug("LoginUseridNotValid");
			UserDialogHelper.error(mainFrame, translator.get("LoginUseridNotValid"));
			login.setErrorMessage(translator.get("LoginUseridNotValid"));
			signInNetwork(username, null, false);
			return;
		} catch (NetworkException e1) {
			log.debug("NetworkException");
			UserDialogHelper.error(mainFrame, translator.get("NetworkError", e1
					.getLocalizedMessage()));
			return;
		}
	}

	/**
	 * @author johannes
	 */
	private void signOutNetworkMenuItemActionPerformed(ActionEvent e) {
		try {
			jakeGuiAccess.logout();
		} catch (NetworkException e1) {
			UserDialogHelper.inform(mainFrame, "", translator.get("NetworkError", e1.getMessage()),
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * ** Project Menu ****
	 */

	private void openProjectMenuItemActionPerformed(ActionEvent e) {
		// TODO
	}

	/**
	 * ** Help Menu ****
	 */

	private void aboutHelpMenuItemActionPerformed(ActionEvent e) {
		JOptionPane.showMessageDialog(mainFrame, "Jake GUI by SEPM Group 3950");
	}

	/**
	 * ** Tool Bar ****
	 */

	private void newNoteButtonActionPerformed(ActionEvent e) {
		new NoteEditorDialog(mainFrame).setVisible(true);
	}

	/**
	 * ** People Context Menu ****
	 */

	private void showInfoPeopleMenuItemActionPerformed(ActionEvent e) {
		new ProjectMemberInfoDialog(mainFrame).setVisible(true);
	}

	private void sendMessageMenuItemActionPerformed(ActionEvent e) {
		new SendMessageDialog(mainFrame).setVisible(true);
	}

	private void newNoteProjectMenuItemActionPerformed(ActionEvent e) {
		new NoteEditorDialog(mainFrame).setVisible(true);
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
		// mainTabbedPane.setTitleAt(0, filesPanel.getTitle());
		// mainTabbedPane.setTitleAt(0, peoplePanel.getTitle());
		mainTabbedPane.setTitleAt(2, notesPanel.getTitle());
		mainTabbedPane.updateUI();
	}

	public void updateAll() {
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
		//peoplePanel = new JPanel();
		peopleScrollPane = new JScrollPane();
		//peopleTable = new JXTable();
		filesPanel = new FilesPanel(this);
		notesPanel = new NotesPanel(this);
		peoplePanel = new PeoplePanel(this);
		mainToolBar = new JToolBar();
		openProjectFolderButton = new JButton();
		refreshDatapoolViewButton = new JButton();
		propagateFileButton = new JButton();
		pullFilesButton = new JButton();
		LockFileToggleButton = new JToggleButton();
		newNoteButton = new JButton();
		searchSpacer = new JPanel(null);
		searchButton = new JButton();
		searchTextField = new JTextField();
		mainMenuBar = new JMenuBar();
		fileMenu = new JMenu();
		newProjectMenuItem = new JMenuItem();
		openProjectMenuItem = new JMenuItem();
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
		autoFilePropagateCheckBoxMenuItem = new JCheckBoxMenuItem();
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
		sendMessageMenuItem = new JMenuItem();
		showInfoPeopleMenuItem = new JMenuItem();
		renamePeopleMenuItem = new JMenuItem();
		changeUserIdMenuItem = new JMenuItem();
		removePeopleMenuItem = new JMenuItem();
		searchPopupMenu = new JPopupMenu();

		// ======== frame1 ========
		{
			mainFrame.setTitle("Jake - Please open/create a project");

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

						// ---- openProjectFolderButton ----
						openProjectFolderButton.setHorizontalAlignment(SwingConstants.RIGHT);
						openProjectFolderButton.setIcon(new ImageIcon(getClass().getResource(
								"/icons/project_folder.png")));
						openProjectFolderButton.setToolTipText("Open Project Folder");
						openProjectFolderButton.setText("Open Project Folder");
						mainToolBar.add(openProjectFolderButton);

						// ---- refreshDatapoolViewButton ----
						refreshDatapoolViewButton.setHorizontalAlignment(SwingConstants.RIGHT);
						refreshDatapoolViewButton.setToolTipText("Refresh Datapool View");
						refreshDatapoolViewButton.setIcon(new ImageIcon(getClass().getResource(
								"/icons/sync_project_folder.png")));

						refreshDatapoolViewButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent event) {
								jakeGuiAccess.refreshFileObjects();
								filesPanel.updateUI();
							}
						});

						mainToolBar.add(refreshDatapoolViewButton);
						mainToolBar.addSeparator();

						// ---- propagateFileButton ----
						propagateFileButton.setIcon(new ImageIcon(getClass().getResource(
								"/icons/push.png")));
						propagateFileButton.setToolTipText("Propagate locally changed file");
						mainToolBar.add(propagateFileButton);

						// ---- pullFilesButton ----
						pullFilesButton.setIcon(new ImageIcon(getClass().getResource(
								"/icons/pull.png")));
						pullFilesButton.setToolTipText("Pull file from project member");
						mainToolBar.add(pullFilesButton);

						// ---- LockFileToggleButton ----
						LockFileToggleButton.setIcon(new ImageIcon(getClass().getResource(
								"/icons/lock.png")));
						LockFileToggleButton.setToolTipText("Lock File...");
						mainToolBar.add(LockFileToggleButton);
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
						searchTextField.setToolTipText("Search for Files");
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
						searchButton.setToolTipText("Search for");
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
						fileMenu.setText("File");

						// ---- newProjectMenuItem ----
						newProjectMenuItem.setText("New Project...");
						newProjectMenuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								newProjectMenuItemActionPerformed(e);
							}
						});
						fileMenu.add(newProjectMenuItem);

						// ---- openProjectMenuItem ----
						openProjectMenuItem.setText("Open Project...");
						openProjectMenuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								openProjectMenuItemActionPerformed(e);
							}
						});
						fileMenu.add(openProjectMenuItem);

						// ---- preferencesMenuItem ----
						preferencesMenuItem.setText("Preferences...");
						preferencesMenuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								propertiesMenuItemActionPerformed(e);
							}
						});
						fileMenu.add(preferencesMenuItem);

						// ---- exitApplicationMenuItem ----
						exitApplicationMenuItem.setText("Exit");
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
						viewMenu.setText("View");

						// ---- peopleViewMenuItem ----
						peopleViewMenuItem.setText("People");
						peopleViewMenuItem.setIcon(new ImageIcon(getClass().getResource(
								"/icons/people.png")));
						peopleViewMenuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								peopleViewMenuItemActionPerformed(e);
							}
						});
						viewMenu.add(peopleViewMenuItem);

						// ---- filesViewMenuItem ----
						filesViewMenuItem.setText("Files");
						filesViewMenuItem.setIcon(new ImageIcon(getClass().getResource(
								"/icons/files.png")));
						filesViewMenuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								filesViewMenuItemActionPerformed(e);
							}
						});
						viewMenu.add(filesViewMenuItem);

						// ---- notesViewMenuItem ----
						notesViewMenuItem.setText("Notes");
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
						systemLogViewMenuItem.setText("System Log");
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

						// ---- autoFilePropagateCheckBoxMenuItem ----
						autoFilePropagateCheckBoxMenuItem.setText(translator.get("PreferencesLabelAutoPush"));
						autoFilePropagateCheckBoxMenuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								setAutoPushMenuItemActionPerformed(e);
							}
						});
						networkMenu.add(autoFilePropagateCheckBoxMenuItem);

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
						openProjectFolderMenuItem.setText("Open Project Folder");
						projectMenu.add(openProjectFolderMenuItem);

						// ---- refreshDatapoolViewProjectMenuItem ----
						refreshDatapoolViewProjectMenuItem.setText("Refresh Datapool View");
						projectMenu.add(refreshDatapoolViewProjectMenuItem);

						// ---- newNoteProjectMenuItem ----
						newNoteProjectMenuItem.setText("New Note...");
						newNoteProjectMenuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								newNoteProjectMenuItemActionPerformed(e);
							}
						});
						projectMenu.add(newNoteProjectMenuItem);
						projectMenu.addSeparator();

						// ---- addFileToProjectMenuItem ----
						addFileToProjectMenuItem.setText("Add File...");
						projectMenu.add(addFileToProjectMenuItem);

						// ---- addFolderToProjectMenuItem ----
						addFolderToProjectMenuItem.setText("Add Folder...");
						projectMenu.add(addFolderToProjectMenuItem);
						projectMenu.addSeparator();

						// ---- addProjectMemberMenuItem ----
						addProjectMemberMenuItem.setText("Add Project Member...");
						projectMenu.add(addProjectMemberMenuItem);

					}
					mainMenuBar.add(projectMenu);

					// ======== helpMenu ========
					{
						helpMenu.setText("Help");

						// ---- aboutHelpMenuItem ----
						aboutHelpMenuItem.setText("About");
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
			mainFrame.setVisible(true);
		}
/*
		// ======== peoplePopupMenu ========
		{

			// ---- sendMessageMenuItem ----
			sendMessageMenuItem.setText("Send Message...");
			sendMessageMenuItem.setIcon(new ImageIcon(getClass().getResource(
					"/icons/message-new.png")));
			sendMessageMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sendMessageMenuItemActionPerformed(e);
				}
			});
			peoplePopupMenu.add(sendMessageMenuItem);
*/
			// ---- showInfoPeopleMenuItem ----
			showInfoPeopleMenuItem.setText("Show Info/Comments...");
			showInfoPeopleMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					showInfoPeopleMenuItemActionPerformed(e);
				}
			});
			peoplePopupMenu.add(showInfoPeopleMenuItem);

			// ---- renamePeopleMenuItem ----
			renamePeopleMenuItem.setText("Change Nickname...");
			peoplePopupMenu.add(renamePeopleMenuItem);

			// ---- changeUserIdMenuItem ----
			changeUserIdMenuItem.setText("Change User ID...");
			peoplePopupMenu.add(changeUserIdMenuItem);

			// ---- removePeopleMenuItem ----
			removePeopleMenuItem.setText("Remove Member...");
			peoplePopupMenu.add(removePeopleMenuItem);
		}
	}

	enum SearchMode {
		Name, Tag, Both
	}

	private void initSearchPopupMenu() {
		nameSearchMenuItem = new JCheckBoxMenuItem();
		tagsSearchMenuItem = new JCheckBoxMenuItem();
		bothSearchMenuItem = new JCheckBoxMenuItem();
		// ---- nameSearchMenuItem ----
		nameSearchMenuItem.setText("Name");
		nameSearchMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setSearchMode(SearchMode.Name);
			}
		});
		searchPopupMenu.add(nameSearchMenuItem);

		// ---- tagsSearchMenuItem ----
		tagsSearchMenuItem.setText("Tags");
		tagsSearchMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setSearchMode(SearchMode.Tag);
			}
		});
		searchPopupMenu.add(tagsSearchMenuItem);

		// ---- bothSearchMenuItem ----
		bothSearchMenuItem.setText("Both");
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

		if (nameSearch)
			filesPanel.setFilters(new FilterPipeline(new Filter[] { searchFileName }));

		if (tagsSearch)
			filesPanel.setFilters(new FilterPipeline(new Filter[] { searchFileTags }));

		if (bothSearch)
			filesPanel.setFilters(new FilterPipeline(new Filter[] { searchFileNameAnTags }));

		if (nameSearch)
			notesPanel.setFilters(new FilterPipeline(new Filter[] { searchNoteName }));

		if (tagsSearch)
			notesPanel.setFilters(new FilterPipeline(new Filter[] { searchNoteTags }));

		if (bothSearch)
			notesPanel.setFilters(new FilterPipeline(new Filter[] { searchNoteNameAnTags }));
		
		// TODO: when philipp is finished...
		peopleTable.setFilters(new FilterPipeline(new Filter[] { new PatternFilter(searchTextField
				.getText(), 0, 0) }));
	}

	private SearchMode getSearchMode() {
		return searchMode;
	}

	private JFrame mainFrame;
	private StatusPanel statusPanel;
	private JPanel mainPanel;
	private JTabbedPane mainTabbedPane;
	private PeoplePanel peoplePanel;
	private JScrollPane peopleScrollPane;
	private JXTable peopleTable;
	private FilesPanel filesPanel;
	private NotesPanel notesPanel;
	private JToolBar mainToolBar;
	private JButton openProjectFolderButton;
	private JButton refreshDatapoolViewButton;
	private JButton propagateFileButton;
	private JButton pullFilesButton;
	private JToggleButton LockFileToggleButton;
	private JButton newNoteButton;
	private JPanel searchSpacer;
	private JButton searchButton;
	private JTextField searchTextField;
	private JMenuBar mainMenuBar;
	private JMenu fileMenu;
	private JMenuItem newProjectMenuItem;
	private JMenuItem openProjectMenuItem;
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
	private JCheckBoxMenuItem autoFilePropagateCheckBoxMenuItem;
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
	private JMenuItem sendMessageMenuItem;
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
