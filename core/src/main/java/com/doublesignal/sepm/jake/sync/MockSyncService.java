package com.doublesignal.sepm.jake.sync;

import java.io.FileNotFoundException;
import java.rmi.NoSuchObjectException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.doublesignal.sepm.jake.core.InvalidApplicationState;
import com.doublesignal.sepm.jake.core.dao.IJakeDatabase;
import com.doublesignal.sepm.jake.core.dao.exceptions.NoSuchLogEntryException;
import com.doublesignal.sepm.jake.core.dao.exceptions.NoSuchProjectMemberException;
import com.doublesignal.sepm.jake.core.domain.FileObject;
import org.apache.log4j.Logger;

import com.doublesignal.sepm.jake.core.domain.JakeObject;
import com.doublesignal.sepm.jake.core.domain.LogAction;
import com.doublesignal.sepm.jake.core.domain.LogEntry;
import com.doublesignal.sepm.jake.core.domain.NoteObject;
import com.doublesignal.sepm.jake.core.domain.ProjectMember;
import com.doublesignal.sepm.jake.core.services.exceptions.NoSuchFileException;
import com.doublesignal.sepm.jake.fss.IFSService;
import com.doublesignal.sepm.jake.fss.InvalidFilenameException;
import com.doublesignal.sepm.jake.fss.NotAReadableFileException;
import com.doublesignal.sepm.jake.ics.IICService;
import com.doublesignal.sepm.jake.ics.exceptions.NetworkException;
import com.doublesignal.sepm.jake.ics.exceptions.NotLoggedInException;
import com.doublesignal.sepm.jake.ics.exceptions.OtherUserOfflineException;
import com.doublesignal.sepm.jake.ics.exceptions.TimeoutException;
import com.doublesignal.sepm.jake.sync.exceptions.NotAProjectMemberException;
import com.doublesignal.sepm.jake.sync.exceptions.ObjectNotConfiguredException;
import com.doublesignal.sepm.jake.sync.exceptions.SyncException;

/**
 * Static Mock implementation of SyncService
 * @author johannes
 * @see ISyncService
 */
public class MockSyncService implements ISyncService {
	
	private IFSService fss = null;
	private IICService ics = null;
	private IJakeDatabase db = null;
	
	public void setICService(IICService ics) {
		this.ics = ics;
	}

	public void setFSService(IFSService fss) {
		this.fss = fss;
	}

	public void setDatabase(IJakeDatabase db) {
		this.db = db;
	}

	public boolean isConfigured() {
		return (db != null) && (ics != null) && (fss != null);
	}

	/**
	 * Mocks a pull operation.
	 * Returns the local content if the last logentry was made by us, or 
	 * - this is the mock part - the name of the Jake object as content, with 
	 * its timestamp from the log
	 */
	public byte[] pull(JakeObject jo) throws NetworkException,
			NotLoggedInException, TimeoutException, OtherUserOfflineException,
			ObjectNotConfiguredException, NoSuchObjectException, NoSuchLogEntryException {

		if (!isConfigured())
			throw new ObjectNotConfiguredException();
		
		LogEntry le = db.getLogEntryDao().getMostRecentFor(jo);
		
		String userid = ics.getUserid();
		if(le.getUserId().equals(userid)){ 
			/* retrieve from self, don't know if thats gonna happen */
			System.err.println("Self-pull");
			if(jo.getName().startsWith("note:")){
				NoteObject no;
				try {
					no = db.getJakeObjectDao().getNoteObjectByName(jo.getName());
					return no.getContent().getBytes();
				} catch (NoSuchFileException e) {
					throw new NoSuchObjectException(e.getMessage());
				}
			}else{
				try {
					return fss.readFile(jo.getName());
				} catch (Exception e) {
					throw new OtherUserOfflineException();
				}
			}
		}else{
			if(!ics.isLoggedIn(userid))
				throw new OtherUserOfflineException();
			/* also has to be the same as in simulateCreateNewVersion */
			return ("This is " + jo.getName() + " from "
					+ le.getTimestamp() + "\n\n"
					+ le.getComment() + "\n").getBytes();
		}
	}

