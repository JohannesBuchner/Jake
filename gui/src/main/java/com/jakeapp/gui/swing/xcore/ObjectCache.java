package com.jakeapp.gui.swing.xcore;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeStatusBar;
import com.jakeapp.gui.swing.callbacks.DataChanged;
import com.jakeapp.gui.swing.callbacks.PropertyChanged;
import com.jakeapp.gui.swing.worker.GetAllProjectFilesTask;
import com.jakeapp.gui.swing.worker.GetAllProjectNotesTask;
import com.jakeapp.gui.swing.worker.GetMyProjectsTask;
import com.jakeapp.gui.swing.worker.JakeExecutor;
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
public class ObjectCache implements PropertyChanged {
	private static final Logger log = Logger.getLogger(ObjectCache.class);
	private static ObjectCache instance = new ObjectCache();

	private List<Project> myProjects = new ArrayList<Project>();
	private HashMap<Project, List<FileObject>> files =
					new HashMap<Project, List<FileObject>>();
	private HashMap<Project, List<NoteObject>> notes =
					new HashMap<Project, List<NoteObject>>();
	//private HashMap<Project, List<LogEntry>> logEntries =
	//				new HashMap<Project, List<LogEntry>>();

	// do not construct
	private ObjectCache() {
		EventCore.get().addPropertyListener(this);
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

	public List<NoteObject> getNotes(Project p) {
		return notes.get(p);
	}

	public void setNotes(Project project, List<NoteObject> notes) {
		this.notes.put(project, notes);
		fireNotesDataChanged(project);
	}

	private void fireNotesDataChanged(Project project) {
		EventCore.get().fireDataChanged(EnumSet.of(DataChanged.Reason.Notes), project);
	}

	/**
	 * Only update the project if core is initialized
	 * and a user(=MsgService) is selected.
	 */
	public void updateProjects() {
		if (JakeMainApp.isCoreInitialized() && JakeMainApp.getMsgService() != null) {
			JakeExecutor.exec(new GetMyProjectsTask());
		}
	}

	public void updateFiles(Project p) {
		if (JakeMainApp.isCoreInitialized()) {
			JakeExecutor.exec(new GetAllProjectFilesTask(p));
			JakeStatusBar.updateMessage();
		}
	}

	public void updateNotes(Project p) {
		if (JakeMainApp.isCoreInitialized()) {
			JakeExecutor.exec(new GetAllProjectNotesTask(p));
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

	/*
	public void updateLog(Project p) {
		if (JakeMainApp.isCoreInitialized()) {
			JakeExecutor.exec(new GetAllProjectNotesTask(p));
		}
	}
	*/

	/**
	 * Catch the PropertyChange - Event.
	 * We may have to update some data of some app properties change.
	 * @param reason
	 * @param p optional variable
	 * @param data optional poperty
	 */
	@Override public void propertyChanged(EnumSet<Reason> reason, Project p,
					Object data) {

		// update the projects when the MsgService is selected
		// (thus the user logged in)
		if(reason.contains(Reason.MsgService)) {
			this.updateProjects();
		}
	}
}
