package com.jakeapp.gui.swing.actions;

import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.NoteAction;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * DeleteNote Action, that deletes the selected Note from the given <code>notesTable.
 *
 * @author Simon
 */
public class DeleteNoteAction extends NoteAction {
	private static final Logger log = Logger.getLogger(DeleteNoteAction.class);

	public DeleteNoteAction() {
		super();

		String actionStr = JakeMainView.getMainView().getResourceMap().getString("deleteNoteMenuItem");
		putValue(Action.NAME, actionStr);
	}

	@Override
	public void actionPerformed(ActionEvent event) {

		// we rely that notes is never null!
		for (NoteObject note : getSelectedNotes()) {
			try {
				JakeMainApp.getCore().deleteNote(note, this.getProject());
				// TODO: be not that generic on error handling!
			} catch (Exception e) {
				log.error(e);
				ExceptionUtilities.showError(e);
			}
		}
	}

	@Override
	public void updateAction() {
		setEnabled(getSelectedNotes().size() > 0);
	}
}
