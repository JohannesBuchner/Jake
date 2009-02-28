package com.jakeapp.gui.swing;

import com.explodingpixels.macwidgets.LabeledComponentGroup;
import com.explodingpixels.macwidgets.MacButtonFactory;
import com.explodingpixels.macwidgets.MacUtils;
import com.explodingpixels.macwidgets.MacWidgetFactory;
import com.explodingpixels.macwidgets.TriAreaComponent;
import com.explodingpixels.widgets.WindowUtils;
import com.jakeapp.core.domain.InvitationState;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.services.MsgService;
import com.jakeapp.gui.swing.actions.CreateProjectAction;
import com.jakeapp.gui.swing.actions.ImportFileAction;
import com.jakeapp.gui.swing.actions.InvitePeopleAction;
import com.jakeapp.gui.swing.actions.abstracts.ProjectAction;
import com.jakeapp.gui.swing.callbacks.ContextViewChanged;
import com.jakeapp.gui.swing.callbacks.MsgServiceChanged;
import com.jakeapp.gui.swing.callbacks.ProjectChanged;
import com.jakeapp.gui.swing.callbacks.ProjectSelectionChanged;
import com.jakeapp.gui.swing.callbacks.ProjectViewChanged;
import com.jakeapp.gui.swing.controls.SearchField;
import com.jakeapp.gui.swing.dialogs.JakeAboutDialog;
import com.jakeapp.gui.swing.filters.FileObjectNameFilter;
import com.jakeapp.gui.swing.helpers.*;
import com.jakeapp.gui.swing.helpers.dragdrop.FileDropHandler;
import com.jakeapp.gui.swing.panels.FilePanel;
import com.jakeapp.gui.swing.panels.InspectorPanel;
import com.jakeapp.gui.swing.panels.NewsPanel;
import com.jakeapp.gui.swing.panels.NotesPanel;
import com.jakeapp.gui.swing.panels.ProjectInvitationPanel;
import com.jakeapp.gui.swing.panels.UserPanel;
import com.jakeapp.gui.swing.worker.InitCoreWorker;
import com.jakeapp.gui.swing.xcore.EventCore;
import org.apache.log4j.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.PatternFilter;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.PatternSyntaxException;


/**
 * The application's main frame.
 */
public class JakeMainView extends FrameView implements ProjectSelectionChanged, MsgServiceChanged {
	private static final Logger log = Logger.getLogger(JakeMainView.class);
	private static final int CONTENT_SPLITTERSIZE = 2;
	private static JakeMainView mainView;
	private Project project;
	private boolean inspectorEnabled;

	// all the ui panels
	private NewsPanel newsPanel;
	private FilePanel filePanel;
	private NotesPanel notesPanel;
	private ProjectInvitationPanel invitationPanel;
	private UserPanel loginPanel;
	private List<JToggleButton> contextSwitcherButtons;
	private JPanel contextSwitcherPane = createContextSwitcherPanel();
	private JPanel inspectorPanel;
	private JakeMenuBar menuBar;
	private javax.swing.JPanel contentPanel;
	private JSplitPane contentPanelSplit;
	private JDialog aboutBox;

	private ProjectViewPanelEnum projectViewPanel = ProjectViewPanelEnum.News;
	private ContextPanelEnum contextViewPanel = ContextPanelEnum.Login;
	private JakeStatusBar jakeStatusBar;
	private JakeTrayIcon tray;

	private List<ProjectViewChanged> projectViewChanged = new ArrayList<ProjectViewChanged>();
	private List<ContextViewChanged> contextViewChanged = new ArrayList<ContextViewChanged>();
	private JPanel statusPanel;

	private JakeMainApp app;
	private JakeSourceList sourceList;
	private JSplitPane mainSplitPane;
	private SearchField searchField;

	private Image IconAppSmall;
	private Image IconAppLarge;

	public boolean isInspectorEnabled() {
		return inspectorEnabled;
	}

	public void setInspectorEnabled(boolean inspectorEnabled) {
		this.inspectorEnabled = inspectorEnabled;

		updateInspectorPanelVisibility();
	}

