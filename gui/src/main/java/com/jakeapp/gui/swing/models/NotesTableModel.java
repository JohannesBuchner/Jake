package com.jakeapp.gui.swing.models;

import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException;
import com.jakeapp.core.domain.exceptions.ProjectNotLoadedException;
import com.jakeapp.gui.swing.ICoreAccess;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.helpers.TimeUtilities;
import org.apache.log4j.Logger;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
	private Icon padlock, shared_note;

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
		} catch (FrontendNotLoggedInException e) {
			// TODO 4 Peter/Chris
			e.printStackTrace();
		} catch (ProjectNotLoadedException e) {
			// TODO 4 Peter/Chris
			e.printStackTrace();
		}

		for (NoteObject n : incommingNotes) {
			//UserId id = this.core.getLastEditor(n, project).getUserId();
			this.notes.add(new NoteMetaDataWrapper(n,
					  this.core.getLastEdit(n), // TODO: Achtung: berechnung wird jedesmal durchgefuehrt.
					  "first + last",
					  core.isLocalNote(n),
					  core.isSoftLocked(n),
					  core.getLockingMessage(n)));
			// TODO: fix ProjectMember-changes
			//id.getFirstName() + id.getSurName()));
		}
	}

	@Override
	public Object getValueAt(int row, int column) {
		Object value;
		NoteMetaDataWrapper note = this.getNotes().get(row);
		switch (column) {
			case 0: // soft lock
				if (note.isSoftLocked) {
					value = this.padlock;
				} else {
					value = "";
				}
				break;
			case 1: //is local
				if (!note.isLocal) {
					value = this.shared_note;
				} else
					value = "";
				break;
			case 2: //content
				value = note.note.getContent();
				break;
			case 3: //last edit
				value = TimeUtilities.getRelativeTime(note.lastEdit);
				break;
			case 4: //last editor
				value = note.lastEditor;
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

	@Override
	public Class<?> getColumnClass(int columnIndex) {

		if (columnIndex <= 1) {
			return Icon.class;
		}
		return Object.class;
	}


}
