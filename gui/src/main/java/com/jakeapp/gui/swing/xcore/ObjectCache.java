package com.jakeapp.gui.swing.xcore;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.InvitationState;
import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.callbacks.DataChanged;
import com.jakeapp.gui.swing.helpers.JakeExecutor;
import com.jakeapp.gui.swing.worker.GetAllProjectFilesWorker;
import com.jakeapp.gui.swing.worker.GetProjectsWorker;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

/**
 * This is a generic cache that saves data from the core.
 * Especially useful to cache the data from AvailableLaterObjects.
 *
 * @author studpete
 */
public class ObjectCache {
	private static final Logger log = Logger.getLogger(ObjectCache.class);
	private static ObjectCache instance = new ObjectCache();

	private List<Project> myProjects = new ArrayList<Project>();
	private List<Project> invitedProjects = new ArrayList<Project>();
	private HashMap<Project, List<FileObject>> files =
					new HashMap<Project, List<FileObject>>();
	private HashMap<Project, List<NoteObject>> notes =
					new HashMap<Project, List<NoteObject>>();

	// do not construct
	private ObjectCache() {
	}

	/**
	 * Returns the Singleton Instance.
	 *
	 * @return
	 */
	public static ObjectCache get() {
		return instance;
	}

	public List<Project> getMyProjects() {
		return myProjects;
	}

	public void setMyProjects(List<Project> myProjects) {
		this.myProjects = myProjects;
		fireProjectDataChanged();
	}

	private void fireProjectDataChanged() {
		EventCore.get().fireDataChanged(EnumSet.of(DataChanged.Reason.Projects), null);
	}

	public List<Project> getInvitedProjects() {
		return invitedProjects;
	}

	public void setInvitedProjects(List<Project> invitedProjects) {
		this.invitedProjects = invitedProjects;
		fireProjectDataChanged();
	}

	public List<FileObject> getFiles(Project p) {
		return files.get(p);
	}

	public void setFiles(Project project, List<FileObject> files) {
		this.files.put(project, files);
		fireFilesDataChanged(project);
	}

	private void fireFilesDataChanged(Project project) {
		EventCore.get().fireDataChanged(EnumSet.of(DataChanged.Reason.Files), project);
	}

	public void updateProjects() {
		if (JakeMainApp.isCoreInitialized()) {
			JakeExecutor.exec(new GetProjectsWorker(EnumSet.of(InvitationState.ACCEPTED)));
			JakeExecutor.exec(new GetProjectsWorker(EnumSet.of(InvitationState.INVITED)));
		}
	}

	public void updateFiles(Project p) {
		if (JakeMainApp.isCoreInitialized()) {
			JakeExecutor.exec(new GetAllProjectFilesWorker(p));
		}
	}

	public void updateAll() {
		updateProjects();
	}

	/**
	 * Save getter for project files.
	 * Returns null and starts the files getter, if no files currently in the cache.
	 *
	 * @param project
	 * @return
	 */
	public List<FileObject> getFilesSave(Project project) {
		List<FileObject> files = getFiles(project);
		if (files != null) {
			return files;
		} else {
			//updateFiles(project);
			return new ArrayList<FileObject>();
		}
	}
}
