package com.doublesignal.sepm.jake.sync;

import java.util.List;

import com.doublesignal.sepm.jake.core.domain.JakeObject;
import com.doublesignal.sepm.jake.core.domain.LogEntry;
import com.doublesignal.sepm.jake.core.domain.ProjectMember;
import com.doublesignal.sepm.jake.ics.IICService;
import com.doublesignal.sepm.jake.ics.exceptions.NetworkException;
import com.doublesignal.sepm.jake.ics.exceptions.NotLoggedInException;
import com.doublesignal.sepm.jake.ics.exceptions.OtherUserOfflineException;
import com.doublesignal.sepm.jake.ics.exceptions.TimeoutException;
import com.doublesignal.sepm.jake.sync.exceptions.ObjectNotConfiguredException;

/**
 * Static Mock implementation of SyncService
 * @author johannes
 * @see ISyncService
 */
public class MockSyncService implements ISyncService {
	
	protected IICService ics = null;
	protected List<LogEntry> le = null;
	protected List<ProjectMember> pm = null;

	public void setICService(IICService ics) {
		this.ics = ics;
	}

	public void setLogEntries(List<LogEntry> le) {
		this.le = le;
	}

	public void setProjectMembers(List<ProjectMember> pm) {
		this.pm = pm;
	}
	private boolean isConfigured(){
		return pm != null && le != null && ics != null; 
	}
	/**
	 * @returns the name of the Jake object
	 */
	public byte[] pull(JakeObject jo) throws NetworkException,
			NotLoggedInException, TimeoutException, OtherUserOfflineException,
			ObjectNotConfiguredException {
		if(!isConfigured()) 
			throw new ObjectNotConfiguredException();
		return jo.getName().getBytes();
	}

	public List<ProjectMember> push(JakeObject jo)
			throws ObjectNotConfiguredException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<JakeObject> syncLogAndGetChanges(String userid)
			throws NetworkException, NotLoggedInException, TimeoutException,
			ObjectNotConfiguredException {
		// TODO Auto-generated method stub
		return null;
	}

}
