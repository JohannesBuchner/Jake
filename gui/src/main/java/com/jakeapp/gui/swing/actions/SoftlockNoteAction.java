package com.jakeapp.gui.swing.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.NoteAction;

/**
 * Note Action to lock and unlock a note, on/off checkbox behaviour.
 * @author Simon
 *
 */
public class SoftlockNoteAction extends NoteAction {
	
	public SoftlockNoteAction() {
		super();

		String actionStr = JakeMainView.getMainView().getResourceMap().getString("deleteNoteMenuItem");
		putValue(Action.NAME, actionStr);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

}
