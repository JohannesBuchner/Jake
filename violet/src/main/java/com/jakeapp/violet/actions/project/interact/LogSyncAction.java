package com.jakeapp.violet.actions.project.interact;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.filetransfer.negotiate.INegotiationSuccessListener;
import com.jakeapp.violet.actions.global.serve.BlockingFileTransfer;
import com.jakeapp.violet.di.DI;
import com.jakeapp.violet.model.LogEntry;
import com.jakeapp.violet.model.ProjectModel;
import com.jakeapp.violet.model.User;
import com.jakeapp.violet.model.exceptions.NoSuchLogEntryException;
import com.jakeapp.violet.protocol.files.IRequestMarshaller;
import com.jakeapp.violet.protocol.files.RequestFileMessage;
import com.jakeapp.violet.protocol.msg.ILogEntryMarshaller;
import com.jakeapp.violet.protocol.msg.IMessageMarshaller;
import com.jakeapp.violet.protocol.msg.PokeMessage;
import com.jakeapp.violet.protocol.msg.impl.MessageMarshaller;

/**
 * <code>AvailableLaterObject</code> deleting some <code>FileObject</code>
 */
public class LogSyncAction extends AvailableLaterObject<Boolean> {

	private static final Logger log = Logger.getLogger(LogSyncAction.class);

	private ProjectModel model;

	private User user;

	private IRequestMarshaller requestMarshaller = DI
			.getImpl(IRequestMarshaller.class);

	private ILogEntryMarshaller logEntryMarshaller = DI
			.getImpl(ILogEntryMarshaller.class);

	private INegotiationSuccessListener listener;

	public LogSyncAction(ProjectModel model, User user,
			INegotiationSuccessListener listener) {
		this.model = model;
		this.user = user;
		this.listener = listener;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean calculate() throws Exception {
		UserId peer = DI.getUserId(user.getUserId());
		RequestFileMessage msg = RequestFileMessage.createRequestLogsMessage(
				model.getProjectid(), peer);
		requestMarshaller.serialize(msg);
		InputStream is = BlockingFileTransfer.requestFile(model,
				requestMarshaller, msg, listener);
		if (is == null) {
			return false;
		}
		mergeLogEntries(logEntryMarshaller.unpackLogEntries(
				model.getProjectid(), is));

		return true;
	}

	private void mergeLogEntries(List<LogEntry> logEntries) {
		// use resulting file
		List<UUID> uuids = new ArrayList<UUID>();
		log.info("received " + logEntries.size() + " log entries from " + user);
		int count = 0;
		for (LogEntry le : logEntries) {
			uuids.add(le.getId());
			try {
				model.getLog().getById(le.getId(), true);
			} catch (NoSuchLogEntryException e) {
				count++;
				model.getLog().add(le);
			}
		}
		log.info("stored " + count + " log entries from " + user);
		// lets notify the peer for logentries it doesn't have --
		// shouldn't be too many
		count = 0;
		for (LogEntry le : model.getLog().getAll(false)) {
			if (!uuids.contains(le.getId())) {
				count++;
				try {
					IMessageMarshaller messageMarshaller = DI
							.getImpl(MessageMarshaller.class);
					PokeMessage msg = PokeMessage.createPokeMessage(
							model.getProjectid(),
							DI.getUserId(user.getUserId()), null);

					String message = messageMarshaller.serialize(msg);
					log.debug("Sending message: \"" + message + "\"");
					model.getIcs().getMsgService()
							.sendMessage(msg.getUser(), message);
				} catch (Exception e) {
					// best-effort, fail-fast -- we don't want a pile of
					// useless messages
					log.debug("pushing failed. giving up.");
					break;
				}
			}
		}
		log.info("pushed " + count + " updates in return to " + user);
	}

}
