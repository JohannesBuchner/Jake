package com.jakeapp.gui.swing.models;

import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.synchronization.attributes.Attributed;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.globals.JakeContext;
import com.jakeapp.gui.swing.callbacks.DataChanged;
import com.jakeapp.gui.swing.helpers.ImageLoader;
import com.jakeapp.gui.swing.helpers.TimeUtilities;
import com.jakeapp.gui.swing.panels.NotesPanel;
import com.jakeapp.gui.swing.xcore.EventCore;
import com.jakeapp.gui.swing.xcore.ObjectCache;
import org.apache.log4j.Logger;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Table model for the notes table.
 *
 * @author Simon
 */
public class NotesTableModel extends DefaultTableModel implements DataChanged {
	private static final long serialVersionUID = -2745782032637383756L;
	private static Logger log = Logger.getLogger(NotesTableModel.class);

	private List<String> columnNames;
	private List<Attributed<NoteObject>> attributedNotes =
					new ArrayList<Attributed<NoteObject>>();
	private NoteObject noteToSelectLater = null;
	private ResourceMap resourceMap;
	private Icon padlock, shared_note;

	public NotesTableModel() {
		this.resourceMap = org.jdesktop.application.Application
						.getInstance(com.jakeapp.gui.swing.JakeMainApp.class).getContext()
						.getResourceMap(NotesTableModel.class);
		this.columnNames = new ArrayList<String>();
		this.columnNames.add(this.getResourceMap().getString("tableHeaderSoftLock"));
		this.columnNames.add(this.getResourceMap().getString("tableHeaderlocalNote"));
		this.columnNames.add(this.getResourceMap().getString("tableHeaderNote"));
		this.columnNames.add(this.getResourceMap().getString("tableHeaderLastEdit"));
		this.columnNames.add(this.getResourceMap().getString("tableHeaderLastEditor"));

		this.padlock = ImageLoader.get(JakeMainApp.class, "/icons/file-lock.png");
		this.shared_note = ImageLoader.get(JakeMainApp.class, "/icons/shared_note.png");

		EventCore.get().addDataChangedCallbackListener(this);
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
	 * Update the contents of the table model for a given project.
	 *
	 * @param project the project from which the notes should be loaded.
	 */
	public void update(Project project) {
		log.info("Updating note table model");
		if (project == null) {
			return;
		}

		ObjectCache.get().updateNotes(project);
	}

	/**
	 * @return the first currently selected note or  - if assigned - 
	 * {@link #getNoteToSelectLater}
	 */
	private NoteObject getNoteSelection() {
		//assumption: there is only one selected note
		/* get old selection */
		int oldSelection;
		Attributed<NoteObject> oldSelectionNote = null;
		NoteObject result = null;
		
		result = this.getNoteToSelectLater();
		if (result==null) {
			try {
				oldSelection = NotesPanel.getInstance().getNotesTable().getSelectedRow();
				oldSelectionNote = this.getNoteAtRow(NotesPanel.getInstance().getNotesTable().convertRowIndexToModel(oldSelection));
				result = (oldSelectionNote==null)?null:oldSelectionNote.getJakeObject();
			}
			catch (Exception ex) {
				//empty handling, since we might just initialize the whole panel/model
			}
		}
		
		return result;
	}
	
	/**
	 * Sets the selection of the NotesPanel to a specific note.
	 * @param toSelect The note to select in the NotesPanel. If null, nothing happens.
	 */
	private void setSelectedNote(NoteObject toSelect) {
		int newSelection;
		//select the row that was previously selected
		if (toSelect!=null) {
			newSelection = 0;
			for (Attributed<NoteObject> attributed : this.attributedNotes) {
				if (attributed.getJakeObject().equals(toSelect)) {
					//found the previously selected note!
					newSelection = NotesPanel.getInstance().getNotesTable().convertRowIndexToView(newSelection);
					NotesPanel.getInstance().getNotesTable().setRowSelectionInterval(newSelection, newSelection);
				}
				newSelection++;
			}
		}
	}
	
	/**
	 * Gets the note that should be selected when the next note-update happens.
	 * Clears this note, so that further calls to this method will return null,
	 * if no {@link #setNoteSelectLater} happens.
	 */
	private synchronized NoteObject getNoteToSelectLater() {
		NoteObject result = noteToSelectLater;
		
		this.noteToSelectLater = null;
		
		return result;
	}
	
	/**
	 * Sets a note that should be selected when the notes are updated the next time.
	 * the NoteObject might not exist in the model yet.
	 * @param toSelect
	 */
	public void setNoteToSelectLater(NoteObject toSelect) {
		this.noteToSelectLater = toSelect;
	}
	
	public void updateNotes(Project p) {
		NoteObject oldSelectionNote = this.getNoteSelection();

		
		// FIXME: cache better!?
		log.debug("updating notes from core and merge with attributes...");
		List<NoteObject> rawNotes = new ArrayList<NoteObject>(ObjectCache.get().getNotes(p));
		this.attributedNotes.clear();
		if (rawNotes != null) {
			for (NoteObject note : rawNotes) {
				this.attributedNotes
								.add(JakeMainApp.getCore().<NoteObject>getAttributed(p, note));
			}
		}
		log.debug("update done.");

		this.fireTableDataChanged();
		this.setSelectedNote(oldSelectionNote);
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
				log.warn("column count out of range. Range is 0-4, actually was :" + Integer
								.toString(row));
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
	 *
	 * @param note
	 * @return the number of the row of the given row if it exists in the model, or -1 if it does not
	 *         exist.
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

	@Override
	public void dataChanged(EnumSet<DataReason> dataReason, Project p) {

		if (dataReason.contains(DataReason.Notes)) {
			updateNotes(p);
		}
	}
}
