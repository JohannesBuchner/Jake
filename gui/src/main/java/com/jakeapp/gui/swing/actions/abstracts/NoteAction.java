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

	private List<NoteObject> selectedNotes;

	/**
	 * Constructs a new NoteAction that works with the given notesTable.
	 */
	public NoteAction() {
		super();

		setSelectedNotes(NotesPanel.getInstance().getSelectedNotes());

		NotesPanel.getInstance().addNoteSelectionListener(this);
	}

	/**
	 * Save the new notes, renew action
	 *
	 * @param event
	 */
	public void noteSelectionChanged(NoteSelectedEvent event) {
		setSelectedNotes(event.getNotes());

		// call the update action event
		updateAction();
	}


	public List<NoteObject> getSelectedNotes() {
		return this.selectedNotes;
	}

	public void setSelectedNotes(List<NoteObject> notes) {
		this.selectedNotes = notes;
	}
}
