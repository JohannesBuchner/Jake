/*
 * JakeMock2View.java
 */
package com.jakeapp.gui.swing;

import com.explodingpixels.macwidgets.*;
import com.explodingpixels.widgets.WindowUtils;
import com.jakeapp.core.domain.InvitationState;
import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.actions.CreateProjectAction;
import com.jakeapp.gui.swing.actions.InvitePeopleAction;
import com.jakeapp.gui.swing.actions.abstracts.ProjectAction;
import com.jakeapp.gui.swing.callbacks.*;
import com.jakeapp.gui.swing.controls.SearchField;
import com.jakeapp.gui.swing.dialogs.JakeAboutDialog;
import com.jakeapp.gui.swing.dialogs.generic.JSheet;
import com.jakeapp.gui.swing.helpers.*;
import com.jakeapp.gui.swing.panels.*;
import org.apache.log4j.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.swingx.JXPanel;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The application's main frame.
 */
public class JakeMainView extends FrameView implements ProjectSelectionChanged, ErrorCallback {
	private static final Logger log = Logger.getLogger(JakeMainView.class);
	private static JakeMainView mainView;
	private Project project;

	// all the ui panels
	private NewsPanel newsPanel;
	private FilePanel filePanel;
	private NotesPanel notesPanel;
	private ProjectInvitationPanel invitationPanel;
	private LoginPanel loginPanel;
	private List<JToggleButton> contextSwitcherButtons;
	private JPanel contextSwitcherPane = createContextSwitcherPanel();
	private JPanel inspectorPanel;

	private ProjectViewPanels projectViewPanel = ProjectViewPanels.News;
	private ContextPanels contextViewPanel = ContextPanels.Login;
	private JakeStatusBar jakeStatusBar;
	private JakeTrayIcon tray;

	private List<ProjectViewChanged> projectViewChanged = new ArrayList<ProjectViewChanged>();
	private List<ContextViewChanged> contextViewChanged = new ArrayList<ContextViewChanged>();
	private JPanel statusPanel;


	/**
	 * Project View: set of toggle buttons. Alwasy one state setup.
	 */
	public enum ProjectViewPanels {
		News, Files, Notes
	}

	/**
	 * Special context states.
	 */
	public enum ContextPanels {
		Login, Project, Invitation
	}

	// toolbar
	private AbstractButton createProjectButton;
	private AbstractButton createNoteButton;
	private AbstractButton invitePeopleButton;
	private AbstractButton inspectorButton;


	public JakeMainView(SingleFrameApplication app) {
		super(app);

		setMainView(this);

		tray = new JakeTrayIcon();

		// init the panels
		loginPanel = new LoginPanel();
		newsPanel = new NewsPanel();
		filePanel = new FilePanel();
		notesPanel = new NotesPanel();
		inspectorPanel = new InspectorPanel();
		invitationPanel = new ProjectInvitationPanel();


		// initialize helper code
		JakeMainHelper.initializeJakeMainHelper();

		// macify-window
		if (Platform.isMac()) {
			MacUtils.makeWindowLeopardStyle(this.getFrame().getRootPane());
			setMacSystemProperties();
		}

		// set window icon
		this.getFrame().setIconImage(new ImageIcon(Toolkit.getDefaultToolkit().getImage(
				  getClass().getResource("/icons/jakeapp.png"))).getImage());

		// set app size
		this.getFrame().setMinimumSize(new Dimension(600, 600));
		this.getFrame().setSize(new Dimension(800, 800));

		// initialize the mantisse gui components (menu)
		initComponents();

		// adapt the menu if we live on a mac
		if (Platform.isMac()) {
			// install the close handler (meta-w)
			GuiUtilities.installMacCloseHandler(getFrame());

			// mac has a special application menu that
			// implements Quit.
			//projectMenu.remove(exitMenuItem);
			//projectMenu.remove(exitSeparator);
			// install the about handler
			// new MacOSAppMenuHandler();
		}

		// init the content panel
		contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());

		// add the toolbar
		TriAreaComponent toolBar = createToolBar();
		this.getFrame().add(toolBar.getComponent(), BorderLayout.NORTH);

		// create the panels split pane
		JSplitPane splitPane = this.createSourceListAndMainArea();
		this.getFrame().add(splitPane, BorderLayout.CENTER);

		// create status bar
		jakeStatusBar = new JakeStatusBar(getCore());
		statusPanel.add(jakeStatusBar.getComponent());


		// set default window behaviour
		WindowUtils.createAndInstallRepaintWindowFocusListener(this.getFrame());
		this.getFrame().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		registerCallbacks();

		setContextViewPanel(ContextPanels.Login);

		updateTitle();
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

		getCore().addProjectChangedCallbackListener(new ProjectChangedCallback());
		getCore().addErrorListener(this);
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
		String jakeStr = getResourceMap().getString("windowTitle");

