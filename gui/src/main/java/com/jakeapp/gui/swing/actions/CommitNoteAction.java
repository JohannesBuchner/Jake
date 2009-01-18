package com.jakeapp.gui.swing.actions;

import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.synchronization.exceptions.SyncException;
import com.jakeapp.gui.swing.ICoreAccess;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.NoteAction;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Note Action that commits the selected notes. Batch enabled!
 *
 * @author Simon
 */
public class CommitNoteAction extends NoteAction {

	private static final long serialVersionUID = 5522637881549894198L;

	private static final ICoreAccess core = JakeMainApp.getCore();

	public CommitNoteAction() {
		super();

		String actionStr = JakeMainView.getMainView().getResourceMap().getString("commitNoteMenuItem");
		putValue(Action.NAME, actionStr);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			core.announceJakeObject(this.getSelectedNotes().get(0), null);
		} catch (SyncException e1) {
			ExceptionUtilities.showError(e1);
		}
	}

	@Override
	public void updateAction() {
		if (this.getSelectedNotes().size() > 0) {
			boolean isLocal = false;
			for (NoteObject note : getSelectedNotes()) {
				if (core.isLocalNote(note)) {
					isLocal = true;
					break;
				}
			}
			if (isLocal) {
				this.setEnabled(true);
			} else {
				this.setEnabled(false);
			}

		} else {
			this.setEnabled(false);
		}
	}
}
