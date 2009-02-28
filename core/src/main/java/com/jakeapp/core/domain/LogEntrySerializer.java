package com.jakeapp.core.domain;

import com.jakeapp.core.dao.IProjectDao;
import com.jakeapp.core.dao.exceptions.NoSuchProjectException;
import com.jakeapp.core.synchronization.exceptions.InvalidDeserializerCallException;

import java.util.UUID;
import java.util.Date;


public class LogEntrySerializer {

    IProjectDao projectDao;


    private final String SEPERATOR = "XXAAAXXX"; //  SEPERATOR LOOKS LIKE |\/|
    private final String SEPERATOR_REGEX = "XXAAAXXX";

    public LogEntrySerializer(IProjectDao projectDao) {
        this.projectDao = projectDao;
    }

	// FIXME: This is temporary. Remove it once someone tells me why we need a ProjectDAO
	//        if all we want to do is serialize...
	public LogEntrySerializer() {}

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


    public String serialize(JakeObjectNewVersionLogEntry logEntry, Project project) {


        StringBuffer sb = new StringBuffer(500);

        sb.append(SEPERATOR).append(project.getProjectId());
        sb.append(SEPERATOR).append(logEntry.getTimestamp().getTime());
        sb.append(SEPERATOR).append(logEntry.getLogAction().ordinal());
        sb.append(SEPERATOR).append(logEntry.getMember().getProtocolType().ordinal());
        sb.append(SEPERATOR).append(logEntry.getMember().getUserId());
        sb.append(SEPERATOR).append(logEntry.getUuid().toString());

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

//        sb.append(SEPERATOR).append(project.getName());


        sb.append(SEPERATOR);
        return sb.toString();
    }

