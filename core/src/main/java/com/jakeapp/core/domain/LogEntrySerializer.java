package com.jakeapp.core.domain;

import com.jakeapp.core.dao.IFileObjectDao;
import com.jakeapp.core.dao.INoteObjectDao;
import com.jakeapp.core.dao.IProjectDao;
import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;
import com.jakeapp.core.dao.exceptions.NoSuchProjectException;
import com.jakeapp.core.domain.exceptions.InvalidTagNameException;
import com.jakeapp.core.domain.logentries.*;
import com.jakeapp.core.synchronization.exceptions.InvalidDeserializerCallException;
import com.jakeapp.core.util.ProjectApplicationContextFactory;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.UUID;


public class LogEntrySerializer {

	private static Logger log = Logger.getLogger(LogEntrySerializer.class);

	private IProjectDao projectDao;

	private IFileObjectDao fileObjectDao;

	private INoteObjectDao noteObjectDao;

	private ProjectApplicationContextFactory applicationContextFactory;


	private final String SEPERATOR = "XXAAAXXX";

	private final String SEPERATOR_REGEX = "XXAAAXXX";

	public ProjectApplicationContextFactory getApplicationContextFactory() {
		return applicationContextFactory;
	}

	public void setApplicationContextFactory(
					ProjectApplicationContextFactory applicationContextFactory) {
		this.applicationContextFactory = applicationContextFactory;
	}

	public void setFileObjectDao(IFileObjectDao fileObjectDao) {
		this.fileObjectDao = fileObjectDao;
	}

	public void setNoteObjectDao(INoteObjectDao noteObjectDao) {
		this.noteObjectDao = noteObjectDao;
	}

	public void setProjectDao(IProjectDao projectDao) {
		this.projectDao = projectDao;
	}


	// FIXME: This is temporary. Remove it once someone tells me why we need a
	// ProjectDAO
	// if all we want to do is serialize...
	public LogEntrySerializer() {
	}

	public LogEntrySerializer(
					ProjectApplicationContextFactory applicationContextFactory) {
		this.applicationContextFactory = applicationContextFactory;
	}


	public String serialize(JakeObjectNewVersionLogEntry logEntry, Project project) {
		return this.serialize((JakeObjectLogEntry) logEntry, project);
	}

	public String serialize(JakeObjectLockLogEntry logEntry, Project project) {
		return this.serialize((JakeObjectLogEntry) logEntry, project);
	}

	public String serialize(JakeObjectUnlockLogEntry logEntry, Project project) {
		return this.serialize((JakeObjectLogEntry) logEntry, project);
	}

	public String serialize(JakeObjectDeleteLogEntry logEntry, Project project) {
		return this.serialize((JakeObjectLogEntry) logEntry, project);
	}

	private String serialize(JakeObjectLogEntry logEntry, Project project) {

		StringBuffer sb = prepareSerializedString(logEntry, project);


		sb.append(SEPERATOR);
		if (logEntry.getBelongsTo() instanceof FileObject) {
			sb.append("F");
			sb.append(SEPERATOR)
							.append(((FileObject) logEntry.getBelongsTo()).getRelPath());
		} else if (logEntry.getBelongsTo() instanceof NoteObject) {
			sb.append("N");
			sb.append(SEPERATOR)
							.append(((NoteObject) logEntry.getBelongsTo()).getContent());
		} else
			throw new UnsupportedOperationException();


		sb.append(SEPERATOR).append(logEntry.getBelongsTo().getUuid().toString());

		sb.append(SEPERATOR).append(logEntry.getComment());
		sb.append(SEPERATOR).append(logEntry.getChecksum());

		sb.append(SEPERATOR);
		return sb.toString();
	}

	private StringBuffer prepareSerializedString(LogEntry logEntry, Project project) {
		StringBuffer sb = new StringBuffer(500);

		sb.append(SEPERATOR).append(project.getProjectId());
		sb.append(SEPERATOR).append(logEntry.getTimestamp().getTime());
		sb.append(SEPERATOR).append(logEntry.getLogAction().ordinal());
		sb.append(SEPERATOR).append(logEntry.getMember().getProtocolType().ordinal());
		sb.append(SEPERATOR).append(logEntry.getMember().getUserId());
		sb.append(SEPERATOR).append(logEntry.getUuid().toString());
		return sb;
	}


	public String serialize(TagAddLogEntry logEntry, Project project) {
		return this.serialize((TagLogEntry) logEntry, project);
	}

