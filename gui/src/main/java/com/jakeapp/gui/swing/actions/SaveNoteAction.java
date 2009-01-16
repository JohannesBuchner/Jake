package com.jakeapp.gui.swing.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.NoteAction;

/**
 * Note action that saves the selected note.  
 * @author Simon
 *
 */
public class SaveNoteAction extends NoteAction {
	
	private static final long serialVersionUID = 196271937528474367L;

	public SaveNoteAction() {
		super();

		String actionStr = JakeMainView.getMainView().getResourceMap().getString("saveNoteMenuItem");
		putValue(Action.NAME, actionStr);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void updateAction() {
		this.setEnabled(this.getSelectedNotes().size() > 0);
	}
}
