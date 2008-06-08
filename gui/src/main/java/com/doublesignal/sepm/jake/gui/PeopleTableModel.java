package com.doublesignal.sepm.jake.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;
import com.doublesignal.sepm.jake.core.domain.NoteObject;
import com.doublesignal.sepm.jake.core.domain.ProjectMember;
import com.doublesignal.sepm.jake.core.services.IJakeGuiAccess;
import com.doublesignal.sepm.jake.core.dao.exceptions.NoSuchProjectMemberException;

@SuppressWarnings("serial")
/**
 * @author philipp
 */
public class PeopleTableModel extends AbstractTableModel {
	private static Logger log = Logger.getLogger(PeopleTableModel.class);
	private List<ProjectMember> members = new ArrayList<ProjectMember>();
	private final IJakeGuiAccess jakeGuiAccess;

	private int countActiveUser = 0;
	
	PeopleTableModel(IJakeGuiAccess jakeGuiAccess) {
		log.info("Initializing PeopleTableModel.");
		this.jakeGuiAccess = jakeGuiAccess;
		updateData();
		
		jakeGuiAccess.addProjectMember("tesuserph@domain.com");
		ProjectMember pm = new ProjectMember("test");
		jakeGuiAccess.getProject().addMember(pm);
		NoteObject o = new NoteObject("notename","notetext");
		
		jakeGuiAccess.getNotes().add(o);
		log.info("*********************************");
		int i;
		for (i=0;i<jakeGuiAccess.getProject().getMembers().size();i++)
		{log.info(jakeGuiAccess.getMembers().get(i).getUserId());}
		log.info("*********************************");
		
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

	public int getMembersCount()	{
		return members.size();
		
	}

	public ProjectMember getProjectMemberAt(int rowId) {
		return members.get(rowId);
	}
	
	/**
	 * Returns the status of members
	 */
	public int getOnlineMembersCount()	{
		
		for(ProjectMember p:getMembers())
		{
			if(p.getActive())
			countActiveUser++;
		}
		
		//return countActiveUser;
		return 0;
	}
	
	/**
	 * Updates the view to show Project members
	 */
	public void updateData() {
		log.info("Updating People data...");
		this.members = jakeGuiAccess.getMembers();

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
					if (jakeGuiAccess.isLoggedIn())
							return "Online";
					else return "Offline";

		case Comment:
			return member.getNotes();
			//jakeGuiAccess.getLastModifier(note).getNickname();
			

		default:
			throw new IllegalArgumentException(
					"Cannot get Information for column " + columnIndex);
		}
	}

	@Override
	public void setValueAt(Object columnValue, int rowIndex, int columnIndex) {
		if (columnIndex == PeopleColumns.Comment.ordinal()) {
			
			ProjectMember foundProjectMember = members.get(rowIndex);
			log.debug("handling a comment-change event");
			if (foundProjectMember != null) {
				log.debug((String) columnValue);
				String comment = (String) columnValue;
				//foundProjectMember.setNotes(comment);
				//super.setValueAt(comment, rowIndex, columnIndex);
//					try	{
//						jakeGuiAccess.setProjectMemberNote(foundProjectMember.getUserId(),comment);
//					}
//						catch (NoSuchProjectMemberException e1)	{
//							log.debug("No such Member found");
//						}
				foundProjectMember.setNotes(comment);
			}
		}
		
		
		if (columnIndex == PeopleColumns.Nickname.ordinal()) {
			
			ProjectMember foundProjectMember = members.get(rowIndex);
			log.debug("handling a Nickname-change event");
			if (foundProjectMember != null) {
				log.debug((String) columnValue);
				String nickname = (String) columnValue;
					
				foundProjectMember.setNickname(nickname);
			}
		}
		// possible other columns go here
	}

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
