package com.doublesignal.sepm.jake.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXLoginDialog;
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
import com.doublesignal.sepm.jake.core.domain.FileObject;
import com.doublesignal.sepm.jake.core.domain.JakeObject;
import com.doublesignal.sepm.jake.core.services.IJakeGuiAccess;
import com.doublesignal.sepm.jake.core.services.exceptions.LoginDataNotValidException;
import com.doublesignal.sepm.jake.core.services.exceptions.LoginDataRequiredException;
import com.doublesignal.sepm.jake.core.services.exceptions.LoginUseridNotValidException;
import com.doublesignal.sepm.jake.core.services.exceptions.NoSuchJakeObjectException;
import com.doublesignal.sepm.jake.gui.i18n.ITranslationProvider;
import com.doublesignal.sepm.jake.ics.exceptions.NetworkException;

/**
 * @author Peter Steinberger
 */
@SuppressWarnings("serial")
public class JakeGui extends JPanel {
	private static Logger log = Logger.getLogger(JakeGui.class);

	private final ITranslationProvider translator;

	private IJakeGuiAccess jakeGuiAccess = null;

	public IJakeGuiAccess getJakeGuiAccess() {
		log.debug("Getting JakeGuIAccess Object");
		return jakeGuiAccess;
	}

	public void setJakeGuiAccess(IJakeGuiAccess jakeGuiAccess) {
		log.debug("Setting IJakeGuiAccess Object");
		this.jakeGuiAccess = jakeGuiAccess;
	}

	public JakeGui(IJakeGuiAccess jakeGuiAccess) {
		BeanFactory factory = new XmlBeanFactory(new ClassPathResource(
				"beans.xml"));
		translator = (ITranslationProvider) factory
				.getBean("translationProvider");

		setJakeGuiAccess(jakeGuiAccess);
		setNativeLookAndFeel();
		log.debug("Initializing Components");
		initComponents();
	}

	/**
	 * Set the system NATIVE look & feel.
	 */
	private static void setNativeLookAndFeel() {
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
		new NewProjectDialog(mainFrame).setVisible(true);
	}

	private void exitApplicationMenuItemActionPerformed(ActionEvent e) {
		log.debug("ExitApplication");
		System.exit(0);
	}

