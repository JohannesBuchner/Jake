package com.jakeapp.core.services.futures;

import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.synchronization.ISyncService;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.jake.fss.exceptions.InvalidFilenameException;
import com.jakeapp.jake.fss.exceptions.NotAReadableFileException;

import java.io.FileNotFoundException;
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
		double progress,step;
		String status;
		progress = 0d;
		step = 1.0d / jos.size();
		
		for (JakeObject jo : jos) {
			try {
				iss.announce(jo, action, commitMsg);
			} catch (FileNotFoundException e) {
				//skip this file
			} catch (NotAReadableFileException e) {
				//skip this file
			}
			finally {
				progress += step;
				try {
					status = jo.toString();
				}
				catch (Exception ex)  {
					status = "";
				}
				this.getListener().statusUpdate(progress, status);
			}
		}
		
		return null; //success
	}
}
