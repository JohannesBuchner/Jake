/*
 * JakeMock2View.java
 */
package com.jakeapp.gui.swing;

import com.explodingpixels.macwidgets.*;
import com.explodingpixels.widgets.WindowUtils;
import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.dialogs.JakeAboutDialog;
import com.jakeapp.gui.swing.helpers.*;
import com.jakeapp.gui.swing.panels.*;
import com.jakeapp.gui.swing.sheets.InviteUserSheet;
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
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The application's main frame.
 */
public class JakeMainView extends FrameView {

    private static final Logger log = Logger.getLogger(JakeMainView.class);
    private static JakeMainView mainView;
    private ICoreAccess core;

    // all the ui panels
    private NewsPanel newsPanel;
    private FilePanel filePanel;
    private NotesPanel notesPanel;
    private ProjectInvitationPanel invitationPanel = new ProjectInvitationPanel();
    private LoginPanel loginPanel;
    private List<JToggleButton> contextSwitcherButtons;
    private JPanel contextSwitcherPane = createContextSwitcherPane();
    private JPanel inspectorPanel;

    private ProjectViewPanels projectViewPanel = ProjectViewPanels.News;
    private ContextPanels contextPanelView = ContextPanels.Login;


    /**
     * Project View: set of toggle buttons. Alwasy one state setup.
     */
    enum ProjectViewPanels {
        News, Files, Notes
    }

    ;

    /**
     * Special context states.
     */
    enum ContextPanels {
        Login, Project, Invitation
    }

    ;


    // source list management
    private Map<SourceListItem, Project> sourceListProjectMap;
    private Icon projectStartedIcon;
    private Icon projectStoppedIcon;
    private Icon projectInvitedIcon;
    private SourceListModel projectSourceListModel;
    private SourceListCategory invitedProjectsCategory;
    private SourceListCategory myProjectsCategory;
    private SourceList projectSourceList;
    private AbstractButton createProjectButton;
    private AbstractButton createNoteButton;
    private AbstractButton invitePeopleButton;

    // status bar resources
    private JLabel statusLabel;
    private Project currentProject = null;
    private AbstractButton inspectorButton;

