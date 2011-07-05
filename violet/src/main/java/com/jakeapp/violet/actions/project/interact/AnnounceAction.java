package com.jakeapp.violet.actions.project.interact;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.jake.fss.HashValue;
import com.jakeapp.jake.ics.UserId;
import com.jakeapp.violet.di.IUserIdFactory;
import com.jakeapp.violet.model.JakeObject;
import com.jakeapp.violet.model.LogEntry;
import com.jakeapp.violet.model.ProjectModel;
import com.jakeapp.violet.protocol.msg.IMessageMarshaller;
import com.jakeapp.violet.protocol.msg.PokeMessage;

/**
 * Announces a <code>List</code> of <code>JakeObject</code>s.
 */
public class AnnounceAction extends AvailableLaterObject<Void> {

	private static final Logger log = Logger.getLogger(AnnounceAction.class);

	private String why;

	private JakeObject what;

	private ProjectModel model;

	private boolean delete;

	@Inject
	private IMessageMarshaller messageMarshaller;

	@Inject
	private IUserIdFactory userids;

	public AnnounceAction(ProjectModel model, JakeObject what, String why,
			boolean delete) {
		this.why = why;
		this.what = what;
		this.model = model;
		this.delete = delete;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Void calculate() throws Exception {
		HashValue hash = null;
		if (!delete) {
			hash = model.getFss().calculateHashOverFile(what.getRelPath());
		}
		LogEntry le = new LogEntry(null, null, model.getUser(), what, why,
				hash.toString(), true);
		this.model.getLog().add(le);
		for (UserId u : this.model.getIcs().getUsersService().getUsers()) {
			notifyUser(le, u);
		}
		return null;
	}

	private void notifyUser(LogEntry le, UserId u) {
		PokeMessage msg = PokeMessage.createPokeMessage(model.getProjectid(),
				userids.get(model.getUserid()), le);
		try {
			model.getIcs().getMsgService()
					.sendMessage(u, messageMarshaller.serialize(msg));
		} catch (Exception e) {
			// notifying is best-effort, not critical
			log.debug("notifying " + u + " failed.");
		}
	}
}