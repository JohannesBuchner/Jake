package com.jakeapp.gui.swing.xcore;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.services.IProjectInvitationListener;
import com.jakeapp.core.synchronization.change.ChangeListener;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.callbacks.CoreChanged;
import com.jakeapp.gui.swing.callbacks.DataChanged;
import com.jakeapp.gui.swing.callbacks.FileSelectionChanged;
import com.jakeapp.gui.swing.callbacks.NodeSelectionChanged;
import com.jakeapp.gui.swing.callbacks.ProjectChanged;
import com.jakeapp.gui.swing.callbacks.ContextChanged;
import com.jakeapp.gui.swing.callbacks.TaskChanged;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;
import com.jakeapp.gui.swing.worker.IJakeTask;
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

	private final List<ContextChanged> contextChangedListeners =
					new ArrayList<ContextChanged>();

	private final List<TaskChanged> taskChangedListeners =
					new ArrayList<TaskChanged>();

	private final List<ILoginStateListener> loginStateListeners =
					new ArrayList<ILoginStateListener>();

	// forward this event into our gui thread
	private final ILoginStateListener loginStateListener = new ILoginStateListener() {
		@Override public void connectionStateChanged(final ConnectionState le,
						final Exception ex) {
			Runnable runner = new Runnable() {
				@Override public void run() {
					for (ILoginStateListener lsl : loginStateListeners) {
						lsl.connectionStateChanged(le, ex);
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
		fireDataChanged(DataChanged.ALL, null);
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

	public void fireProjectChanged(final ProjectChanged.ProjectChangedEvent ev) {
		Runnable runner = new Runnable() {
			@Override public void run() {
				lastProjectEvents.put(ev.getProject(), ev);

				// save event in the stack, until new data has arrived from db
				projectEvents.add(ev);
				ObjectCache.get().updateProjects();
			}
		};
		SwingUtilities.invokeLater(runner);
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

	public void fireDataChanged(EnumSet<DataChanged.DataReason> dataReason, Project p) {
		log.trace("spread callback event data changed: " + dataReason);
		for (DataChanged callback : dataChanged) {
			callback.dataChanged(dataReason, p);
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

	public void addContextChangedListener(ContextChanged listener) {
		contextChangedListeners.add(listener);
	}

	public void removeContextChangedListener(ContextChanged listener) {
		contextChangedListeners.remove(listener);
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
		fireDataChanged(EnumSet.of(DataChanged.DataReason.Files), p);
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
	 *
	 * @return
	 */
	public ILoginStateListener getLoginStateListener() {
		return loginStateListener;
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
	 * Fire Event that a context has changed.
	 *
	 * @param reason
	 * @param context
	 */
	public void fireContextChanged(ContextChanged.Reason reason, Object context) {
		log.trace("notify property changed listeners");
		for (ContextChanged c : contextChangedListeners) {
			c.contextChanged(EnumSet.of(reason), context);
		}
	}


	public IProjectInvitationListener getInvitationListener() {
		return invitationListener;
	}

}