	public String serialize(TagRemoveLogEntry logEntry, Project project) {
		return this.serialize((TagLogEntry) logEntry, project);
	}

	private String serialize(TagLogEntry logEntry, Project project) {
		StringBuffer sb = prepareSerializedString(logEntry, project);

		sb.append(SEPERATOR);
		if (logEntry.getBelongsTo().getObject() instanceof FileObject) {
			sb.append("F");
		} else if (logEntry.getBelongsTo().getObject() instanceof NoteObject) {
			sb.append("N");
		} else
			throw new UnsupportedOperationException();

		sb.append(SEPERATOR).append(logEntry.getBelongsTo().getName());

		sb.append(SEPERATOR)
						.append(logEntry.getBelongsTo().getObject().getUuid().toString());
		sb.append(SEPERATOR);
		return sb.toString();
	}

	public String serialize(ProjectLogEntry logEntry) {

		Project project = logEntry.getProject();

		StringBuffer sb = new StringBuffer(500);

		sb.append(SEPERATOR).append(project.getProjectId());
		sb.append(SEPERATOR).append(logEntry.getTimestamp().getTime());
		sb.append(SEPERATOR).append(logEntry.getLogAction().ordinal());
		sb.append(SEPERATOR).append(logEntry.getMember().getProtocolType().ordinal());
		sb.append(SEPERATOR).append(logEntry.getMember().getUserId());
		sb.append(SEPERATOR).append(logEntry.getUuid().toString());

		sb.append(SEPERATOR).append(project.getName());

		sb.append(SEPERATOR);

		return sb.toString();
	}

	public String serialize(ProjectCreatedLogEntry logEntry) {
		return this.serialize((ProjectLogEntry) logEntry);
	}

	public String serialize(ProjectJoinedLogEntry logEntry) {
		return this.serialize((ProjectLogEntry) logEntry);
	}

	public String serialize(StartTrustingProjectMemberLogEntry logEntry,
					Project project) {
		return this.serialize((ProjectMemberLogEntry) logEntry, project);
	}

	public String serialize(StopTrustingProjectMemberLogEntry logEntry,
					Project project) {
		return this.serialize((ProjectMemberLogEntry) logEntry, project);
	}

	public String serialize(FollowTrustingProjectMemberLogEntry logEntry,
					Project project) {
		return this.serialize((ProjectMemberLogEntry) logEntry, project);
	}

	public String serialize(ProjectMemberInvitedLogEntry logEntry, Project project) {
		return this.serialize((ProjectMemberLogEntry) logEntry, project);
	}


	private String serialize(ProjectMemberLogEntry logEntry, Project project) {
		StringBuffer sb = prepareSerializedString(logEntry, project);
		sb.append(SEPERATOR).append(logEntry.getBelongsTo().getProtocolType().ordinal());
		sb.append(SEPERATOR).append(logEntry.getBelongsTo().getUserId());
		sb.append(SEPERATOR);
		return sb.toString();
	}

	public String serialize(LogEntry<? extends ILogable> logEntry, Project project) {
		LogAction action = logEntry.getLogAction();
		switch (action) {
			case JAKE_OBJECT_DELETE:
				return serialize(JakeObjectDeleteLogEntry.parse(logEntry), project);
			case JAKE_OBJECT_LOCK:
				return serialize(JakeObjectLockLogEntry.parse(logEntry), project);
			case JAKE_OBJECT_NEW_VERSION:
				return serialize(JakeObjectNewVersionLogEntry.parse(logEntry), project);
			case JAKE_OBJECT_UNLOCK:
				return serialize(JakeObjectUnlockLogEntry.parse(logEntry), project);
			case PROJECT_JOINED:
				return serialize(ProjectJoinedLogEntry.parse(logEntry));
			case PROJECT_CREATED:
				return serialize(ProjectCreatedLogEntry.parse(logEntry));
			case PROJECTMEMBER_INVITED:
				return serialize(ProjectMemberInvitedLogEntry.parse(logEntry), project);
			case PROJECT_REJECTED:
				return serialize(ProjectMemberInvitationRejectedLogEntry.parse(logEntry),
								project);
			case START_TRUSTING_PROJECTMEMBER:
				return serialize(StartTrustingProjectMemberLogEntry.parse(logEntry),
								project);
			case STOP_TRUSTING_PROJECTMEMBER:
				return serialize(StopTrustingProjectMemberLogEntry.parse(logEntry), project);
			case FOLLOW_TRUSTING_PROJECTMEMBER:
				return serialize(FollowTrustingProjectMemberLogEntry.parse(logEntry),
								project);
			case TAG_ADD:
				return serialize(TagAddLogEntry.parse(logEntry), project);
			case TAG_REMOVE:
				return serialize(TagRemoveLogEntry.parse(logEntry), project);
		}
		throw new UnsupportedOperationException();
	}


