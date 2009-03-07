package com.jakeapp.core.dao;

import com.jakeapp.core.domain.Invitation;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.exceptions.InvalidProjectException;

import java.util.List;


public class HibernateInvitationDao implements IInvitationDao {
	@Override
	public Invitation create(Invitation invitation) throws InvalidProjectException {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public List<Invitation> getAll() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Project accept(Invitation invitation) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void reject(Invitation invitation) {
		//To change body of implemented methods use File | Settings | File Templates.
	}
}
