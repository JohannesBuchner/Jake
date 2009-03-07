package com.jakeapp.core.services;

public interface IInvitationHandler {
	void registerInvitationListener(IProjectInvitationListener il);

	void unregisterInvitationListener(IProjectInvitationListener il);
}
