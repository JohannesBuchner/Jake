package com.jakeapp.core.services;

import java.io.InputStream;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.synchronization.RequestHandlePolicy;


public class TrustRequestHandlePolicy implements RequestHandlePolicy {

	public TrustRequestHandlePolicy(Project project) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Iterable<UserId> getPotentialJakeObjectProviders(JakeObject jo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream handleJakeObjectRequest(UserId from, JakeObject jo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean handleLogSyncRequest(Project project, UserId from) {
		// TODO Auto-generated method stub
		return false;
	}

}
