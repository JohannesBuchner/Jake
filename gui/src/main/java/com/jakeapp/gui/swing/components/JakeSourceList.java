package com.jakeapp.gui.swing.components;

import com.explodingpixels.macwidgets.SourceList;
import com.explodingpixels.macwidgets.SourceListCategory;
import com.explodingpixels.macwidgets.SourceListClickListener;
import com.explodingpixels.macwidgets.SourceListContextMenuProvider;
import com.explodingpixels.macwidgets.SourceListItem;
import com.explodingpixels.macwidgets.SourceListModel;
import com.explodingpixels.macwidgets.SourceListSelectionListener;
import com.jakeapp.core.domain.Invitation;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.project.RejectInvitationAction;
import com.jakeapp.gui.swing.actions.project.*;
import com.jakeapp.gui.swing.callbacks.ContextViewChangedCallback;
import com.jakeapp.gui.swing.callbacks.DataChangedCallback;
import com.jakeapp.gui.swing.callbacks.ProjectChangedCallback;
import com.jakeapp.gui.swing.callbacks.TaskChangedCallback;
import com.jakeapp.gui.swing.components.componenthelper.JakeGuiComponent;
import com.jakeapp.gui.swing.controls.SpinningDial;
import com.jakeapp.gui.swing.controls.SpinningDialWaitIndicator;
import com.jakeapp.gui.swing.controls.WaitIndicator;
import com.jakeapp.gui.swing.globals.JakeContext;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;
import com.jakeapp.gui.swing.helpers.ImageLoader;
import com.jakeapp.gui.swing.helpers.JakePopupMenu;
import com.jakeapp.gui.swing.helpers.dragdrop.JakeSourceListTransferHandler;
import com.jakeapp.gui.swing.worker.tasks.GetMyProjectsTask;
import com.jakeapp.gui.swing.worker.tasks.IJakeTask;
import com.jakeapp.gui.swing.worker.JakeExecutor;
import com.jakeapp.gui.swing.xcore.EventCore;
import com.jakeapp.gui.swing.xcore.ObjectCache;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages the Source List for projects.
 */
