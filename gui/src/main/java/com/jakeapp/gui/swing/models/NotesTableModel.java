package com.jakeapp.gui.swing.models;

import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.synchronization.Attributed;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.helpers.JakeExecutor;
import com.jakeapp.gui.swing.helpers.TimeUtilities;
import com.jakeapp.gui.swing.worker.GetAllProjectNotesWorker;
import org.apache.log4j.Logger;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Table model for the notes table.
 *
 * @author Simon
 */
public class NotesTableModel extends DefaultTableModel {
	private static final long serialVersionUID = -2745782032637383756L;
	private static Logger log = Logger.getLogger(NotesTableModel.class);

	private List<String> columnNames;
	private List<Attributed<NoteObject>> attributedNotes;
	private ResourceMap resourceMap;
	private Icon padlock, shared_note;

	public NotesTableModel() {
		this.resourceMap = org.jdesktop.application.Application.getInstance(com.jakeapp.gui.swing.JakeMainApp.class).getContext().getResourceMap(NotesTableModel.class);

		this.attributedNotes = new ArrayList<Attributed<NoteObject>>();
		this.columnNames = new ArrayList<String>();
		this.columnNames.add(this.getResourceMap().getString("tableHeaderSoftLock"));
		this.columnNames.add(this.getResourceMap().getString("tableHeaderlocalNote"));
		this.columnNames.add(this.getResourceMap().getString("tableHeaderNote"));
		this.columnNames.add(this.getResourceMap().getString("tableHeaderLastEdit"));
		this.columnNames.add(this.getResourceMap().getString("tableHeaderLastEditor"));

		this.padlock = new ImageIcon(Toolkit.getDefaultToolkit()
				  .getImage(JakeMainApp.class.getResource("/icons/file-lock.png")));
		this.shared_note = new ImageIcon(Toolkit.getDefaultToolkit()
				  .getImage(JakeMainApp.class.getResource("/icons/shared_note.png")));

	}

	private ResourceMap getResourceMap() {
		return this.resourceMap;
	}

	public Attributed<NoteObject> getNoteAtRow(int row) {
		return this.attributedNotes.get(row);
	}

	@Override
	public int getColumnCount() {
		return this.columnNames.size();
	}

	@Override
	public String getColumnName(int column) {
		return this.columnNames.get(column);
	}

	@Override
	public int getRowCount() {
		return this.attributedNotes != null ? this.attributedNotes.size() : 0;
	}

	/**
	 * Update the contents of the table model. It tries to update with the current project.
	 */
	public void update() {
		this.update(JakeMainApp.getProject());
	}

	/**
	 * Update the contents of the table model for a given project.
	 *
	 * @param project the project from which the notes should be loaded.
	 */
	public void update(Project project) {
		if (project == null) {
			return;
		}

		// get notes!
	  JakeExecutor.exec(new GetAllProjectNotesWorker(JakeMainApp.getProject()));
	}

	public void updateNotes(List<Attributed<NoteObject>> notes) {
	// FIXME: exception handling!	
		this.attributedNotes.clear();
//		try {
			this.attributedNotes = notes;
	//	} catch (NoteOperationFailedException e) {
//			ExceptionUtilities.showError(e);
//		}
	}

	@Override
	public Object getValueAt(int row, int column) {
		Object value;
		Attributed<NoteObject> note = this.getAttributedNotes().get(row);
		switch (column) {
			case 0: // soft lock
				if (note.isLocked()) {
					value = this.padlock;
				} else {
					value = "";
				}
				break;
			case 1: //is local
				if (!note.isOnlyLocal()) {
					value = this.shared_note;
				} else
					value = "";
				break;
			case 2: //content
				value = (note.getJakeObject()).getContent();
				break;
			case 3: //last edit
				// FIXME: it is unclear what the attributed<> returns as lastModificationDate if it is
				// a local note.
				if (note.getLastModificationDate() == 0) { 
					value = "-";
				} else {
					value = TimeUtilities.getRelativeTime(note.getLastModificationDate());
				}
				break;
			case 4: //last editor
				if (note.getLastVersionEditor() != null) {
					value = note.getLastVersionEditor().getUserId();	
				} else {
					value = "local"; //FIXME: i18n
				}
				
				break;

			default:
				value = "illegal column count!";
				log.warn("column count out of range. Range is 0-2, actually was :" + Integer.toString(row));
		}
		return value;
	}

	private List<Attributed<NoteObject>> getAttributedNotes() {
		return this.attributedNotes;
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {

		if (columnIndex <= 1) {
			return Icon.class;
		}
		return Object.class;
	}
	
	/**
	 * Returns the row of a given note.
	 * @param note
	 * @return the number of the row of the given row if it exists in the model, or -1 if it does not
	 * exist.
	 */
	public int getRow(NoteObject note) {
		int row = -1;
		for (int i = 0; i < this.attributedNotes.size(); i++) {
			if (this.attributedNotes.get(i).getJakeObject().equals(note)) {
				row = i;
				break;
			}
		}
		return row;
	}
}
