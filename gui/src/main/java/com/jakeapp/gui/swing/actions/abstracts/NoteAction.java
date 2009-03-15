package com.jakeapp.gui.swing.actions.abstracts;

import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.synchronization.attributes.Attributed;
import com.jakeapp.gui.swing.globals.JakeContext;
import com.jakeapp.gui.swing.callbacks.NoteSelectionChangedCallback;
import com.jakeapp.gui.swing.panels.NotesPanel;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract superclass for actions on notes.
 *
 * @author Simon
 */
public abstract class NoteAction extends ProjectAction implements NoteSelectionChangedCallback {

	private static final long serialVersionUID = 8541763489137302803L;
	private static Logger log = Logger.getLogger(NoteAction.class);

	private List<Attributed<NoteObject>> selectedNotes = new ArrayList<Attributed<NoteObject>>();

	/**
	 * Constructs a new NoteAction that works with the given notesTable.
	 */
	public NoteAction() {
		super();

		NotesPanel.getInstance().addNoteSelectionListener(this);
	}

	/**
	 * Callback for the <code>NoteSelectionChanged</code> Listener.
	 *
	 * @param event
	 */
	public void noteSelectionChanged(NoteSelectedEvent event) {
		updateAction();
	}

	public List<Attributed<NoteObject>> getSelectedNotes() {
		return NotesPanel.getInstance().getSelectedNotes();
	}

	@Override
	public void updateAction() {
		this.setEnabled((JakeContext.getProject() != null));
	}

	protected void refreshNotesPanel() {
		//XXX very cheap implementation
		NotesPanel.getInstance().projectChanged(
						new ProjectChangedEvent(JakeContext.getProject(),
										ProjectChangedEvent.Reason.Syncing));
	}

	protected Attributed<NoteObject> getSelectedNote() {
		if (getSelectedNotes().size() > 0) {
			return getSelectedNotes().get(0);
		} else {
			return null;
		}
	}

	/**
	 * Checks if there are selected notes.
	 * @return true if at least one note is selected.
	 */
	public boolean hasSelectedNotes() {
		return getSelectedNotes() != null && getSelectedNotes().size() > 0;
	}

	/**
	 * Checks if there is a single note selected.
	 * @return
	 */
	public boolean hasSingleSelectedNote() {
		return getSelectedNotes() != null && getSelectedNotes().size() == 1;
	}
}