    public String serialize(JakeObjectLockLogEntry logEntry, Project project) {


        StringBuffer sb = new StringBuffer(500);

        sb.append(SEPERATOR).append(project.getProjectId());
        sb.append(SEPERATOR).append(logEntry.getTimestamp().getTime());
        sb.append(SEPERATOR).append(logEntry.getLogAction().ordinal());
        sb.append(SEPERATOR).append(logEntry.getMember().getProtocolType().ordinal());
        sb.append(SEPERATOR).append(logEntry.getMember().getUserId());
        sb.append(SEPERATOR).append(logEntry.getUuid().toString());


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

    public String serialize(JakeObjectUnlockLogEntry logEntry, Project project) {

        StringBuffer sb = new StringBuffer(500);

        sb.append(SEPERATOR).append(project.getProjectId());
        sb.append(SEPERATOR).append(logEntry.getTimestamp().getTime());
        sb.append(SEPERATOR).append(logEntry.getLogAction().ordinal());
        sb.append(SEPERATOR).append(logEntry.getMember().getProtocolType().ordinal());
        sb.append(SEPERATOR).append(logEntry.getMember().getUserId());
        sb.append(SEPERATOR).append(logEntry.getUuid().toString());


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

    public String serialize(JakeObjectDeleteLogEntry logEntry, Project project) {

        StringBuffer sb = new StringBuffer(500);

        sb.append(SEPERATOR).append(project.getProjectId());
        sb.append(SEPERATOR).append(logEntry.getTimestamp().getTime());
        sb.append(SEPERATOR).append(logEntry.getLogAction().ordinal());
        sb.append(SEPERATOR).append(logEntry.getMember().getProtocolType().ordinal());
        sb.append(SEPERATOR).append(logEntry.getMember().getUserId());
        sb.append(SEPERATOR).append(logEntry.getUuid().toString());


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

    public String serialize(JakeObjectLogEntry logEntry, Project project) {


        StringBuffer sb = new StringBuffer(500);

        sb.append(SEPERATOR).append(project.getProjectId());
        sb.append(SEPERATOR).append(logEntry.getTimestamp().getTime());
        sb.append(SEPERATOR).append(logEntry.getLogAction().ordinal());
        sb.append(SEPERATOR).append(logEntry.getMember().getProtocolType().ordinal());
        sb.append(SEPERATOR).append(logEntry.getMember().getUserId());
        sb.append(SEPERATOR).append(logEntry.getUuid().toString());

        if (logEntry.getLogAction().equals(LogAction.JAKE_OBJECT_NEW_VERSION)) {
            // TODO REFACTOR THIS!
            sb.append(SEPERATOR);
            if (logEntry.getBelongsTo() instanceof FileObject) {
                sb.append("F");
                sb.append(SEPERATOR).append(((FileObject) logEntry.getBelongsTo()).getRelPath());
            } else if (logEntry.getBelongsTo() instanceof NoteObject) {
                sb.append("N");
                sb.append(SEPERATOR).append(((NoteObject) logEntry.getBelongsTo()).getContent());
            } else
                throw new UnsupportedOperationException();
        } else if (logEntry.getLogAction().equals(LogAction.JAKE_OBJECT_DELETE) || logEntry.getLogAction().equals(LogAction.JAKE_OBJECT_LOCK)
                || logEntry.getLogAction().equals(LogAction.JAKE_OBJECT_UNLOCK)) {


        } else
            throw new InvalidDeserializerCallException();


        sb.append(SEPERATOR).append(logEntry.getBelongsTo().getUuid().toString());

        sb.append(SEPERATOR).append(logEntry.getComment());
        sb.append(SEPERATOR).append(logEntry.getChecksum());

//        sb.append(SEPERATOR).append(project.getName());


        sb.append(SEPERATOR);
        return sb.toString();
    }


    public String serialize(LogEntry<? extends ILogable> logEntry, Project project) {
	    Object o = logEntry.getBelongsTo();
	    if(o instanceof JakeObjectLogEntry) {
		    // FIXME: UGLY UGLY UGLY FUCKING UGLY
		    return this.serialize((JakeObjectLogEntry)o, project);
	    }

        throw new UnsupportedOperationException();
//        return "";
    }


    public LogEntry<? extends ILogable> deserialize(String input) throws NoSuchProjectException {
        String[] parts = input.split(SEPERATOR_REGEX);

        if (parts.length < 4)
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

                p = projectDao.read(projectUUID); // throws NoSuchProjectException

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


                result = new JakeObjectDeleteLogEntry(jakeObject, remoteUser, comment, checksum, false);


                /// always do this
                result.setTimestamp(date);
                result.setUuid(logEntryUUID);
                return result;
            }
            case JAKE_OBJECT_LOCK: {
                Project p = null;
                JakeObjectLockLogEntry result;

                p = projectDao.read(projectUUID); // throws NoSuchProjectException

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


                result = new JakeObjectLockLogEntry(jakeObject, remoteUser, comment, checksum, false);


                /// always do this
                result.setTimestamp(date);
                result.setUuid(logEntryUUID);
                return result;
            }
            case JAKE_OBJECT_NEW_VERSION: {
                Project p = null;
                JakeObjectNewVersionLogEntry result;

                p = projectDao.read(projectUUID); // throws NoSuchProjectException

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

                p = projectDao.read(projectUUID); // throws NoSuchProjectException

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


                result = new JakeObjectUnlockLogEntry(jakeObject, remoteUser, comment, checksum, false);


                /// always do this
                result.setTimestamp(date);
                result.setUuid(logEntryUUID);
                return result;
            }
            case PROJECT_CREATED: {
                ProjectLogEntry result;
                String projectName = parts[7];
                Project p = null;
                try {
                    p = projectDao.read(projectUUID);
                } catch (NoSuchProjectException e) {
                    p = new Project(projectName, projectUUID, null, null);
                }

                result = new ProjectLogEntry(p, remoteUser);


                /// always do this
                result.setTimestamp(date);
                result.setUuid(logEntryUUID);
                return result;
            }
            case NOOP:
                break;
            case FOLLOW_TRUSTING_PROJECTMEMBER:
                break;
            case START_TRUSTING_PROJECTMEMBER:
                break;
            case STOP_TRUSTING_PROJECTMEMBER:
                break;
            case TAG_ADD:
                break;
            case TAG_REMOVE:
                break;
        }


        throw new UnsupportedOperationException();
//        return null;
    }

}
