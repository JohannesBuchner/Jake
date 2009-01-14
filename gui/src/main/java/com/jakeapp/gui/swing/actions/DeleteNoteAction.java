package com.jakeapp.gui.swing.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.NoteAction;
import com.jakeapp.gui.swing.dialogs.generic.JSheet;
import com.jakeapp.gui.swing.dialogs.generic.SheetEvent;
import com.jakeapp.gui.swing.dialogs.generic.SheetListener;
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
		
		String[] options = {NotesPanel.getInstance().getResourceMap().getString("confirmDeleteNotes.ok"),
				NotesPanel.getInstance().getResourceMap().getString("genericCancel")};

		JSheet.showOptionSheet(NotesPanel.getInstance(),
				NotesPanel.getInstance().getResourceMap().getString("confirmDeleteNotes.text"),
				  JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0], new SheetListener() {
			@Override
			public void optionSelected(SheetEvent evt) {
				if (evt.getOption() == 0) {
					log.info("Deleting Notes...");
					for (NoteObject note : getSelectedNotes()) {
						JakeMainApp.getCore().deleteNote(note);
					}
				}
			}
		});
	}

	@Override
	public void updateAction() {
		log.debug("update Action...");
		log.debug("getSelectedNotes returns: " + this.getSelectedNotes());
		this.setEnabled(this.getSelectedNotes().size() > 0);
	}
}
