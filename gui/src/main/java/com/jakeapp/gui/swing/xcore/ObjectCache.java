package com.jakeapp.gui.swing.xcore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.callbacks.ContextChangedCallback;
import com.jakeapp.gui.swing.callbacks.DataChangedCallback;
import com.jakeapp.gui.swing.components.JakeStatusBar;
import com.jakeapp.gui.swing.globals.JakeContext;
import com.jakeapp.gui.swing.worker.GetAllProjectFilesTask;
import com.jakeapp.gui.swing.worker.GetAllProjectNotesTask;
import com.jakeapp.gui.swing.worker.GetMyProjectsTask;
import com.jakeapp.gui.swing.worker.JakeExecutor;

/**
 * This is a generic cache that saves data from the core.
 * Especially useful to cache the data from AvailableLaterObjects.
 *
 * @author studpete
 */
public class ObjectCache implements ContextChangedCallback {
	private static final Logger log = Logger.getLogger(ObjectCache.class);
	private static ObjectCache instance = new ObjectCache();

	private List<Project> myProjects = new ArrayList<Project>();
	private HashMap<Project, Collection<FileObject>> files =
					new HashMap<Project, Collection<FileObject>>();
	private HashMap<Project, Collection<NoteObject>> notes =
					new HashMap<Project, Collection<NoteObject>>();
	//private HashMap<Project, List<LogEntry>> logEntries =
	//				new HashMap<Project, List<LogEntry>>();

	// do not construct
	private ObjectCache() {
		EventCore.get().addContextChangedListener(this);
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
		return this.myProjects;
	}

	public void setMyProjects(List<Project> myProjects) {
		this.myProjects = myProjects;
		fireProjectDataChanged();
	}

	private void fireProjectDataChanged() {
		EventCore.get().fireDataChanged(EnumSet.of(DataChangedCallback.DataReason.Projects), null);
	}

	public Collection<FileObject> getFiles(Project p) {
		//XXX: FIXME: same hack as in getNotes(). DANGER! ZOMBIES AHEAD!
		// FIXME: UltraFAIL
		// this.updateFiles(p);
		if(!this.files.containsKey(p)) {
			log.warn("files requested, but not available yet");
			return new LinkedList<FileObject>();
		}
		return this.files.get(p);
	}

	public void setFiles(Project project, Collection<FileObject> files) {
		log.debug("got " + files.size() + "files");
		for(FileObject file : files) {
			log.debug("got " + file);
		}
		this.files.put(project, files);
		fireFilesDataChanged(project);
	}

	private void fireFilesDataChanged(Project project) {
		EventCore.get().fireDataChanged(EnumSet.of(DataChangedCallback.DataReason.Files), project);
	}

	public Collection<NoteObject> getNotes(Project p) {
		//XXX: FIXME: this hack renders the cache useless. It also causes some null pointer exceptions on
		// startup. But hey, it works!!!
		// FIXME: No, it doesn't :'(
		// this.updateNotes(p);
		if(!this.files.containsKey(p)) {
			log.warn("notes requested, but not available yet");
			return new LinkedList<NoteObject>();
		}
		return this.notes.get(p);
	}

	public void setNotes(Project project, Collection<NoteObject> notes) {
		this.notes.put(project, notes);
		fireNotesDataChanged(project);
	}

	private void fireNotesDataChanged(Project project) {
		EventCore.get().fireDataChanged(EnumSet.of(DataChangedCallback.DataReason.Notes), project);
	}

	/**
	 * Only update the project if core is initialized
	 * and a user(=MsgService) is selected.
	 */
	public void updateProjects() {
		if (JakeContext.isCoreInitialized() && JakeContext.getMsgService() != null) {
			JakeExecutor.exec(new GetMyProjectsTask());
		}
	}

	public void updateFiles(Project p) {
		if (JakeContext.isCoreInitialized()) {
			JakeExecutor.exec(new GetAllProjectFilesTask(p));
			JakeStatusBar.updateMessage();
		}
	}

	public void updateNotes(Project p) {
		if (JakeContext.isCoreInitialized()) {
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
	public Collection<FileObject> getFilesSave(Project project) {
		Collection<FileObject> files = getFiles(project);
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
	 * @param context
	 */
	@Override public void contextChanged(EnumSet<Reason> reason, Object context) {

		// update the projects when the MsgService is selected
		// (thus the user logged in)
		if(reason.contains(Reason.MsgService)) {
			this.updateProjects();
		}
	}
}
