package com.jakeapp.gui.swing.actions.abstracts;

import java.awt.event.ActionEvent;

import org.jdesktop.swingx.JXTable;

/**
 * Abstract superclass for actions on notes.
 * @author Simon
 *
 */
public abstract class NoteAction extends ProjectAction {
	
	private JXTable notesTable;
	
	/**
	 * Constructs a new NoteAction that works with the given notesTable.
	 * @param notesTable
	 */
	public NoteAction(JXTable notesTable) {
		super();
		this.notesTable = notesTable;
	}
	
	protected JXTable getNotesTable() {
		return this.notesTable;
	}
}
