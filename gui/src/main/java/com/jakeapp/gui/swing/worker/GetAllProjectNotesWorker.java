package com.jakeapp.gui.swing.worker;

import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.xcore.ObjectCache;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Get all Notes for a certain project
 * @author studpete
 */
public class GetAllProjectNotesWorker extends
				SwingWorkerWithAvailableLaterObject<List<NoteObject>> {
	private static final Logger log = Logger.getLogger(GetAllProjectNotesWorker.class);
	private Project project;

	public GetAllProjectNotesWorker(Project project) {
		this.project = project;
	}

	@Override
	protected AvailableLaterObject<List<NoteObject>> calculateFunction() {
		log.debug("calling GetAllProjectNotesWorker:calculateFunction");
		return JakeMainApp.getCore().getNotes(project);
	}

	@Override
	protected void done() {
		log.trace("Done GetAllProjectNotesWorker");

		// done! so lets update the filetree
		try {
			ObjectCache.get().setNotes(project, get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
}