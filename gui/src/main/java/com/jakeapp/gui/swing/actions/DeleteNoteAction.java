package com.jakeapp.gui.swing.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.jdesktop.application.ResourceMap;

import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.ProjectMember;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.gui.swing.ICoreAccess;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.NoteAction;
import com.jakeapp.gui.swing.dialogs.generic.JSheet;
import com.jakeapp.gui.swing.dialogs.generic.SheetEvent;
import com.jakeapp.gui.swing.dialogs.generic.SheetListener;
import com.jakeapp.gui.swing.helpers.Translator;
import com.jakeapp.gui.swing.panels.NotesPanel;

/**
 * DeleteNote Action, that deletes the selected Note from the given <code>notesTable.
 *
 * @author Simon
 */
public class DeleteNoteAction extends NoteAction {

	private static final long serialVersionUID = 8553169924173654143L;
	private static final Logger log = Logger.getLogger(DeleteNoteAction.class);

	public DeleteNoteAction() {
		super();

		String actionStr = JakeMainView.getMainView().getResourceMap().getString("deleteNoteMenuItem");
		putValue(Action.NAME, actionStr);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		
		final List<NoteObject> cache = new ArrayList<NoteObject>(getSelectedNotes());
		ICoreAccess core = JakeMainApp.getCore();
		UserId currentUser = JakeMainApp.getProject().getUserId();
		
		ResourceMap map = NotesPanel.getInstance().getResourceMap();
		String[] options = {map.getString("confirmDeleteNote.ok"), map.getString("genericCancel")};
		String text;

		
		if (cache.size() == 1) { //single delete
			if (JakeMainApp.getCore().isSoftLocked(cache.get(0))) { //is locked
				ProjectMember lockOwner = core.getLockOwner(cache.get(0));

				if (lockOwner.getUserId().equals(currentUser)) { //local member is owner
					text = map.getString("confirmDeleteNote.text");
				} else {
					text = Translator.get(map, "confirmDeleteLockedNote.text", lockOwner.getNickname());
				}
			} else {
				text = map.getString("confirmDeleteNote.text");
			}
		} else { //batch delete
			log.debug("batch delete -------------------------------------------------------------");
			boolean locked = false;
			for (NoteObject note : cache) {
				if (core.isSoftLocked(note) && !core.getLockOwner(note).getUserId().equals(currentUser)) {
					locked = true;
					break;
				}
			}
			if (locked) {
				log.debug("at least one file is locked");
				text = map.getString("confirmDeleteLockedNotes.text");
			} else {
				log.debug("no file is locked or locked by the local user");
				text = map.getString("conformDeleteNotes.text");
			}
		}
		
		
		JSheet.showOptionSheet(NotesPanel.getInstance(), text,
				  JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0], new SheetListener() {
			@Override
			public void optionSelected(SheetEvent evt) {
				if (evt.getOption() == 0) {
					for (NoteObject note : cache) {
						JakeMainApp.getCore().deleteNote(note);
					}
				}
			}
		});
	}

	@Override
	public void updateAction() {
		this.setEnabled(this.getSelectedNotes().size() > 0);
	}
}
