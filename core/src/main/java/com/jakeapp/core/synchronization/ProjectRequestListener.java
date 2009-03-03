package com.jakeapp.core.synchronization;

import com.jakeapp.jake.ics.msgservice.IMessageReceiveListener;
import com.jakeapp.jake.ics.status.IOnlineStatusListener;
import com.jakeapp.jake.ics.status.ILoginStateListener;
import com.jakeapp.jake.ics.filetransfer.negotiate.FileRequest;
import com.jakeapp.jake.ics.exceptions.NotLoggedInException;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import com.jakeapp.jake.ics.exceptions.OtherUserOfflineException;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.core.domain.*;
import com.jakeapp.core.domain.logentries.LogEntry;
import com.jakeapp.core.domain.exceptions.IllegalProtocolException;
import com.jakeapp.core.services.ICSManager;
import com.jakeapp.core.util.ProjectApplicationContextFactory;
import com.jakeapp.core.synchronization.helpers.MessageMarshaller;
import org.springframework.transaction.annotation.Transactional;
import org.apache.log4j.Logger;

import java.util.UUID;
import java.util.List;

public class ProjectRequestListener implements IMessageReceiveListener,
		IOnlineStatusListener, ILoginStateListener {

	private static final String BEGIN_LOGENTRY = "<le>";
	private static final String END_LOGENTRY = "</le>";
	private static final String BEGIN_PROJECT_UUID = "<project>";
	private static final String END_PROJECT_UUID = "</project>";
	private static final String LOGENTRIES_MESSAGE = "<logentries/>";
	private static final String REQUEST_LOGS_MESSAGE = "<requestlogs/>";
	private static final String NEW_FILE = "<newfile/>";
	private static final String POKE_MESSAGE = "<poke/>"; // dup


	private static final String NEW_NOTE = "<newnote/>";
	private Project p;

	private ICSManager ICSManager;
	private ProjectApplicationContextFactory db;

	private LogEntrySerializer logEntrySerializer;

	private IInternalSyncService syncService;

	private MessageMarshaller messageMarshaller;

	private static Logger log = Logger.getLogger(ProjectRequestListener.class);


	public ProjectRequestListener(
			Project p,
			ICSManager icsManager,
			ProjectApplicationContextFactory db,
			LogEntrySerializer logEntrySerializer,
			IInternalSyncService syncService,
			MessageMarshaller messageMarshaller) {
		this.p = p;
		this.ICSManager = icsManager;
		this.db = db;
		this.logEntrySerializer = logEntrySerializer;
		this.syncService = syncService;
		this.messageMarshaller = messageMarshaller;
	}


	public ICSManager getICSManager() {
		return ICSManager;
	}




	private void sendLogs(Project project, com.jakeapp.jake.ics.UserId user) {
		ICService ics = ICSManager.getICService(project);

		try {
			List<LogEntry<? extends ILogable>> logs = db.getLogEntryDao(project).getAll();
			String message = messageMarshaller.packLogEntries(project, logs);
			ics.getMsgService().sendMessage(user, message);
		} catch (NetworkException e) {
			log.warn("Could not sync logs", e);
		} catch (OtherUserOfflineException e) {
			log.warn("Could not sync logs", e);
		}
	}


	private String getProjectUUID(String content) {
		int begin = content.indexOf(BEGIN_PROJECT_UUID) + BEGIN_PROJECT_UUID.length();
		int end = content.indexOf(END_PROJECT_UUID);

		return content.substring(begin, end);
	}

	@Override
	@Transactional
	public void receivedMessage(com.jakeapp.jake.ics.UserId from_userid, String content) {


		String projectUUID = getProjectUUID(content);
		log.debug("Received a message for project " + projectUUID);

		if (!projectUUID.equals(p.getProjectId())) {
			log.debug("Discarding message because it's not for this project");
			return;
		}

		log.debug("Message is for this project!");

		String message = content.substring(BEGIN_PROJECT_UUID.length() + projectUUID.length() + END_PROJECT_UUID.length());
		log.debug("Message content: \"" + message + "\"");

		if (message.startsWith(POKE_MESSAGE)) {
			log.info("Received poke from " + from_userid.getUserId());
			log.debug("This means we should sync logs!");

			// Eventually, this should consider things such as trust
			UserId user = getICSManager().getFrontendUserId(p, from_userid);
			try {
				syncService.startLogSync(p, user);
			} catch (IllegalProtocolException e) {
				// This should neeeeeeeeever happen
				log.fatal("Received an unexpected IllegalProtocolException while trying to perform logsync",
						e);
			}
			return;
		}

		if (message.startsWith(REQUEST_LOGS_MESSAGE)) {
			log.info("Received logs request from " + from_userid.getUserId());

			sendLogs(p, from_userid);
			return;
		}

		if (message.startsWith(LOGENTRIES_MESSAGE)) {
			log.info("Received serialized logentries from " + from_userid.getUserId());

			String les = message.substring(LOGENTRIES_MESSAGE.length() + BEGIN_LOGENTRY.length(), message.length() - END_LOGENTRY.length());

			List<LogEntry> logEntries = messageMarshaller.unpackLogEntries(les);

			for(LogEntry entry : logEntries)
			{
				try
				{
					log.debug("Deserialized successfully, it is a " + entry.getLogAction() + " for object UUID " + entry.getObjectuuid());
					db.getLogEntryDao(p).create(entry);
				}
				catch(Throwable t)
				{
					log.debug("Failed to deserialize and/or save", t);
				}
			}
		}

		// TODO: The stuff below here could use some refactoring
		// (e.g. redeclaring parameter content)
		int uuidlen = UUID.randomUUID().toString().length();
		String projectid = message.substring(0, uuidlen);
		message = message.substring(uuidlen);
		Project p = syncService.getProjectById(projectid);
		ChangeListener cl = syncService.getProjectChangeListener(projectid);
		if (message.startsWith(NEW_NOTE)) {
			log.debug("requesting note");
			UUID uuid = UUID.fromString(message.substring(NEW_NOTE.length()));
			log.debug("persisting object");
			NoteObject no = new NoteObject(uuid, p, "loading ...");
			db.getNoteObjectDao(p).persist(no);
			log.debug("calling other user: " + from_userid);
			try {
				p.getMessageService().getIcsManager().getTransferService(p).request(
						new FileRequest("N" + uuid, false, from_userid),
						cl.beganRequest(no));
			} catch (NotLoggedInException e) {
				log.error("Not logged in");
			}
		}

		if (message.startsWith(NEW_FILE)) {
			log.debug("requesting file");
			String relpath = message.substring(NEW_FILE.length());
			FileObject fo = new FileObject(p, relpath);
			log.debug("persisting object");
			db.getFileObjectDao(p).persist(fo);
			log.debug("calling other user: " + from_userid);
			try {
				p.getMessageService().getIcsManager().getTransferService(p).request(
						new FileRequest("F" + relpath, false, from_userid),
						cl.beganRequest(fo));
			} catch (NotLoggedInException e) {
				log.error("Not logged in");
			}
		}
	}

	@Override
	public void onlineStatusChanged(com.jakeapp.jake.ics.UserId userid) {
		// TODO Auto-generated method stub
		log.info("Online status of " + userid.getUserId() + " changed... (Project "
				+ p + ")");
	}

	@Override
	public void loginHappened() {
		// TODO Auto-generated method stub
		log.info("We logged in with project " + this.p);
	}

	@Override
	public void logoutHappened() {
		// TODO Auto-generated method stub
		log.info("We logged out with project " + this.p);
	}
}