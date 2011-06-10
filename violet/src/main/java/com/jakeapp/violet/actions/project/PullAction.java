package com.jakeapp.violet.actions.project;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.availablelater.AvailableLaterWaiter;
import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;
import com.jakeapp.violet.di.DI;
import com.jakeapp.violet.model.JakeObject;
import com.jakeapp.violet.model.LogEntry;
import com.jakeapp.violet.model.ProjectModel;
import com.jakeapp.violet.model.User;
import com.jakeapp.violet.protocol.RequestFileMessage;
import com.jakeapp.violet.synchronization.pull.ChangeListener;
import com.jakeapp.violet.synchronization.pull.FileRequestFuture;
import com.jakeapp.violet.synchronization.request.MessageMarshaller;

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
	private MessageMarshaller messageMarshaller;
	private ChangeListener listener;
	private Exception ex;

	public PullAction(ProjectModel model, JakeObject jakeObject,
			ChangeListener listener, UserOrderStrategy strategy) {
		this.model = model;
		this.jakeObject = jakeObject;
		this.strategy = strategy;
		this.listener = listener;
		this.messageMarshaller = DI.getImpl(MessageMarshaller.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Void calculate() throws Exception {
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

		for (User u : selected) {
			RequestFileMessage msg = new RequestFileMessage();
			msg.setLogEntry(le);
			msg.setProjectId(model.getProjectid());
			msg.setUser(DI.getUserId(u.getUserId()));
			String contentname = this.messageMarshaller.serialize(msg);
			log.debug("content addressed with: " + contentname);
			FileRequest fr = new FileRequest(contentname, false, msg.getUser());

			// this also reports to the corresponding ChangeListener and
			// watches the FileTransfer and returns after the
			// FileTransfer has
			// either returned successfully or not successfully
			log.debug("requesting " + fr);
			FileRequestFuture frf = new FileRequestFuture(model, jakeObject,
					fr, this.listener);
			try {
				AvailableLaterWaiter.await(frf);
				lastException = null;
				break;
			} catch (Exception e) {
				lastException = e;
			}
		}
		if (lastException != null)
			throw lastException;
		return innercontent;
	}
}
