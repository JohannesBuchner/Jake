package com.jakeapp.violet.actions.project.interact;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.violet.di.DI;
import com.jakeapp.violet.model.ProjectModel;
import com.jakeapp.violet.model.User;
import com.jakeapp.violet.protocol.msg.IMessageMarshaller;
import com.jakeapp.violet.protocol.msg.PokeMessage;
import com.jakeapp.violet.protocol.msg.impl.MessageMarshaller;

/**
 * Notifies another user that we have changes she might want to pull.
 */
public class PokeAction extends AvailableLaterObject<Void> {

	private static final Logger log = Logger.getLogger(PokeAction.class);

	private ProjectModel model;

	private User user;

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
		IMessageMarshaller messageMarshaller = DI
				.getImpl(MessageMarshaller.class);
		PokeMessage msg = PokeMessage.createPokeMessage(model.getProjectid(),
				DI.getUserId(user.getUserId()), null);

		String message = messageMarshaller.serialize(msg);
		log.debug("Sending message: \"" + message + "\"");
		ics.getMsgService().sendMessage(msg.getUser(), message);

		return null;
	}
}