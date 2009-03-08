package com.jakeapp.gui.swing.panels;

import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.JakeContext;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.actions.InviteUsersAction;
import com.jakeapp.gui.swing.actions.RenameUsersAction;
import com.jakeapp.gui.swing.actions.StartStopProjectAction;
import com.jakeapp.gui.swing.actions.SyncUsersAction;
import com.jakeapp.gui.swing.actions.TrustFullPeopleAction;
import com.jakeapp.gui.swing.actions.TrustNoPeopleAction;
import com.jakeapp.gui.swing.actions.TrustPeopleAction;
import com.jakeapp.gui.swing.callbacks.ContextChanged;
import com.jakeapp.gui.swing.callbacks.DataChanged;
import com.jakeapp.gui.swing.callbacks.ProjectChanged;
import com.jakeapp.gui.swing.controls.JListMutable;
import com.jakeapp.gui.swing.controls.PeopleListCellEditor;
import com.jakeapp.gui.swing.controls.cmacwidgets.ITunesTable;
import com.jakeapp.gui.swing.helpers.ConfigControlsHelper;
import com.jakeapp.gui.swing.helpers.JakePopupMenu;
import com.jakeapp.gui.swing.helpers.Platform;
import com.jakeapp.gui.swing.helpers.ProjectHelper;
import com.jakeapp.gui.swing.models.EventsTableModel;
import com.jakeapp.gui.swing.models.PeopleListModel;
import com.jakeapp.gui.swing.renderer.PeopleListCellRenderer;
import com.jakeapp.gui.swing.xcore.EventCore;
import org.apache.log4j.Logger;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.EnumSet;

/**
 * First Panel in Project View. Shows recent evetns and people list.
 *
 * @author studpete
 */