		if (getProject() != null && getProject().getInvitationState() == InvitationState.ACCEPTED) {
			String projectPath = getProject().getRootPath();
			getFrame().setTitle(projectPath + " - " + jakeStr);

			// mac only
			if (Platform.isMac()) {
				getFrame().getRootPane().putClientProperty("Window.documentFile", new File(getProject().getRootPath()));
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
		createProjectAction = new CreateProjectAction();
		JButton createProjectJButton = new JButton();
		createProjectJButton.setAction(createProjectAction);
		createProjectButton = MacButtonFactory.makeUnifiedToolBarButton(createProjectJButton);
		createProjectButton.setEnabled(true);
		createProjectButton.setBorder(new LineBorder(Color.BLACK, 0));
		toolBar.addComponentToLeft(createProjectButton, 10);

		// Create Note
		Icon noteIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
				  getClass().getResource("/icons/notes.png")).getScaledInstance(32, 32, Image.SCALE_SMOOTH));
		JButton jCreateNodeButton = new JButton(getResourceMap().getString("toolbarCreateNote"), noteIcon);

		createNoteButton = MacButtonFactory.makeUnifiedToolBarButton(jCreateNodeButton);
		createNoteButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				createNoteAction();
			}
		});
		createNoteButton.setEnabled(true);
		jCreateNodeButton.setBorder(new LineBorder(Color.BLACK, 0));
		toolBar.addComponentToLeft(createNoteButton, 10);

		// Add People
		JButton invitePeopleJButton = new JButton(new InvitePeopleAction(false));
		invitePeopleButton = MacButtonFactory.makeUnifiedToolBarButton(invitePeopleJButton);
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
				  getClass().getResource("/icons/inspector.png")).getScaledInstance(32, 32, Image.SCALE_SMOOTH));
		JButton inspectorJButton = new JButton("Inspector", inspectorIcon);
		inspectorJButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				showHideInspectorPanel(!isInspectorPanelVisible());
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


		JTextField textField = new SearchField("");
		textField.putClientProperty("JTextField.variant", "search");
		toolBar.addComponentToRight(new LabeledComponentGroup("Search", textField).getComponent());


		toolBar.addComponentToCenter(new LabeledComponentGroup("View", contextSwitcherPane).getComponent());

		toolBar.installWindowDraggerOnWindow(this.getFrame());

		updateToolBar();
		return toolBar;
	}


	/**
	 * Enables/disables the toolbar depending on current dataset
	 */
	private void updateToolBar() {
		boolean hasProject = getProject() != null;
		boolean isInvite = getProject() != null && getProject().getInvitationState() == InvitationState.INVITED;
		boolean isFilePaneOpen = getContextViewPanel() == ContextPanels.Project && getProjectViewPanel() == ProjectViewPanels.Files;

		createNoteButton.setEnabled(hasProject && !isInvite);
		invitePeopleButton.setEnabled(hasProject && !isInvite);
		for (JToggleButton btn : contextSwitcherButtons) {
			btn.setEnabled(hasProject && !isInvite);
		}
		inspectorButton.setEnabled(hasProject && isFilePaneOpen);
	}


	private void createNoteAction() {
		//AddEditNoteDialog.ShowAsDialog(null, getFrame());
	}


	/**
	 * Called after pressing the toggle buttons for project view.
	 */
	public void setProjectViewFromToolBarButtons() {

		// TODO: remove hack
		// show connection info
		//contentPanel.remove(loginPanel);
		//connectionButton.setText("pstein@fsinf.ac.at");


		// determine toggle button selection
		if (contextSwitcherButtons.get(ProjectViewPanels.News.ordinal()).isSelected()) {
			setProjectViewPanel(ProjectViewPanels.News);
		} else if (contextSwitcherButtons.get(ProjectViewPanels.Files.ordinal()).isSelected()) {
			setProjectViewPanel(ProjectViewPanels.Files);
		} else if (contextSwitcherButtons.get(ProjectViewPanels.Notes.ordinal()).isSelected()) {
			setProjectViewPanel(ProjectViewPanels.Notes);
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
		contextSwitcherButtons = SegmentButtonCreator.createSegmentedTexturedButtons(3, switcherGroup);

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
		JakeSourceList sourceList = new JakeSourceList(getCore());

		// creates the special SplitPlane
		JSplitPane splitPane = MacWidgetFactory.createSplitPaneForSourceList(
				  sourceList.getSourceList(), contentPanel);

		// TODO: divider location should be a saved property
		splitPane.setDividerLocation(200);
		splitPane.getLeftComponent().setMinimumSize(new Dimension(150, 150));

		return splitPane;
	}


	/**
	 * Show or hide the inspector panel.
	 *
	 * @param show: shows the inspector panel when set to true.
	 */
	private void showHideInspectorPanel(boolean show) {
		if (show) {
			// add inspector
			if (!isInspectorPanelVisible()) {
				// greatfully, the file panel has a BorderLayout.
				filePanel.add(inspectorPanel, BorderLayout.EAST);
			}
		} else {
			if (isInspectorPanelVisible()) {
				filePanel.remove(inspectorPanel);
			}
		}

		// refresh panel
		contentPanel.updateUI();
	}

	private boolean isInspectorPanelVisible() {
		return inspectorPanel.getParent() != null;
	}


	@Action
	public void showAboutBox() {
		if (aboutBox == null) {
			JFrame mainFrame = JakeMainApp.getApplication().getMainFrame();
			aboutBox = new JakeAboutDialog(mainFrame);
			aboutBox.setLocationRelativeTo(mainFrame);
		}
		JakeMainApp.getApplication().show(aboutBox);
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

	private JakeMenuBar menuBar;
	private javax.swing.JPanel contentPanel;


	/*
		 private final Timer messageTimer;
		 private final Timer busyIconTimer;
		 private final Icon idleIcon;
		 private final Icon[] busyIcons = new Icon[15];
		 private int busyIconIndex = 0;
		  * */
	private JDialog aboutBox;


	/**
	 * Updates the window
	 */
	private void updateAll() {
		updateToolBar();
		updateTitle();
		updateView();
	}


	public ProjectViewPanels getProjectViewPanel() {
		return projectViewPanel;
	}

	/**
	 * Set the Project View Panel.
	 * Only works if the ContextView is set to Project.
	 *
	 * @param view: the project view panel that should be active.
	 */
	public void setProjectViewPanel(ProjectViewPanels view) {
		this.projectViewPanel = view;
		updateProjectViewPanel();
		fireProjectViewChanged();
	}

	/**
	 * Updates the state of the toogle bottons to keep them in sync with
	 * ProjectViewPanels - state.
	 */
	private void updateProjectToggleButtons() {
		boolean canBeSelected = getContextViewPanel() == ContextPanels.Project;
		contextSwitcherButtons.get(ProjectViewPanels.News.ordinal()).setSelected(canBeSelected && getProjectViewPanel() == ProjectViewPanels.News);
		contextSwitcherButtons.get(ProjectViewPanels.Files.ordinal()).setSelected(canBeSelected && getProjectViewPanel() == ProjectViewPanels.Files);
		contextSwitcherButtons.get(ProjectViewPanels.Notes.ordinal()).setSelected(canBeSelected && getProjectViewPanel() == ProjectViewPanels.Notes);

		// problem:

		// adapt button style
		for (JToggleButton btn : contextSwitcherButtons) {
			Platform.getStyler().styleToolbarButton(btn);
		}
	}

	/**
	 * Updates the Project View, called after setting with setProjectViewPanel
	 */
	private void updateProjectViewPanel() {
		ProjectViewPanels view = getProjectViewPanel();

		// only set if project panels are shown!
		boolean show = getContextViewPanel() == ContextPanels.Project;

		showContentPanel(newsPanel, show && view == ProjectViewPanels.News);
		showContentPanel(filePanel, show && view == ProjectViewPanels.Files);
		showContentPanel(notesPanel, show && view == ProjectViewPanels.Notes);


		updateProjectToggleButtons();

		// toolbar changes with viewPort
		updateToolBar();
	}


	public ContextPanels getContextViewPanel() {
		return contextViewPanel;
	}

	public void setContextViewPanel(ContextPanels view) {
		this.contextViewPanel = view;

		showContentPanel(loginPanel, view == ContextPanels.Login);
		showContentPanel(invitationPanel, view == ContextPanels.Invitation);

		updateProjectViewPanel();
		fireContextViewChanged();
	}

	/**
	 * Called everytime a new project is selected.
	 * Updates the view depending on that selection
	 * Called automatically on setProject()
	 */
	private void updateView() {
		Project pr = getProject();

		boolean needsInvite = pr != null && pr.getInvitationState() == InvitationState.INVITED;
		// determine what to show
		if (pr == null) {
			setContextViewPanel(ContextPanels.Login);
		} else if (needsInvite) {
			setContextViewPanel(ContextPanels.Invitation);
		} else {
			setContextViewPanel(ContextPanels.Project);
		}
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
		JakeMainHelper.showJakeWebsite();
	}


	private ICoreAccess getCore() {
		return JakeMainApp.getApp().getCore();
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

	/**
	 * Called when the core is reporting an error.
	 * (maybe from an async call)
	 *
	 * @param ee
	 */
	public void reportError(JakeErrorEvent ee) {
		JSheet.showMessageSheet(getFrame(), ee.getException().toString(),
				  JOptionPane.ERROR_MESSAGE, null);
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
}