    public JakeMainView(SingleFrameApplication app) {
        super(app);

        setMainView(this);

        // initializeJakeMainHelper the core connection
        setCore(new CoreAccessMock());

        // init the panels
        loginPanel = new LoginPanel();
        newsPanel = new NewsPanel();
        filePanel = new FilePanel();
        notesPanel = new NotesPanel();
        inspectorPanel = new InspectorPanel();


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

        // init toolbar icon
        JakeTrayIcon tray = new JakeTrayIcon();

        // initialize the mantisse gui components (menu)
        initComponents();

        // adapt the menu if we live on a mac
        if (Platform.isMac()) {
            // mac has a special application menu that
            // implements Quit.
            projectMenu.remove(exitMenuItem);
            projectMenu.remove(exitSeparator);

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
        TriAreaComponent bottomBar = createStatusBar();
        statusPanel.add(bottomBar.getComponent());

        // set default window behaviour
        WindowUtils.createAndInstallRepaintWindowFocusListener(this.getFrame());
        this.getFrame().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setContextPanelView(ContextPanels.Login);

        updateTitle();
    }


    /**
     * Public Resource Map
     *
     * @return
     */
    public static ResourceMap getResouceMap() {
        return mainView.getResourceMap();
    }


    /**
     * Update the application title to show the project, once it's
     */
    private void updateTitle() {
        String jakeStr = getResourceMap().getString("windowTitle");

        if (getCurrentProject() != null && !isInvitationProject(getCurrentProject())) {
            String projectPath = getCurrentProject().getRootPath().toString();
            getFrame().setTitle(projectPath + " - " + jakeStr);

            // mac only
            if (Platform.isMac()) {
                getFrame().getRootPane().putClientProperty("Window.documentFile", new File(getCurrentProject().getRootPath()));
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
     * Create status bar code
     *
     * @return
     */
    private TriAreaComponent createStatusBar() {
        // status bar creation code

        // only draw the 'fat' statusbar if we are in a mac. does not look good on win/linux -> USELESS?
        BottomBarSize bottombarSize = Platform.isMac() ? BottomBarSize.LARGE : BottomBarSize.SMALL;

        TriAreaComponent bottomBar = MacWidgetFactory.createBottomBar(bottombarSize);
        statusLabel = MacWidgetFactory.createEmphasizedLabel("200 Files, 2,9 GB");

        // make status label 2 px smaller
        statusLabel.setFont(statusLabel.getFont().deriveFont(statusLabel.getFont().getSize() - 2f));

        bottomBar.addComponentToCenter(statusLabel);

        //Font statusButtonFont = statusLabel.getFont().deriveFont(statusLabel.getFont().getSize()-2f)

        // control button code
        Icon plusIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
                getClass().getResource("/icons/plus.png")));

        JButton addProjectButton = new JButton();
        addProjectButton.setIcon(plusIcon);
        addProjectButton.setToolTipText("Add Project...");

        addProjectButton.putClientProperty("JButton.buttonType", "segmentedTextured");
        addProjectButton.putClientProperty("JButton.segmentPosition", "first");

        if (Platform.isWin()) {
            addProjectButton.setFocusPainted(false);
        }

        bottomBar.addComponentToLeft(addProjectButton);

        Icon minusIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
                getClass().getResource("/icons/minus.png")));
        JButton removeProjectButton = new JButton();
        removeProjectButton.setIcon(minusIcon);
        removeProjectButton.setToolTipText("Remove Project...");

        removeProjectButton.putClientProperty("JButton.buttonType", "segmentedTextured");
        removeProjectButton.putClientProperty("JButton.segmentPosition", "last");

        if (Platform.isWin()) {
            addProjectButton.setFocusPainted(false);
        }

        ButtonGroup group = new ButtonGroup();
        group.add(addProjectButton);
        group.add(removeProjectButton);


        bottomBar.addComponentToLeft(addProjectButton, 0);
        bottomBar.addComponentToLeft(removeProjectButton);

        /*
        JButton playPauseProjectButton = new JButton(">/||");
        if(!Platform.isMac()) playPauseProjectButton.setFont(statusButtonFont);
        playPauseProjectButton.putClientProperty("JButton.buttonType", "textured");
        bottomBar.addComponentToLeft(playPauseProjectButton, 0);


        playPauseProjectButton.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent event) {
        new SheetTest();
        }
        });
         */

        // connection info
        Icon loginIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
                getClass().getResource("/icons/login.png")));
        connectionButton = new JButton(getResourceMap().getString("statusLoginNotLoggedIn"));
        connectionButton.setIcon(loginIcon);
        connectionButton.setHorizontalTextPosition(SwingConstants.LEFT);

        connectionButton.putClientProperty("JButton.buttonType", "textured");
        connectionButton.putClientProperty("JComponent.sizeVariant", "small");
        if (!Platform.isMac()) {
            connectionButton.setFont(connectionButton.getFont().deriveFont(connectionButton.getFont().getSize() - 2f));
        }

        connectionButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                JPopupMenu menu = new JPopupMenu();
                menu.add(new JMenuItem("Sign Out"));
                menu.show((JButton) event.getSource(), ((JButton) event.getSource()).getX(), ((JButton) event.getSource()).getY() - 20);
            }
        });

        bottomBar.addComponentToRight(connectionButton);
        return bottomBar;
    }

    /**
     * Creates the unified toolbar on top.
     *
     * @return
     */
    private TriAreaComponent createToolBar() {
        // create empty toolbar
        TriAreaComponent toolBar = MacWidgetFactory.createUnifiedToolBar();


        // Create Project
        Icon createProjectIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
                getClass().getResource("/icons/createproject.png")).getScaledInstance(32, 32, Image.SCALE_SMOOTH));
        JButton createProjectJButton = new JButton(getResourceMap().getString("toolbarCreateProject"), createProjectIcon);

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
        Icon addPeopleIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
                getClass().getResource("/icons/people.png")).getScaledInstance(32, 32, Image.SCALE_SMOOTH));
        JButton invitePeopleJButton = new JButton(getResourceMap().getString("toolbarInvitePeople"), addPeopleIcon);
        invitePeopleButton = MacButtonFactory.makeUnifiedToolBarButton(invitePeopleJButton);
        invitePeopleJButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                addPeopleAction();
            }

        });
        invitePeopleButton.setEnabled(true);
        invitePeopleButton.setBorder(new LineBorder(Color.BLACK, 0));
        toolBar.addComponentToLeft(invitePeopleButton, 10);


        /*
        // Announce File
        Icon announceIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
                getClass().getResource("/icons/announce.png")).getScaledInstance(32, 32, Image.SCALE_SMOOTH));
        JButton announceJButton = new JButton("Announce", announceIcon);


        AbstractButton announceButton =
                MacButtonFactory.makeUnifiedToolBarButton(announceJButton);

        announceButton.setEnabled(true);
        announceJButton.setBorder(new LineBorder(Color.BLACK, 0));
        toolBar.addComponentToRight(announceButton, 10);

        // Pull File
        Icon pullIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
                getClass().getResource("/icons/pull.png")).getScaledInstance(32, 32, Image.SCALE_SMOOTH));

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
                getClass().getResource("/icons/lock.png")).getScaledInstance(32, 32, Image.SCALE_SMOOTH));
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


        JTextField textField = new JTextField(10);
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
        boolean hasProject = getCurrentProject() != null;
        boolean isInvite = isInvitationProject(getCurrentProject());
        boolean isFilePaneOpen = getContextPanelView() == ContextPanels.Project && getProjectViewPanel() == ProjectViewPanels.Files;

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

    private void addPeopleAction() {
        SheetHelper.ShowJDialogAsSheet(getFrame(), new InviteUserSheet());
    }


    private JButton connectionButton;


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
     * Context Switcher Pane
     *
     * @return
     */
    private JPanel createContextSwitcherPane() {
        JXPanel switcherPanel = new JXPanel();
        switcherPanel.setOpaque(false);

        /*
        switcherPanel.setBorder(BorderFactory.createMatteBorder(
                1, 0, 1, 0, Color.LIGHT_GRAY));

        // set the background painter
        MattePainter mp = new MattePainter(Colors.Blue.alpha(0.5f));
        GlossPainter gp = new GlossPainter(Colors.White.alpha(0.3f),
                GlossPainter.GlossPosition.TOP);
        switcherPanel.setBackgroundPainter(new CompoundPainter(mp, gp));
        */


        ButtonGroup switcherGroup = new ButtonGroup();
        contextSwitcherButtons = SegmentButtonCreator.createSegmentedTexturedButtons(3, switcherGroup);

        contextSwitcherButtons.get(0).setText("Project");
        contextSwitcherButtons.get(1).setText("Files");
        contextSwitcherButtons.get(2).setText("Notes");


        contextSwitcherButtons.get(0).addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                setProjectViewFromToolBarButtons();
            }
        });
        contextSwitcherButtons.get(1).addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                setProjectViewFromToolBarButtons();
            }
        });

        contextSwitcherButtons.get(2).addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                setProjectViewFromToolBarButtons();
            }
        });

        /*
        contextSwitcherButtons.get(3).addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                setProjectViewFromToolBarButtons();
            }
        });
         * */


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
     * @returns the JSplitPane
     */
    private JSplitPane createSourceListAndMainArea() {

        // create the source list and save it in class members.
        setProjectSourceList(createSourceList());

        // creates the special SplitPlane
        JSplitPane splitPane = MacWidgetFactory.createSplitPaneForSourceList(getProjectSourceList(), contentPanel);

        // TODO: divider location should be a saved property
        splitPane.setDividerLocation(200);
        splitPane.getLeftComponent().setMinimumSize(new Dimension(150, 150));

        return splitPane;
    }

    /**
     * Creates the source list (lightblue project tree on the left)
     *
     * @return the generated sourcelist.
     */
    private SourceList createSourceList() {

        // init the icons
        projectStartedIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/folder-open.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
        projectStoppedIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/folder.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
        projectInvitedIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/folder-new.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH));

        // init the project <-> sourcelistitem - map
        sourceListProjectMap = new HashMap<SourceListItem, Project>();

        // inits the main data model
        projectSourceListModel = new SourceListModel();

        myProjectsCategory = new SourceListCategory(getResourceMap().getString("projectTreeMyProjects"));
        invitedProjectsCategory = new SourceListCategory(getResourceMap().getString("projectTreeInvitedProjects"));
        projectSourceListModel.addCategory(myProjectsCategory);
        projectSourceListModel.addCategory(invitedProjectsCategory);

        // run the inital update, later updated by events
        updateSourceList();

        // init the SourceListClickListener for project
        final SourceListClickListener projectClickListener = new SourceListClickListener() {

            public void sourceListItemClicked(SourceListItem item, Button button,
                                              int clickCount) {
                log.info(item.getText() + " clicked " + clickCount + " time" + JakeMainHelper.getPluralModifer(clickCount) + ".");

                // get the project from the hashmap
                Project project = sourceListProjectMap.get(item);

                if (button == button.RIGHT) {
                    // select the clicked project
                    getProjectSourceList().setSelectedItem(item);
                }

                /*
                if (button == button.LEFT || button == button.RIGHT) {
                    setCurrentProject(project);
                }
                */
            }

            public void sourceListCategoryClicked(SourceListCategory category,
                                                  Button button, int clickCount) {
                log.info(category.getText() + " clicked " + clickCount + " time" + JakeMainHelper.getPluralModifer(clickCount) + ".");

                // we don't need that event
            }
        };


        final SourceListSelectionListener projectSelectionListener = new SourceListSelectionListener() {

            public void sourceListItemSelected(SourceListItem item) {
                // get the project from the hashmap
                Project project = sourceListProjectMap.get(item);

                setCurrentProject(project);
            }
        };


        SourceList sourceList = new SourceList(projectSourceListModel);
        sourceList.addSourceListClickListener(projectClickListener);
        sourceList.addSourceListSelectionListener(projectSelectionListener);
