package com.jakeapp.gui.swing.worker;

import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.synchronization.Attributed;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.panels.NotesPanel;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author studpete
 */
public class GetAllProjectNotesWorker extends
				SwingWorkerWithAvailableLaterObject<List<Attributed<NoteObject>>> {
	private static final Logger log = Logger.getLogger(GetAllProjectNotesWorker.class);
	private Project project;

	public GetAllProjectNotesWorker(Project project) {
		this.project = project;
	}

	@Override
	protected AvailableLaterObject<List<Attributed<NoteObject>>> calculateFunction() {
		log.debug("calling GetAllProjectNotesWorker:calculateFunction");
		return JakeMainApp.getCore().getNotes(project);
	}

	@Override
	protected void done() {
		log.info("Done GetAllProjectNotesWorker");

		// done! so lets update the filetree
		try {
			NotesPanel.getInstance().setProjectNotes(this.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
}