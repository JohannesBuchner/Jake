package com.jakeapp.gui.swing.xcore;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.services.IProjectInvitationListener;
import com.jakeapp.core.synchronization.ChangeListener;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.callbacks.*;
import com.jakeapp.gui.swing.dialogs.generic.JSheet;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;
import com.jakeapp.gui.swing.worker.IJakeTask;
import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;
import com.jakeapp.jake.ics.status.ILoginStateListener;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

/**
 * The Core Event Distributor.
 */
public class EventCore {
	private static final Logger log = Logger.getLogger(EventCore.class);
	private static EventCore instance;

	private final ProjectsChangeListener projectsChangeListener =
					new ProjectsChangeListener();

	private final List<ProjectChanged> projectChanged;
	private final Stack<ProjectChanged.ProjectChangedEvent> projectEvents =
					new Stack<ProjectChanged.ProjectChangedEvent>();

	private final List<DataChanged> dataChanged;
	private List<FileSelectionChanged> fileSelectionListeners =
					new ArrayList<FileSelectionChanged>();
	private List<NodeSelectionChanged> nodeSelectionListeners =
					new ArrayList<NodeSelectionChanged>();

	// save the last project events in a list (check for ongoing operations)
	private final HashMap<Project, ProjectChanged.ProjectChangedEvent>
					lastProjectEvents =
					new HashMap<Project, ProjectChanged.ProjectChangedEvent>();

	private IProjectInvitationListener invitationListener =
					new ProjectInvitationListener();

	private final List<PropertyChanged> propertyChangedListeners =
					new ArrayList<PropertyChanged>();

	private final List<TaskChanged> taskChangedListeners =
					new ArrayList<TaskChanged>();

	private final List<ILoginStateListener> loginStateListeners =
					new ArrayList<ILoginStateListener>();

	// forward this event into our gui thread
	private final ILoginStateListener loginStateListener = new ILoginStateListener() {
		@Override public void connectionStateChanged(final ConnectionState le,
						Exception ex) {
			Runnable runner = new Runnable() {
				@Override public void run() {
					for (ILoginStateListener lsl : loginStateListeners) {
						lsl.connectionStateChanged(le, null);
					}
				}
			};
			SwingUtilities.invokeLater(runner);
		}
	};

	static {
		instance = new EventCore();
	}

	public EventCore() {
		projectChanged = new ArrayList<ProjectChanged>();
		dataChanged = new ArrayList<DataChanged>();

		JakeMainApp.getInstance().addCoreChangedListener(new CoreChanged() {
			@Override public void coreChanged() {
				log.debug("received core change, rolling out updates...");
				fireAllChanged();
			}
		});
	}

	private void fireAllChanged() {
		ObjectCache.get().updateAll();
		fireDataChanged(DataChanged.All, null);
	}

	public static EventCore get() {
		return instance;
	}

	public void addProjectChangedCallbackListener(ProjectChanged cb) {
		projectChanged.add(cb);
	}

	public void removeProjectChangedCallbackListener(ProjectChanged cb) {
		log.trace("Deregister project changed callback: " + cb);

		if (projectChanged.contains(cb)) {
			projectChanged.remove(cb);
		}
	}

	public void fireProjectChanged(ProjectChanged.ProjectChangedEvent ev) {
		lastProjectEvents.put(ev.getProject(), ev);

		// save event in the stack, until new data has arrived from db
		projectEvents.add(ev);
		ObjectCache.get().updateProjects();
	}

	private void spreadProjectChanged(ProjectChanged.ProjectChangedEvent ev) {
		for (ProjectChanged callback : projectChanged) {
			callback.projectChanged(ev);
		}
	}

	private void shootStalledProjectChangedEvents() {
		while (!projectEvents.empty()) {
			spreadProjectChanged(projectEvents.pop());
		}
	}

	public void addConnectionStatusCallbackListener(ILoginStateListener cb) {
		loginStateListeners.add(cb);
	}

	public void removeConnectionStatusCallbackListener(ILoginStateListener cb) {
		loginStateListeners.remove(cb);
	}

	public void addDataChangedCallbackListener(DataChanged cb) {
		dataChanged.add(cb);
	}

	public void removeDataChangedCallbackListener(DataChanged cb) {
		dataChanged.remove(cb);
	}

	public void fireDataChanged(EnumSet<DataChanged.Reason> reason, Project p) {
		log.trace("spead callback event data changed: " + reason);
		for (DataChanged callback : dataChanged) {
			callback.dataChanged(reason, p);
		}

		// any stalled events?
		shootStalledProjectChangedEvents();
	}

	public void addFileSelectionListener(FileSelectionChanged listener) {
		fileSelectionListeners.add(listener);
	}

	public void removeFileSelectionListener(FileSelectionChanged listener) {
		fileSelectionListeners.remove(listener);
	}

	public void addNodeSelectionListener(NodeSelectionChanged listener) {
		nodeSelectionListeners.add(listener);
	}

