package com.jakeapp.core.synchronization;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jakeapp.core.dao.exceptions.NoSuchLogEntryException;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProjectMember;
import com.jakeapp.core.synchronization.exceptions.NoSuchObjectException;
import com.jakeapp.core.synchronization.exceptions.NotAProjectMemberException;
import com.jakeapp.core.synchronization.exceptions.ObjectNotConfiguredException;
import com.jakeapp.core.synchronization.exceptions.SyncException;
import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.exceptions.OtherUserOfflineException;
import com.jakeapp.jake.ics.exceptions.TimeoutException;
import com.jakeapp.jake.ics.msgservice.IMsgService;


public class SyncService implements ISyncService {

	static Map<Project, ISyncService> instances = new HashMap<Project, ISyncService>();

	static ISyncService getInstance(IMsgService ics, IFSService fss, Project project,
			RequestHandlePolicy rhp) {
		if(!instances.containsKey(project))
			instances.put(project, new SyncService(fss, ics, project, rhp));
		return instances.get(project);
	}

	private SyncService(IFSService fss, IMsgService ics, Project project,
			RequestHandlePolicy rhp) {
		super();
		this.fss = fss;
		this.ics = ics;
		this.project = project;
		this.rhp = rhp;
		// TODO
	}

	private IMsgService ics;

	private IFSService fss;

	private Project project;

	private RequestHandlePolicy rhp;

	@Override
	public List<ProjectMember> announce(JakeObject jo, String userid,
			String commitmsg) throws ObjectNotConfiguredException,
			SyncException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream pull(JakeObject jo) throws NetworkException,
			NotLoggedInException, TimeoutException, OtherUserOfflineException,
			ObjectNotConfiguredException, NoSuchObjectException,
			NoSuchLogEntryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<JakeObject> syncLogAndReturnChangedObjects(String userid)
			throws NetworkException, NotLoggedInException, TimeoutException,
			ObjectNotConfiguredException, OtherUserOfflineException,
			NotAProjectMemberException {
		// TODO Auto-generated method stub
		return null;
	}

}
