package com.jakeapp.violet.actions.project.connect;

import com.jakeapp.violet.context.ProjectModel;


public interface ConnectProjectActionsFactory {
	InviteUserAction inviteUsers(ProjectModel model);
	
	ListUsersViewAction listUsers(ProjectModel model);
	
	LoginAction login(ProjectModel model);
	
	LogoutAction logout(ProjectModel model);
	
	SuggestUsersToInviteAction suggestInvites(ProjectModel model);
	
	UserInfoAction userInfo(ProjectModel model);
}
