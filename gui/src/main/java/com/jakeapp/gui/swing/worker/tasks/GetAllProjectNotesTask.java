package com.jakeapp.gui.swing.worker.tasks;

import java.util.Collection;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.xcore.ObjectCache;

/**
 * Get all Notes for a certain project
 *
 * @author studpete
 */
public class GetAllProjectNotesTask extends AbstractTask<Collection<NoteObject>> {
	private static final Logger log = Logger.getLogger(GetAllProjectNotesTask.class);
	private Project project;

	public GetAllProjectNotesTask(Project project) {
		super();
		this.project = project;
	}

	@Override
	protected AvailableLaterObject<Collection<NoteObject>> calculateFunction() {
		log.trace("calling GetAllProjectNotesTask:calculateFunction");
		return JakeMainApp.getCore().getNotes(project);
	}

	@Override
	protected void onDone() {
		log.trace("Done GetAllProjectNotesTask");

		// done! so lets update the note-panel
		try {
			ObjectCache.get().setNotes(project, get());
		} catch (Exception ex) {
			log.warn("GetAllProjectNotesTask failed", ex);
		}
	}
}