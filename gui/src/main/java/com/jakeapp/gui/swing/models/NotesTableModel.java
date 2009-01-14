package com.jakeapp.gui.swing.models;

import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.exceptions.NotLoggedInException;
import com.jakeapp.core.domain.exceptions.ProjectNotLoadedException;
import com.jakeapp.gui.swing.ICoreAccess;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.helpers.TimeUtilities;
import org.apache.log4j.Logger;
import org.jdesktop.application.ResourceMap;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Date;
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
	private List<NoteMetaDataWrapper> notes;
	private ResourceMap resourceMap;
	private Project currentProject;
	private ICoreAccess core;

	private class NoteMetaDataWrapper {
		public NoteObject note;
		public Date lastEdit;
		public String lastEditor;
		public boolean isLocal;
		public boolean isSoftLocked;
		public String lockingMessage;

		public NoteMetaDataWrapper(NoteObject note, Date lastEdit, String lastEditor, boolean isLocal, boolean isSoftLocked, String lockingMessage) {
			this.note = note;
			this.lastEdit = lastEdit;
			this.lastEditor = lastEditor;
			this.isLocal = isLocal;
			this.isSoftLocked = isSoftLocked;
			this.lockingMessage = lockingMessage;
		}
	}

	public NotesTableModel() {
		this.resourceMap = org.jdesktop.application.Application.getInstance(com.jakeapp.gui.swing.JakeMainApp.class).getContext().getResourceMap(NotesTableModel.class);

		this.notes = new ArrayList<NoteMetaDataWrapper>();
		this.core = JakeMainApp.getApp().getCore();
		this.columnNames = new ArrayList<String>();
		this.columnNames.add(this.getResourceMap().getString("tableHEaderNote"));
		this.columnNames.add(this.getResourceMap().getString("tableHeaderLastEdit"));
		this.columnNames.add(this.getResourceMap().getString("tableHeaderLastEditor"));
		this.columnNames.add(this.getResourceMap().getString("tableHeaderlocalNote"));
		this.columnNames.add(this.getResourceMap().getString("tableHeaderSoftLock"));

	}

	private ResourceMap getResourceMap() {
		return this.resourceMap;
	}

	public NoteObject getNoteAtRow(int row) {
		return this.notes.get(row).note;
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
		return this.notes != null ? this.notes.size() : 0;
	}

	/**
	 * Update the contents of the table model. It tries to update with the current project.
	 */
	public void update() {
		this.update(this.currentProject);
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
		this.currentProject = project;
		this.notes.clear();
		List<NoteObject> incommingNotes = new ArrayList<NoteObject>();


        try {
            incommingNotes = this.core.getNotes(project);
        } catch (NotLoggedInException e) {
            // TODO 4 Peter/Chris
            e.printStackTrace();
        } catch (ProjectNotLoadedException e) {
            // TODO 4 Peter/Chris
            e.printStackTrace();
        }
        
        for (NoteObject n : incommingNotes) {
			//UserId id = this.core.getLastEditor(n, project).getUserId();
			this.notes.add(new NoteMetaDataWrapper(n,
					  this.core.getLastEdit(n, project), // TODO: Achtung: berechnung wird jedesmal durchgefuehrt.
					  "first + last",
					  core.isLocalNote(n, project),
					  core.isSoftLocked(n, project),
					  core.getLockingMessage(n, project)));
			// TODO: fix ProjectMember-changes
			//id.getFirstName() + id.getSurName()));
		}
	}

	@Override
	public Object getValueAt(int row, int column) {
		String value;
		NoteMetaDataWrapper note = this.getNotes().get(row);
		switch (column) {
			case 0: //content
				value = note.note.getContent();
				break;
			case 1: //last edit
				value = TimeUtilities.getRelativeTime(note.lastEdit);
				break;
			case 2: //last editor
				value = note.lastEditor;
				break;
			case 3: //is local
				value = Boolean.toString(note.isLocal);
				break;
			case 4: // soft lock
				value = Boolean.toString(note.isSoftLocked);
				break;
			default:
				value = "illegal column count!";
				log.warn("column count out of range. Range is 0-2, actually was :" + Integer.toString(row));
		}
		return value;
	}

	private List<NoteMetaDataWrapper> getNotes() {
		return this.notes;
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
}
