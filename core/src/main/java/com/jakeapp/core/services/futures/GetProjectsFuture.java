package com.jakeapp.core.services.futures;

import com.jakeapp.core.domain.InvitationState;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.services.IProjectsManagingService;
import com.jakeapp.core.services.MsgService;
import com.jakeapp.core.synchronization.SyncServiceImpl;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.apache.log4j.Logger;

public class GetProjectsFuture extends AvailableLaterObject<List<Project>> {
	private static final Logger log = Logger.getLogger(GetProjectsFuture.class);
	
	private IProjectsManagingService pms;
	private MsgService msg;
	private EnumSet<InvitationState> inv;

	public GetProjectsFuture(IProjectsManagingService pms, MsgService msg) {
		super();
		this.pms = pms;
		this.msg = msg;

	}

	@Override
	public List<Project> calculate() throws Exception {
		return pms.getProjectList(msg);
	}
}