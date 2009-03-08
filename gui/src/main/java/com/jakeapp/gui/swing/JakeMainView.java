package com.jakeapp.gui.swing;

import com.explodingpixels.macwidgets.MacUtils;
import com.explodingpixels.macwidgets.MacWidgetFactory;
import com.explodingpixels.macwidgets.TriAreaComponent;
import com.explodingpixels.widgets.WindowUtils;
import com.jakeapp.core.domain.Invitation;
import com.jakeapp.core.domain.InvitationState;
import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.callbacks.ContextChanged;
import com.jakeapp.gui.swing.callbacks.ContextViewChanged;
import com.jakeapp.gui.swing.callbacks.ProjectChanged;
import com.jakeapp.gui.swing.callbacks.ProjectViewChanged;
import com.jakeapp.gui.swing.dialogs.JakeAboutDialog;
import com.jakeapp.gui.swing.helpers.AppUtilities;
import com.jakeapp.gui.swing.helpers.GuiUtilities;
import com.jakeapp.gui.swing.helpers.JakeHelper;
import com.jakeapp.gui.swing.helpers.JakeMenuBar;
import com.jakeapp.gui.swing.helpers.JakeTrayIcon;
import com.jakeapp.gui.swing.helpers.Platform;
import com.jakeapp.gui.swing.helpers.SegmentButtonCreator;
import com.jakeapp.gui.swing.helpers.dragdrop.FileDropHandler;
import com.jakeapp.gui.swing.panels.FilePanel;
import com.jakeapp.gui.swing.panels.InspectorPanel;
import com.jakeapp.gui.swing.panels.InvitationPanel;
import com.jakeapp.gui.swing.panels.NewsPanel;
import com.jakeapp.gui.swing.panels.NotesPanel;
import com.jakeapp.gui.swing.panels.UserPanel;
import com.jakeapp.gui.swing.worker.InitCoreWorker;
import com.jakeapp.gui.swing.worker.JakeExecutor;
import com.jakeapp.gui.swing.xcore.EventCore;
import com.jakeapp.gui.swing.xcore.JakeDatabaseTools;
import org.apache.log4j.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.swingx.JXPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;


/**
 * The application's main frame.
 */
public class JakeMainView extends FrameView implements ContextChanged {
	private static final Logger log = Logger.getLogger(JakeMainView.class);
	private static final int CONTENT_SPLITTERSIZE = 2;
	private static JakeMainView mainView;
	private boolean inspectorEnabled;

	// all the ui panels
	private NewsPanel newsPanel;
	private FilePanel filePanel;
	private NotesPanel notesPanel;
	private InvitationPanel invitationPanel;
	private UserPanel loginPanel;
	private List<JToggleButton> contextSwitcherButtons;
	private JPanel contextSwitcherPane = createContextSwitcherPanel();
	private JPanel inspectorPanel;
	private JakeMenuBar menuBar;
	private javax.swing.JPanel contentPanel;
	private JSplitPane contentPanelSplit;
	private JDialog aboutBox;

	private ProjectView projectViewPanel = ProjectView.News;
	private ContextPanelEnum contextViewPanel = ContextPanelEnum.Login;
	private JakeStatusBar jakeStatusBar;
	private JakeTrayIcon tray;

	private List<ProjectViewChanged> projectViewChanged =
					new ArrayList<ProjectViewChanged>();
	private List<ContextViewChanged> contextViewChanged =
					new ArrayList<ContextViewChanged>();
	private JPanel statusPanel;

	private JakeMainApp app;
	private JakeSourceList sourceList;
	private JSplitPane mainSplitPane;

	private Image IconAppSmall;
	private Image IconAppLarge;
	private final JakeToolbar jakeToolbar;


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

	@Override public void contextChanged(EnumSet<Reason> reason, Object context) {
		updateAll();
	}

	/**
	 * Project View: set of toggle buttons. Alwasy one state setup.
	 */
	public enum ProjectView {
		News, Files, Notes
	}

	/**
	 * Special context states.
	 */
	public enum ContextPanelEnum {
		Login, Project, Invitation
	}


	//	public JakeMainView(SingleFrameApplication app) {
	public JakeMainView(JakeMainApp app) {
		super(app);

		IconAppSmall = new ImageIcon(Toolkit
						.getDefaultToolkit().getImage(getClass().getResource("/icons/jakeapp.png")))
						.getImage();

		IconAppLarge = new ImageIcon(Toolkit
						.getDefaultToolkit().getImage(getClass().getResource(
						"/icons/jakeapp-large.png"))).getImage();

		setMainView(this);
		this.app = app;

		tray = new JakeTrayIcon();
		jakeToolbar = new JakeToolbar(this);

		// init the panels
		// FIXME: lazy loading!
		loginPanel = new UserPanel();
		newsPanel = new NewsPanel();
		filePanel = new FilePanel();
		notesPanel = new NotesPanel();
		inspectorPanel = new InspectorPanel();
		invitationPanel = new InvitationPanel();


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
		contentPanelSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
						contentPanel,
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
		TriAreaComponent toolBar = jakeToolbar.createToolBar();
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

		/**
		 * Check if the CAPS_LOCK key is pressed at startup.
		 * If so, ask the User if he wants to reset the Database.
		 */
		JakeDatabaseTools.checkKeysResetDatabase();

		JakeExecutor.exec(new InitCoreWorker());

		// debug property
		if (System.getProperty("com.jakeapp.gui.test.instantquit") != null) {
			JakeMainApp.getApp().saveQuit();
		}
	}


