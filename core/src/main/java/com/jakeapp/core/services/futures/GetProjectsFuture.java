package com.jakeapp.core.services.futures;

import java.util.EnumSet;
import java.util.List;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.core.domain.InvitationState;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.services.IProjectsManagingService;
import com.jakeapp.core.services.MsgService;

/**
 * <code>AvailableLaterObject</code> returning a list of available <code>Project</code>s.
 */
public class GetProjectsFuture extends AvailableLaterObject<List<Project>> {
	private static final Logger log = Logger.getLogger(GetProjectsFuture.class);
	
	private IProjectsManagingService pms;
	private MsgService msg;

	public GetProjectsFuture(IProjectsManagingService pms, MsgService msg) {
		super();
		this.pms = pms;
		this.msg = msg;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Project> calculate() throws Exception {
		return pms.getProjectList(msg);
	}
}