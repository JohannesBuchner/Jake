package com.jakeapp.core.domain;

import com.jakeapp.core.dao.IProjectDao;
import com.jakeapp.core.dao.INoteObjectDao;
import com.jakeapp.core.dao.IFileObjectDao;
import com.jakeapp.core.dao.exceptions.NoSuchProjectException;
import com.jakeapp.core.dao.exceptions.NoSuchJakeObjectException;
import com.jakeapp.core.synchronization.exceptions.InvalidDeserializerCallException;
import com.jakeapp.core.domain.exceptions.InvalidTagNameException;
import com.jakeapp.core.domain.logentries.*;
import com.jakeapp.core.util.ProjectApplicationContextFactory;

import java.util.UUID;
import java.util.Date;


public class LogEntrySerializer {

	private IProjectDao projectDao;
	private IFileObjectDao fileObjectDao;
	private INoteObjectDao noteObjectDao;
	private ProjectApplicationContextFactory applicationContextFactory;


	private final String SEPERATOR = "XXAAAXXX";
	private final String SEPERATOR_REGEX = "XXAAAXXX";

	public ProjectApplicationContextFactory getApplicationContextFactory() {
		return applicationContextFactory;
	}

	public void setApplicationContextFactory(ProjectApplicationContextFactory applicationContextFactory) {
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


	// FIXME: This is temporary. Remove it once someone tells me why we need a ProjectDAO
	//        if all we want to do is serialize...
	public LogEntrySerializer() {
	}

	public LogEntrySerializer(ProjectApplicationContextFactory applicationContextFactory)
	{
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
			sb.append(SEPERATOR).append(((FileObject) logEntry.getBelongsTo()).getRelPath());
		} else if (logEntry.getBelongsTo() instanceof NoteObject) {
			sb.append("N");
			sb.append(SEPERATOR).append(((NoteObject) logEntry.getBelongsTo()).getContent());
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

		sb.append(SEPERATOR).append(logEntry.getBelongsTo().getObject().getUuid().toString());
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

	public String serialize(StartTrustingProjectMemberLogEntry logEntry, Project project) {
		return this.serialize((ProjectMemberLogEntry) logEntry, project);
	}

	public String serialize(StopTrustingProjectMemberLogEntry logEntry, Project project) {
		return this.serialize((ProjectMemberLogEntry) logEntry, project);
	}

	public String serialize(FollowTrustingProjectMemberLogEntry logEntry, Project project) {
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
			case START_TRUSTING_PROJECTMEMBER:
				return serialize(StartTrustingProjectMemberLogEntry.parse(logEntry), project);
			case STOP_TRUSTING_PROJECTMEMBER:
				return serialize(StopTrustingProjectMemberLogEntry.parse(logEntry), project);
			case FOLLOW_TRUSTING_PROJECTMEMBER:
				return serialize(FollowTrustingProjectMemberLogEntry.parse(logEntry), project);
			case TAG_ADD:
				return serialize(TagAddLogEntry.parse(logEntry), project);
			case TAG_REMOVE:
				return serialize(TagRemoveLogEntry.parse(logEntry), project);
		}
		throw new UnsupportedOperationException();
	}


	public LogEntry<? extends ILogable> deserialize(String input) throws InvalidDeserializerCallException {
		String[] parts = input.split(SEPERATOR_REGEX);

		if (parts.length < 6)
			throw new InvalidDeserializerCallException();

		UUID projectUUID = UUID.fromString(parts[1]);
		Date date = new Date(new Long(parts[2]));
		LogAction logAction = LogAction.values()[new Integer(parts[3])];
		ProtocolType protocolType = ProtocolType.values()[new Integer(parts[4])];
		String userId = parts[5];

		UserId remoteUser = new UserId(protocolType, userId);
		UUID logEntryUUID = UUID.fromString(parts[6]);


		System.out.println("projectUUID = " + projectUUID);
		System.out.println("date = " + date);
		System.out.println("logAction = " + logAction);

		for (String part : parts) {
			System.out.println("part = " + part);
		}

		switch (logAction) {
			case JAKE_OBJECT_DELETE: {
				Project p = null;
				JakeObjectDeleteLogEntry result;

				try {
					p = getProjectDao().read(projectUUID); // throws NoSuchProjectException
				} catch (NoSuchProjectException e) {
					throw new InvalidDeserializerCallException();
				}

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


				result = new JakeObjectDeleteLogEntry(jakeObject, remoteUser, comment, false);


				/// always do this
				result.setTimestamp(date);
				result.setUuid(logEntryUUID);
				return result;
			}
			case JAKE_OBJECT_LOCK: {
				Project p = null;
				JakeObjectLockLogEntry result;

				try {
					p = getProjectDao().read(projectUUID); // throws NoSuchProjectException
				} catch (NoSuchProjectException e) {
					throw new InvalidDeserializerCallException();
				}

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


				/// always do this
				result.setTimestamp(date);
				result.setUuid(logEntryUUID);
				return result;
			}
			case JAKE_OBJECT_NEW_VERSION: {
				Project p = null;
				JakeObjectNewVersionLogEntry result;

				try {
					p = getProjectDao().read(projectUUID); // throws NoSuchProjectException
				} catch (NoSuchProjectException e) {
					throw new InvalidDeserializerCallException();
				}

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


				result = new JakeObjectNewVersionLogEntry(jakeObject, remoteUser, comment, checksum, false);


				/// always do this
				result.setTimestamp(date);
				result.setUuid(logEntryUUID);
				return result;

			}

			case JAKE_OBJECT_UNLOCK: {
				Project p = null;
				JakeObjectUnlockLogEntry result;

				try {
					p = getProjectDao().read(projectUUID); // throws NoSuchProjectException
				} catch (NoSuchProjectException e) {
					throw new InvalidDeserializerCallException();
				}

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


				result = new JakeObjectUnlockLogEntry(jakeObject, remoteUser, comment, false);


				/// always do this
				result.setTimestamp(date);
				result.setUuid(logEntryUUID);
				return result;
			}
			case PROJECT_CREATED: {
				ProjectCreatedLogEntry result;
				String projectName = parts[7];
				Project p = null;
				try {
					p = getProjectDao().read(projectUUID);
					// project already exists
				} catch (NoSuchProjectException e) {
					p = new Project(projectName, projectUUID, null, null);
					// project object created
				}

				result = new ProjectCreatedLogEntry(p, remoteUser);
				result.setProject(p);

				/// always do this
				result.setTimestamp(date);
				result.setUuid(logEntryUUID);
				return result;
			}
			case PROJECT_JOINED: {
				ProjectJoinedLogEntry result;
				String projectName = parts[7];
				Project p = null;
				try {
					p = getProjectDao().read(projectUUID);
					// project already exists
				} catch (NoSuchProjectException e) {
					p = new Project(projectName, projectUUID, null, null);
					// project object created
				}

				result = new ProjectJoinedLogEntry(p, remoteUser);
				result.setProject(p);

				/// always do this
				result.setTimestamp(date);
				result.setUuid(logEntryUUID);
				return result;
			}
			case FOLLOW_TRUSTING_PROJECTMEMBER: {
				FollowTrustingProjectMemberLogEntry result;
				Project p = null;
				try {
					p = getProjectDao().read(projectUUID);
				}
				catch (NoSuchProjectException e) {
					throw new InvalidDeserializerCallException();
				}
				ProtocolType protocolTypeOther = ProtocolType.values()[new Integer(parts[7])];
				String userIdOther = parts[8];

				UserId other = new UserId(protocolTypeOther, userIdOther);

				result = new FollowTrustingProjectMemberLogEntry(other, remoteUser);
				result.setTimestamp(date);
				result.setUuid(logEntryUUID);
				return result;

			}
			case START_TRUSTING_PROJECTMEMBER: {
				StartTrustingProjectMemberLogEntry result;
				Project p = null;
				try {
					p = getProjectDao().read(projectUUID);
				}
				catch (NoSuchProjectException e) {
					throw new InvalidDeserializerCallException();
				}
				ProtocolType protocolTypeOther = ProtocolType.values()[new Integer(parts[7])];
				String userIdOther = parts[8];

				UserId other = new UserId(protocolTypeOther, userIdOther);

				result = new StartTrustingProjectMemberLogEntry(other, remoteUser);
				result.setTimestamp(date);
				result.setUuid(logEntryUUID);
				return result;

			}
			case STOP_TRUSTING_PROJECTMEMBER: {
				StopTrustingProjectMemberLogEntry result;
				Project p = null;
				try {
					p = getProjectDao().read(projectUUID);
				}
				catch (NoSuchProjectException e) {
					throw new InvalidDeserializerCallException();
				}
				ProtocolType protocolTypeOther = ProtocolType.values()[new Integer(parts[7])];
				String userIdOther = parts[8];

				UserId other = new UserId(protocolTypeOther, userIdOther);

				result = new StopTrustingProjectMemberLogEntry(other, remoteUser);
				result.setTimestamp(date);
				result.setUuid(logEntryUUID);
				return result;
			}
			case TAG_ADD: {
				Project p = null;
				TagAddLogEntry result;

				try {
					p = getProjectDao().read(projectUUID); // throws NoSuchProjectException
				} catch (NoSuchProjectException e) {
					throw new InvalidDeserializerCallException();
				}


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
				}
				catch (InvalidTagNameException e) {
					throw new InvalidDeserializerCallException();
				}

				tag.setObject(jakeObject);

				result = new TagAddLogEntry(tag, remoteUser);


				/// always do this
				result.setTimestamp(date);
				result.setUuid(logEntryUUID);
				return result;


			}
			case TAG_REMOVE: {
				Project p = null;
				TagRemoveLogEntry result;

				try {
					p = getProjectDao().read(projectUUID); // throws NoSuchProjectException
				} catch (NoSuchProjectException e) {
					throw new InvalidDeserializerCallException();
				}


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
				}
				catch (InvalidTagNameException e) {
					throw new InvalidDeserializerCallException();
				}

				tag.setObject(jakeObject);

				result = new TagRemoveLogEntry(tag, remoteUser);


				/// always do this
				result.setTimestamp(date);
				result.setUuid(logEntryUUID);
				return result;


			}
		}


		throw new UnsupportedOperationException();
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
