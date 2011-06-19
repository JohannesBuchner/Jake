package com.jakeapp.violet.actions.project.interact.pull;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.availablelater.AvailableLaterWaiter;
import com.jakeapp.violet.actions.project.interact.UserOrderStrategy;
import com.jakeapp.violet.model.JakeObject;
import com.jakeapp.violet.model.ProjectModel;

/**
 * <code>AvailableLaterObject</code> downloading (<code>Pull</code>ing) a
 * <code>List</code> of <code>JakeObject</code>s with the given
 * <code>ISyncService</code>
 */
public class PullAction extends AvailableLaterObject<Void> {

	private static final Logger log = Logger.getLogger(PullAction.class);

	private ProjectModel model;

	private JakeObject jakeObject;

	private UserOrderStrategy strategy;

	public PullAction(ProjectModel model, JakeObject jakeObject,
			UserOrderStrategy strategy) {
		this.model = model;
		this.jakeObject = jakeObject;
		this.strategy = strategy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Void calculate() throws Exception {
		AvailableLaterWaiter.await(new FailoverFileRequestAction(model,
				jakeObject, strategy, true));
		return null;
	}
}
