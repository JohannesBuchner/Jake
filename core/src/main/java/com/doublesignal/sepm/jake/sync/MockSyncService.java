package com.doublesignal.sepm.jake.sync;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.doublesignal.sepm.jake.core.domain.JakeObject;
import com.doublesignal.sepm.jake.core.domain.LogAction;
import com.doublesignal.sepm.jake.core.domain.LogEntry;
import com.doublesignal.sepm.jake.core.domain.NoteObject;
import com.doublesignal.sepm.jake.core.domain.ProjectMember;
import com.doublesignal.sepm.jake.fss.IFSService;
import com.doublesignal.sepm.jake.fss.InvalidFilenameException;
import com.doublesignal.sepm.jake.fss.NotAReadableFileException;
import com.doublesignal.sepm.jake.ics.IICService;
import com.doublesignal.sepm.jake.ics.exceptions.NetworkException;
import com.doublesignal.sepm.jake.ics.exceptions.NotLoggedInException;
import com.doublesignal.sepm.jake.ics.exceptions.OtherUserOfflineException;
import com.doublesignal.sepm.jake.ics.exceptions.TimeoutException;
import com.doublesignal.sepm.jake.sync.exceptions.ObjectNotConfiguredException;
import com.doublesignal.sepm.jake.sync.exceptions.SyncException;

/**
 * Static Mock implementation of SyncService
 * @author johannes
 * @see ISyncService
 */
public class MockSyncService implements ISyncService {
	
	protected IFSService fss = null;
	protected IICService ics = null;
	protected List<LogEntry> le = null;
	protected List<ProjectMember> pm = null;

	public void setICService(IICService ics) {
		this.ics = ics;
	}

	public void setFSService(IFSService fss) {
		this.fss = fss;
	}

	public void setLogEntries(List<LogEntry> le) {
		this.le = le;
	}

	public void setProjectMembers(List<ProjectMember> pm) {
		this.pm = pm;
	}
	
	public boolean isConfigured(){
		return (pm != null) && (le != null) && (ics != null) && (fss != null); 
	}
	
	/**
	 * @returns the name of the Jake object, with its timestamp from the log
	 */
	public byte[] pull(JakeObject jo) throws NetworkException,
			NotLoggedInException, TimeoutException, OtherUserOfflineException,
			ObjectNotConfiguredException {
		
		if(!isConfigured()) 
			throw new ObjectNotConfiguredException();
		
		/* TODO: we need a findLastLogentryForJakeObject(jo) */
		int i;
		for(i=le.size()-1;i>=0;i--){
			if(le.get(i).getJakeObjectName().equals(jo.getName()))
				break;
		}
		if(i<0)
			return null;
		
		/* TODO: we need a getProjectMemberForLogentry(le) 
		 * when we have that, local content can be pulled here 
		 * (integrity) 
		 * */
		
		return ("This is " + jo.getName() + " from "+ le.get(i).getTimestamp() + 
				"\n\n" + le.get(i).getComment() + "\n" 
				).getBytes();
		
	}

	public List<ProjectMember> push(JakeObject jo, String userid, String commitmsg)
			throws ObjectNotConfiguredException, SyncException {
		if(!isConfigured())
			throw new ObjectNotConfiguredException();
		
		String hash;
		if(jo.getName().startsWith("note:")){
			NoteObject note = (NoteObject) jo;
			hash = fss.calculateHash(note.getContent().getBytes());
		}else{
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
		
		/* TODO: When and how is that written to the database? */
		le.add(new LogEntry(LogAction.NEW_VERSION, new Date(), 
			jo.getName(), userid, hash, commitmsg));
		
		return new ArrayList<ProjectMember>();
	}
	
	/* on every sync, a new version of the object o is created by the user
	 * u. (u, o fixed) */
	public List<JakeObject> syncLogAndGetChanges(String userid)
			throws NetworkException, NotLoggedInException, TimeoutException,
			ObjectNotConfiguredException, OtherUserOfflineException, 
			NotAProjectMemberException
	{
		if(!isConfigured())
			throw new ObjectNotConfiguredException();
		if(!ics.isLoggedIn())
			throw new NotLoggedInException();
		
		int i;
		for(i=0;i<pm.size();i++){
			if(pm.get(i).getUserId().equals(userid))
				break;
		}
		if(i==pm.size())
			throw new NotAProjectMemberException();
		if(!ics.isLoggedIn(userid))
			throw new OtherUserOfflineException();
		
		LogEntry myle = simulateCreateNewVersion(userid);
		List<JakeObject> jolist = new ArrayList<JakeObject>();
		if(myle!=null){
			le.add(myle);
			jolist.add(new JakeObject(myle.getJakeObjectName()));
		}
		return jolist;
	}

	private LogEntry simulateCreateNewVersion(String userid) {
		if(!userid.contains("n"))
			return null;
		
		/* simulate creating updated version of JakeObject */
		JakeObject jo = new JakeObject("Projektauftrag/Lastenheft.txt");
		
		String comment = "automated change (MockSync)";
		String hash = "This is " + jo.getName() + " from "+ new Date() + 
			"\n\n" + comment + "\n"; 
		
		hash = fss.calculateHash(hash.getBytes());
		
		LogEntry le = new LogEntry(LogAction.NEW_VERSION, new Date(), 
				jo.getName(), hash, userid, comment);
		
		return le;
	}

}
