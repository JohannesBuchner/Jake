package com.jakeapp.gui.swing.models;

import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.synchronization.AttributedJakeObject;
import com.jakeapp.gui.swing.ICoreAccess;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.exceptions.NoteOperationFailedException;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;
import com.jakeapp.gui.swing.helpers.TimeUtilities;
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
	private List<AttributedJakeObject<NoteObject>> attributedNotes;
	private ResourceMap resourceMap;
	private ICoreAccess core;
	private Icon padlock, shared_note;

	public NotesTableModel() {
		this.resourceMap = org.jdesktop.application.Application.getInstance(com.jakeapp.gui.swing.JakeMainApp.class).getContext().getResourceMap(NotesTableModel.class);

		this.attributedNotes = new ArrayList<AttributedJakeObject<NoteObject>>();
		this.core = JakeMainApp.getCore();
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

	public AttributedJakeObject<NoteObject> getNoteAtRow(int row) {
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

		this.attributedNotes.clear();
		try {
			this.attributedNotes = this.core.getNotes(project);
		} catch (NoteOperationFailedException e) {
			ExceptionUtilities.showError(e);
		}
	}

	@Override
	public Object getValueAt(int row, int column) {
		Object value;
		AttributedJakeObject<NoteObject> note = this.getAttributedNotes().get(row);
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
				value = TimeUtilities.getRelativeTime(note.getLastModificationDate());
				break;
			case 4: //last editor
				value = note.getLastVersionLogEntry().getMember();
				break;

			default:
				value = "illegal column count!";
				log.warn("column count out of range. Range is 0-2, actually was :" + Integer.toString(row));
		}
		return value;
	}

	private List<AttributedJakeObject<NoteObject>> getAttributedNotes() {
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
