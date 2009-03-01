package com.jakeapp.core.services.futures;

import com.jakeapp.core.domain.InvitationState;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.services.IProjectsManagingService;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class GetProjectsFuture extends AvailableLaterObject<List<Project>> {
	private IProjectsManagingService pms;
	private EnumSet<InvitationState> inv;

	public GetProjectsFuture(IProjectsManagingService pms,
					EnumSet<InvitationState> inv) {
		this.pms = pms;
		this.inv = inv;
	}

	@Override
	public List<Project> calculate() throws Exception {
		List<Project> ret = new ArrayList<Project>();
		for (InvitationState is : inv) {
			ret.addAll(pms.getProjectList(is));
		}
		return ret;
	}
}