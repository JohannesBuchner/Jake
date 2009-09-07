package com.jakeapp.gui.swing.worker.tasks;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import com.jakeapp.availablelater.AvailableErrorObject;
import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.callbacks.DataChangedCallback;
import com.jakeapp.gui.swing.callbacks.ProjectChangedCallback;
import com.jakeapp.gui.swing.callbacks.DataChangedCallback.DataReason;
import com.jakeapp.gui.swing.exceptions.FileOperationFailedException;
import com.jakeapp.gui.swing.panels.NotesPanel;
import com.jakeapp.gui.swing.xcore.EventCore;

public class AnnounceJakeObjectTask extends AbstractTask<Void> {
	private List<JakeObject> jos;
	private String commitMessage;

	public AnnounceJakeObjectTask(List<JakeObject> jos, String commitMessage) {
		this.jos = jos;
		this.commitMessage = (commitMessage == null) ? "" : commitMessage;
	}

	public AnnounceJakeObjectTask(FileObject fo, String commitMessage) {
		this(Arrays.asList((JakeObject)fo), commitMessage);
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
		EventCore.get()
						.fireDataChanged(EnumSet.of(DataChangedCallback.DataReason.Files), null);
		if (this.jos.size() > 0) {
			project = this.jos.get(0).getProject();
			if ((this.jos.get(0)) instanceof FileObject) {
				EventCore.get().fireFilesChanged(project);
				reason = EnumSet.of(DataChangedCallback.DataReason.Files);
			} else if ((this.jos.get(0)) instanceof NoteObject) {
				NotesPanel.getInstance().getNotesTableModel()
								.setNoteToSelectLater((NoteObject) (this.jos.get(0)));
				EventCore.get().fireNotesChanged(project);
				reason = EnumSet.of(DataChangedCallback.DataReason.Notes);
			}

			if (reason != null)
				EventCore.get().fireDataChanged(reason, null);

			EventCore.get().fireProjectChanged(
							new ProjectChangedCallback.ProjectChangedEvent(project,
											ProjectChangedCallback.ProjectChangedEvent.Reason.People));
		}

		// TODO: only poke?
		if (project != null) {
			JakeMainApp.getCore().syncProject(project, null);
		}
	}
}