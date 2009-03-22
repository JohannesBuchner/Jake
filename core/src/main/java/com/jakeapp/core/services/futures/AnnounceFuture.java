package com.jakeapp.core.services.futures;

import java.io.FileNotFoundException;
import java.util.List;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.LogAction;
import com.jakeapp.core.synchronization.ISyncService;
import com.jakeapp.jake.fss.exceptions.NotAReadableFileException;

/**
 * Announces a <code>List</code> of <code>JakeObject</code>s. 
 */
public class AnnounceFuture extends AvailableLaterObject<Void> {
	private ISyncService iss;
	private List<? extends JakeObject> jakeObjects;
	private LogAction action;
	private String commitMsg;
	
	private static final Logger log = Logger.getLogger(AnnounceFuture.class);

	public AnnounceFuture(ISyncService iss, List<? extends JakeObject> jakeObjects,
					String commitMsg) {
		this.iss = iss;
		this.jakeObjects = jakeObjects;
		this.commitMsg = commitMsg;
		this.action = LogAction.JAKE_OBJECT_NEW_VERSION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Void calculate() throws Exception {
		double progress,step;
		String status;
		progress = 0d;
		step = 1.0d / this.jakeObjects.size();
		
		for (JakeObject jo : this.jakeObjects) {
			try {
				this.iss.announce(jo, this.action, this.commitMsg);
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
