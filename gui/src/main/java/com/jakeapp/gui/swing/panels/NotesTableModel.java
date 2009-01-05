package com.jakeapp.gui.swing.panels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;
import org.jdesktop.application.ResourceMap;

import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.ICoreAccess;
import com.jakeapp.gui.swing.JakeMainApp;

/**
 * Table model for the notes table.
 * @author Simon
 *
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
		public NoteMetaDataWrapper(NoteObject note, Date lastEdit, String lastEditor) {
			this.note = note;
			this.lastEdit = lastEdit;
			this.lastEditor = lastEditor;
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
	 * @param project the project from which the notes should be loaded. 
	 */
	public void update (Project project) {
		if (project == null) {
			return;
		}
		this.currentProject = project;
		this.notes.clear();
		List<NoteObject> incommingNotes = this.core.getNotes(project);
		for (NoteObject n : incommingNotes) {
			this.notes.add(new NoteMetaDataWrapper(n,
					core.getLastEdit(n, project),
					core.getLastEditor(n, project).getUserId().getFirstName()));
		}
	}
	
	@Override
	public Object getValueAt(int row, int column) {
		String value;
		switch (column) {
			case 0: //content
				value = this.getNotes().get(row).note.getContent();
				break;
			case 1: //last edit
				value = this.getNotes().get(row).lastEdit.toGMTString();
				break;
			case 2: //last editor
				value = this.getNotes().get(row).lastEditor;
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
