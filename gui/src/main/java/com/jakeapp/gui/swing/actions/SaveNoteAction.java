package com.jakeapp.gui.swing.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.apache.log4j.Logger;

import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.NoteAction;
import com.jakeapp.gui.swing.panels.NotesPanel;

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
	public void actionPerformed(ActionEvent e) {
		String newContent = NotesPanel.getInstance().getNoteReaderText();
		NoteObject cachedNote = this.getSelectedNotes().get(0);
		cachedNote.setContent(newContent);
		
		log.debug("saving note with new content: " + newContent);
		JakeMainApp.getCore().saveNote(cachedNote);
	}
	
	@Override
	public void updateAction() {
		this.setEnabled(this.getSelectedNotes().size() > 0);
	}
}
