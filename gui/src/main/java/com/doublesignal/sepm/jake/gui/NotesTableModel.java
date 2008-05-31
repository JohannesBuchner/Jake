package com.doublesignal.sepm.jake.gui;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import com.doublesignal.sepm.jake.core.domain.NoteObject;

@SuppressWarnings("serial")
/**
 * @author peter
 */
public class NotesTableModel extends AbstractTableModel {
	private static Logger log = Logger.getLogger(NotesTableModel.class);
	private final List<NoteObject> notes;

	NotesTableModel(List<NoteObject> notes) {
		log.info("Initializing NoteTableModel.");
		this.notes = notes;
	}

	String[] colNames = new String[] { "Title", "Tags", "Last changed", "User" };
	boolean[] columnEditable = new boolean[] { true, true, true, false };

	enum NotesColumns {
		Title, Tags, LastChanged, User
	}

	public int getColumnCount() {
		return colNames.length;
	}

	public int getRowCount() {
		return notes.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		NoteObject note = notes.get(rowIndex);

		NotesColumns col = NotesColumns.values()[columnIndex];
		switch (col) {
		case Title:
			return note.getName();

		case Tags:
			return note.getTags();

		case LastChanged:
			return "???";

		case User:
			return "???";

		default:
			throw new IllegalArgumentException(
					"Cannot get Information for column " + columnIndex);
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnEditable[columnIndex];
	}

	@Override
	public String getColumnName(int columnIndex) {
		return colNames[columnIndex];
	}
}