	/**
	 * Returns the large application image.
	 *
	 * @return
	 */
	public Image getLargeAppImage() {
		return IconAppLarge;
	}

	/**
	 * Project View: set of toggle buttons. Alwasy one state setup.
	 */
	public enum ProjectViewPanelEnum {
		News, Files, Notes
	}

	/**
	 * Special context states.
	 */
	public enum ContextPanelEnum {
		Login, Project, Invitation
	}

	// toolbar
	private AbstractButton createProjectButton;
	private AbstractButton addFilesButton;
	private AbstractButton invitePeopleButton;
	private AbstractButton inspectorButton;


	//	public JakeMainView(SingleFrameApplication app) {
	public JakeMainView(JakeMainApp app) {
		super(app);

		// show "progress"
		if (JakeMainApp.getApp().getSplashFrame() != null) {
			JakeMainApp.getApp().getSplashFrame().setTransparency(1F);
		}


		IconAppSmall = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
						getClass().getResource("/icons/jakeapp.png"))).getImage();

		IconAppLarge = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
						getClass().getResource("/icons/jakeapp-large.png"))).getImage();

		setMainView(this);
		this.app = app;

		tray = new JakeTrayIcon();

		// init the panels
		loginPanel = new UserPanel();
		newsPanel = new NewsPanel();
		filePanel = new FilePanel();
		notesPanel = new NotesPanel();
		inspectorPanel = new InspectorPanel();
		invitationPanel = new ProjectInvitationPanel();


		// initialize helper code
		JakeHelper.initializeJakeMainHelper();

		// macify-window
		if (Platform.isMac()) {
			MacUtils.makeWindowLeopardStyle(this.getFrame().getRootPane());
			setMacSystemProperties();
		}

		// set window icon (small, large)
		// large icon may be shown e.g. on big icon tab switch on vista
		this.getFrame().setIconImage(IconAppSmall);
		this.getFrame().setIconImages(Arrays.asList(IconAppSmall, IconAppLarge));

		// set app size
		this.getFrame().setMinimumSize(new Dimension(600, 600));
		this.getFrame().setSize(new Dimension(800, 800));

		// initialize the mantisse gui components (menu)
		initComponents();

		// adapt the menu if we live on a mac
		if (Platform.isMac()) {
			// install the close handler (meta-w)
			GuiUtilities.installMacCloseHandler(getFrame());
		}

		// init the content panel and the splitter
		contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());
		contentPanelSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, contentPanel,
						inspectorPanel);
		contentPanelSplit.setOneTouchExpandable(false);
		contentPanelSplit.setContinuousLayout(true);
		contentPanelSplit.setBorder(null);
		contentPanelSplit.setResizeWeight(1.0);
		contentPanelSplit.setEnabled(true);
		contentPanelSplit.setDividerSize(CONTENT_SPLITTERSIZE);
		contentPanelSplit
						.addPropertyChangeListener(new ResizeListener(contentPanelSplit));
		updateInspectorPanelVisibility();

		// add the toolbar
		TriAreaComponent toolBar = createToolBar();
		this.getFrame().add(toolBar.getComponent(), BorderLayout.NORTH);

		// create the panels split pane
		mainSplitPane = this.createSourceListAndMainArea();
		this.getFrame().add(mainSplitPane, BorderLayout.CENTER);

		// create status bar
		jakeStatusBar = new JakeStatusBar();
		statusPanel.add(jakeStatusBar.getComponent());

		// set default window behaviour
		WindowUtils.createAndInstallRepaintWindowFocusListener(this.getFrame());
		this.getFrame().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// register dragdrop handler
		this.getFrame().setTransferHandler(new FileDropHandler());

		registerCallbacks();

		setContextViewPanel(ContextPanelEnum.Login);

		updateTitle();

		if (JakeMainApp.getApp().getSplashFrame() != null) {
			JakeMainApp.getApp().getSplashFrame().setVisible(false);
		}

		JakeExecutor.exec(new InitCoreWorker());

		
		// debug property
		if (System.getProperty("com.jakeapp.gui.test.instantquit") != null) {
			JakeMainApp.getApp().saveQuit();
		}
	}


	/**
	 * This is a private inner class to control the resizing
	 * of the JSplitPane for the Inspector.
	 */
	private class ResizeListener implements PropertyChangeListener {
		private boolean inSplitPaneResized;
		private JSplitPane splitPane;

		public ResizeListener(JSplitPane splitPane) {
			this.splitPane = splitPane;
		}

		public void propertyChange(PropertyChangeEvent evt) {
			splitPaneResized(evt);
		}

		private void splitPaneResized(PropertyChangeEvent evt) {
			if (inSplitPaneResized) {
				return;
			}

			inSplitPaneResized = true;

			if (evt.getPropertyName().equalsIgnoreCase("dividerLocation")) {
				int current = splitPane.getDividerLocation();
				int max = splitPane.getMaximumDividerLocation();
				log.debug("splitPaneResized: current: " + current + " max: " + max);
				log.debug(
								"splitPane.getWidth()-current: " + (splitPane.getWidth() - current));

				// hide inspector!
				if ((splitPane.getWidth() - current) < 155) {

					// only hide if this was done by user
					// inspector is hidden sometimes, but remember the original state!
					if (isInspectorAllowed()) {
						setInspectorEnabled(false);
					}
				}
				// limit the minimum size manually, to detect closing wish
				else if ((splitPane.getWidth() - current) < InspectorPanel.INSPECTOR_SIZE) {
					splitPane.setDividerLocation(
									splitPane.getWidth() - InspectorPanel.INSPECTOR_SIZE);
					setInspectorEnabled(true);
				} else {
					setInspectorEnabled(true);
				}
			}

			inSplitPaneResized = false;
		}
	}


	/**
	 * Inner class that handles the project changed events
	 * for status bar / source list.
	 */
	private class ProjectChangedCallback implements ProjectChanged {

		public void projectChanged(ProjectChangedEvent ev) {
			log.info("Received project changed callback.");

			Runnable runner = new Runnable() {
				public void run() {
					updateAll();
				}
			};

			SwingUtilities.invokeLater(runner);
		}
	}

	/**
	 * Registers the callbacks with the core
	 */
	private void registerCallbacks() {
		// register for project selection changes
		JakeMainApp.getApp().addProjectSelectionChangedListener(this);

		EventCore.get().addProjectChangedCallbackListener(new ProjectChangedCallback());
		JakeMainApp.getApp().addMsgServiceChangedListener(this);
	}


	/**
	 * Public Resource Map
	 *
	 * @return the JakeMainView Resource Map.
	 */
	public static ResourceMap getResouceMap() {
		return mainView.getResourceMap();
	}


	/**
	 * Update the application title to show the project, once it's
	 */
	private void updateTitle() {
		String jakeStr = AppUtilities.getAppName();

		if (getProject() != null && getProject()
						.getInvitationState() == InvitationState.ACCEPTED) {
			String projectPath = getProject().getRootPath();
			getFrame().setTitle(projectPath + " - " + jakeStr);

			// mac only
			if (Platform.isMac()) {
				getFrame().getRootPane().putClientProperty("Window.documentFile",
								new File(getProject().getRootPath()));
			}
		} else {
			getFrame().setTitle(jakeStr);

			// mac only
			if (Platform.isMac()) {
				getFrame().getRootPane().putClientProperty("Window.documentFile", null);
			}
		}
	}

	/**
	 * Creates the unified toolbar on top.
	 *
	 * @return TriAreaComponent of toolbar.
	 */
	private TriAreaComponent createToolBar() {
		// create empty toolbar
		TriAreaComponent toolBar = MacWidgetFactory.createUnifiedToolBar();
		// Create Project
		ProjectAction createProjectAction;
		createProjectAction = new CreateProjectAction(false);
		JButton createProjectJButton = new JButton();
		createProjectJButton.setAction(createProjectAction);
		createProjectButton = MacButtonFactory
						.makeUnifiedToolBarButton(createProjectJButton);
		createProjectButton.setEnabled(true);
		createProjectButton.setBorder(new LineBorder(Color.BLACK, 0));
		toolBar.addComponentToLeft(createProjectButton, 10);

		// Add Files
		Icon addFilesIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
						getClass().getResource("/icons/toolbar-addfiles.png")).getScaledInstance(
						32, 32, Image.SCALE_SMOOTH));
		JButton jCreateAddFilesButton = new JButton(
						getResourceMap().getString("toolbarAddFiles"), addFilesIcon);

		addFilesButton = MacButtonFactory
						.makeUnifiedToolBarButton(jCreateAddFilesButton);
		addFilesButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				new ImportFileAction().openImportDialog();
			}
		});
		addFilesButton.setEnabled(true);
		jCreateAddFilesButton.setBorder(new LineBorder(Color.BLACK, 0));
		toolBar.addComponentToLeft(addFilesButton, 10);

		/*
		// Create Note
		Icon noteIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
				  getClass().getResource("/icons/notes.png")).getScaledInstance(32, 32, Image.SCALE_SMOOTH));
		JButton jCreateNodeButton = new JButton(getResourceMap().getString("toolbarCreateNote"), noteIcon);

		addFilesButton = MacButtonFactory.makeUnifiedToolBarButton(jCreateNodeButton);
		addFilesButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				addFilesAction();
			}
		});
		addFilesButton.setEnabled(true);
		jCreateNodeButton.setBorder(new LineBorder(Color.BLACK, 0));
		toolBar.addComponentToLeft(addFilesButton, 10);
		*/

		// Add People

		JButton invitePeopleJButton = new JButton(new InvitePeopleAction(false));
		invitePeopleButton = MacButtonFactory
						.makeUnifiedToolBarButton(invitePeopleJButton);
		invitePeopleButton.setBorder(new LineBorder(Color.BLACK, 0));
		toolBar.addComponentToLeft(invitePeopleButton, 10);


		/*
			 // Announce File
			 Icon announceIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
						getClass().getResourceMap("/icons/announce.png")).getScaledInstance(32, 32, Image.SCALE_SMOOTH));
			 JButton announceJButton = new JButton("Announce", announceIcon);


			 AbstractButton announceButton =
						MacButtonFactory.makeUnifiedToolBarButton(announceJButton);

			 announceButton.setEnabled(true);
			 announceJButton.setBorder(new LineBorder(Color.BLACK, 0));
			 toolBar.addComponentToRight(announceButton, 10);

			 // Pull File
			 Icon pullIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
						getClass().getResourceMap("/icons/pull.png")).getScaledInstance(32, 32, Image.SCALE_SMOOTH));

			 JButton jPullButton = new JButton("Pull", pullIcon);
			 AbstractButton pullButton =
						MacButtonFactory.makeUnifiedToolBarButton(jPullButton);
			 pullButton.setEnabled(true);
			 jPullButton.setBorder(new LineBorder(Color.BLACK, 0));

			 toolBar.addComponentToRight(pullButton, 10);
  */
