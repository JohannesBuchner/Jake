package com.jakeapp.gui.swing.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.jdesktop.swingx.JXTable;

import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.NoteAction;

/**
 * DeleteNote Action, that deletes the selected Note from the given <code>notesTable.
 * @author Simon
 *
 */
public class DeleteNote extends NoteAction {

	public DeleteNote(JXTable notesTable) {
		super(notesTable);
		
		String actionStr = JakeMainView.getMainView().getResourceMap(). getString("deleteNoteMenuItem");
		putValue(Action.NAME, actionStr);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int row = this.getNotesTable().getSelectedRow(); 
		if (row != -1) {
			//JakeMainApp.getCore().deleteNote(this.getNotesTable().getModel().getNoteAtRow(row));
		}
	}	
}