public class JakeSourceList extends JakeGuiComponent
				implements ProjectChangedCallback, DataChangedCallback, TaskChangedCallback, ContextViewChangedCallback {
	private static final Logger log = Logger.getLogger(JakeSourceList.class);

	private Map<SourceListItem, Project> sourceListProjectMap =
					new HashMap<SourceListItem, Project>();

	private Map<SourceListItem, Invitation> sourceListInvitationMap =
					new HashMap<SourceListItem, Invitation>();
	private Icon projectStartedIcon;
	private Icon projectStoppedIcon;
	private Icon projectInvitedIcon;
	private Icon projectWorkingIcon;
	private SourceListModel projectSourceListModel;
	private SourceListCategory invitedProjectsCategory;
	private SourceListCategory myProjectsCategory;
	private SourceList sourceList;
	private JPopupMenu sourceListContextMenu;
	private JPopupMenu sourceListInvitiationContextMenu;

	private SourceListSelectionListener projectSelectionListener;

	/**
	 * Constructor
	 */
	public JakeSourceList() {
		super();

		EventCore.get().addProjectChangedCallbackListener(this);
		EventCore.get().addDataChangedCallbackListener(this);
		EventCore.get().addTasksChangedListener(this);

		sourceListContextMenu = createSourceListContextMenu();
		sourceListInvitiationContextMenu = createSourceListInvitationContextMenu();
		sourceList = createSourceList();

		// get internal tree
		// FIXME: this is a MODIFICATION of MacWidgets, need to fork+publish sources!
		JTree tree = sourceList.getTree();

		tree.setDragEnabled(true);
		tree.setDropMode(DropMode.ON_OR_INSERT);
		tree.setTransferHandler(new JakeSourceListTransferHandler());

		// start deferred updates
		ObjectCache.get().updateProjects();

		// run the inital update, later updated by events
		updateSourceList();
	}


	/**
	 * Creates the source list (lightblue project tree on the left)
	 *
	 * @return the generated sourcelist.
	 */
	private SourceList createSourceList() {

		// init the icons
		projectStartedIcon = ImageLoader.getScaled(getClass(), "/icons/folder-open.png",
				16);
		projectStoppedIcon = ImageLoader.getScaled(getClass(), "/icons/folder.png", 16);
		projectInvitedIcon = ImageLoader.getScaled(getClass(), "/icons/folder-new.png",
				16);
		projectWorkingIcon = new SpinningDial(16, 16);

		// inits the main data model
		projectSourceListModel = new SourceListModel();

		myProjectsCategory =
						new SourceListCategory(getResourceMap().getString("projectTreeMyProjects"));
		invitedProjectsCategory = new SourceListCategory(getResourceMap().getString(
						"projectTreeInvitedProjects"));
		projectSourceListModel.addCategory(myProjectsCategory);
		projectSourceListModel.addCategory(invitedProjectsCategory);

		// init the SourceListClickListener for project
		final SourceListClickListener projectClickListener =
						new SourceListClickListener() {

							public void sourceListItemClicked(SourceListItem item, Button button,
											int clickCount) {
								//log.info(item.getText() + " clicked " + clickCount + " time" + JakeHelper.getPluralModifer(clickCount) + ".");

								if (button == Button.RIGHT) {
									// select the clicked project
									getSourceList().setSelectedItem(item);
								}
							}

							public void sourceListCategoryClicked(SourceListCategory category,
											Button button, int clickCount) {
								//log.info(category.getText() + " clicked " + clickCount + " time" + JakeHelper.getPluralModifer(clickCount) + ".");

								// we don't need that event
							}
						};


		// selection controller
		projectSelectionListener = new SourceListSelectionListener() {

			public void sourceListItemSelected(SourceListItem item) {
				log.trace("Source List Selection: " + item);

				if (item != null) {
					// get the project from the hashmap
					Project project = sourceListProjectMap.get(item);
					if(project != null)
						JakeContext.setProject(project);

					Invitation invite = sourceListInvitationMap.get(item);
					if(invite != null)
						JakeContext.setInvitation(invite);
					
				} else {
					// fixme: i'm dead tired
					//JakeMainApp.getApp().setProject(null);

					// show the login context panel
					//JakeMainView.getMainView()
					//				.setContextViewPanel(JakeMainView.ContextPanelEnum.Login);
				}
			}
		};

		SourceList sourceList = new SourceList(projectSourceListModel);

		sourceList.addSourceListClickListener(projectClickListener);
		sourceList.addSourceListSelectionListener(projectSelectionListener);

		// use the fancy scrollbars on mac
		//if (Platform.isMac()) {
		//	sourceList.useIAppStyleScrollBars();
		//}

		final SourceListContextMenuProvider menuProvider =
						new SourceListContextMenuProvider() {

							/**
							 * Popupmenu to be created when no item is selected
							 * @return
							 */
							public JPopupMenu createContextMenu() {
								log.trace("public JPopupMenu createContextMenu() {");
								JPopupMenu popupMenu = new JakePopupMenu();
								popupMenu.add(new JMenuItem(new CreateProjectAction(true)));
								return popupMenu;
							}

							/**
							 * Popupmenu to be created when a specific &quot;SourceListItem&quot; is
							 * selected.
							 * @param item
							 * @return
							 */
							public JPopupMenu createContextMenu(SourceListItem item) {
								log.trace("public JPopupMenu createContextMenu(SourceListItem item) {");
								if(item == null)
									return createContextMenu();

								if(sourceListProjectMap.containsKey(item))
								{
									if(sourceListContextMenu != null)
										return sourceListContextMenu;
									else
									{
										log.error("sourceListContextMenu is null");
										return null;
									}
								}
								else if(sourceListInvitationMap.containsKey(item))
								{
									if(sourceListInvitiationContextMenu != null)
										return sourceListInvitiationContextMenu;
									else
									{
										log.error("sourceListInvitiationContextMenu is null");
										return null;
									}
								}
								else
								{
									return createContextMenu();
								}

//								Project project = sourceListProjectMap.get(item);
//								if(project == null)
//								{
//									// item is probably a invitation
//									Invitation invite = sourceListInvitationMap.get(item);
//									if(invite == null)
//									{
//										// no it isn't. don't do anything
//										return createContextMenu();
//									}
//									else
//									{
//									}
//								}
//								else
//								{
//								}
							}

							/**
							 * Contextmenu to be created when a specific &quot;Category&quot; like
							 * &quot;Projects&quot; or &quot;Invitations&quot; are clicked.
							 * @param category
							 * @return
							 */
							public JPopupMenu createContextMenu(SourceListCategory category) {
								JPopupMenu popupMenu = new JakePopupMenu();
								//popupMenu.add(new JMenuItem("Menu for " + category.getText()));
								return popupMenu;
							}
						};

		sourceList.setSourceListContextMenuProvider(menuProvider);
		return sourceList;
	}

	/**
	 * One-time creation of the context menu: generic projects
	 *
	 * @return
	 */
	private JPopupMenu createSourceListContextMenu() {

		// create the menu
		JPopupMenu popupMenu = new JakePopupMenu();

		JMenuItem syncMenuItem = new JMenuItem();
		syncMenuItem.setAction(new SyncProjectAction());
		popupMenu.add(syncMenuItem);

		popupMenu.add(new JSeparator());

		JMenuItem startStopMenuItem = new JMenuItem();
		startStopMenuItem.setAction(new StartStopProjectAction());
		popupMenu.add(startStopMenuItem);

		JMenuItem renameMenuItem = new JMenuItem();
		renameMenuItem.setAction(new RenameProjectAction());
		popupMenu.add(renameMenuItem);

		JMenuItem deleteProjectMenuItem = new JMenuItem();
		deleteProjectMenuItem.setAction(new DeleteProjectAction());
		popupMenu.add(deleteProjectMenuItem);

		return popupMenu;
	}

	/**
	 * One-time creation of the context menu: invitiations
	 *
	 * @return
	 */
	private JPopupMenu createSourceListInvitationContextMenu() {
		JPopupMenu popupMenu = new JakePopupMenu();

		JMenuItem joinMenuItem = new JMenuItem();
		joinMenuItem.setAction(new JoinProjectAction());
		popupMenu.add(joinMenuItem);

		JMenuItem rejectMenuItem = new JMenuItem();
		rejectMenuItem.setAction(new RejectInvitationAction());
		popupMenu.add(rejectMenuItem);

		return popupMenu;
	}

	/**
	 * Updates the SourceList (project list)
	 */
	private void updateSourceList() {
		setWaiting(sourceList.getComponent(),
						JakeExecutor.isTaskRunning(GetMyProjectsTask.class));

		//log.info("updating source list. current selection: " + sourceList.getSelectedItem());
		sourceList.removeSourceListSelectionListener(projectSelectionListener);

		Project selectedProject = getProject();
		SourceListItem projectSLI = null;

		// clear our old mapped data!
		sourceListProjectMap.clear();
		sourceListInvitationMap.clear();

		// clear & update 'invited projects'
		if (!projectSourceListModel.getCategories().contains(invitedProjectsCategory)) {
			projectSourceListModel.addCategory(invitedProjectsCategory);
		}

		projectSourceListModel
						.addItemToCategory(new SourceListItem(""), invitedProjectsCategory);
		while (invitedProjectsCategory.getItemCount() > 1) {
			projectSourceListModel
							.removeItemFromCategoryAtIndex(invitedProjectsCategory, 0);
		}
		List<Invitation> invitations = new ArrayList<Invitation>();
		if (JakeContext.isCoreInitialized()) {
			try {
				invitations = JakeMainApp.getCore().getInvitations();
			} catch (FrontendNotLoggedInException e) {
				e.printStackTrace();
			}
		}
		for (Invitation invitation : invitations) {
			Icon prIcon = projectInvitedIcon;
			SourceListItem sli = new SourceListItem(invitation.getProjectName(), prIcon);

			projectSourceListModel.addItemToCategory(sli, invitedProjectsCategory);
			sourceListInvitationMap.put(sli, invitation);

			// check if invitation was selected, save this SourceListItem.
			if (selectedProject != null && selectedProject.getProjectId()
							.compareTo(invitation.getProjectUUID().toString()) == 0) {
				projectSLI = sli;
			}
		}
		projectSourceListModel.removeItemFromCategoryAtIndex(invitedProjectsCategory, 0);

		if (invitedProjectsCategory.getItemCount() == 0) {
			projectSourceListModel.removeCategory(invitedProjectsCategory);
		}

		// clear & update 'my projects'
		// TODO: remove this hack 2x (prevent collapsing of sourcelist)
		// TODO: do not deleted & recreate sli's (creates selection events we dont wanna have)
		projectSourceListModel
						.addItemToCategory(new SourceListItem(""), myProjectsCategory);
		while (myProjectsCategory.getItemCount() > 1) {
			projectSourceListModel.removeItemFromCategoryAtIndex(myProjectsCategory, 0);
		}

		java.util.List<Project> myprojects = null;
		try {
			myprojects = ObjectCache.get().getMyProjects();
		} catch (FrontendNotLoggedInException e) {
			ExceptionUtilities.showError(e);
		}
		for (Project project : myprojects) {
			SourceListItem sli = createSourceListItem(project);

			// TODO: we need a new event source like project.getTotalNewEventCount()
			//int newEventsCount = 0;
			//if (newEventsCount > 0) {
			//	sli.setCounterValue(newEventsCount);
			//}

			// fixme: should only show *new* eventlogs
			// fixme: need counter-only or some sort of caching...
			sli.setCounterValue(JakeMainApp.getCore().getLog(project, null, 0).size());

			projectSourceListModel.addItemToCategory(sli, myProjectsCategory);
			sourceListProjectMap.put(sli, project);

			// check if project was selected, save this SourceListItem.
			if (selectedProject != null && selectedProject
							.getRootPath() != null && selectedProject.getRootPath()
							.compareTo(project.getRootPath()) == 0) {
				projectSLI = sli;
			}
		}
		projectSourceListModel.removeItemFromCategoryAtIndex(myProjectsCategory, 0);


		if (getSourceList() != null && projectSLI != null) {
			log.info("setting selected item: " + projectSLI);
			getSourceList().setSelectedItem(projectSLI);
		}

		sourceList.addSourceListSelectionListener(projectSelectionListener);

		if (projectSLI == null) {
			log.trace("selected project not found, selecting null");
			JakeContext.setProject(null);
		}
	}

	private static void setWaiting(JComponent c, boolean on) {
		log.trace("SourceList-setWaiting: " + on);
		WaitIndicator w = (WaitIndicator) c.getClientProperty("waiter");
		if (w == null) {
			if (on) {
				w = new SpinningDialWaitIndicator(c);
			}
		} else if (!on) {
			w.dispose();
			w = null;
		}
		c.putClientProperty("waiter", w);
	}

	private SourceListItem createSourceListItem(Project project) {
		Icon prIcon = project.isStarted() ? projectStartedIcon : projectStoppedIcon;

		// override the icon if work is in process
		ProjectChangedCallback.ProjectChangedEvent pce =
						EventCore.get().getLastProjectEvent(project);
		if (pce != null && pce.isWorking()) {
			prIcon = projectWorkingIcon;
		}

		return new SourceListItem(project.getName(), prIcon);
	}

	/**
	 * Returns the Item in the Sourceist for the Project
	 *
	 * @param project
	 * @return
	 */
	private SourceListItem getListItemForProject(Project project) {
		for (Map.Entry<SourceListItem, Project> slip : sourceListProjectMap.entrySet()) {
			if (slip.getValue().equals(project)) {
				return slip.getKey();
			}
		}
		return null;
	}


	/**
	 * Selects a certail project in the sourceList.
	 *
	 * @param project: the project that will be selected.
	 */
	private void selectProject(Project project) {
		log.trace("selectProject in SourceList: " + project);
		SourceListItem sli = getListItemForProject(project);
		if (sli != null) {
			sourceList.setSelectedItem(sli);
		} else {
			removeSelection();
			log.trace("Project Selection: null");
		}
	}


	/**
	 *
	 */
	private void syncSelection() {
		Project p = JakeContext.getProject();
		Invitation i = JakeContext.getInvitation();


	}


	/**
	 * Removes the current selection (if any)
	 */
	private void removeSelection() {
		if (sourceList.getTree().getSelectionPath() != null) {
			sourceList.getTree()
							.removeSelectionPath(sourceList.getTree().getSelectionPath());
		}
	}


	@Override
	protected void projectUpdated() {
		// fixme: needed?
		updateSourceList();
	}

	public SourceList getSourceList() {
		return sourceList;
	}

	public void setSourceList(SourceList sourceList) {
		this.sourceList = sourceList;
	}

	/**
	 * The projectChanged-Event. Needs to be thread save.
	 *
	 * @param ev
	 */
	public void projectChanged(final ProjectChangedEvent ev) {
		log.trace("Received project changed callback.");

		Runnable runner = new Runnable() {
			public void run() {

				// TODO: make more specific instead of full update...
				updateSourceList();

				// react in a special way on some reasons
				switch (ev.getReason()) {
					case Created: {
						log.info("A new project was created - selecting: " + ev.getProject());
						selectProject(ev.getProject());
					}
					break;

					case Syncing:
						setProjectBusy(ev.getProject(), EnumSet.of(BusyState.Syncing));
				}
			}
		};

		SwingUtilities.invokeLater(runner);
	}

	@Override public void dataChanged(EnumSet<DataReason> reason, Project p) {
		if (reason.contains(DataReason.Projects) || reason.contains(DataReason.LogEntries)) {
			updateSourceList();
		}
	}

	@Override public void taskStarted(IJakeTask worker) {
		if (worker instanceof GetMyProjectsTask) {
			updateSourceList();
		}
	}

	@Override public void taskUpdated(IJakeTask worker) {
	}

	@Override public void taskFinished(IJakeTask worker) {
		if (worker instanceof GetMyProjectsTask) {
			updateSourceList();
		}
	}

	@Override public void setContextViewPanel(JakeMainView.ContextPanelEnum panel) {
		if(panel == JakeMainView.ContextPanelEnum.Login) {
			removeSelection();
		}else {
			syncSelection();
		}
	}

	/**
	 * The Project Busy state.
	 */
	enum BusyState {
		Idle, Creating, Syncing
	}

	/**
	 * Shows an indicator that the project is busy
	 *
	 * @param project
	 * @param busy
	 */
	public void setProjectBusy(Project project, EnumSet<BusyState> busy) {
		// TODO: VERY basic; what we want is an animation...
		SourceListItem sli = getListItemForProject(project);
		if (sli != null) {
		//	sli.setCounterValue((busy.contains(BusyState.Idle) ? 0 : 1));
		}
	}
}
