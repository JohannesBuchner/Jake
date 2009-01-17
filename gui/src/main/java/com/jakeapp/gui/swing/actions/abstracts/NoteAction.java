package com.jakeapp.gui.swing.actions.abstracts;

import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.callbacks.NoteSelectionChanged;
import com.jakeapp.gui.swing.callbacks.ProjectSelectionChanged;
import com.jakeapp.gui.swing.panels.NotesPanel;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Abstract superclass for actions on notes.
 *
 * @author Simon
 */
public abstract class NoteAction extends ProjectAction
		  implements NoteSelectionChanged {

	private static final long serialVersionUID = 8541763489137302803L;
	private static Logger log = Logger.getLogger(NoteAction.class);

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
	 * Callback for the <code>NoteSelectionChanged</code> Listener. 
	 * @param event
	 */
	public void noteSelectionChanged(NoteSelectedEvent event) {
		
		setSelectedNotes(event.getNotes());
		updateAction();
	}

	public List<NoteObject> getSelectedNotes() {
		//FIXME: hack, hack, hack, don't know why it returns null ???
		if (this.selectedNotes == null) {
			return new ArrayList<NoteObject>();
		}
		return this.selectedNotes;
	}

	public void setSelectedNotes(List<NoteObject> notes) {
		this.selectedNotes = notes;
	}
}
