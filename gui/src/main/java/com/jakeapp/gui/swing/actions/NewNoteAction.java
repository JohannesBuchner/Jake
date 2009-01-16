package com.jakeapp.gui.swing.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.NoteAction;

/**
 * Note action for creating new notes. One note at a time.
 * @author Simon
 *
 */
public class NewNoteAction extends NoteAction {
	
	public NewNoteAction() {
		super();

		String actionStr = JakeMainView.getMainView().getResourceMap().getString("newNoteMenuItem");
		putValue(Action.NAME, actionStr);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

}
