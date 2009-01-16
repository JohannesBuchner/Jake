package com.jakeapp.gui.swing.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.NoteAction;

/**
 * Note Action that commits the selected notes. Batch enabled!
 * @author Simon
 *
 */
public class CommitNoteAction extends NoteAction {
	
	public CommitNoteAction() {
		super();

		String actionStr = JakeMainView.getMainView().getResourceMap().getString("commitNoteMenuItem");
		putValue(Action.NAME, actionStr);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

}
