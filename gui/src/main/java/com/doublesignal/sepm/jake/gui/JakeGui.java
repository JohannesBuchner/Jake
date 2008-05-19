package com.doublesignal.sepm.jake.gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

/**
 * @author Peter Steinberger
 */
public class JakeGui extends JPanel {
	public JakeGui() {
		initComponents();
	}
	
    public static void main( String[] args )
    {
    	new JakeGui();
    }

	private void exitApplicationMenuItemActionPerformed(ActionEvent e) {
		System.exit(0);
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - tester tester
		mainFrame = new JFrame();
		statusPanel = new JPanel();
		statusLabel = new JLabel();
		panel1 = new JPanel();
		messageReceivedLabel = new JButton();
		fileConflictLabel = new JButton();
		connectionLabel = new JButton();
		mainPanel = new JPanel();
		mainTabbedPane = new JTabbedPane();
		peoplePanel = new JPanel();
		peopleScrollPane = new JScrollPane();
		peopleTable = new JTable();
		filesPanel = new JPanel();
		filesScrollPane = new JScrollPane();
		filesTable = new JTable();
		notesPanel = new JPanel();
		notesScrollPane = new JScrollPane();
		notesTable = new JTable();
		mainToolBar = new JToolBar();
		openProjectFolderButton = new JButton();
		refreshDatapoolViewButton = new JButton();
		propagateFileButton = new JButton();
		pullFilesButton = new JButton();
		LockFileToggleButton = new JToggleButton();
		newNoteButton = new JButton();
		searchSpacer = new JPanel(null);
		searchLabel = new JLabel();
		searchTextField = new JTextField();
		mainMenuBar = new JMenuBar();
		fileMenu = new JMenu();
		newProjectMenuItem = new JMenuItem();
		openProjectMenuItem = new JMenuItem();
		saveMenuItem = new JMenuItem();
		saveAsMenuItem = new JMenuItem();
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
		menuItem5 = new JMenuItem();
		removePeopleMenuItem = new JMenuItem();
		filesPopupMenu = new JPopupMenu();
		openExecuteFileMenuItem = new JMenuItem();
		lockFileMenuItem = new JMenuItem();
		deleteFileMenuItem = new JMenuItem();
		viewLogForFileMenuItem = new JMenuItem();
		resolveFileConflictMenuItem = new JMenuItem();
		propagateFileMenuItem = new JMenuItem();
		menuItem4 = new JMenuItem();
		notesPopupMenu = new JPopupMenu();
		viewEditNoteMenuItem = new JMenuItem();
		newNoteMenuItem = new JMenuItem();
		removeNoteMenuItem = new JMenuItem();
		searchPopupMenu = new JPopupMenu();
		nameSearchMenuItem = new JMenuItem();
		tagsSearchMenuItem = new JMenuItem();
		bothSearchMenuItem = new JMenuItem();

		//======== frame1 ========
		{
			mainFrame.setTitle("Jake - \u00dcbersetzerbau");
			System.out.println("test");
			System.out.flush();
			mainFrame.setIconImage(new ImageIcon(getClass().getResource("/resources/icons/Jake.png")).getImage());
			Container frame1ContentPane = mainFrame.getContentPane();
			frame1ContentPane.setLayout(new BorderLayout());

			//======== this ========
			{
			this.setLayout(new BorderLayout());

				//======== statusPanel ========
				{
					statusPanel.setLayout(new BorderLayout(60, 4));

					//---- statusLabel ----
					statusLabel.setText("Pulling File xy...");
					statusLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
					statusLabel.setAlignmentX(0.5F);
					statusPanel.add(statusLabel, BorderLayout.WEST);

					//======== panel1 ========
					{
						panel1.setLayout(new FlowLayout());

						//---- messageReceivedLabel ----
						messageReceivedLabel.setText("2 Messages received");
						messageReceivedLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
						messageReceivedLabel.setIcon(new ImageIcon(getClass().getResource("/resources/icons/message.png")));
						messageReceivedLabel.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
						panel1.add(messageReceivedLabel);

						//---- fileConflictLabel ----
						fileConflictLabel.setText("1 File Conflict");
						fileConflictLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
						fileConflictLabel.setIcon(new ImageIcon(getClass().getResource("/resources/icons/warning.png")));
						fileConflictLabel.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
						panel1.add(fileConflictLabel);

						//---- connectionLabel ----
						connectionLabel.setText("Connected");
						connectionLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
						connectionLabel.setIcon(new ImageIcon(getClass().getResource("/resources/icons/network-idle.png")));
						connectionLabel.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
						connectionLabel.setToolTipText("Connected as pstein@jabber.fsinf.at");
						panel1.add(connectionLabel);
					}
					statusPanel.add(panel1, BorderLayout.EAST);
				}
				this.add(statusPanel, BorderLayout.SOUTH);

				//======== mainPanel ========
				{
					mainPanel.setLayout(new BorderLayout());

					//======== mainTabbedPane ========
					{

						//======== peoplePanel ========
						{
							peoplePanel.setLayout(new BoxLayout(peoplePanel, BoxLayout.X_AXIS));

							//======== peopleScrollPane ========
							{
								peopleScrollPane.setComponentPopupMenu(peoplePopupMenu);

								//---- peopleTable ----
								peopleTable.setModel(new DefaultTableModel(
									new Object[][] {
										{"Simon", "simon.wallner@jabber.fsinf.at", "Online", "Projektleiter"},
										{"Dominik", "dominik.dorn@jabber.fsinf.at", "Online", null},
										{"Chris", "chris.sutter@jabber.fsinf.at", "unknown", null},
										{"Peter", "pstein@jabber.fsinf.at", "Offline", null},
									},
									new String[] {
										"Nickname", "User ID", "Status", "Comment"
									}
								) {
									boolean[] columnEditable = new boolean[] {
										true, true, false, false
									};
									@Override
									public boolean isCellEditable(int rowIndex, int columnIndex) {
										return columnEditable[columnIndex];
									}
								});
								{
									TableColumnModel cm = peopleTable.getColumnModel();
									cm.getColumn(1).setPreferredWidth(195);
									cm.getColumn(3).setPreferredWidth(145);
								}
								peopleScrollPane.setViewportView(peopleTable);
							}
							peoplePanel.add(peopleScrollPane);
						}
						mainTabbedPane.addTab("People (3/4)", new ImageIcon(getClass().getResource("/resources/icons/people.png")), peoplePanel);


						//======== filesPanel ========
						{
							filesPanel.setLayout(new BorderLayout());

							//======== filesScrollPane ========
							{
								filesScrollPane.setComponentPopupMenu(filesPopupMenu);

								//---- filesTable ----
								filesTable.setModel(new DefaultTableModel(
									new Object[][] {
										{"SEPM_SS08_Artefaktenbeschreibung.pdf", "1 KB", "! released", "Latest", "Yesterday", "Peter"},
										{"SEPM_VO_Block_1.pdf", "5 MB", "obsolete", "remotely changed", "March 18", "Chris"},
										{"SEPM_SS08_Artefaktenliste.pdf", "10 MB", "!", "locally changed", "Today, 12:00", "Simon"},
										{"ToDos.txt", "5 KB", "todo", "not synced", "Today 12:00", "Dominik"},
									},
									new String[] {
										"Name", "Size", "Tags", "Sync Status", "Last Changed", "User"
									}
								) {
									boolean[] columnEditable = new boolean[] {
										false, false, true, false, false, true
									};
									@Override
									public boolean isCellEditable(int rowIndex, int columnIndex) {
										return columnEditable[columnIndex];
									}
								});
								{
									TableColumnModel cm = filesTable.getColumnModel();
									cm.getColumn(0).setPreferredWidth(245);
									cm.getColumn(1).setPreferredWidth(50);
									cm.getColumn(2).setPreferredWidth(75);
								}
								filesTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
								filesTable.setPreferredScrollableViewportSize(new Dimension(450, 379));
								filesTable.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
								filesScrollPane.setViewportView(filesTable);
							}
							filesPanel.add(filesScrollPane, BorderLayout.CENTER);
						}
						mainTabbedPane.addTab("Files (4/10 MB)", new ImageIcon(getClass().getResource("/resources/icons/files.png")), filesPanel);


						//======== notesPanel ========
						{
							notesPanel.setLayout(new BorderLayout());

							//======== notesScrollPane ========
							{
								notesScrollPane.setComponentPopupMenu(notesPopupMenu);

								//---- notesTable ----
								notesTable.setModel(new DefaultTableModel(
									new Object[][] {
										{"Update 1", "", "Today", "Peter"},
										{"Aufgaben und Ziele", "!", "Yesterday, 11:00", "Simon"},
										{"Testnotiz 1", "test", "April 12th", "Johannes"},
									},
									new String[] {
										"Title", "Tags", "Last changed", "User"
									}
								) {
									boolean[] columnEditable = new boolean[] {
										true, true, true, false
									};
									@Override
									public boolean isCellEditable(int rowIndex, int columnIndex) {
										return columnEditable[columnIndex];
									}
								});
								{
									TableColumnModel cm = notesTable.getColumnModel();
									cm.getColumn(0).setPreferredWidth(265);
								}
								notesTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
								notesScrollPane.setViewportView(notesTable);
							}
							notesPanel.add(notesScrollPane, BorderLayout.NORTH);
						}
						mainTabbedPane.addTab("Notes (3)", new ImageIcon(getClass().getResource("/resources/icons/notes.png")), notesPanel);

					}
					mainPanel.add(mainTabbedPane, BorderLayout.CENTER);

					//======== mainToolBar ========
					{
						mainToolBar.setBorderPainted(false);
						mainToolBar.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
						mainToolBar.setRollover(true);

						//---- openProjectFolderButton ----
						openProjectFolderButton.setHorizontalAlignment(SwingConstants.RIGHT);
						openProjectFolderButton.setIcon(new ImageIcon(getClass().getResource("/resources/icons/project_folder.png")));
						openProjectFolderButton.setToolTipText("Open Project Folder");
						openProjectFolderButton.setText("Open Project Folder");
						mainToolBar.add(openProjectFolderButton);

						//---- refreshDatapoolViewButton ----
						refreshDatapoolViewButton.setHorizontalAlignment(SwingConstants.RIGHT);
						refreshDatapoolViewButton.setToolTipText("Refresh Datapool View");
						refreshDatapoolViewButton.setIcon(new ImageIcon(getClass().getResource("/resources/icons/sync_project_folder.png")));
						mainToolBar.add(refreshDatapoolViewButton);
						mainToolBar.addSeparator();

						//---- propagateFileButton ----
						propagateFileButton.setIcon(new ImageIcon(getClass().getResource("/resources/icons/push.png")));
						propagateFileButton.setToolTipText("Propagate locally changed file");
						mainToolBar.add(propagateFileButton);

						//---- pullFilesButton ----
						pullFilesButton.setIcon(new ImageIcon(getClass().getResource("/resources/icons/pull.png")));
						pullFilesButton.setToolTipText("Pull file from project member");
						mainToolBar.add(pullFilesButton);

						//---- LockFileToggleButton ----
						LockFileToggleButton.setIcon(new ImageIcon(getClass().getResource("/resources/icons/lock.png")));
						LockFileToggleButton.setToolTipText("Lock File...");
						mainToolBar.add(LockFileToggleButton);
						mainToolBar.addSeparator();

						//---- newNoteButton ----
						newNoteButton.setToolTipText("New Note");
						newNoteButton.setIcon(new ImageIcon(getClass().getResource("/resources/icons/notes-new.png")));
						mainToolBar.add(newNoteButton);
						mainToolBar.add(searchSpacer);

						//---- searchLabel ----
						searchLabel.setToolTipText("Search for");
						searchLabel.setIcon(new ImageIcon(getClass().getResource("/resources/icons/search.png")));
						searchLabel.setComponentPopupMenu(searchPopupMenu);
						searchLabel.setLabelFor(searchTextField);
						mainToolBar.add(searchLabel);

						//---- searchTextField ----
						searchTextField.setToolTipText("Search for Files");
						searchTextField.setMaximumSize(new Dimension(150, 2147483647));
						searchTextField.setPreferredSize(new Dimension(150, 28));
						searchTextField.setComponentPopupMenu(searchPopupMenu);
						mainToolBar.add(searchTextField);
					}
					mainPanel.add(mainToolBar, BorderLayout.NORTH);
				}
				this.add(mainPanel, BorderLayout.CENTER);

				//======== mainMenuBar ========
				{

					//======== fileMenu ========
					{
						fileMenu.setText("File");

						//---- newProjectMenuItem ----
						newProjectMenuItem.setText("New Project...");
						fileMenu.add(newProjectMenuItem);

						//---- openProjectMenuItem ----
						openProjectMenuItem.setText("Open Project...");
						fileMenu.add(openProjectMenuItem);

						//---- saveMenuItem ----
						saveMenuItem.setText("Save");
						fileMenu.add(saveMenuItem);

						//---- saveAsMenuItem ----
						saveAsMenuItem.setText("Save As...");
						fileMenu.add(saveAsMenuItem);

						//---- exitApplicationMenuItem ----
						exitApplicationMenuItem.setText("Exit");
						exitApplicationMenuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								exitApplicationMenuItemActionPerformed(e);
							}
						});
						fileMenu.add(exitApplicationMenuItem);
					}
					mainMenuBar.add(fileMenu);

					//======== viewMenu ========
					{
						viewMenu.setText("View");

						//---- peopleViewMenuItem ----
						peopleViewMenuItem.setText("People");
						peopleViewMenuItem.setIcon(new ImageIcon(getClass().getResource("/resources/icons/people.png")));
						viewMenu.add(peopleViewMenuItem);

						//---- filesViewMenuItem ----
						filesViewMenuItem.setText("Files");
						filesViewMenuItem.setIcon(new ImageIcon(getClass().getResource("/resources/icons/files.png")));
						viewMenu.add(filesViewMenuItem);

						//---- notesViewMenuItem ----
						notesViewMenuItem.setText("Notes");
						notesViewMenuItem.setIcon(new ImageIcon(getClass().getResource("/resources/icons/notes.png")));
						viewMenu.add(notesViewMenuItem);
						viewMenu.addSeparator();

						//---- systemLogViewMenuItem ----
						systemLogViewMenuItem.setText("System Log");
						systemLogViewMenuItem.setIcon(new ImageIcon(getClass().getResource("/resources/icons/log.png")));
						viewMenu.add(systemLogViewMenuItem);
					}
					mainMenuBar.add(viewMenu);

					//======== networkMenu ========
					{
						networkMenu.setText("Network");

						//---- signInNetworkMenuItem ----
						signInNetworkMenuItem.setText("Sign In...");
						networkMenu.add(signInNetworkMenuItem);

						//---- signOutNetworkMenuItem ----
						signOutNetworkMenuItem.setText("Sign Out");
						networkMenu.add(signOutNetworkMenuItem);
						networkMenu.addSeparator();

						//---- showOfflineMembersCheckBoxMenuItem ----
						showOfflineMembersCheckBoxMenuItem.setText("Show Offline Members");
						showOfflineMembersCheckBoxMenuItem.setSelected(true);
						networkMenu.add(showOfflineMembersCheckBoxMenuItem);

						//---- checkBoxMenuItem2 ----
						checkBoxMenuItem2.setText("Automatic Datapool Refresh");
						checkBoxMenuItem2.setSelected(true);
						networkMenu.add(checkBoxMenuItem2);

						//---- autoFilePropagateCheckBoxMenuItem ----
						autoFilePropagateCheckBoxMenuItem.setText("Automatic File Propagation");
						autoFilePropagateCheckBoxMenuItem.setSelected(true);
						networkMenu.add(autoFilePropagateCheckBoxMenuItem);

						//---- autoFilePullCheckBoxMenuItem ----
						autoFilePullCheckBoxMenuItem.setText("Automatic File Pull");
						autoFilePullCheckBoxMenuItem.setSelected(true);
						networkMenu.add(autoFilePullCheckBoxMenuItem);
					}
					mainMenuBar.add(networkMenu);

					//======== projectMenu ========
					{
						projectMenu.setText("Project");

						//---- openProjectFolderMenuItem ----
						openProjectFolderMenuItem.setText("Open Project Folder");
						projectMenu.add(openProjectFolderMenuItem);

						//---- refreshDatapoolViewProjectMenuItem ----
						refreshDatapoolViewProjectMenuItem.setText("Refresh Datapool View");
						projectMenu.add(refreshDatapoolViewProjectMenuItem);

						//---- newNoteProjectMenuItem ----
						newNoteProjectMenuItem.setText("New Note...");
						projectMenu.add(newNoteProjectMenuItem);
						projectMenu.addSeparator();

						//---- addFileToProjectMenuItem ----
						addFileToProjectMenuItem.setText("Add File...");
						projectMenu.add(addFileToProjectMenuItem);

						//---- addFolderToProjectMenuItem ----
						addFolderToProjectMenuItem.setText("Add Folder...");
						projectMenu.add(addFolderToProjectMenuItem);
						projectMenu.addSeparator();

						//---- addProjectMemberMenuItem ----
						addProjectMemberMenuItem.setText("Add Project Member...");
						projectMenu.add(addProjectMemberMenuItem);
					}
					mainMenuBar.add(projectMenu);

					//======== helpMenu ========
					{
						helpMenu.setText("Help");

						//---- aboutHelpMenuItem ----
						aboutHelpMenuItem.setText("About");
						helpMenu.add(aboutHelpMenuItem);
					}
					mainMenuBar.add(helpMenu);
				}
				this.add(mainMenuBar, BorderLayout.NORTH);
			}
			frame1ContentPane.add(this, BorderLayout.CENTER);
			mainFrame.pack();
			mainFrame.setLocationRelativeTo(mainFrame.getOwner());
			mainFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE ); 
			//mainFrame.setSize( 300, 200 ); 
			mainFrame.setVisible( true );
		}

		//======== peoplePopupMenu ========
		{

			//---- sendMessageMenuItem ----
			sendMessageMenuItem.setText("Send Message...");
			sendMessageMenuItem.setFont(new Font("Lucida Grande", Font.BOLD, 14));
			sendMessageMenuItem.setIcon(new ImageIcon(getClass().getResource("/resources/icons/message-new.png")));
			peoplePopupMenu.add(sendMessageMenuItem);

			//---- showInfoPeopleMenuItem ----
			showInfoPeopleMenuItem.setText("Show Info/Comments...");
			peoplePopupMenu.add(showInfoPeopleMenuItem);

			//---- renamePeopleMenuItem ----
			renamePeopleMenuItem.setText("Change Nickname...");
			peoplePopupMenu.add(renamePeopleMenuItem);

			//---- menuItem5 ----
			menuItem5.setText("Change User ID...");
			peoplePopupMenu.add(menuItem5);

			//---- removePeopleMenuItem ----
			removePeopleMenuItem.setText("Remove Member...");
			peoplePopupMenu.add(removePeopleMenuItem);
		}

		//======== filesPopupMenu ========
		{

			//---- openExecuteFileMenuItem ----
			openExecuteFileMenuItem.setText("Open");
			openExecuteFileMenuItem.setFont(new Font("Lucida Grande", Font.BOLD, 14));
			filesPopupMenu.add(openExecuteFileMenuItem);

			//---- lockFileMenuItem ----
			lockFileMenuItem.setText("Lock File...");
			filesPopupMenu.add(lockFileMenuItem);

			//---- deleteFileMenuItem ----
			deleteFileMenuItem.setText("Delete File...");
			filesPopupMenu.add(deleteFileMenuItem);

			//---- viewLogForFileMenuItem ----
			viewLogForFileMenuItem.setText("View Log...");
			filesPopupMenu.add(viewLogForFileMenuItem);

			//---- resolveFileConflictMenuItem ----
			resolveFileConflictMenuItem.setText("Resolve Conflict...");
			filesPopupMenu.add(resolveFileConflictMenuItem);
			filesPopupMenu.addSeparator();

			//---- propagateFileMenuItem ----
			propagateFileMenuItem.setText("Propagate File");
			propagateFileMenuItem.setToolTipText("Propagate locally changed file");
			filesPopupMenu.add(propagateFileMenuItem);

			//---- menuItem4 ----
			menuItem4.setText("Pull File");
			filesPopupMenu.add(menuItem4);
		}

		//======== notesPopupMenu ========
		{

			//---- viewEditNoteMenuItem ----
			viewEditNoteMenuItem.setText("View/Edit Note");
			viewEditNoteMenuItem.setFont(new Font("Lucida Grande", Font.BOLD, 14));
			notesPopupMenu.add(viewEditNoteMenuItem);

			//---- newNoteMenuItem ----
			newNoteMenuItem.setText("New Note...");
			notesPopupMenu.add(newNoteMenuItem);

			//---- removeNoteMenuItem ----
			removeNoteMenuItem.setText("Remove");
			notesPopupMenu.add(removeNoteMenuItem);
		}

		//======== searchPopupMenu ========
		{

			//---- nameSearchMenuItem ----
			nameSearchMenuItem.setText("Name");
			searchPopupMenu.add(nameSearchMenuItem);

			//---- tagsSearchMenuItem ----
			tagsSearchMenuItem.setText("Tags");
			searchPopupMenu.add(tagsSearchMenuItem);

			//---- bothSearchMenuItem ----
			bothSearchMenuItem.setText("Both");
			searchPopupMenu.add(bothSearchMenuItem);
		}
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	private JFrame mainFrame;
	private JPanel statusPanel;
	private JLabel statusLabel;
	private JPanel panel1;
	private JButton messageReceivedLabel;
	private JButton fileConflictLabel;
	private JButton connectionLabel;
	private JPanel mainPanel;
	private JTabbedPane mainTabbedPane;
	private JPanel peoplePanel;
	private JScrollPane peopleScrollPane;
	private JTable peopleTable;
	private JPanel filesPanel;
	private JScrollPane filesScrollPane;
	private JTable filesTable;
	private JPanel notesPanel;
	private JScrollPane notesScrollPane;
	private JTable notesTable;
	private JToolBar mainToolBar;
	private JButton openProjectFolderButton;
	private JButton refreshDatapoolViewButton;
	private JButton propagateFileButton;
	private JButton pullFilesButton;
	private JToggleButton LockFileToggleButton;
	private JButton newNoteButton;
	private JPanel searchSpacer;
	private JLabel searchLabel;
	private JTextField searchTextField;
	private JMenuBar mainMenuBar;
	private JMenu fileMenu;
	private JMenuItem newProjectMenuItem;
	private JMenuItem openProjectMenuItem;
	private JMenuItem saveMenuItem;
	private JMenuItem saveAsMenuItem;
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
	private JMenuItem menuItem5;
	private JMenuItem removePeopleMenuItem;
	private JPopupMenu filesPopupMenu;
	private JMenuItem openExecuteFileMenuItem;
	private JMenuItem lockFileMenuItem;
	private JMenuItem deleteFileMenuItem;
	private JMenuItem viewLogForFileMenuItem;
	private JMenuItem resolveFileConflictMenuItem;
	private JMenuItem propagateFileMenuItem;
	private JMenuItem menuItem4;
	private JPopupMenu notesPopupMenu;
	private JMenuItem viewEditNoteMenuItem;
	private JMenuItem newNoteMenuItem;
	private JMenuItem removeNoteMenuItem;
	private JPopupMenu searchPopupMenu;
	private JMenuItem nameSearchMenuItem;
	private JMenuItem tagsSearchMenuItem;
	private JMenuItem bothSearchMenuItem;
}
