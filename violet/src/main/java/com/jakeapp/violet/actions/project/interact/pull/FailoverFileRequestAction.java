package com.jakeapp.violet.actions.project.interact.pull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.availablelater.AvailableLaterWaiter;
import com.jakeapp.jake.ics.UserId;
import com.jakeapp.violet.actions.project.interact.UserOrderStrategy;
import com.jakeapp.violet.model.JakeObject;
import com.jakeapp.violet.model.LogEntry;
import com.jakeapp.violet.model.ProjectModel;
import com.jakeapp.violet.model.User;

/**
 * Requests the file from one user at a time, until success
 */
class FailoverFileRequestAction extends AvailableLaterObject<File> {

	private static final Logger log = Logger
			.getLogger(FailoverFileRequestAction.class);

	private ProjectModel model;

	private JakeObject jakeObject;

	private UserOrderStrategy strategy;

	private boolean storeInFss;

	public FailoverFileRequestAction(ProjectModel model, JakeObject jakeObject,
			UserOrderStrategy strategy, boolean storeInFss) {
		this.model = model;
		this.jakeObject = jakeObject;
		this.strategy = strategy;
		this.storeInFss = storeInFss;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public File calculate() throws Exception {
		Exception lastException = null;
		Iterable<UserId> userIds = model.getIcs().getUsersService()
				.getAllUsers();
		List<User> users = new ArrayList<User>();
		for (UserId e : userIds) {
			users.add(new User(e.getUserId()));
		}
		LogEntry le = model.getLog().getLastOfJakeObject(jakeObject, true);
		User user = null;
		if (le != null) {
			user = le.getWho();
		}
		Collection<User> selected = strategy.selectUsers(user, users);

		if (selected.isEmpty())
			throw new Exception("no users available");

		for (User u : selected) {
			log.debug("requesting " + le);
			FileRequestAction frf = new FileRequestAction(model, u, le,
					storeInFss);
			try {
				return AvailableLaterWaiter.await(frf);
			} catch (Exception e) {
				log.debug("transfer failed, trying next user");
				lastException = e;
			}
		}
		log.debug("transfer failed, no next user left");
		throw lastException;
	}
}