//        projectSourceList.setFocusable(false);


        final SourceListContextMenuProvider menuProvider = new SourceListContextMenuProvider() {

            public JPopupMenu createContextMenu() {
                JPopupMenu popupMenu = new JPopupMenu();
                // popupMenu.add(new JMenuItem("Generic Menu for SourceList"));
                return popupMenu;
            }

            public JPopupMenu createContextMenu(SourceListItem item) {
                log.info("Creating context Menu for SourceListitem " + item);

                // get the project from the projectSourceList
                Project project = sourceListProjectMap.get(item);

                // create the menu on the fly
                JPopupMenu popupMenu = new JPopupMenu();
                popupMenu.setLightWeightPopupEnabled(false);

                String startStopString = getProjectStartStopString(project);

                popupMenu.add(new JMenuItem(startStopString));
                popupMenu.add(new JMenuItem(getResourceMap().getString("renameProjectPopupMenuItem")));
                popupMenu.add(new JMenuItem("Remove..."));
                popupMenu.add(new JSeparator());
                popupMenu.add(new JCheckBoxMenuItem("Auto Push"));
                popupMenu.add(new JCheckBoxMenuItem("Auto Pull"));
                return popupMenu;
            }

            public JPopupMenu createContextMenu(SourceListCategory category) {
                JPopupMenu popupMenu = new JPopupMenu();
                //popupMenu.add(new JMenuItem("Menu for " + category.getText()));
                return popupMenu;
            }
        };


        sourceList.setSourceListContextMenuProvider(menuProvider);
        return sourceList;
    }

    /**
     * Updates the SourceList (project list)
     */
    private void updateSourceList() {
        log.info("updading source list...");

        // clear our old mapped data!
        sourceListProjectMap.clear();

        // clear & update 'my projects'
        while (myProjectsCategory.getItemCount() > 0) {
            projectSourceListModel.removeItemFromCategoryAtIndex(myProjectsCategory, 0);
        }
        List<Project> myprojects = getCore().getMyProjects();
        for (Project project : myprojects) {
            Icon prIcon = project.isStarted() ? projectStartedIcon : projectStoppedIcon;
            SourceListItem sli = new SourceListItem(project.getName(), prIcon);

            // TODO: we need a new event source like project.getTotalNewEventCount()
            int newEventsCount = 0;
            if (newEventsCount > 0) {
                sli.setCounterValue(newEventsCount);
            }

            projectSourceListModel.addItemToCategory(sli, myProjectsCategory);
            sourceListProjectMap.put(sli, project);
        }

        // clear & update 'invited projects'
        while (invitedProjectsCategory.getItemCount() > 0) {
            projectSourceListModel.removeItemFromCategoryAtIndex(invitedProjectsCategory, 0);
        }
        List<Project> iprojects = getCore().getInvitedProjects();
        for (Project project : iprojects) {
            Icon prIcon = projectInvitedIcon;
            SourceListItem sli = new SourceListItem(project.getName(), prIcon);

            projectSourceListModel.addItemToCategory(sli, invitedProjectsCategory);
            sourceListProjectMap.put(sli, project);
        }
    }


    /**
     * Show or hide the inspector panel.
     *
     * @param show
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


    /**
     * Evaluates the Project and returns a Start/Stop-String depending on its state.
     *
     * @param project
     * @return String with either Start or Stop.
     */
    public String getProjectStartStopString(Project project) {
        String startStopString;
        if (!project.isStarted()) {
            startStopString = getResourceMap().getString("projectTreeStartProject");
        } else {
            startStopString = getResourceMap().getString("projectTreeStopProject");
        }

        return startStopString;
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
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menuBar = new javax.swing.JMenuBar();
        projectMenu = new javax.swing.JMenu();
        createProjectMenuItem = new javax.swing.JMenuItem();
        startStopProjectMenuItem = new javax.swing.JMenuItem();
        deleteProjectMenuItem = new javax.swing.JMenuItem();
        projectSeparator1 = new javax.swing.JSeparator();
        invitePeopleMenuItem = new javax.swing.JMenuItem();
        createNoteMenuItem = new javax.swing.JMenuItem();
        jSeparator13 = new javax.swing.JSeparator();
        signInOutMenuItem = new javax.swing.JMenuItem();
        exitSeparator = new javax.swing.JSeparator();
        exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        cutMenuItem = new javax.swing.JMenuItem();
        copyMenuItem = new javax.swing.JMenuItem();
        selectAllMenuItem = new javax.swing.JMenuItem();
        preferencesMenuItem = new javax.swing.JMenuItem();
        jSeparator11 = new javax.swing.JSeparator();
        editMenuSeparator = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        showProjectMenuItem = new javax.swing.JMenuItem();
        showFilesMenuItem = new javax.swing.JMenuItem();
        showNotesMenuItem = new javax.swing.JMenuItem();
        actionMenu = new javax.swing.JMenu();
        openMenuItem = new javax.swing.JMenuItem();
        openMenuSeparator = new javax.swing.JSeparator();
        announceMenuItem = new javax.swing.JMenuItem();
        pullMenuItem = new javax.swing.JMenuItem();
        fixFilenameMenuItem = new javax.swing.JMenuItem();
        actionNetworkSeparator = new javax.swing.JSeparator();
        deleteMenuItem = new javax.swing.JMenuItem();
        renameMenuItem = new javax.swing.JMenuItem();
        actionFileSeparator = new javax.swing.JSeparator();
        showHideInspectorMenuItem = new javax.swing.JMenuItem();
        actionInspectorMenuItem = new javax.swing.JSeparator();
        importMenuItem = new javax.swing.JMenuItem();
        newFolderMenuItem = new javax.swing.JMenuItem();
        actionImportSeparator = new javax.swing.JSeparator();
        lockMenuItem = new javax.swing.JMenuItem();
        lockWithMessageMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        visitWebsiteMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();

        menuBar.setName("menuBar"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(com.jakeapp.gui.swing.JakeMainApp.class).getContext().getResourceMap(JakeMainView.class);
        projectMenu.setText(resourceMap.getString("projectMenu.text")); // NOI18N
        projectMenu.setName("projectMenu"); // NOI18N

        createProjectMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.META_MASK));
        createProjectMenuItem.setText(resourceMap.getString("createProjectMenuItem.text")); // NOI18N
        createProjectMenuItem.setName("createProjectMenuItem"); // NOI18N
        projectMenu.add(createProjectMenuItem);

        startStopProjectMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.META_MASK));
        startStopProjectMenuItem.setText(resourceMap.getString("startStopProjectMenuItem.text")); // NOI18N
        startStopProjectMenuItem.setName("startStopProjectMenuItem"); // NOI18N
        projectMenu.add(startStopProjectMenuItem);

        deleteProjectMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_BACK_SPACE, java.awt.event.InputEvent.META_MASK));
        deleteProjectMenuItem.setText(resourceMap.getString("deleteProjectMenuItem.text")); // NOI18N
        deleteProjectMenuItem.setName("deleteProjectMenuItem"); // NOI18N
        projectMenu.add(deleteProjectMenuItem);

        projectSeparator1.setName("projectSeparator1"); // NOI18N
        projectMenu.add(projectSeparator1);

        invitePeopleMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.META_MASK));
        invitePeopleMenuItem.setText(resourceMap.getString("invitePeopleMenuItem.text")); // NOI18N
        invitePeopleMenuItem.setName("invitePeopleMenuItem"); // NOI18N
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

        exitMenuItem.setText(resourceMap.getString("exitMenuItem.text")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        projectMenu.add(exitMenuItem);

        menuBar.add(projectMenu);

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

        preferencesMenuItem.setText(resourceMap.getString("preferencesMenuItem.text")); // NOI18N
        preferencesMenuItem.setName("preferencesMenuItem"); // NOI18N
        editMenu.add(preferencesMenuItem);

        jSeparator11.setName("jSeparator11"); // NOI18N
        editMenu.add(jSeparator11);

        editMenuSeparator.setText(resourceMap.getString("editMenuSeparator.text")); // NOI18N
        editMenuSeparator.setName("editMenuSeparator"); // NOI18N
        editMenu.add(editMenuSeparator);

        menuBar.add(editMenu);

        viewMenu.setText(resourceMap.getString("viewMenu.text")); // NOI18N
        viewMenu.setName("viewMenu"); // NOI18N

        showProjectMenuItem.setText(resourceMap.getString("showProjectMenuItem.text")); // NOI18N
        showProjectMenuItem.setName("showProjectMenuItem"); // NOI18N
        viewMenu.add(showProjectMenuItem);

        showFilesMenuItem.setText(resourceMap.getString("showFilesMenuItem.text")); // NOI18N
        showFilesMenuItem.setName("showFilesMenuItem"); // NOI18N
        viewMenu.add(showFilesMenuItem);

        showNotesMenuItem.setText(resourceMap.getString("showNotesMenuItem.text")); // NOI18N
        showNotesMenuItem.setName("showNotesMenuItem"); // NOI18N
        viewMenu.add(showNotesMenuItem);

        menuBar.add(viewMenu);

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

        menuBar.add(actionMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(com.jakeapp.gui.swing.JakeMainApp.class).getContext().getActionMap(JakeMainView.class, this);
        visitWebsiteMenuItem.setAction(actionMap.get("showJakeWebsite")); // NOI18N
        visitWebsiteMenuItem.setText(resourceMap.getString("visitWebsiteMenuItem.text")); // NOI18N
        visitWebsiteMenuItem.setName("visitWebsiteMenuItem"); // NOI18N
        helpMenu.add(visitWebsiteMenuItem);

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N
        statusPanel.setLayout(new java.awt.BorderLayout());

        jSeparator1.setName("jSeparator1"); // NOI18N

        jSeparator3.setName("jSeparator3"); // NOI18N

        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSeparator actionFileSeparator;
    private javax.swing.JSeparator actionImportSeparator;
    private javax.swing.JSeparator actionInspectorMenuItem;
    private javax.swing.JMenu actionMenu;
    private javax.swing.JSeparator actionNetworkSeparator;
    private javax.swing.JMenuItem announceMenuItem;
    private javax.swing.JMenuItem copyMenuItem;
    private javax.swing.JMenuItem createNoteMenuItem;
    private javax.swing.JMenuItem createProjectMenuItem;
    private javax.swing.JMenuItem cutMenuItem;
    private javax.swing.JMenuItem deleteMenuItem;
    private javax.swing.JMenuItem deleteProjectMenuItem;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem editMenuSeparator;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JSeparator exitSeparator;
    private javax.swing.JMenuItem fixFilenameMenuItem;
    private javax.swing.JMenuItem importMenuItem;
    private javax.swing.JMenuItem invitePeopleMenuItem;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JMenuItem lockMenuItem;
    private javax.swing.JMenuItem lockWithMessageMenuItem;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem newFolderMenuItem;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JSeparator openMenuSeparator;
    private javax.swing.JMenuItem preferencesMenuItem;
    private javax.swing.JMenu projectMenu;
    private javax.swing.JSeparator projectSeparator1;
    private javax.swing.JMenuItem pullMenuItem;
    private javax.swing.JMenuItem renameMenuItem;
    private javax.swing.JMenuItem selectAllMenuItem;
    private javax.swing.JMenuItem showFilesMenuItem;
    private javax.swing.JMenuItem showHideInspectorMenuItem;
    private javax.swing.JMenuItem showNotesMenuItem;
    private javax.swing.JMenuItem showProjectMenuItem;
    private javax.swing.JMenuItem signInOutMenuItem;
    private javax.swing.JMenuItem startStopProjectMenuItem;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JMenu viewMenu;
    private javax.swing.JMenuItem visitWebsiteMenuItem;
    // End of variables declaration//GEN-END:variables
    private javax.swing.JPanel contentPanel;
    /*
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
     * */
    private JDialog aboutBox;

    public ICoreAccess getCore() {
        return core;
    }

    public void setCore(ICoreAccess core) {
        this.core = core;
    }

    public SourceList getProjectSourceList() {
        return projectSourceList;
    }

    public void setProjectSourceList(SourceList projectSourceList) {
        this.projectSourceList = projectSourceList;
    }

    public Project getCurrentProject() {
        return currentProject;
    }

    public void setCurrentProject(Project currentProject) {

        //TODO: hack to test the "no project-state"
        if (currentProject.getRootPath() == null) {
            currentProject = null;
        }

        this.currentProject = currentProject;
        updateAll();

        // relay it to the other panels!
        newsPanel.setProject(currentProject);
    }

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
     * @param view
     */
    public void setProjectViewPanel(ProjectViewPanels view) {
        this.projectViewPanel = view;
        updateProjectViewPanel();
    }

    /**
     * Updates the state of the toogle bottons to keep them in sync with
     * ProjectViewPanels - state.
     */
    private void updateProjectToggleButtons() {
        boolean canBeSelected = getContextPanelView() == ContextPanels.Project;
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
        boolean show = getContextPanelView() == ContextPanels.Project;

        showContentPanel(newsPanel, show && view == ProjectViewPanels.News);
        showContentPanel(filePanel, show && view == ProjectViewPanels.Files);
        showContentPanel(notesPanel, show && view == ProjectViewPanels.Notes);


        updateProjectToggleButtons();

        // toolbar changes with viewPort
        updateToolBar();
    }


    public ContextPanels getContextPanelView() {
        return contextPanelView;
    }

    public void setContextPanelView(ContextPanels view) {
        this.contextPanelView = view;

        showContentPanel(loginPanel, view == ContextPanels.Login);
        showContentPanel(invitationPanel, view == ContextPanels.Invitation);

        updateProjectViewPanel();

    }


    private boolean isInvitationProject(Project pr) {
        //TODO: need better way to determine if project needs invitaton!
        boolean needsInvite = pr != null && pr.getName().compareTo("DEMO INVITATION") == 0;
        return needsInvite;
    }


    /**
     * Called everytime a new project is selected.
     * Updates the view depending on that selection
     * Called automatically on setProject()
     */
    private void updateView() {
        Project pr = getCurrentProject();

        boolean needsInvite = isInvitationProject(pr);
        // determine what to show
        if (pr == null) {
            setContextPanelView(ContextPanels.Login);
        } else if (needsInvite) {
            setContextPanelView(ContextPanels.Invitation);
        } else {
            setContextPanelView(ContextPanels.Project);
        }
        contentPanel.updateUI();
    }

    /**
     * Helper to set content panel once.
     * Used internally by updateView()
     */
    private void showContentPanel(JPanel panel, boolean show) {
        if (show) {
            contentPanel.add(panel, BorderLayout.CENTER);
        } else if (!show) {
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
    public void showJakeWebsite() {
        try {
            Desktop.getDesktop().browse(new URI(getResourceMap().getString("JakeWebsite")));
        } catch (IOException e) {
            log.warn("Unable to open Website!", e);
        } catch (URISyntaxException e) {
            log.warn("Unable to open Website, invalid syntax", e);
        }
    }
}