	/**
	 * The push is real (writing the logentry), but no projectmembers could 
	 * be reached (returns empty list)
	 */
	public List<ProjectMember> push(JakeObject jo, String userid,
			String commitmsg) throws ObjectNotConfiguredException,
			SyncException {
		if (!isConfigured())
			throw new ObjectNotConfiguredException();
		
		String hash;
		if (jo.getName().startsWith("note:")) {
			NoteObject note = (NoteObject) jo;
			hash = fss.calculateHash(note.getContent().getBytes());
			db.getJakeObjectDao().save(note);
		} else {
			FileObject file = (FileObject) jo;
			System.out.println("File: " + file.getName());
			db.getJakeObjectDao().save(file);
			try {
				hash = fss.calculateHashOverFile(jo.getName());
			} catch (FileNotFoundException e) {
				throw new SyncException(e);
			} catch (InvalidFilenameException e) {
				throw new SyncException(e);
			} catch (NotAReadableFileException e) {
				throw new SyncException(e);
			}
		}
		
		LogEntry le = new LogEntry(LogAction.NEW_VERSION, new Date(), 
				jo.getName(), hash, userid, commitmsg);
		
		db.getLogEntryDao().create(le);
		
		return new ArrayList<ProjectMember>();
	}

	/**
	 * Mocks a Sync operation.
	 * 
	 * <p>On every sync, a new version of the object o is created by the user
	 * u, if the userid of u contains a 'n'.</p>
	 * o = "Projektauftrag/Lastenheft.txt" 
	 *  
	 */
	public List<JakeObject> syncLogAndGetChanges(String userid)
			throws NetworkException, NotLoggedInException, TimeoutException,
			ObjectNotConfiguredException, OtherUserOfflineException,
			NotAProjectMemberException {
		
		if (!isConfigured())
			throw new ObjectNotConfiguredException();
		if (!ics.isLoggedIn())
			throw new NotLoggedInException();
		
		try {
			db.getProjectMemberDao().getByUserId(userid);
		} catch (NoSuchProjectMemberException e) {
			throw new NotAProjectMemberException();
		}
		
		if (!ics.isLoggedIn(userid))
			throw new OtherUserOfflineException();
		
		LogEntry le = simulateCreateNewVersion(userid);
		List<JakeObject> jolist = new ArrayList<JakeObject>();
		
		if(le == null)
			return jolist;

		if(le.getJakeObjectName().startsWith("note:")){
			InvalidApplicationState.die("We received sync logentries containing " +
					"a note, which is not implemented.");
		}else{
			FileObject jo = new FileObject(le.getJakeObjectName());
			try{
				db.getJakeObjectDao().getFileObjectByName(le.getJakeObjectName());
				System.err.println("JakeObject found.");
			}catch (NoSuchFileException e){
				System.err.println("FileObject created.");
				db.getJakeObjectDao().save(jo);
			}
			jolist.add(jo);
		}
		db.getLogEntryDao().create(le);
		return jolist;
	}
	
	private LogEntry simulateCreateNewVersion(String userid) {
		if (!userid.contains("n"))
			return null;

		/* simulate creating updated version of JakeObject */
		JakeObject jo = new JakeObject("Projektauftrag/Lastenheft.txt");

		String comment = "automated change (MockSync)";
		/* also has to be the same as in pull */
		String content = "This is " + jo.getName() + " from " + new Date()
				+ "\n\n" + comment + "\n";
		
		String hash = fss.calculateHash(content.getBytes());
		
		LogEntry le = new LogEntry(LogAction.NEW_VERSION, new Date(), jo
				.getName(), hash, userid, comment);

		return le;
	}
}
