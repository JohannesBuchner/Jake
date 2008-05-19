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

public class MockSyncService implements ISyncService {

	public byte[] pull(JakeObject jo) throws NetworkException,
			NotLoggedInException, TimeoutException, OtherUserOfflineException,
			ObjectNotConfiguredException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ProjectMember> push(JakeObject jo)
			throws ObjectNotConfiguredException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setICService(IICService ics) {
		// TODO Auto-generated method stub

	}

	public void setLogEntries(List<LogEntry> le) {
		// TODO Auto-generated method stub

	}

	public void setProjectMembers(List<ProjectMember> pm) {
		// TODO Auto-generated method stub

	}

	public List<JakeObject> syncLogAndGetChanges(String userid)
			throws NetworkException, NotLoggedInException, TimeoutException,
			ObjectNotConfiguredException {
		// TODO Auto-generated method stub
		return null;
	}

}