	public NewsPanel getNewsPanel() {
		if (newsPanel == null) {
			newsPanel = new NewsPanel();
		}
		return newsPanel;
	}

	public FilePanel getFilePanel() {
		if (filePanel == null) {
			filePanel = new FilePanel();
		}
		return filePanel;
	}

	public NotesPanel getNotesPanel() {
		if (notesPanel == null) {
			notesPanel = new NotesPanel();
		}
		return notesPanel;
	}

	public InvitationPanel getInvitationPanel() {
		return invitationPanel;
	}

	public UserPanel getLoginPanel() {
		return loginPanel;
	}

	public JPanel getContextSwitcherPane() {
		return contextSwitcherPane;
	}

	public List<JToggleButton> getContextSwitcherButtons() {
		return contextSwitcherButtons;
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
				log.debug("splitPane.getWidth()-current: " + (splitPane
								.getWidth() - current));

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
					splitPane.setDividerLocation(splitPane
									.getWidth() - InspectorPanel.INSPECTOR_SIZE);
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
			log.trace("Received project changed callback.");

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
		EventCore.get().addProjectChangedCallbackListener(new ProjectChangedCallback());
		EventCore.get().addContextChangedListener(this);
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

		if(JakeContext.getProject() != null && JakeContext.getProject()
						.getInvitationState() == InvitationState.ACCEPTED) {
			String projectPath = JakeContext.getProject().getRootPath();
			getFrame().setTitle(projectPath + " - " + jakeStr);

			// mac only
			if (Platform.isMac()) {
				getFrame().getRootPane().putClientProperty("Window.documentFile",
								new File(JakeContext.getProject().getRootPath()));
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
	 * Checks if the inspector is allowed to be displayed.
	 *
	 * @return true if CAN be displayed with current content.
	 */
	protected boolean isInspectorAllowed() {
		boolean hasProject = JakeContext.getProject() != null;
		boolean isFilePaneOpen =
						getContextViewPanel() == ContextPanelEnum.Project && getProjectViewPanel() == ProjectView.Files;
		boolean isNotePaneOpen =
						getContextViewPanel() == ContextPanelEnum.Project && getProjectViewPanel() == ProjectView.Notes;

		return hasProject && (isFilePaneOpen || isNotePaneOpen);
	}

	/**
	 * Called after pressing the toggle buttons for project view.
	 */
	public void setProjectViewFromToolBarButtons() {
		// determine toggle button selection
		if (contextSwitcherButtons.get(ProjectView.News.ordinal()).isSelected()) {
			setProjectViewPanel(ProjectView.News);
		} else if (contextSwitcherButtons.get(ProjectView.Files.ordinal())
						.isSelected()) {
			setProjectViewPanel(ProjectView.Files);
		} else if (contextSwitcherButtons.get(ProjectView.Notes.ordinal())
						.isSelected()) {
			setProjectViewPanel(ProjectView.Notes);
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
		contextSwitcherButtons =
						SegmentButtonCreator.createSegmentedTexturedButtons(3, switcherGroup);

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

		log.trace("now: isInspectorEnabled: " + isInspectorEnabled() + " isInspectorPanelVisible: " + isInspectorPanelVisible() + " isInspectorAllowed: " + isInspectorAllowed());
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
		jakeToolbar.updateToolBar();
		updateTitle();
		updateView();
	}


	public ProjectView getProjectViewPanel() {
		return projectViewPanel;
	}

	/**
	 * Set the Project View Panel.
	 * Only works if the ContextView is set to Project.
	 *
	 * @param view: the project view panel that should be active.
	 */
	public void setProjectViewPanel(ProjectView view) {
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
		log.trace("updateProjectToggleButtons. canBeSelected=" + canBeSelected);

		contextSwitcherButtons.get(ProjectView.News.ordinal())
						.setSelected(canBeSelected && getProjectViewPanel() == ProjectView.News);
		contextSwitcherButtons.get(ProjectView.Files.ordinal())
						.setSelected(canBeSelected && getProjectViewPanel() == ProjectView.Files);
		contextSwitcherButtons.get(ProjectView.Notes.ordinal())
						.setSelected(canBeSelected && getProjectViewPanel() == ProjectView.Notes);

		// adapt button style
		for (JToggleButton btn : contextSwitcherButtons) {
			Platform.getStyler().styleToolbarButton(btn);
		}
	}

	/**
	 * Updates the Project View, called after setting with setProjectViewPanel
	 */
	private void updateProjectViewPanel() {
		ProjectView view = getProjectViewPanel();

		// only set if project panels are shown!
		boolean show = getContextViewPanel() == ContextPanelEnum.Project;

		// remote the selection from the sourcelist
		if (!show) {
			sourceList.selectProject(null);
		}

		showContentPanel(newsPanel, show && view == ProjectView.News);
		showContentPanel(filePanel, show && view == ProjectView.Files);
		showContentPanel(notesPanel, show && view == ProjectView.Notes);

		updateProjectToggleButtons();

		// show or hide the inspector
		updateInspectorPanelVisibility();

		updateSourceListVisibility();

		// toolbar changes with viewPort
		jakeToolbar.updateToolBar();
	}

	private void updateSourceListVisibility() {
		log.trace("update sourcelist visible state: visible=" + JakeContext
						.getMsgService());

		if (JakeContext.getMsgService() == null) {
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
		log.trace("updating view");
		Project pr = JakeContext.getProject();
		Invitation invite = JakeContext.getInvitation();

		// determine what to show
		if (pr == null && invite == null) {
			setContextViewPanel(ContextPanelEnum.Login);
		} else if (invite != null) {
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
