package com.jakeapp.violet.synchronization.request;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.jakeapp.jake.fss.FSService;
import com.jakeapp.jake.ics.filetransfer.AdditionalFileTransferData;
import com.jakeapp.jake.ics.filetransfer.FileRequestFileMapper;
import com.jakeapp.jake.ics.filetransfer.ITransferListener;
import com.jakeapp.jake.ics.filetransfer.IncomingTransferListener;
import com.jakeapp.jake.ics.filetransfer.TransferWatcherThread;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.IFileTransfer;
import com.jakeapp.jake.ics.filetransfer.runningtransfer.Status;
import com.jakeapp.jake.ics.msgservice.IMessageReceiveListener;
import com.jakeapp.violet.actions.project.AttributedCalculator;
import com.jakeapp.violet.di.DI;
import com.jakeapp.violet.model.JakeObject;
import com.jakeapp.violet.model.LogEntry;
import com.jakeapp.violet.model.ProjectModel;
import com.jakeapp.violet.model.User;
import com.jakeapp.violet.model.attributes.Attributed;
import com.jakeapp.violet.model.exceptions.NoSuchLogEntryException;

public class ProjectRequestListener implements IMessageReceiveListener,
		IncomingTransferListener, FileRequestFileMapper {

	private static Logger log = Logger.getLogger(ProjectRequestListener.class);

	private static final String BEGIN_LOGENTRY = "<le>";

	private static final String END_LOGENTRY = "</le>";

	private static final String BEGIN_PROJECT_UUID = "<project>";

	private static final String END_PROJECT_UUID = "</project>";

	private static final String LOGENTRIES_MESSAGE = "<logentries/>";

	private static final String REQUEST_LOGS_MESSAGE = "<requestlogs/>";

	private static final String POKE_MESSAGE = "<poke/>"; // dup

	private MessageMarshaller messageMarshaller;

	private ProjectModel model;

	private ISyncListener listener;

	public ProjectRequestListener(ProjectModel model, ISyncListener listener) {
		this.listener = listener;
		this.messageMarshaller = DI.getImpl(MessageMarshaller.class);
		this.model = model;
	}

	private String extractProjectUUID(String content) {
		int begin = content.indexOf(BEGIN_PROJECT_UUID)
				+ BEGIN_PROJECT_UUID.length();
		int end = content.indexOf(END_PROJECT_UUID);
		if (end == -1 || begin == -1)
			return null;
		return content.substring(begin, end);
	}

	@Override
	public void receivedMessage(com.jakeapp.jake.ics.UserId from_userid,
			String content) {

		try {
			User user = new User(from_userid.getUserId());
			String projectUUID = extractProjectUUID(content);
			if (content == null)
				return;

			log.info("Received a message for project " + projectUUID);

			if (projectUUID == null
					|| !projectUUID.equals(model.getProjectid())) {
				log.debug("Discarding message because it's not for this project");
				return;
			}

			log.debug("Message is for this project!");

			String message = content.substring(BEGIN_PROJECT_UUID.length()
					+ projectUUID.length() + END_PROJECT_UUID.length());
			log.debug("Message content: \"" + message + "\"");

			if (message.startsWith(POKE_MESSAGE)) {
				log.info("Received poke from " + from_userid.getUserId());
				log.debug("This means we should sync logs!");

				listener.poke(user);
			} else if (message.startsWith(REQUEST_LOGS_MESSAGE)) {
				log.info("Received logs request from "
						+ from_userid.getUserId());
				List<LogEntry> logs = model.getLog().getAll(true);
				String logsstr = messageMarshaller.packLogEntries(
						model.getProjectid(), logs);
				model.getIcs().getMsgService()
						.sendMessage(from_userid, logsstr);
			} else if (message.startsWith(LOGENTRIES_MESSAGE)) {
				listener.startReceiving(user);
				this.handleReceivedLogEntries(from_userid, message);
				listener.finishedReceiving(user);
			} else
				log.warn("We got a unknown/unhandled Message: " + message);
		} catch (Exception e) {
			log.error("handling message failed: " + content, e);
		}
	}

	/**
	 * @param from_userid
	 * @param message
	 * @throws Exception
	 */
	private void handleReceivedLogEntries(
			com.jakeapp.jake.ics.UserId from_userid, String message)
			throws Exception {
		log.info("Received serialized logentries from "
				+ from_userid.getUserId());

		String les = message.substring(LOGENTRIES_MESSAGE.length()
				+ BEGIN_LOGENTRY.length(),
				message.length() - END_LOGENTRY.length());

		List<LogEntry> logEntries = this.messageMarshaller
				.unpackLogEntries(les);

		log.info("got " + logEntries.size() + " to add/process.");

		for (LogEntry entry : logEntries) {
			try {
				if (log.isDebugEnabled())
					log.debug("Deserialized successfully, it is " + entry + "");
				model.getLog().add(entry);
			} catch (Throwable t) {
				log.debug("Failed to deserialize and/or save", t);
			}
		}
	}

	private JakeObject getJakeObjectForRequest(String filerequest)
			throws SQLException {
		if (!model.getProjectid().equals(
				this.messageMarshaller.getProjectUUIDFromRequestMessage(
						filerequest).toString())) {
			log.debug("got request for a different project");
			return null; // not our project
		}
		UUID leuuid = this.messageMarshaller
				.getLogEntryUUIDFromRequestMessage(filerequest);
		LogEntry le;
		try {
			le = model.getLog().getById(leuuid, false);
		} catch (NoSuchLogEntryException e) {
			log.debug("we don't know about this version");
			return null;
		}

		log.debug("got request for file belonging to entry " + leuuid);

		JakeObject fo = le.getWhat();

		LogEntry version;
		try {
			version = model.getLog().getLastOfJakeObject(fo, false);
		} catch (NoSuchLogEntryException e1) {
			log.debug("we don't have a version");
			return null;
		}
		if (!version.getId().equals(leuuid)) {
			log.debug("we have a different last version");
			return null;
		}
		Attributed status;
		try {
			status = AttributedCalculator.calculateAttributed(model.getFss(),
					model.getLog(), fo);
		} catch (Exception e) {
			log.debug("status of the requested object is weird", e);
			return null;
		}
		if (status.isModifiedLocally()) {
			log.debug("can't distribute tainted object");
			return null;
		}
		return fo;
	}

	@Override
	public boolean accept(FileRequest req) {
		try {
			log.info("incoming request: " + req);
			JakeObject fo = getJakeObjectForRequest(req.getFileName());
			if (fo == null) {
				// reason has already been logged.
				return false;
			}
			log.info("we accept the request");
			return true;
		} catch (Exception e) {
			log.warn("unexpected Exception", e);
			return false;
		}
	}

	@Override
	public void started(IFileTransfer t) {
		log.debug("we are transmitting." + t);
		new TransferWatcherThread(t, new ITransferListener() {

			@Override
			public void onFailure(AdditionalFileTransferData transfer,
					String error) {
				log.warn("transmitting failed: " + error);
			}

			@Override
			public void onSuccess(AdditionalFileTransferData transfer) {
				log.info("transmitting was successful");
			}

			@Override
			public void onUpdate(AdditionalFileTransferData transfer,
					Status status, double progress) {
				log.debug("transmitting update: " + progress + " - " + status);
			}

		});
	}

	private File getDeliveryDirectory() {
		String systmpdir = System.getProperty("java.io.tmpdir", "");
		if (!systmpdir.endsWith(File.separator))
			systmpdir = systmpdir + File.separator;

		File f = new File(systmpdir, "jakeDelivery");
		f.mkdir();
		return f;
	}

	@Override
	public File getFileForRequest(FileRequest req) {
		// this is a interesting function. watch this:
		try {
			log.info("incoming request: " + req);
			JakeObject fo = getJakeObjectForRequest(req.getFileName());
			if (fo == null) {
				// reason has already been logged.
				return null;
			}
			File origfile = new File(model.getFss()
					.getFullpath(fo.getRelPath()));
			log.info("original file at " + origfile);
			File tempfile = new File(getDeliveryDirectory(), req.getFileName());

			FSService.writeFileStreamAbs(tempfile.getAbsolutePath(),
					FSService.readFileStreamAbs(origfile.getAbsolutePath()));

			log.info("we accept the request and provided the file at "
					+ tempfile);

			return tempfile;
		} catch (Exception e) {
			log.warn("unexpected Exception", e);
			return null;
		}
	}
}