	public void removeNodeSelectionListener(NodeSelectionChanged listener) {
		nodeSelectionListeners.remove(listener);
	}

	public void addPropertyListener(PropertyChanged listener) {
		propertyChangedListeners.add(listener);
	}

	public void removePropertyListener(PropertyChanged listener) {
		propertyChangedListeners.remove(listener);
	}


	public void notifyFileSelectionListeners(java.util.List<FileObject> objs) {
		log.debug("notify selection listeners");
		for (FileSelectionChanged listener : fileSelectionListeners) {
			listener.fileSelectionChanged(new FileSelectionChanged.FileSelectedEvent(objs));
		}
	}

	public void notifyNodeSelectionListeners(
					java.util.List<ProjectFilesTreeNode> objs) {
		log.trace("notify selection listeners");
		for (NodeSelectionChanged c : nodeSelectionListeners) {
			c.nodeSelectionChanged(new NodeSelectionChanged.NodeSelectedEvent(objs));
		}
	}

	public ChangeListener getChangeListener() {
		return projectsChangeListener;
	}

	public IProjectInvitationListener getInvitiationListener() {
		return invitationListener;
	}

	public void fireNotesChanged(Project p) {
		ObjectCache.get().updateNotes(p);
		fireLogChanged(p);
	}

	/**
	 * Updates the cache and spreads the event when new data is available.
	 *
	 * @param p
	 */
	public void fireFilesChanged(Project p) {
		ObjectCache.get().updateFiles(p);
		fireLogChanged(p);
	}

	// FIXME: need conversion to AvailableLater?
	public void fireLogChanged(Project p) {
		//ObjectCache.get().updateLog(p);
		fireDataChanged(EnumSet.of(DataChanged.Reason.Files), p);
	}

	public void addTasksChangedListener(TaskChanged callback) {
		taskChangedListeners.add(callback);
	}

	public void removeTasksChangedListener(TaskChanged callback) {
		taskChangedListeners.remove(callback);
	}

	public void fireTasksChangedListener(IJakeTask task, TaskChanged.TaskOps op) {
		for (TaskChanged callback : taskChangedListeners) {
			switch (op) {
				case Started:
					callback.taskStarted(task);
					break;
				case Updated:
					callback.taskUpdated(task);
					break;
				case Finished:
					callback.taskFinished(task);
			}
		}
	}

	/**
	 * Returns the Login State Listener, that acts as the callback for the core.
	 * If you want this event in the gui, register for it!
	 * @return
	 */
	public ILoginStateListener getLoginStateListener() {
		return loginStateListener;
	}


	private class ProjectsChangeListener implements ChangeListener {
		public ProjectsChangeListener() {
		}

		@Override public INegotiationSuccessListener beganRequest(JakeObject jo) {
			log.debug("beganRequest for " + jo);
			return null;
		}

		@Override public void pullNegotiationDone(JakeObject jo) {
			log.debug("pullNegitiationDone: " + jo);
		}

		@Override public void pullDone(JakeObject jo) {
			log.debug("pullDone: " + jo);
		}

		@Override public void pullProgressUpdate(JakeObject jo, Status status,
						double progress) {
			log.debug("pullProgressUpdate: " + jo + ", status: " + status + ", progress: " + progress);
		}

		@Override
		public void pullFailed(JakeObject jo, Exception reason) {
			log.debug("pullFailed: " + jo, reason);
		}
	}

	/**
	 * Returns the last event for a project
	 *
	 * @param project
	 * @return
	 */
	public ProjectChanged.ProjectChangedEvent getLastProjectEvent(Project project) {
		return lastProjectEvents.get(project);
	}


	/**
	 * Fire Event that a property has changed.
	 *
	 * @param reason
	 * @param project
	 * @param obj
	 */
	public void firePropertyChanged(PropertyChanged.Reason reason, Project project,
					Object obj) {
		log.trace("notify property changed listeners");
		for (PropertyChanged c : propertyChangedListeners) {
			c.propertyChanged(EnumSet.of(reason), project, obj);
		}
	}


	private class ProjectInvitationListener implements IProjectInvitationListener {
		@Override public void invited(UserId user, Project p) {
			log.debug("received invitation from " + user + " for project: " + p);

			// save in InvitationManager
			InvitationManager.get().saveInvitationSource(p, user);

			fireProjectChanged(new ProjectChanged.ProjectChangedEvent(p,
							ProjectChanged.ProjectChangedEvent.Reason.Invited));
		}

		@Override public void accepted(UserId user, Project p) {
			log.debug("accepted: " + user + ", project" + p);

			// TODO: find a better place for that
			JSheet.showMessageSheet(JakeMainApp.getFrame(),
							"User " + user + " accepted your Invitation to " + p);
		}

		@Override public void rejected(UserId user, Project p) {
			log.debug("rejected" + user + ", project" + p);

			// TODO: find a better place for that			
			JSheet.showMessageSheet(JakeMainApp.getFrame(),
							"User " + user + " rejected your Invitation to " + p);
		}
	}
}
