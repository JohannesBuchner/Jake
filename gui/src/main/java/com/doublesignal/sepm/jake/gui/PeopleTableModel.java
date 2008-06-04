package com.doublesignal.sepm.jake.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import com.doublesignal.sepm.jake.core.domain.JakeObject;
import com.doublesignal.sepm.jake.core.domain.NoteObject;
import com.doublesignal.sepm.jake.core.services.IJakeGuiAccess;

@SuppressWarnings("serial")
/**
 * @author peter
 */
public class PeopleTableModel extends AbstractTableModel {
	private static Logger log = Logger.getLogger(PeopleTableModel.class);
	private List<NoteObject> notes = new ArrayList<NoteObject>();
	private final IJakeGuiAccess jakeGuiAccess;

	PeopleTableModel(IJakeGuiAccess jakeGuiAccess) {
		log.info("Initializing NoteTableModel.");
		this.jakeGuiAccess = jakeGuiAccess;
	}

	String[] colNames = new String[] { "Nickname", "UserID", "Status", "Comment" };
	boolean[] columnEditable = new boolean[] { true, false, false, true };

	enum NotesColumns {
		Nickname, UserID, Status, Comment
	}

	public int getColumnCount() {
		return colNames.length;
	}

	public int getRowCount() {
		return notes.size();
	}

	/**
	 * Updates the view for notes, get new notes from GuiAccess
	 */
	public void updateData() {
		log.info("Updating Notes data...");
		notes = jakeGuiAccess.getNotes();
	}

	/**
	 * Observable class, fires when data is updated.
	 * 
	 * @author peter
	 * 
	 */
	public class NotesUpdaterObservable extends Observable {
		public void dataUpdated() {
			setChanged();
			notifyObservers();
		}
	}

	private final NotesUpdaterObservable notesUpdater = new NotesUpdaterObservable();

	public Object getValueAt(int rowIndex, int columnIndex) {
		NoteObject note = notes.get(rowIndex);

		NotesColumns col = NotesColumns.values()[columnIndex];
		switch (col) {
		case Nickname:
			return note.getName();

		case UserID:
			return JakeObjLib.getTagString(note.getTags());

		case Status:
			return jakeGuiAccess.getLastModified(note).toString();

		case Comment:
			return jakeGuiAccess.getLastModifier(note).getNickname();

		default:
			throw new IllegalArgumentException(
					"Cannot get Information for column " + columnIndex);
		}
	}
/*
	@Override
	public void setValueAt(Object columnValue, int rowIndex, int columnIndex) {
		if (columnIndex == NotesColumns.Tags.ordinal()) {
			JakeObject foundJakeObject = notes.get(rowIndex);
			log.debug("handling a tag-change event");
			if (foundJakeObject != null) {
				String sTags = JakeObjLib.generateNewTagString(jakeGuiAccess,
						foundJakeObject, (String) columnValue);
				super.setValueAt(sTags, rowIndex, columnIndex);
			}
		}
		// possible other columns go here
	}
*/
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnEditable[columnIndex];
	}

	@Override
	public String getColumnName(int columnIndex) {
		return colNames[columnIndex];
	}

	public NotesUpdaterObservable getNotesUpdater() {
		return notesUpdater;
	}

	public List<NoteObject> getNotes() {
		return notes;
	}
}
