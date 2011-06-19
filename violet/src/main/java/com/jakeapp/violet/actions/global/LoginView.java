package com.jakeapp.violet.actions.global;

import com.jakeapp.jake.ics.status.ILoginStateListener;
import com.jakeapp.jake.ics.status.IOnlineStatusListener;
import com.jakeapp.violet.protocol.invites.IProjectInvitationListener;

public interface LoginView extends ILoginStateListener, IOnlineStatusListener,
		IProjectInvitationListener {

}
