package com.doublesignal.sepm.jake.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import com.doublesignal.sepm.jake.core.domain.ProjectMember;
import com.doublesignal.sepm.jake.core.services.IJakeGuiAccess;

@SuppressWarnings("serial")
/**
 * @author philipp
 */
public class PeopleTableModel extends AbstractTableModel {
	private static Logger log = Logger.getLogger(PeopleTableModel.class);
	private List<ProjectMember> members = new ArrayList<ProjectMember>();
	private final IJakeGuiAccess jakeGuiAccess;

	PeopleTableModel(IJakeGuiAccess jakeGuiAccess) {
		log.info("Initializing NoteTableModel.");
		this.jakeGuiAccess = jakeGuiAccess;
	}

	String[] colNames = new String[] { "Nickname", "UserID", "Status", "Comment" };
	boolean[] columnEditable = new boolean[] { true, false, false, true };

	enum PeopleColumns {
		Nickname, UserID, Status, Comment
	}

	public int getColumnCount() {
		return colNames.length;
	}

	public int getRowCount() {
		return members.size();
	}

	/**
	 * Updates the view to show Project members
	 */
	public void updateData() {
		log.info("Updating People data...");
		members = jakeGuiAccess.getProject().getMembers();
	}

	/**
	 * Observable class, fires when data is updated.
	 * 
	 * @author peter
	 * 
	 */
	public class PeopleUpdaterObservable extends Observable {
		public void dataUpdated() {
			setChanged();
			notifyObservers();
		}
	}

	private final PeopleUpdaterObservable peopleUpdater = new PeopleUpdaterObservable();

	public Object getValueAt(int rowIndex, int columnIndex) {
		ProjectMember member = members.get(rowIndex);

		PeopleColumns col = PeopleColumns.values()[columnIndex];
		switch (col) {
		case Nickname:
			return member.getNickname();

		case UserID:
			return member.getUserId();

		case Status:
			return "status";

		case Comment:
			return member.getNotes();
			//jakeGuiAccess.getLastModifier(note).getNickname();

		default:
			throw new IllegalArgumentException(
					"Cannot get Information for column " + columnIndex);
		}
	}
/*
	@Override
	public void setValueAt(Object columnValue, int rowIndex, int columnIndex) {
		if (columnIndex == PeopleColumns.Tags.ordinal()) {
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

	public PeopleUpdaterObservable getNotesUpdater() {
		return peopleUpdater;
	}

	public List<ProjectMember> getMembers() {
		return members;
	}
}
