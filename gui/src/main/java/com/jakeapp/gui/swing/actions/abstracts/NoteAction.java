package com.jakeapp.gui.swing.actions.abstracts;

import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.gui.swing.callbacks.NoteSelectionChanged;
import com.jakeapp.gui.swing.panels.NotesPanel;

import java.util.List;

/**
 * Abstract superclass for actions on notes.
 *
 * @author Simon
 */
public abstract class NoteAction extends ProjectAction
		  implements NoteSelectionChanged {

	private List<NoteObject> notes;

	/**
	 * Constructs a new NoteAction that works with the given notesTable.
	 */
	public NoteAction() {
		super();

		setNotes(NotesPanel.getInstance().getSelectedNotes());

		NotesPanel.getInstance().addNoteSelectionListener(this);
	}

	/**
	 * Save the new notes, renew action
	 *
	 * @param event
	 */
	public void noteSelectionChanged(NoteSelectedEvent event) {
		setNotes(event.getNotes());

		// call the update action event
		updateAction();
	}


	public List<NoteObject> getNotes() {
		return notes;
	}

	public void setNotes(List<NoteObject> notes) {
		this.notes = notes;
	}
}
