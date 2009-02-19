package com.jakeapp.gui.swing.actions;

import java.awt.event.ActionEvent;
import java.util.UUID;

import javax.swing.Action;

import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.NoteAction;
import com.jakeapp.gui.swing.exceptions.NoteOperationFailedException;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;
import com.jakeapp.gui.swing.panels.NotesPanel;

/**
 * Note action for creating new notes. One note at a time.
 * @author Simon
 *
 */
public class CreateNoteAction extends NoteAction {
	
	private static final long serialVersionUID = 8883731800177455307L;

	public CreateNoteAction() {
		super();

		String actionStr = JakeMainView.getMainView().getResourceMap().getString("newNoteMenuItem");
		putValue(Action.NAME, actionStr);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		try {
			NoteObject newNote = new NoteObject(
					NotesPanel.getInstance().getCurrentProject(),
					JakeMainView.getMainView().getResourceMap().getString("NewNoteDefaultContent"));
			
			JakeMainApp.getCore().newNote(newNote);
			//this.refreshNotesPanel();
			int row = NotesPanel.getInstance().getNotesTableModel().getRow(newNote);
			if (row > -1) {
				NotesPanel.getInstance().getNotesTable().changeSelection(row, 0, false, false);	
			}
		} catch (NoteOperationFailedException e) {
			ExceptionUtilities.showError(e);
		}
	}
}