	/**
	 * Deserialize Component.
	 *
	 * @param input
	 * @return
	 * @throws InvalidDeserializerCallException
	 *
	 */
	// fixme: oh please, i wanna be cleaned up so badly!
	public LogEntry<? extends ILogable> deserialize(String input)
					throws InvalidDeserializerCallException {
		LogEntry result;

		// get message parts
		String[] parts = input.split(SEPERATOR_REGEX);

		for (String part : parts) {
			log.trace("part = " + part);
		}

		// stop on error
		if (parts.length < 6)
			throw new InvalidDeserializerCallException(
							"Invalid format: need at least 6 parts.");

		// first parts of message are equal
		UUID projectUUID = UUID.fromString(parts[1]);
		Date date = new Date(new Long(parts[2]));
		LogAction logAction = LogAction.values()[new Integer(parts[3])];
		ProtocolType protocolType = ProtocolType.values()[new Integer(parts[4])];
		String userId = parts[5];
		UUID logEntryUUID = UUID.fromString(parts[6]);

		User remoteUser = new User(protocolType, userId);

		log.trace("projectUUID = " + projectUUID);
		log.trace("date = " + date);
		log.trace("logAction = " + logAction);

		// check that project exists locally
		Project p;
		try {
			p = getProjectDao().read(projectUUID);
		} catch (NoSuchProjectException e) {

			// we have to process two log entries special and create logEntries for them...
			if (logAction == LogAction.PROJECT_CREATED || logAction == LogAction.PROJECT_JOINED) {
				String projectName = parts[7];
				p = new Project(projectName, projectUUID, null, null);
			} else {
				throw new InvalidDeserializerCallException(
								"Project does not exist locally: " + projectUUID);
			}
		}

		switch (logAction) {
			case JAKE_OBJECT_DELETE: {
				String type = parts[7];

				JakeObject jakeObject;
				if (type.equals("F")) {
					String relPath = parts[8];
					jakeObject = new FileObject(p, relPath);
				} else if (type.equals("N")) {
					String content = parts[8];
					jakeObject = new NoteObject(p, content);
				} else
					throw new InvalidDeserializerCallException();

				jakeObject.setUuid(UUID.fromString(parts[9]));

				String comment = parts[10];
				String checksum = parts[11];

				result =
								new JakeObjectDeleteLogEntry(jakeObject, remoteUser, comment, false);
			}break;
			
			case JAKE_OBJECT_LOCK: {
				String type = parts[7];

				JakeObject jakeObject;
				if (type.equals("F")) {
					String relPath = parts[8];
					jakeObject = new FileObject(p, relPath);
				} else if (type.equals("N")) {
					String content = parts[8];
					jakeObject = new NoteObject(p, content);
				} else
					throw new InvalidDeserializerCallException();

				jakeObject.setUuid(UUID.fromString(parts[9]));

				String comment = parts[10];
				String checksum = parts[11];

				result = new JakeObjectLockLogEntry(jakeObject, remoteUser, comment, false);
			}break;

			case JAKE_OBJECT_NEW_VERSION: {
				String type = parts[7];

				JakeObject jakeObject;
				if (type.equals("F")) {
					String relPath = parts[8];
					jakeObject = new FileObject(p, relPath);
				} else if (type.equals("N")) {
					String content = parts[8];
					jakeObject = new NoteObject(p, content);
				} else
					throw new InvalidDeserializerCallException();

				jakeObject.setUuid(UUID.fromString(parts[9]));

				String comment = parts[10];
				String checksum = parts[11];

				result = new JakeObjectNewVersionLogEntry(jakeObject, remoteUser, comment,
								checksum, false);
			}break;

			case JAKE_OBJECT_UNLOCK: {
				String type = parts[7];

				JakeObject jakeObject;
				if (type.equals("F")) {
					String relPath = parts[8];
					jakeObject = new FileObject(p, relPath);
				} else if (type.equals("N")) {
					String content = parts[8];
					jakeObject = new NoteObject(p, content);
				} else
					throw new InvalidDeserializerCallException();

				jakeObject.setUuid(UUID.fromString(parts[9]));

				String comment = parts[10];
				String checksum = parts[11];

				result =
								new JakeObjectUnlockLogEntry(jakeObject, remoteUser, comment, false);
			}break;

			case PROJECT_CREATED: {
				ProjectCreatedLogEntry res;
				res = new ProjectCreatedLogEntry(p, remoteUser);
				res.setProject(p);
				result = res;
			}break;

			case PROJECT_JOINED: {
				ProjectJoinedLogEntry res;

				res = new ProjectJoinedLogEntry(p, remoteUser);
				res.setProject(p);
				result = res;
			}
			break;

			case FOLLOW_TRUSTING_PROJECTMEMBER: {
				ProtocolType protocolTypeOther =
								ProtocolType.values()[new Integer(parts[7])];
				String userIdOther = parts[8];

				User other = new User(protocolTypeOther, userIdOther);

				result = new FollowTrustingProjectMemberLogEntry(other, remoteUser);
			}
			break;

			case START_TRUSTING_PROJECTMEMBER: {
				ProtocolType protocolTypeOther =
								ProtocolType.values()[new Integer(parts[7])];
				String userIdOther = parts[8];

				User other = new User(protocolTypeOther, userIdOther);

				result = new StartTrustingProjectMemberLogEntry(other, remoteUser);
			}
			break;

			case STOP_TRUSTING_PROJECTMEMBER: {
				ProtocolType protocolTypeOther =
								ProtocolType.values()[new Integer(parts[7])];
				String userIdOther = parts[8];

				User other = new User(protocolTypeOther, userIdOther);

				result = new StopTrustingProjectMemberLogEntry(other, remoteUser);
			}
			break;

			case PROJECTMEMBER_INVITED: {
				ProtocolType protocolTypeOther =
								ProtocolType.values()[new Integer(parts[7])];
				String userIdOther = parts[8];

				User other = new User(protocolTypeOther, userIdOther);

				result = new ProjectMemberInvitedLogEntry(other, remoteUser);
			}break;

			case PROJECT_REJECTED: {
				ProtocolType protocolTypeOther =
								ProtocolType.values()[new Integer(parts[7])];
				String userIdOther = parts[8];

				User other = new User(protocolTypeOther, userIdOther);

				result = new ProjectMemberInvitationRejectedLogEntry(other, remoteUser);
			}break;

			case TAG_ADD: {
				String type = parts[7];
				JakeObject jakeObject;
				Tag tag = null;
				try {
					if (type.equals("F")) {
						jakeObject = getFileObjectDao(p).get(UUID.fromString(parts[9]));
					} else if (type.equals("N")) {
						jakeObject = getNoteObjectDao(p).get(UUID.fromString(parts[9]));
					} else
						throw new InvalidDeserializerCallException();

					tag = new Tag(parts[8]);
				} catch (NoSuchJakeObjectException e) {
					throw new InvalidDeserializerCallException();
				} catch (InvalidTagNameException e) {
					throw new InvalidDeserializerCallException();
				}

				tag.setObject(jakeObject);

				result = new TagAddLogEntry(tag, remoteUser);
			}
			break;

			case TAG_REMOVE: {
				String type = parts[7];
				JakeObject jakeObject;
				Tag tag;
				try {
					if (type.equals("F")) {
						jakeObject = getFileObjectDao(p).get(UUID.fromString(parts[9]));
					} else if (type.equals("N")) {
						jakeObject = getNoteObjectDao(p).get(UUID.fromString(parts[9]));
					} else
						throw new InvalidDeserializerCallException();

					tag = new Tag(parts[8]);
				} catch (NoSuchJakeObjectException e) {
					throw new InvalidDeserializerCallException();
				} catch (InvalidTagNameException e) {
					throw new InvalidDeserializerCallException();
				}

				tag.setObject(jakeObject);

				result = new TagRemoveLogEntry(tag, remoteUser);
			}
			break;

			default: {
				throw new UnsupportedOperationException("Unknown logAction: " + logAction);
			}
		}

		// / always do this
		result.setTimestamp(date);
		result.setUuid(logEntryUUID);
		return result;
	}

	private IProjectDao getProjectDao() {
		return projectDao;
	}

	private IFileObjectDao getFileObjectDao(Project project) {
		return applicationContextFactory.getFileObjectDao(project);
	}

	private INoteObjectDao getNoteObjectDao(Project project) {
		return applicationContextFactory.getNoteObjectDao(project);
	}
}
