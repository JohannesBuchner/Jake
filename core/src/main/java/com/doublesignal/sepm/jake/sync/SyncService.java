package com.doublesignal.sepm.jake.sync;

import java.util.List;

import com.doublesignal.sepm.jake.core.domain.JakeObject;
import com.doublesignal.sepm.jake.core.domain.LogEntry;
import com.doublesignal.sepm.jake.core.domain.ProjectMember;
import com.doublesignal.sepm.jake.fss.IFSService;
import com.doublesignal.sepm.jake.fss.IModificationListener;
import com.doublesignal.sepm.jake.ics.IICService;
import com.doublesignal.sepm.jake.ics.IMessageReceiveListener;
import com.doublesignal.sepm.jake.ics.IObjectReceiveListener;
import com.doublesignal.sepm.jake.ics.exceptions.NetworkException;
import com.doublesignal.sepm.jake.ics.exceptions.NotLoggedInException;
import com.doublesignal.sepm.jake.ics.exceptions.OtherUserOfflineException;
import com.doublesignal.sepm.jake.ics.exceptions.TimeoutException;
import com.doublesignal.sepm.jake.sync.exceptions.ObjectNotConfiguredException;
import com.doublesignal.sepm.jake.sync.exceptions.SyncException;

public class SyncService implements ISyncService, IModificationListener,
		IMessageReceiveListener, IObjectReceiveListener {
	
	private IICService ics;
	private IFSService fss;
	private List<LogEntry> logEntrys;
	private List<ProjectMember> projectMembers;
	
	public static final String REQUEST_LOG = "sync_request_log";

	public boolean isConfigured() {
		return (ics != null && fss != null && logEntrys != null && projectMembers != null); 
	}

	public void logSyncWithUser(String userid) {
		// TODO Auto-generated method stub
	}

	public byte[] pull(JakeObject jo) throws NetworkException,
			NotLoggedInException, TimeoutException, OtherUserOfflineException,
			ObjectNotConfiguredException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ProjectMember> push(JakeObject jo, String userid,
			String commitmsg) throws ObjectNotConfiguredException,
			SyncException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Set the FSService to be used. After setting the fss this class is 
	 * registered as a modificaiton listener at the fss.
	 */
	public void setFSService(IFSService fss) {
		this.fss = fss;
		this.fss.registerModificationListener(this);
	}

	/**
	 * Set the ICService to be used. After setting the ics this class is registered
	 * as a receiveMessage and receiveObject listener.
	 */
	public void setICService(IICService ics) {
		this.ics = ics;
		this.ics.registerReceiveMessageListener(this);
		this.ics.registerReceiveObjectListener(this);
	}

	public void setLogEntries(List<LogEntry> le) {
		logEntrys = le;
	}

	public void setProjectMembers(List<ProjectMember> pm) {
		projectMembers = pm;
	}

	public List<JakeObject> syncLogAndGetChanges(String userid)
			throws NetworkException, NotLoggedInException, TimeoutException,
			ObjectNotConfiguredException, OtherUserOfflineException,
			NotAProjectMemberException {
		// TODO Auto-generated method stub
		return null;
	}

	public void fileModified(String relpath, ModifyActions action) {
		// TODO Auto-generated method stub

	}

	public void receivedMessage(String from_userid, String content) {
		// TODO Auto-generated method stub

	}

	public void receivedObject(String from_userid, String identifier,
			byte[] content) {
		// TODO Auto-generated method stub

	}

}
