package com.jakeapp.gui.swing.xcore;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.synchronization.ChangeListener;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.callbacks.ConnectionStatus;
import com.jakeapp.gui.swing.callbacks.CoreChanged;
import com.jakeapp.gui.swing.callbacks.DataChanged;
import com.jakeapp.gui.swing.callbacks.FileSelectionChanged;
import com.jakeapp.gui.swing.callbacks.NodeSelectionChanged;
import com.jakeapp.gui.swing.callbacks.ProjectChanged;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;
import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * The Core Event Distributor.
 */
public class EventCore {
	private static final Logger log = Logger.getLogger(EventCore.class);
	private static EventCore instance;

	//private final HashMap<Project, ProjectsChangeListener> projectChangeListenerHash =
	//				new HashMap<Project, ProjectsChangeListener>();

	private final ProjectsChangeListener projectsChangeListener = new ProjectsChangeListener();

	private final List<ConnectionStatus> connectionStatus;
	//private final List<RegistrationStatus> registrationStatus;
	private final List<ProjectChanged> projectChanged;
	private final List<DataChanged> dataChanged;
	private List<FileSelectionChanged> fileSelectionListeners =
					new ArrayList<FileSelectionChanged>();
	private List<NodeSelectionChanged> nodeSelectionListeners =
					new ArrayList<NodeSelectionChanged>();


	static {
		instance = new EventCore();
	}

	public EventCore() {
		connectionStatus = new ArrayList<ConnectionStatus>();
		//registrationStatus = new ArrayList<RegistrationStatus>();
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
		fireDataChanged(DataChanged.All);
	}

	public static EventCore get() {
		return instance;
	}

	public void addProjectChangedCallbackListener(ProjectChanged cb) {
		projectChanged.add(cb);
	}

	public void removeProjectChangedCallbackListener(ProjectChanged cb) {
		log.info("Deregister project changed callback: " + cb);

		if (projectChanged.contains(cb)) {
			projectChanged.remove(cb);
		}
	}

	public void fireProjectChanged(ProjectChanged.ProjectChangedEvent ev) {
		for (ProjectChanged callback : projectChanged) {
			callback.projectChanged(ev);
		}
	}

	public void addConnectionStatusCallbackListener(ConnectionStatus cb) {
		connectionStatus.add(cb);
	}

	public void removeConnectionStatusCallbackListener(ConnectionStatus cb) {
		connectionStatus.remove(cb);
	}


	public void fireConnectionStatus(ConnectionStatus.ConnectionStati state,
					String str) {
		log.info("spead callback event...");
		for (ConnectionStatus callback : connectionStatus) {
			callback.setConnectionStatus(state, str);
		}
	}

	public void addDataChangedCallbackListener(DataChanged cb) {
		dataChanged.add(cb);
	}

	public void removeDataChangedCallbackListener(DataChanged cb) {
		dataChanged.remove(cb);
	}

	public void fireDataChanged(EnumSet<DataChanged.Reason> reason) {
		log.info("spead callback event data changed: " + reason);
		for (DataChanged callback : dataChanged) {
			callback.dataChanged(reason);
		}
	}

	/*
	public void addRegistrationStatusCallbackListener(RegistrationStatus cb) {
		log.info("Registers registration status callback: " + cb);
	}

	public void removeRegistrationStatusCallbackListener(RegistrationStatus cb) {
		log.info("Deregisters registration status callback: " + cb);
	}

	private void fireRegistrationStatus(RegistrationStatus.RegisterStati state,
					String str) {
		for (RegistrationStatus callback : registrationStatus) {
			callback.setRegistrationStatus(state, str);
		}
	}*/


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

	public void notifyFileSelectionListeners(java.util.List<FileObject> objs) {
		log.debug("notify selection listeners");
		for (FileSelectionChanged c : fileSelectionListeners) {
			//c.fileSelectionChanged(new FileSelectionChanged.FileSelectedEvent(objs));
		}
	}

	public void notifyNodeSelectionListeners(
					java.util.List<ProjectFilesTreeNode> objs) {
		log.debug("notify selection listeners");
		for (NodeSelectionChanged c : nodeSelectionListeners) {
			c.nodeSelectionChanged(new NodeSelectionChanged.NodeSelectedEvent(objs));
		}
	}

	public ChangeListener getChangeListener() {
		return projectsChangeListener;
	}

	/*
	public ChangeListener registerProjectChangeListener(Project project) {
		ProjectsChangeListener pcl = new ProjectsChangeListener(project);
		projectChangeListenerHash.put(project, pcl);
		return pcl;
	}

	public void removeProjectChangeListener(Project project) {
		projectChangeListenerHash.remove(project);
	}
*/

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
	}
}
