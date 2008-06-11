package com.doublesignal.sepm.jake.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

import com.doublesignal.sepm.jake.core.domain.ProjectMember;
import com.doublesignal.sepm.jake.core.services.IJakeGuiAccess;
import com.doublesignal.sepm.jake.gui.i18n.ITranslationProvider;
import com.doublesignal.sepm.jake.gui.i18n.TranslatorFactory;
import com.doublesignal.sepm.jake.ics.exceptions.NoSuchUseridException;
import com.doublesignal.sepm.jake.ics.exceptions.NotLoggedInException;


@SuppressWarnings("serial")
/**
 * @author philipp
 */
public class PeopleTableModel extends AbstractTableModel {
	private static final Logger log = Logger.getLogger(PeopleTableModel.class);
	
	private static final ITranslationProvider translator = TranslatorFactory.getTranslator();
	
	private List<ProjectMember> members = new ArrayList<ProjectMember>();
	private final IJakeGuiAccess jakeGuiAccess;
	
	PeopleTableModel(IJakeGuiAccess jakeGuiAccess) {
		log.info("Initializing PeopleTableModel.");
		this.jakeGuiAccess = jakeGuiAccess;
		updateData();
	}

	String[] colNames = new String[] { "Nickname", "UserID", "Status", "Comment" };
	boolean[] columnEditable = new boolean[] { true, true, false, true };

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
		return this.members.size();
		
	}

	public ProjectMember getProjectMemberAt(int rowId) {
		return members.get(rowId);
	}
	
	/**
	 * Returns the status of members
	 */
	public int getOnlineMembersCount()	{
		
		int onlineMembers=0;
		
		for(ProjectMember p:this.members)
		{
			try {
				if(jakeGuiAccess.isLoggedIn(p.getUserId()))
					onlineMembers++;
			} catch (NotLoggedInException e) {
				return 0;
			} catch (NoSuchUseridException e) {
				
			}
		}
		
	
		return onlineMembers;
	}
	
	/**
	 * Updates the view to show Project members
	 */
	public void updateData() {
		log.info("Updating People data...");
		this.members = jakeGuiAccess.getMembers();
		peopleUpdater.dataUpdated();

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
			try {
				if(jakeGuiAccess.isLoggedIn(member.getUserId()))
					return translator.get("Online");
				else
					return translator.get("Offline");
			} catch (NotLoggedInException e) {
				return translator.get("You are offline");
			} catch (NoSuchUseridException e) {
				return translator.get("No such userid");
			}
		
		case Comment:
			return member.getNotes();
			
			

		default:
			throw new IllegalArgumentException(
					"Cannot get Information for column " + columnIndex);
		}
	}

	@Override
	public void setValueAt(Object columnValue, int rowIndex, int columnIndex) {
		if (columnIndex == PeopleColumns.Comment.ordinal()) {
			
			ProjectMember foundProjectMember = members.get(rowIndex);
			log.debug("handling a note-change event");
			if (foundProjectMember != null) {
				log.debug((String) columnValue);
				String note = (String) columnValue;
				jakeGuiAccess.editProjectMemberNote(foundProjectMember , note);
		        updateData();
				//foundProjectMember.setNotes(note);
			}
		}
		
		
		if (columnIndex == PeopleColumns.Nickname.ordinal()) {
			
			ProjectMember foundProjectMember = members.get(rowIndex);
			log.debug("handling a Nickname-change event");
			if (foundProjectMember != null) {
				log.debug((String) columnValue);
				String nickname = (String) columnValue;
				jakeGuiAccess.editProjectMemberNickName(foundProjectMember , nickname);
				updateData();
				
			}
		}
		
		if (columnIndex == PeopleColumns.UserID.ordinal())	{
			
			ProjectMember foundProjectMember = members.get(rowIndex);
			log.debug("handling a UserId-change event");
			if (foundProjectMember != null) {
				log.debug((String) columnValue);
				log.info("***********");
				log.info(foundProjectMember.getUserId());
				log.info((String) columnValue);
				log.info("***********");
				String userId = (String) columnValue;
				jakeGuiAccess.editProjectMemberUserId(foundProjectMember , userId);
				updateData();
				log.info("user id updated");
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
