package com.jakeapp.violet.actions.project.interact;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.violet.context.ProjectModel;
import com.jakeapp.violet.di.IUserIdFactory;
import com.jakeapp.violet.model.User;
import com.jakeapp.violet.protocol.msg.IMessageMarshaller;
import com.jakeapp.violet.protocol.msg.PokeMessage;

/**
 * Notifies another user that we have changes she might want to pull.
 */
public class PokeAction extends AvailableLaterObject<Void> {

	private static final Logger log = Logger.getLogger(PokeAction.class);

	private ProjectModel model;

	private User user;

	@Inject
	private IMessageMarshaller messageMarshaller;

	@Inject
	private IUserIdFactory userids;

	public PokeAction(ProjectModel model, User user) {
		this.model = model;
		this.user = user;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Void calculate() throws Exception {
		ICService ics = this.model.getIcs();
		PokeMessage msg = PokeMessage.createPokeMessage(model.getProjectid(),
				userids.get(user.getUserId()), null);

		String message = messageMarshaller.serialize(msg);
		log.debug("Sending message: \"" + message + "\"");
		ics.getMsgService().sendMessage(msg.getUser(), message);

		return null;
	}
}