/*
        // Lock File
        Icon lockIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
                getClass().getResourceMap("/icons/lock.png")).getScaledInstance(32, 32, Image.SCALE_SMOOTH));
        JButton jLockButton = new JButton("Lock File", lockIcon);
        AbstractButton lockButton =
                MacButtonFactory.makeUnifiedToolBarButton(
                        jLockButton);
        lockButton.setEnabled(false);
        jLockButton.setBorder(new LineBorder(Color.BLACK, 0));
        toolBar.addComponentToRight(lockButton, 10);
*/

		Icon inspectorIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
						getClass().getResource("/icons/inspector.png")).getScaledInstance(32, 32,
						Image.SCALE_SMOOTH));
		JButton inspectorJButton = new JButton("Inspector", inspectorIcon);
		inspectorJButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				setInspectorEnabled(!isInspectorEnabled());
			}
		});


		// The mighty Inspector
		inspectorButton = MacButtonFactory.makeUnifiedToolBarButton(inspectorJButton);
		inspectorButton.setEnabled(true);
		inspectorJButton.setBorder(new LineBorder(Color.BLACK, 0));
		toolBar.addComponentToRight(inspectorButton, 10);


		//announceButton.setBackground(Color.);

		/*
				  JButton annouceButton = new JButton("Announce");
				  annouceButton.putClientProperty("JButton.buttonType", "textured");
					*/
		//toolBar.addComponentToLeft(announceButton);
		//toolBar.add(announceButton);


		searchField = new SearchField();
		searchField.putClientProperty("JTextField.variant", "search");
		searchField.setSendsNotificationForEachKeystroke(true);
		toolBar.addComponentToRight(
						new LabeledComponentGroup("Search", searchField).getComponent());

		final FilePanel filePanelp = filePanel;
		searchField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				log.debug("Search field: " + e.getActionCommand());
				if (e.getActionCommand().equals("")) {
					filePanelp.resetFilter();
					NotesPanel.getInstance().resetFilter();
				} else {
					try {
						PatternFilter fileFilter = new FileObjectNameFilter(
										e.getActionCommand());
						PatternFilter notesFilter = new PatternFilter(e.getActionCommand(), 0,
										2);

						FilterPipeline filePipeline = new FilterPipeline(fileFilter);
						FilterPipeline notesPipeline = new FilterPipeline(notesFilter);

						filePanelp.switchToFlatAndFilter(filePipeline);

						NotesPanel.getInstance().setFilter(notesPipeline);
					} catch (PatternSyntaxException ex) {
						log.info("Invalid regex was entered in search field", ex);
					}
				}
			}
		});

		toolBar.addComponentToCenter(
						new LabeledComponentGroup("View", contextSwitcherPane).getComponent());

		toolBar.installWindowDraggerOnWindow(this.getFrame());

		updateToolBar();
		return toolBar;
	}


	/**
	 * Enables/disables the toolbar depending on current dataset
	 */
	private void updateToolBar() {
		boolean hasProject = getProject() != null;
		boolean isInvite = getProject() != null && getProject()
						.getInvitationState() == InvitationState.INVITED;
		boolean isProjectContext = getContextViewPanel() == ContextPanelEnum.Project;
		boolean hasUser = JakeMainApp.getMsgService() != null;

		createProjectButton.setEnabled(hasUser);
		addFilesButton.setEnabled(hasProject && !isInvite);
		invitePeopleButton.setEnabled(hasProject && !isInvite);
		for (JToggleButton btn : contextSwitcherButtons) {
			btn.setEnabled(isProjectContext && hasProject && !isInvite);
		}
		inspectorButton.setEnabled(isInspectorAllowed());
		searchField.setEditable(hasProject);
	}

	/**
	 * Checks if the inspector is allowed to be displayed.
	 *
	 * @return true if CAN be displayed with current content.
	 */
	private boolean isInspectorAllowed() {
		boolean hasProject = getProject() != null;
		boolean isFilePaneOpen = getContextViewPanel() == ContextPanelEnum.Project && getProjectViewPanel() == ProjectViewPanelEnum.Files;
		boolean isNotePaneOpen = getContextViewPanel() == ContextPanelEnum.Project && getProjectViewPanel() == ProjectViewPanelEnum.Notes;

		return hasProject && (isFilePaneOpen || isNotePaneOpen);
	}

	/**
	 * Called after pressing the toggle buttons for project view.
	 */
	public void setProjectViewFromToolBarButtons() {
		// determine toggle button selection
		if (contextSwitcherButtons.get(ProjectViewPanelEnum.News.ordinal())
						.isSelected()) {
			setProjectViewPanel(ProjectViewPanelEnum.News);
		} else if (contextSwitcherButtons.get(ProjectViewPanelEnum.Files.ordinal())
						.isSelected()) {
			setProjectViewPanel(ProjectViewPanelEnum.Files);
		} else if (contextSwitcherButtons.get(ProjectViewPanelEnum.Notes.ordinal())
						.isSelected()) {
			setProjectViewPanel(ProjectViewPanelEnum.Notes);
		}
	}

	/**
	 * Changes Menu Bar to be Mac compatible.
	 */
	public static void setMacSystemProperties() {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
	}

	/**
	 * Context Switcher Panel
	 *
	 * @return the context switcher panel
	 */
	private JPanel createContextSwitcherPanel() {
		JXPanel switcherPanel = new JXPanel();
		switcherPanel.setOpaque(false);

		ButtonGroup switcherGroup = new ButtonGroup();
		contextSwitcherButtons = SegmentButtonCreator
						.createSegmentedTexturedButtons(3, switcherGroup);

		contextSwitcherButtons.get(0).setText("Project");
		contextSwitcherButtons.get(1).setText("Files");
		contextSwitcherButtons.get(2).setText("Notes");

		class ContextSwitchActionListener implements ActionListener {
			public void actionPerformed(ActionEvent event) {
				setProjectViewFromToolBarButtons();
			}
		}
		ContextSwitchActionListener cslistener = new ContextSwitchActionListener();

		contextSwitcherButtons.get(0).addActionListener(cslistener);
		contextSwitcherButtons.get(1).addActionListener(cslistener);
		contextSwitcherButtons.get(2).addActionListener(cslistener);

		JPanel flowButtons = new JPanel();
		flowButtons.setOpaque(false);
		flowButtons.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		switcherPanel.setLayout(new BorderLayout());
		switcherPanel.add(flowButtons, BorderLayout.CENTER);

		for (JToggleButton button : contextSwitcherButtons) {
			flowButtons.add(button);
		}

		/*
				  JButton resolveConflictBotton = new JButton("3 Conflicts");
				  resolveConflictBotton.putClientProperty("JButton.buttonType", "textured");
				  resolveConflictBotton.setForeground(Color.RED);
				  //resolveConflictBotton.putClientProperty("JComponent.sizeVariant", "small");
				  resolveConflictBotton.setOpaque(true);
				  switcherPanel.add(resolveConflictBotton, BorderLayout.WEST);
					*/

		return switcherPanel;
	}

	/**
	 * Creates the SplitPane for SourceList and the Main Content Area.
	 *
	 * @return the JSplitPane
	 */
	private JSplitPane createSourceListAndMainArea() {
		sourceList = new JakeSourceList();

		// creates the special SplitPlane
		JSplitPane splitPane = MacWidgetFactory
						.createSplitPaneForSourceList(sourceList.getSourceList(),
										contentPanelSplit);

		// TODO: divider location should be a saved property
		splitPane.setDividerLocation(180);
		splitPane.getLeftComponent().setMinimumSize(new Dimension(150, 150));

		return splitPane;
	}


	/**
	 * Show or hide the inspector panel.
	 * This may not succeed if inspector is not allowed.
	 * Checks isInspectorEnabled property.
	 */
	private void updateInspectorPanelVisibility() {
		//log.debug("pre: isInspectorEnabled: " + isInspectorEnabled() +
		//		  " isInspectorPanelVisible: " + isInspectorPanelVisible() +
		//		  " isInspectorAllowed: " + isInspectorAllowed());
		if (isInspectorEnabled()) {
			// add inspector IF allowed
			if (isInspectorAllowed() && !isInspectorPanelVisible()) {
				inspectorPanel.setVisible(true);
				contentPanelSplit.setDividerLocation(contentPanelSplit
								.getWidth() - InspectorPanel.INSPECTOR_SIZE - 1 - contentPanelSplit
								.getDividerSize());
			} else if (!isInspectorAllowed()) {
				inspectorPanel.setVisible(false);
			}
		} else {
			if (isInspectorPanelVisible()) {
				inspectorPanel.setVisible(false);
			}
		}

		// hide divider if not allowed
		if (!isInspectorAllowed()) {
			contentPanelSplit.setDividerSize(0);
		} else {
			contentPanelSplit.setDividerSize(CONTENT_SPLITTERSIZE);
		}

		// refresh panel
		contentPanel.updateUI();

		log.debug(
						"now: isInspectorEnabled: " + isInspectorEnabled() + " isInspectorPanelVisible: " + isInspectorPanelVisible() + " isInspectorAllowed: " + isInspectorAllowed());
	}

	private boolean isInspectorPanelVisible() {
		return inspectorPanel.isVisible();
		//return contentPanelSplit.getDividerLocation() < contentPanelSplit.getWidth();
	}


	@Action
	public void showAboutBox() {
		if (aboutBox == null) {
			JFrame mainFrame = JakeMainApp.getInstance().getMainFrame();
			aboutBox = new JakeAboutDialog(mainFrame);
			aboutBox.setLocationRelativeTo(mainFrame);
		}
		JakeMainApp.getInstance().show(aboutBox);
	}


	/**
	 * init app
	 */
	private void initComponents() {

		menuBar = new JakeMenuBar();
		setMenuBar(menuBar);

		statusPanel = new JPanel();
		statusPanel.setName("statusPanel"); // NOI18N
		statusPanel.setLayout(new java.awt.BorderLayout());
		setStatusBar(statusPanel);
	}

	/**
	 * Updates the window
	 */
	private void updateAll() {
		updateToolBar();
		updateTitle();
		updateView();
	}


	public ProjectViewPanelEnum getProjectViewPanel() {
		return projectViewPanel;
	}

	/**
	 * Set the Project View Panel.
	 * Only works if the ContextView is set to Project.
	 *
	 * @param view: the project view panel that should be active.
	 */
	public void setProjectViewPanel(ProjectViewPanelEnum view) {
		this.projectViewPanel = view;
		updateProjectViewPanel();
		fireProjectViewChanged();
	}

	/**
	 * Updates the state of the toogle bottons to keep them in sync with
	 * ProjectViewPanels - state.
	 */
	private void updateProjectToggleButtons() {
		boolean canBeSelected = getContextViewPanel() == ContextPanelEnum.Project;
		log.debug("updateProjectToggleButtons. canBeSelected=" + canBeSelected);

		contextSwitcherButtons.get(ProjectViewPanelEnum.News.ordinal()).setSelected(
						canBeSelected && getProjectViewPanel() == ProjectViewPanelEnum.News);
		contextSwitcherButtons.get(ProjectViewPanelEnum.Files.ordinal()).setSelected(
						canBeSelected && getProjectViewPanel() == ProjectViewPanelEnum.Files);
		contextSwitcherButtons.get(ProjectViewPanelEnum.Notes.ordinal()).setSelected(
						canBeSelected && getProjectViewPanel() == ProjectViewPanelEnum.Notes);

		// adapt button style
		for (JToggleButton btn : contextSwitcherButtons) {
			Platform.getStyler().styleToolbarButton(btn);
		}
	}

	/**
	 * Updates the Project View, called after setting with setProjectViewPanel
	 */
	private void updateProjectViewPanel() {
		ProjectViewPanelEnum view = getProjectViewPanel();

		// only set if project panels are shown!
		boolean show = getContextViewPanel() == ContextPanelEnum.Project;

		// remote the selection from the sourcelist
		if (!show) {
			sourceList.selectProject(null);
		}

		showContentPanel(newsPanel, show && view == ProjectViewPanelEnum.News);
		showContentPanel(filePanel, show && view == ProjectViewPanelEnum.Files);
		showContentPanel(notesPanel, show && view == ProjectViewPanelEnum.Notes);

		updateProjectToggleButtons();

		// show or hide the inspector
		updateInspectorPanelVisibility();

		updateSourceListVisibility();

		// toolbar changes with viewPort
		updateToolBar();
	}

	private void updateSourceListVisibility() {
		log.debug("update sourcelist visible state: visible=" + JakeMainApp
						.getMsgService() != null);

		if (JakeMainApp.getMsgService() == null) {
			this.mainSplitPane.getLeftComponent().setVisible(false);
		} else {
			this.mainSplitPane.getLeftComponent().setVisible(true);
			// TODO: save original value
			this.mainSplitPane.setDividerLocation(180);
		}

		this.mainSplitPane.updateUI();
	}


	public ContextPanelEnum getContextViewPanel() {
		return contextViewPanel;
	}

	public void setContextViewPanel(ContextPanelEnum view) {
		this.contextViewPanel = view;

		showContentPanel(loginPanel, view == ContextPanelEnum.Login);
		showContentPanel(invitationPanel, view == ContextPanelEnum.Invitation);

		updateProjectViewPanel();
		fireContextViewChanged();
	}

	/**
	 * Called everytime a new project is selected.
	 * Updates the view depending on that selection
	 * Called automatically on setProject()
	 */
	private void updateView() {
		//log.debug("updating view");
		Project pr = getProject();

		boolean needsInvite = pr != null && pr
						.getInvitationState() == InvitationState.INVITED;
		// determine what to show
		if (pr == null) {
			setContextViewPanel(ContextPanelEnum.Login);
		} else if (needsInvite) {
			setContextViewPanel(ContextPanelEnum.Invitation);
		} else {
			setContextViewPanel(ContextPanelEnum.Project);
		}
		updateProjectViewPanel();
		contentPanel.updateUI();
	}

	/**
	 * Helper to set content panel once.
	 * Used internally by updateView()
	 *
	 * @param panel: the panel to show/hide.
	 * @param show:  true to show panel.
	 */
	private void showContentPanel(JPanel panel, boolean show) {
		if (show) {
			contentPanel.add(panel, BorderLayout.CENTER);
		} else {
			contentPanel.remove(panel);
		}

		contentPanel.updateUI();
	}


	public static JakeMainView getMainView() {
		return mainView;
	}

	private static void setMainView(JakeMainView mainView) {
		JakeMainView.mainView = mainView;
	}


	@Action
	public void hideApplicationAction() {
		getFrame().setVisible(false);
	}


	@Action
	public static void showJakeWebsite() {
		JakeHelper.showJakeWebsite();
	}


	private ICoreAccess getCore() {
		return JakeMainApp.getCore();
	}

	public Project getProject() {
		return project;
	}

	/**
	 * Called from the event interface
	 *
	 * @param project
	 */
	public void setProject(Project project) {
		this.project = project;

		updateAll();
	}

	public void addProjectViewChangedListener(ProjectViewChanged pvc) {
		projectViewChanged.add(pvc);
	}

	public void removeProjectViewChangedListener(ProjectViewChanged pvc) {
		projectViewChanged.remove(pvc);
	}

	/**
	 * Fires a project selection change event, calling all
	 * registered members of the event.
	 */
	private void fireProjectViewChanged() {
		for (ProjectViewChanged psc : projectViewChanged) {
			psc.setProjectViewPanel(getProjectViewPanel());
		}
	}


	public void addContextViewChangedListener(ContextViewChanged pvc) {
		contextViewChanged.add(pvc);
	}

	public void removeContextViewChangedListener(ContextViewChanged pvc) {
		contextViewChanged.remove(pvc);
	}

	/**
	 * Fires a project selection change event, calling all
	 * registered members of the event.
	 */
	private void fireContextViewChanged() {
		for (ContextViewChanged psc : contextViewChanged) {
			psc.setContextViewPanel(getContextViewPanel());
		}
	}

	public void quit() {
		app.saveQuit();
	}

	@Override
	public void msgServiceChanged(MsgService msg) {
		updateAll();
	}

	public static boolean isMainWindowVisible() {
		return JakeMainView.getMainView().getFrame().isVisible();
	}

	public static void setMainWindowVisible(boolean visible) {
		JakeMainView.getMainView().getFrame().setVisible(visible);
		JakeMainView.getMainView().getFrame().toFront();
		//JakeMainView.getMainView().getFrame().requestFocus();
	}

	public static void toggleShowHideMainWindow() {
		if (!isMainWindowVisible()) {
			//JakeMainView.getMainView().getFrame().setExtendedState(JFrame.ICONIFIED);
		}
		JakeMainView.getMainView().getFrame().setVisible(!isMainWindowVisible());
		if (isMainWindowVisible()) {
			//JakeMainView.getMainView().getFrame().requestFocus();
			//JakeMainView.getMainView().getFrame().setExtendedState(JFrame.NORMAL);
		}
	}
}
