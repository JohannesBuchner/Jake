package com.jakeapp.gui.swing.actions.notes;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.synchronization.attributes.Attributed;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.NoteAction;
import com.jakeapp.gui.swing.worker.AnnounceJakeObjectTask;
import com.jakeapp.gui.swing.worker.JakeExecutor;

import javax.swing.*;

import org.apache.log4j.Logger;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

/**
 * Note Action that commits the selected notes. Batch enabled!
 *
 * @author Simon
 */
public class CommitNoteAction extends NoteAction {

	private static final long serialVersionUID = 5522637881549894198L;
	private static final Logger log = Logger.getLogger(CommitNoteAction.class);
	public CommitNoteAction() {
		super();

		String actionStr = JakeMainView.getMainView().getResourceMap().getString("commitNoteMenuItem");
		putValue(Action.NAME, actionStr);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
			/*
			for (Attributed<NoteObject> attributedNote : this.getSelectedNotes()) {

				JakeMainApp.getCore().announceJakeObject(attributedNote.getJakeObject(), null);
			}*/
			//this.refreshNotesPanel();
			
		JakeExecutor.exec(
			new AnnounceJakeObjectTask(
				new ArrayList<JakeObject>(
					Attributed.castDownCollection(
						Attributed.extract(this.getSelectedNotes())
					)
				),
				null
			)
		);
	}

	@Override
	public void updateAction() {
		if (this.hasSelectedNotes()) {
			boolean isLocal = false;
			for (Attributed<NoteObject> note : getSelectedNotes()) {
				if (note.isLocal()) {
					isLocal = true;
					break;
				}
			}
			this.setEnabled(isLocal);
		} else {
			this.setEnabled(false);
		}
	}
}