package com.jakeapp.core.services;

/**
 * Created by IntelliJ IDEA.
 * User: domdorn
 * Date: Mar 5, 2009
 * Time: 2:39:03 AM
 * To change this template use File | Settings | File Templates.
 */
public interface IInvitationHandler {
	void registerInvitationListener(IProjectInvitationListener il);

	void unregisterInvitationListener(IProjectInvitationListener il);
}
