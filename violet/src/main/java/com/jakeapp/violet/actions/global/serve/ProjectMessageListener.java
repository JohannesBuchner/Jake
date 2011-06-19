package com.jakeapp.violet.actions.global.serve;

import org.apache.log4j.Logger;

import com.jakeapp.jake.ics.msgservice.IMessageReceiveListener;
import com.jakeapp.violet.di.DI;
import com.jakeapp.violet.model.ProjectModel;
import com.jakeapp.violet.model.User;
import com.jakeapp.violet.protocol.Message;
import com.jakeapp.violet.protocol.msg.IMessageMarshaller;
import com.jakeapp.violet.protocol.msg.PokeMessage;
import com.jakeapp.violet.protocol.msg.impl.MessageMarshaller;

public class ProjectMessageListener implements IMessageReceiveListener {

	private static Logger log = Logger.getLogger(ProjectMessageListener.class);

	private IMessageMarshaller messageMarshaller = DI
			.getImpl(IMessageMarshaller.class);

	private ProjectModel model;

	private ISyncListener listener;

	public ProjectMessageListener(ProjectModel model, ISyncListener listener) {
		this.listener = listener;
		this.messageMarshaller = DI.getImpl(MessageMarshaller.class);
		this.model = model;
	}

	@Override
	public void receivedMessage(com.jakeapp.jake.ics.UserId from_userid,
			String content) {
		try {
			User user = new User(from_userid.getUserId());
			PokeMessage msg = messageMarshaller.decodePokeMessage(content,
					from_userid);
			log.info("Received a message for project " + msg.getProjectId());

			if (!isForThisProject(msg))
				return;
			if (msg.getLogEntry() == null) {
				// other user wants us to do a logsync
				listener.poke(user);
			} else {
				listener.startReceiving(user);
				model.getLog().add(msg.getLogEntry());
				listener.finishedReceiving(user);
			}
		} catch (Exception e) {
			log.error("handling message failed: " + content, e);
		}
	}

	private boolean isForThisProject(Message msg) {
		if (msg == null || msg.getProjectId() == null
				|| !msg.getProjectId().equals(model.getProjectid())) {
			log.debug("Discarding message because it's not for this project");
			return false;
		}
		return true;
	}
}