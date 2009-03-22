package com.jakeapp.gui.swing.xcore;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.services.IProjectInvitationListener;
import com.jakeapp.core.synchronization.change.ChangeListener;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.callbacks.ContextChangedCallback;
import com.jakeapp.gui.swing.callbacks.CoreChangedCallback;
import com.jakeapp.gui.swing.callbacks.DataChangedCallback;
import com.jakeapp.gui.swing.callbacks.FileSelectionChangedCallback;
import com.jakeapp.gui.swing.callbacks.NodeSelectionChangedCallback;
import com.jakeapp.gui.swing.callbacks.ProjectChangedCallback;
import com.jakeapp.gui.swing.callbacks.TaskChangedCallback;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;
import com.jakeapp.gui.swing.worker.tasks.IJakeTask;
import com.jakeapp.jake.fss.IFileModificationListener;
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

	private final List<ProjectChangedCallback> projectChanged;
	private final Stack<ProjectChangedCallback.ProjectChangedEvent> projectEvents =
					new Stack<ProjectChangedCallback.ProjectChangedEvent>();

	private final List<DataChangedCallback> dataChanged;
	private List<FileSelectionChangedCallback> fileSelectionListeners =
					new ArrayList<FileSelectionChangedCallback>();
	private List<NodeSelectionChangedCallback> nodeSelectionListeners =
					new ArrayList<NodeSelectionChangedCallback>();

	// save the last project events in a list (check for ongoing operations)
	private final HashMap<Project, ProjectChangedCallback.ProjectChangedEvent>
					lastProjectEvents =
					new HashMap<Project, ProjectChangedCallback.ProjectChangedEvent>();

	private IProjectInvitationListener invitationListener =
					new ProjectInvitationListener();

	private final List<ContextChangedCallback> contextChangedListeners =
					new ArrayList<ContextChangedCallback>();

	private final List<TaskChangedCallback> taskChangedListeners =
					new ArrayList<TaskChangedCallback>();

	private final List<ILoginStateListener> loginStateListeners =
					new ArrayList<ILoginStateListener>();

	private HashMap<Project, IFileModificationListener> fileModificationListener;


	// forward this event into our gui thread
	private final ILoginStateListener loginStateListener = new ILoginStateListener() {
		@Override public void connectionStateChanged(final ConnectionState le,
						final Exception ex) {
			Runnable runner = new Runnable() {
				@Override public void run() {
					for (ILoginStateListener lsl : loginStateListeners) {
						try {
							lsl.connectionStateChanged(le, ex);
						} catch (Exception ignored) {
						}
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
		projectChanged = new ArrayList<ProjectChangedCallback>();
		dataChanged = new ArrayList<DataChangedCallback>();

		JakeMainApp.getInstance().addCoreChangedListener(new CoreChangedCallback() {
			@Override public void coreChanged() {
				log.debug("received core change, rolling out updates...");
				fireAllChanged();
			}
		});
	}

	private void fireAllChanged() {
		ObjectCache.get().updateAll();
		fireDataChanged(DataChangedCallback.ALL, null);
	}

	public static EventCore get() {
		return instance;
	}

	public void addProjectChangedCallbackListener(ProjectChangedCallback cb) {
		projectChanged.add(cb);
	}

	public void removeProjectChangedCallbackListener(ProjectChangedCallback cb) {
		log.trace("Deregister project changed callback: " + cb);

		if (projectChanged.contains(cb)) {
			projectChanged.remove(cb);
		}
	}

	public void fireProjectChanged(final ProjectChangedCallback.ProjectChangedEvent ev) {
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
		
		

	private void spreadProjectChanged(ProjectChangedCallback.ProjectChangedEvent ev) {
		for (ProjectChangedCallback callback : projectChanged) {
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

	public void addDataChangedCallbackListener(DataChangedCallback cb) {
		dataChanged.add(cb);
	}

	public void removeDataChangedCallbackListener(DataChangedCallback cb) {
		dataChanged.remove(cb);
	}

	public void fireDataChanged(EnumSet<DataChangedCallback.DataReason> dataReason,
					Project p) {
		log.trace("spread callback event data changed: " + dataReason);
		for (DataChangedCallback callback : dataChanged) {
			callback.dataChanged(dataReason, p);
		}

		// any stalled events?
		shootStalledProjectChangedEvents();
	}

	public void addFileSelectionListener(FileSelectionChangedCallback listener) {
		fileSelectionListeners.add(listener);
	}

	public void removeFileSelectionListener(FileSelectionChangedCallback listener) {
		fileSelectionListeners.remove(listener);
	}

	public void addNodeSelectionListener(NodeSelectionChangedCallback listener) {
		nodeSelectionListeners.add(listener);
	}

	public void removeNodeSelectionListener(NodeSelectionChangedCallback listener) {
		nodeSelectionListeners.remove(listener);
	}

	public void addContextChangedListener(ContextChangedCallback listener) {
		contextChangedListeners.add(listener);
	}

	public void removeContextChangedListener(ContextChangedCallback listener) {
		contextChangedListeners.remove(listener);
	}


	public void notifyFileSelectionListeners(java.util.List<FileObject> objs) {
		log.debug("notify selection listeners");
		for (FileSelectionChangedCallback listener : fileSelectionListeners) {
			listener.fileSelectionChanged(
							new FileSelectionChangedCallback.FileSelectedEvent(objs));
		}
	}

	public void notifyNodeSelectionListeners(
					java.util.List<ProjectFilesTreeNode> objs) {
		log.trace("notify selection listeners");
		for (NodeSelectionChangedCallback c : nodeSelectionListeners) {
			c.nodeSelectionChanged(new NodeSelectionChangedCallback.NodeSelectedEvent(objs));
		}
	}

	public ChangeListener getChangeListener() {
		return projectsChangeListener;
	}


	public void fireNotesChanged(Project p) {
		System.err.println("!!!!! HELLO, THE NOTES HAVE CHANGED !!!!!");
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

	public void fireLogChanged(Project p) {
		//log.error("!!!!! HELLO, THE LOG HAS CHANGED !!!!!");
		
		// TODO: UGLY HACK, change someday
		ObjectCache.get().updateNotes(p);
		fireDataChanged(DataChangedCallback.ALL, p);
	}

	public void addTasksChangedListener(TaskChangedCallback callback) {
		taskChangedListeners.add(callback);
	}

	public void removeTasksChangedListener(TaskChangedCallback callback) {
		taskChangedListeners.remove(callback);
	}

	public void fireTasksChangedListener(IJakeTask task, TaskChangedCallback.TaskOps op) {
		for (TaskChangedCallback callback : taskChangedListeners) {
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
	public ProjectChangedCallback.ProjectChangedEvent getLastProjectEvent(Project project) {
		return lastProjectEvents.get(project);
	}


	/**
	 * Fire Event that a context has changed.
	 *
	 * @param reason
	 * @param context
	 */
	public void fireContextChanged(ContextChangedCallback.Reason reason, Object context) {
		log.trace("notify property changed listeners");
		for (ContextChangedCallback c : contextChangedListeners) {
			c.contextChanged(EnumSet.of(reason), context);
		}
		
	}


	public IProjectInvitationListener getInvitationListener() {
		return invitationListener;
	}

	public void fireUserChanged(Project p) {
		fireDataChanged(EnumSet.of(DataChangedCallback.DataReason.User), p);
	}
}