public class NewsPanel extends javax.swing.JPanel
				implements ProjectChanged, DataChanged, ContextChanged {

	private static final long serialVersionUID = -6867091182930736758L;
	private static final Logger log = Logger.getLogger(NewsPanel.class);
	private Project project;
	private ResourceMap resourceMap;
	private Icon startIcon;
	private Icon stopIcon;
	private Icon invalidIcon;
	private StartStopProjectAction startStopProjectAction =
					new StartStopProjectAction();
	private Timer eventsTableUpdateTimer;
	private final static int EventTableUpdateDelay = 20000;
	// 20 sec //FIXME: magic number
	private EventsTableModel eventTableModel;

	private javax.swing.JPanel actionPanel;
	private javax.swing.JCheckBox autoDownloadCB;
	private javax.swing.JCheckBox autoUploadCB;
	private javax.swing.JLabel eventsLabel;
	private javax.swing.JScrollPane eventsScrollPanel;
	private org.jdesktop.swingx.JXTable eventsTable;
	private org.jdesktop.swingx.JXPanel newsContentPanel;
	private javax.swing.JLabel optionsLabel;
	private javax.swing.JPanel optionsPanel;
	private javax.swing.JLabel peopleLabel;
	private org.jdesktop.swingx.JXList usersList;
	private javax.swing.JScrollPane peopleScrollPanel;
	private org.jdesktop.swingx.JXHyperlink projectFolderHyperlink;
	private javax.swing.JLabel projectIconLabel;
	private javax.swing.JLabel projectLabel;
	private javax.swing.JButton projectRunningButton;
	private javax.swing.JLabel projectStatusLabel;
	private javax.swing.JPanel projectTitlePanel;
	private javax.swing.JPanel titlePanel;

	/**
	 * Creates new form NewsPanel
	 */
	// FIXME: rewrite in miglayout!
	public NewsPanel() {
		initComponents();
		setResourceMap(org.jdesktop.application.Application
						.getInstance(com.jakeapp.gui.swing.JakeMainApp.class)
						.getContext().getResourceMap(NewsPanel.class));

		// register the callbacks
		EventCore.get().addProjectChangedCallbackListener(this);
		EventCore.get().addContextChangedListener(this);

		// init actions!
		this.projectRunningButton.setAction(this.startStopProjectAction);

		// ensure opaque(=draw background) is false (default on mac, not default on win/lin)
		this.autoUploadCB.setOpaque(false);
		this.autoDownloadCB.setOpaque(false);

		// set the background painter
		this.newsContentPanel.setBackgroundPainter(Platform
						.getStyler().getContentPanelBackgroundPainter());

		this.startIcon = new ImageIcon(Toolkit
						.getDefaultToolkit().getImage(getClass().getResource(
						"/icons/folder-open.png")));
		this.stopIcon = new ImageIcon(Toolkit
						.getDefaultToolkit().getImage(getClass().getResource("/icons/folder.png")));
		this.invalidIcon = new ImageIcon(Toolkit
						.getDefaultToolkit().getImage(getClass().getResource(
						"/icons/folder_invalid.png")));

		this.autoDownloadCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				JakeMainApp.getCore().setProjectSettings(JakeContext.getProject(),
								autoDownloadCB.isSelected(),
								null);
			}
		});

		this.autoUploadCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				JakeMainApp.getCore().setProjectSettings(JakeContext.getProject(),
								null,
								autoUploadCB.isSelected());
			}
		});

		// configure the people list
		this.usersList.setHighlighters(HighlighterFactory.createSimpleStriping());
		this.usersList.setModel(new PeopleListModel());
		this.usersList.setCellRenderer(new PeopleListCellRenderer());
		((JListMutable) this.usersList)
						.setListCellEditor(new PeopleListCellEditor(new JTextField()));

		this.usersList.addMouseListener(new UsersListMouseListener());

		// config the recent events table
		this.eventTableModel = new EventsTableModel(getProject());
		this.eventsTable.setModel(this.eventTableModel);
		ConfigControlsHelper.configEventsTable(this.eventsTable);

		this.eventsTable.getColumnModel().getColumn(1).setWidth(130);

		//eventsTable.setBorder(BorderFactory.createEtchedBorder());
		this.eventsTable.addMouseListener(new EventsTableMouseListener());

		// install event table update timer
		this.eventsTableUpdateTimer =
						new Timer(EventTableUpdateDelay, new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent actionEvent) {
								//log.debug("Updating eventsTable");
								eventsTable.updateUI();
							}
						});
		this.eventsTableUpdateTimer.start();
	}

	@Override public void dataChanged(EnumSet<DataReason> dataReason, Project p) {
		if (dataReason.contains(DataReason.LogEntries)) {
			this.updatePanel();
		}
	}


	@Override public void contextChanged(EnumSet<ContextChanged.Reason> reason,
		Object context) {
		this.updatePanel();
	}


	/**
	 * private inner mouselistener for events table.
	 */
	// TODO: make hyperlinks?
	private class EventsTableMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent mouseEvent) {
		}

		@Override
		public void mousePressed(MouseEvent mouseEvent) {
		}

		@Override
		public void mouseReleased(MouseEvent mouseEvent) {
		}

		@Override
		public void mouseEntered(MouseEvent mouseEvent) {
		}

		@Override
		public void mouseExited(MouseEvent mouseEvent) {
		}
	}

	private class UsersListMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent me) {
			if (SwingUtilities.isRightMouseButton(me)) {
				log.trace("right clicked");
				// get the coordinates of the mouse click
				Point p = me.getPoint();

				// get the row index that contains that coordinate
				int rowNumber = usersList.locationToIndex(p);

				// Get the ListSelectionModel of the JTable
				ListSelectionModel model = usersList.getSelectionModel();

				// set the selected interval of rows. Using the "rowNumber"
				// variable for the beginning and end selects only that one
				// row.
				// ONLY select new item if we didn't select multiple items.
				if (usersList.getSelectedValues().length <= 1) {
					model.setSelectionInterval(rowNumber, rowNumber);
				}
				showUsersMenu(me);
			}
		}

		private void showUsersMenu(MouseEvent me) {
			log.trace("triggered popup event");

			JPopupMenu pm = new JakePopupMenu();

			pm.add(new JMenuItem(new InviteUsersAction(true)));
			pm.add(new JSeparator());
			pm.add(new JMenuItem(new RenameUsersAction((JListMutable) usersList)));
			pm.add(new JSeparator());
			pm.add(new JMenuItem(new SyncUsersAction(usersList)));
			pm.add(new JSeparator());
			pm.add(new JCheckBoxMenuItem(new TrustFullPeopleAction(usersList)));
			pm.add(new JCheckBoxMenuItem(new TrustPeopleAction(usersList)));
			pm.add(new JCheckBoxMenuItem(new TrustNoPeopleAction(usersList)));

			pm.show(usersList, (int) me.getPoint().getX(), (int) me.getPoint().getY());
		}

		@Override
		public void mousePressed(MouseEvent mouseEvent) {
			log.trace("mousePressed");
			//showUsersMenu(mouseEvent);
		}

		@Override
		public void mouseReleased(MouseEvent mouseEvent) {
			log.trace("mouseReleased");
			//showUsersMenu(mouseEvent);
		}

		@Override
		public void mouseEntered(MouseEvent mouseEvent) {
		}

		@Override
		public void mouseExited(MouseEvent mouseEvent) {
		}
	}


	/**
	 * Update the news panel.
	 */
	private void updatePanel() {
		log.trace("updating panel with " + getProject());

		// don't update if project is null
		if (getProject() == null) {
			return;
		}

		// set model project
		this.eventTableModel.setProject(getProject());


		/*
//TODO: find better way !
		try {
			FolderObject rootPath = JakeMainApp.getApp().getCore().getProjectRootFolder(getProject());
		} catch (ProjectFolderMissingException e) {
			log.warn("Project root path " + getProject().getRootPath() + " is invalid.");
			projectStatusLabel.setText("ERROR: Project folder does not exist");
			projectStatusLabel.setForeground(Color.RED);
			projectIconLabel.setIcon(invalidIcon);

			return;
		}*/

		this.projectStatusLabel.setForeground(Color.BLACK);

		// update all text in panel
		this.projectLabel.setText(getProject().getName());
		this.projectFolderHyperlink.setText(getProject().getRootPath());
		this.projectStatusLabel.setText(ProjectHelper.printProjectStatus(getProject()));
		this.autoDownloadCB.setSelected(getProject().isAutoPullEnabled());
		this.autoUploadCB.setSelected(getProject().isAutoAnnounceEnabled());

		// update the checkboxes
		this.autoDownloadCB.setSelected(getProject().isAutoPullEnabled());
		this.autoUploadCB.setSelected(getProject().isAutoAnnounceEnabled());

		// update the icon (start/stop-state)
		this.projectIconLabel.setIcon(getProject().isStarted() ? startIcon : stopIcon);

		/*
		// FIXME?
		SourceListControlBar cb = new SourceListControlBar();
		cb.createAndAddButton(startIcon, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JSheet.showMessageSheet(usersList, "Bla");
			}
		});

		this.add(cb.getComponent());
		*/

	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		this.newsContentPanel = new org.jdesktop.swingx.JXPanel();
		this.titlePanel = new javax.swing.JPanel();
		this.projectFolderHyperlink = new org.jdesktop.swingx.JXHyperlink();
		this.projectStatusLabel = new javax.swing.JLabel();
		this.projectRunningButton = new javax.swing.JButton();
		this.projectIconLabel = new javax.swing.JLabel();
		this.projectTitlePanel = new javax.swing.JPanel();
		this.projectLabel = new javax.swing.JLabel();
		this.actionPanel = new javax.swing.JPanel();
		this.eventsLabel = new javax.swing.JLabel();
		this.eventsScrollPanel = new javax.swing.JScrollPane();

		this.eventsTable = new ITunesTable();

		this.peopleScrollPanel = new javax.swing.JScrollPane();
		this.usersList = new JListMutable();
		this.peopleLabel = new javax.swing.JLabel();
		this.optionsPanel = new javax.swing.JPanel();
		this.autoUploadCB = new javax.swing.JCheckBox();
		this.optionsLabel = new javax.swing.JLabel();
		this.autoDownloadCB = new javax.swing.JCheckBox();

		org.jdesktop.application.ResourceMap resourceMap =
						org.jdesktop.application.Application
										.getInstance(com.jakeapp.gui.swing.JakeMainApp.class)
										.getContext().getResourceMap(NewsPanel.class);
		setBackground(resourceMap.getColor("Form.background")); // NOI18N
		setName("Form"); // NOI18N
		setLayout(new java.awt.BorderLayout());

		this.newsContentPanel
						.setBackground(resourceMap.getColor("newsContentPanel.background")); // NOI18N
		this.newsContentPanel.setName("newsContentPanel"); // NOI18N
		this.newsContentPanel.setLayout(new javax.swing.BoxLayout(newsContentPanel,
						javax.swing.BoxLayout.Y_AXIS));

		this.titlePanel.setMaximumSize(new java.awt.Dimension(32767, 120));
		this.titlePanel.setMinimumSize(new java.awt.Dimension(389, 120));
		this.titlePanel.setName("titlePanel"); // NOI18N
		this.titlePanel.setOpaque(false);
		this.titlePanel.setPreferredSize(new java.awt.Dimension(389, 120));

		this.projectFolderHyperlink
						.setText(resourceMap.getString("projectFolderHyperlink.text")); // NOI18N
		this.projectFolderHyperlink.setFocusable(false);
		this.projectFolderHyperlink.setName("projectFolderHyperlink"); // NOI18N
		this.projectFolderHyperlink
						.addActionListener(new java.awt.event.ActionListener() {
							public void actionPerformed(java.awt.event.ActionEvent evt) {
								projectFolderHyperlinkActionPerformed(evt);
							}
						});

		this.projectStatusLabel
						.setText(resourceMap.getString("projectStatusLabel.text")); // NOI18N
		this.projectStatusLabel.setName("projectStatusLabel"); // NOI18N

		this.projectRunningButton
						.setText(resourceMap.getString("projectRunningButton.text")); // NOI18N
		this.projectRunningButton.setName("projectRunningButton"); // NOI18N

		this.projectIconLabel
						.setIcon(resourceMap.getIcon("projectIconLabel.icon")); // NOI18N
		this.projectIconLabel.setName("projectIconLabel"); // NOI18N

		this.projectTitlePanel.setName("projectTitlePanel"); // NOI18N
		this.projectTitlePanel.setOpaque(false);
		this.projectTitlePanel
						.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));

		this.projectLabel.setFont(resourceMap.getFont("projectLabel.font")); // NOI18N
		this.projectLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		this.projectLabel.setText(resourceMap.getString("projectLabel.text")); // NOI18N
		this.projectLabel.setName("projectLabel"); // NOI18N

		javax.swing.GroupLayout titlePanelLayout =
						new javax.swing.GroupLayout(this.titlePanel);
		this.titlePanel.setLayout(titlePanelLayout);
		titlePanelLayout.setHorizontalGroup(titlePanelLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
						titlePanelLayout.createSequentialGroup().addGroup(titlePanelLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(titlePanelLayout.createSequentialGroup()
														.addContainerGap().addComponent(this.projectRunningButton)).addGroup(
										titlePanelLayout.createSequentialGroup()
														.addGap(28, 28, 28).addComponent(this.projectIconLabel)))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(titlePanelLayout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(this.projectTitlePanel,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		515,
																		Short.MAX_VALUE)
														.addComponent(this.projectFolderHyperlink,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(this.projectLabel).addComponent(this.projectStatusLabel)).addContainerGap()));
		titlePanelLayout.setVerticalGroup(titlePanelLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
						titlePanelLayout.createSequentialGroup().addContainerGap()
										.addGroup(titlePanelLayout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING,
																		false)
														.addGroup(titlePanelLayout.createSequentialGroup()
																		.addComponent(this.projectIconLabel).addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)).addGroup(
														titlePanelLayout.createSequentialGroup()
																		.addComponent(this.projectLabel)
																		.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(this.projectTitlePanel,
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						Short.MAX_VALUE)
																		.addComponent(this.projectFolderHyperlink,
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						javax.swing.GroupLayout.PREFERRED_SIZE).addGap(
																		11,
																		11,
																		11))).addGroup(titlePanelLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(this.projectRunningButton).addComponent(this.projectStatusLabel)).addGap(
										30,
										30,
										30)));

		this.newsContentPanel.add(this.titlePanel);

		this.actionPanel.setName("actionPanel"); // NOI18N
		this.actionPanel.setOpaque(false);

		this.eventsLabel.setFont(resourceMap.getFont("eventsLabel.font")); // NOI18N
		this.eventsLabel
						.setForeground(resourceMap.getColor("eventsLabel.foreground")); // NOI18N
		this.eventsLabel.setText(resourceMap.getString("eventsLabel.text")); // NOI18N
		this.eventsLabel.setName("eventsLabel"); // NOI18N

		this.eventsScrollPanel.setName("eventsScrollPanel"); // NOI18N
		this.eventsScrollPanel.setViewportView(this.eventsTable);

		this.peopleScrollPanel.setName("peopleScrollPanel"); // NOI18N

		this.usersList.setDoubleBuffered(true);
		this.usersList.setDragEnabled(true);
		this.usersList.setName("usersList"); // NOI18N
		this.usersList.setRolloverEnabled(true);
		this.peopleScrollPanel.setViewportView(this.usersList);

		this.peopleLabel.setFont(resourceMap.getFont("peopleLabel.font")); // NOI18N
		this.peopleLabel
						.setForeground(resourceMap.getColor("peopleLabel.foreground")); // NOI18N
		this.peopleLabel.setText(resourceMap.getString("peopleLabel.text")); // NOI18N
		this.peopleLabel.setName("peopleLabel"); // NOI18N

		javax.swing.GroupLayout actionPanelLayout =
						new javax.swing.GroupLayout(this.actionPanel);
		this.actionPanel.setLayout(actionPanelLayout);
		actionPanelLayout.setHorizontalGroup(actionPanelLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
						actionPanelLayout.createSequentialGroup().addContainerGap()
										.addGroup(actionPanelLayout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(this.eventsLabel).addComponent(this.eventsScrollPanel,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														401,
														Short.MAX_VALUE))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(actionPanelLayout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(this.peopleLabel).addComponent(this.peopleScrollPanel,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														184,
														javax.swing.GroupLayout.PREFERRED_SIZE)).addContainerGap()));
		actionPanelLayout.setVerticalGroup(actionPanelLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						actionPanelLayout.createSequentialGroup().addGroup(actionPanelLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(this.eventsLabel).addComponent(this.peopleLabel))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
										actionPanelLayout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(this.eventsScrollPanel,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		262,
																		Short.MAX_VALUE).addComponent(this.peopleScrollPanel,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														262,
														Short.MAX_VALUE))));

		this.newsContentPanel.add(this.actionPanel);

		this.optionsPanel.setMaximumSize(new java.awt.Dimension(32767, 100));
		this.optionsPanel.setMinimumSize(new java.awt.Dimension(0, 100));
		this.optionsPanel.setName("optionsPanel"); // NOI18N
		this.optionsPanel.setOpaque(false);
		this.optionsPanel.setPreferredSize(new java.awt.Dimension(697, 100));

		this.autoUploadCB.setSelected(true);
		this.autoUploadCB.setText(resourceMap.getString("autoUploadCB.text")); // NOI18N
		this.autoUploadCB.setName("autoUploadCB"); // NOI18N

		this.optionsLabel.setFont(resourceMap.getFont("optionsLabel.font")); // NOI18N
		this.optionsLabel
						.setForeground(resourceMap.getColor("optionsLabel.foreground")); // NOI18N
		this.optionsLabel.setText(resourceMap.getString("optionsLabel.text")); // NOI18N
		this.optionsLabel.setName("optionsLabel"); // NOI18N

		this.autoDownloadCB.setSelected(true);
		this.autoDownloadCB
						.setText(resourceMap.getString("autoDownloadCB.text")); // NOI18N
		this.autoDownloadCB.setName("autoDownloadCB"); // NOI18N

		javax.swing.GroupLayout optionsPanelLayout =
						new javax.swing.GroupLayout(this.optionsPanel);
		this.optionsPanel.setLayout(optionsPanelLayout);
		optionsPanelLayout.setHorizontalGroup(optionsPanelLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
						optionsPanelLayout.createSequentialGroup().addContainerGap()
										.addGroup(optionsPanelLayout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(optionsPanelLayout.createSequentialGroup()
																		.addComponent(this.optionsLabel,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						263,
																						Short.MAX_VALUE).addGap(168,
																		168,
																		168)).addGroup(optionsPanelLayout
														.createSequentialGroup().addGap(21, 21, 21).addGroup(
														optionsPanelLayout
																		.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING,
																						false).addComponent(this.autoUploadCB,
																		javax.swing.GroupLayout.Alignment.LEADING,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		Short.MAX_VALUE).addComponent(this.autoDownloadCB,
																		javax.swing.GroupLayout.Alignment.LEADING)))).addGap(
										441,
										441,
										441)));
		optionsPanelLayout.setVerticalGroup(optionsPanelLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						optionsPanelLayout.createSequentialGroup()
										.addContainerGap(20, Short.MAX_VALUE)
										.addComponent(this.optionsLabel)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(this.autoDownloadCB)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(this.autoUploadCB).addContainerGap()));

		this.newsContentPanel.add(this.optionsPanel);

		add(this.newsContentPanel, java.awt.BorderLayout.CENTER);
	}// </editor-fold>//GEN-END:initComponents

	private void projectFolderHyperlinkActionPerformed(
					java.awt.event.ActionEvent evt) {
		log.info("Opening the Folder: " + getProject().getRootPath());
		try {
			Desktop.getDesktop().open(new File(getProject().getRootPath()));
		} catch (IOException e) {
			log.warn("Unable to open folder: " + getProject().getRootPath(), e);
		}
	}

	public Project getProject() {
		return JakeContext.getProject();
	}


	public ResourceMap getResourceMap() {
		return this.resourceMap;
	}

	public void setResourceMap(ResourceMap resourceMap) {
		this.resourceMap = resourceMap;
	}

	public void projectChanged(ProjectChangedEvent ev) {
		updatePanel();
	}
}