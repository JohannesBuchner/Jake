package com.jakeapp.violet.actions.project.interact;

import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.violet.actions.project.interact.pull.DownloadAction;
import com.jakeapp.violet.actions.project.interact.pull.PullAction;
import com.jakeapp.violet.context.ProjectModel;
import com.jakeapp.violet.model.JakeObject;
import com.jakeapp.violet.model.User;


public interface InteractProjectActionsFactory {

	AnnounceAction announce(ProjectModel model, JakeObject what, String why,
			boolean delete);

	DownloadAction download(ProjectModel model, JakeObject jakeObject,
			UserOrderStrategy strategy);

	LogSyncAction logsync(ProjectModel model, User user,
			INegotiationSuccessListener listener);

	PokeAction poke(ProjectModel model, User user);

	PullAction pull(ProjectModel model, JakeObject jakeObject,
			UserOrderStrategy strategy);
}
