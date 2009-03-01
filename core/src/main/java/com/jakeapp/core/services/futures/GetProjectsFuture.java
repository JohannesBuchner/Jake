package com.jakeapp.core.services.futures;

import com.jakeapp.core.domain.InvitationState;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.services.IProjectsManagingService;
import com.jakeapp.core.synchronization.SyncServiceImpl;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.apache.log4j.Logger;

public class GetProjectsFuture extends AvailableLaterObject<List<Project>> {
	private static final Logger log = Logger.getLogger(GetProjectsFuture.class);
	
	private IProjectsManagingService pms;
	private EnumSet<InvitationState> inv;

	public GetProjectsFuture(IProjectsManagingService pms,
					EnumSet<InvitationState> inv) {
		this.pms = pms;
		this.inv = inv;
	}

	@Override
	public List<Project> calculate() throws Exception {
		double progress,step;
		
		progress = 0d;
		step = 1.0d;
		if (this.inv.size()>0)
			step /= this.inv.size();
		
		List<Project> ret = new ArrayList<Project>();
		log.debug("Getting projects for n states,n=" + this.inv.size());
		for (InvitationState is : inv) {
			log.debug("before addall for "+is);
			ret.addAll(pms.getProjectList(is));
			log.debug("after addall for "+is);
			progress+=step;
			this.getListener().statusUpdate(progress, "");
		}
		return ret;
	}
}