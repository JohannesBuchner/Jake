package com.jakeapp.gui.swing.actions.notes;

import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.JakeContext;
import com.jakeapp.gui.swing.actions.abstracts.NoteAction;
import com.jakeapp.gui.swing.exceptions.NoteOperationFailedException;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;
import com.jakeapp.gui.swing.panels.NotesPanel;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Note action that saves the selected note.  
 * @author Simon
 *
 */
public class SaveNoteAction extends NoteAction {
	
	private static final long serialVersionUID = 196271937528474367L;
	private static final Logger log = Logger.getLogger(SaveNoteAction.class);
	
	public SaveNoteAction() {
		super();

		String actionStr = JakeMainView.getMainView().getResourceMap().getString("saveNoteMenuItem");
		putValue(Action.NAME, actionStr);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String newContent = NotesPanel.getInstance().getNoteReaderText();
		NoteObject cachedNote = this.getSelectedNote().getJakeObject();
		cachedNote.setContent(newContent);
		
		log.debug("saving note with new content: " + newContent);
		try {
			NotesPanel.getInstance().getNotesTableModel().setNoteToSelectLater(cachedNote);
			JakeMainApp.getCore().saveNote(cachedNote);
		} catch (NoteOperationFailedException e) {
			ExceptionUtilities.showError(e);
		}
	}
	
	@Override
	public void updateAction() {
		if (this.hasSelectedNotes()) { // notes are selected
			this.setEnabled(true);
			
			if(this.getSelectedNote().isLocked()) { // the file is locked
				if (getSelectedNote().getLockLogEntry().getMember().equals(JakeContext.getCurrentUser())) {
					// local user has lock
					this.setEnabled(true);
				} else {
					//somone else has the lock
					this.setEnabled(false);
				}
			}
		} else {
			this.setEnabled(false);
		}
	}
}
