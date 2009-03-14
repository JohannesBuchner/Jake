package com.jakeapp.gui.swing.worker;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.util.availablelater.AvailableErrorObject;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.callbacks.DataChanged;
import com.jakeapp.gui.swing.callbacks.ProjectChanged;
import com.jakeapp.gui.swing.callbacks.DataChanged.DataReason;
import com.jakeapp.gui.swing.exceptions.FileOperationFailedException;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;
import com.jakeapp.gui.swing.panels.NotesPanel;
import com.jakeapp.gui.swing.xcore.EventCore;

import java.util.EnumSet;
import java.util.List;

public class AnnounceJakeObjectTask extends AbstractTask<Void> {
	private List<JakeObject> jos;
	private String commitMessage;

	public AnnounceJakeObjectTask(List<JakeObject> jos, String commitMessage) {
		this.jos = jos;
		this.commitMessage = (commitMessage==null)?"":commitMessage;
	}

	@Override
	protected AvailableLaterObject<Void> calculateFunction() {
		try {
			return JakeMainApp.getCore().announceJakeObjects(jos, commitMessage);
		} catch (FileOperationFailedException e) {
			return new AvailableErrorObject<Void>(e);
		}
	}


	@Override
	protected void onDone() {
		EnumSet<DataReason> reason = null;
		Project project = null;

		// inform the core that there are new log entries available.
		EventCore.get().fireDataChanged(
				EnumSet.of(DataChanged.DataReason.Files), null);
		if (this.jos.size() > 0) {
			project = this.jos.get(0).getProject();
			if ((this.jos.get(0)) instanceof FileObject) {
				EventCore.get().fireFilesChanged(project);
				reason = EnumSet.of(DataChanged.DataReason.Files);
			} else if ((this.jos.get(0)) instanceof NoteObject) {
				NotesPanel.getInstance().getNotesTableModel()
						.setNoteToSelectLater((NoteObject) (this.jos.get(0)));
				EventCore.get().fireNotesChanged(project);
				reason = EnumSet.of(DataChanged.DataReason.Notes);
			}

			if (reason != null)
				EventCore.get().fireDataChanged(reason, null);
			
			EventCore.get().fireProjectChanged(
					new ProjectChanged.ProjectChangedEvent(project,
									ProjectChanged.ProjectChangedEvent.Reason.People));
		}
	}

}