package com.jakeapp.gui.swing.worker;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.util.availablelater.AvailableErrorObject;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.core.util.availablelater.AvailableNowObject;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.callbacks.DataChanged;
import com.jakeapp.gui.swing.panels.NotesPanel;
import com.jakeapp.gui.swing.xcore.EventCore;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Collection;


public class DeleteJakeObjectsTask extends AbstractTask<Integer> {
	Project project;
	Collection<JakeObject> jos;

	private Project getProject() {
		return project;
	}

	private void setProject(Project project) {
		this.project = project;
	}

	private Collection<JakeObject> getJos() {
		return jos;
	}

	private void setJos(Collection<JakeObject> jos) {
		this.jos = jos;
	}

	public DeleteJakeObjectsTask(Project project, Collection<JakeObject> jos) {
		super();
		this.setProject(project);
		this.setJos(jos);
	}

	@Override
	protected AvailableLaterObject<Integer> calculateFunction() throws RuntimeException {
		if (this.jos==null || this.jos.size()==0) {
			return new AvailableNowObject<Integer>(0);
		}
		else if (containsNoteObjects()) {
			ArrayList<NoteObject> notes = new ArrayList<NoteObject>(); //this is bogus...check
			for (JakeObject jo : jos)
				notes.add((NoteObject)jo);
			return JakeMainApp.getCore().deleteNotes(notes);
		}
		else if (containsFileObjects()) {
			ArrayList<FileObject> files = new ArrayList<FileObject>(); //this is bogus...check
			for (JakeObject jo : jos)
				files.add((FileObject)jo);
			return JakeMainApp.getCore().deleteFiles(files, false);
		}
		else {
			return new AvailableErrorObject<Integer>(
				new IllegalStateException("DeleteJakeObjectsTask was not correctly initialized.")
			);
		}
	}

	/**
	 * @return true, if the FIRST element of jos contains a NoteObject.
	 */
	private boolean containsNoteObjects() {
		return this.jos.iterator().hasNext() && this.jos.iterator().next() instanceof NoteObject;
	}

	/**
	 * @return true, if the FIRST element of jos contains a FileObject.
	 */
	private boolean containsFileObjects() {
		return this.jos.iterator().hasNext() && this.jos.iterator().next() instanceof FileObject;
	}
	
	@Override
	public void done() {
		super.done();		

		// inform the core that there are new log entries available.
		EventCore.get().fireDataChanged(EnumSet.of(DataChanged.Reason.Files), null);
		if (containsFileObjects())
			EventCore.get().fireFilesChanged(this.project);
		else if (containsNoteObjects()) {
			//select a note to be selected after the deletion
			
			//inform the EventCore about the change
			EventCore.get().fireNotesChanged(this.project);
		}
	}
}