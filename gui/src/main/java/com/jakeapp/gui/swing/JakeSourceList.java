package com.jakeapp.gui.swing;

import com.explodingpixels.macwidgets.*;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException;
import com.jakeapp.gui.swing.actions.*;
import com.jakeapp.gui.swing.callbacks.ProjectChanged;
import com.jakeapp.gui.swing.callbacks.ProjectSelectionChanged;
import com.jakeapp.gui.swing.helpers.JakePopupMenu;
import com.jakeapp.gui.swing.helpers.Platform;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;
import com.jakeapp.gui.swing.helpers.dragdrop.JakeSourceListTransferHandler;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages the Source List for projects.
 * User: studpete
 * Date: Dec 29, 2008
 * Time: 11:43:29 AM
 */
public class JakeSourceList extends JakeGuiComponent implements
		  ProjectSelectionChanged, ProjectChanged {
	private static final Logger log = Logger.getLogger(JakeSourceList.class);

	private Map<SourceListItem, Project> sourceListProjectMap;
	private Icon projectStartedIcon;
	private Icon projectStoppedIcon;
	private Icon projectInvitedIcon;
	private SourceListModel projectSourceListModel;
	private SourceListCategory invitedProjectsCategory;
	private SourceListCategory myProjectsCategory;
	private SourceList sourceList;
	private JPopupMenu sourceListContextMenu;
	private JPopupMenu sourceListInvitiationContextMenu;

	private SourceListSelectionListener projectSelectionListener;

	public JakeSourceList(ICoreAccess core) {
		super(core);

		JakeMainApp.getApp().addProjectSelectionChangedListener(this);
		JakeMainApp.getApp().getCore().addProjectChangedCallbackListener(this);

		sourceListContextMenu = createSourceListContextMenu();
		sourceListInvitiationContextMenu = createSourceListInvitationContextMenu();
		sourceList = createSourceList();

		// get internal tree
		// TODO: this is a MODIFICATION of MacWidgets, need to fork+publish sources!
		JTree tree = sourceList.getTree();

		tree.setDragEnabled(true);
		tree.setDropMode(DropMode.ON_OR_INSERT);
		tree.setTransferHandler(new JakeSourceListTransferHandler());

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

		// init the SourceListClickListener for project
		final SourceListClickListener projectClickListener = new SourceListClickListener() {

			public void sourceListItemClicked(SourceListItem item, Button button,
														 int clickCount) {
				//log.info(item.getText() + " clicked " + clickCount + " time" + JakeHelper.getPluralModifer(clickCount) + ".");

				if (button == button.RIGHT) {
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


		projectSelectionListener = new SourceListSelectionListener() {

			public void sourceListItemSelected(SourceListItem item) {
				//log.info("Source List Selection: " + item);

				if (item != null) {
					// get the project from the hashmap
					Project project = sourceListProjectMap.get(item);

					JakeMainApp.getApp().setProject(project);
				} else {
					JakeMainApp.getApp().setProject(null);

					// show the login context panel
					JakeMainView.getMainView().setContextViewPanel(JakeMainView.ContextPanelEnum.Login);
				}
			}
		};

		SourceList sourceList = new SourceList(projectSourceListModel);

		sourceList.addSourceListClickListener(projectClickListener);
		sourceList.addSourceListSelectionListener(projectSelectionListener);

		// use the fancy scrollbars on mac
		if (Platform.isMac()) {
			sourceList.useIAppStyleScrollBars();
		}

		final SourceListContextMenuProvider menuProvider = new SourceListContextMenuProvider() {

			public JPopupMenu createContextMenu() {
				JPopupMenu popupMenu = new JakePopupMenu();
				popupMenu.add(new JMenuItem(new CreateProjectAction(true)));
				return popupMenu;
			}

			public JPopupMenu createContextMenu(SourceListItem item) {
				Project project = sourceListProjectMap.get(item);

				if (project.isInvitation()) {
					return sourceListInvitiationContextMenu;
				} else {
					return sourceListContextMenu;
				}
			}

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
		rejectMenuItem.setAction(new RejectProjectAction());
		popupMenu.add(rejectMenuItem);

		return popupMenu;
	}

	/**
	 * Updates the SourceList (project list)
	 */
	private void updateSourceList() {
		//log.info("updating source list. current selection: " + sourceList.getSelectedItem());

		sourceList.removeSourceListSelectionListener(projectSelectionListener);

		Project selectedProject = getProject();
		SourceListItem projectSLI = null;

		// clear our old mapped data!
		sourceListProjectMap.clear();

		// clear & update 'my projects'
		// TODO: remove this hack 2x (prevent collapsing of sourcelist)
		// TODO: do not deleted & recreate sli's (creates selection events we dont wanna have)
		projectSourceListModel.addItemToCategory(new SourceListItem(""), myProjectsCategory);
		while (myProjectsCategory.getItemCount() > 1) {
			projectSourceListModel.removeItemFromCategoryAtIndex(myProjectsCategory, 0);
		}

		java.util.List<Project> myprojects = null;
		try {
			myprojects = getCore().getMyProjects();
		} catch (FrontendNotLoggedInException e) {
			ExceptionUtilities.showError(e);
		}
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

			// check if project was selected, save this SourceListItem.
			if (selectedProject != null && selectedProject.getRootPath().compareTo(project.getRootPath()) == 0) {
				projectSLI = sli;
			}
		}
		projectSourceListModel.removeItemFromCategoryAtIndex(myProjectsCategory, 0);

		// clear & update 'invited projects'
		if (!projectSourceListModel.getCategories().contains(invitedProjectsCategory)) {
			projectSourceListModel.addCategory(invitedProjectsCategory);
		}

		projectSourceListModel.addItemToCategory(new SourceListItem(""), invitedProjectsCategory);
		while (invitedProjectsCategory.getItemCount() > 1) {
			projectSourceListModel.removeItemFromCategoryAtIndex(invitedProjectsCategory, 0);
		}
		java.util.List<Project> iprojects = null;
		try {
			iprojects = getCore().getInvitedProjects();
		} catch (FrontendNotLoggedInException e) {
			// TODO @ Peter: reauthenticate, retry action, if it still fails show error message
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		for (Project project : iprojects) {
			Icon prIcon = projectInvitedIcon;
			SourceListItem sli = new SourceListItem(project.getName(), prIcon);

			projectSourceListModel.addItemToCategory(sli, invitedProjectsCategory);
			sourceListProjectMap.put(sli, project);

			// check if project was selected, save this SourceListItem.
			if (selectedProject != null && selectedProject.getProjectId().compareTo(project.getProjectId()) == 0) {
				projectSLI = sli;
			}
		}
		projectSourceListModel.removeItemFromCategoryAtIndex(invitedProjectsCategory, 0);

		if (invitedProjectsCategory.getItemCount() == 0) {
			projectSourceListModel.removeCategory(invitedProjectsCategory);
		}

		if (getSourceList() != null && projectSLI != null) {
			log.info("setting selected item: " + projectSLI);
			getSourceList().setSelectedItem(projectSLI);
		}

		sourceList.addSourceListSelectionListener(projectSelectionListener);

		if (projectSLI == null) {
			log.info("selected project not found, selecting null");
			JakeMainApp.getApp().setProject(null);
		}
	}


	/**
	 * Selects a certail project in the sourceList.
	 * Throws a ProjectNotFoundException if the project was not found in the sourceList.
	 *
	 * @param project: the project that will be selected.
	 */
	public void selectProject(Project project) {
		log.info("selectProject in SourceList: " + project);
		boolean success = false;
		for (Map.Entry<SourceListItem, Project> slip : sourceListProjectMap.entrySet()) {
			if (slip.getValue() == project) {
				sourceList.setSelectedItem(slip.getKey());
				success = true;
				break;
			}
		}

		if (!success) {
			removeSelection();
		}
	}

	/**
	 * Removes the current selection (if any)
	 */
	// TODO: this uses interal tree api. move into SourceList api?
	private void removeSelection() {
		if (sourceList.getTree().getSelectionPath() != null) {
			sourceList.getTree().removeSelectionPath(sourceList.getTree().getSelectionPath());
		}
	}


	@Override
	protected void projectUpdated() {
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
		log.info("Received project changed callback.");

		Runnable runner = new Runnable() {
			public void run() {

				// TODO: make more specific instead of full update...
				updateSourceList();

				// select a new created project
				if (ev.getReason() == ProjectChangedEvent.ProjectChangedReason.Created) {
					selectProject(ev.getProject());
				}
			}
		};

		SwingUtilities.invokeLater(runner);
	}
}
