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
			JakeMainApp.getCore().newNote(new NoteObject(UUID.randomUUID(),
					NotesPanel.getInstance().getCurrentProject(),
					JakeMainView.getMainView().getResourceMap().getString("NewNoteDefaultContent")));
			//update notes view
			this.refreshNotesPanel();
		} catch (NoteOperationFailedException e) {
			ExceptionUtilities.showError(e);
		}
	}
}
