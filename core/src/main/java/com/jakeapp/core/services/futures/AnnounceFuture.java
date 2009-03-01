package com.jakeapp.core.services.futures;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.synchronization.ISyncService;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;

import java.util.List;

public class AnnounceFuture extends AvailableLaterObject<Void> {
	private ISyncService iss;
	private List<? extends JakeObject> jos;
	private LogAction action;
	private String commitMsg;

	public AnnounceFuture(ISyncService iss, List<? extends JakeObject> jos,
					String commitMsg) {
		this.iss = iss;
		this.jos = jos;
		this.commitMsg = commitMsg;
		this.action = LogAction.JAKE_OBJECT_NEW_VERSION;
	}

	@Override
	public Void calculate() throws Exception {
		for (JakeObject jo : jos) {
			this.iss.announce(jo, action, commitMsg);
		}
		return null;
	}
}