	private void propertiesMenuItemActionPerformed(ActionEvent e) {
		log.debug("Open Preferences Dialog");
		new PreferencesDialog(mainFrame).setVisible(true);
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
	private void signInNetwork(String username, String password,
			boolean fillFromConfig) {
		log.debug("Network signin procedure");

		boolean showDialog = (username == null || password == null)
				&& !fillFromConfig;

		final JXLoginPane login = new JXLoginPane();
		log.debug("Do we need a login dialog? " + showDialog);
		if (showDialog) {
			try {
				login.setUserName(jakeGuiAccess.getConfigOption("userid"));
			} catch (NoSuchConfigOptionException e2) {
				log.debug("Username not stored");
			}
			try {
				login.setPassword(jakeGuiAccess.getConfigOption("password")
						.toCharArray());
			} catch (NoSuchConfigOptionException e2) {
				log.debug("Password not stored");
			}
			login.setVisible(true);
			final JXLoginPane.JXLoginFrame frm = JXLoginPane
					.showLoginFrame(login);

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
			UserDialogHelper.error(mainFrame, translator
					.get("LoginDataNotValid"));
			signInNetwork(username, null, false);
			return;
		} catch (LoginUseridNotValidException e) {
			log.debug("LoginUseridNotValid");
			UserDialogHelper.error(mainFrame, translator
					.get("LoginUseridNotValid"));
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
			UserDialogHelper
					.inform(mainFrame, "", translator.get("NetworkError", e1
							.getMessage()), JOptionPane.ERROR_MESSAGE);
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
		new InfoDialog(mainFrame).setVisible(true);
	}

	private void sendMessageMenuItemActionPerformed(ActionEvent e) {
		new SendMessageDialog(mainFrame).setVisible(true);
	}

	private void newNoteProjectMenuItemActionPerformed(ActionEvent e) {
		new NoteEditorDialog(mainFrame).setVisible(true);
	}

	/**
	 * ** Files Context Menu ****
	 */
	private void resolveFileConflictMenuItemActionPerformed(ActionEvent e) {
		new ResolveConflictDialog(mainFrame).setVisible(true);
	}

	/**
	 * ** Status Bar Buttons ****
	 */
	private void messageReceivedStatusButtonActionPerformed(ActionEvent e) {
		new ReceiveMessageDialog(mainFrame).setVisible(true);
	}

	private void fileConflictStatusButtonActionPerformed(ActionEvent e) {
		new ResolveConflictDialog(mainFrame).setVisible(true);
	}

	private void connectionStatusButtonActionPerformed(ActionEvent e) {
		new JXLoginDialog().setVisible(true);
	}

	private void initComponents() {
		mainFrame = new JFrame();
		statusPanel = new JPanel();
		statusLabel = new JLabel();
		statusButtonsPanel = new JPanel();
		messageReceivedStatusButton = new JButton();
		fileConflictStatusButton = new JButton();
		connectionStatusButton = new JButton();
		mainPanel = new JPanel();
		mainTabbedPane = new JTabbedPane();
		peoplePanel = new JPanel();
		peopleScrollPane = new JScrollPane();
		peopleTable = new JXTable();
		filesPanel = new JPanel();
		filesScrollPane = new JScrollPane();
		filesTable = new JXTable();
		notesPanel = new NotesPanel(this);

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
		checkBoxMenuItem2 = new JCheckBoxMenuItem();
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
		filesPopupMenu = new JPopupMenu();
		openExecuteFileMenuItem = new JMenuItem();
		lockFileMenuItem = new JMenuItem();
		deleteFileMenuItem = new JMenuItem();
		viewLogForFileMenuItem = new JMenuItem();
		resolveFileConflictMenuItem = new JMenuItem();
		propagateFileMenuItem = new JMenuItem();
		menuItem4 = new JMenuItem();

		searchPopupMenu = new JPopupMenu();
		nameSearchMenuItem = new JMenuItem();
		tagsSearchMenuItem = new JMenuItem();
		bothSearchMenuItem = new JMenuItem();

		// ======== frame1 ========
		{
			mainFrame.setTitle("Jake - \u00dcbersetzerbau");
			mainFrame.setIconImage(new ImageIcon(getClass().getResource(
					"/icons/Jake.png")).getImage());
			Container frame1ContentPane = mainFrame.getContentPane();
			frame1ContentPane.setLayout(new BorderLayout());

			// ======== this ========
			{
				this.setLayout(new BorderLayout());

				// ======== statusPanel ========
				{
					statusPanel.setLayout(new BorderLayout(2, 2));

					// ---- statusLabel ----
					statusLabel.setText("Pulling File xy...");
					// statusLabel.setAlignmentX(0);
					statusPanel.add(statusLabel, BorderLayout.WEST);

					// ======== statusButtonsPanel ========
					{
						statusButtonsPanel.setLayout(new FlowLayout(
								FlowLayout.LEFT, 0, 0));

						// ---- messageReceivedStatusButton ----
						messageReceivedStatusButton
								.setText("2 Messages received");
						messageReceivedStatusButton.setIcon(new ImageIcon(
								getClass().getResource("/icons/message.png")));
						messageReceivedStatusButton
								.setBorder(new SoftBevelBorder(
										SoftBevelBorder.RAISED));
						messageReceivedStatusButton
								.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent e) {
										messageReceivedStatusButtonActionPerformed(e);
									}
								});
						statusButtonsPanel.add(messageReceivedStatusButton);

						// ---- fileConflictStatusButton ----
						fileConflictStatusButton.setText("1 File Conflict");
						fileConflictStatusButton.setIcon(new ImageIcon(
								getClass().getResource("/icons/warning.png")));
						fileConflictStatusButton.setBorder(new SoftBevelBorder(
								SoftBevelBorder.RAISED));
						fileConflictStatusButton
								.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent e) {
										fileConflictStatusButtonActionPerformed(e);
									}
								});
						statusButtonsPanel.add(fileConflictStatusButton);

						// ---- connectionStatusButton ----
						connectionStatusButton.setText("Connected");
						connectionStatusButton.setIcon(new ImageIcon(getClass()
								.getResource("/icons/network-idle.png")));
						connectionStatusButton.setBorder(new SoftBevelBorder(
								SoftBevelBorder.RAISED));
						connectionStatusButton
								.setToolTipText("Connected as pstein@jabber.fsinf.at");
						connectionStatusButton
								.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent e) {
										connectionStatusButtonActionPerformed(e);
									}
								});
						statusButtonsPanel.add(connectionStatusButton);
					}
					statusPanel.add(statusButtonsPanel, BorderLayout.EAST);
				}
				this.add(statusPanel, BorderLayout.SOUTH);

				// ======== mainPanel ========
				{
					mainPanel.setLayout(new BorderLayout());

					// ======== mainTabbedPane ========
					{

						// ======== peoplePanel ========
						{
							peoplePanel.setLayout(new BoxLayout(peoplePanel,
									BoxLayout.X_AXIS));

							// ======== peopleScrollPane ========
							{
								// ---- peopleTable ----
								peopleTable
										.setComponentPopupMenu(peoplePopupMenu);
								peopleTable.setColumnControlVisible(true);
								peopleTable.setHighlighters(HighlighterFactory
										.createSimpleStriping());
								peopleTable
										.setModel(new DefaultTableModel(
												new Object[][] {
														{
																"Simon",
																"simon.wallner@jabber.fsinf.at",
																"Online",
																"Projektleiter" },
														{
																"Dominik",
																"dominik.dorn@jabber.fsinf.at",
																"Online", null },
														{
																"Chris",
																"chris.sutter@jabber.fsinf.at",
																"unknown", null },
														{
																"Peter",
																"pstein@jabber.fsinf.at",
																"Offline", null }, },
												new String[] { "Nickname",
														"User ID", "Status",
														"Comment" }) {
											boolean[] columnEditable = new boolean[] {
													true, true, false, false };

											@Override
											public boolean isCellEditable(
													int rowIndex,
													int columnIndex) {
												return columnEditable[columnIndex];
											}
										});
								{
									TableColumnModel cm = peopleTable
											.getColumnModel();
									cm.getColumn(1).setPreferredWidth(195);
									cm.getColumn(3).setPreferredWidth(145);
								}
								peopleScrollPane.setViewportView(peopleTable);
							}
							peoplePanel.add(peopleScrollPane);
						}
						mainTabbedPane.addTab("People (3/4)", new ImageIcon(
								getClass().getResource("/icons/people.png")),
								peoplePanel);

						// ======== filesPanel ========
						{
							filesPanel.setLayout(new BorderLayout());

							// ======== filesScrollPane ========
							{
								// ---- filesTable ----
								filesTable
										.setComponentPopupMenu(filesPopupMenu);
								filesTable.setColumnControlVisible(true);
								filesTable.setHighlighters(HighlighterFactory
										.createSimpleStriping());

								/*
								 * Object[] fileListContent = new Object[]{};
								 * 
								 * fileListContent[fileListContent.length] = new
								 * String[] {"Name", "Size", "Tags", "Sync
								 * Status", "Last Changed", "User"};
								 */

								String[] fileListCaptions = new String[] {
										"Name", "Size", "Tags", "Sync Status",
										"Last Changed", "User" };

								DefaultTableModel fileListTableModel = new DefaultTableModel(
										fileListCaptions, 0

								) {
									boolean[] columnEditable = new boolean[] {
											false, false, true, false, false,
											true };

									@Override
									public boolean isCellEditable(int rowIndex,
											int columnIndex) {
										return columnEditable[columnIndex];
									}
								};

								List<JakeObject> files = null;
								try {
									files = jakeGuiAccess
											.getJakeObjectsByPath("/");
								} catch (NoSuchJakeObjectException e) {
									e.printStackTrace(); // To change body of
									// catch statement
									// use File |
									// Settings | File
									// Templates.
								}

								for (JakeObject obj : files) {
									long FileSize = jakeGuiAccess
											.getFileSize((FileObject) obj);
									String FileSizeUnity = "Bytes";
									if (FileSize > 1024) {
										FileSize /= 1024;
										FileSizeUnity = "KB";
									}
									if (FileSize > 1024) {
										FileSize /= 1024;
										FileSizeUnity = "MB";
									}
									if (FileSize > 1024) {
										FileSize /= 1024;
										FileSizeUnity = "GB";
									}

									fileListTableModel.addRow(new String[] {
											obj.getName(),
											FileSize + " " + FileSizeUnity,
											obj.getTags().toString(),
											"offline",
											jakeGuiAccess.getLastModified(obj)
													.toString(),
											jakeGuiAccess.getLastModifier(obj)
													.getNickname() });
								}

								filesTable.setModel(fileListTableModel);
								{
									TableColumnModel cm = filesTable
											.getColumnModel();
									cm.getColumn(0).setPreferredWidth(245);
									cm.getColumn(1).setPreferredWidth(50);
									cm.getColumn(2).setPreferredWidth(75);
								}
								filesTable
										.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
								filesTable
										.setPreferredScrollableViewportSize(new Dimension(
												450, 379));
								filesScrollPane.setViewportView(filesTable);
							}
							filesPanel
									.add(filesScrollPane, BorderLayout.CENTER);

							mainTabbedPane.addTab("Files (4/10 MB)",
									new ImageIcon(getClass().getResource(
											"/icons/files.png")), filesPanel);

						}
						mainTabbedPane.addTab("Notes (3)", new ImageIcon(
								getClass().getResource("/icons/notes.png")),
								notesPanel);

					}
					mainPanel.add(mainTabbedPane, BorderLayout.CENTER);

					// ======== mainToolBar ========
					{
						mainToolBar.setBorderPainted(false);
						mainToolBar.setRollover(true);

						// ---- openProjectFolderButton ----
						openProjectFolderButton
								.setHorizontalAlignment(SwingConstants.RIGHT);
						openProjectFolderButton.setIcon(new ImageIcon(
								getClass().getResource(
										"/icons/project_folder.png")));
						openProjectFolderButton
								.setToolTipText("Open Project Folder");
						openProjectFolderButton.setText("Open Project Folder");
						mainToolBar.add(openProjectFolderButton);

						// ---- refreshDatapoolViewButton ----
						refreshDatapoolViewButton
								.setHorizontalAlignment(SwingConstants.RIGHT);
						refreshDatapoolViewButton
								.setToolTipText("Refresh Datapool View");
						refreshDatapoolViewButton.setIcon(new ImageIcon(
								getClass().getResource(
										"/icons/sync_project_folder.png")));
						mainToolBar.add(refreshDatapoolViewButton);
						mainToolBar.addSeparator();

						// ---- propagateFileButton ----
						propagateFileButton.setIcon(new ImageIcon(getClass()
								.getResource("/icons/push.png")));
						propagateFileButton
								.setToolTipText("Propagate locally changed file");
						mainToolBar.add(propagateFileButton);

						// ---- pullFilesButton ----
						pullFilesButton.setIcon(new ImageIcon(getClass()
								.getResource("/icons/pull.png")));
						pullFilesButton
								.setToolTipText("Pull file from project member");
						mainToolBar.add(pullFilesButton);

						// ---- LockFileToggleButton ----
						LockFileToggleButton.setIcon(new ImageIcon(getClass()
								.getResource("/icons/lock.png")));
						LockFileToggleButton.setToolTipText("Lock File...");
						mainToolBar.add(LockFileToggleButton);
						mainToolBar.addSeparator();

						// ---- newNoteButton ----
						newNoteButton.setToolTipText("New Note");
						newNoteButton.setIcon(new ImageIcon(getClass()
								.getResource("/icons/notes-new.png")));
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
						searchTextField
								.setPreferredSize(new Dimension(150, 28));
						searchTextField.setComponentPopupMenu(searchPopupMenu);
						searchTextField.addCaretListener(new CaretListener() {
							public void caretUpdate(CaretEvent e) {
								// TODO: proof of concept! filter input (e.g.
								// crash with '*')
								peopleTable
										.setFilters(new FilterPipeline(
												new Filter[] { new PatternFilter(
														searchTextField
																.getText(), 0,
														0) }));
								filesTable
										.setFilters(new FilterPipeline(
												new Filter[] { new PatternFilter(
														searchTextField
																.getText(), 0,
														0) }));
								/*
								 * notesTable.setFilters(new FilterPipeline(new
								 * Filter[]{ new
								 * PatternFilter(searchTextField.getText(), 0,
								 * 0) }));
								 */
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
						searchButton.setIcon(new ImageIcon(getClass()
								.getResource("/icons/search.png")));
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
						newProjectMenuItem
								.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent e) {
										newProjectMenuItemActionPerformed(e);
									}
								});
						fileMenu.add(newProjectMenuItem);

						// ---- openProjectMenuItem ----
						openProjectMenuItem.setText("Open Project...");
						openProjectMenuItem
								.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent e) {
										openProjectMenuItemActionPerformed(e);
									}
								});
						fileMenu.add(openProjectMenuItem);

						// ---- preferencesMenuItem ----
						preferencesMenuItem.setText("Preferences...");
						preferencesMenuItem
								.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent e) {
										propertiesMenuItemActionPerformed(e);
									}
								});
						fileMenu.add(preferencesMenuItem);

						// ---- exitApplicationMenuItem ----
						exitApplicationMenuItem.setText("Exit");
						exitApplicationMenuItem
								.addActionListener(new ActionListener() {
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
						peopleViewMenuItem.setIcon(new ImageIcon(getClass()
								.getResource("/icons/people.png")));
						peopleViewMenuItem
								.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent e) {
										peopleViewMenuItemActionPerformed(e);
									}
								});
						viewMenu.add(peopleViewMenuItem);

						// ---- filesViewMenuItem ----
						filesViewMenuItem.setText("Files");
						filesViewMenuItem.setIcon(new ImageIcon(getClass()
								.getResource("/icons/files.png")));
						filesViewMenuItem
								.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent e) {
										filesViewMenuItemActionPerformed(e);
									}
								});
						viewMenu.add(filesViewMenuItem);

						// ---- notesViewMenuItem ----
						notesViewMenuItem.setText("Notes");
						notesViewMenuItem.setIcon(new ImageIcon(getClass()
								.getResource("/icons/notes.png")));
						notesViewMenuItem
								.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent e) {
										notesViewMenuItemActionPerformed(e);
									}
								});
						viewMenu.add(notesViewMenuItem);
						viewMenu.addSeparator();

						// ---- systemLogViewMenuItem ----
						systemLogViewMenuItem.setText("System Log");
						systemLogViewMenuItem.setIcon(new ImageIcon(getClass()
								.getResource("/icons/log.png")));
						systemLogViewMenuItem
								.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent e) {
										systemLogViewMenuItemActionPerformed(e);
									}
								});

						viewMenu.add(systemLogViewMenuItem);
					}
					mainMenuBar.add(viewMenu);

					// ======== networkMenu ========
					{
						networkMenu.setText("Network");

						// ---- signInNetworkMenuItem ----
						signInNetworkMenuItem.setText("Sign In...");
						signInNetworkMenuItem
								.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent e) {
										signInNetworkMenuItemActionPerformed(e);
									}
								});
						networkMenu.add(signInNetworkMenuItem);

						// ---- signOutNetworkMenuItem ----
						signOutNetworkMenuItem.setText("Sign Out");
						signOutNetworkMenuItem
								.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent e) {
										signOutNetworkMenuItemActionPerformed(e);
									}
								});
						networkMenu.add(signOutNetworkMenuItem);
						networkMenu.addSeparator();

						// ---- showOfflineMembersCheckBoxMenuItem ----
						showOfflineMembersCheckBoxMenuItem
								.setText("Show Offline Members");
						showOfflineMembersCheckBoxMenuItem.setSelected(true);
						networkMenu.add(showOfflineMembersCheckBoxMenuItem);

						// ---- checkBoxMenuItem2 ----
						checkBoxMenuItem2.setText("Automatic Datapool Refresh");
						checkBoxMenuItem2.setSelected(true);
						networkMenu.add(checkBoxMenuItem2);

						// ---- autoFilePropagateCheckBoxMenuItem ----
						autoFilePropagateCheckBoxMenuItem
								.setText("Automatic File Propagation");
						autoFilePropagateCheckBoxMenuItem.setSelected(true);
						networkMenu.add(autoFilePropagateCheckBoxMenuItem);

						// ---- autoFilePullCheckBoxMenuItem ----
						autoFilePullCheckBoxMenuItem
								.setText("Automatic File Pull");
						autoFilePullCheckBoxMenuItem.setSelected(true);
						networkMenu.add(autoFilePullCheckBoxMenuItem);
					}
					mainMenuBar.add(networkMenu);

					// ======== projectMenu ========
					{
						projectMenu.setText("Project");

						// ---- openProjectFolderMenuItem ----
						openProjectFolderMenuItem
								.setText("Open Project Folder");
						projectMenu.add(openProjectFolderMenuItem);

						// ---- refreshDatapoolViewProjectMenuItem ----
						refreshDatapoolViewProjectMenuItem
								.setText("Refresh Datapool View");
						projectMenu.add(refreshDatapoolViewProjectMenuItem);

						// ---- newNoteProjectMenuItem ----
						newNoteProjectMenuItem.setText("New Note...");
						newNoteProjectMenuItem
								.addActionListener(new ActionListener() {
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
						addProjectMemberMenuItem
								.setText("Add Project Member...");
						projectMenu.add(addProjectMemberMenuItem);
					}
					mainMenuBar.add(projectMenu);

					// ======== helpMenu ========
					{
						helpMenu.setText("Help");

						// ---- aboutHelpMenuItem ----
						aboutHelpMenuItem.setText("About");
						aboutHelpMenuItem
								.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent e) {
										aboutHelpMenuItemActionPerformed(e);
									}
								});
						helpMenu.add(aboutHelpMenuItem);
					}
					mainMenuBar.add(helpMenu);
				}
				this.add(mainMenuBar, BorderLayout.NORTH);
			}
			frame1ContentPane.add(this, BorderLayout.CENTER);
			mainFrame.pack();
			mainFrame.setLocationRelativeTo(mainFrame.getOwner());
			mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			// mainFrame.setSize( 300, 200 );
			mainFrame.setVisible(true);
		}

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

		// ======== filesPopupMenu ========
		{

			// ---- openExecuteFileMenuItem ----
			openExecuteFileMenuItem.setText("Open");
			filesPopupMenu.add(openExecuteFileMenuItem);

			// ---- lockFileMenuItem ----
			lockFileMenuItem.setText("Lock File...");
			filesPopupMenu.add(lockFileMenuItem);

			// ---- deleteFileMenuItem ----
			deleteFileMenuItem.setText("Delete File...");
			filesPopupMenu.add(deleteFileMenuItem);

			// ---- viewLogForFileMenuItem ----
			viewLogForFileMenuItem.setText("View Log...");
			filesPopupMenu.add(viewLogForFileMenuItem);

			// ---- resolveFileConflictMenuItem ----
			resolveFileConflictMenuItem.setText("Resolve Conflict...");
			resolveFileConflictMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					resolveFileConflictMenuItemActionPerformed(e);
				}
			});
			filesPopupMenu.add(resolveFileConflictMenuItem);
			filesPopupMenu.addSeparator();

			// ---- propagateFileMenuItem ----
			propagateFileMenuItem.setText("Propagate File");
			propagateFileMenuItem
					.setToolTipText("Propagate locally changed file");
			filesPopupMenu.add(propagateFileMenuItem);

			// ---- menuItem4 ----
			menuItem4.setText("Pull File");
			filesPopupMenu.add(menuItem4);
		}

		// ======== searchPopupMenu ========
		{

			// ---- nameSearchMenuItem ----
			nameSearchMenuItem.setText("Name");
			searchPopupMenu.add(nameSearchMenuItem);

			// ---- tagsSearchMenuItem ----
			tagsSearchMenuItem.setText("Tags");
			searchPopupMenu.add(tagsSearchMenuItem);

			// ---- bothSearchMenuItem ----
			bothSearchMenuItem.setText("Both");
			searchPopupMenu.add(bothSearchMenuItem);
		}
	}

	private JFrame mainFrame;
	private JPanel statusPanel;
	private JLabel statusLabel;
	private JPanel statusButtonsPanel;
	private JButton messageReceivedStatusButton;
	private JButton fileConflictStatusButton;
	private JButton connectionStatusButton;
	private JPanel mainPanel;
	private JTabbedPane mainTabbedPane;
	private JPanel peoplePanel;
	private JScrollPane peopleScrollPane;
	private JXTable peopleTable;
	private JPanel filesPanel;
	private JScrollPane filesScrollPane;
	private JXTable filesTable;
	private JPanel notesPanel;

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
	private JCheckBoxMenuItem checkBoxMenuItem2;
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
	private JPopupMenu filesPopupMenu;
	private JMenuItem openExecuteFileMenuItem;
	private JMenuItem lockFileMenuItem;
	private JMenuItem deleteFileMenuItem;
	private JMenuItem viewLogForFileMenuItem;
	private JMenuItem resolveFileConflictMenuItem;
	private JMenuItem propagateFileMenuItem;
	private JMenuItem menuItem4;
	private JPopupMenu searchPopupMenu;
	private JMenuItem nameSearchMenuItem;
	private JMenuItem tagsSearchMenuItem;
	private JMenuItem bothSearchMenuItem;

	public JFrame getMainFrame() {
		return mainFrame;
	}
}
