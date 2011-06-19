package com.jakeapp.violet.actions.project.local;

import java.util.Collection;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.availablelater.AvailableLaterWaiter;
import com.jakeapp.availablelater.StatusUpdate;
import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.violet.actions.project.interact.AnnounceAction;
import com.jakeapp.violet.di.DI;
import com.jakeapp.violet.di.KnownProperty;
import com.jakeapp.violet.model.JakeObject;
import com.jakeapp.violet.model.Log;
import com.jakeapp.violet.model.ProjectModel;

/**
 * <code>AvailableLaterObject</code> deleting some <code>FileObject</code>
 */
public class DeleteFilesAction extends AvailableLaterObject<Void> {

	private static final Logger log = Logger.getLogger(DeleteFilesAction.class);

	private Collection<JakeObject> toDelete;

	private ProjectModel model;

	private boolean trash;

	private int totalSteps;

	private int stepsDone;

	private String why;

	public DeleteFilesAction(ProjectModel model,
			Collection<JakeObject> toDelete, String why) {
		this.model = model;
		this.toDelete = toDelete;
		this.why = why;
		totalSteps = toDelete.size() + 1;
		stepsDone = 1;
		this.setStatus(new StatusUpdate(totalSteps * 1. / stepsDone, "init"));
		this.trash = true;
		if ("false".equals(DI.getProperty(KnownProperty.USE_TRASH)))
			this.trash = false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Void calculate() throws Exception {
		IFSService fss = this.model.getFss();

		for (JakeObject fo : toDelete) {
			AvailableLaterWaiter
					.await(new AnnounceAction(model, fo, why, true));

			if (trash) {
				fss.trashFile(fo.getRelPath());
			} else {
				fss.deleteFile(fo.getRelPath());
			}
			stepsDone++;
			this.setStatus(new StatusUpdate(totalSteps * 1. / stepsDone,
					"working"));
		}

		return null;
	}

}
